package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class PlaylistIDTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int playlistID = -1;
		List<Element> list = null;
		list = getList(null, "playlist.addTrack");
		for (int i = 0; i < list.size(); i++) {
			Element node = (Element) list.get(i);
			List<Element> subList = node.getChildren("playlist");
			for (int j = 0; j < subList.size(); j++) {
				Element subNode = (Element) subList.get(j);
				playlistID = Integer.parseInt(subNode.getChildText("id"));
			}
		}
		System.out.println(playlistID);
	}


	private static List<Element> getList(String urlToParse, String method) {
		if (urlToParse != null) {
			URL website = null;
			try {
				website = new URL(urlToParse);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			ReadableByteChannel rbc = null;
			try {
				rbc = Channels.newChannel(website.openStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream("request.xml");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				fos.getChannel().transferFrom(rbc, 0, 1 << 24);
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File xmlFile = new File("request.xml");

		SAXBuilder builder = new SAXBuilder();
		Document document = null;
		try {
			document = (Document) builder.build(xmlFile);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element rootNode = document.getRootElement();

		if (method.equals("tag.gettoptracks")) {
			return rootNode.getChildren("toptracks");
		} else if (method.equals("track.getInfo")) {
			return rootNode.getChildren("track");
		} else if (method.equals("auth.gettoken")) {
			return rootNode.getChildren("token");
		} else if (method.equals("auth.getSession")) {
			return rootNode.getChildren("session");
		} else if (method.equals("playlist.addTrack")) {
			return rootNode.getChildren("playlists");
		} else {
			return null;
		}
	}

}
