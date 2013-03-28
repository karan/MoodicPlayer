package im.goel.MoodicPlayer;

public class TrackInfo {
	
	private String trackName;
	private String trackArtist;
	private int playCount;
	private int listeners;
	
	public TrackInfo(String trackName, String trackArtist) {
		this(trackName, trackArtist, -1, -1);
	}
	
	public TrackInfo(String trackName, String trackArtist, int playCount, int listeners) {
		this.trackName = trackName;
		this.trackArtist = trackArtist;
		this.playCount = playCount;
		this.listeners = listeners;
	}

	public String getTrackName() {
		return trackName;
	}
	
	public String getTrackArtist() {
		return trackArtist;
	}
	
	public int getPlayCount() {
		return playCount;
	}
	
	public int getListeners() {
		return listeners;
	}
	
	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}
	
	public void setListeners(int listeners) {
		this.listeners = listeners;
	}
	
	public String toString() {
		return trackName + " - by " + trackArtist;
	}
	
	/**
	 * Some tracks may have non-English characters, which will prevent
	 * Moodic Player from working properly. This method will check this,
	 * and return false if the track name and/or artist name contain
	 * non-English characters.
	 * @return
	 */
	public boolean isRecognised() {
		return trackName.matches("[^\\x00-\\x80]+");
	}

}
