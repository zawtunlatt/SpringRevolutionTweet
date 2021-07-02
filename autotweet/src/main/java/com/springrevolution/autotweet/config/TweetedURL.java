package com.springrevolution.autotweet.config;

public class TweetedURL {
	
	public TweetedURL() {
		this.url = "";
	}
	
	public TweetedURL(String url) {
		this.url = url;
	}
	private String url;

	public String getUrl() {
		if (url == null) {
			url = "";
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TweetedURL) {
			TweetedURL other = (TweetedURL) obj;
			if (this.getUrl().equals(other.getUrl())) {
				return true;
			} else {
				return false;
			}
		}
		return super.equals(obj);
	}
}