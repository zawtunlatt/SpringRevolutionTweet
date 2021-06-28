package com.springrevolution.autotweet.datacollector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;

import com.springrevolution.autotweet.config.ChannelConfig;
import com.springrevolution.autotweet.data.PostData;
import com.springrevolution.autotweet.support.Helper;
import com.springrevolution.autotweet.support.WebDriverSupporter;

public class TelegramChannel {
	
	protected String channelName;
	
	protected String channelURL;
	
	protected WebDriver driver;
	protected Wait<WebDriver> wait;

	public TelegramChannel(WebDriverSupporter driver_support, ChannelConfig channelConfig) {
		this.driver = driver_support.getTelegramDriver();
		this.wait = driver_support.getTwitterWait();
		this.channelName = channelConfig.getChannelName();
		this.channelURL = channelConfig.getChannelURL();
	}
	
	public synchronized Set<PostData> getTodayPosts() throws InterruptedException {
		Set<PostData> postDataSet = new TreeSet<>();
		String url = channelURL + "?q=" + Helper.dateToTag(1).replace("#", "%23");
		System.out.println(url);
		driver.get(url);
		Thread.sleep(2000);
//		List<WebElement> elements = driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
		List<WebElement> elements = wait.until(new Function<WebDriver, List<WebElement>>() {
			  public List<WebElement> apply(WebDriver drv) {
			    return driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
			  }
			});
		System.out.println("Pattern 1 Size : " + elements.size());
		collectPostData(elements, postDataSet);

		url = channelURL + "?q=" + Helper.dateToTag(2).replace("#", "%23");
		System.out.println(url);
		driver.get(url);
		Thread.sleep(2000);
//		elements = driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
		elements = wait.until(new Function<WebDriver, List<WebElement>>() {
			  public List<WebElement> apply(WebDriver drv) {
			    return driver.findElements(By.cssSelector(".tgme_widget_message.js-widget_message"));
			  }
			});
		System.out.println("Pattern 2 Size : " + elements.size());
		collectPostData(elements, postDataSet);
		return postDataSet;
	}
	
	protected synchronized void collectPostData(List<WebElement> elements, Set<PostData> postDataList) {
		for (WebElement post : elements) {
			String data_post = post.getAttribute("data-post");
			String text = post.getText();
			if (text.contains("#WhatsHappeningInMyanmar")) {
				PostData data = new PostData();
				data.setChannelName(this.channelName);
				data.setChannelURL(this.channelURL);
				data.setTelegramPostID(data_post);
				
				String channel_name_css = ".tgme_widget_message_author.accent_color";
				WebElement channel_name_element = post.findElement(By.cssSelector(channel_name_css));
				text = text.replace(channel_name_element.getText(), "");
				
				String footer_css = ".tgme_widget_message_footer.compact.js-message_footer";
				WebElement footer_element = post.findElement(By.cssSelector(footer_css));
				text = text.replace(footer_element.getText(), "").trim();
				data.setTwitterURL("https://twitter.com/intent/tweet?text=" + encodeURL(text));
				postDataList.add(data);
			}
		}
	}
	
	private String encodeURL(String text) {
		try {
			text = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return text;
	}
	
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelURL() {
		return channelURL;
	}

	public void setChannelURL(String channelURL) {
		this.channelURL = channelURL;
	}
}
