package org.wejar.net.http.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Jiance Qin"
 * 
 * @date 2016年6月22日
 * 
 * @time 上午10:37:51
 * 
 * @desc
 * 
 */
public class HttpClientPool {

	private final static Logger logger = LoggerFactory.getLogger(HttpClientPool.class);

	// http client
	private PoolingHttpClientConnectionManager httpConnManager = null;
	private CloseableHttpClient httpClient = null;
	private int maxConnection = 100;
	private int maxConnectionPerRoute = 50;

	// https client with cert
	private PoolingHttpClientConnectionManager httpsCertConnManager = null;
	private CloseableHttpClient httpsCertClient = null;
	private int httpsMaxConnection = 100;
	private int httpsMaxConnectionPerRoute = 50;
	private String httpsCertFilePath = null;
	private String httpsCertKeystoreType = "jks";
	private String httpsCertPasswd = null;

	// common config
	private int keepAliveMillSec = 30000;

	/**
	 * 
	 */
	public HttpClientPool(int maxConnection, int maxConnectionPerRoute) {
		initConfig(maxConnection, maxConnectionPerRoute, 0, 0, null, null, null, 0);
		initHttpClient();
	}

	/**
	 * 
	 */
	public HttpClientPool(int maxConnection, int maxConnectionPerRoute, int keepAliveMillSec) {
		initConfig(maxConnection, maxConnectionPerRoute, 0, 0, null, null, null, keepAliveMillSec);
		initHttpClient();
	}

	/**
	 * 
	 */
	public HttpClientPool(int maxConnection, int maxConnectionPerRoute, int httpsMaxConnection,
			int httpsMaxConnectionPerRoute, String httpsCertFilePath, String httpsCertKeystoreType,
			String httpsCertPasswd) {
		initConfig(maxConnection, maxConnectionPerRoute, httpsMaxConnection, httpsMaxConnectionPerRoute,
				httpsCertFilePath, httpsCertKeystoreType, httpsCertPasswd, 0);
		initHttpClient();
		initHttpsCertClient();
	}

	/**
	 * 
	 */
	public HttpClientPool(int maxConnection, int maxConnectionPerRoute, int httpsMaxConnection,
			int httpsMaxConnectionPerRoute, String httpsCertFilePath, String httpsCertKeystoreType,
			String httpsCertPasswd, int keepAliveMillSec) {
		initConfig(maxConnection, maxConnectionPerRoute, httpsMaxConnection, httpsMaxConnectionPerRoute,
				httpsCertFilePath, httpsCertKeystoreType, httpsCertPasswd, keepAliveMillSec);
		initHttpClient();
		initHttpsCertClient();
	}

	/**
	 * 配置文件
	 */
	private void initConfig(int maxConnection, int maxConnectionPerRoute, int httpsMaxConnection,
			int httpsMaxConnectionPerRoute, String httpsCertFilePath, String httpsCertKeystoreType,
			String httpsCertPasswd, int keepAliveMillSec) {
		if (maxConnection > 0) {
			this.maxConnection = maxConnection;
		}
		logger.info("[HttpClient init] Http conn pool max total: " + this.maxConnection);

		if (maxConnectionPerRoute > 0) {
			this.maxConnectionPerRoute = maxConnectionPerRoute;
		}
		logger.info("[HttpClient init] Http conn pool max per route: " + this.maxConnectionPerRoute);

		if (httpsMaxConnection > 0) {
			this.httpsMaxConnection = httpsMaxConnection;
		}
		logger.info("[HttpClient init] Https cert conn pool max total: " + this.httpsMaxConnection);

		if (httpsMaxConnectionPerRoute > 0) {
			this.httpsMaxConnectionPerRoute = httpsMaxConnectionPerRoute;
		}
		logger.info("[HttpClient init] Https cert conn pool max per route: " + this.httpsMaxConnectionPerRoute);

		if (isNotBlank(httpsCertFilePath)) {
			this.httpsCertFilePath = httpsCertFilePath;
		}
		logger.info("[HttpClient init] Https cert file: " + this.httpsCertFilePath);

		if (isNotBlank(httpsCertKeystoreType)) {
			this.httpsCertKeystoreType = httpsCertKeystoreType;
		}
		logger.info("[HttpClient init] Https cert keystore type: " + this.httpsCertKeystoreType);

		if (isNotBlank(httpsCertPasswd)) {
			this.httpsCertPasswd = httpsCertPasswd;
		}
		logger.info("[HttpClient init] Https cert password: " + this.httpsCertPasswd);

		if (keepAliveMillSec > 0) {
			this.keepAliveMillSec = keepAliveMillSec;
		}
		logger.info("[HttpClient init] keep alive millisecond: " + this.keepAliveMillSec);

	}

	/**
	 * 初始化httpClient对象
	 */
	private void initHttpClient() {
		try {
			SSLContext sslContext = SSLContexts.custom().useTLS().build();
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} }, null);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
					.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
						// 解决https请求 hostname in certificate didn't match的问题
						@Override
						public boolean verify(String arg0, SSLSession arg1) {
							return true;
						}

						@Override
						public void verify(String host, SSLSocket ssl) throws IOException {

						}

						@Override
						public void verify(String host, X509Certificate cert) throws SSLException {

						}

						@Override
						public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {

						}
					})).build();

			ConnectionKeepAliveStrategy keepAliveStrategy = new DefaultConnectionKeepAliveStrategy() {
				@Override
				public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
					long keepAlive = super.getKeepAliveDuration(response, context);
					if (keepAlive == -1) {
						// Keep connections alive KEEP_ALIVE milliseconds if a
						// keep-alive value has not be explicitly set by the
						// server
						keepAlive = keepAliveMillSec;
					}
					return keepAlive;
				}
			};

			httpConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			httpClient = HttpClients.custom().setConnectionManager(httpConnManager)
					.setKeepAliveStrategy(keepAliveStrategy).build();
			// Create socket configuration
			SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).setSoKeepAlive(true)
					.setSoReuseAddress(true).build();
			httpConnManager.setDefaultSocketConfig(socketConfig);
			// Create message constraints
			MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
					.setMaxLineLength(2000).build();
			// Create connection configuration
			ConnectionConfig connectionConfig = ConnectionConfig.custom()
					.setMalformedInputAction(CodingErrorAction.IGNORE)
					.setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8)
					.setMessageConstraints(messageConstraints).build();
			httpConnManager.setDefaultConnectionConfig(connectionConfig);

			httpConnManager.setMaxTotal(maxConnection);
			httpConnManager.setDefaultMaxPerRoute(maxConnectionPerRoute);

			HttpClientPoolIdleMonitorListener.scheduledTask(new HttpClientPoolIdleMonitorTask(httpConnManager));
		} catch (Exception e) {
			logger.error("InitHttpClient Exception:", e);
		}
	}

	/**
	 * 初始化httpsCertClient对象
	 */
	private void initHttpsCertClient() {
		if (isBlank(httpsCertFilePath)) {
			return;
		}
		try {
			KeyStore ks = KeyStore.getInstance(httpsCertKeystoreType);
			ks.load(HttpClientPool.class.getResourceAsStream(httpsCertFilePath), httpsCertPasswd.toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, httpsCertPasswd.toCharArray());

			SSLContext sslContext = SSLContexts.custom().useTLS().build();
			sslContext.init(kmf.getKeyManagers(), new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} }, null);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
					.<ConnectionSocketFactory> create().register("https", new SSLConnectionSocketFactory(sslContext))
					.build();

			ConnectionKeepAliveStrategy keepAliveStrategy = new DefaultConnectionKeepAliveStrategy() {
				@Override
				public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
					long keepAlive = super.getKeepAliveDuration(response, context);
					if (keepAlive == -1) {
						// Keep connections alive KEEP_ALIVE milliseconds if a
						// keep-alive value has not be explicitly set by the
						// server
						keepAlive = keepAliveMillSec;
					}
					return keepAlive;
				}
			};

			httpsCertConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			httpsCertClient = HttpClients.custom().setConnectionManager(httpsCertConnManager)
					.setKeepAliveStrategy(keepAliveStrategy).build();
			// Create socket configuration
			SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).setSoKeepAlive(true)
					.setSoReuseAddress(true).build();
			httpsCertConnManager.setDefaultSocketConfig(socketConfig);
			// Create message constraints
			MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
					.setMaxLineLength(2000).build();
			// Create connection configuration
			ConnectionConfig connectionConfig = ConnectionConfig.custom()
					.setMalformedInputAction(CodingErrorAction.IGNORE)
					.setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8)
					.setMessageConstraints(messageConstraints).build();
			httpsCertConnManager.setDefaultConnectionConfig(connectionConfig);

			httpsCertConnManager.setMaxTotal(httpsMaxConnection);
			httpsCertConnManager.setDefaultMaxPerRoute(httpsMaxConnectionPerRoute);

			HttpClientPoolIdleMonitorListener.scheduledTask(new HttpClientPoolIdleMonitorTask(httpsCertConnManager));
		} catch (Exception e) {
			logger.error("InitHttpsCertClient Exception: ", e);
		}
	}

	private boolean checkInit() {
		if (httpClient == null) {
			logger.error("Initialize httpClient failed.");
		}
		return (httpClient == null) ? false : true;
	}

	private boolean checkHttpsCertInit() {
		if (httpsCertClient == null) {
			logger.error("Initialize httpsCertClient failed.");
		}
		return (httpsCertClient == null) ? false : true;
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
	public HttpResponseInfo invokePost(String url, Map<String, Object> params, String encode, int connectTimeout, int soTimeout) {
		return invokePost(url, params, null, encode, connectTimeout, soTimeout);
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
	public HttpResponseInfo invokePost(String url, Map<String, Object> params, Map<String, String> headers, String encode,
			int connectTimeout, int soTimeout) {
		if (!checkInit()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("url : ").append(url).append(", params : ").append(params.toString());

		logger.debug("[HttpClientPool POST] Begin post, " + sb.toString());
		List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				BasicNameValuePair param = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
				paramPairs.add(param);
			}
		}
		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(soTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout)
				.setExpectContinueEnabled(false).build();
		httpPost.setConfig(requestConfig);

		if (headers != null && !headers.isEmpty()) {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
		}

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(paramPairs, isBlank(encode) ? Consts.UTF_8.name()
					: encode));
			CloseableHttpResponse response = null;
			HttpResponseInfo httpResponseInfo = null;
			try {
				response = httpClient.execute(httpPost);
				httpResponseInfo = new HttpResponseInfo(response.getStatusLine(),response.getAllHeaders(),response.getLocale(),response.getParams(),response.getEntity(),response.getProtocolVersion());
				logger.debug("[HttpClientPool POST] RequestUri : " + sb.toString() + ", Response status code : "
						+ httpResponseInfo.getStatusLine().getStatusCode() + ", Response content : " + httpResponseInfo.getContent());
				return httpResponseInfo;
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			httpPost.abort();
		}
		return null;

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
	public HttpResponseInfo invokePost(String url, String content, String encode, int connectTimeout, int soTimeout) {
		return invokePost(url, content, null, encode, connectTimeout, soTimeout);
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
	public HttpResponseInfo invokePost(String url, String content, Map<String, String> headers, String encode,
			int connectTimeout, int soTimeout) {
		if (!checkInit()) {
			return null;
		}

		if (content == null || content.length() == 0) {
			throw new IllegalArgumentException();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("url : ").append(url).append(", content: ").append(content);

		logger.debug("[HttpClientPool POST] Begin post, " + sb.toString());

		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(soTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout)
				.setExpectContinueEnabled(false).build();
		httpPost.setConfig(requestConfig);

		if (headers != null && !headers.isEmpty()) {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
		}
		encode = isBlank(encode) ? Consts.UTF_8.name() : encode;

		try {
			HttpEntity requestEntity = new StringEntity(content, encode);
			httpPost.setEntity(requestEntity);
			CloseableHttpResponse response = null;
			HttpResponseInfo httpResponseInfo = null;
			try {
				response = httpClient.execute(httpPost);
				httpResponseInfo = new HttpResponseInfo(response.getStatusLine(),response.getAllHeaders(),response.getLocale(),response.getParams(),response.getEntity(),response.getProtocolVersion());
				logger.debug("[HttpClientPool POST] RequestUri : " + sb.toString() + ", Response status code : "
						+ httpResponseInfo.getStatusLine().getStatusCode() + ", Response content : " + httpResponseInfo.getContent());
				return httpResponseInfo;
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			httpPost.abort();
			logger.error("Exception", e);
		}
		return null;
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
	public HttpResponseInfo invokePostWithHttpsCert(String url, String content, Map<String, String> headers, String encode,
			int connectTimeout, int soTimeout) {
		if (!checkHttpsCertInit()) {
			return null;
		}

		if (content == null || content.length() == 0) {
			throw new IllegalArgumentException();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("url : ").append(url).append(", content: ").append(content);

		logger.debug("[HttpClientPool POST] Begin post, " + sb.toString());

		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(soTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout)
				.setExpectContinueEnabled(false).build();
		httpPost.setConfig(requestConfig);

		if (headers != null && !headers.isEmpty()) {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
		}
		encode = isBlank(encode) ? Consts.UTF_8.name() : encode;

		try {
			HttpEntity requestEntity = new InputStreamEntity(new ByteArrayInputStream(content.getBytes(encode)));
			httpPost.setEntity(requestEntity);
			CloseableHttpResponse response = null;
			HttpResponseInfo httpResponseInfo = null;
			try {
				response = httpsCertClient.execute(httpPost);
				httpResponseInfo = new HttpResponseInfo(response.getStatusLine(),response.getAllHeaders(),response.getLocale(),response.getParams(),response.getEntity(),response.getProtocolVersion());
				logger.debug("[HttpClientPool POST] RequestUri : " + sb.toString() + ", Response status code : "
						+ httpResponseInfo.getStatusLine().getStatusCode() + ", Response content : " + httpResponseInfo.getContent());
				return httpResponseInfo;
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			httpPost.abort();
			logger.error("Exception", e);
		}
		return null;
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
	public HttpResponseInfo invokeGet(String url, Map<String, String> params, String encode, int connectTimeout, int soTimeout) {
		return invokeGet(url, params, null, encode, connectTimeout, soTimeout);
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
	@SuppressWarnings("deprecation")
	public HttpResponseInfo invokeGet(String url, Map<String, String> params, Map<String, String> headers, String encode,
			int connectTimeout, int soTimeout) {
		if (!checkInit()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(url);
		int i = 0;
		if(params != null)
		for (Entry<String, String> entry : params.entrySet()) {
			if (i == 0 && !url.contains("?")) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(entry.getKey());
			sb.append("=");
			String value = entry.getValue();
			try {
				sb.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.warn("encode http get params error, value is " + value, e);
				sb.append(URLEncoder.encode(value));
			}
			i++;
		}
		logger.debug("[HttpClientPool Get] begin invoke:" + sb.toString());
		HttpGet httpGet = new HttpGet(sb.toString());
		RequestConfig config = RequestConfig.custom().setSocketTimeout(soTimeout).setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectTimeout).build();
		httpGet.setConfig(config);

		if (headers != null && !headers.isEmpty()) {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpGet.setHeader(entry.getKey(), entry.getValue());
			}
		}

		try {
			CloseableHttpResponse response = null;
			HttpResponseInfo httpResponseInfo = null;
			try {
				response = httpClient.execute(httpGet);
				httpResponseInfo = new HttpResponseInfo(response.getStatusLine(),response.getAllHeaders(),response.getLocale(),response.getParams(),response.getEntity(),response.getProtocolVersion());
				logger.debug("[HttpClientPool POST] RequestUri : " + sb.toString() + ", Response status code : "
						+ httpResponseInfo.getStatusLine().getStatusCode() + ", Response content : " + httpResponseInfo.getContent());
				return httpResponseInfo;
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			httpGet.abort();
			logger.error(String.format("[HttpClientPool Get]invoke get error, url:%s", sb.toString()), e);
		}

		return null;
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
	public HttpResponseInfo invotePostForMultiPart(File file, String url, Map<String, String> params, int connectTimeout,
			int soTimeout) {
		return invotePostForMultiPart(file, url, params, null, connectTimeout, soTimeout);
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
	public HttpResponseInfo invotePostForMultiPart(File file, String url, Map<String, String> params,
			Map<String, String> headers, int connectTimeout, int soTimeout) {
		if (!checkInit()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("url : ").append(url).append(", params : ")
				.append(params.toString() + ", filename=" + file.getName());

		logger.debug("[HttpClientPool POST] Begin post, " + sb.toString());

		String responseContent = null; // 响应内容
		HttpResponseInfo httpResponseInfo = null;
		HttpPost httpPost = new HttpPost(url); // 创建HttpPost
		try {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(soTimeout)
					.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout)
					.setExpectContinueEnabled(false).build();
			httpPost.setConfig(requestConfig);

			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}

			MultipartEntity reqEntity = new MultipartEntity();
			FileBody fileBody = new FileBody(file);
			reqEntity.addPart("pic", fileBody);
			for (Map.Entry<String, String> entry : params.entrySet()) {
				StringBody sbody = new StringBody(entry.getValue(), Charset.forName("UTF-8"));
				reqEntity.addPart(entry.getKey(), sbody);
			}
			httpPost.setEntity(reqEntity);
			CloseableHttpResponse response = null;
			try {
				response = httpClient.execute(httpPost);
				httpResponseInfo = new HttpResponseInfo(response.getStatusLine(),response.getAllHeaders(),response.getLocale(),response.getParams(),response.getEntity(),response.getProtocolVersion());
				logger.debug("[HttpClientPool POST] RequestUri : " + sb.toString() + ", Response status code : "
						+ httpResponseInfo.getStatusLine().getStatusCode() + ", Response content : " + httpResponseInfo.getContent());
				return httpResponseInfo;
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			httpPost.abort();
			logger.error("IOException", e);
		}
		return httpResponseInfo;
	}
	
	private boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	private boolean isBlank(String str) {
		return str == null || str.length() == 0;
	}
	
}
