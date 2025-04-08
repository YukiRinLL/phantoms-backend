package com.phantoms.phantomsbackend.common.utils.PIS;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @author 影耀子YingYew
 * @Description: 生成分享二维码
 * @Package: com.guidechem.common.util
 * @ClassName: CreateShareCodeImg
 * @date 2022/10/28 16:01
 */
public class CreateShareCodeImg {

    // 缓存
    private static Cache<String, String> lruCache = CacheUtil.newLRUCache(10);

    /**
     *
     * @param url 页面路径
     * @return
     */
    public static String wapCreateImg(String url){
        return wapCreateImg(url,185,185);
    }

    /**
     *
     * @param url 页面路径
     * @return
     */
    public static String wapCreateImg(String url,Integer width,Integer height){
        try {
            String key = width + "_" + height + "_" + "img" + url;
            String o = lruCache.get(key);
            if (StrUtil.isNotBlank(o)){
                return o;
            }

            BufferedImage generate = QrCodeUtil.generate(url, width, height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
            ImageIO.write(generate, "png", baos);//写入流中
            byte[] bytes = baos.toByteArray();//转换成字节
            String png_base64 = Base64.getEncoder().encodeToString(bytes);
            String imgStr = "data:image/jpg;base64,"+png_base64;
            lruCache.put(key, imgStr);
            return imgStr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}