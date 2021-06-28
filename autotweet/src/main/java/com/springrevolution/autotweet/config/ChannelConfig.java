package com.springrevolution.autotweet.config;

public class ChannelConfig implements Comparable<ChannelConfig> {
	private String channelName;
	
	private String channelURL;
	
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChannelConfig) {
			ChannelConfig other = (ChannelConfig) obj;
			if (this.channelURL.equals(other.getChannelURL())) {
				return true;
			} else {
				return false;
			}
		}
		return super.equals(obj);
	}
	
	@Override
	public int compareTo(ChannelConfig o) {
		return this.getChannelURL().compareTo(o.getChannelURL());
	}
	
}
