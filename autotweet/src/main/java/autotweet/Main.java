package autotweet;

import java.util.Set;

public class Main {

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new ShutDownHook());
		
		boolean needToSleep = false;
		
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
			
			if (!login()) {
				System.out.println("Login Fail!");
				return;
			}

			try {
				Set<PostData> pdSet = ClickAndTweet.getTodayTelegramPosts();
				System.out.println("Batch Tweet Size : " + pdSet.size());
				ClickAndTweet.tweet(pdSet);
				System.out.println("Done a batch tweet.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}finally {
				new ShutDownHook().start();
			}
		}
	}
	
	private static boolean login() {
		boolean loginSuccess = true;
		try {
			ClickAndTweet.twitterLogin();
		} catch (Exception e) {
			loginSuccess = false;
			e.printStackTrace();
		}

		return loginSuccess;
	}
}

class ShutDownHook extends Thread {
	@Override
	public void run() {
		System.out.println("@End");
		try {
			System.out.println("Web Driver will be closing soon.");
			ClickAndTweet.getWebDriver().close();
			ClickAndTweet.getWebDriver().quit();
			Runtime.getRuntime().exec("taskkill /f /im chromedriver.exe /T");
			Runtime.getRuntime().exec("taskkill /f /im chrome.exe /T");
			System.out.println("Web Driver closing is Done.");
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		}
	}
}
