/*******************************************************
 * 
 * Karan Goel, 2013
 * karan@goel.im
 * 
 * TODO: Write this comment!
 * 
 *******************************************************/

package im.goel.MoodicPlayer;

/**
 * @see JaCo: http://jacomp3player.sourceforge.net/guide/javadocs/jaco/mp3/player/MP3Player.html
 * @see JGroove: http://jgroove.googlecode.com/svn/trunk/javadoc/index.html
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import utils.Moods;


public class MoodicPlayer implements ActionListener, PropertyChangeListener {

	public static void main(String[] args) throws IOException {
		new MoodicPlayer();
	}

	private List<TrackInfo> allTracks; // Stores all tracks found for all moods
	private List<String> mbids; // Stores mbids found for all moods
	
	private Moods moodObj = new Moods();
	private JFrame frame;
	private JPanel north, south;
	private JProgressBar progress;
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

	/**
	 * Keeps track of events on GUI elements.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == go) {
			// Main button is pressed
			progress.setIndeterminate(true);
			go.setEnabled(false); // disable button
			Task task = new Task();
			task.addPropertyChangeListener(this);
			task.execute(); // start the magic!
		}
	}

	/**
	 * Keeps track of the progress to notify the progress bar
	 * to change its value appropriately.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if ("progress" == e.getPropertyName()) {
			int p = (Integer) e.getNewValue();
			progress.setIndeterminate(false); // Because we know how much change we need
			progress.setValue(p * (100 / mbids.size()) + (100 % mbids.size()));
		}
	}

	//********************** BUILD GUI **********************//
	/**
	 * Initializes the main frame and sets its properties.
	 */
	private void initializeFrame() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(300, 110));
		frame.setLocation(new Point(400, 300));
		frame.setTitle("Moodic Player");
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
	}

	/**
	 * Initializes the north panel and sets its properties.
	 */
	private void initializeNorth() {
		north = new JPanel(new GridLayout(1, 3));
		moodSelector = new JComboBox<String>(moodObj.getMoods());
		go = new JButton("Play");
		go.addActionListener(this);
		progress = new JProgressBar(0, 100);
		progress.setValue(0);
		north.add(moodSelector);
		north.add(go);
		north.add(progress);
		north.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * Initializes the south panel and sets its properties. 
	 */
	private void initializeSouth() {
		south = new JPanel(new BorderLayout(1, 2));
		south.add(new JLabel("Created by Karan Goel || http://www.goel.im"));
		south.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
	}
	//********************** BUILD GUI **********************//


	/**
	 * This class is meant to process all things in a background
	 * thread.
	 * @author Karan Goel
	 */
	public class Task extends SwingWorker<Void, Object> {

		private static final String LIMIT = "5"; // number of tracks per tag to find

		@Override
		/**
		 * Everything in this method is done in a separate thread
		 * and not EDT!
		 */
		protected Void doInBackground() {
			//System.out.println(javax.swing.SwingUtilities.isEventDispatchThread());
			String mood = moodSelector.getSelectedItem().toString().toLowerCase();
			List<String> subMoods = moodObj.getSubMoods(mood);
			String key = "3a5d7dcb68d7fb252365acc1ed4eb32d";
			
			mbids = getAllMbids(subMoods, key); // Find all mbids for given moods
			int currentProgress = 0;
			int maxProgress = mbids.size(); // Important so we don't loop unnecessarily
			setProgress(1);
			
			allTracks = new ArrayList<TrackInfo>();
			while (currentProgress < maxProgress) {
				// Make progress.
				// Run this once for ever mbid
				getTrackInfoForMbid(mbids.get(currentProgress), key);
				currentProgress = allTracks.size();
				setProgress(Math.min(currentProgress, maxProgress));
			}
			return null;
		}

		/**
		 * This method runs in EDT after background thread is completed.
		 */
		public void done() {
			//System.out.println(javax.swing.SwingUtilities.isEventDispatchThread());
			go.setEnabled(true);
			System.out.println(allTracks.toString());
		}

		/**
		 * Returns a list of string of mbids for upto LIMIT tracks for each
		 * tag (submood)
		 * @param subMoods - a list of moods
		 * @param key - last.fm API key
		 * @return list of string of mbids for upto LIMIT tracks for each
		 * tag (submood)
		 */
		private List<String> getAllMbids(List<String> subMoods, String key) {
			// START building list of MBID
			List<String> allMbids = new ArrayList<String>();
			for (String tag : subMoods) {
				String urlToParse = "http://ws.audioscrobbler.com/2.0/?method=tag.gettoptracks&" +
						"tag=" + tag + "&api_key=" + key + "&limit=" + LIMIT;
				try {
					List<Element> list = getList(urlToParse, "tag.gettoptracks");
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
			return allMbids;
		}

		/**
		 * For a given mbid, find the required information, and store
		 * it as a TrackInfo object in the list.
		 * @param mbid of track to find information
		 * @param key - last.fm API key
		 */
		private void getTrackInfoForMbid(String mbid, String key) {
			// START building list of Info
			String urlToParse = "http://ws.audioscrobbler.com/2.0/?method=track.getInfo&" + 
					"api_key=" + key + "&mbid=" + mbid;
			try {
				List<Element> trackList = getList(urlToParse, "track.getInfo");
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
			// END building list of Info
		}

		/**
		 * Required for JDOM. Returns a list of Element's to be parser
		 * found in the given URL.
		 */
		private List<Element> getList(String urlToParse, String method) throws IOException, JDOMException {
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
			} else {
				return rootNode.getChildren("track");
			}
		}

	}

}