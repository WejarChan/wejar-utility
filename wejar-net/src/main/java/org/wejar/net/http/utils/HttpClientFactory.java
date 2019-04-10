package org.wejar.net.http.utils;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author "Jiance Qin"
 * 
 * @date 2016年8月2日
 * 
 * @time 下午2:21:17
 * 
 * @desc
 * 
 */
public final class HttpClientFactory {

	private final static Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

	private static String configFileName = "httpClientPool.properties";

	private static HttpClientPool httpClientPool = null;

	// http client config
	private static int POOL_MAX_TOTAL = 100;
	private static int POOL_MAX_PER_ROUTE = 50;

	// https client with cert config
	private static int HTTPS_CERT_POOL_MAX_TOTAL = 100;
	private static int HTTPS_CERT_POOL_MAX_PER_ROUTE = 50;
	private static String HTTPS_CERT_FILE = null;
	private static String HTTPS_CERT_KEYSTORE_TYPE = "jks";
	private static String HTTPS_CERT_PASSWORD = null;

	// common config
	private static int KEEP_ALIVE_MILLIS = 30000;

	static {
		initConfig();
		initHttpClientPool();
	}

	/**
	 * 配置文件
	 */
	private static void initConfig() {
		PropertiesUtil propertiesUtil = PropertiesUtil.newInstance(configFileName);

		String POOL_MAX_TOTAL_STR = propertiesUtil.getValue("POOL_MAX_TOTAL");
		if (isNotBlank(POOL_MAX_TOTAL_STR)) {
			POOL_MAX_TOTAL = Integer.valueOf(POOL_MAX_TOTAL_STR);
		}
		logger.info("[HttpClient init] Http conn pool max total: " + POOL_MAX_TOTAL);

		String POOL_MAX_PER_ROUTE_STR = propertiesUtil.getValue("POOL_MAX_PER_ROUTE");
		if (isNotBlank(POOL_MAX_PER_ROUTE_STR)) {
			POOL_MAX_PER_ROUTE = Integer.valueOf(POOL_MAX_PER_ROUTE_STR);
		}
		logger.info("[HttpClient init] Http conn pool max per route: " + POOL_MAX_PER_ROUTE);

		String HTTPS_CERT_POOL_MAX_TOTAL_STR = propertiesUtil.getValue("HTTPS_CERT_POOL_MAX_TOTAL");
		if (isNotBlank(HTTPS_CERT_POOL_MAX_TOTAL_STR)) {
			HTTPS_CERT_POOL_MAX_TOTAL = Integer.valueOf(HTTPS_CERT_POOL_MAX_TOTAL_STR);
		}
		logger.info("[HttpClient init] Https cert conn pool max total: " + HTTPS_CERT_POOL_MAX_TOTAL);

		String HTTPS_CERT_POOL_MAX_PER_ROUTE_STR = propertiesUtil.getValue("HTTPS_CERT_POOL_MAX_PER_ROUTE");
		if (isNotBlank(HTTPS_CERT_POOL_MAX_PER_ROUTE_STR)) {
			HTTPS_CERT_POOL_MAX_PER_ROUTE = Integer.valueOf(HTTPS_CERT_POOL_MAX_PER_ROUTE_STR);
		}
		logger.info("[HttpClient init] Https cert conn pool max per route: " + HTTPS_CERT_POOL_MAX_PER_ROUTE);

		String HTTPS_CERT_FILE_STR = propertiesUtil.getValue("HTTPS_CERT_FILE");
		if (isNotBlank(HTTPS_CERT_FILE_STR)) {
			HTTPS_CERT_FILE = HTTPS_CERT_FILE_STR;
		}
		logger.info("[HttpClient init] Https cert file: " + HTTPS_CERT_FILE);

		String HTTPS_CERT_KEYSTORE_TYPE_STR = propertiesUtil.getValue("HTTPS_CERT_KEYSTORE_TYPE");
		if (isNotBlank(HTTPS_CERT_KEYSTORE_TYPE_STR)) {
			HTTPS_CERT_KEYSTORE_TYPE = HTTPS_CERT_KEYSTORE_TYPE_STR;
		}
		logger.info("[HttpClient init] Https cert keystore type: " + HTTPS_CERT_KEYSTORE_TYPE);

		String HTTPS_CERT_PASSWORD_STR = propertiesUtil.getValue("HTTPS_CERT_PASSWORD");
		if (isNotBlank(HTTPS_CERT_PASSWORD_STR)) {
			HTTPS_CERT_PASSWORD = HTTPS_CERT_PASSWORD_STR;
		}
		logger.info("[HttpClient init] Https cert password: " + HTTPS_CERT_PASSWORD);

		String KEEP_ALIVE_STR = propertiesUtil.getValue("KEEP_ALIVE_MILLIS");
		if (isNotBlank(KEEP_ALIVE_STR)) {
			KEEP_ALIVE_MILLIS = Integer.valueOf(KEEP_ALIVE_STR);
		}
		logger.info("[HttpClient init] Keep alive millisecond: " + KEEP_ALIVE_MILLIS);
	}

	/**
	 * 初始化httpClient对象
	 */
	private static void initHttpClientPool() {
		if (isBlank(HTTPS_CERT_FILE)) {
			httpClientPool = new HttpClientPool(POOL_MAX_TOTAL, POOL_MAX_PER_ROUTE, KEEP_ALIVE_MILLIS);
		} else {
			httpClientPool = new HttpClientPool(POOL_MAX_TOTAL, POOL_MAX_PER_ROUTE, HTTPS_CERT_POOL_MAX_TOTAL,
					HTTPS_CERT_POOL_MAX_PER_ROUTE, HTTPS_CERT_FILE, HTTPS_CERT_KEYSTORE_TYPE, HTTPS_CERT_PASSWORD,
					KEEP_ALIVE_MILLIS);
		}
	}

	private static boolean checkInit() {
		if (httpClientPool == null) {
			logger.error("Initialize httpClientPool failed.");
		}
		return (httpClientPool == null) ? false : true;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param params
	 * @param encode
	 * @param connectTimeout
	 * @param soTimeout
	 * @return success return content get from response and failed return null
	 */
	public static HttpResponseInfo invokePost(String url, Map<String, Object> params, String encode, int connectTimeout,
			int soTimeout) {
		if (!checkInit()) {
			return null;
		}
		return httpClientPool.invokePost(url, params, encode, connectTimeout, soTimeout);
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param params
	 * @param headers
	 * @param encode
	 * @param connectTimeout
	 * @param soTimeout
	 * @return
	 */
	public static HttpResponseInfo invokePost(String url, Map<String, Object> params, Map<String, String> headers, String encode,
			int connectTimeout, int soTimeout) {
		if (!checkInit()) {
			return null;
		}
		return httpClientPool.invokePost(url, params, headers, encode, connectTimeout, soTimeout);
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param content
	 *            要post的字符串
	 * @param encode
	 * @param connectTimeout
	 * @param soTimeout
	 * @return
	 */
	public static HttpResponseInfo invokePost(String url, String content, String encode, int connectTimeout, int soTimeout) {
		if (!checkInit()) {
			return null;
		}
		return httpClientPool.invokePost(url, content, encode, connectTimeout, soTimeout);
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param content
	 *            要post的字符串
	 * @param headers
	 * @param encode
	 * @param connectTimeout
	 * @param soTimeout
	 * @return
	 */
	public static HttpResponseInfo invokePost(String url, String content, Map<String, String> headers, String encode,
			int connectTimeout, int soTimeout) {
		if (!checkInit()) {
			return null;
		}
		return httpClientPool.invokePost(url, content, headers, encode, connectTimeout, soTimeout);
	}

	/**
	 * 带ssl客户端证书的post请求
	 * 
	 * @param url
	 * @param content
	 *            要post的字符串
	 * @param headers
	 * @param encode
	 * @param connectTimeout
	 * @param soTimeout
	 * @return
	 */
	public static HttpResponseInfo invokePostWithHttpsCert(String url, String content, Map<String, String> headers,
			String encode, int connectTimeout, int soTimeout) {
		if (!checkInit()) {
			return null;
		}
		return httpClientPool.invokePostWithHttpsCert(url, content, headers, encode, connectTimeout, soTimeout);
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param params
	 * @param encode
	 * @param connectTimeout
	 * @param soTimeout
	 * @return success return content get from response and failed return null;
	 */
	public static HttpResponseInfo invokeGet(String url, Map<String, String> params, String encode, int connectTimeout,
			int soTimeout) {
		if (!checkInit()) {
			return null;
		}
		return httpClientPool.invokeGet(url, params, encode, connectTimeout, soTimeout);
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param params
	 * @param headers
	 * @param encode
	 * @param connectTimeout
	 * @param soTimeout
	 * @return
	 */
	public static HttpResponseInfo invokeGet(String url, Map<String, String> params, Map<String, String> headers, String encode,
			int connectTimeout, int soTimeout) {
		if (!checkInit()) {
			return null;
		}
		return httpClientPool.invokeGet(url, params, headers, encode, connectTimeout, soTimeout);
	}

	/**
	 * 提交文件的post请求
	 * 
	 * @param file
	 * @param url
	 * @param params
	 * @param connectTimeout
	 * @param soTimeout
	 * @return
	 */
	public static HttpResponseInfo invotePostForMultiPart(File file, String url, Map<String, String> params, int connectTimeout,
			int soTimeout) {
		if (!checkInit()) {
			return null;
		}
		return httpClientPool.invotePostForMultiPart(file, url, params, connectTimeout, soTimeout);
	}

	/**
	 * 提交文件的post请求
	 * 
	 * @param file
	 * @param url
	 * @param params
	 * @param headers
	 * @param connectTimeout
	 * @param soTimeout
	 * @return
	 */
	public static HttpResponseInfo invotePostForMultiPart(File file, String url, Map<String, String> params,
			Map<String, String> headers, int connectTimeout, int soTimeout) {
		if (!checkInit()) {
			return null;
		}
		return httpClientPool.invotePostForMultiPart(file, url, params, headers, connectTimeout, soTimeout);
	}

	/**
	 * 创建http/https连接池实例
	 */
	public static HttpClientPool newPoolInstance(int maxConnection, int maxConnectionPerRoute) {
		return new HttpClientPool(maxConnection, maxConnectionPerRoute, 0, 0, null, null, null, 0);
	}

	/**
	 * 创建http/https连接池实例
	 */
	public static HttpClientPool newPoolInstance(int maxConnection, int maxConnectionPerRoute, int keepAliveMillSec) {
		return new HttpClientPool(maxConnection, maxConnectionPerRoute, 0, 0, null, null, null, keepAliveMillSec);
	}

	/**
	 * 创建需要ssl客户端证书的https连接池实例
	 */
	public static HttpClientPool newHttpsCertPoolInstance(int maxConnection, int maxConnectionPerRoute,
			int httpsMaxConnection, int httpsMaxConnectionPerRoute, String httpsCertFilePath,
			String httpsCertKeystoreType, String httpsCertPasswd) {
		return new HttpClientPool(maxConnection, maxConnectionPerRoute, httpsMaxConnection, httpsMaxConnectionPerRoute,
				httpsCertFilePath, httpsCertKeystoreType, httpsCertPasswd, 0);
	}

	/**
	 * 创建需要ssl客户端证书的https连接池实例
	 */
	public static HttpClientPool newHttpsCertPoolInstance(int maxConnection, int maxConnectionPerRoute,
			int httpsMaxConnection, int httpsMaxConnectionPerRoute, String httpsCertFilePath,
			String httpsCertKeystoreType, String httpsCertPasswd, int keepAliveMillSec) {
		return new HttpClientPool(maxConnection, maxConnectionPerRoute, httpsMaxConnection, httpsMaxConnectionPerRoute,
				httpsCertFilePath, httpsCertKeystoreType, httpsCertPasswd, keepAliveMillSec);
	}
	
	private static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	private static boolean isBlank(String str) {
		return str == null || str.length() == 0;
	}
}
