/*******************************************************
 * 
 * Karan Goel, 2013
 * karan@goel.im
 * 
 * The class contains all moods that Moodic Player
 * uses.
 * 
 *******************************************************/

package utils;

import java.util.List;
import java.util.Arrays;

public class Moods {

	public Moods() {}

	/**
	 * The 'categories' of moods.
	 */
	private String[] moods = {
			"Happy",
			"Sad",
			"Relaxed",
			"Angry"
	};
	
	/**
	 * @return all moods as an array.
	 */
	public String[] getMoods() {
		return moods;
	}
	
	/**
	 * Takes a category of mood as parameter and returns the
	 * sub moods as a list.
	 * @param mood, also called a category of moods.
	 * @return list of moods for given category.
	 */
	public List<String> getSubMoods(String mood) {
		if (mood.equals("happy")) {
			return Arrays.asList("happy", "excited", "joyful", "cheerful");
		} else if (mood.equals("sad")) {
			return Arrays.asList("sad", "gloomy", "depressed", "hopeless");
		} else if (mood.equals("relaxed")) {
			return Arrays.asList("relaxed", "calm", "serene", "tranquil");
		} else {
			return Arrays.asList("angry", "agressive", "furious", "distress");
		}
	}

}
