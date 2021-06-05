package autotweet;

import java.util.Set;

public class Main {

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new ShutDownHook());
		
		boolean loginFail = false;
		boolean needToSleep = false;
		try {
			ClickAndTweet.twitterLogin();
		} catch (Exception e) {
			loginFail = true;
			e.printStackTrace();
		}
		if (loginFail) {
			System.out.println("Twitter Login Fail");
			return;
		}
		
		while (true) {
			if (needToSleep) {
				try {
					// Wait for 15 minutes
					int scheduler = 15;
					System.out.println("Wait for " + scheduler + " minutes for next tweet");
					System.out.println("Press Ctrl+C to stop program. :D");
					Thread.sleep(scheduler * 1000 * 60);
					System.out.println("Time to tweet again.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			needToSleep = true;

			try {
				Set<PostData> pdSet = ClickAndTweet.getTodayTelegramPosts();
				System.out.println("Batch Tweet Size : " + pdSet.size());
				ClickAndTweet.tweet(pdSet);
				System.out.println("Done a batch tweet.");
			} catch (Exception e1) {
				e1.printStackTrace();
			}finally {
			}
		}
	}
}

class ShutDownHook extends Thread {
	@Override
	public void run() {
		System.out.println("@End");
		ClickAndTweet.getWebDriver().close();
	}
}
