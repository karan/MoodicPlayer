package im.goel.MoodicPlayer;

public class TrackInfo {

	private String name;
	private String artist;
	private String mbid;
	private int playCount;
	private int listeners;

	public TrackInfo(String name, String artist, String mbid, int playCount, int listeners) {
		this.name = name;
		this.artist = artist;
		this.mbid = mbid;
		this.playCount = playCount;
		this.listeners = listeners;
	}

	public String getTrackName() {
		return name;
	}

	public String getTrackArtist() {
		return artist;
	}
	
	public String getMbid() {
		return mbid;
	}
	
	public int getPlayCount() {
		return playCount;
	}
	
	public int getListeners() {
		return listeners;
	}

	public String toString() {
		return name + " - by " + artist;
	}
	
}
