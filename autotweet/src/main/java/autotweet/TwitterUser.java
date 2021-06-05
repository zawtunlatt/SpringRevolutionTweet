package autotweet;

public class TwitterUser {
	
	public static TwitterUser getDefault() {
//		TwitterUser user = new TwitterUser();
//		user.setUsername("");
//		user.setPassword("");
//		return user;
		TwitterUser user = Helper.getTweeterUser();
		if (user.getUsername().isBlank() || user.getPassword().isBlank()) {
			throw new IllegalArgumentException("User Info not found!");
		}
		return user;

	}

	private String username;
	
	private String password;

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
	
	
}
