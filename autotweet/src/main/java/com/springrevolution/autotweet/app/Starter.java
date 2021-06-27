package com.springrevolution.autotweet.app;

import com.springrevolution.autotweet.tweet.TweetManager;

public class Starter {

	public static void main(String[] args) {
		boolean needToSleep = false;
		while (true) {
			TweetManager.DRIVER_SUPPORTER_LIST.clear();
			if (needToSleep) {
				try {
					// Wait for 15 minutes
					int duration_min = 15;
					System.out.println("Wait for " + duration_min + " minutes for next tweet");
					System.out.println("Press Ctrl+C to stop program. :D");
					Thread.sleep(duration_min * 1000 * 60);
					System.out.println("Time to tweet again.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			needToSleep = true;
			new TweetManager().tweet();
		}
	}
}
