package com.geminno.erhuo.utils;

import java.io.IOException;
import java.util.Properties;

public class Url {
	
	public static String getUrlHead(){
		Properties prop = new Properties();
		String urlHead = null;
		try {
			prop.load(Url.class.getResourceAsStream("/com/geminno/erhuo/utils/url.properties"));
			urlHead = prop.getProperty("url");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urlHead;
	}
	//heikki
	public static String getHeikkiUrlHead(){
		Properties prop = new Properties();
		String urlHead = null;
		try {
			prop.load(Url.class.getResourceAsStream("/com/geminno/erhuo/utils/url.properties"));
			urlHead = prop.getProperty("heikkiUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urlHead;
	}
}
