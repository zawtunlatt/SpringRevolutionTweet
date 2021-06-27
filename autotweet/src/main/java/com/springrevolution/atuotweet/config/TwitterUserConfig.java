package com.springrevolution.atuotweet.config;

import java.util.ArrayList;
import java.util.List;

import com.springrevolution.autotweet.data.TwitterUser;

public class TwitterUserConfig  implements Comparable<TwitterUserConfig> {
	private String username;
	
	private String password;
	
	private String displayName = "Not Set";
	
	private boolean tweet = true;
	
	private List<ChannelConfig> channelList = new ArrayList<>();
	
	private List<LastTweetHistoryConfig> lastTweetHistory = new ArrayList<>();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<ChannelConfig> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<ChannelConfig> channelList) {
		this.channelList = channelList;
	}

	public List<LastTweetHistoryConfig> getLastTweetHistory() {
		if (null == lastTweetHistory) {
			lastTweetHistory = new ArrayList<LastTweetHistoryConfig>();
			return lastTweetHistory;
		}
		return lastTweetHistory;
	}

	public void setLastTweetHistory(List<LastTweetHistoryConfig> lastTweetHistory) {
		this.lastTweetHistory = lastTweetHistory;
	}

	public boolean isTweet() {
		return tweet;
	}

	public void setTweet(boolean tweet) {
		this.tweet = tweet;
	}

	public TwitterUser convertUser() {
		TwitterUser user = new TwitterUser();
		user.setUsername(this.getUsername());
		user.setPassword(this.getPassword());
		user.setUserConfig(this);
		return user;
	}

	@Override
		public boolean equals(Object obj) {
			if (obj instanceof TwitterUserConfig) {
				TwitterUserConfig other = (TwitterUserConfig) obj;
				if (this.username.equals(other.getUsername())
						&& this.password.equals(other.getPassword())) {
					return true;
				} else {
					return false;
				}
			}
			return super.equals(obj);
		}

	@Override
	public int compareTo(TwitterUserConfig o) {
		return this.getUsername().compareTo(o.getUsername());
	}
}
