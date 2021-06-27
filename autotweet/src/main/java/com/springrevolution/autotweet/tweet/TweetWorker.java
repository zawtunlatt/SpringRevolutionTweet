package com.springrevolution.autotweet.tweet;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;

import com.springrevolution.atuotweet.config.Configuration;
import com.springrevolution.atuotweet.config.LastTweetHistoryConfig;
import com.springrevolution.atuotweet.config.TwitterUserConfig;
import com.springrevolution.autotweet.data.PostData;
import com.springrevolution.autotweet.data.TwitterUser;
import com.springrevolution.autotweet.support.Helper;
import com.springrevolution.autotweet.support.WebDriverSupporter;

public class TweetWorker {
	private final TwitterUser user;
	private final WebDriver driver;
	private final Wait<WebDriver> wait;
	private Set<PostData> tweetSet;
	private final Configuration app_config;
	private final List<LastTweetHistoryConfig> history_list;
	
	public static final Random RANDOM = new Random();
	
	public TweetWorker(TwitterUser user, WebDriverSupporter driver_support, Configuration app_config) {
		this.user = user;
        this.driver = driver_support.getTwitterDriver();
        this.wait = driver_support.getTwitterWait();
        this.app_config = app_config;
        history_list = user.getUserConfig().getLastTweetHistory();
	}
	
	public TwitterUser getTwitterUser(String user_name, String password) {
        return user;
	}
	
	public void loginTwitter() throws InterruptedException {
		driver.get("https://twitter.com/login");
		Thread.sleep(1000);
		WebElement username_input = wait.until(new Function<WebDriver, WebElement>() {
			String login_title = "//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[1]/label/div/div[2]/div/input";
			  public WebElement apply(WebDriver drv) {
				  System.out.println("Waiting : " + (driver == drv));
			    return driver.findElement(By.xpath(login_title));
			  }
			});
//		String login_title = "//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[1]/label/div/div[2]/div/input";
//		WebElement username_input = driver.findElement(By.xpath(login_title));
		for (int i = 0; i < user.getUsername().length(); i++) {
			username_input.sendKeys(Character.toString(user.getUsername().charAt(i)));
			Thread.sleep(100);
		}
		Thread.sleep(1000);
		
		String password_xpath = "//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[2]/label/div/div[2]/div/input"; 
		WebElement password_input = driver.findElement(By.xpath(password_xpath));
		password_input.sendKeys(user.getPassword());
		
		Thread.sleep(2 * 1000);

		String login_btn_xpath = "//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[3]/div";
		WebElement login_btn = driver.findElement(By.xpath(login_btn_xpath));
		login_btn.sendKeys(Keys.ENTER);
		
		Thread.sleep(3 * 1000);
	}
	
	public void tweet(Set<PostData> post_data_set) throws InterruptedException {
		this.tweetSet = post_data_set;
		filterTweet(this.tweetSet);
		System.out.println("Start to tweet");
		System.out.println("Actual Tweet Size : " + tweetSet.size());
		if (tweetSet.size() < 1) {
			System.out.println("No new tweet. Returned ");
			return;
		}
		for (PostData data : tweetSet) {
			System.out.println("To tweet : " + data.getTelegramPostID());
		}
		
		int index = 0;
		for (Iterator<PostData> dataIterator = tweetSet.iterator(); dataIterator.hasNext();) {
			PostData data = dataIterator.next();
			System.out.println("It is time to tweet : " + data.getTelegramPostID());
			System.out.println("Tweet URL : \n" + data.getTwitterURL());
			driver.get(data.getTwitterURL());
//			Thread.sleep(1000 * 4);
			WebElement tweet_btn = wait.until(new Function<WebDriver, WebElement>() {
				String tweet_btn_xpath = "/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]"
						+ "/div[2]/div/div[3]/div/div/div/div[1]/div/div/div/div/div[2]/div[3]/div/div/div[2]/div[4]";
				  public WebElement apply(WebDriver drv) {
				    return driver.findElement(By.xpath(tweet_btn_xpath));
				  }
				});
//			String tweet_btn_xpath = "/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]"
//					+ "/div[2]/div/div[3]/div/div/div/div[1]/div/div/div/div/div[2]/div[3]/div/div/div[2]/div[4]";
//			WebElement tweet_btn = driver.findElement(By.xpath(tweet_btn_xpath));
			tweet_btn.click();
			System.out.println("Tweeted Post: " + data.getTelegramPostID());
			System.out.println("Tweeted By: [" + user.getUserConfig().getDisplayName() + "] -> for [" + data.getChannelName() + "] Channel");
			System.out.println("Remaining Tweet : " + (tweetSet.size() - (index + 1)));
			updateLastTweet(data.getChannelURL(), data.getTelegramPostID());
			index++;
			int delay = 15 + RANDOM.nextInt(10);
			System.out.println("Wait for " + delay + " seconds.");
			Thread.sleep(delay * 1000);
		}
	}
	
	private void updateLastTweet(String channelURL, String postId) {
		for (TwitterUserConfig user_config : app_config.getTwitterUserList()) {
			if (user.equals(user_config.convertUser())) {
				boolean history_found = false;
				for (LastTweetHistoryConfig last_tweet : user_config.getLastTweetHistory()) {
					if (channelURL.equals(last_tweet.getChannelURL())) {
						history_found = true;
						last_tweet.setLastTweet(postId);
						break;
					}
				}
				if (!history_found) {
					LastTweetHistoryConfig last_tweet_history = new LastTweetHistoryConfig();
					last_tweet_history.setChannelURL(channelURL);
					last_tweet_history.setLastTweet(postId);
					user_config.getLastTweetHistory().add(last_tweet_history);
				}
				break;
			}
		}
		Helper.updateConfig(app_config);
	}
	
	public void filterTweet(Set<PostData> postDataSet) {
		if (null == history_list) {
			return;
		}
		boolean history_found = false;
		Set<PostData> filtered = new TreeSet<PostData>();
		for (PostData pd : postDataSet) {
			String url = pd.getChannelURL();
			for (LastTweetHistoryConfig history : history_list) {
				if (url.equals(history.getChannelURL())) {
					int last_tweet_id = Integer.parseInt(history.getLastTweet().split("/")[1]);
					int current_tweet_id = Integer.parseInt(pd.getTelegramPostID().split("/")[1]);
					if (current_tweet_id > last_tweet_id) {
						history_found = true;
						filtered.add(pd);
					}					
				}
			}
		}
		if (history_found) {
			postDataSet.clear();
			postDataSet.addAll(filtered);			
		}
	}

	public TwitterUser getUser() {
		return user;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public Wait<WebDriver> getWait() {
		return wait;
	}
}
