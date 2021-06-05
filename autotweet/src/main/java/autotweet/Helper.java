package autotweet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

public class Helper {
	private static Map<String, String> monthMap = new HashMap<>();
	static {
		monthMap.put("Jan", "Jan");
		monthMap.put("Feb", "Feb");
		monthMap.put("Mar", "Mar");
		monthMap.put("Apr", "Apr");
		monthMap.put("May", "May");
		monthMap.put("Jun", "June");
		monthMap.put("Jul", "July");
		monthMap.put("Aug", "Aug");
		monthMap.put("Sep", "Sep");
		monthMap.put("Oct", "Oct");
		monthMap.put("Nov", "Nov");
		monthMap.put("Dec", "Dec");
	}
	private static File logFile = new File("tweeted.log");
	private static File twitter_user = new File("twitter-user.txt");
	
	public static String dateToTag(int pattern) {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("MMMd", new Locale("mm_MM"));
		df.setTimeZone(TimeZone.getTimeZone("Asia/Rangoon"));
		String tag = "";
		switch(pattern) {
		case 1:
			tag = "#" + df.format(date) + "Coup";
			break;
		case 2:			
			tag = "#" + df.format(date) + "Coup";
			if (tag.contains("Jun")) {
				tag = tag.replace("Jun", "June");
			} else if (tag.contains("Jul")) {
				tag = tag.replace("Jul", "July");
			}
			break;
		default:
			tag = "#" + df.format(date) + "Coup";
			break;
		}
		return tag;
	}
	
	public static String getLastTweeted() {
		String log = "";
		if (!logFile.exists()) {
			return log;
		}
		Scanner sc = null;
	    try {
	    	sc = new Scanner(logFile);
	        sc.useDelimiter("\\Z");
	        log = sc.nextLine();
        	System.out.println("Log : " + log);
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			sc.close();
		}
	    return log;
	}
	
	public static TwitterUser getTweeterUser() {
        TwitterUser user = new TwitterUser();
		Scanner sc = null;
	    try {
	    	sc = new Scanner(twitter_user);
	        String user_name = sc.nextLine().trim();
	        String pass = sc.nextLine().trim();
	        user.setUsername(user_name);
	        user.setPassword(pass);
		} catch (IOException e) {
		} catch (Exception e) {
		} finally {
			sc.close();
		}
	    return user;
	}
	
	public static void updateLog(String lastTweet) {
		try {
			logFile.delete();
			FileWriter myWriter = new FileWriter(logFile);
		    myWriter.write(lastTweet);
		    myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
//	public static void main(String[] args) {
//		Date date = new Date();
//		date.setMonth(2);
//		date.setDate(22);
//		DateFormat df = new SimpleDateFormat("MMM---d", new Locale("mm_MM"));
//		df.setTimeZone(TimeZone.getTimeZone("Asia/Rangoon"));
//		String tag = "#" + df.format(date).replace("---", "(\\w)*") + "Coup";
//		System.out.println(tag);
//		if ("#Marc02Coup".matches(tag)) {
//			System.out.println("Yes Match");
//		} else {
//			System.out.println("Not Match");
//		}
//	}
}
