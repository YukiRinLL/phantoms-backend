package com.phantoms.phantomsbackend.common.utils.PIS;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

public class CaptchaUtil {

    private static final String CHAR_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CAPTCHA_LENGTH = 4;
    private static final int CAPTCHA_WIDTH = 150;
    private static final int CAPTCHA_HEIGHT = 50;
    private static final int LINE_COUNT = 20;
    private static final Random random = new SecureRandom();

    public static String generateCaptchaText() {
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            String charToAppend = CHAR_STRING.charAt(random.nextInt(CHAR_STRING.length())) + "";
            captcha.append(charToAppend);
        }
        return captcha.toString();
    }

    public static BufferedImage generateCaptchaImage(String captchaText) {
        int width = 150;
        int height = 50;
        int fontSize = 30; // 增加字体大小
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 设置字体
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g.setFont(font);

        // 添加干扰线
        for (int i = 0; i < 20; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width / 8);
            int ye = ys + random.nextInt(height / 8);
            g.setColor(getRandomColor(160, 200));
            g.drawLine(xs, ys, xe, ye);
        }

        // 添加验证码字母
        int y = (height - fontSize) / 2 + fontSize; // 调整字母的垂直位置
        int x = 10; // 调整起始位置
        for (int i = 0; i < captchaText.length(); i++) {
            String s = String.valueOf(captchaText.charAt(i));
            int angle = random.nextInt(30) - 15; // 随机倾斜角度
            g.setColor(getRandomColor(40, 150));
            g.rotate(Math.toRadians(angle), x + fontSize / 2, y + fontSize / 2); // 旋转坐标系
            g.drawString(s, x, y);
            g.rotate(-Math.toRadians(angle), x + fontSize / 2, y + fontSize / 2); // 恢复坐标系
            x += fontSize + random.nextInt(10); // 增加间距
        }

        g.dispose();

        return bufferedImage;
    }

    private static Color getRandomColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}