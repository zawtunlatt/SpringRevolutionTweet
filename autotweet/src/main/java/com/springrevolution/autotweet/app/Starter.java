package com.springrevolution.autotweet.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springrevolution.autotweet.config.Configuration;
import com.springrevolution.autotweet.tweet.TweetManager;

public class Starter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Starter.class);
	public static void main(String[] args) {
		boolean needToSleep = false;
		TweetManager tweetManager = new TweetManager();
		tweetManager.prepareEnvironment();
		while (true) {
			TweetManager.DRIVER_SUPPORTER_LIST.clear();
			if (needToSleep) {
				try {
					// Wait for 15 minutes
					int duration_min = 15;
					LOGGER.info("Wait for " + duration_min + " minutes for next tweet");
					LOGGER.info("Press Ctrl+C to stop program. :D");
					Thread.sleep(duration_min * 1000 * 60);
					LOGGER.info("Time to tweet again.");
				} catch (InterruptedException e) {
					e.printStackTrace();
					LOGGER.error(e.getMessage());
				}
			}
			needToSleep = true;
			tweetManager.tweet();
			if (handleShutdownSignal(tweetManager)) {
				break;
			}
		}
	}
	
	private static boolean handleShutdownSignal(TweetManager manager) {
		Configuration app_config = Configuration.loadConfiguration();
		if (app_config == null) {
			LOGGER.error(String.format("Failed to load %s in app directory!", Configuration.CONFIG_FILE.getName()));
			LOGGER.error("Program will exit.");
			System.exit(0);
		} else {
			if (app_config.getAppConfig().isAppShutdownSignal()) {
				manager.killProcess();
				return true;
			}
		}
		return false;
	}
}
