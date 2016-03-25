package com.geminno.erhuo.entity;

import java.io.IOException;
import java.util.Properties;

public class Url {


	public static String urllogin = "http://10.201.1.16:8080/secondHandShop/LoginServlet";
	public static String urlregister = "http://10.201.1.16:8080/secondHandShop/AddUserServlet";
	public static String urlreget = "http://10.201.1.16:8080/secondHandShop/UpdateUserServlet";
	


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
