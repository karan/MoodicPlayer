package tests;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Scanner;

public class PlaylistCreateTest {

	private static final String key = ""; // Add your API key
	private static final String secret = ""; // Add your API secret
	private static final String sessionKey = ""; // Add your session key
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println("Starting test for creating playlist...");
		buildPlaylist();
	}
	
	private static void buildPlaylist() throws UnsupportedEncodingException {

		String mood = "Happy";
		System.out.println("\nMood is " + mood + "\n\n");
		MessageDigest md = null;
		String title = URLEncoder.encode(mood + " " + new Date().getTime(), "UTF-8");
		String description = URLEncoder.encode("For when you are " + mood + ". Created by MoodicPlayer.", "UTF-8");
		
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String apiSig = "api_key" + key + "description" + description + "methodplaylist.createsk" + 
					sessionKey + "title" + title + secret;
		md.update(apiSig.getBytes());
		byte byteData[] = md.digest();
		//convert the byte to hex format
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		String hashedSig = sb.toString();

		// FOR DEBUGGING
		System.out.println("api_key = " + key);
		System.out.println("api_sig = " + hashedSig);
		System.out.println("session key = " + sessionKey);
		// FOR DEBUGGING

		String urlParameters = null;
		urlParameters = "method=playlist.create&api_key="+ key + "&api_sig=" + hashedSig +
				"&description=" + description + "&sk=" + sessionKey + "&title=" + title;
		String request = "http://ws.audioscrobbler.com/2.0/";

		URL url = null;
		try {
			url = new URL(request);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}           
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false); 
		try {
			connection.setRequestMethod("POST");
		} catch (ProtocolException e) {
			e.printStackTrace();
		} 
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
		connection.setRequestProperty("User-Agent", "MoodicPlayer http://www.goel.im"); 
		connection.setUseCaches(false);

		DataOutputStream wr = null;
		try {
			wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream is = null;
		Scanner s = null;
		try {
			if (connection.getResponseCode() != 200) {
				s = new Scanner(connection.getErrorStream());
			} else {
				is = connection.getInputStream();
				s = new Scanner(is);
			}
			s.useDelimiter("\\Z");
			String response = s.next();
			System.out.println("\nResponse: " + response + "\n\n");
			BufferedWriter out = new BufferedWriter(new FileWriter("requestCreate.xml"));
	        out.write(response);
	        out.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		// FOR DEBUGGING
		try {
			System.out.println("Response Code: " + connection.getResponseCode());
			System.out.println("Response Message: " + connection.getResponseMessage()); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		// FOR DEBUGGING
		connection.disconnect();
	}

}
