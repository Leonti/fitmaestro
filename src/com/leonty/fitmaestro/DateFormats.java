package com.leonty.fitmaestro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;


public class DateFormats {

	private final Context mCtx;
	private SimpleDateFormat iso8601FormatLocal;
	
	DateFormats(Context ctx){
		iso8601FormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mCtx = ctx;
	}
	
	public String getWithYear(String dateString) throws ParseException{
		
		Date date;	
		date = iso8601FormatLocal.parse(dateString);
		return this.getWithYearFromDate(date);		
	}
	
	public String getWithYearFromDate(Date date){
		
		long when = date.getTime();
		int flags = 0;
		flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
		flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;
		String finalDateTime = android.text.format.DateUtils
						.formatDateTime(mCtx, when
								+ TimeZone.getDefault().getOffset(when),
								flags);
		return finalDateTime;
	}
	
	public String getMonthName(Date date){
		
		long when = date.getTime();
		int flags = 0;
		flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
		flags |= android.text.format.DateUtils.FORMAT_NO_MONTH_DAY;
		String finalDateTime = android.text.format.DateUtils
						.formatDateTime(mCtx, when
								+ TimeZone.getDefault().getOffset(when),
								flags);
		return finalDateTime;
	}

}
