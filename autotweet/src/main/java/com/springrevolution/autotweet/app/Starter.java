package com.springrevolution.autotweet.app;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springrevolution.autotweet.config.Configuration;
import com.springrevolution.autotweet.tweet.TweetManager;
import com.springrevolution.autotweet.tweet.TweetWorker;

public class Starter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Starter.class);
	public static void main(String[] args) {
		LOGGER.info("Application is started");
		boolean needToSleep = false;
		TweetManager tweetManager = new TweetManager();
		tweetManager.prepareEnvironment();
		while (true) {
			if (tweetManager.isWebDriverRestartRequired()) {
				LOGGER.info("Twitter accounts will be logged out.");
				logout(tweetManager);
				LOGGER.info("Twitter accounts have been logged out.");
				LOGGER.info("Web Browsers will be restarted");
				tweetManager.quitWebDriver();
				LOGGER.info("Web Browser closing was completed.");
				
				tweetManager.prepareEnvironment();
				tweetManager.setWebDriverRestartRequired(false);
			}
			
			if (needToSleep) {
				if (handleShutdownSignal(tweetManager)) {
					break;
				}
				try {
					// Wait for 15 minutes
					int duration_min = 15;
					LOGGER.info("Wait " + duration_min + " minutes for next tweet");
					LOGGER.info("Press Ctrl+C to stop program. :D");
					for (float i = 0; i < duration_min; i += 0.5) {
						Thread.sleep(Duration.ofSeconds(30).toMillis());
						float rem = (float) (duration_min - (i + 0.5));
						LOGGER.info(rem + " minutes remaining for next tweet");
					}
					LOGGER.info("Time to tweet again.");
				} catch (InterruptedException e) {
					e.printStackTrace();
					LOGGER.error(e.getMessage());
				}
			}
			needToSleep = true;
			tweetManager.tweet();
		}
	}
	
	private static void logout(TweetManager manager) {
		ExecutorService es = Executors.newFixedThreadPool(manager.getWorkerList().size());
		for (TweetWorker worker : manager.getWorkerList()) {
			if (worker.isHomeExist()) {
				es.execute(() -> {
					try {
						worker.logout();
					} catch (InterruptedException e) {
						LOGGER.error(e.getMessage() + " -> " + worker.getUser().getUsername());
					}							
				});
			}
		}
		es.shutdown();
		try {
			es.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage());
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
				LOGGER.info("Shutdown Signal is detected in app config.");
				logout(manager);
				manager.stopProgram();
				return true;
			}
		}
		return false;
	}
}
