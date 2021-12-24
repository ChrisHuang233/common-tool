package com.huangwei.util;

/**
 * GPS工具
 */
public class GpsUtil {

	private static final double a = 6378245.0;
	private static final double ee = 0.00669342162296594323;

	/** 地心坐标(WGS-84) - 经度（GPS原始坐标） */
	private double longitude;
	/** 地心坐标(WGS-84) - 纬度（GPS原始坐标） */
	private double latitude;
	/** 国测局坐标(火星坐标,GCJ-02) - 经度（高德地图，腾讯地图，谷歌中国） */
	private double longitudeGcj02;
	/** 国测局坐标(火星坐标,GCJ-02) - 纬度（高德地图，腾讯地图，谷歌中国） */
	private double latitudeGcj02;
	/** 百度坐标(BD-09) - 经度（百度地图） */
	private double longitudeBd09;
	/** 百度坐标(BD-09) - 纬度（百度地图） */
	private double latitudeBd09;

	/**
	 * GPS工具
	 *
	 * @param longitude
	 *            地心坐标(WGS-84) - 经度（GPS原始坐标）
	 * @param latitude
	 *            地心坐标(WGS-84) - 纬度（GPS原始坐标）
	 * @param longitudeGcj02
	 *            国测局坐标(火星坐标,GCJ-02) - 经度（高德地图，腾讯地图，谷歌中国）
	 * @param latitudeGcj02
	 *            国测局坐标(火星坐标,GCJ-02) - 纬度（高德地图，腾讯地图，谷歌中国）
	 * @param longitudeBd09
	 *            百度坐标(BD-09) - 经度（百度地图）
	 * @param latitudeBd09
	 *            百度坐标(BD-09) - 纬度（百度地图）
	 */
	private GpsUtil(double longitude, double latitude, double longitudeGcj02, double latitudeGcj02,
			double longitudeBd09, double latitudeBd09) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.longitudeGcj02 = longitudeGcj02;
		this.latitudeGcj02 = latitudeGcj02;
		this.longitudeBd09 = longitudeBd09;
		this.latitudeBd09 = latitudeBd09;
	}

	/**
	 * GPS工具 - 源自“地心坐标(WGS-84)”
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return GPS工具（含多种坐标）
	 */
	public static GpsUtil fromGps(double lng, double lat) {
		if (outofChina(lng, lat)) {
			return new GpsUtil(lng, lat, lng, lat, lng, lat);
		}

		double[] mars = gps2Mars(lng, lat);// 国测局坐标(火星坐标,GCJ-02)
		double[] baidu = mars2Baidu(mars[0], mars[1]);// 百度坐标(BD-09)
		return new GpsUtil(lng, lat, mars[0], mars[1], baidu[0], baidu[1]);
	}

	/**
	 * GPS工具 - 源自“国测局坐标(火星坐标,GCJ-02)”
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return GPS工具（含多种坐标）
	 */
	public static GpsUtil fromGcj02(double lng, double lat) {
		double[] gps = mars2Gps(lng, lat);// 地心坐标(WGS-84)
		double[] baidu = mars2Baidu(lng, lat);// 百度坐标(BD-09)
		return new GpsUtil(gps[0], gps[1], lng, lat, baidu[0], baidu[1]);
	}

	/**
	 * GPS工具 - 源自“百度坐标(BD-09)”
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return GPS工具（含多种坐标）
	 */
	public static GpsUtil fromBd09(double lng, double lat) {
		double[] mars = baidu2Mars(lng, lat);// 国测局坐标(火星坐标,GCJ-02)
		double[] gps = mars2Gps(mars[0], mars[1]);// 地心坐标(WGS-84)
		return new GpsUtil(gps[0], gps[1], mars[0], mars[1], lng, lat);
	}

	/**
	 * 地心坐标(WGS-84) -> 国测局坐标(火星坐标,GCJ-02)
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return 国测局坐标(火星坐标,GCJ-02)（{ 经度, 纬度 }）
	 */
	public static double[] gps2Mars(double lng, double lat) {
		if (outofChina(lng, lat)) {
			return new double[] { lng, lat };
		}

		double dLng = transformLongitude(lng - 105.0, lat - 35.0);
		double dLat = transformLatitude(lng - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * Math.PI;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLng = dLng * 180.0 / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
		dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * Math.PI);

		double longitude = lng + dLng;
		double latitude = lat + dLat;
		return new double[] { longitude, latitude };
	}

	/**
	 * 地心坐标(WGS-84) -> 百度坐标(BD-09)<br>
	 *
	 * 注意：该方法经过多次计算得到结果，不保证其精确度。
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return 百度坐标(BD-09)（{ 经度, 纬度 }）
	 */
	public static double[] gps2Baidu(double lng, double lat) {
		if (outofChina(lng, lat)) {
			return new double[] { lng, lat };
		}

		double[] coordinate = gps2Mars(lng, lat);
		return mars2Baidu(coordinate[0], coordinate[1]);
	}

	/**
	 * 国测局坐标(火星坐标,GCJ-02) -> 地心坐标(WGS-84)
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return 地心坐标(WGS-84)（{ 经度, 纬度 }）
	 */
	public static double[] mars2Gps(double lng, double lat) {
		double[] coordinate = gps2Mars(lng, lat);

		double longitude = lng * 2 - coordinate[0];
		double latitude = lat * 2 - coordinate[1];
		return new double[] { longitude, latitude };
	}

	/**
	 * 国测局坐标(火星坐标,GCJ-02) -> 百度坐标(BD-09)
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return 百度坐标（{ 经度, 纬度 }）
	 */
	public static double[] mars2Baidu(double lng, double lat) {
		double x = lng;
		double y = lat;

		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI * 3000D / 180D);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI * 3000D / 180D);

		double longitude = z * Math.cos(theta) + 0.0065;
		double latitude = z * Math.sin(theta) + 0.006;
		return new double[] { longitude, latitude };
	}

	/**
	 * 百度坐标(BD-09) -> 国测局坐标(火星坐标,GCJ-02)
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return 国测局坐标（{ 经度, 纬度 }）
	 */
	public static double[] baidu2Mars(double lng, double lat) {
		double x = lng - 0.0065;
		double y = lat - 0.006;

		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI * 3000D / 180D);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI * 3000D / 180D);

		double longitude = z * Math.cos(theta);
		double latitude = z * Math.sin(theta);
		return new double[] { longitude, latitude };
	}

	/**
	 * 百度坐标(BD-09) -> 地心坐标(WGS-84)<br>
	 *
	 * 注意：该方法经过多次计算得到结果，不保证其精确度。
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return 地心坐标(WGS-84)（{ 经度, 纬度 }）
	 */
	public static double[] baidu2Gps(double lng, double lat) {
		double[] coordinate = baidu2Mars(lng, lat);
		return mars2Gps(coordinate[0], coordinate[1]);
	}

	/**
	 * 判断坐标是否在中国之外（地心坐标(WGS-84)）
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return true:国外 false:国内
	 */
	private static boolean outofChina(double lng, double lat) {
		if (lng < 72.004 || lng > 137.8347) {
			return true;
		}
		return lat < 0.8293 || lat > 55.8271;
	}

	/** 经度转换 */
	private static double transformLongitude(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
		return ret;
	}

	/** 维度转换 */
	private static double transformLatitude(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	/** 地心坐标(WGS-84) - 经度（GPS原始坐标） */
	public double getLongitude() {
		return longitude;
	}

	/** 地心坐标(WGS-84) - 经度（GPS原始坐标） */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/** 地心坐标(WGS-84) - 纬度（GPS原始坐标） */
	public double getLatitude() {
		return latitude;
	}

	/** 地心坐标(WGS-84) - 纬度（GPS原始坐标） */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/** 百度坐标(BD-09) - 经度（百度地图） */
	public double getLongitudeBd09() {
		return longitudeBd09;
	}

	/** 百度坐标(BD-09) - 经度（百度地图） */
	public void setLongitudeBd09(double longitudeBd09) {
		this.longitudeBd09 = longitudeBd09;
	}

	/** 百度坐标(BD-09) - 纬度（百度地图） */
	public double getLatitudeBd09() {
		return latitudeBd09;
	}

	/** 百度坐标(BD-09) - 纬度（百度地图） */
	public void setLatitudeBd09(double latitudeBd09) {
		this.latitudeBd09 = latitudeBd09;
	}

	/** 国测局坐标(火星坐标,GCJ-02) - 经度（高德地图，腾讯地图，谷歌中国） */
	public double getLongitudeGcj02() {
		return longitudeGcj02;
	}

	/** 国测局坐标(火星坐标,GCJ-02) - 经度（高德地图，腾讯地图，谷歌中国） */
	public void setLongitudeGcj02(double longitudeGcj02) {
		this.longitudeGcj02 = longitudeGcj02;
	}

	/** 国测局坐标(火星坐标,GCJ-02) - 纬度（高德地图，腾讯地图，谷歌中国） */
	public double getLatitudeGcj02() {
		return latitudeGcj02;
	}

	/** 国测局坐标(火星坐标,GCJ-02) - 纬度（高德地图，腾讯地图，谷歌中国） */
	public void setLatitudeGcj02(double latitudeGcj02) {
		this.latitudeGcj02 = latitudeGcj02;
	}

}
