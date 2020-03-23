package com.huangwei.util;

/**
 * 正则工具
 */
public class RegexUtil {

	/** 正则核心 - 日期 - 年-月-日（yyyy-MM-dd） */
	private static final String P_CORE_Y_M_D = "(([0-9]{2}(0[48]|[2468][048]|[13579][26]))|((0[48]|[2468][048]|[13579][26])00))-02-29|([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
	/** 正则 - 日期 - 年-月-日（yyyy-MM-dd） */
	public static final String P_Y_M_D = "^" + P_CORE_Y_M_D + "$";

	/** 正则核心 - (中国)汽车车牌号（不适用于：使馆摩托车牌号(如：使A0062)） */
	private static final String P_CORE_LICENSE_AUTO = "[京津冀晋蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼渝川贵云藏陕甘青宁新使A-Z][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9领使警学挂港澳试超]";
	/** 正则核心 - (中国)新能源车牌号（《GA36-2018》纯电动：D、A、B、C、E；非纯电动：F、G、H、J、K） */
	private static final String P_CORE_LICENSE_NEV = "[京津冀晋蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼渝川贵云藏陕甘青宁新使A-Z][A-Z](([0-9]{5}[ABCEDFGHJK])|([ABCEDFGHJK][A-HJ-NP-Z0-9][0-9]{4}))";
	/** 正则 - (中国)汽车车牌号（不适用于：使馆摩托车牌号(如：使A0062)） */
	public static final String P_LICENSE_AUTO = "^" + P_CORE_LICENSE_AUTO + "$";
	/** 正则 - (中国)新能源车牌号 */
	public static final String P_LICENSE_NEV = "^" + P_CORE_LICENSE_NEV + "$";
	/** 正则 - (中国)车牌号（包括汽车和新能源车牌号）（不适用于：使馆摩托车牌号(如：使A0062)） */
	public static final String P_LICENSE = "^(" + P_CORE_LICENSE_NEV + ")|(" + P_CORE_LICENSE_AUTO + ")$";
	/** 正则 - VIN（车辆识别码/车架号） */
	public static final String P_VIN = "^[A-HJ-NPR-Z0-9]{8}[0-9X][A-HJ-NPR-TV-Y1-9][A-HJ-NPR-Z0-9]{4}[0-9]{3}$";

	/**
	 * 日期格式检查 - 年-月-日（yyyy-MM-dd）
	 * 
	 * @param dateStr
	 *            (待检查的)日期
	 * @return true:正确 false:错误
	 */
	public static final boolean dateByYMD(String dateStr) {
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

}
