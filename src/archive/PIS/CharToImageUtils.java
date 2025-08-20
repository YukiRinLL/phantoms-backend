package com.phantoms.phantomsbackend.common.utils.PIS;


import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 阡陌兮
 * @version 1.0.0
 * @date 2024-01-15 11:01
 * @description
 */
@Slf4j
public class CharToImageUtils {
    /**
     * 生成图片
     *
     * @param content  内容
     * @param width    宽度
     * @param height   高度
     * @param fontSize 字体大小
     * @return 图片
     */
    public static BufferedImage buildImage(String content, int width, int height, int fontSize) {
        // 1.创建空白图片
        BufferedImage image = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);

        // 2.获取图片画笔
        Graphics2D graphic = image.createGraphics();
        // 3.设置背景颜色为白色
        graphic.setColor(new Color(255, 255, 255, 0));
        // 4.绘制背景填充
        graphic.fillRect(0, 0, width, height);
        // 6.设置字体和文字大小
        graphic.setFont(getFont());
        // 7.设置字体颜色
        graphic.setColor(Color.BLACK);
        // 8.获取字体的宽度和高度
        int textWidth = graphic.getFontMetrics().stringWidth(content);
        int textHeight = graphic.getFontMetrics().getHeight();
        // 8.1获取文本开始绘制的x点位
        int x = (width - textWidth) / 2;
        // 8.2获取文本开始绘制的y点位
        int y = ((height - fontSize) / 2) + (fontSize - ((textHeight - fontSize) / 2));
        // 9.绘制文字
        graphic.drawString(content, x, y);
        // 9.1释放资源
        graphic.dispose();
        return image;
    }

    /**
     * 生成图片
     *
     * @param content  内容
     * @param width    宽度
     * @param height   高度
     * @param fontSize 字体大小
     * @return base64
     * @throws IOException 异常
     */
    public static byte[] generateBase64(String content, int width, int height, int fontSize) throws IOException {
        BufferedImage image = buildImage(content, width, height, fontSize);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", stream);
        return stream.toByteArray();
    }

    public static Font getFont() {

        InputStream stream = CharToImageUtils.class.getResourceAsStream("/fonts/Source_Han_Sans_SC_Light_Light.otf");
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
            log.info("font =="+font.toString());
            log.info("font name=="+font.getFontName());
            return new Font(font.getFontName(), Font.PLAIN, 48);
        } catch (Exception e) {
            log.error("获取Font出错",e);
            return new Font("Arial", Font.PLAIN, 48);
        }
    }

}