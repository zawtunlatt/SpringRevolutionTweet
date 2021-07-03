package com.springrevolution.autotweet.support;

import java.time.Duration;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.springrevolution.autotweet.config.AppConfig;

public class WebDriverSupporter {
	private WebDriver telegramDriver;
	private WebDriver twitterDriver;
	private Wait<WebDriver> telegramWait;
	private Wait<WebDriver> twitterWait;

	public WebDriverSupporter(int index, AppConfig config) {
		
		System.setProperty("webdriver.chrome.driver","chromedriver.exe");
		
		ChromeOptions telegramOpt = new ChromeOptions();
		if (!config.isShowTelegramUI()) {
			telegramOpt.addArguments("--headless");
		}
//		telegramOpt.addArguments("--no-sandbox");
		telegramOpt.addArguments("window-size=800,700");
		telegramDriver = new ChromeDriver(telegramOpt);
				
		ChromeOptions twitterOpt = new ChromeOptions();
		if (!config.isShowTwitterUI()) {
			twitterOpt.addArguments("--headless");
		}
//		twitterOpt.addArguments("--no-sandbox");
		twitterOpt.addArguments("window-size=800,700");
		twitterDriver = new ChromeDriver(twitterOpt);
		int x = ((index % 4) * 350) + 5;
		int y = (((index % 9) / 4) * 400) + 5;
		((ChromeDriver)twitterDriver).manage().window().setPosition(new Point(x, y));

		
//		System.setProperty("webdriver.chrome.driver","geckodriver.exe");
//		FirefoxBinary firefoxBinary = new FirefoxBinary();
////	    firefoxBinary.addCommandLineOptions("--headless");
//	    FirefoxOptions firefoxOpt = new FirefoxOptions();
//	    firefoxOpt.setBinary(firefoxBinary);
//		FirefoxOptions telegramOpt = new FirefoxOptions();
//		telegramDriver = new FirefoxDriver(telegramOpt);
//		if (!config.isShowTelegramUI()) {
//			telegramDriver.manage().window().setPosition(new Point(-2000, 0));
//		}
//		
//		firefoxBinary = new FirefoxBinary();
////	    firefoxBinary.addCommandLineOptions("--headless");
//	    firefoxOpt = new FirefoxOptions();
//	    firefoxOpt.setBinary(firefoxBinary);
//		twitterDriver = new FirefoxDriver(firefoxOpt);
//		twitterDriver.manage().window().setSize(new Dimension(800, 700));
//		if (config.isShowTwitterUI()) {
//			int x = ((index % 4) * 350) + 5;
//			int y = (((index % 9) / 4) * 400) + 5;
//			twitterDriver.manage().window().setPosition(new Point(x, y));
//		} else {
//			twitterDriver.manage().window().setPosition(new Point(-2000, 0));
//		}

		
		telegramWait = new FluentWait<WebDriver>(telegramDriver)
				  .withTimeout(Duration.ofSeconds(20))
				  .pollingEvery(Duration.ofMillis(1500))
				  .ignoring(NoSuchElementException.class);
		
		twitterWait = new FluentWait<WebDriver>(twitterDriver)
				  .withTimeout(Duration.ofSeconds(20))
				  .pollingEvery(Duration.ofMillis(1000))
				  .ignoring(NoSuchElementException.class);
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
