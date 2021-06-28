package com.springrevolution.autotweet.config;

public class LastTweetHistoryConfig {
	private String channelURL;
	private String lastTweet;
	public String getChannelURL() {
		return channelURL;
	}
	public void setChannelURL(String channelURL) {
		this.channelURL = channelURL;
	}
	public String getLastTweet() {
		return lastTweet;
	}
	public void setLastTweet(String lastTweet) {
		this.lastTweet = lastTweet;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LastTweetHistoryConfig) {
			LastTweetHistoryConfig other = (LastTweetHistoryConfig) obj;
			if (this.channelURL.equals(other.getChannelURL())) {
				return true;
			} else {
				return false;
			}
		}
		return super.equals(obj);
	}
}
