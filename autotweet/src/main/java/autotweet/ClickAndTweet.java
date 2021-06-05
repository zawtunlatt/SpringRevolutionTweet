package autotweet;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

public class ClickAndTweet extends TelegramTweet{
	private static WebDriver driver;
	private static Wait<WebDriver> wait;
	
	public static Set<PostData> getTodayTelegramPosts() throws InterruptedException {
		Set<PostData> postDataSet = new TreeSet<>();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		WebDriver driver = new ChromeDriver(options);
		String url = "https://t.me/s/mmtweet?q=" + Helper.dateToTag(1).replace("#", "%23");
		System.out.println(url);
		driver.get(url);
		Thread.sleep(2000);
		List<WebElement> elements = driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
//		List<WebElement> elements = wait.until(new Function<WebDriver, List<WebElement>>() {
//			  public List<WebElement> apply(WebDriver driver) {
//			    return driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
//			  }
//			});
		System.out.println("Pattern 1 Size : " + elements.size());
		collectPostData(elements, postDataSet);

		url = "https://t.me/s/mmtweet?q=" + Helper.dateToTag(2).replace("#", "%23");
		System.out.println(url);
		driver.get(url);
		Thread.sleep(2000);
		elements = driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
//		elements = wait.until(new Function<WebDriver, List<WebElement>>() {
//			  public List<WebElement> apply(WebDriver driver) {
//			    return driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
//			  }
//			});
		System.out.println("Pattern 2 Size : " + elements.size());
		collectPostData(elements, postDataSet);
		return postDataSet;
	}
	
	private static void collectPostData(List<WebElement> elements, Set<PostData> postDataList) {
		for (WebElement post : elements) {
			String data_post = post.getAttribute("data-post");
			String text = post.getText();
			if (text.contains("#WhatsHappeningInMyanmar")) {
				PostData data = new PostData();
				data.setTelegramPostID(data_post);
				StringBuilder selector = new StringBuilder("div > div:nth-child(2) a.tgme_widget_message_inline_button.url_button");
				List<WebElement> els = post.findElements(By.cssSelector(selector.toString()));
				for (WebElement e : els) {
					String url = e.getAttribute("href");
					data.setTwitterURL(url);
					postDataList.add(data);
					break;
				}				
			}
		}
	}
	
	public static void twitterLogin() {
		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless");
		driver = new ChromeDriver(options);
		TwitterUser user = TwitterUser.getDefault();
		wait = new FluentWait<WebDriver>(driver)
				  .withTimeout(Duration.ofSeconds(20))
				  .pollingEvery(Duration.ofSeconds(3))
				  .ignoring(NoSuchElementException.class);
		
		driver.get("https://twitter.com/login");
		

		WebElement username_input = wait.until(new Function<WebDriver, WebElement>() {
			String login_title = "//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[1]/label/div/div[2]/div/input";
			  public WebElement apply(WebDriver driver) {
			    return driver.findElement(By.xpath(login_title));
			  }
			});
		username_input.sendKeys(user.getUsername());
		
		try {
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String password_xpath = "//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[2]/label/div/div[2]/div/input"; 
		WebElement password_input = driver.findElement(By.xpath(password_xpath));
		password_input.sendKeys(user.getPassword());
		
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String login_btn_xpath = "//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[3]/div";
		WebElement login_btn = driver.findElement(By.xpath(login_btn_xpath));
		login_btn.sendKeys(Keys.ENTER);
	}
	
	public static void tweet(Set<PostData> tweetSet) throws InterruptedException {
		System.out.println("Start to tweet");
		tweetSet = TelegramTweet.filterTweet(tweetSet);
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
			System.out.println("Time to tweet : " + data.getTelegramPostID());
			driver.get(data.getTwitterURL());
			
			WebElement tweet_btn = wait.until(new Function<WebDriver, WebElement>() {
				String tweet_btn_xpath = "/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]"
						+ "/div[2]/div/div[3]/div/div/div/div[1]/div/div/div/div/div[2]/div[3]/div/div/div[2]/div[4]";
				  public WebElement apply(WebDriver driver) {
				    return driver.findElement(By.xpath(tweet_btn_xpath));
				  }
				});
			tweet_btn.click();
			System.out.println("Tweeted : " + data.getTelegramPostID());
			System.out.println("Remaining Tweet : " + (tweetSet.size() - (index + 1)));
			Helper.updateLog(data.getTelegramPostID());
			int delay = 10 + random.nextInt(10);
			System.out.println("Wait for " + delay + " seconds.");
			Thread.sleep(delay * 1000);
			index++;
		}
	}

	public static WebDriver getWebDriver() {
		return driver;
	}
}
