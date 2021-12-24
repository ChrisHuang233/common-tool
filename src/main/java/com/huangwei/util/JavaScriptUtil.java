package com.huangwei.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

/**
 * JavaScript工具类
 */
public class JavaScriptUtil {

	/** JavaScript引擎（可能为NULL） */
	public static final ScriptEngine JAVA_SCRIPT_ENGINE;

	static {
		ScriptEngine engine = null;// 脚本引擎
		// 构造脚本引擎管理器
		ScriptEngineManager manager = new ScriptEngineManager();
		// 遍历引擎工厂
		for (ScriptEngineFactory factory : manager.getEngineFactories()) {
			if ("ECMAScript".equalsIgnoreCase(factory.getLanguageName())) {// 支持JavaScript
				engine = factory.getScriptEngine();
				break;
			}
		}
		JAVA_SCRIPT_ENGINE = engine;
	}

	/**
	 * URI编码（执行 encodeURI() 函数）<br>
	 * <br>
	 * 不编码字符（82个）：-_.!~*'();/?:@&=+$,#0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ<br>
	 * 由于该方法对在URI中具有特殊含义的标点符号（;/?:@&=+$,#）不进行转义，故常用于对网址编码（不包含参数）。
	 *
	 * @param uri
	 *            地址（不能为空）
	 * @return 编码后的URI
	 */
	public static String encodeUri(String uri) {
		if (uri == null) {
			throw new IllegalArgumentException("URI不能为空！");
		}
		if (JAVA_SCRIPT_ENGINE == null) {
			throw new RuntimeException("运行环境不支持JavaScript！");
		}

		try {
			return (String) JAVA_SCRIPT_ENGINE.eval("encodeURI('" + uri + "');");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * URI组件编码（执行 encodeURIComponent() 函数）<br>
	 * <br>
	 * 不编码字符（71个）：-_.!~*'()0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ<br>
	 * 由于该方法对用于分隔URI各个部分的符号进行转义，故常用于对请求参数进行编码。
	 *
	 * @param uri
	 *            地址（不能为空）
	 * @return 编码后的URI
	 */
	public static String encodeUriComponent(String uri) {
		if (uri == null) {
			throw new RuntimeException("URI不能为空！");
		}
		if (JAVA_SCRIPT_ENGINE == null) {
			throw new RuntimeException("运行环境不支持JavaScript！");
		}

		try {
			return (String) JAVA_SCRIPT_ENGINE.eval("encodeURIComponent('" + uri + "');");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * URI解码（执行 decodeURI() 函数）<br>
	 * <br>
	 * 对 encodeURI() 函数编码过的 URI 进行解码。
	 *
	 * @param uri
	 *            地址（不能为空）
	 * @return 解码后的URI
	 */
	public static String decodeUri(String uri) {
		if (uri == null) {
			throw new RuntimeException("URI不能为空！");
		}
		if (JAVA_SCRIPT_ENGINE == null) {
			throw new RuntimeException("运行环境不支持JavaScript！");
		}

		try {
			return (String) JAVA_SCRIPT_ENGINE.eval("decodeURI('" + uri + "');");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * URI组件解码（执行 decodeURIComponent() 函数）<br>
	 * <br>
	 * 对 encodeURIComponent() 函数编码过的 URI 进行解码。
	 *
	 * @param uri
	 *            地址（不能为空）
	 * @return 解码后的URI
	 */
	public static String decodeUriComponent(String uri) {
		if (uri == null) {
			throw new RuntimeException("URI不能为空！");
		}
		if (JAVA_SCRIPT_ENGINE == null) {
			throw new RuntimeException("运行环境不支持JavaScript！");
		}

		try {
			return (String) JAVA_SCRIPT_ENGINE.eval("decodeURIComponent('" + uri + "');");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

}
