package im.goel.MoodicPlayer;

/**
 * TODO Use GrooveShark. Sort on genre and mood
 * HAPPY: excited, glad, joyful, cheerful, exciting
 * SAD: gloomy, blue, depressed, hopeless
 * RELAXED: calm, dreamy, serene, tranquil
 * ANGRY: agressive, furious, disturbing, distress 
 */

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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import com.google.gdata.data.youtube.YtStatistics;
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
	
	private List<String> listOfIds;
	private JFrame frame;
	private JPanel north, center, south;
	private JLabel tempLabel;
	private JProgressBar progress;
	private JComboBox<String> moodSelector, langSelector;
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
				initializeCenter();
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
				listOfIds = new ArrayList<String>();
				find(mood, lang);
				System.out.println(listOfIds.toString());
				tempLabel.setText("hello");
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}
		}
	}	

	public void find(String mood, String lang) throws IOException, ServiceException {
		YouTubeQuery query = new YouTubeQuery(new URL("http://gdata.youtube.com/feeds/api/videos"));
		query.setOrderBy(YouTubeQuery.OrderBy.VIEW_COUNT);
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
		query.addCategoryFilter(categoryFilter3);
		query.addCategoryFilter(categoryFilter2);
		
		VideoFeed videoFeed = service.query(query, VideoFeed.class);
		printEntireVideoFeed(service, videoFeed);
	}

	// for pagination
	public void printEntireVideoFeed(YouTubeService service, VideoFeed videoFeed) throws MalformedURLException, 
	IOException, ServiceException {
		listOfIds.addAll(printVideoFeed(videoFeed));
	}

	// prints feed from 1 single page (videofeed)
	public List<String> printVideoFeed(VideoFeed videoFeed) {
		List<String> singlePage = new ArrayList<String>();
		for(VideoEntry videoEntry : videoFeed.getEntries() ) {
			String entryID = printVideoEntry(videoEntry);
			if (entryID != "") {
				singlePage.add(entryID);
			}
		}
		return singlePage;
	}

	// print details about single video
	public String printVideoEntry(VideoEntry videoEntry) {
		String entryID = "";
		YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
		//MediaPlayer mediaPlayer = mediaGroup.getPlayer();
		YouTubeMediaContent mediaContent = mediaGroup.getYouTubeContents().get(0);
		
		YtStatistics stats = videoEntry.getStatistics();
		if (mediaContent.getDuration() >= 240 && stats.getViewCount() >= 50000) {
			entryID = mediaGroup.getVideoId();
		}
		return entryID;
		
//		if (mediaContent.getDuration() >= 240) {
//			System.out.println("Title: " + videoEntry.getTitle().getPlainText());
//			System.out.println("Video ID: " + mediaGroup.getVideoId());
//			
//			System.out.println("Web Player URL: " + mediaPlayer.getUrl());
//			System.out.println("Media:");
//			System.out.println("\tMedia Location: "+ mediaContent.getUrl());
//			System.out.println("\tMedia Type: "+ mediaContent.getType());
//			System.out.println("Duration: " + mediaContent.getDuration());
//	
//			int i = 0;
//			for(MediaThumbnail mediaThumbnail : mediaGroup.getThumbnails()) {
//				if (i >= 3) {
//					System.out.println("Thumbnail URL: " + mediaThumbnail.getUrl());
//					System.out.println("Thumbnail Time Index: " + mediaThumbnail.getTime());
//					System.out.println();
//					break;
//				}
//				i++;
//			}
//			String embedURL = "<html><iframe title=\"YouTube video player\" class=\"youtube-player\" type=\"text/html\" width=\"640\"" + 
//						" height=\"390\" src=\"http://www.youtube.com/embed/" + mediaGroup.getVideoId() + " frameborder=\"0\" allowFullScreen></iframe></html>";
//			System.out.println(embedURL);
//		}
	}

	//********************** BUILD GUI **********************//
	private void initializeFrame() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(650, 450));
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
		moodSelector.setPreferredSize(new Dimension(200, 25));
		moodSelector.addActionListener(this);
		langSelector = new JComboBox<String>(mood.getMoods());
		langSelector.setPreferredSize(new Dimension(200, 25));
		langSelector.addActionListener(this);
		go = new JButton("< Play >");
		go.setPreferredSize(new Dimension(240, 25));
		go.addActionListener(this);
		north.add(moodSelector);
		north.add(langSelector);
		north.add(go);
	}
	
	private void initializeCenter() {
		center = new JPanel(new GridLayout(1, 1));
		progress = new JProgressBar(1, 100);
		center.add(progress);
	}

	private void initializeSouth() {
		south = new JPanel(new GridLayout(1, 1));
		tempLabel = new JLabel("aasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfdaasdsfdfdfd");
		south.add(tempLabel);
	}

	private void addEverything() {
		frame.add(north, BorderLayout.NORTH);
		frame.add(center, BorderLayout.CENTER);
		frame.add(south, BorderLayout.SOUTH);
	}
	//********************** BUILD GUI **********************//

}