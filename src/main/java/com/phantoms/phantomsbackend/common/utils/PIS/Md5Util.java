package com.phantoms.phantomsbackend.common.utils.PIS;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Md5处理工具类
 * @author zhanngle
 */
@Slf4j
public class Md5Util {

	/**
	 * 为输入的字符串生成MD5码
	 */
	public static String toMd5(String str) {

		if (str == null || str.length() == 0) {
			throw new IllegalArgumentException("参数不能为空!");
		}

		StringBuffer hexString = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] hash = md.digest();

			for (int i = 0; i < hash.length; i++) {
				if ((0xff & hash[i]) < 0x10) {
					hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & hash[i]));
				}
			}
		} catch (NoSuchAlgorithmException e) {
			log.error("生成MD5码出错",e);
		}

		String code = hexString.toString().toUpperCase();
		return code;
	}
	
	/**
	 * 生成用户密码
	 * @param password
	 * @param salt 用户id
	 * @return
	 */
	public static String makePassword(String password,String salt) {
		return toMd5(toMd5(password)+salt);
	}
	
	/**
	 * 随机生成UUID值
	 * @return
	 */
	public static String randomUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	}

	public static void main(String[] args) {
//		System.out.println(Md5Util.makePassword("1", "E209E961775C46A5802556EC56D18146"));
//		System.out.println(Md5Util.makePassword("123!@#qweQWE", "cf441646333142a98baf250daa6326d8"));
		System.out.println(Md5Util.toMd5("00D66A8CBEE64691911D3F0F8B6B7C30"));
//		System.out.println(toMd5("C4CA4238A0B923820DCC509A6F75849B"+"D3245CD1FEFE48CCA388E1A7EA125B5B"));
	}
}
