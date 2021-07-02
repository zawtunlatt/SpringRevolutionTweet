package com.springrevolution.autotweet.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class TweetedURLConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(TweetedURLConfig.class);
	private List<TweetedURL> tweetedList = new ArrayList<>();

	public List<TweetedURL> getTweetedList() {
		if (tweetedList == null) {
			tweetedList = new ArrayList<TweetedURL>();
		}
		return tweetedList;
	}

	public void setTweetedList(List<TweetedURL> tweetedList) {
		this.tweetedList = tweetedList;
	}
	
	public TweetedURL createTweetedURL(String url) {
		return new TweetedURL(url);
	}

	public TweetedURLConfig loadConfiguration(File file) {
		Yaml yaml = new Yaml(new Constructor(TweetedURLConfig.class));
		TweetedURLConfig config = null;
		try {
			FileInputStream inputStream = new FileInputStream(file); 
			config = yaml.load(inputStream);
			inputStream.close();
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return config;
	}
	
	public void recordTweetedURL(File file, String tweetedURL) {
		TweetedURL url = new TweetedURL(tweetedURL);
		TweetedURLConfig config = loadConfiguration(file);
		if (config == null) {
			config = new TweetedURLConfig();
			config.getTweetedList().add(url);
		} else {
			config.getTweetedList().add(url);
		}
		
		Yaml yaml = new Yaml();
		yaml.setName("Hello World");
		try {
			FileWriter fw = new FileWriter(file);
		    yaml.dump(config, fw);
		    fw.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
//	public static void main(String[] args) {
//		TweetedURLConfig config = new TweetedURLConfig();
//		for (int i = 0; i < 100; i++) {
//			config.getTweetedList().add(new TweetedURL("tw/" + i));	
//		}
//		Yaml yaml = new Yaml();
//		yaml.setName("Hello World");
//		try {
//			File folder = new File("Tweet Record");
//			if (!folder.exists()) {
//				folder.mkdir();
//			}
//			FileWriter fw = new FileWriter(new File("Tweet Record/kkk_#July27.history"));
//		    yaml.dump(config, fw);
//		    fw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	    System.out.println("Done");
//	}
}
