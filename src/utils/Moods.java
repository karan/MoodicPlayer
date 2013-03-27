package utils;

import java.util.List;
import java.util.Arrays;

public class Moods {

	public Moods() {}

	private String[] moods = {
			"Happy",
			"Sad",
			"Relaxed",
			"Angry"
	};
	
	public String[] getMoods() {
		return moods;
	}
	
	public List<String> getSubMoods(String mood) {
		if (mood.equals("happy")) {
			return Arrays.asList("happy", "excited", "glad", "joyful", "cheerful", "exciting");
		} else if (mood.equals("sad")) {
			return Arrays.asList("sad", "gloomy", "blue", "depressed", "hopeless");
		} else if (mood.equals("relaxed")) {
			return Arrays.asList("relaxed", "calm", "dreamy", "serene", "tranquil");
		} else {
			return Arrays.asList("angry", "agressive", "furious", "disturbing", "distress");
		}
	}

}
