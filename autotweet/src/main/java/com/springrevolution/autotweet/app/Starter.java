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
			TweetManager.DRIVER_SUPPORTER_LIST.clear();
			if (needToSleep) {
				if (handleShutdownSignal(tweetManager)) {
					break;
				}
				try {
					// Wait for 15 minutes
					int duration_min = 15;
					LOGGER.info("Wait " + duration_min + " minutes for next tweet");
					LOGGER.info("Press Ctrl+C to stop program. :D");
					Thread.sleep(Duration.ofMinutes(duration_min).toMillis());
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
	
	private static boolean handleShutdownSignal(TweetManager manager) {
		Configuration app_config = Configuration.loadConfiguration();
		if (app_config == null) {
			LOGGER.error(String.format("Failed to load %s in app directory!", Configuration.CONFIG_FILE.getName()));
			LOGGER.error("Program will exit.");
			System.exit(0);
		} else {
			if (app_config.getAppConfig().isAppShutdownSignal()) {
				LOGGER.info("Shutdown Signal is detected in app config.");
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
				manager.killProcess();
				return true;
			}
		}
		return false;
	}
}
