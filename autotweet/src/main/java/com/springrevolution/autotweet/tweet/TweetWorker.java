package com.springrevolution.autotweet.tweet;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springrevolution.autotweet.config.Configuration;
import com.springrevolution.autotweet.config.LastTweetHistoryConfig;
import com.springrevolution.autotweet.config.TweetedURLConfig;
import com.springrevolution.autotweet.config.TwitterUserConfig;
import com.springrevolution.autotweet.data.PostData;
import com.springrevolution.autotweet.data.TwitterUser;
import com.springrevolution.autotweet.support.Helper;
import com.springrevolution.autotweet.support.WebDriverSupporter;

public class TweetWorker {
	private static final Logger LOGGER = LoggerFactory.getLogger(TweetWorker.class);
	public static final Random RANDOM = new Random();
	private static final File FOLDER = new File("TweetedRecords");
	static {
		if (!FOLDER.exists()) {
			FOLDER.mkdir();
		}
	}

	private final TwitterUser user;
	private final WebDriver driver;
	private final Wait<WebDriver> wait;

	private Set<PostData> tweetSet;
	private Configuration app_config;
	private List<LastTweetHistoryConfig> history_list;
	private WebDriverSupporter driverSupport;
	private TweetedURLConfig tweeted_config = new TweetedURLConfig();
	private File tweeted_record_file;

	private boolean isLoggedIn = false;

	public TweetWorker(TwitterUser user, WebDriverSupporter driver_support, Configuration app_config,
			TweetManager manager) {
		this.user = user;
		this.driverSupport = driver_support;
		this.driver = driver_support.getTwitterDriver();
		this.wait = driver_support.getTwitterWait();
		this.app_config = app_config;
		history_list = user.getUserConfig().getLastTweetHistory();
	}

	private void closeCookieRequest() {
		// Cookie Message box Button
		try {

			WebElement el = wait.until(new Function<WebDriver, WebElement>() {
				String btn_xpath = "/html/body/div/div/div/div[1]/div/div/div/div/div/div[2]";

				public WebElement apply(WebDriver drv) {
					return drv.findElement(By.xpath(btn_xpath));
				}
			});
			el.click();
			LOGGER.info("Close button found and clicked");
			Thread.sleep(Duration.ofMillis(1500).toMillis());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	public boolean loginTwitter() throws InterruptedException, WebDriverException {
		if (this.isLoggedIn) {
			return true;
		}
		driver.get("https://twitter.com/login");
//		driver.get("https://twitter.com/i/flow/login");
		Thread.sleep(Duration.ofSeconds(3).toMillis());
		LOGGER.info("Current URL: " + driver.getCurrentUrl());
		if (driver.getCurrentUrl().equals("https://twitter.com/i/flow/login")) {
			this.isLoggedIn = tryLoginMethod1(user.getUsername());
		} else if (driver.getCurrentUrl().equals("https://twitter.com/login")) {
			if (tryLoginMethod2(user.getUsername())) {
				this.isLoggedIn = true;
			} else {
				// If twitter detect some abnormal login, they request to login with other
				// method
				try {
					this.isLoggedIn = tryLoginMethod2(user.getEmail());
				} catch (InterruptedException e) {
					// Trying with Login Fail
					// Twitter request with different methods like phone number or different dialog.
				}
			}
		}
		closeCookieRequest();

		if (this.isLoggedIn) {
			LOGGER.info("Login Success:  For " + formatInfo(user.getUsername()));
			return true;
		} else {
			LOGGER.info("Login Fail:  For " + formatInfo(user.getUsername()));
			return false;
		}

	}

	private boolean tryLoginMethod1(String userInfo) throws WebDriverException, InterruptedException {
		try {
			LOGGER.info("Trying to login with : " + userInfo);
			WebElement userinfo_input = wait.until(new Function<WebDriver, WebElement>() {
				String user_info_input_xpath = "//*[@id=\"layers\"]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/label/div/div[2]/div/input";

				public WebElement apply(WebDriver drv) {
					return drv.findElement(By.xpath(user_info_input_xpath));
				}
			});
			for (int i = 0; i < userInfo.length(); i++) {
				userinfo_input.sendKeys(Character.toString(user.getUsername().charAt(i)));
				Thread.sleep(Duration.ofMillis(100).toMillis());
			}
			Thread.sleep(Duration.ofSeconds(1).toMillis());

			WebElement next_btn = wait.until(new Function<WebDriver, WebElement>() {
				String nextButton_xpath = "//*[@id=\"layers\"]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div";

				public WebElement apply(WebDriver drv) {
					return drv.findElement(By.xpath(nextButton_xpath));
				}
			});
			next_btn.click();
			Thread.sleep(Duration.ofSeconds(2).toMillis());

			WebElement password_input_btn = wait.until(new Function<WebDriver, WebElement>() {
				String password_input_xpath = "//*[@id=\"layers\"]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/div/label/div/div[2]/div/input";

				public WebElement apply(WebDriver drv) {
					return drv.findElement(By.xpath(password_input_xpath));
				}
			});

			for (int i = 0; i < user.getPassword().length(); i++) {
				password_input_btn.sendKeys(Character.toString(user.getPassword().charAt(i)));
				Thread.sleep(Duration.ofMillis(25).toMillis());
			}

			WebElement login_btn = wait.until(new Function<WebDriver, WebElement>() {
				String login_xpath = "//*[@id=\"layers\"]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div";

				public WebElement apply(WebDriver drv) {
					return drv.findElement(By.xpath(login_xpath));
				}
			});
			login_btn.click();
			return isHomeExist();
		} catch (NoSuchElementException e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}

	private boolean tryLoginMethod2(String userInfo) throws InterruptedException, WebDriverException {
		LOGGER.info("Trying to login with : " + userInfo);
		try {
			WebElement userinfo_input = wait.until(new Function<WebDriver, WebElement>() {
				String user_info_input_xpath = "//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[1]/label/div/div[2]/div/input";

				public WebElement apply(WebDriver drv) {
					return drv.findElement(By.xpath(user_info_input_xpath));
				}
			});
			for (int i = 0; i < userInfo.length(); i++) {
				userinfo_input.sendKeys(Character.toString(userInfo.charAt(i)));
				Thread.sleep(Duration.ofMillis(100).toMillis());
			}
			Thread.sleep(Duration.ofSeconds(1).toMillis());

			WebElement password_input = wait.until(new Function<WebDriver, WebElement>() {
				String password_xpath = "//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[2]/label/div/div[2]/div/input";

				public WebElement apply(WebDriver drv) {
					return drv.findElement(By.xpath(password_xpath));
				}
			});

			for (int i = 0; i < user.getPassword().length(); i++) {
				password_input.sendKeys(Character.toString(user.getPassword().charAt(i)));
				Thread.sleep(Duration.ofMillis(25).toMillis());
			}
			Thread.sleep(Duration.ofSeconds(1).toMillis());

			WebElement login_btn = wait.until(new Function<WebDriver, WebElement>() {
				String login_btn_xpath = "//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[3]/div";

				public WebElement apply(WebDriver drv) {
					return drv.findElement(By.xpath(login_btn_xpath));
				}
			});
			login_btn.sendKeys(Keys.ENTER);
			return isHomeExist();
		} catch (NoSuchElementException e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}

	public boolean isHomeExist() {
		boolean homeExist = false;
		try {
			wait.until(new Function<WebDriver, WebElement>() {
				String home_xpath = "/html/body/div/div/div/div[2]/header/div/div/div/div[1]/div[2]/nav/a[1]";

				public WebElement apply(WebDriver drv) {
					return drv.findElement(By.xpath(home_xpath));
				}
			});
			homeExist = true;
		} catch (NoSuchElementException e) {
			homeExist = false;
			LOGGER.error(e.getMessage());
		} catch (TimeoutException e) {
			homeExist = false;
			LOGGER.error(e.getMessage());
		}
		return homeExist;
	}

	public void logout() throws InterruptedException {
		LOGGER.info("Twitter account -> Logout for [" + user.getUsername() + "]");
		WebElement popup = wait.until(new Function<WebDriver, WebElement>() {
			String popup_xpath = "/html/body/div/div/div/div[2]/header/div/div/div/div[2]/div/div";

			public WebElement apply(WebDriver drv) {
				return drv.findElement(By.xpath(popup_xpath));
			}
		});
		popup.click();
		Thread.sleep(Duration.ofSeconds(1).toMillis());

		WebElement logout = wait.until(new Function<WebDriver, WebElement>() {
			String logout_xpath = "/html/body/div/div/div/div[1]/div[2]/div/div/div[2]/div/div[2]/div/div/div/div/div/a[2]";

			public WebElement apply(WebDriver drv) {
				return drv.findElement(By.xpath(logout_xpath));
			}
		});
		logout.click();
		Thread.sleep(Duration.ofSeconds(1).toMillis());

		WebElement logout_confirm = wait.until(new Function<WebDriver, WebElement>() {
			String logout_confirm_xpath = "/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div[3]/div[2]";

			public WebElement apply(WebDriver drv) {
				return drv.findElement(By.xpath(logout_confirm_xpath));
			}
		});
		logout_confirm.click();
		Thread.sleep(Duration.ofSeconds(3).toMillis());
	}

	public void tweet(Set<PostData> post_data_set) throws InterruptedException {
		this.tweetSet = post_data_set;
		filterTweet(this.tweetSet);
		LOGGER.info("Start to tweet");
		LOGGER.info("Actual Tweet Size : " + tweetSet.size());
		if (tweetSet.size() < 1) {
			LOGGER.info("No new tweet found. Returned for " + user.getUsername());
			return;
		}
		for (PostData data : tweetSet) {
			LOGGER.info("To tweet : " + data.getTelegramPostID());
		}
		tweeted_record_file = new File(FOLDER, user.getUsername() + "_" + Helper.dateToTag(2) + ".log");
		TweetedURLConfig config = tweeted_config.loadConfiguration(tweeted_record_file);
		int index = 0;
		boolean waiting_required = false;
		for (Iterator<PostData> dataIterator = tweetSet.iterator(); dataIterator.hasNext();) {
			// Stop app if shutdown signal is found in app configuration.
			if (handleShutdownSignal()) {
				return;
			}
			if (waiting_required) {
				int delay = 90 + RANDOM.nextInt(90); // Wait at least 90 seconds to avoid twitter account suspending or
				// temporary limiting some features.
				LOGGER.info(formatInfo(user.getUsername()) + " is waiting for " + delay + " seconds.");
				Thread.sleep(Duration.ofSeconds(delay).toMillis());
			}
			PostData data = dataIterator.next();
			if (config != null) {
				if (config.getTweetedList().contains(tweeted_config.createTweetedURL(data.getTwitterURL()))) {
					LOGGER.warn("URL is already found in tweeted records for " + user.getUsername());
					continue;
				}
			}
			LOGGER.info("It is time to tweet : " + data.getTelegramPostID());
			LOGGER.info("Tweet URL : \n" + data.getTwitterURL());
			driver.get(data.getTwitterURL());

			WebElement tweet_btn = wait.until(new Function<WebDriver, WebElement>() {
				String tweet_btn_xpath = "/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]"
						+ "/div[2]/div/div[3]/div/div/div/div[1]/div/div/div/div/div[2]/div[3]/div/div/div[2]/div[4]";

				public WebElement apply(WebDriver drv) {
					return drv.findElement(By.xpath(tweet_btn_xpath));
				}
			});
			tweet_btn.click();

			tweeted_config.recordTweetedURL(tweeted_record_file, data.getTwitterURL());
			LOGGER.info("Tweeted Post: " + data.getTelegramPostID());
			LOGGER.info("Tweeted By: " + formatInfo(user.getUserConfig().getDisplayName()) + " -> for "
					+ formatInfo(data.getChannelName()) + " Channel");
			LOGGER.info("Remaining Tweet : " + (tweetSet.size() - (index + 1)));
			updateLastTweet(data);
			index++;
			waiting_required = true;
		}
	}

	private String formatInfo(String info) {
		return "[" + info + "]";
	}

	private boolean handleShutdownSignal() throws InterruptedException {
		app_config = Configuration.loadConfiguration();
		if (app_config == null) {
			LOGGER.error(String.format("Failed to load %s in app directory!", Configuration.CONFIG_FILE.getName()));
			LOGGER.error("Program will exit.");
			System.exit(0);
		} else {
			if (app_config.getAppConfig().isAppShutdownSignal()) {
				LOGGER.warn("Shutdown signal is detected by " + user.getUsername());
				if (isHomeExist()) {
					logout();
				}
				LOGGER.info("Logout OK for " + user.getUsername());
				return true;
			}
		}
		return false;
	}

	private void updateLastTweet(PostData data) throws InterruptedException {
		synchronized ("Wait") {
			String tweetedChannelURL = data.getChannelURL();
			String postId = data.getTelegramPostID();
			Configuration config = Configuration.loadConfiguration();
			for (TwitterUserConfig user_config : config.getTwitterUserList()) {
				if (user.equals(user_config.convertUser())) {
					boolean history_found = false;
					history_list = new ArrayList<>();
					for (LastTweetHistoryConfig last_tweet_history : user_config.getLastTweetHistory()) {
						history_list.add(last_tweet_history);
						if (tweetedChannelURL.equals(last_tweet_history.getChannelURL())) {
							history_found = true;
							last_tweet_history.setLastTweet(postId);
							break;
						}
					}
					if (!history_found) {
						LastTweetHistoryConfig last_tweet_history = new LastTweetHistoryConfig();
						last_tweet_history.setChannelURL(tweetedChannelURL);
						last_tweet_history.setLastTweet(postId);
						history_list.add(last_tweet_history);
						user_config.getLastTweetHistory().add(last_tweet_history);

					}
					break;
				}
			}
			Helper.updateConfig(config);
			Thread.sleep(Duration.ofMillis(200).toMillis());
		}
	}

	public void filterTweet(Set<PostData> postDataSet) {
		if (null == history_list) {
			LOGGER.info("No History List found for " + user.getUsername());
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

	public WebDriverSupporter getDriverSupport() {
		return driverSupport;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public Wait<WebDriver> getWait() {
		return wait;
	}
}
