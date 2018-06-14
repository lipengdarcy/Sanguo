package cn.smarthse.framework.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HTTP相关的操作
 * 
 */

public class HttpUtil {

	/**
	 * 是否访问静态文件
	 * 
	 * @param uri
	 *            uri链接
	 * @return 返回状态
	 */
	public static boolean isStaticResource(String uri) {
		if (uri.startsWith("/static"))
			return true;
		String[] exts = new String[] { "jpg", "gif", "js", "png", "css", "doc", "xls", "swf", "ico" };
		for (String ext : exts) {
			if (uri.toUpperCase().endsWith(ext.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置cookie
	 * 
	 * @param response
	 *            应答
	 * @param cookieName
	 *            cookie名
	 * @param cookieValue
	 *            cookie值
	 * @param time
	 *            cookie生存时间
	 */
	public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int time) {
		// //System.out.println("write :" + cookieName + " " + cookieValue);
		try {
			if (cookieValue != null)
				cookieValue = URLEncoder.encode(cookieValue, "UTF-8");

		} catch (Exception ex) {
			ex.printStackTrace();
			// //System.out.println(ex);
		}

		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setMaxAge(time);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static void addCookie(HttpServletResponse response, String domain, String path, String cookieName,
			String cookieValue, int time) {
		try {
			cookieValue = URLEncoder.encode(cookieValue, "UTF-8");
		} catch (Exception ex) {
		}
		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setMaxAge(time);
		cookie.setDomain(domain);
		cookie.setPath(path);
		response.addCookie(cookie);
		// //System.out.println("write " + cookieName);
	}

	public static void addCookie1(HttpServletResponse response, String cookieName, String cookieValue, int time) {

		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setMaxAge(time);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static String getCookieValue(HttpServletRequest request, String cookieName, String domain, String path) {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (domain.equals(cookies[i].getDomain()) && path.equals(cookies[i].getPath())
						&& cookieName.equals(cookies[i].getName())) {
					return cookies[i].getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 根据cookie名称取得cookie的值
	 * 
	 * @param HttpServletRequest
	 *            request request对象
	 * @param name
	 *            cookie名称
	 * @return string cookie的值 当取不到cookie的值时返回null
	 */
	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {

				if (cookieName.equals(cookies[i].getName())) {
					return cookies[i].getValue();
				}
			}
		}
		return null;
	}

	public static String getURL(HttpServletRequest request) {
		StringBuffer sb = request.getRequestURL();
		String queryString = request.getQueryString();
		if (queryString != null)
			return (new StringBuilder()).append(sb.toString()).append("?").append(queryString).toString();
		else
			return sb.toString();
	}

	/**
	 * 
	 */
	public static int getInt(HttpServletRequest request, String paramName, int defaultValue) {
		String s = request.getParameter(paramName);
		if (s == null || s.equals(""))
			return defaultValue;
		else
			return Integer.parseInt(s);
	}

	public static int getInt(HttpServletRequest request, String paramName) {
		String s = request.getParameter(paramName);
		return Integer.parseInt(s);
	}

	public static Long getLong(HttpServletRequest request, String paramName) {
		String s = request.getParameter(paramName);
		return Long.parseLong(s);
	}

	public static Long getLong(HttpServletRequest request, String paramName, Long defaultValue) {
		String s = request.getParameter(paramName);
		if (s == null || s.equals(""))
			return defaultValue;
		else
			return Long.parseLong(s);
	}

	public static String getString(HttpServletRequest request, String paramName, String defaultValue) {
		String s = request.getParameter(paramName);
		if (s == null || s.equals(""))
			return defaultValue;
		else
			return s;
	}

	public static String getString(HttpServletRequest request, String paramName) {
		String s = request.getParameter(paramName);
		if (s == null || s.equals(""))
			throw new NullPointerException(
					(new StringBuilder()).append("Null parameter: ").append(paramName).toString());
		else
			return s;
	}

	public static boolean getBoolean(HttpServletRequest request, String paramName) {
		String s = request.getParameter(paramName);
		return Boolean.parseBoolean(s);
	}

	public static boolean getBoolean(HttpServletRequest request, String paramName, boolean defaultValue) {
		String s = request.getParameter(paramName);
		if (s == null || s.equals(""))
			return defaultValue;
		else
			return Boolean.parseBoolean(s);
	}

	public static float getFloat(HttpServletRequest request, String paramName) {
		String s = request.getParameter(paramName);
		return Float.parseFloat(s);
	}

	public static String htmlEncode(String text) {
		if (text == null || "".equals(text)) {
			return "";
		} else {
			text = text.replace("<", "&lt;");
			text = text.replace(">", "&gt;");
			text = text.replace(" ", "&nbsp;");
			text = text.replace("\"", "&quot;");
			text = text.replace("'", "&apos;");
			return text.replace("\n", "<br/>");
		}
	}

	public static String htmlEncodeNotBlank(String text) {
		if (text == null || "".equals(text)) {
			return "";
		} else {
			text = text.replace("<", "&lt;");
			text = text.replace(">", "&gt;");
			text = text.replace("\"", "&quot;");
			text = text.replace("'", "&apos;");
			return text.replace("\n", "<br/>");
		}
	}

	public static String encode(String text) {
		if (text == null || "".equals(text)) {
			return "";
		} else {
			text = text.replace("<", "&lt;");
			text = text.replace(">", "&gt;");
			text = text.replace("\"", "&quot;");
			text = text.replace("'", "&apos;");
			text = text.replace("&", "&amp;");
			return text.replace("\n", "<br/>");
		}
	}

	public static String encodeHtml(String text) {
		if (text == null || "".equals(text)) {
			return "";
		} else {
			text = text.replace("&lt;", "<");
			text = text.replace("&gt;", ">");
			text = text.replace("&nbsp;", " ");
			text = text.replace("&quot;", "\"");
			text = text.replace("&apos;", "'");
			return text.replace("<br/>", "\n");
		}
	}

	public static String encodeHtml2(String text) {
		if (text == null || "".equals(text)) {
			return "";
		} else {
			text = text.replace("&lt;", "<");
			text = text.replace("&gt;", ">");
			text = text.replace("&nbsp;", " ");
			text = text.replace("&quot;", "");
			text = text.replace("&apos;", "");
			return text.replace("<br/>", "\n");
		}
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getRemoteAddr();
		return ip;
	}

	@SuppressWarnings("unchecked")
	public static String subStringHTML(String param, int length, String end) {
		StringBuffer result = new StringBuffer();
		int n = 0;
		boolean isCode = false;
		boolean isHTML = false;
		int i = 0;
		do {
			if (i >= param.length())
				break;
			char temp = param.charAt(i);
			if (temp == '<')
				isCode = true;
			else if (temp == '&')
				isHTML = true;
			else if (temp == '>' && isCode) {
				n--;
				isCode = false;
			} else if (temp == ';' && isHTML)
				isHTML = false;
			if (!isCode && !isHTML) {
				n++;
				if ((new StringBuilder()).append(temp).append("").toString().getBytes().length > 1)
					n++;
			}
			result.append(temp);
			if (n >= length)
				break;
			i++;
		} while (true);
		result.append(end);
		String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)", "$1$2");
		temp_result = temp_result.replaceAll(
				"</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
				"");
		temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>", "$2");
		Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
		Matcher m = p.matcher(temp_result);
		List endHTML = new ArrayList();
		for (; m.find(); endHTML.add(m.group(1)))
			;

		for (int j = endHTML.size() - 1; j >= 0; j--) {
			result.append("</");
			result.append(endHTML.get(j));
			result.append(">");
		}

		return result.toString();
	}

	public static void main(String args[]) {
		String test = "&lt;FONT&nbsp;color=#1a6be6&gt;像艺术家一样浪漫，像工程师一样严谨;安分竭力，泊然如一无所求者，不过两年，则必为上官僚友所钦属也;虚妄的世界寻求一方净土:人生哲学-智者的天空;四书五经-儒、道、佛家的经典;日韩潮流-浪漫唯美的一隅；经典流行-清新雅乐的纯净&lt;/FONT&gt;";
		System.out.println(test);
		System.out.println("=============================================================================");
		System.out.println(encodeHtml(test));
		System.out.println(subStringHTML(encodeHtml(test), 20, "..."));
	}

}
