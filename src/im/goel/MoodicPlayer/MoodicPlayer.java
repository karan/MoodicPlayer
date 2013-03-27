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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.umass.lastfm.Caller;
import de.umass.lastfm.Tag;
import de.umass.lastfm.Track;

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
			allTracks = new ArrayList<TrackInfo>();
			String mood = moodSelector.getSelectedItem().toString().toLowerCase();
			List<String> subMoods = moodObj.getSubMoods(mood);

			String key = "6ea9780361e0c7342a8a08a1ad78dfec";
			Caller.getInstance().setUserAgent("tst");
			Caller.getInstance().setCache(null);

			Collection<Track> allTracksForSubMoods = new ArrayList<Track>();
			for (String tag : subMoods) {
				allTracksForSubMoods.addAll(Tag.getTopTracks(tag, key));
			}

			for (Track track : allTracksForSubMoods) {
				TrackInfo tempTrack = new TrackInfo(track.getName(), track.getArtist());
				if (!tempTrack.isRecognised()) {
					allTracks.add(tempTrack);
				}
			}
			for (TrackInfo track : allTracks) {
				System.out.println(track.toString());
			}
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