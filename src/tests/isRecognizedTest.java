package tests;

import im.goel.MoodicPlayer.TrackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.umass.lastfm.Caller;
import de.umass.lastfm.Tag;
import de.umass.lastfm.Track;

public class isRecognizedTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<TrackInfo> allTracks = new ArrayList<TrackInfo>();

		String key = "6ea9780361e0c7342a8a08a1ad78dfec";
		Caller.getInstance().setUserAgent("tst");
		Caller.getInstance().setCache(null);

		long start = System.currentTimeMillis();
		Collection<Track> allTracksForTag = Tag.getTopTracks("gloomy", key);
		
		// Print all tracks without pruning
		System.out.println(allTracksForTag.size() + " tracks without pruning..");
		for (Track t : allTracksForTag) {
			System.out.println(t.getName() + " by " + t.getArtist());
		}
		System.out.println();
		System.out.println();
		
		// Transfer all tracks that are recognised
		int removeCount = 0;
		for (Track track : allTracksForTag) {
			TrackInfo tempTrack = new TrackInfo(track.getName(), track.getArtist());
			if (!tempTrack.isRecognised()) {
				allTracks.add(tempTrack);
			} else {
				removeCount++;
			}
		}
		long end = System.currentTimeMillis();
		
		System.out.println(allTracks.size() + " tracks with pruning..");
		// print new tracks after removing bad ones.
		for (TrackInfo track : allTracks) {
			System.out.println(track.toString());
		}
		
		System.out.println();
		System.out.println("Took " + (end - start) + " ms.");
		System.out.println(removeCount + " removed");
		System.out.println();
	}

}
