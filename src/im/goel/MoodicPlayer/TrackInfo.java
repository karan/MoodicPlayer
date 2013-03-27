package im.goel.MoodicPlayer;

public class TrackInfo {
	
	private String trackName;
	private String trackArtist;
	
	public TrackInfo(String trackName, String trackArtist) {
		this.trackName = trackName;
		this.trackArtist = trackArtist;
	}
	
	public String getTrackName() {
		return trackName;
	}
	
	public String getTrackArtist() {
		return trackArtist;
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
	
	public String toString() {
		return trackName + " - by " + trackArtist;
	}
}
