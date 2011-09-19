package com.stretchcom.sandbox.server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GMT {
	// only supports the format: YYYY-MM-DD hh:mm
	// parses date using specified time zone -- don't want to use the default which depends on server configuration
	public static Date stringToDate(String theDateStr, TimeZone theTimeZone) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			if(theTimeZone != null) df.setTimeZone(theTimeZone);
			return df.parse(theDateStr);
		} catch (ParseException e) {
			return null;
		}
	}

}
