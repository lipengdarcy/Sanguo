package org.darcy.sanguo.utils;

public class SdkHelp {
	public static String checkUserLogin(String url, String gameid, String gamekey, String uid, String sessionId,
			int httpType) {
		url = url + "/inter/checkLogin.page";
		System.out.println(ParamUtils.getParam(gameid, gamekey, uid, sessionId));
		if (2 == httpType)
			return HttpUtil.sendPost(url, ParamUtils.getParam(gameid, gamekey, uid, sessionId));
		if (1 == httpType) {
			return HttpsUtil.sendPost(url, ParamUtils.getParam(gameid, gamekey, uid, sessionId));
		}
		return "{'status':'fail','code':'40000010','info':'parameter error!【01】'}";
	}

	public static String saverechargeLogs(String url, String gameid, String gamekey, String uid, int level,
			String orderno, String itemId, Double currencyAmount, Integer vcAmount, String currencyType,
			String gameserver, int httpType) {
		url = url + "/inter/rechargeLog.page";
		System.out.println(ParamUtils.saveRechargeLog(gameid, gamekey, uid, level, orderno, itemId, currencyAmount,
				vcAmount, currencyType, gameserver));
		if (2 == httpType)
			return HttpUtil.sendPost(url, ParamUtils.saveRechargeLog(gameid, gamekey, uid, level, orderno, itemId,
					currencyAmount, vcAmount, currencyType, gameserver));
		if (1 == httpType) {
			return HttpsUtil.sendPost(url, ParamUtils.saveRechargeLog(gameid, gamekey, uid, level, orderno, itemId,
					currencyAmount, vcAmount, currencyType, gameserver));
		}
		return "{'status':'fail','code':'40000010','info':'parameter error!【03】'}";
	}
}
