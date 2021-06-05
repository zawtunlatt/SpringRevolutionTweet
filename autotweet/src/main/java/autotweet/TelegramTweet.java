package autotweet;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TelegramTweet {
	static {
		System.setProperty("webdriver.chrome.driver","chromedriver.exe");
	}
	
	public static Random random = new Random();
	
	public static Set<PostData> filterTweet(Set<PostData> postDataSet) {
		String last_tweeted = Helper.getLastTweeted();
		if (last_tweeted.isBlank()) {
			return postDataSet;
		}
		Set<PostData> filtered = new HashSet<PostData>();
		for (PostData pd : postDataSet) {
			int last_tweet_id = Integer.parseInt(last_tweeted.split("/")[1]);
			int current_tweet_id = Integer.parseInt(pd.getTelegramPostID().split("/")[1]);
			if (current_tweet_id > last_tweet_id) {
				filtered.add(pd);
			}
		}
		return filtered;
	}
}

