package com.huangwei.util;

/**
 * 正则工具
 */
public class RegexUtil {

	/** 正则核心 - 字母 */
	private static final String CORE_ALPHABETIC = "a-zA-Z";
	/** 正则 - 字母 */
	public static final String P_ALPHABETIC = "^[" + CORE_ALPHABETIC + "]+$";
	/** 正则核心 - 数字 */
	private static final String CORE_NUMERIC = "0-9";
	/** 正则 - 数字 */
	public static final String P_NUMERIC = "^[" + CORE_NUMERIC + "]+$";
	/** 正则 - 字母 或 数字 或 两者组合 */
	public static final String P_ALPHANUMERIC = "^[" + CORE_ALPHABETIC + CORE_NUMERIC + "]+$";
	/** 正则核心 - 标点符号（不包括不包括空格和制表符(TAB)）（参考：POSIX[:punct:]） */
	private static final String CORE_PUNCTUATION = "~'\\?\\!@#\\$%\\^&\\*\\(\\)-_=\\+\\[\\]\\{\\},\\.:;`\"|\\/\\\\<>";
	/** 正则 - 标点符号（不包括不包括空格和制表符(TAB)）（参考：POSIX[:punct:]） */
	public static final String P_PUNCTUATION = "^[" + CORE_PUNCTUATION + "]+$";
	/** 正则核心 - 空白字符（空格、制表符(TAB)、回车符、换行符、垂直制表符、换页符及几者组合）（参考：POSIX[:space:]） */
	private static final String CORE_SPACE = "\\s";
	/** 正则 - 空白字符（空格、制表符(TAB)、回车符、换行符、垂直制表符、换页符及几者组合）（参考：POSIX[:space:]） */
	public static final String P_SPACE = "^[" + CORE_SPACE + "]+$";
	/** 正则 - 可见字符（字母、数字、标点符号及三者组合，不包括空格和制表符(TAB)）（参考：POSIX[:graph:]） */
	public static final String P_GRAPH = "^[" + CORE_ALPHABETIC + CORE_NUMERIC + CORE_PUNCTUATION + "]+$";
	/** 正则 - 可打印字符（字母、数字、标点符号、空白字符及四者组合）（参考：POSIX[:print:]） */
	public static final String P_PRINT = "^[" + CORE_ALPHABETIC + CORE_NUMERIC + CORE_PUNCTUATION + CORE_SPACE + "]+$";

	/** 正则核心 - 日期 - 年-月-日（yyyy-MM-dd） */
	private static final String CORE_Y_M_D = "(([0-9]{2}(0[48]|[2468][048]|[13579][26]))|((0[48]|[2468][048]|[13579][26])00))-02-29|([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
	/** 正则 - 日期 - 年-月-日（yyyy-MM-dd） */
	public static final String P_Y_M_D = "^" + CORE_Y_M_D + "$";

	/** 正则核心 - (中国)汽车车牌号（不适用于：使馆摩托车牌号(如：使A0062)） */
	private static final String CORE_LICENSE_AUTO = "[京津冀晋蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼渝川贵云藏陕甘青宁新使A-Z][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9领使警学挂港澳试超]";
	/** 正则核心 - (中国)新能源车牌号（《GA36-2018》纯电动：D、A、B、C、E；非纯电动：F、G、H、J、K） */
	private static final String CORE_LICENSE_NEV = "[京津冀晋蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼渝川贵云藏陕甘青宁新使A-Z][A-Z](([0-9]{5}[ABCEDFGHJK])|([ABCEDFGHJK][A-HJ-NP-Z0-9][0-9]{4}))";
	/** 正则 - (中国)汽车车牌号（不适用于：使馆摩托车牌号(如：使A0062)） */
	public static final String P_LICENSE_AUTO = "^" + CORE_LICENSE_AUTO + "$";
	/** 正则 - (中国)新能源车牌号 */
	public static final String P_LICENSE_NEV = "^" + CORE_LICENSE_NEV + "$";
	/** 正则 - (中国)车牌号（包括汽车和新能源车牌号）（不适用于：使馆摩托车牌号(如：使A0062)） */
	public static final String P_LICENSE = "^(" + CORE_LICENSE_NEV + ")|(" + CORE_LICENSE_AUTO + ")$";
	/** 正则 - VIN（车辆识别码/车架号） */
	public static final String P_VIN = "^[A-HJ-NPR-Z0-9]{8}[0-9X][A-HJ-NPR-TV-Y1-9][A-HJ-NPR-Z0-9]{4}[0-9]{3}$";

	/** 正则 - 手机号(中国大陆) */
	public static final String MOBILE_PHONE = "^1[3456789]\\d{9}$";

	/** 正则核心 - 端口 */
	private static final String CORE_PORT = "[0-9]|[1-9]\\\\d{1,3}|[1-5]\\\\d{4}|6[0-4]\\\\d{3}|65[0-4]\\\\d{2}|655[0-2]\\\\d|6553[0-5]";
	/** 正则 - 端口 */
	public static final String P_PORT = "^" + CORE_PORT + "$";

	/**
	 * 是否只包含字母
	 * 
	 * @param str
	 *            (待检查的)字符串
	 * @return true:是 false:否
	 */
	public static final boolean isAlphabetic(String str) {
		if (str == null || str.length() < 1) {
			return false;
		}

		return str.matches(P_ALPHABETIC);
	}

	/**
	 * 是否只包含数字
	 * 
	 * @param str
	 *            (待检查的)字符串
	 * @return true:是 false:否
	 */
	public static final boolean isNumeric(String str) {
		if (str == null || str.length() < 1) {
			return false;
		}

		return str.matches(P_NUMERIC);
	}

	/**
	 * 是否只包含字母或数字或两者组合
	 * 
	 * @param str
	 *            (待检查的)字符串
	 * @return true:是 false:否
	 */
	public static final boolean isAlphanumeric(String str) {
		if (str == null || str.length() < 1) {
			return false;
		}

		return str.matches(P_ALPHANUMERIC);
	}

	/**
	 * 是否只包含可见字符<br>
	 * <br>
	 * 可见字符：字母、数字、标点符号及三者组合，不包括空格和制表符(TAB)（参考：POSIX[:graph:]）。
	 * 
	 * @param str
	 *            (待检查的)字符串
	 * @return true:是 false:否
	 */
	public static final boolean isGraph(String str) {
		if (str == null || str.length() < 1) {
			return false;
		}

		return str.matches(P_GRAPH);
	}

	/**
	 * 日期格式检查 - 年-月-日（yyyy-MM-dd）
	 * 
	 * @param dateStr
	 *            (待检查的)日期
	 * @return true:正确 false:错误
	 */
	public static final boolean dateWithYmd(String dateStr) {
		if (dateStr == null || dateStr.length() < 10) {
			return false;
		}

		return dateStr.matches(P_Y_M_D);
	}

	/**
	 * 是否汽车车牌号（不包括新能源车牌号）<br>
	 * 不适用于：使馆摩托车牌号(如：使A0062)
	 * 
	 * @param license
	 *            (待校验的)车牌号
	 * @return true:是 false:否
	 */
	public static final boolean isAutoLicense(String license) {
		if (license == null || license.length() < 7) {
			return false;
		}

		return license.matches(P_LICENSE_AUTO);
	}

	/**
	 * 是否是新能源车牌号
	 * 
	 * @param license
	 *            (待校验的)车牌号
	 * @return true:是 false:否
	 */
	public static final boolean isNevLicense(String license) {
		if (license == null || license.length() < 8) {
			return false;
		}

		return license.matches(P_LICENSE_NEV);
	}

	/**
	 * 是否是车牌号（包括汽车和新能源车牌号）<br>
	 * 不适用于：使馆摩托车牌号(如：使A0062)
	 * 
	 * @param license
	 *            (待校验的)车牌号
	 * @return true:是 false:否
	 */
	public static final boolean isVehicleLicense(String license) {
		if (license == null || license.length() < 7) {
			return false;
		}

		return license.matches(P_LICENSE);
	}

	/**
	 * 是否是VIN（车辆识别码/车架号）
	 * 
	 * @param vin
	 *            (待校验的)VIN
	 * @return true:是 false:否
	 */
	public static final boolean isVin(String vin) {
		if (vin == null || vin.length() < 17) {
			return false;
		}

		return vin.matches(P_VIN);
	}

	/**
	 * 是否是手机号
	 *
	 * @param phoneNumber
	 *            (待校验的)手机号
	 * @return true:是 false:否
	 */
	public static final boolean isMobileNumber(String phoneNumber) {
		if (phoneNumber == null) {
			return false;
		}
		return phoneNumber.matches(MOBILE_PHONE);
	}

	/**
	 * 校验是否为银行卡号
	 *
	 * @param cardNo
	 *            银行卡号
	 * @return true:是 false:否
	 */
	public static boolean isBankCardNo(String cardNo) {
		if (cardNo == null || !cardNo.matches("^[0-9]{16,19}$")) {
			return false;
		}
		// Luhn算法校验步骤：
		// ①从右边第1个数字(校验码)开始偶数位乘以2；
		// ②把步骤①中获得的乘积的各位数字(个位与十位)与原号码中未乘2的每个数字相加；
		// ③如果步骤②得到的总和模10为0，则校验通过。
		char[] array = cardNo.toCharArray();
		int length = array.length, sum = 0;
		for (int i = length; i >= 1; i--) {
			int n = array[length - i] - '0';
			if (i % 2 == 0) {
				n *= 2;
			}
			// 偶数位乘以2，乘积的个位与十位相加；非偶数位不乘以2，其十位为0，不影响计算结果。
			sum += n % 10 + n / 10;
		}
		return sum % 10 == 0;
	}

}
