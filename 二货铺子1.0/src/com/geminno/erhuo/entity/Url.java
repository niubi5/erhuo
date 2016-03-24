package com.geminno.erhuo.entity;

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
	
}
