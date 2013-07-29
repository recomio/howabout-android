package io.recom.howabout.category.music.net;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

// node-grooveshark-streaming library porting. 
// https://npmjs.org/package/grooveshark-streaming
public class GroovesharkStreamUrlGetter {

	public interface OnGetGroovesharkStreamKey {
		void sucess(String streamUrl);

		void error(Exception e);
	}

	Context context;
	AQuery aq;

	MessageDigest md5;
	MessageDigest sha1;

	String songId;
	UUID uuid;
	String sessionId;
	String country;

	OnGetGroovesharkStreamKey onGetGroovesharkStreamKey;

	public GroovesharkStreamUrlGetter(Context context, String songId,
			OnGetGroovesharkStreamKey onGroovesharkStreamKey)
			throws NoSuchAlgorithmException {
		this.context = context;
		this.aq = new AQuery(context);
		this.songId = songId;
		this.onGetGroovesharkStreamKey = onGroovesharkStreamKey;

		this.md5 = MessageDigest.getInstance("MD5");
		this.sha1 = MessageDigest.getInstance("SHA1");

		this.sessionId = null;
		this.country = null;
	}

	final protected static char[] hexArray = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	protected String generateRandomString(int length) {
		Random random = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();

		for (int i = 0; i < length; i++) {
			randomStringBuilder
					.append(Integer.toHexString(random.nextInt(0x10)));
		}

		return randomStringBuilder.toString();
	}

	public void getGroovesharkStreamUrlAsync() {
		uuid = UUID.randomUUID();

		String groovesharkUrl = "http://html5.grooveshark.com/";

		AjaxCallback<String> groovesharkMainPageAjaxCallback = new AjaxCallback<String>();
		groovesharkMainPageAjaxCallback.url(groovesharkUrl).type(String.class)
				.handler(this, "groovesharkMainPageCallback");

		groovesharkMainPageAjaxCallback
				.header("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		groovesharkMainPageAjaxCallback.header("Accept-Charset",
				"UTF-8,*;q=0.5");
		groovesharkMainPageAjaxCallback.header("Accept-Language",
				"en-US,en;q=0.8");
		groovesharkMainPageAjaxCallback.header("Cache-Control", "max-age=0");
		groovesharkMainPageAjaxCallback.header("Connection", "keep-alive");
		groovesharkMainPageAjaxCallback.header("Host", "html5.grooveshark.com");
		groovesharkMainPageAjaxCallback
				.header("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");

		aq.ajax(groovesharkMainPageAjaxCallback);
	}

	public void groovesharkMainPageCallback(String url, String body,
			AjaxStatus status) {
		if (status.getCode() != 200) {
			onGetGroovesharkStreamKey.error(new Exception(
					"Failed to get grooveshark stream url."));
			return;
		}

		try {
			parseGroovesharkMainPage(body);
		} catch (Exception e) {
			onGetGroovesharkStreamKey.error(e);
		}
	}

	protected void parseGroovesharkMainPage(String body) throws JSONException,
			UnsupportedEncodingException {
		Pattern sessionIdPattern = Pattern
				.compile("(?:\"sessionID\":)\"[\\w]+");
		Matcher sessionIdMatcher = sessionIdPattern.matcher(body);
		while (sessionIdMatcher.find()) {
			sessionId = sessionIdMatcher.group().substring(13);
		}

		Pattern countryPattern = Pattern.compile("(?:\"country\":)\\{(.)*?\\}");
		Matcher countryMatcher = countryPattern.matcher(body);
		while (countryMatcher.find()) {
			country = countryMatcher.group().substring(10);
		}

		String secretKey = bytesToHex(md5.digest(sessionId.getBytes()));

		JSONObject headerObject = new JSONObject();
		headerObject.put("client", "mobileshark");
		headerObject.put("clientRevision", "20120830");
		headerObject.put("privacy", "0");
		headerObject.put("country", country);
		headerObject.put("uuid", uuid);
		headerObject.put("session", sessionId);

		JSONObject parametersObject = new JSONObject();
		parametersObject.put("secretKey", secretKey);

		JSONObject bodyObject = new JSONObject();
		bodyObject.put("header", headerObject);
		bodyObject.put("method", "getCommunicationToken");
		bodyObject.put("parameters", parametersObject);

		String groovesharkGetCommunicationTokenUrl = "https://html5.grooveshark.com/more.php?getCommunicationToken";

		AjaxCallback<JSONObject> groovesharkGetCommunicationTokenAjaxCallback = new AjaxCallback<JSONObject>();
		groovesharkGetCommunicationTokenAjaxCallback
				.url(groovesharkGetCommunicationTokenUrl)
				.type(JSONObject.class)
				.handler(this, "groovesharkGetCommunicationTokenCallback");

		HttpEntity bodyEntity = new StringEntity(bodyObject.toString());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(AQuery.POST_ENTITY, bodyEntity);

		groovesharkGetCommunicationTokenAjaxCallback.params(params);

		groovesharkGetCommunicationTokenAjaxCallback.header("Content-Type",
				"application/json");
		groovesharkGetCommunicationTokenAjaxCallback
				.header("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		groovesharkGetCommunicationTokenAjaxCallback.header("Accept-Charset",
				"UTF-8,*;q=0.5");
		groovesharkGetCommunicationTokenAjaxCallback.header("Accept-Language",
				"en-US,en;q=0.8");
		groovesharkGetCommunicationTokenAjaxCallback.header("Cache-Control",
				"max-age=0");
		groovesharkGetCommunicationTokenAjaxCallback.header("Connection",
				"keep-alive");
		groovesharkGetCommunicationTokenAjaxCallback.header("Host",
				"html5.grooveshark.com");
		groovesharkGetCommunicationTokenAjaxCallback
				.header("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");

		aq.ajax(groovesharkGetCommunicationTokenAjaxCallback);
	}

	public void groovesharkGetCommunicationTokenCallback(String url,
			JSONObject jsonResponseObject, AjaxStatus status) {
		if (status.getCode() != 200) {
			onGetGroovesharkStreamKey.error(new Exception(
					"Failed to get grooveshark stream url."));
			return;
		}

		try {
			parseGroovesharkGetCommunicationToken(jsonResponseObject);
		} catch (Exception e) {
			onGetGroovesharkStreamKey.error(e);
		}
	}

	public void parseGroovesharkGetCommunicationToken(
			JSONObject jsonResponseObject) throws JSONException,
			UnsupportedEncodingException {
		String lastRandomizer = generateRandomString(6);
		String communicationToken = jsonResponseObject.getString("result");

		String tokenSource = "getStreamKeyFromSongIDEx:" + communicationToken
				+ ":gooeyFlubber:" + lastRandomizer;
		String token = lastRandomizer
				+ bytesToHex(sha1.digest(tokenSource.getBytes()));

		JSONObject headerObject = new JSONObject();
		headerObject.put("client", "mobileshark");
		headerObject.put("clientRevision", "20120830");
		headerObject.put("privacy", "0");
		headerObject.put("country", country);
		headerObject.put("uuid", uuid);
		headerObject.put("session", sessionId);
		headerObject.put("token", token);

		JSONObject parametersObject = new JSONObject();
		parametersObject.put("prefetch", false);
		parametersObject.put("mobile", true);
		parametersObject.put("songID", songId);
		parametersObject.put("country", country);

		JSONObject bodyObject = new JSONObject();
		bodyObject.put("header", headerObject);
		bodyObject.put("method", "getStreamKeyFromSongIDEx");
		bodyObject.put("parameters", parametersObject);

		String groovesharkGetStreamKeyUrl = "https://html5.grooveshark.com/more.php?getStreamKeyFromSongIDEx";

		AjaxCallback<JSONObject> groovesharkGetStreamKeyAjaxCallback = new AjaxCallback<JSONObject>();
		groovesharkGetStreamKeyAjaxCallback.url(groovesharkGetStreamKeyUrl)
				.type(JSONObject.class)
				.handler(this, "groovesharkGetStreamKeyCallback");

		HttpEntity bodyEntity = new StringEntity(bodyObject.toString());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(AQuery.POST_ENTITY, bodyEntity);

		groovesharkGetStreamKeyAjaxCallback.params(params);

		groovesharkGetStreamKeyAjaxCallback.header("Content-Type",
				"application/json");
		groovesharkGetStreamKeyAjaxCallback
				.header("Accept", "application/json");
		groovesharkGetStreamKeyAjaxCallback.header("Accept-Charset",
				"ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		groovesharkGetStreamKeyAjaxCallback.header("Accept-Language",
				"en-US,en;q=0.8");
		groovesharkGetStreamKeyAjaxCallback.header("Cache-Control", "no-cache");
		groovesharkGetStreamKeyAjaxCallback.header("Connection", "keep-alive");
		groovesharkGetStreamKeyAjaxCallback.header("Host",
				"html5.grooveshark.com");
		groovesharkGetStreamKeyAjaxCallback
				.header("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");

		aq.ajax(groovesharkGetStreamKeyAjaxCallback);
	}

	public void groovesharkGetStreamKeyCallback(String url,
			JSONObject jsonResponseObject, AjaxStatus status) {
		if (status.getCode() != 200) {
			onGetGroovesharkStreamKey.error(new Exception(
					"Failed to get grooveshark stream url."));
			return;
		}

		try {
			parseGroovesharkGetSteramKey(jsonResponseObject);
		} catch (Exception e) {
			onGetGroovesharkStreamKey.error(e);
		}
	}

	public void parseGroovesharkGetSteramKey(JSONObject jsonResponseObject)
			throws JSONException {
		JSONObject resultObject = jsonResponseObject.getJSONObject("result");

		String host = resultObject.getString("ip");
		String streamKey = resultObject.getString("streamKey");
		String streamUrl = "http://" + host + "/stream.php?streamKey="
				+ streamKey;

		onGetGroovesharkStreamKey.sucess(streamUrl);
	}
}
