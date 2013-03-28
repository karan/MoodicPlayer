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

	/**
	 * @param args
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new MoodicPlayer();
	}

	private List<TrackInfo> allTracks;
	private List<String> mbids;
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

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == go) {
			progress.setIndeterminate(true);
			go.setEnabled(false);
			Task task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();
		}
	}

	public void propertyChange(PropertyChangeEvent e) {
		if ("progress" == e.getPropertyName()) {
			int p = (Integer) e.getNewValue();
			progress.setIndeterminate(false);
			System.out.println(p * (100 / mbids.size()) + (100 % mbids.size()));
			progress.setValue(p * (100 / mbids.size()) + (100 % mbids.size()));
		}
	}

	//********************** BUILD GUI **********************//
	private void initializeFrame() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(300, 110));
		frame.setLocation(new Point(400, 300));
		frame.setTitle("Moodic Player");
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
	}

	private void initializeNorth() {
		north = new JPanel(new GridLayout(1, 3));
		moodSelector = new JComboBox<String>(moodObj.getMoods());
		go = new JButton("Play");
		go.addActionListener(this);
		initializeProgress();
		north.add(moodSelector);
		north.add(go);
		north.add(progress);
		north.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	private void initializeProgress() {
		progress = new JProgressBar(0, 100);
		progress.setValue(0);
	}

	private void initializeSouth() {
		south = new JPanel(new BorderLayout(1, 2));
		south.add(new JLabel("Created by Karan Goel || http://www.goel.im"));
		south.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
	}
	//********************** BUILD GUI **********************//


	public class Task extends SwingWorker<Void, Object> {

		@Override
		protected Void doInBackground() {
			String mood = moodSelector.getSelectedItem().toString().toLowerCase();
			List<String> subMoods = moodObj.getSubMoods(mood);
			String key = "3a5d7dcb68d7fb252365acc1ed4eb32d";
			mbids = getAllMbids(subMoods, key);
			int currentProgress = 0;
			int maxProgress = mbids.size();
			//Initialize progress property.
			setProgress(1);
			allTracks = new ArrayList<TrackInfo>();
			while (currentProgress < maxProgress) {
				// Make progress.
				// Run this once for ever mbid
				getAllTracksForMbid(mbids.get(currentProgress), key);
				currentProgress = allTracks.size();
				setProgress(Math.min(currentProgress, maxProgress));
			}
			return null;
		}

		public void done() {
			go.setEnabled(true);
			System.out.println(allTracks.toString());
		}

		private List<String> getAllMbids(List<String> subMoods, String key) {
			// START building list of MBID
			List<String> allMbids = new ArrayList<String>();
			for (String tag : subMoods) {
				String urlToParse = "http://ws.audioscrobbler.com/2.0/?method=tag.gettoptracks&" +
						"tag=" + tag + "&api_key=" + key + "&limit=5";
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

		private void getAllTracksForMbid(String mbid, String key) {
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

		private List<Element> getList(String urlToParse, String method) throws IOException, JDOMException {
			SAXBuilder builder = new SAXBuilder();
			URL website = new URL(urlToParse);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
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