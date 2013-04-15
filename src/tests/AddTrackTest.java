package tests;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


public class AddTrackTest {

	private static final String key = ""; // Add your API key
	private static final String secret = ""; // Add your API secret
	private static final String sessionKey = ""; // Add your session key

	public static void main(String[] args) {
		System.out.println("Starting test for adding tracks to playlist...");
		int playlistID = -1;
		List<Element> list = null;
		try {
			list = getList();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < list.size(); i++) {
			Element node = (Element) list.get(i);
			List<Element> subList = node.getChildren("playlist");
			for (int j = 0; j < subList.size(); j++) {
				Element subNode = (Element) subList.get(j);
				playlistID = Integer.parseInt(subNode.getChildText("id"));
				System.out.println(playlistID);
			}
		}
		System.out.println("Trying to add tracks to playlist...");
		addTracksToPlaylist(playlistID);
	}

	private static List<Element> getList() throws JDOMException, IOException {
		File xmlFile = new File("request.xml");
		SAXBuilder builder = new SAXBuilder();
		Document document = (Document) builder.build(xmlFile);
		Element rootNode = document.getRootElement();
		return rootNode.getChildren("playlists");
	}

	private static void addTracksToPlaylist(int playlistID) {
		String track = "Paradise";
		String artist = "Coldplay";

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String apiSig = "api_key" + key + "artist" + artist + "methodplaylist.addTrackplaylistID" + 
				playlistID + "sk" + sessionKey + "track" + track + secret;
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
		urlParameters = "method=playlist.addTrack" + "&api_key=" + key + "&artist=" + artist + "&playlistID=" + 
				playlistID + "&sk=" + sessionKey + "&track=" + track + "&api_sig=" + hashedSig;

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
		
		// FOR DEBUGGING
		try {
			System.out.println("Response Code: " + connection.getResponseCode());
			System.out.println("Response Message: " + connection.getResponseMessage()); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		// FOR DEBUGGING


//		InputStream is = connection.getInputStream();
//		//Use transformer to transform
//		TransformerFactory transFactory = TransformerFactory.newInstance();
//		Transformer t= transFactory.newTransformer();
//		t.setOutputProperty(OutputKeys.METHOD, "xml");
//		t.setOutputProperty(OutputKeys.INDENT,"yes");                
//		Source input = new StreamSource(is);
//		Result output = new StreamResult(new FileOutputStream("requestAdd.xml"));
//		transFactory.newTransformer().transform(input, output);

		connection.disconnect();
	}
}
