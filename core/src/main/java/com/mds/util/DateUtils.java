package com.mds.util;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mds.common.Constants;
import org.springframework.context.i18n.LocaleContextHolder;

import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date Utility Class used to convert Strings to Dates and Timestamps
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *         Modified by <a href="mailto:dan@getrolling.com">Dan Kibler </a>
 *         to correct time pattern. Minutes should be mm not MM (MM is month).
 */
public final class DateUtils extends org.apache.commons.lang.time.DateUtils {
    private static Logger log = LoggerFactory.getLogger(DateUtils.class);
    private static final String TIME_PATTERN = "HH:mm";
    private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
    		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" };
    
    //public static Date Now = Calendar.getInstance().getTime();
    
    public static Date MinValue = new GregorianCalendar(1899, 11, 30, 0, 0, 0).getTime();//new Date(Long.MIN_VALUE);
      
    public static Date MaxValue = new GregorianCalendar(9999, 11, 31, 23, 59, 59).getTime();//new Date(Long.MAX_VALUE);
    
    /**
     * Checkstyle rule: utility classes should not have public constructor
     */
    private DateUtils() {
    }
    
    public static Date Now() {
    	return Calendar.getInstance().getTime();
    }
    
    public static Date Today() {
    	return getDateOnly();
    }

    /**
     * Return default datePattern (MM/dd/yyyy)
     *
     * @return a string representing the date pattern on the UI
     */
    public static String getDatePattern() {
        Locale locale = LocaleContextHolder.getLocale();
        String defaultDatePattern;
        try {
            defaultDatePattern = ResourceBundle.getBundle(Constants.BUNDLE_KEY, locale)
                    .getString("date.format");
        	//defaultDatePattern = I18nUtils.getString("date.format", locale);
        } catch (MissingResourceException mse) {
            defaultDatePattern = "MM/dd/yyyy";
        }

        return defaultDatePattern;
    }

    public static String getDateTimePattern() {
        return DateUtils.getDatePattern() + " HH:mm:ss.S";
    }

    /**
     * This method attempts to convert an Oracle-formatted date
     * in the form dd-MMM-yyyy to mm/dd/yyyy.
     *
     * @param aDate date from database as a string
     * @return formatted string for the ui
     */
    public static String convertFrom(Date aDate) {
        SimpleDateFormat df;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(getDatePattern());
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * This method generates a string representation of a date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param strDate a string representation of a date
     * @return a converted Date object
     * @throws ParseException when String doesn't match the expected format
     * @see java.text.SimpleDateFormat
     */
    public static Date convertStringToDate(String aMask, String strDate)
            throws ParseException {
        SimpleDateFormat df;
        Date date;
        df = new SimpleDateFormat(aMask);

        if (log.isDebugEnabled()) {
            log.debug("converting '" + strDate + "' to date with mask '" + aMask + "'");
        }

        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            //log.error("ParseException: " + pe);
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }

        return (date);
    }

    /**
     * This method returns the current date time in the format:
     * MM/dd/yyyy HH:MM a
     *
     * @param theTime the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime) {
        return getDateTime(TIME_PATTERN, theTime);
    }

    /**
     * This method returns the current date in the format: MM/dd/yyyy
     *
     * @return the current date
     * @throws ParseException when String doesn't match the expected format
     */
    public static Calendar getToday() throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(getDatePattern());

        // This seems like quite a hack (date -> string -> date),
        // but it works ;-)
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));

        return cal;
    }

    /**
     * This method generates a string representation of a date's date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     * @see java.text.SimpleDateFormat
     */
    public static String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            log.warn("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * This method generates a string representation of a date based
     * on the System Property 'dateFormat'
     * in the format you specify on input
     *
     * @param aDate A date to convert
     * @return a string representation of the date
     */
    public static String convertDateToString(Date aDate) {
        return getDateTime(getDatePattern(), aDate);
    }

    /**
     * This method converts a String to a date using the datePattern
     *
     * @param strDate the date to convert (in format MM/dd/yyyy)
     * @return a date object
     * @throws ParseException when String doesn't match the expected format
     */
    public static Date convertStringToDate(final String strDate) throws ParseException {
        return convertStringToDate(getDatePattern(), strDate);
    }
    
    /**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}
	
	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}
	
	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}
	
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}
	
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date parseDate(Object str, String aMask) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), new String[] {aMask});
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(24*60*60*1000);
	}
	
	public static Date getValidateDate(Date date) {
		if (date != null && date.after(DateUtils.MinValue))
			return date;
		
		return null;
	}
	
	public static Date getFromTimeString(String time) {
		return mergeTimeFrom(Now(), time);
	}
	
	public static Date mergeTimeFrom(Date date, String time) {
		if(date==null) {
			return null;
		}

		if (StringUtils.split(time, ':').length == 2) {
			time = time.concat(":00");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date= sdf.parse(formatDate(date, "yyyy-MM-dd") + " " + time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date getDateOnly() {
		Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	
    	return cal.getTime();
	}
    
	public static Date getDateStart(Date date) {
		if(date==null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date= sdf.parse(formatDate(date, "yyyy-MM-dd")+" 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date getDateEnd(Date date) {
		if(date==null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date= sdf.parse(formatDate(date, "yyyy-MM-dd") +" 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date fromFileTime(FileTime fileTime) {
		return new Date(fileTime.toMillis());
	}
	
	public static ZonedDateTime fromFileTimeUTC(FileTime fileTime) {
		long cTime = fileTime.toMillis();
		//ZonedDateTime t = Instant.ofEpochMilli(cTime).atZone(ZoneId.of("UTC"));
		//String dateCreated = DateTimeFormatter.ofPattern("MM/dd/yyyy").format(t);
		//System.out.println(dateCreated);
		return Instant.ofEpochMilli(cTime).atZone(ZoneId.of("UTC"));
	}
	
	public static String jsonSerializer(Date date) {
		if (date == null)
			return "/Date(" + Now().getTime() + ")/";
		else
			return "/Date(" + date.getTime() + ")/";
	}
		
	public static String jsonSerializer(Date date, Date defaDate) {
		if (date == null) {
			if (defaDate == null) {
				return "/Date(" + MinValue.getTime() + ")/";
			}else {
				return "/Date(" + defaDate.getTime() + ")/";
			}
		}
		else
			return "/Date(" + date.getTime() + ")/";
	}
	
	public static String jsonSerializer(Date date, boolean blankIfNULLOrMin) {
		if (blankIfNULLOrMin) {
			if (date != null && date.after(DateUtils.MinValue)) {
				return "/Date(" + date.getTime() + ")/";
			}else {
				return StringUtils.EMPTY;
			}
		}else {
			return jsonSerializer(date, null);
		}
	}
	
	
	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
//		System.out.println(formatDate(parseDate("2010/3/6")));
//		System.out.println(getDate("yyyy年MM月dd日 E"));
//		long time = new Date().getTime()-parseDate("2012-11-19").getTime();
//		System.out.println(time/(24*60*60*1000));
	}
	
	private static Pattern p = Pattern
	        .compile("(\\d+)d\\s+(\\d+)h\\s+(\\d+)m\\s+(\\d+)s");

	/**
	 * Parses a duration string of the form "98d 01h 23m 45s" into milliseconds.
	 * 
	 * @throws ParseException
	 */
	public static long parseDuration(String duration) throws ParseException {
	    Matcher m = p.matcher(duration);

	    long milliseconds = 0;

	    if (m.find() && m.groupCount() == 4) {
	        int days = Integer.parseInt(m.group(1));
	        milliseconds += TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
	        int hours = Integer.parseInt(m.group(2));
	        milliseconds += TimeUnit.MILLISECONDS
	                .convert(hours, TimeUnit.HOURS);
	        int minutes = Integer.parseInt(m.group(3));
	        milliseconds += TimeUnit.MILLISECONDS.convert(minutes,
	                TimeUnit.MINUTES);
	        int seconds = Integer.parseInt(m.group(4));
	        milliseconds += TimeUnit.MILLISECONDS.convert(seconds,
	                TimeUnit.SECONDS);
	    } else {
	        throw new ParseException("Cannot parse duration " + duration, 0);
	    }

	    return milliseconds;
	}
	
	public static long getSecondsFromFormattedDuration(String duration){
        if(duration==null)
            return 0;
        try{

            Pattern patternDuration = Pattern.compile("\\d+(?::\\d+){0,2}");

            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            if(patternDuration.matcher(duration).matches()){
                String[] tokens = duration.split(":");
                if(tokens.length==1){
                    seconds = Integer.parseInt(tokens[0]);
                }else if(tokens.length == 2){
                    minutes = Integer.parseInt(tokens[0]);
                    seconds = Integer.parseInt(tokens[1]);
                }else{
                    hours = Integer.parseInt(tokens[0]);
                    minutes = Integer.parseInt(tokens[1]);
                    seconds = Integer.parseInt(tokens[2]);
                }

                return 3600 * hours + 60 * minutes + seconds;
            }else
                return 0;

        }catch (NumberFormatException ignored){
            return 0;
        }

	}
}
