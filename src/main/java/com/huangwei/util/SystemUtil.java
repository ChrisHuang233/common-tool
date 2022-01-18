package com.huangwei.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统工具
 */
public class SystemUtil {
	protected static Logger logger = LoggerFactory.getLogger(SystemUtil.class);

	/** 本机IP地址 */
	private static List<String> localIP = null;

	/**
	 * 获取物理地址
	 * 
	 * @param addresses
	 *            网络地址
	 * @return null 或 物理地址
	 */
	public static String getMAC(InetAddress addresses) {
		if (addresses == null) {
			return null;
		}

		try {
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(addresses);
			if (networkInterface == null) {
				return null;
			}
			byte[] mac = networkInterface.getHardwareAddress();
			if (mac == null || mac.length == 0) {
				return null;
			}

			return ByteUtil.toHexStr(mac, "-");
		} catch (Exception e) {
			logger.error("获取MAC出错！", e);
			return null;
		}
	}

	/**
	 * 获取本机物理地址
	 * 
	 * @return null 或 物理地址
	 */
	public static String getLocalMAC() {
		try {
			return getMAC(InetAddress.getLocalHost());
		} catch (Exception e) {
			logger.error("获取本机MAC出错！", e);
			return null;
		}
	}

	/**
	 * 获取本机所有IP地址（IPv4 & IPv6）
	 * 
	 * @return null 或 IP地址列表
	 */
	public static List<String> getAllLocalIP() {
		HashSet<String> set = new HashSet<String>();
		try {
			for (Enumeration<NetworkInterface> adapter = NetworkInterface.getNetworkInterfaces(); adapter
					.hasMoreElements();) {
				for (Enumeration<InetAddress> address = adapter.nextElement().getInetAddresses(); address
						.hasMoreElements();) {
					set.add(address.nextElement().getHostAddress());
				}
			}
		} catch (Exception e) {
			logger.error("获取本机所有IP地址出错！", e);
		}
		return set.isEmpty() ? null : new ArrayList<String>(set);
	}

	/**
	 * 获取本机所有物理地址
	 * 
	 * @return null 或 物理地址列表
	 */
	public static List<String> getAllLocalMAC() {
		HashSet<String> set = new HashSet<String>();
		try {
			byte[] mac;
			for (Enumeration<NetworkInterface> adapter = NetworkInterface.getNetworkInterfaces(); adapter
					.hasMoreElements();) {
				mac = adapter.nextElement().getHardwareAddress();
				if (mac != null && mac.length > 0) {
					set.add(ByteUtil.toHexStr(mac, "-"));
				}
			}
		} catch (Exception e) {
			logger.error("获取本机所有MAC出错！", e);
		}
		return set.isEmpty() ? null : new ArrayList<String>(set);
	}

	/**
	 * 获取本机所有IP地址（IPv4 & IPv6）
	 * 
	 * @return null 或 IP地址列表
	 */
	public static String[] getAllLocalAddress() {
		List<String> list = getAllLocalIP();
		return list == null ? null : list.toArray(new String[0]);
	}

	/**
	 * 是否是本机IP
	 * 
	 * @param ip
	 *            IP地址
	 * @return true:本机IP false:非本机IP
	 */
	public static boolean isLocalIP(String ip) {
		if (ip == null || "".equals(ip)) {
			return false;
		}

		if (localIP == null || localIP.isEmpty()) {
			localIP = getAllLocalIP();
		}

		return localIP == null ? false : localIP.contains(ip);
	}

}
