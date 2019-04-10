package org.wejar.net.http.utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
  
public class HttpClientCallSoapUtils {  
    static int socketTimeout = 30000;// 请求超时时间  
    static int connectTimeout = 30000;// 传输超时时间  
    static Logger logger = LoggerFactory.getLogger(HttpClientCallSoapUtils.class);
  
    /** 
     * 使用SOAP1.1发送消息 
     *  
     * @param postUrl 
     * @param soapXml 
     * @param soapAction 
     * @return 
     */  
    public static String doPostSoap1_1(String postUrl, String soapXml,String soapAction) {  
        String retStr = "";  
        // 创建HttpClientBuilder  
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
        // HttpClient  
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
        HttpPost httpPost = new HttpPost(postUrl);  
                //  设置请求和传输超时时间  
        RequestConfig requestConfig = RequestConfig.custom()  
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();  
        httpPost.setConfig(requestConfig);  
        
        try {  
            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");  
            httpPost.setHeader("SOAPAction", soapAction);  
            StringEntity data = new StringEntity(soapXml, Charset.forName("UTF-8"));  
            httpPost.setEntity(data);  
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);  
            HttpEntity httpEntity = response.getEntity();  
            if (httpEntity != null) {  
                // 打印响应内容  
                retStr = EntityUtils.toString(httpEntity, "UTF-8");  
//                logger.info("response:" + retStr);  
            }  
            // 释放资源  
            closeableHttpClient.close();  
        } catch (Exception e) {  
            logger.error("exception in doPostSoap1_1", e);  
        }  
        return retStr;  
    }  
  
    /** 
     * 使用SOAP1.2发送消息 
     *  
     * @param postUrl 
     * @param soapXml 
     * @param soapAction 
     * @return 
     */  
    public static String doPostSoap1_2(String postUrl, String soapXml,  
            String soapAction) {  
        String retStr = "";  
        // 创建HttpClientBuilder  
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
        // HttpClient  
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
        HttpPost httpPost = new HttpPost(postUrl);  
                // 设置请求和传输超时时间  
        RequestConfig requestConfig = RequestConfig.custom()  
                .setSocketTimeout(socketTimeout)  
                .setConnectTimeout(connectTimeout).build();  
        httpPost.setConfig(requestConfig);  
        try {  
            httpPost.setHeader("Content-Type",  
                    "application/soap+xml;charset=UTF-8");  
            httpPost.setHeader("SOAPAction", soapAction);  
            StringEntity data = new StringEntity(soapXml,  
                    Charset.forName("UTF-8"));  
            httpPost.setEntity(data);  
            CloseableHttpResponse response = closeableHttpClient  
                    .execute(httpPost);  
            HttpEntity httpEntity = response.getEntity();  
            if (httpEntity != null) {  
                // 打印响应内容  
                retStr = EntityUtils.toString(httpEntity, "UTF-8");  
//                logger.info("response:" + retStr);  
            }  
            // 释放资源  
            closeableHttpClient.close();  
        } catch (Exception e) {  
            logger.error("exception in doPostSoap1_2", e);  
        }  
        return retStr;  
    }  
  
    public static void main(String[] args) throws NoSuchAlgorithmException {  
        String shanghuhao = "GZQQF004";
        String zhongduanhao = "36241531";
        String chepaihao = "闽HB2956";
        int liushuihao = 12345621;
        String shijian = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
        String zhongduanmima = "xr888888";
        String str = shanghuhao+zhongduanhao+liushuihao+shijian+zhongduanmima;
        
        // 生成一个MD5加密计算摘要
        MessageDigest md = MessageDigest.getInstance("MD5");
        // 计算md5函数
        md.update(str.getBytes());
        // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
        String qianming = new BigInteger(1, md.digest()).toString(32);
        String soapRequestData = 
        		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:jia=\"http://jiaotongfakuan.epaylinks\">"
        	        +"<soapenv:Header/>"
        	        +"<soapenv:Body>"
        	        +"<jia:jiaofaChaxun>"
        	        +"<jia:reqXml>"
        	        
        	        +"<![CDATA["
					+"<JiaofaChaxunReq>"
						+"<shanghuhao>"+shanghuhao+"</shanghuhao>"
	    	  			+"<zhongduanhao>"+zhongduanhao+"</zhongduanhao>"
	    	  			+"<liushuihao>"+liushuihao+"</liushuihao>"
	        	  			
	    	  			+"<chepaihao>"+chepaihao+"</chepaihao>"
//	    	  			+"<chejiahao>150855</chejiahao>"
//	    	  			+"<fadongjihao>558615</fadongjihao>"
//	    	  			+"<weizhangdi></weizhangdi>"
//	    	  			+"<chezhu></chezhu>"
	    	  			+"<shijian>"+shijian+"</shijian>"
		    			+"<qianming>"+qianming+"</qianming>"
    				+"</JiaofaChaxunReq>"
    				+ "]]>"
    				
        	        + "</jia:reqXml>"
        	        +"</jia:jiaofaChaxun>"
        	        +"</soapenv:Body>"
        	        +"</soapenv:Envelope>";
        
        String postUrl = "http://www.epaylinks.cn/chongzhi/services/JiaotongfakuanService";  
        //采用SOAP1.1调用服务端，这种方式能调用服务端为soap1.1和soap1.2的服务  
        String some = doPostSoap1_1(postUrl, soapRequestData, "");  
        System.out.println(some);
//        System.out.println();
//        some = doPostSoap1_2(postUrl, soapRequestData, "");  
//        System.out.println(some);
        //采用SOAP1.2调用服务端，这种方式只能调用服务端为soap1.2的服务  
        //doPostSoap1_2(postUrl, orderSoapXml, "order");  
        //doPostSoap1_2(postUrl, querySoapXml, "query");  
    }  
}  