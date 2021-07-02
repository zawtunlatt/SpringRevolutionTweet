package com.springrevolution.autotweet.data;

import com.springrevolution.autotweet.config.TwitterUserConfig;

public class TwitterUser implements Comparable<TwitterUser>{

	private String username;
	
	private String email;
	
	private String password;
	
	private TwitterUserConfig userConfig;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public TwitterUserConfig getUserConfig() {
		return userConfig;
	}

	public void setUserConfig(TwitterUserConfig userConfig) {
		this.userConfig = userConfig;
	}

	@Override
		public boolean equals(Object obj) {
			if (obj instanceof TwitterUser) {
				TwitterUser other = (TwitterUser) obj;
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
	public int compareTo(TwitterUser o) {
		return this.getUsername().compareTo(o.getUsername());
	}
}
