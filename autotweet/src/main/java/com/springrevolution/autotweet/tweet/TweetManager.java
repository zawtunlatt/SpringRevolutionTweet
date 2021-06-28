package com.springrevolution.autotweet.tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
	public static final int PARALLEL_TWEET_COUNT = 8;
	public static final List<WebDriverSupporter> DRIVER_SUPPORTER_LIST = new ArrayList<>();
	static {
		System.setProperty("webdriver.chrome.driver","chromedriver.exe");
		Runtime.getRuntime().addShutdownHook(new ShutDownHook());
	}
	
	public Configuration app_config;

	public void tweet() {
		app_config = Configuration.loadConfiguration();
		if (app_config == null) {
			System.out.println(String.format("Failed to load %s in app directory!", Configuration.CONFIG_FILE.getName()));
			System.out.println("Program will exit.");
			System.exit(0);
		}
		Set<TwitterUserConfig> user_config_set = new TreeSet<TwitterUserConfig>(app_config.getTwitterUserList());

		ExecutorService es = Executors.newFixedThreadPool(PARALLEL_TWEET_COUNT);
		int index = 0;
		for (TwitterUserConfig user_config : user_config_set) {
			if (!user_config.isTweet()) {
				continue;
			}
			int i = index;
			TwitterUser user = user_config.convertUser();
			Set<ChannelConfig> tweet_channel_config_set = new TreeSet<>(user_config.getChannelList());
			WebDriverSupporter driver_support = new WebDriverSupporter(i, app_config.getAppConfig());
			DRIVER_SUPPORTER_LIST.add(driver_support);
			es.execute(() -> {
                try {
    				TweetWorker tweet_worker = new TweetWorker(user, driver_support, app_config);
    				tweet_worker.loginTwitter();
    				
        			for (ChannelConfig channelConfig : tweet_channel_config_set) {
        				TelegramChannel channel = null;
        				if (channelConfig.getChannelURL().equals(ChannelTemplate.CLICK_AND_TWEET_URL)) {
        					System.out.println("Click & Tweet Found");
        					channel = new ClickAndTweetChannel(driver_support, channelConfig);
        				} else {
        					System.out.println("Other Channel Found");
        					channel = new TelegramChannel(driver_support, channelConfig);
        				}
            			try {
            				Set<PostData> post_data_set = channel.getTodayPosts();
            				tweet_worker.tweet(post_data_set);
            			} catch (InterruptedException e) {
            				e.printStackTrace();
            			} catch (Exception e) {
            				e.printStackTrace();
            			}
        			}
                } catch (Exception e) {
					e.printStackTrace();
				}
			});
			if (index > 0 && index % 7 == 0) {
		        es.shutdown();
		        try {
		            es.awaitTermination(1, TimeUnit.DAYS);
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		        new ShutDownHook().killProcess();
		        System.out.println("Partial Thread Pool ended");
		        try {
		        	System.out.println("Wait for 7 seconds for next Thread Pool");
					Thread.sleep(1000 * 7);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        es = Executors.newFixedThreadPool(PARALLEL_TWEET_COUNT);
			}
			index++;
		}
        es.shutdown();
        try {
            es.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new ShutDownHook().start();
	}

}


class ShutDownHook extends Thread {
	
	public void killProcess () {
		try {
			System.out.println("Web Driver will be closing soon.");
			for (WebDriverSupporter driver_support : TweetManager.DRIVER_SUPPORTER_LIST) {
				driver_support.getTelegramDriver().close();
				driver_support.getTelegramDriver().quit();
				driver_support.getTwitterDriver().close();
				driver_support.getTwitterDriver().quit();
			}
			TweetManager.DRIVER_SUPPORTER_LIST.clear();
			Thread.sleep(1000 * 5);
			Runtime.getRuntime().exec("taskkill /f /im chromedriver.exe /T");
			Runtime.getRuntime().exec("taskkill /f /im chrome.exe /T");
			System.out.println("Web Driver closing is Done.");
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		}
	}
	@Override
	public void run() {
		System.out.println("@End");
		killProcess();
	}
}
