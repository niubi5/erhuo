package com.geminno.erhuo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MySdf {
	public static String getDateToString(Date date) {
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 return sdf.format(date);		 
	}
}
