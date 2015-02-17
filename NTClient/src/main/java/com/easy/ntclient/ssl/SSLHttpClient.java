package com.easy.ntclient.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SSLHttpClient {
	private static String CLIENT_KEY_STORE = "sslclientkeys";
	private static String CLIENT_TRUST_KEY_STORE = "sslclienttrust";
	private static String CLIENT_KEY_STORE_PASSWORD = "client";
	private static String CLIENT_TRUST_KEY_STORE_PASSWORD = "client";
	private static String CLIENT_KEY_PASS = "client";
	private CloseableHttpClient httpclient;

	public SSLHttpClient(){
		init();
	}
	private void init() {
		InputStream instream = null;
		InputStream keyStoreInput = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			instream = SSLHttpClient.class
					.getResourceAsStream(CLIENT_TRUST_KEY_STORE);
			trustStore.load(instream,
					CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());

			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStoreInput = SSLHttpClient.class
					.getResourceAsStream(CLIENT_KEY_STORE);
			keyStore.load(keyStoreInput,
					CLIENT_KEY_STORE_PASSWORD.toCharArray());

			// Trust own CA and all self-signed certs
			SSLContext sslcontext = SSLContexts
					.custom()
					.loadTrustMaterial(trustStore,
							new TrustSelfSignedStrategy())
					.loadKeyMaterial(keyStore, CLIENT_KEY_PASS.toCharArray())
					.build();
			// Allow TLSv1 protocol only
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext,
					new String[] { "SSLv3" },
					null,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)
					.build();
		} catch (Exception e) {

		} finally {
			try {
				if (instream != null) {
					instream.close();
				}
				if (keyStoreInput != null) {
					keyStoreInput.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public String get(String url){
		String ret=null;
		CloseableHttpResponse response=null;
			HttpGet httpPost = new HttpGet(url);
			//httpPost.setHeader("keepalive","false");
			httpPost.setHeader("Connection", "close");  
			try {
				response = httpclient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					ret = EntityUtils.toString(entity, "UTF-8");
				}
				EntityUtils.consume(entity);
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		return ret;
	}
	public final static void main(String[] args) throws Exception {

		SSLHttpClient client = new SSLHttpClient();
		String ret = client.get("https://127.0.0.1:8888/aa?name=1aaaaa");
		System.out.println(ret); 
	}
}
