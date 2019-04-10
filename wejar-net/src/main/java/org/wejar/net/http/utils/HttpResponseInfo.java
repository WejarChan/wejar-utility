package org.wejar.net.http.utils;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseInfo {

	private Logger logger = LoggerFactory.getLogger(HttpResponseInfo.class);
	
	private StatusLine statusLine;
	private Header[] headers;
	private Locale locale;
	private HttpParams params;
	
	private String content;
	private String contentType;
	private String contentEncoding;
	
	private ProtocolVersion protocolVersion;
	
	public HttpResponseInfo(StatusLine statusLine, Header[] allHeaders, Locale locale, HttpParams params, HttpEntity entity,
			ProtocolVersion protocolVersion)  {
		this.statusLine = statusLine;
		this.headers = allHeaders;
		this.locale = locale;
		this.params = params;
		try {
			this.content = EntityUtils.toString(entity, Consts.UTF_8);
			this.contentType = entity.getContentType().getValue();
			if(entity.getContentEncoding() != null){
				this.contentEncoding = entity.getContentEncoding().toString();
			}
		} catch (IOException e) {
			logger.info("解析返回信息异常",e);
		}
		this.protocolVersion = protocolVersion;
	}

	public StatusLine getStatusLine() {
		return statusLine;
	}

	public Header[] getHeaders() {
		return headers;
	}

	public Locale getLocale() {
		return locale;
	}

	public HttpParams getParams() {
		return params;
	}

	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}

	public String getContent() {
		return content;
	}

	public String getContentType() {
		return contentType;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}
	
	
//	
//	{
//	    "headers": [{
//	        "valuePos": 5,
//	        "elements": [{
//	            "parameterCount": 0,
//	            "name": "Wed",
//	            "parameters": [],
//	            "value": ""
//	        },
//	        {
//	            "parameterCount": 0,
//	            "name": "20 Sep 2017 03:31:45 GMT",
//	            "parameters": [],
//	            "value": ""
//	        }],
//	        "name": "Date",
//	        "buffer": {
//	            "empty": false,
//	            "full": false
//	        },
//	        "value": "Wed, 20 Sep 2017 03:31:45 GMT"
//	    },
//	    {
//	        "valuePos": 13,
//	        "elements": [{
//	            "parameterCount": 1,
//	            "name": "text/html",
//	            "parameters": [{
//	                "name": "charset",
//	                "value": "UTF-8"
//	            }],
//	            "value": ""
//	        }],
//	        "name": "Content-Type",
//	        "buffer": {
//	            "empty": false,
//	            "full": false
//	        },
//	        "value": "text/html; charset=UTF-8"
//	    },
//	    {
//	        "valuePos": 18,
//	        "elements": [{
//	            "parameterCount": 0,
//	            "name": "chunked",
//	            "parameters": [],
//	            "value": ""
//	        }],
//	        "name": "Transfer-Encoding",
//	        "buffer": {
//	            "empty": false,
//	            "full": false
//	        },
//	        "value": "chunked"
//	    },
//	    {
//	        "valuePos": 11,
//	        "elements": [{
//	            "parameterCount": 0,
//	            "name": "keep-alive",
//	            "parameters": [],
//	            "value": ""
//	        }],
//	        "name": "Connection",
//	        "buffer": {
//	            "empty": false,
//	            "full": false
//	        },
//	        "value": "keep-alive"
//	    },
//	    {
//	        "valuePos": 11,
//	        "elements": [{
//	            "parameterCount": 0,
//	            "name": "timeout",
//	            "parameters": [],
//	            "value": "20"
//	        }],
//	        "name": "Keep-Alive",
//	        "buffer": {
//	            "empty": false,
//	            "full": false
//	        },
//	        "value": "timeout=20"
//	    },
//	    {
//	        "valuePos": 5,
//	        "elements": [{
//	            "parameterCount": 0,
//	            "name": "Accept-Encoding",
//	            "parameters": [],
//	            "value": ""
//	        }],
//	        "name": "Vary",
//	        "buffer": {
//	            "empty": false,
//	            "full": false
//	        },
//	        "value": "Accept-Encoding"
//	    },
//	    {
//	        "valuePos": 7,
//	        "elements": [{
//	            "parameterCount": 0,
//	            "name": "WAF1.0",
//	            "parameters": [],
//	            "value": ""
//	        }],
//	        "name": "Server",
//	        "buffer": {
//	            "empty": false,
//	            "full": false
//	        },
//	        "value": "WAF1.0"
//	    },
//	    {
//	        "valuePos": 26,
//	        "elements": [{
//	            "parameterCount": 0,
//	            "name": "max-age",
//	            "parameters": [],
//	            "value": "31536000"
//	        }],
//	        "name": "Strict-Transport-Security",
//	        "buffer": {
//	            "empty": false,
//	            "full": false
//	        },
//	        "value": "max-age=31536000"
//	    },
//	    {
//	        "valuePos": 23,
//	        "elements": [{
//	            "parameterCount": 0,
//	            "name": "nosniff",
//	            "parameters": [],
//	            "value": ""
//	        }],
//	        "name": "X-Content-Type-Options",
//	        "buffer": {
//	            "empty": false,
//	            "full": false
//	        },
//	        "value": "nosniff"
//	    }],
//	    "statusLine": {
//	        "reasonPhrase": "OK",
//	        "protocolVersion": {
//	            "protocol": "HTTP",
//	            "major": 1,
//	            "minor": 1
//	        },
//	        "statusCode": 200
//	    },
//	    "contentEncoding": "",
//	    "protocolVersion": {
//	        "protocol": "HTTP",
//	        "major": 1,
//	        "minor": 1
//	    },
//	    "locale": {
//	        "unicodeLocaleKeys": [],
//	        "ISO3Language": "zho",
//	        "country": "CN",
//	        "displayName": "中文 (中国)",
//	        "displayVariant": "",
//	        "language": "zh",
//	        "displayLanguage": "中文",
//	        "script": "",
//	        "unicodeLocaleAttributes": [],
//	        "displayCountry": "中国",
//	        "ISO3Country": "CHN",
//	        "variant": "",
//	        "extensionKeys": [],
//	        "displayScript": ""
//	    },
//	    "params": {
//	        "names": []
//	    },
//	    "contentType": "text/html; charset=UTF-8",
//	    "content": "{\"biz_content\":{\"mch_id\":\"10000272\",\"order_no\":\"80020170920113145126703206400201\",\"out_order_no\":\"1505878381781\",\"pay_params\":{\"appId\":\"wxc3f90f556263fb0c\",\"nonceStr\":\"1505878305\",\"package\":\"prepay_id=wx20170920113145418d72c4b60289588677\",\"paySign\":\"F8E146E3E227977C6B8970427BC6311B\",\"signType\":\"MD5\",\"timeStamp\":\"1505878305\"},\"payment_fee\":\"100\"},\"ret_code\":\"0\",\"ret_msg\":\"success\",\"sign_type\":\"MD5\",\"signature\":\"63C40C108B81CE93AB769267FF2EEC5D\"}\r\n"
//	}
	
	
}
