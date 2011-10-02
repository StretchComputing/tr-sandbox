package com.stretchcom.sandbox.server;

public class ApiStatusCode {
	public static final String SERVER_ERROR = "0";
	public static final String SUCCESS = "100";
	
	public static final String INVALID_USER_CREDENTIALS = "200";
	public static final String INVALID_STATUS = "201";
	public static final String INVALID_LOG_LEVEL = "202";
	
	public static final String FEEDBACK_ID_REQUIRED = "300";
	public static final String CRASH_DETECT_ID_REQUIRED = "301";
	public static final String CLIENT_LOG_ID_REQUIRED = "302";
	
	public static final String INVALID_STATUS_PARAMETER = "400";
	public static final String INVALID_RECORDED_DATE_PARAMETER = "401";
	public static final String INVALID_DETECTED_DATE_PARAMETER = "402";

	public static final String USER_NOT_FOUND = "600";
	public static final String FEEDBACK_NOT_FOUND = "601";
	public static final String CRASH_DETECT_NOT_FOUND = "602";
	public static final String CLIENT_LOG_NOT_FOUND = "603";
}
