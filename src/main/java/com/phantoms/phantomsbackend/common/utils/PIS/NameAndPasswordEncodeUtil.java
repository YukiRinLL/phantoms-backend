package com.phantoms.phantomsbackend.common.utils.PIS;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;

public class NameAndPasswordEncodeUtil {
    private static final String KEY = "w.slofmobile.com";
    private static final String IV = "oauth.slofmobile";

//    private static final String KEY = "oauth.slofmobile";
//    private static final String IV = "w.slofmobile.com";

    /**
     * 快速加密函数
     * @param content
     * @return
     */
    public static String encrypt(String content) {
        return encryptCommon(content,KEY,IV);
    }

    /**
     * 快速解密函数
     * @param content
     * @return
     */
    public static String decrypt(String content) {
        return decryptCommon(content,KEY,IV);
    }

    /**
     * 对明文加密
     *
     * @param content 明文
     * @param key     密钥
     * @return
     */
    public static String encryptCommon(String content, String key, String iv) {
        //加密为16进制表示
//        return getAes(key, iv).encryptBase64(content);
        return getAes(key, iv).encryptHex(content);
    }

    public static String encryptCommonBase64(String content, String key, String iv) {
        //加密为16进制表示
        return getAes(key, iv).encryptBase64(content);
//        return getAes(key, iv).encryptHex(content);
    }



    /**
     * 对密文解密
     *
     * @param encryptContent 密文
     * @param key            密钥
     * @return
     */
    public static String decryptCommon(String encryptContent, String key, String iv) {
        // 解密为字符串
        return getAes(key, iv).decryptStr(encryptContent, CharsetUtil.CHARSET_UTF_8);
    }

    private static AES getAes(String key, String iv) {
        if (key != null && key.length() != 16) {
            throw new RuntimeException("密钥长度需为16位");
        }
        //构建
        return new AES(Mode.CBC, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());
    }


    public static void main(String[] args){
        String en =encryptCommonBase64("yujk93",KEY,IV);
        System.out.println(en);
        String s = decrypt(en);
        System.out.println(s);
        String encrypt = encrypt(s);
        System.out.println(encrypt);

//        String p = decrypt("bce13cd8e2b7a1393c40b2f38414173c");

    }
}
