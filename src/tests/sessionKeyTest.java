package tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class sessionKeyTest {

	private static final String key = ""; // Add your API key
	private static final String secret = ""; // Add your API secret

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws JDOMException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, JDOMException {

		String token = getToken();
		System.out.println("Token = " + token);

		MessageDigest md = MessageDigest.getInstance("MD5");
		String apiSig = "api_key" + key + "methodauth.getSessiontoken" + token + secret;
		System.out.println("API signature = " + apiSig);

		md.update(apiSig.getBytes());
		byte byteData[] = md.digest();
		//convert the byte to hex format
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		String urlToParse = "http://ws.audioscrobbler.com/2.0/?method=auth.getSession&" + 
				"api_key=" + key + "&api_sig=" + sb.toString() + "&token=" + token;
		
		System.out.println("URL for getting session key = " + urlToParse);
		
		try {
			List<Element> list = getList(urlToParse, "auth.getSession");
			for (int i = 0; i < list.size(); i++) {
				Element node = (Element) list.get(i);
				System.out.println("\nSession Key = " + node.getChildText("key"));
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JDOMException e1) {
			e1.printStackTrace();
		}
	}

	public static String getToken() throws IOException, JDOMException {
		String urlToParse = "http://ws.audioscrobbler.com/2.0/?method=auth.gettoken&" + 
				"api_key=" + key + "&api_sig=" + secret;
		try {
			List<Element> list = getList(urlToParse, "auth.gettoken");
			for (int i = 0; i < list.size();) {
				Element node = (Element) list.get(i);
				return node.getText();
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JDOMException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	private static List<Element> getList(String urlToParse, String method) throws IOException, JDOMException {
		SAXBuilder builder = new SAXBuilder();
		URL website = new URL(urlToParse);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		@SuppressWarnings("resource")
		FileOutputStream fos = new FileOutputStream("request.xml");
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);

		File xmlFile = new File("request.xml");

		Document document = (Document) builder.build(xmlFile);
		Element rootNode = document.getRootElement();

		if (method.equals("tag.gettoptracks")) {
			return rootNode.getChildren("toptracks");
		} else if (method.equals("track.getInfo")) {
			return rootNode.getChildren("track");
		} else if (method.equals("auth.gettoken")) {
			return rootNode.getChildren("token");
		} else if (method.equals("auth.getSession")) {
			return rootNode.getChildren("session");
		} else {
			return null;
		}
	}

}
