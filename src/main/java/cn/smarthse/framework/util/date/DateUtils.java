package cn.smarthse.framework.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.util.StringUtils;

import cn.smarthse.framework.util.SimpleLunarCalendar;

/**
 * 
 * @author lipeng Joda Time，一个面向 Java™ 平台的易于使用的开源时间/日期库
 */
public class DateUtils {
	public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	private static final String TIME_FORMAT_DEFAULT = "HH:mm:ss";
	private static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
	/**
	 * 生肖
	 */
	private static final String zodiacArr[] = { "猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊" };
	/**
	 * 星座
	 */
	private static final String constellationArr[] = { "水瓶座", "双鱼座", "牡羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座",
			"天蝎座", "射手座", "魔羯座" };

	/**
	 * 星座对应的天数
	 */
	private static final int constellationEdgeDay[] = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };

	/**
	 * 星期中文字符
	 */
	private static String[] weeks = new String[] { "一", "二", "三", "四", "五", "六", "日" };

	private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();

	private static final Object object = new Object();

	// ==========================20160128 增加

	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
			"yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd HH:mm", "yyyy.MM" };

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	public static String format(Date date, String pattern) {
		if (date == null)
			return "";
		return DateFormatUtils.format(date, pattern);
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date) {
		return format(date, DATE_FORMAT_YMD);
	}

	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return format(date, DATE_FORMAT_DEFAULT);
	}

	/**
	 * 格式化时间，并返回星期几，例如：2014/12/30 08:38 周二
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateWithWeek(Date date) {
		if (date == null)
			return "";
		String d = format(date, "yyyy/MM/dd HH:mm");
		String week = weeks[Math.abs(getWeekByDate(date))];

		return d + " 周" + week;
	}

	/**
	 * 将时间转换为字符串,1小时内转换为文本,1小时后转换为yyyy-MM-dd HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateForWeibo(Date date) {
		if (date == null)
			return "";
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);// 当前年份
		int dateYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));// 传入时间年份
		StringBuffer buffer = new StringBuffer();
		long millTime = System.currentTimeMillis() - date.getTime();
		long second = millTime / 1000;
		long minutes = second / 60;
		long hours = minutes / 60;
		if (second < 60) {
			buffer.append("刚刚");
		} else if (minutes < 60) {
			buffer.append(minutes).append("分钟前");
		} else if (hours < 24) {
			buffer.append(hours).append("小时前");
		} else if (dateYear != year) {
			buffer.append(new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(date));
		} else {
			buffer.append(new SimpleDateFormat("MM月dd日 HH:mm").format(date));
		}
		return buffer.toString();
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return format(new Date(), TIME_FORMAT_DEFAULT);
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return format(new Date(), DATE_FORMAT_DEFAULT);
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return format(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return format(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return format(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return format(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy.MM.dd",
	 * "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null || StringUtils.isEmpty(str.toString())) {
			return null;
		}
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * 
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	/**
	 * 获取过去的小时
	 * 
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 60 * 1000);
	}

	/**
	 * 获取过去的分钟
	 * 
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 1000);
	}

	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static String formatDateTime(long timeMillis) {
		long day = timeMillis / (24 * 60 * 60 * 1000);
		long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
		long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
		return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
	}

	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}

	/**
	 * 获取一个日期相差的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static long getDistanceOfTodayDate(Date after) {
		long beforeTime = System.currentTimeMillis();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}

	/**
	 * 获取SimpleDateFormat
	 * 
	 * @param pattern
	 *            日期格式
	 * @return SimpleDateFormat对象
	 * @throws RuntimeException
	 *             异常：非法日期格式
	 */
	private static SimpleDateFormat getDateFormat(String pattern) throws RuntimeException {
		SimpleDateFormat dateFormat = threadLocal.get();
		if (dateFormat == null) {
			synchronized (object) {
				if (dateFormat == null) {
					dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setLenient(false);
					threadLocal.set(dateFormat);
				}
			}
		}
		dateFormat.applyPattern(pattern);
		return dateFormat;
	}

	/**
	 * 获取日期中的某数值。如获取月份
	 * 
	 * @param date
	 *            日期
	 * @param dateType
	 *            日期格式
	 * @return 数值
	 */
	private static int getInteger(Date date, int dateType) {
		int num = 0;
		Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
			num = calendar.get(dateType);
		}
		return num;
	}

	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * 
	 * @param date
	 *            日期字符串
	 * @param dateType
	 *            类型
	 * @param amount
	 *            数值
	 * @return 计算后日期字符串
	 */
	private static String addInteger(String date, int dateType, int amount) {
		String dateString = null;
		DateStyle dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			myDate = addInteger(myDate, dateType, amount);
			dateString = DateToString(myDate, dateStyle);
		}
		return dateString;
	}

	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * 
	 * @param date
	 *            日期
	 * @param dateType
	 *            类型
	 * @param amount
	 *            数值
	 * @return 计算后日期
	 */
	private static Date addInteger(Date date, int dateType, int amount) {
		Date myDate = null;
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(dateType, amount);
			myDate = calendar.getTime();
		}
		return myDate;
	}

	/**
	 * 获取精确的日期
	 * 
	 * @param timestamps
	 *            时间long集合
	 * @return 日期
	 */
	private static Date getAccurateDate(List<Long> timestamps) {
		Date date = null;
		long timestamp = 0;
		Map<Long, long[]> map = new HashMap<Long, long[]>();
		List<Long> absoluteValues = new ArrayList<Long>();

		if (timestamps != null && timestamps.size() > 0) {
			if (timestamps.size() > 1) {
				for (int i = 0; i < timestamps.size(); i++) {
					for (int j = i + 1; j < timestamps.size(); j++) {
						long absoluteValue = Math.abs(timestamps.get(i) - timestamps.get(j));
						absoluteValues.add(absoluteValue);
						long[] timestampTmp = { timestamps.get(i), timestamps.get(j) };
						map.put(absoluteValue, timestampTmp);
					}
				}

				// 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的。此时minAbsoluteValue为0
				// 因此不能将minAbsoluteValue取默认值0
				long minAbsoluteValue = -1;
				if (!absoluteValues.isEmpty()) {
					minAbsoluteValue = absoluteValues.get(0);
					for (int i = 1; i < absoluteValues.size(); i++) {
						if (minAbsoluteValue > absoluteValues.get(i)) {
							minAbsoluteValue = absoluteValues.get(i);
						}
					}
				}

				if (minAbsoluteValue != -1) {
					long[] timestampsLastTmp = map.get(minAbsoluteValue);

					long dateOne = timestampsLastTmp[0];
					long dateTwo = timestampsLastTmp[1];
					if (absoluteValues.size() > 1) {
						timestamp = Math.abs(dateOne) > Math.abs(dateTwo) ? dateOne : dateTwo;
					}
				}
			} else {
				timestamp = timestamps.get(0);
			}
		}

		if (timestamp != 0) {
			date = new Date(timestamp);
		}
		return date;
	}

	/**
	 * 判断字符串是否为日期字符串
	 * 
	 * @param date
	 *            日期字符串
	 * @return true or false
	 */
	public static boolean isDate(String date) {
		boolean isDate = false;
		if (date != null) {
			if (getDateStyle(date) != null) {
				isDate = true;
			}
		}
		return isDate;
	}

	/**
	 * 获取日期字符串的日期风格。失敗返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 日期风格
	 */
	public static DateStyle getDateStyle(String date) {
		DateStyle dateStyle = null;
		Map<Long, DateStyle> map = new HashMap<Long, DateStyle>();
		List<Long> timestamps = new ArrayList<Long>();
		for (DateStyle style : DateStyle.values()) {
			if (style.isShowOnly()) {
				continue;
			}
			Date dateTmp = null;
			if (date != null) {
				try {
					ParsePosition pos = new ParsePosition(0);
					dateTmp = getDateFormat(style.getValue()).parse(date, pos);
					if (pos.getIndex() != date.length()) {
						dateTmp = null;
					}
				} catch (Exception e) {
				}
			}
			if (dateTmp != null) {
				timestamps.add(dateTmp.getTime());
				map.put(dateTmp.getTime(), style);
			}
		}
		Date accurateDate = getAccurateDate(timestamps);
		if (accurateDate != null) {
			dateStyle = map.get(accurateDate.getTime());
		}
		return dateStyle;
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 日期
	 */
	public static Date StringToDate(String date) {
		DateStyle dateStyle = getDateStyle(date);
		return StringToDate(date, dateStyle);
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return 日期
	 */
	public static Date StringToDate(String date, String pattern) {
		Date myDate = null;
		if (date != null) {
			try {
				myDate = getDateFormat(pattern).parse(date);
			} catch (Exception e) {
			}
		}
		return myDate;
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param dateStyle
	 *            日期风格
	 * @return 日期
	 */
	public static Date StringToDate(String date, DateStyle dateStyle) {
		Date myDate = null;
		if (dateStyle != null) {
			myDate = StringToDate(date, dateStyle.getValue());
		}
		return myDate;
	}

	/**
	 * 将日期转化为日期字符串。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            日期格式
	 * @return 日期字符串
	 */
	public static String DateToString(Date date, String pattern) {
		String dateString = null;
		if (date != null) {
			try {
				dateString = getDateFormat(pattern).format(date);
			} catch (Exception e) {
			}
		}
		return dateString;
	}

	/**
	 * 将日期转化为日期字符串。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param dateStyle
	 *            日期风格
	 * @return 日期字符串
	 */
	public static String DateToString(Date date, DateStyle dateStyle) {
		String dateString = null;
		if (dateStyle != null) {
			dateString = DateToString(date, dateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param newPattern
	 *            新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String newPattern) {
		DateStyle oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, newPattern);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param newDateStyle
	 *            新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle newDateStyle) {
		DateStyle oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, newDateStyle);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddPattern
	 *            旧日期格式
	 * @param newPattern
	 *            新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String olddPattern, String newPattern) {
		return DateToString(StringToDate(date, olddPattern), newPattern);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddDteStyle
	 *            旧日期风格
	 * @param newParttern
	 *            新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle olddDteStyle, String newParttern) {
		String dateString = null;
		if (olddDteStyle != null) {
			dateString = StringToString(date, olddDteStyle.getValue(), newParttern);
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddPattern
	 *            旧日期格式
	 * @param newDateStyle
	 *            新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String olddPattern, DateStyle newDateStyle) {
		String dateString = null;
		if (newDateStyle != null) {
			dateString = StringToString(date, olddPattern, newDateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddDteStyle
	 *            旧日期风格
	 * @param newDateStyle
	 *            新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle olddDteStyle, DateStyle newDateStyle) {
		String dateString = null;
		if (olddDteStyle != null && newDateStyle != null) {
			dateString = StringToString(date, olddDteStyle.getValue(), newDateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 增加日期的年份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param yearAmount
	 *            增加数量。可为负数
	 * @return 增加年份后的日期字符串
	 */
	public static String addYear(String date, int yearAmount) {
		return addInteger(date, Calendar.YEAR, yearAmount);
	}

	/**
	 * 增加日期的年份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param yearAmount
	 *            增加数量。可为负数
	 * @return 增加年份后的日期
	 */
	public static Date addYear(Date date, int yearAmount) {
		return addInteger(date, Calendar.YEAR, yearAmount);
	}

	/**
	 * 增加日期的月份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param monthAmount
	 *            增加数量。可为负数
	 * @return 增加月份后的日期字符串
	 */
	public static String addMonth(String date, int monthAmount) {
		return addInteger(date, Calendar.MONTH, monthAmount);
	}

	/**
	 * 增加日期的月份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param monthAmount
	 *            增加数量。可为负数
	 * @return 增加月份后的日期
	 */
	public static Date addMonth(Date date, int monthAmount) {
		return addInteger(date, Calendar.MONTH, monthAmount);
	}

	/**
	 * 增加日期的天数。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加天数后的日期字符串
	 */
	public static String addDay(String date, int dayAmount) {
		return addInteger(date, Calendar.DATE, dayAmount);
	}

	/**
	 * 增加日期的天数。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加天数后的日期
	 */
	public static Date addDay(Date date, int dayAmount) {
		return addInteger(date, Calendar.DATE, dayAmount);
	}

	/**
	 * 增加日期的小时。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param hourAmount
	 *            增加数量。可为负数
	 * @return 增加小时后的日期字符串
	 */
	public static String addHour(String date, int hourAmount) {
		return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
	}

	/**
	 * 增加日期的小时。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param hourAmount
	 *            增加数量。可为负数
	 * @return 增加小时后的日期
	 */
	public static Date addHour(Date date, int hourAmount) {
		return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
	}

	/**
	 * 增加日期的分钟。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param minuteAmount
	 *            增加数量。可为负数
	 * @return 增加分钟后的日期字符串
	 */
	public static String addMinute(String date, int minuteAmount) {
		return addInteger(date, Calendar.MINUTE, minuteAmount);
	}

	/**
	 * 增加日期的分钟。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加分钟后的日期
	 */
	public static Date addMinute(Date date, int minuteAmount) {
		return addInteger(date, Calendar.MINUTE, minuteAmount);
	}

	/**
	 * 增加日期的秒钟。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加秒钟后的日期字符串
	 */
	public static String addSecond(String date, int secondAmount) {
		return addInteger(date, Calendar.SECOND, secondAmount);
	}

	/**
	 * 增加日期的秒钟。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加秒钟后的日期
	 */
	public static Date addSecond(Date date, int secondAmount) {
		return addInteger(date, Calendar.SECOND, secondAmount);
	}

	/**
	 * 获取日期的年份。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 年份
	 */
	public static int getYear(String date) {
		return getYear(StringToDate(date));
	}

	/**
	 * 获取日期的年份。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 年份
	 */
	public static int getYear(Date date) {
		return getInteger(date, Calendar.YEAR);
	}

	/**
	 * 获取日期的月份。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 月份
	 */
	public static int getMonth(String date) {
		return getMonth(StringToDate(date));
	}

	/**
	 * 获取日期的月份。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 月份
	 */
	public static int getMonth(Date date) {
		return getInteger(date, Calendar.MONTH) + 1;
	}

	/**
	 * 读取当月月底有效时间搓
	 * 
	 * @return
	 */
	public static Long getMonthLastDay() {
		return getMonthLastDate().getTime();
	}

	/**
	 * 获得系统当前月最后一天
	 */
	public static Date getMonthLastDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
		// 当前月的最后一天
		cal.set(Calendar.DATE, 1);
		cal.roll(Calendar.DATE, -1);
		Date endTime = cal.getTime();
		String endTime1 = datef.format(endTime) + " 23:59:59";
		// System.out.println("当月最后一天："+endTime1);

		return StringToDate(endTime1, DATE_FORMAT_DEFAULT);
	}

	/**
	 * 获取日期的天数。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 天
	 */
	public static int getDay(String date) {
		return getDay(StringToDate(date));
	}

	/**
	 * 获取日期的天数。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 天
	 */
	public static int getDay(Date date) {
		return getInteger(date, Calendar.DATE);
	}

	/**
	 * 获取日期的小时。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 小时
	 */
	public static int getHour(String date) {
		return getHour(StringToDate(date));
	}

	/**
	 * 获取日期的小时。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 小时
	 */
	public static int getHour(Date date) {
		return getInteger(date, Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取日期的分钟。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 分钟
	 */
	public static int getMinute(String date) {
		return getMinute(StringToDate(date));
	}

	/**
	 * 获取日期的分钟。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 分钟
	 */
	public static int getMinute(Date date) {
		return getInteger(date, Calendar.MINUTE);
	}

	/**
	 * 获取日期的秒钟。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 秒钟
	 */
	public static int getSecond(String date) {
		return getSecond(StringToDate(date));
	}

	/**
	 * 获取日期的秒钟。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 秒钟
	 */
	public static int getSecond(Date date) {
		return getInteger(date, Calendar.SECOND);
	}

	/**
	 * 获取日期 。默认yyyy-MM-dd格式。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 日期
	 */
	public static String getDate(String date) {
		return StringToString(date, DateStyle.YYYY_MM_DD);
	}

	/**
	 * 获取日期。默认yyyy-MM-dd格式。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @return 日期
	 */
	public static String getDate(Date date) {
		return DateToString(date, DateStyle.YYYY_MM_DD);
	}

	/**
	 * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 时间
	 */
	public static String getTime(String date) {
		return StringToString(date, DateStyle.HH_MM_SS);
	}

	/**
	 * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @return 时间
	 */
	public static String getTime(Date date) {
		return DateToString(date, DateStyle.HH_MM_SS);
	}

	/**
	 * 获取日期的星期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 星期
	 */
	public static Week getWeek(String date) {
		Week week = null;
		DateStyle dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			week = getWeek(myDate);
		}
		return week;
	}

	/**
	 * 获取日期的星期。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @return 星期
	 */
	public static Week getWeek(Date date) {
		Week week = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		switch (weekNumber) {
		case 0:
			week = Week.SUNDAY;
			break;
		case 1:
			week = Week.MONDAY;
			break;
		case 2:
			week = Week.TUESDAY;
			break;
		case 3:
			week = Week.WEDNESDAY;
			break;
		case 4:
			week = Week.THURSDAY;
			break;
		case 5:
			week = Week.FRIDAY;
			break;
		case 6:
			week = Week.SATURDAY;
			break;
		}
		return week;
	}

	/**
	 * 根据date得到它是周几,
	 * 
	 * @param date
	 * @return 周一返回0,周日返回7
	 */
	public static int getWeekByDate(Date date) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		// 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
		if (dayOfWeek == 0) { // 如果为0表示今天为周日,返回7
			dayOfWeek = 7;
		}

		if (dayOfWeek == 1) { // 如果是1表示 今天是周一,返回0
			return 0;
		} else {
			return 1 - dayOfWeek;
		}
	}

	/**
	 * 返回星期
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeekStr(String date) {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT_YMD);
		try {
			Date dt1 = df.parse(date);
			return weeks[Math.abs(getWeekByDate(dt1))];
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取两个日期相差的天数
	 * 
	 * @param date
	 *            日期字符串
	 * @param otherDate
	 *            另一个日期字符串
	 * @return 相差天数。如果失败则返回-1
	 */
	public static int getIntervalDays(String date, String otherDate) {
		return getIntervalDays(StringToDate(date), StringToDate(otherDate));
	}

	/**
	 * @param date
	 *            日期
	 * @param otherDate
	 *            另一个日期
	 * @return 相差天数。如果失败则返回-1
	 */
	public static int getIntervalDays(Date date, Date otherDate) {
		int num = -1;
		Date dateTmp = StringToDate(getDate(date), DateStyle.YYYY_MM_DD);
		Date otherDateTmp = StringToDate(getDate(otherDate), DateStyle.YYYY_MM_DD);
		if (dateTmp != null && otherDateTmp != null) {
			long time = Math.abs(dateTmp.getTime() - otherDateTmp.getTime());
			num = (int) (time / (24 * 60 * 60 * 1000));
		}
		return num;
	}

	/**
	 * 获取简单农历对象
	 * 
	 * @param date
	 *            日期字符串
	 * @return 简单农历对象
	 */
	public static SimpleLunarCalendar getSimpleLunarCalendar(String date) {
		return new SimpleLunarCalendar(StringToDate(date));
	}

	/**
	 * 获取简单农历对象
	 * 
	 * @param date
	 *            日期
	 * @return 简单农历对象
	 */
	public static SimpleLunarCalendar getSimpleLunarCalendar(Date date) {
		return new SimpleLunarCalendar(date);
	}

	/**
	 * 获得生肖
	 * 
	 * @param time
	 * @return
	 */
	public static String date2Zodica(Calendar time) {
		return zodiacArr[time.get(1) % 12];
	}

	/**
	 * 根据生日中的月、日，转换为星座
	 * 
	 * @param month
	 * @param day
	 * @return
	 */
	public static String getConstellation(int month, int day) {
		return day < constellationEdgeDay[month - 1] ? constellationArr[month - 1] : constellationArr[month];
	}

	/**
	 * date1与当前日期比较,等于返回0,大于返回1,小于返回-1
	 * 
	 * @param date
	 * @return
	 */
	public static int compare_date(String date) {
		return compare_date(new Date(), StringToDate(date, "yyyy-MM-dd"));
	}

	/**
	 * 比较日期
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int compare_date(Date d1, Date d2) {
		try {
			if (d1.getTime() > d2.getTime()) {
				System.out.println("dt1 在dt2前");
				return 1;
			} else if (d1.getTime() < d2.getTime()) {
				System.out.println("dt1在dt2后");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * 计算两个日期的时间差,返回几天几小时几分几秒
	 * 
	 * @param sd
	 * @param ed
	 * @return
	 */
	public static float date2diff(Date sd, Date ed) {
		Long s = sd.getTime();
		Long e = ed.getTime();
		if (s > e) {
			Long tmp = s;
			s = e;
			e = tmp;
		}
		Long diff = e - s;
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long nh = 1000 * 60 * 60;// 一小时的毫秒数
		long nm = 1000 * 60;// 一分钟的毫秒数
		long ns = 1000;// 一秒钟的毫秒数

		long day = diff / nd;// 计算差多少天
		long hour = diff % nd / nh;// 计算差多少小时
		long min = diff % nd % nh / nm;// 计算差多少分钟
		long sec = diff % nd % nh % nm / ns;// 计算差多少秒//输出结果
		System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒。");

		return Float.valueOf(String.format("%s.%s", day, hour));
	}

	public static void main(String[] args) {
		// System.out.println(createLink("http://www.tombaba.cn","/weiweb/1/home?a=1"));
		// System.out.println(createLink("http://www.tombaba.cn","/weiweb/1/home?a=1","token=%s&sid=%s"));
		// System.out.println(compare_date("2014-11-16 00:00:00"));
		// Date today = new Date();
		// int bday = DateUtil.getDayOfDate(today);
		// int bmonth = DateUtil.getMonthOfDate(today);
		// System.out.println("today:"+today);
		// System.out.println("bmonth:"+bmonth);
		// System.out.println("bday:"+bday);
		// System.out.println("星座:"+getConstellation(bmonth, bday));
		// System.out.println(MD5Util.md5Hex("123456"));
		// List<String> list = new ArrayList<String>();
		// list.add("1.jpg");
		// list.add("2.jpg");
		// System.out.println(StringUtils.join(list.toArray(),","));
		// System.out.println("时间搓："+getMonthLastDay());
		// System.out.println("orderno:"+dateformat(new Date(),"yyyyMMddHH"));
		// System.out.println(str2double("20",0.01d));
		// System.out.println(double2str(1.01d * 2 , "######.00", "0.01"));
		// System.out.println(getDistanceOfTodayDate(parseDate("2016-05-11
		// 16:43:22"))+1);
		// System.out.println("本期号:"+getWeek());
		// System.out.println(double2str(str2double("20",0.01d),"####.00",
		// "0.01"));
		System.out.println(getDistanceOfTodayDate(parseDate("2016-08-29")));
	}

	public static Date getYearFirstDay(int year) {
		String format = "yyyy-MM-dd";
		String firstDayDateString = year + "-01-01";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(firstDayDateString);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date getYearLastDay(int year) {
		String format = "yyyy-MM-dd";
		String lastDayDateString = year + "-12-31";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(lastDayDateString);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 
	 * create date:2010-5-22下午04:29:37
	 * 
	 * 描述：将日期转换为指定格式字符串
	 * 
	 * @param date
	 *            日期
	 * 
	 * @return
	 * 
	 */

	public static String getDateStr(Date date)

	{

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String datestr = sdf.format(date);

		return datestr;

	}

	/**
	 * 
	 * create date:2010-5-22下午03:40:44
	 * 
	 * 描述：取出日期字符串中的年份字符串
	 * 
	 * @param str
	 *            日期字符串
	 * 
	 * @return
	 * 
	 */

	public static String getYearStr(String str)

	{

		String yearStr = "";

		yearStr = str.substring(0, 4);

		return yearStr;

	}

	/**
	 * 
	 * create date:2010-5-22下午03:40:47
	 * 
	 * 描述：取出日期字符串中的月份字符串
	 * 
	 * @param str日期字符串
	 * 
	 * @return
	 * 
	 */

	public static String getMonthStr(String str)

	{

		String monthStr;

		int startIndex = str.indexOf("年");

		int endIndex = str.indexOf("月");

		monthStr = str.substring(startIndex + 1, endIndex);

		return monthStr;

	}

	/**
	 * 
	 * create date:2010-5-22下午03:32:31
	 * 
	 * 描述：将源字符串中的阿拉伯数字格式化为汉字
	 * 
	 * @param sign
	 *            源字符串中的字符
	 * 
	 * @return
	 * 
	 */

	public static char formatDigit(char sign) {

		if (sign == '0')

			sign = '0';

		if (sign == '1')

			sign = '一';

		if (sign == '2')

			sign = '二';

		if (sign == '3')

			sign = '三';

		if (sign == '4')

			sign = '四';

		if (sign == '5')

			sign = '五';

		if (sign == '6')

			sign = '六';

		if (sign == '7')

			sign = '七';

		if (sign == '8')

			sign = '八';

		if (sign == '9')

			sign = '九';

		return sign;

	}

	/**
	 * 
	 * create date:2010-5-22下午03:31:51
	 * 
	 * 描述： 获得月份字符串的长度
	 * 
	 * @param str
	 *            待转换的源字符串
	 * 
	 * @param pos1
	 *            第一个'-'的位置
	 * 
	 * @param pos2
	 *            第二个'-'的位置
	 * 
	 * @return
	 * 
	 */

	public static int getMidLen(String str, int pos1, int pos2) {

		return str.substring(pos1 + 1, pos2).length();

	}

	/**
	 * 
	 * create date:2010-5-22下午03:32:17
	 * 
	 * 描述：获得日期字符串的长度
	 * 
	 * @param str
	 *            待转换的源字符串
	 * 
	 * @param pos2
	 *            第二个'-'的位置
	 * 
	 * @return
	 * 
	 */

	public static int getLastLen(String str, int pos2) {

		return str.substring(pos2 + 1).length();

	}

	/**
	 * 
	 * create date:2010-5-22下午03:40:50
	 * 
	 * 描述：取出日期字符串中的日字符串
	 * 
	 * @param str
	 *            日期字符串
	 * 
	 * @return
	 * 
	 */

	public static String getDayStr(String str)

	{

		String dayStr = "";

		int startIndex = str.indexOf("月");

		int endIndex = str.indexOf("日");

		dayStr = str.substring(startIndex + 1, endIndex);

		return dayStr;

	}

	/**
	 * 
	 * create date:2010-5-22下午03:32:46
	 * 
	 * 描述：格式化日期
	 * 
	 * @param str
	 *            源字符串中的字符
	 * 
	 * @return
	 * 
	 */

	public static String formatStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str = sdf.format(date);
		StringBuffer sb = new StringBuffer();

		int pos1 = str.indexOf("-");

		int pos2 = str.lastIndexOf("-");

		for (int i = 0; i < 4; i++) {

			sb.append(formatDigit(str.charAt(i)));

		}

		sb.append('年');

		if (getMidLen(str, pos1, pos2) == 1) {

			sb.append(formatDigit(str.charAt(5)) + "月");

			if (str.charAt(7) != '0') {

				if (getLastLen(str, pos2) == 1) {

					sb.append(formatDigit(str.charAt(7)) + "日");

				}

				if (getLastLen(str, pos2) == 2) {

					if (str.charAt(7) != '1' && str.charAt(8) != '0') {

						sb.append(formatDigit(str.charAt(7)) + "十" + formatDigit(str.charAt(8)) + "日");

					}

					else if (str.charAt(7) != '1' && str.charAt(8) == '0') {

						sb.append(formatDigit(str.charAt(7)) + "十日");

					}

					else if (str.charAt(7) == '1' && str.charAt(8) != '0') {

						sb.append("十" + formatDigit(str.charAt(8)) + "日");

					}

					else {

						sb.append("十日");

					}

				}

			}

			else {

				sb.append(formatDigit(str.charAt(8)) + "日");

			}

		}

		if (getMidLen(str, pos1, pos2) == 2) {

			if (str.charAt(5) != '0' && str.charAt(6) != '0') {

				sb.append("十" + formatDigit(str.charAt(6)) + "月");

				if (getLastLen(str, pos2) == 1) {

					sb.append(formatDigit(str.charAt(8)) + "日");

				}

				if (getLastLen(str, pos2) == 2) {

					if (str.charAt(8) != '0') {

						if (str.charAt(8) != '1' && str.charAt(9) != '0') {

							sb.append(formatDigit(str.charAt(8)) + "十" + formatDigit(str.charAt(9)) + "日");

						}

						else if (str.charAt(8) != '1' && str.charAt(9) == '0') {

							sb.append(formatDigit(str.charAt(8)) + "十日");

						}

						else if (str.charAt(8) == '1' && str.charAt(9) != '0') {

							sb.append("十" + formatDigit(str.charAt(9)) + "日");

						}

						else {

							sb.append("十日");

						}

					}

					else {

						sb.append(formatDigit(str.charAt(9)) + "日");

					}

				}

			}

			else if (str.charAt(5) != '0' && str.charAt(6) == '0') {

				sb.append("十月");

				if (getLastLen(str, pos2) == 1) {

					sb.append(formatDigit(str.charAt(8)) + "日");

				}

				if (getLastLen(str, pos2) == 2) {

					if (str.charAt(8) != '0') {

						if (str.charAt(8) != '1' && str.charAt(9) != '0') {

							sb.append(formatDigit(str.charAt(8)) + "十" + formatDigit(str.charAt(9)) + "日");

						}

						else if (str.charAt(8) != '1' && str.charAt(9) == '0') {

							sb.append(formatDigit(str.charAt(8)) + "十日");

						}

						else if (str.charAt(8) == '1' && str.charAt(9) != '0') {

							sb.append("十" + formatDigit(str.charAt(9)) + "日");

						}

						else {

							sb.append("十日");

						}

					}

					else {

						sb.append(formatDigit(str.charAt(9)) + "日");

					}

				}

			}

			else {

				sb.append(formatDigit(str.charAt(6)) + "月");

				if (getLastLen(str, pos2) == 1) {

					sb.append(formatDigit(str.charAt(8)) + "日");

				}

				if (getLastLen(str, pos2) == 2) {

					if (str.charAt(8) != '0') {

						if (str.charAt(8) != '1' && str.charAt(9) != '0') {

							sb.append(formatDigit(str.charAt(8)) + "十" + formatDigit(str.charAt(9)) + "日");

						}

						else if (str.charAt(8) != '1' && str.charAt(9) == '0') {

							sb.append(formatDigit(str.charAt(8)) + "十日");

						}

						else if (str.charAt(8) == '1' && str.charAt(9) != '0') {

							sb.append("十" + formatDigit(str.charAt(9)) + "日");

						}

						else {

							sb.append("十日");

						}

					}

					else {

						sb.append(formatDigit(str.charAt(9)) + "日");

					}

				}

			}

		}

		return sb.toString();

	}
}
