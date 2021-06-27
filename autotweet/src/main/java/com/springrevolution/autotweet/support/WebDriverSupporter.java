package com.springrevolution.autotweet.support;

import java.time.Duration;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.springrevolution.atuotweet.config.AppConfig;

public class WebDriverSupporter {
	private WebDriver telegramDriver;
	private WebDriver twitterDriver;
	private static Wait<WebDriver> telegramWait;
	private static Wait<WebDriver> twitterWait;
	
//	private static WebDriverWait telegramWait;
//	private static WebDriverWait twitterWait;

	public WebDriverSupporter(int index, AppConfig config) {
		ChromeOptions telegramOpt = new ChromeOptions();
		if (!config.isShowTelegramUI()) {
			telegramOpt.addArguments("--headless");
		}
//		telegramOpt.addArguments("no-sandbox");
		telegramOpt.addArguments("window-size=800,500");
		telegramDriver = new ChromeDriver(telegramOpt);
		
		ChromeOptions twitterOpt = new ChromeOptions();
		if (!config.isShowTwitterUI()) {
			twitterOpt.addArguments("--headless");			
		}
//		twitterOpt.addArguments("no-sandbox");
		twitterOpt.addArguments("window-size=800,500");
		twitterDriver = new ChromeDriver(twitterOpt);
		int x = ((index % 4) * 350) + 10;
		int y = (((index % 9) / 4) * 400) + 10;
		((ChromeDriver)twitterDriver).manage().window().setPosition(new Point(x, y));
		
		
		telegramWait = new FluentWait<WebDriver>(telegramDriver)
				  .withTimeout(Duration.ofSeconds(20))
				  .pollingEvery(Duration.ofSeconds(2))
				  .ignoring(NoSuchElementException.class);
		
		twitterWait = new FluentWait<WebDriver>(twitterDriver)
				  .withTimeout(Duration.ofSeconds(20))
				  .pollingEvery(Duration.ofSeconds(2))
				  .ignoring(NoSuchElementException.class);
		
//		telegramWait = new WebDriverWait(telegramDriver, 10);
//		twitterWait = new WebDriverWait(twitterDriver, 10);
	}

	public WebDriver getTelegramDriver() {
		return telegramDriver;
	}

	public WebDriver getTwitterDriver() {
		return twitterDriver;
	}

	public Wait<WebDriver> getTelegramWait() {
		return telegramWait;
	}

	public Wait<WebDriver> getTwitterWait() {
		return twitterWait;
	}
	
	
}
