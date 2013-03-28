package im.goel.MoodicPlayer;

/**
 * TODO: Consider using LinkedList instead of ArrayList
 * @see JaCo: http://jacomp3player.sourceforge.net/guide/javadocs/jaco/mp3/player/MP3Player.html
 * @see JGroove: http://jgroove.googlecode.com/svn/trunk/javadoc/index.html
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import utils.Moods;


public class MoodicPlayer implements ActionListener {

	/**
	 * @param args
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new MoodicPlayer();
	}

	private List<TrackInfo> allTracks = new ArrayList<TrackInfo>();
	Moods moodObj = new Moods();
	private JFrame frame;
	private JPanel north, south;
	private JProgressBar progress; // TODO: Worry about this later!
	private JComboBox<String> moodSelector;
	private JButton go;

	private MoodicPlayer() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create and show GUI
				initializeFrame();		
				initializeNorth();
				initializeSouth();
				frame.add(north, BorderLayout.NORTH);
				frame.add(south, BorderLayout.SOUTH);
				frame.setVisible(true);
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == go) {
			go.setEnabled(false);
			long start = System.currentTimeMillis();
			String mood = moodSelector.getSelectedItem().toString().toLowerCase();
			List<String> subMoods = moodObj.getSubMoods(mood);

			String key = "6ea9780361e0c7342a8a08a1ad78dfec";

			List<String> allMbids = new ArrayList<String>();

			// START building list of MBID
			for (String tag : subMoods) {
				SAXBuilder builder = new SAXBuilder();
				String urlToParse = "http://ws.audioscrobbler.com/2.0/?method=tag.gettoptracks&" +
							"tag=" + tag + "&api_key=" + key + "&limit=5";
				try {
					URL website = new URL(urlToParse);
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream("request.xml");
					fos.getChannel().transferFrom(rbc, 0, 1 << 24);

					File xmlFile = new File("request.xml");

					Document document = (Document) builder.build(xmlFile);
					Element rootNode = document.getRootElement();
					List<Element> list = rootNode.getChildren("toptracks");

					for (int i = 0; i < list.size(); i++) {
						Element node = (Element) list.get(i);
						List<Element> subList = node.getChildren("track");
						for (int j = 0; j < subList.size(); j++) {
							Element subNode = (Element) subList.get(j);
							String extractedMbid = subNode.getChildText("mbid");
							if (extractedMbid.length() == 36) { // Length of mbid
								allMbids.add(extractedMbid);
							}
						}
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JDOMException e1) {
					e1.printStackTrace();
				}
			}
			// END building list of MBID

			// START building list of Info
			for (String mbid : allMbids) {
				SAXBuilder builder = new SAXBuilder();
				String urlToParse = "http://ws.audioscrobbler.com/2.0/?method=track.getInfo&" + 
								"api_key=" + key + "&mbid=" + mbid;
				try {
					URL website = new URL(urlToParse);
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream("request.xml");
					fos.getChannel().transferFrom(rbc, 0, 1 << 24);
	
					File xmlFile = new File("request.xml");
	
					Document document = (Document) builder.build(xmlFile);
					Element rootNode = document.getRootElement();
					List<Element> trackList = rootNode.getChildren("track");
					for (int i = 0; i < trackList.size(); i++) {
						Element node = (Element) trackList.get(i);
						String name = node.getChildText("name");
						int playCount = Integer.parseInt(node.getChildText("playcount"));
						int listeners = Integer.parseInt(node.getChildText("listeners"));
						List<Element> artistList = node.getChildren("artist");
						for (int j = 0; j < artistList.size(); j++) {
							Element artistElement = (Element) artistList.get(j);
							allTracks.add(new TrackInfo(name, artistElement.getChildText("name"), 
									mbid, playCount, listeners));
						}
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JDOMException e1) {
					e1.printStackTrace();
				}
			}
			// END building list of Info

			long end = System.currentTimeMillis();
			System.out.println(end - start + " ms");
			
			System.out.println(allTracks.toString());
		}
	}	

	//********************** BUILD GUI **********************//
	private void initializeFrame() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(300, 80));
		frame.setLocation(new Point(400, 300));
		frame.setTitle("Moodic Player");
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
	}

	private void initializeNorth() {
		north = new JPanel(new GridLayout(1, 3));
		moodSelector = new JComboBox<String>(moodObj.getMoods());
		go = new JButton("< Play >");
		go.addActionListener(this);
		progress = new JProgressBar(1, 100);
		north.add(progress);
		north.add(moodSelector);
		north.add(go);
	}

	private void initializeSouth() {
		south = new JPanel(new BorderLayout(1, 2));
		south.add(new JLabel("Created by Karan Goel || http://www.goel.im"));
	}
	//********************** BUILD GUI **********************//

}