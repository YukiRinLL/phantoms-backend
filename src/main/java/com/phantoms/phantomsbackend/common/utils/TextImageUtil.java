package com.phantoms.phantomsbackend.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文本图片渲染公共工具：CJK 字体降级加载、按像素宽度换行、文本渲染为图片、图片转 NapCat base64。
 * 适用于 Docker（Alpine + Noto CJK）与 Windows 本地开发环境。
 */
public final class TextImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(TextImageUtil.class);

    /**
     * CJK 字体加载优先级（覆盖 Alpine 容器、常见 Linux 及 Windows）
     */
    private static final String[] CJK_FONT_PRIORITIES = {
        "Noto Sans CJK JP",
        "Noto Sans CJK SC",
        "WenQuanYi Micro Hei",
        "Droid Sans Fallback",
        "Microsoft YaHei",
        "Yu Gothic",
        "MS Gothic",
        Font.SANS_SERIF
    };

    private TextImageUtil() {
    }

    /**
     * 加载 CJK 字体，按优先级自动降级
     *
     * @param preferredFontName 首选字体名，可为 null
     * @param style             字体样式，如 {@link Font#PLAIN}、{@link Font#BOLD}
     * @param size              字号
     */
    public static Font loadCjkFont(String preferredFontName, int style, int size) {
        List<String> priorities = new ArrayList<>();
        if (preferredFontName != null && !preferredFontName.isEmpty()) {
            priorities.add(preferredFontName);
        }
        for (String name : CJK_FONT_PRIORITIES) {
            if (!priorities.contains(name)) {
                priorities.add(name);
            }
        }

        for (String fontName : priorities) {
            try {
                Font font = new Font(fontName, style, size);
                if (font.getFontName() != null && !font.getFontName().equals("Dialog")) {
                    return font;
                }
            } catch (Exception e) {
                logger.debug("加载字体 {} 失败: {}", fontName, e.getMessage());
            }
        }

        logger.warn("所有字体加载失败，使用系统默认字体");
        return new Font(Font.SANS_SERIF, style, size);
    }

    /**
     * 文本/图片抗锯齿与高质量渲染提示
     */
    public static void applyQualityHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /**
     * 按像素宽度逐字符换行（适配中文/日文等无空格文本，保留显式换行符）
     */
    public static List<String> wrapByPixelWidth(FontMetrics metrics, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        for (String paragraph : text.split("\n", -1)) {
            if (paragraph.isEmpty()) {
                lines.add("");
                continue;
            }
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < paragraph.length(); i++) {
                char c = paragraph.charAt(i);
                if (line.length() > 0 && metrics.stringWidth(line.toString() + c) > maxWidth) {
                    lines.add(line.toString());
                    line.setLength(0);
                }
                line.append(c);
            }
            if (line.length() > 0) {
                lines.add(line.toString());
            }
        }
        return lines;
    }

    /**
     * 将 BufferedImage 编码为 PNG 并转成 NapCat 可直接发送的 base64 字符串（base64:// 前缀）
     */
    public static String toBase64Png(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        return "base64://" + java.util.Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 将纯文本渲染为白底文字图片（长度不限，高度随行数自适应），返回 NapCat base64 字符串。
     *
     * @param text    待渲染文本，为空返回 null
     * @param options 渲染参数，null 时使用默认值
     */
    public static String renderTextToBase64Png(String text, TextImageOptions options) throws IOException {
        if (text == null || text.isEmpty()) {
            return null;
        }
        TextImageOptions opt = options != null ? options : TextImageOptions.defaults();

        Font font = loadCjkFont(opt.preferredFontName, opt.fontStyle, opt.fontSize);
        int contentWidth = opt.width - opt.padding * 2;
        int lineHeight = opt.lineHeight != null ? opt.lineHeight : Math.round(opt.fontSize * 1.6f);

        // 临时画布用于文本测量与换行
        BufferedImage tmpImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D tmpG = tmpImage.createGraphics();
        applyQualityHints(tmpG);
        tmpG.setFont(font);
        List<String> lines = wrapByPixelWidth(tmpG.getFontMetrics(), text, contentWidth);
        tmpG.dispose();

        if (lines.isEmpty()) {
            return null;
        }

        int imageHeight = opt.padding * 2 + lines.size() * lineHeight;
        BufferedImage image = new BufferedImage(opt.width, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        applyQualityHints(g);

        g.setColor(opt.backgroundColor);
        g.fillRect(0, 0, opt.width, imageHeight);

        g.setFont(font);
        g.setColor(opt.textColor);
        FontMetrics metrics = g.getFontMetrics();
        int y = opt.padding;
        // 每行基线按行盒垂直居中计算
        for (String line : lines) {
            int baseline = y + (lineHeight + metrics.getAscent() - metrics.getDescent()) / 2;
            g.drawString(line, opt.padding, baseline);
            y += lineHeight;
        }

        g.dispose();
        return toBase64Png(image);
    }

    /**
     * 文本图片渲染参数
     */
    public static class TextImageOptions {
        /** 图片宽度（像素） */
        public int width = 720;
        /** 内容四周内边距 */
        public int padding = 40;
        /** 首选字体名（仍会自动降级） */
        public String preferredFontName = "Noto Sans CJK JP";
        /** 字体样式 */
        public int fontStyle = Font.PLAIN;
        /** 字号 */
        public int fontSize = 21;
        /** 行高，null 时按字号 1.6 倍计算 */
        public Integer lineHeight = null;
        /** 文字颜色 */
        public Color textColor = new Color(0x33, 0x33, 0x33);
        /** 背景颜色 */
        public Color backgroundColor = Color.WHITE;

        public static TextImageOptions defaults() {
            return new TextImageOptions();
        }

        public TextImageOptions width(int width) {
            this.width = width;
            return this;
        }

        public TextImageOptions padding(int padding) {
            this.padding = padding;
            return this;
        }

        public TextImageOptions preferredFontName(String preferredFontName) {
            this.preferredFontName = preferredFontName;
            return this;
        }

        public TextImageOptions fontStyle(int fontStyle) {
            this.fontStyle = fontStyle;
            return this;
        }

        public TextImageOptions fontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public TextImageOptions lineHeight(Integer lineHeight) {
            this.lineHeight = lineHeight;
            return this;
        }

        public TextImageOptions textColor(Color textColor) {
            this.textColor = textColor;
            return this;
        }

        public TextImageOptions backgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }
    }
}
