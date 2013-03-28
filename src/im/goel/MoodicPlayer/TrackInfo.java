/*******************************************************
 * 
 * Karan Goel, 2013
 * karan@goel.im
 * 
 * A TrackInfo object stores all crucial information
 * about a particular track. All this information is
 * extracted from last.fm.
 * 
 *******************************************************/

package im.goel.MoodicPlayer;

public class TrackInfo {

	private String name;
	private String artist;
	private String mbid; // MusicBrainz ID
	private int playCount; // As on last.fm
	private int listeners; // As on last.fm

	/**
	 * Build a TrackInfo object using the passed parameters.
	 */
	public TrackInfo(String name, String artist, String mbid, int playCount, int listeners) {
		this.name = name;
		this.artist = artist;
		this.mbid = mbid;
		this.playCount = playCount;
		this.listeners = listeners;
	}

	/**
	 * @return track's name
	 */
	public String getTrackName() {
		return name;
	}

	/**
	 * @return artist's name for this track
	 */
	public String getTrackArtist() {
		return artist;
	}
	
	/**
	 * @return mbid for this track
	 */
	public String getMbid() {
		return mbid;
	}
	
	/**
	 * @return play count for this track
	 */
	public int getPlayCount() {
		return playCount;
	}
	
	/**
	 * @return listeners for this track
	 */
	public int getListeners() {
		return listeners;
	}

	/**
	 * @return string representation for this track
	 */
	public String toString() {
		return name + " - by " + artist;
	}
	
}