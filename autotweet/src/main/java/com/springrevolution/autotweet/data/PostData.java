package com.springrevolution.autotweet.data;

public class PostData implements Comparable<PostData>{
	
	private String channelName;
	
	private String channelURL;
	
	private String telegramPostID;
	
	private String twitterURL;
	
	
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
	
	public String getTelegramPostID() {
		return telegramPostID;
	}
	public void setTelegramPostID(String telegramPostID) {
		this.telegramPostID = telegramPostID;
	}
	public String getTwitterURL() {
		return twitterURL;
	}
	public void setTwitterURL(String twitterURL) {
		this.twitterURL = twitterURL;
	}
	
	@Override
	public String toString() {
		return this.telegramPostID + " : " + this.twitterURL;
	}
	
	@Override
	public boolean equals(Object obj) {
		System.out.println("Equal ? : " + this.telegramPostID.equals(((PostData)obj).telegramPostID));
		return this.telegramPostID.equals(((PostData)obj).telegramPostID);
	}
	@Override
	public int compareTo(PostData o) {
		Integer current_tweet_id = Integer.parseInt(this.getTelegramPostID().split("/")[1]);
//		System.out.println("Current : " + current_tweet_id);
		Integer next = Integer.valueOf(o.getTelegramPostID().split("/")[1]);
		return current_tweet_id.compareTo(next);
	}

//	public static void main(String[] args) {
//		PostData p1 = new PostData();
//		p1.setTelegramPostID("aa/300");
//		
//		PostData p2 = new PostData();
//		p2.setTelegramPostID("bb/900");
//		
//		PostData p3 = new PostData();
//		p3.setTelegramPostID("cc/100");
//		
//		Set<PostData> ps = new TreeSet<>();
//		ps.add(p1);
//		ps.add(p2);
//		ps.add(p3);
//		System.out.println(ps);
//	}
}

