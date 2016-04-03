package com.geminno.erhuo.utils;

public class GetExpressageCom {
	public static String getExpressageName(String expressageName){
		String str = expressageName;
		if(str.equals("顺丰")){
			str = "sf";
		}else if(str.equals("申通")){
			str = "sto";
		}else if(str.equals("圆通")){
			str = "yt";
		}else if(str.equals("韵达")){
			str = "yd";
		}else if(str.equals("天天")){
			str= "tt";
		}else if(str.equals("EMS")){
			str = "ems";
		}else if(str.equals("中通")){
			str = "zto";
		}else if(str.equals("汇通")){
			str = "ht";
		}
		return str;
	}
}
