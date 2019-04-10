package org.wejar.weixin.mp;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wejar.dom.utils.xml.SerializeXmlUtil;
import org.wejar.net.http.utils.HttpClientFactory;
import org.wejar.net.http.utils.HttpResponseInfo;

import com.thoughtworks.xstream.XStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class WeiXinTool {

	private static final Logger logger = LoggerFactory.getLogger(WeiXinTool.class);
	
	private static final String ENCODE = "UTF-8";
	
	private Map<String,Object> tokenStore = null;
	
	private String appId;
	private String appSecret;
	private String token;
	private String AES_KEY;
	
	
	/**
	 * 获取一个可用的accessToken
	 * @return access_token
	 */
	public String getAccessToken(){
		String token = (String) tokenStore.get("accessToken");
		Long deadLine = (Long) tokenStore.get("deadLine");
		
		Long now = new Date().getTime();
		//离过期5分钟提前刷新
		if(token == null || deadLine <= now-300){
			token = refreshAccessToken();
		}
		
		return token;
	}

	/**
	 * 刷新 accessToken
	 * @return
	 */
	private String refreshAccessToken() {
		String grantType = "client_credential";
//		https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx1fe85dd331d2887b&secret=9e76541d4d35fc5d34c9b9e452665fd8
		String url = "https://api.weixin.qq.com/cgi-bin/token";
		
		Map<String,String> params = new HashMap<String,String>();
		
		params.put("grant_type", "client_credential");
		
		params.put("appid", this.appId);
		params.put("secret", this.appSecret);
		
		HttpResponseInfo response = HttpClientFactory.invokeGet(url, params, ENCODE, 10000, 10000);
		String responseContent = response.getContent();
		
//		{"access_token":"ACCESS_TOKEN","expires_in":7200}
//		{"errcode":40013,"errmsg":"invalid appid"}
		JSONObject json = JSONObject.fromObject(responseContent);
		
		String accessToken =json.getString("access_token");
		Integer expiresIn = json.getInt("expires_in");
		
		if(expiresIn != null && accessToken != null){
			tokenStore.put("accessToken", accessToken);
			tokenStore.put("deadLine", new Date().getTime() + expiresIn);
		}
		
		return accessToken;
	}
	
	/**
	 * 用SHA1算法生成安全签名
	 * 
	 * @param token
	 *            票据
	 * @param timestamp
	 *            时间戳
	 * @param nonce
	 *            随机字符串
	 * @param encrypt
	 *            密文
	 * @return 安全签名
	 * @throws NoSuchAlgorithmException
	 * @throws AesException
	 */
	public static String getSHA1(String token, String timestamp, String nonce) throws NoSuchAlgorithmException {
		String[] array = new String[] { token, timestamp, nonce };
		StringBuffer sb = new StringBuffer();
		// 字符串排序
		Arrays.sort(array);
		for (int i = 0; i < 3; i++) {
			sb.append(array[i]);
		}
		String str = sb.toString();
		// SHA1签名生成
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(str.getBytes());
		byte[] digest = md.digest();

		StringBuffer hexstr = new StringBuffer();
		String shaHex = "";
		for (int i = 0; i < digest.length; i++) {
			shaHex = Integer.toHexString(digest[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexstr.append(0);
			}
			hexstr.append(shaHex);
		}
		return hexstr.toString();
	}
	
	/**
	 * 用code获取授权的AccessToken
	 * @param code
	 * @return 
	 */
	public AuthAccessToken getAuthAccessToken(String code) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+
				this.appId+"&secret="+this.appSecret+"&code="+code+"&grant_type=authorization_code";
		HttpResponseInfo response = HttpClientFactory.invokeGet(url, null, ENCODE, 10000, 10000);
		String responseContent = response.getContent();
		JSONObject json = JSONObject.fromObject(responseContent);
		
		AuthAccessToken authAccessToken = new AuthAccessToken();
		authAccessToken.setAccessToken(json.getString("access_token"));
		authAccessToken.setDeadLine(System.currentTimeMillis() + json.getLong("expires_in"));
		authAccessToken.setRefreshToken(json.getString("refresh_token"));
		authAccessToken.setOpenid(json.getString("openid"));
		authAccessToken.setScope(json.getString("scope"));
		
		return authAccessToken;
	
	}

	/**
	 * 获取微信用户信息
	 * @param authAccessToken
	 * @return 
	 */
	public static WXUserInfo getWXUserInfo(AuthAccessToken authAccessToken) {
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+authAccessToken.getAccessToken()+"&openid="+authAccessToken.getOpenid()+"&lang=zh_CN";
		HttpResponseInfo response = HttpClientFactory.invokeGet(url, null, ENCODE, 10000, 10000);
		String responseContent = response.getContent();
		JSONObject json = JSONObject.fromObject(responseContent);
		
		WXUserInfo userInfo = new WXUserInfo();
		userInfo.setOpenid(json.getString("openid"));
		userInfo.setNickname(json.getString("nickname"));
		userInfo.setSex(json.getString("sex"));
		userInfo.setProvince(json.getString("province"));
		userInfo.setCity(json.getString("city"));
		userInfo.setCountry(json.getString("country"));
		userInfo.setHeadimageurl(json.getString("headimgurl"));
		JSONArray arr = json.getJSONArray("privilege");
		String[] privilege = new String[arr.size()];
		for(int i=0; i<arr.size(); ++i){
			privilege[i] = arr.getString(i);
		}
		userInfo.setPrivilege(privilege);
		if(json.get("unionid") != null){
			userInfo.setUnionid(json.getString("unionid"));
		}
	
		return userInfo;
	}

	public boolean verifyToken(String signature, String timestamp, String nonce) throws NoSuchAlgorithmException {
		String tmpStr = getSHA1(token, timestamp, nonce);
		if (tmpStr.equals(signature)) {
			return true;
		}
		return false;
	}

	/**
     * 将xml转化为Map集合
     * 
     * @param request
     * @return
     */
    public static Map<String, String> XMLToMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        SAXReader reader = new SAXReader();
        InputStream ins = null;
        try {
            ins = request.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Document doc = null;
        try {
            doc = reader.read(ins);
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
        Element root = doc.getRootElement();
        @SuppressWarnings("unchecked")
        List<Element> list = root.elements();
        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        try {
            ins.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return map;
    }
    
    /**
     * 文本消息转化为xml
     * 
     * @param textMessage
     * @return
     */
    public static String textMessageToXml(TextMessage textMessage) {

    	XStream xStream = SerializeXmlUtil.createXstream();  
    	xStream.processAnnotations(TextMessage.class);  
    	String xml = xStream.toXML(textMessage);
    	return xml;
    }
    
    /**
	 * 判断是否微信浏览器
	 * @param request
	 * @return
	 */
	public static boolean isWX(HttpServletRequest request){
		//判断 是否是微信浏览器
		if(request != null){
			String userAgent = request.getHeader("user-agent");
			if(userAgent != null){
				userAgent = userAgent.toLowerCase();
				if(userAgent.indexOf("micromessenger")>-1){//微信客户端
					return true;
				}
			}
		}
		return false;
	}

	public static void printMap(Map map) {
		Iterator it = map.keySet().iterator();
		while(it.hasNext()){
			Object key = it.next();
			String[] values = (String[]) map.get(key);
			for(int i=0;i<values.length;++i){
				logger.info("======="+key+":"+values[i].toString()+"========");
			}
		}
	}

	// 各种消息类型,除了扫带二维码事件
    /**
     * 文本消息
     */
    public static final String MESSAGE_TEXT = "text";
    /**
     * 图片消息
     */
    public static final String MESSAtGE_IMAGE = "image";
    /**
     * 图文消息
     */
    public static final String MESSAGE_NEWS = "news";
    /**
     * 语音消息
     */
    public static final String MESSAGE_VOICE = "voice";
    /**
     * 视频消息
     */
    public static final String MESSAGE_VIDEO = "video";
    /**
     * 小视频消息
     */
    public static final String MESSAGE_SHORTVIDEO = "shortvideo";
    /**
     * 地理位置消息
     */
    public static final String MESSAGE_LOCATION = "location";
    /**
     * 链接消息
     */
    public static final String MESSAGE_LINK = "link";
    /**
     * 事件推送消息
     */
    public static final String MESSAGE_EVENT = "event";
    /**
     * 事件推送消息中,事件类型，subscribe(订阅)
     */
    public static final String MESSAGE_EVENT_SUBSCRIBE = "subscribe";
    /**
     * 事件推送消息中,事件类型，unsubscribe(取消订阅)
     */
    public static final String MESSAGE_EVENT_UNSUBSCRIBE = "unsubscribe";
    /**
     * 事件推送消息中,上报地理位置事件
     */
    public static final String MESSAGE_EVENT_LOCATION_UP = "LOCATION";
    /**
     * 事件推送消息中,自定义菜单事件,点击菜单拉取消息时的事件推送
     */
    public static final String MESSAGE_EVENT_CLICK = "CLICK";
    /**
     * 事件推送消息中,自定义菜单事件,点击菜单跳转链接时的事件推送
     */
    public static final String MESSAGE_EVENT_VIEW = "VIEW";

	
    public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setAES_KEY(String AES_KEY) {
		this.AES_KEY = AES_KEY;
	}

	/**
	 * 返回微信跳转链接
	 * @param fullUrl
	 * @param state
	 * @return
	 */
	public String getRedirectUrl(String fullUrl, String state) {
		
		fullUrl = URLEncoder.encode(fullUrl);
		logger.info("EncoderUrl:"+fullUrl);
//		https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI
//		&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect
		StringBuffer sb = new StringBuffer();
		sb.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=");
		sb.append(this.appId);
		sb.append("&redirect_uri=");
		sb.append(fullUrl);
		sb.append("&response_type=code&scope=snsapi_base&state=");
//		sb.append("&response_type=code&scope=snsapi_userinfo&state=");
		sb.append("state#wechat_redirect");
		return sb.toString();
	}
}
