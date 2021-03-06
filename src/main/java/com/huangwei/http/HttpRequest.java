package com.huangwei.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

	public interface ResultListener {
		void onConnectionPoolTimeoutError();
	}

	/** 表示请求器是否已经做了初始化工作 */
	private boolean hasInit = false;

	/** 连接超时时间，默认10秒 */
	private int socketTimeout = 10000;

	/** 传输超时时间，默认30秒 */
	private int connectTimeout = 30000;

	/** 请求器的配置 */
	private RequestConfig requestConfig;

	/** HTTP请求器 */
	private CloseableHttpClient httpClient;

	/** 证书地址 */
	private final String certpath;
	/** 证书密码 */
	private final String password;

	public HttpRequest(String certpath, String password) throws UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, IOException {
		this.certpath = certpath;
		this.password = password;
		init();
	}

	private void init() throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyManagementException {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		// 加载本地的证书进行https加密传输
		FileInputStream instream = new FileInputStream(new File(certpath));
		try {
			// 设置证书密码
			keyStore.load(instream, password.toCharArray());
		} catch (CertificateException | NoSuchAlgorithmException e) {
			log.error("设置证书密码出错", e);
		} finally {
			instream.close();
		}

		// Trust own CA and all self-signed certs
		SSLContext sslContext = SSLContexts.custom()
				.loadKeyMaterial(keyStore, password.toCharArray()).build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
				new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();

		// 根据默认超时限制初始化requestConfig
		requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout)
				.build();
		hasInit = true;
	}

	/**
	 * 通过Https往API post xml数据
	 *
	 * @param url
	 *            API地址
	 * @param xmlObj
	 *            要提交的XML数据对象
	 * @return API回包的实际数据
	 */

	public String sendPost(String url, Object xmlObj) throws IOException, KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyManagementException {

		if (!hasInit) {
			init();
		}

		String result = null;

		HttpPost httpPost = new HttpPost(url);

		// 解决XStream对出现双下划线的bug
		XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));

		// 将要提交给API的数据对象转换成XML格式数据Post给API
		String postDataXml = xStreamForRequestPostData.toXML(xmlObj);

		log.info("API，POST过去的数据是：");
		log.info(postDataXml);

		// 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
		StringEntity postEntity = new StringEntity(postDataXml, "UTF-8");
		httpPost.addHeader("Content-Type", "text/xml");
		httpPost.setEntity(postEntity);

		// 设置请求器的配置
		httpPost.setConfig(requestConfig);

		log.info("executing request" + httpPost.getRequestLine());

		try {
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
		} catch (ConnectionPoolTimeoutException e) {
			log.error("http get throw ConnectionPoolTimeoutException(wait time out)");
		} catch (ConnectTimeoutException e) {
			log.error("http get throw ConnectTimeoutException");
		} catch (SocketTimeoutException e) {
			log.error("http get throw SocketTimeoutException");
		} catch (Exception e) {
			log.error("http get throw Exception");
		} finally {
			httpPost.abort();
		}

		return result;
	}

	/**
	 * 设置连接超时时间
	 *
	 * @param socketTimeout
	 *            连接时长，默认10秒
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		resetRequestConfig();
	}

	/**
	 * 设置传输超时时间
	 *
	 * @param connectTimeout
	 *            传输时长，默认30秒
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		resetRequestConfig();
	}

	private void resetRequestConfig() {
		requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout)
				.build();
	}

	/**
	 * 允许商户自己做更高级更复杂的请求器配置
	 *
	 * @param requestConfig
	 *            设置HttpsRequest的请求器配置
	 */
	public void setRequestConfig(RequestConfig requestConfig) {
		this.requestConfig = requestConfig;
	}

}
