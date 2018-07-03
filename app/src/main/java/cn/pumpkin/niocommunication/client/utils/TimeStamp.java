package cn.pumpkin.niocommunication.client.utils;

import java.util.Date;

public class TimeStamp {

	public static int getSecondTimestampTwo(Date date){ 
		if (null == date) { 
			return 0; 
		} 
		String timestamp = String.valueOf(date.getTime()/1000); 
		return Integer.valueOf(timestamp); 
	}
	
}
