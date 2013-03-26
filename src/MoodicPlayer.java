/**
 * @see API docs: https://developers.google.com/youtube/2.0/developers_guide_java
 * @see GWT2SWF: http://code.google.com/p/gwt2swf/wiki/Usage
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import utils.Langs;
import utils.Moods;

import com.google.gdata.client.Query;
import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.Category;
import com.google.gdata.data.media.mediarss.MediaPlayer;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaContent;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.data.youtube.YouTubeNamespace;
import com.google.gdata.util.ServiceException;


public class MoodicPlayer implements ActionListener {

	/**
	 * @param args
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ServiceException {
		new MoodicPlayer();
	}

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
				addEverything();
				frame.setVisible(true);
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == go) {
			String mood = moodSelector.getSelectedItem().toString().toLowerCase();
			String lang = langSelector.getSelectedItem().toString().toLowerCase();
			try {
				find(mood, lang);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}
		}
	}	

	public void find(String mood, String lang) throws IOException, ServiceException {
		YouTubeQuery query = new YouTubeQuery(new URL("http://gdata.youtube.com/feeds/api/videos"));
		YouTubeService service = new YouTubeService(null);

		// a category filter holds a collection of categories to limit the search
		Query.CategoryFilter categoryFilter1 = new Query.CategoryFilter();
		Query.CategoryFilter categoryFilter2 = new Query.CategoryFilter();
		Query.CategoryFilter categoryFilter3 = new Query.CategoryFilter();

		//this restricts to videos tagged with the keywords "sports" and "football"
		categoryFilter1.addCategory(new Category(YouTubeNamespace.KEYWORD_SCHEME, mood));
		categoryFilter3.addCategory(new Category(YouTubeNamespace.KEYWORD_SCHEME, lang));
		//this restricts to videos in the category of "News".
		categoryFilter2.addCategory(new Category(YouTubeNamespace.CATEGORY_SCHEME, "Music"));

		// multiple filters mean "AND" in a category query
		query.addCategoryFilter(categoryFilter1);
		query.addCategoryFilter(categoryFilter2);
		query.addCategoryFilter(categoryFilter3);

		VideoFeed videoFeed = service.query(query, VideoFeed.class);
		printEntireVideoFeed(service, videoFeed);
	}

	// fo' pagination
	public static void printEntireVideoFeed(YouTubeService service, VideoFeed videoFeed) throws MalformedURLException, 
	IOException, ServiceException {
		do {
			printVideoFeed(videoFeed);
			if(videoFeed.getNextLink() != null) {
				videoFeed = service.getFeed(new URL(videoFeed.getNextLink().getHref()), 
						VideoFeed.class);
			}
			else {
				videoFeed = null;
			}
		}
		while(videoFeed != null);
	}

	// prints feed from 1 single page (videofeed)
	public static void printVideoFeed(VideoFeed videoFeed) {
		for(VideoEntry videoEntry : videoFeed.getEntries() ) {
			printVideoEntry(videoEntry);
		}
	}

	// print details about single video
	public static void printVideoEntry(VideoEntry videoEntry) {
		System.out.println("Title: " + videoEntry.getTitle().getPlainText());

		YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();

		System.out.println("Video ID: " + mediaGroup.getVideoId());

		MediaPlayer mediaPlayer = mediaGroup.getPlayer();
		System.out.println("Web Player URL: " + mediaPlayer.getUrl());

		System.out.println("Media:");
		YouTubeMediaContent mediaContent = mediaGroup.getYouTubeContents().get(0);
		System.out.println("\tMedia Location: "+ mediaContent.getUrl());
		System.out.println("\tMedia Type: "+ mediaContent.getType());
		System.out.println("Duration: " + mediaContent.getDuration());


		int i = 0;
		for(MediaThumbnail mediaThumbnail : mediaGroup.getThumbnails()) {
			if (i >= 3) {
				System.out.println("Thumbnail URL: " + mediaThumbnail.getUrl());
				System.out.println("Thumbnail Time Index: " + mediaThumbnail.getTime());
				System.out.println();
				break;
			}
			i++;
		}

	}

	//********************** BUILD GUI **********************//
	private JFrame frame;
	private JPanel north, south;
	private JTextArea temp;
	private JComboBox<String> moodSelector, langSelector;
	private JButton go;

	private void initializeFrame() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(650, 500));
		frame.setLocation(new Point(400, 300));
		frame.setTitle("Moodic Player");
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
	}

	private void initializeNorth() {
		north = new JPanel(new GridLayout(1, 3));
		Langs lang = new Langs();
		Moods mood = new Moods();
		moodSelector = new JComboBox<String>(lang.getLangs());
		moodSelector.addActionListener(this);
		langSelector = new JComboBox<String>(mood.getMoods());
		langSelector.addActionListener(this);
		go = new JButton("< Play >");
		go.addActionListener(this);
		north.add(moodSelector);
		north.add(langSelector);
		north.add(go);
	}

	private void initializeSouth() {
		south = new JPanel(new GridLayout(1, 1));
		temp = new JTextArea();
		south.add(temp);
	}

	private void addEverything() {
		frame.add(north, BorderLayout.NORTH);
		frame.add(south, BorderLayout.SOUTH);
	}
	//********************** BUILD GUI **********************//

}