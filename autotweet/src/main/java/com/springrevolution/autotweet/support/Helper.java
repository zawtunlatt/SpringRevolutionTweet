package com.springrevolution.autotweet.support;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.springrevolution.autotweet.config.Configuration;

public class Helper {
	private static Map<String, String> monthMap = new HashMap<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(Helper.class);
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
//	private static File logFile = new File("tweeted.log");
//	private static File twitter_user = new File("twitter-user.txt");
	
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
	
	public static void updateConfig(Configuration app_config) {
		Yaml yaml = new Yaml();
		yaml.setName("Hello World");
		try {
			FileWriter fw = new FileWriter(Configuration.CONFIG_FILE);
		    yaml.dump(app_config, fw);
		    fw.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	    LOGGER.info("Configuration Updated.");
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
