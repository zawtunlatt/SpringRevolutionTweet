package com.springrevolution.autotweet.tweet;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springrevolution.autotweet.config.ChannelConfig;
import com.springrevolution.autotweet.config.Configuration;
import com.springrevolution.autotweet.config.TwitterUserConfig;
import com.springrevolution.autotweet.data.ChannelTemplate;
import com.springrevolution.autotweet.data.PostData;
import com.springrevolution.autotweet.data.TwitterUser;
import com.springrevolution.autotweet.datacollector.ClickAndTweetChannel;
import com.springrevolution.autotweet.datacollector.TelegramChannel;
import com.springrevolution.autotweet.support.WebDriverSupporter;

public class TweetManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(TweetManager.class);
	
	public static final int PARALLEL_TWEET_COUNT = 8;
	public static final List<WebDriverSupporter> DRIVER_SUPPORTER_LIST = new ArrayList<>();
	static {
		Runtime.getRuntime().addShutdownHook(new ShutDownHook());
	}
	
	public Configuration app_config;
	
	private List<TweetWorker> workerList = new ArrayList<>();
	
	public void prepareEnvironment() {
		app_config = Configuration.loadConfiguration();
		if (app_config == null) {
			LOGGER.error(String.format("Failed to load %s in app directory!", Configuration.CONFIG_FILE.getName()));
			LOGGER.error("Program will exit.");
			System.exit(0);
		}
		Set<TwitterUserConfig> user_config_set = new TreeSet<TwitterUserConfig>(app_config.getTwitterUserList());
		int index = 0;
		for (TwitterUserConfig user_config : user_config_set) {
			if (!user_config.isTweet()) {
				continue;
			}
			TwitterUser user = user_config.convertUser();
			WebDriverSupporter driver_support = null;
			TweetWorker tweet_worker = null;
			while (tweet_worker == null) {
				driver_support = new WebDriverSupporter(index, app_config.getAppConfig());
				tweet_worker = new TweetWorker(user, driver_support, app_config, this);
				try {
					LOGGER.info(user.getUsername() + " is waiting web driver for 5 seconds");
					Thread.sleep(Duration.ofSeconds(5).toMillis());
					boolean loggedInOK = tweet_worker.loginTwitter();
					if (!loggedInOK) {
						cleanWebDriver(driver_support);
						tweet_worker = null;
					}
				} catch (InterruptedException e) {
					LOGGER.error(e.getMessage());
				} catch (WebDriverException e) {
					cleanWebDriver(driver_support);
					tweet_worker = null;
				}
				if (tweet_worker == null) {
					LOGGER.warn("Login Failed! Program is trying again to login for [" + user.getUsername() + "]");
				}
			}
			DRIVER_SUPPORTER_LIST.add(driver_support);
			workerList.add(tweet_worker);
			index++;
		}
	}
	
	private void cleanWebDriver(WebDriverSupporter driver_support) {
		driver_support.getTelegramDriver().close();
		driver_support.getTelegramDriver().quit();
		driver_support.getTwitterDriver().close();
		driver_support.getTwitterDriver().quit();
	}

	public void tweet() {
		app_config = Configuration.loadConfiguration();
		if (app_config == null) {
			LOGGER.error(String.format("Failed to load %s in app directory!", Configuration.CONFIG_FILE.getName()));
			LOGGER.error("Program will exit.");
			System.exit(0);
		}
		LOGGER.info("Configuration file loaded");

		ExecutorService es = Executors.newFixedThreadPool(PARALLEL_TWEET_COUNT);
		int index = 0;
		for (TweetWorker worker : workerList) {
			TwitterUser user = worker.getUser();
			TwitterUserConfig user_config = user.getUserConfig();
			Set<ChannelConfig> tweet_channel_config_set = new TreeSet<>(user_config.getChannelList());
			es.execute(() -> {
                try {
        			for (ChannelConfig channelConfig : tweet_channel_config_set) {
        				TelegramChannel channel = null;
        				if (channelConfig.getChannelURL().equals(ChannelTemplate.CLICK_AND_TWEET_URL)) {
        					channel = new ClickAndTweetChannel(worker.getDriverSupport(), channelConfig);
        				} else {
        					channel = new TelegramChannel(worker.getDriverSupport(), channelConfig);
        				}
            			try {
            				Set<PostData> post_data_set = channel.getTodayPosts();
            				worker.tweet(post_data_set);
            			} catch (InterruptedException e) {
            				LOGGER.error(e.getMessage());
            			} catch (Exception e) {
            				LOGGER.error(e.getMessage());
            			}
        			}
                } catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
			});
			if (index > 0 && index % 7 == 0) {
		        es.shutdown();
		        try {
		            es.awaitTermination(1, TimeUnit.DAYS);
		        } catch (InterruptedException e) {
		            LOGGER.error(e.getMessage());
		        }
//		        new ShutDownHook().killProcess();
		        LOGGER.info("Partial Thread Pool ended");
		        try {
		        	LOGGER.info("Wait for 7 seconds for next Thread Pool");
					Thread.sleep(1000 * 7);
				} catch (InterruptedException e) {
					LOGGER.error(e.getMessage());
				}
		        es = Executors.newFixedThreadPool(PARALLEL_TWEET_COUNT);
			}
			index++;
		}
        es.shutdown();
        try {
            es.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
//        new ShutDownHook().start();
	}

	public List<TweetWorker> getWorkerList() {
		return workerList;
	}
	
	public void killProcess() {
		new ShutDownHook().start();;
	}
}


class ShutDownHook extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShutDownHook.class);
	public void killProcess () {
		try {
			LOGGER.info("Web Driver will be closed soon.");
//			for (WebDriverSupporter driver_support : TweetManager.DRIVER_SUPPORTER_LIST) {
//				driver_support.getTelegramDriver().close();
//				driver_support.getTelegramDriver().quit();
//				driver_support.getTwitterDriver().close();
//				driver_support.getTwitterDriver().quit();
//			}
//			Thread.sleep(1000 * 5);
			TweetManager.DRIVER_SUPPORTER_LIST.clear();
			Runtime.getRuntime().exec("taskkill /f /im chromedriver.exe /T");
			Runtime.getRuntime().exec("taskkill /f /im chrome.exe /T");
			Runtime.getRuntime().exec("taskkill /f /im geckodriver.exe /T");
			Runtime.getRuntime().exec("taskkill /f /im firefox.exe /T");
			LOGGER.info("Web Driver closing is Done.");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
	@Override
	public void run() {
		killProcess();
		LOGGER.info("@Program Ended");
	}
}
