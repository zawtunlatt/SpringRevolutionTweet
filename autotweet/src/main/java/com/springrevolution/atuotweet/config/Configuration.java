package com.springrevolution.atuotweet.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class Configuration {
	public static final File CONFIG_FILE = new File("@app-config.yaml");
	private AppConfig appConfig;
	private List<ChannelConfig> telegramChannelList = new ArrayList<>();
	private List<TwitterUserConfig> TwitterUserList = new ArrayList<>();

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	public List<ChannelConfig> getTelegramChannelList() {
		return telegramChannelList;
	}

	public void setTelegramChannelList(List<ChannelConfig> channelSet) {
		this.telegramChannelList = channelSet;
	}

	public List<TwitterUserConfig> getTwitterUserList() {
		return TwitterUserList;
	}

	public void setTwitterUserList(List<TwitterUserConfig> userList) {
		this.TwitterUserList = userList;
	}
	
	public static Configuration loadConfiguration() {
		Yaml yaml = new Yaml(new Constructor(Configuration.class));
		Configuration config = null;
		try {
			FileInputStream inputStream = new FileInputStream(CONFIG_FILE); 
			config = yaml.load(inputStream);
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}
		return config;
	}

	@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("[ App Config ]").append("\n");
			sb.append("Mass Trending Tag : ").append(appConfig.getMassTrendingTag()).append("\n");
			sb.append("Mass Trending Channel : ").append(appConfig.getMassTrendingChannel().getChannelURL()).append("\n\n");
			
			sb.append("[ Supported Channel List ]").append("\n");

			for (ChannelConfig channel : telegramChannelList) {
				sb.append("  - ");
				sb.append(channel.getChannelName()).append("\n");
			}
			
			sb.append("----------------------------").append("\n\n");
			sb.append("User List : ").append("\n");
			sb.append("-----------------------").append("\n");
			for (TwitterUserConfig user : TwitterUserList) {
				sb.append(user.getDisplayName()).append("\n");
				sb.append("  [Channel list for this user ]").append("\n");
				for (ChannelConfig user_config : user.getChannelList()) {
					sb.append("  - ")
					.append(user_config.getChannelName())
					.append("\n");
				}
				sb.append("  [Last Tweet History for this user]").append("\n");
				for (LastTweetHistoryConfig h : user.getLastTweetHistory()) {
					sb.append("  -> ")
					.append(h.getLastTweet())
					.append("\n");
				}
			}
			return sb.toString();
		}

	public static void main(String[] args) {
//		writeTest();
		readTest();
	}
	
	private static void readTest() {
		Yaml yaml = new Yaml(new Constructor(Configuration.class));
//		InputStream is = Configuration.class.getClassLoader().getResourceAsStream("config.yaml");
		Configuration config = null;
		try {
			config = yaml.load(new FileInputStream(CONFIG_FILE));
//			try {
//				FileWriter fw = new FileWriter(CONFIG_FILE);
//			    yaml.dump(config, fw);
//			    fw.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			System.out.println(config);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private static void writeTest() {
		Configuration config = new Configuration();
		
		AppConfig appConfig = new AppConfig();
//		appConfig.setMassTrendingTag("#Hello");
		ChannelConfig massTrending = new ChannelConfig();
		massTrending.setChannelName("Mass Trending");
		massTrending.setChannelURL("t.me/s/masstrending");
		appConfig.setMassTrendingChannel(massTrending);
		config.setAppConfig(appConfig);
		
		ChannelConfig clickAndTweet = new ChannelConfig();
		clickAndTweet.setChannelName("ClickAndTweet");
		clickAndTweet.setChannelURL("t.me/mmtweet");
		
		ChannelConfig other = new ChannelConfig();
		other.setChannelName("other");
		other.setChannelURL("t.me/other");
		
		List<ChannelConfig> channelList = new ArrayList<>();
		channelList.add(clickAndTweet);
		channelList.add(other);
		config.setTelegramChannelList(channelList);
		
		List<TwitterUserConfig> userList = new ArrayList<>();
		
		TwitterUserConfig u1 = new TwitterUserConfig();
		u1.setUsername("$username1");
		u1.setPassword("$password1");
		u1.setDisplayName("User 1");
		u1.setChannelList(channelList);
		
		TwitterUserConfig u2 = new TwitterUserConfig();
		u2.setUsername("$username2");
		u2.setPassword("$password2");
		u2.setDisplayName("User 2");
//		u2.setChannelList(channelList);
		
		
		ChannelConfig t1 = new ChannelConfig();
		t1.setChannelName("$DummyChannel1");
		t1.setChannelURL("t.me/DummyChannel1");
		
		ChannelConfig t2 = new ChannelConfig();
		t2.setChannelName("$DummyChannel2");
		t2.setChannelURL("t.me/DummyChannel2");
		
		List<ChannelConfig> c2 = new ArrayList<>();
		c2.add(t1);
		c2.add(t2);
		
		TwitterUserConfig u3 = new TwitterUserConfig();
		u3.setUsername("$username3");
		u3.setPassword("$password3");
		u3.setDisplayName("User 3");
		u3.setChannelList(c2);
		LastTweetHistoryConfig h1 = new LastTweetHistoryConfig();
		h1.setChannelURL("https://t.me/mmtweet");
		h1.setLastTweet("mmtweet/12345");
		LastTweetHistoryConfig h2 = new LastTweetHistoryConfig();
		h2.setChannelURL("https://t.me/mmtweet");
		h2.setLastTweet("mmtweet/12345");
		u3.getLastTweetHistory().add(h1);
		u3.getLastTweetHistory().add(h2);
		
		userList.add(u1);
		userList.add(u2);
		userList.add(u3);
		
		config.setTwitterUserList(userList);
		
		Yaml yaml = new Yaml();
		yaml.setName("Hello World");
		try {
			FileWriter fw = new FileWriter(CONFIG_FILE);
		    yaml.dump(config, fw);
		    fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    System.out.println("Done");
	}
}
