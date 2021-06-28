package com.springrevolution.autotweet.datacollector;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.springrevolution.autotweet.config.ChannelConfig;
import com.springrevolution.autotweet.data.PostData;
import com.springrevolution.autotweet.support.WebDriverSupporter;

public class ClickAndTweetChannel extends TelegramChannel{
	
	public ClickAndTweetChannel(WebDriverSupporter driver_support, ChannelConfig channelConfig) {
		super(driver_support, channelConfig);
	}

//	@Override
//	public synchronized Set<PostData> getTodayPosts() throws InterruptedException {
//		Set<PostData> postDataSet = new TreeSet<>();
//		String url = this.channelURL + "?q=" + Helper.dateToTag(1).replace("#", "%23");
//		System.out.println(url);
//		driver.get(url);
//		Thread.sleep(2000);
//		List<WebElement> elements = driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
////		List<WebElement> elements = wait.until(new Function<WebDriver, List<WebElement>>() {
////			  public List<WebElement> apply(WebDriver drv) {
////			    return driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
////			  }
////			});
//		System.out.println("Pattern 1 Size : " + elements.size());
//		collectPostData(elements, postDataSet);
//
//		url = this.channelURL + "?q=" + Helper.dateToTag(2).replace("#", "%23");
//		System.out.println(url);
//		driver.get(url);
//		Thread.sleep(2000);
//		elements = driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
////		elements = wait.until(new Function<WebDriver, List<WebElement>>() {
////			  public List<WebElement> apply(WebDriver driver) {
////			    return driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
////			  }
////			});
//		System.out.println("Pattern 2 Size : " + elements.size());
//		collectPostData(elements, postDataSet);
//		return postDataSet;
//	}
	
	@Override
	protected synchronized void collectPostData(List<WebElement> elements, Set<PostData> postDataList) {
		for (WebElement post : elements) {
			String data_post = post.getAttribute("data-post");
			String text = post.getText();
			if (text.contains("#WhatsHappeningInMyanmar")) {
				PostData data = new PostData();
				data.setChannelName(this.channelName);
				data.setChannelURL(this.channelURL);
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
	
}
