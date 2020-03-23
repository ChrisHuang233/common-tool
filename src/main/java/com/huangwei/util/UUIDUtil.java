package com.huangwei.util;

import java.util.UUID;

/**
 * UUID工具
 */
public class UUIDUtil {

	/** 随机UUID（长度36，带连接符） */
	public static String random() {
		return UUID.randomUUID().toString();
	}

	/** 随机UUID（长度32，不带连接符） */
	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

}
