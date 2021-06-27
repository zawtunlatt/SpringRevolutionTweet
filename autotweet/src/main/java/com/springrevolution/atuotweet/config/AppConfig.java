package com.springrevolution.atuotweet.config;

public class AppConfig {
	private ChannelConfig massTrendingChannel;
	
	private String massTrendingTag = "";
	
	private boolean showTelegramUI = false;
	
	private boolean showTwitterUI = true;

	public ChannelConfig getMassTrendingChannel() {
		return massTrendingChannel;
	}

	public void setMassTrendingChannel(ChannelConfig massTrendingChannel) {
		this.massTrendingChannel = massTrendingChannel;
	}

	public String getMassTrendingTag() {
		return massTrendingTag;
	}

	public void setMassTrendingTag(String massTrendingTag) {
		this.massTrendingTag = massTrendingTag;
	}

	public boolean isShowTelegramUI() {
		return showTelegramUI;
	}

	public void setShowTelegramUI(boolean showTelegramUI) {
		this.showTelegramUI = showTelegramUI;
	}

	public boolean isShowTwitterUI() {
		return showTwitterUI;
	}

	public void setShowTwitterUI(boolean showTwitterUI) {
		this.showTwitterUI = showTwitterUI;
	}
	
	
}
