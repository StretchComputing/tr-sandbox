package com.stretchcom.sandbox.server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

public class GMT {
	private static final Logger log = Logger.getLogger(GMT.class.getName());

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
	
	public static String convertToLocalDate(Date theDate, TimeZone theTimeZone) {
		// defaults format to: "yyyy-MM-dd HH:mm"
		return convertToLocalDate(theDate, theTimeZone, "yyyy-MM-dd HH:mm");
	}
	
	public static String convertToLocalDate(Date theDate, TimeZone theTimeZone, String theDateFormat) {
		if(theDate == null || theDateFormat == null) {
			return null;
		}
		
		DateFormat df = new SimpleDateFormat(theDateFormat);
		if(theTimeZone != null) df.setTimeZone(theTimeZone);
		
		String timezoneStr = theTimeZone == null ? "<not_specified>" : theTimeZone.getDisplayName();
		log.info("convertToLocalDate(): timezone = " + timezoneStr + " local date = " + df.format(theDate));
		return df.format(theDate);
	}
}
