package com.geminno.erhuo.utils;

public class GetExpressageCom {
	public static String getExpressageCode(String expressageName) {
		String str = expressageName;
		if (str.equals("顺丰") || str.equals("顺丰快递") || str.equals("顺丰物流")) {
			str = "sf";
		} else if (str.equals("申通") || str.equals("申通快递") || str.equals("申通物流")) {
			str = "sto";
		} else if (str.equals("圆通") || str.equals("圆通快递") || str.equals("圆通物流")) {
			str = "yt";
		} else if (str.equals("韵达") || str.equals("韵达快递") || str.equals("韵达物流")) {
			str = "yd";
		} else if (str.equals("天天") || str.equals("天天快递") || str.equals("天天物流")) {
			str = "tt";
		} else if (str.equals("EMS") || str.equals("EMS快递")
				|| str.equals("EMS物流") || str.equals("邮政EMS")) {
			str = "ems";
		} else if (str.equals("中通") || str.equals("中通快递") || str.equals("中通物流")) {
			str = "zto";
		} else if (str.equals("汇通") || str.equals("汇通快递") || str.equals("汇通物流")) {
			str = "ht";
		}
		return str;
	}

	public static String getExpressageName(String expressageCode) {
		String str = expressageCode;
		if (str.equals("sf")) {
			str = "顺丰快递";
		} else if (str.equals("sto")) {
			str = "申通快递";
		} else if (str.equals("yt")) {
			str = "圆通快递";
		} else if (str.equals("yd")) {
			str = "韵达快递";
		} else if (str.equals("tt")) {
			str = "天天快递";
		} else if (str.equals("ems")) {
			str = "邮政EMS";
		} else if (str.equals("zto")) {
			str = "中通快递";
		} else if (str.equals("ht")) {
			str = "汇通快递";
		}
		return str;
	}

}
