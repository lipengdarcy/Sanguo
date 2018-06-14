 package org.darcy.sanguo.utils;
 
 public class ParamUtils
 {
   public static String getParam(String gameid, String gamekey, String uid, String sessionId)
   {
     if (gameid.length() == 32) {
       String channelID = gameid.substring(0, 16);
       String gameID = gameid.substring(16, 32);
       String result = "{'funcNo':9,'version':4,'sysType':0,'channelID':'" + channelID + "','gameID':'" + gameID + "','uId':'" + uid + "','sessionId':'" + sessionId + "'";
       String result1 = result + ",\"gameKey\":\"" + gamekey + "\"}";
       String sign = MD5.MD5(result1);
       return "param=" + result + "}&sign=" + sign;
     }
     return "{'status':'fail','code':'40000010','info':'parameter error!【02】'}";
   }
 
   public static String saveRechargeLog(String gameid, String gamekey, String uid, int level, String orderno, String itemId, Double currencyAmount, Integer vcAmount, String currencyType, String gameserver)
   {
     if (gameid.length() == 32)
     {
       String channelID = gameid.substring(0, 16);
       String gameID = gameid.substring(16, 32);
       String result = "{'funcNo':1,'version':4,'sysType':0,'channelID':'" + channelID + "','gameID':'" + gameID + "','uId':'" + uid + "','level':'" + level + "','orderno':'" + orderno + "','gameServer':'" + gameserver + "','itemid':'" + itemId + "','currencyAmount':'" + currencyAmount + "','vcAmount':'" + vcAmount + "','currencyType':'" + currencyType + "'";
       String result1 = result + ",\"gameKey\":\"" + gamekey + "\"}";
       String sign = MD5.MD5(result1);
       return "param=" + result + "}&sign=" + sign;
     }
 
     return "{'status':'fail','code':'40000010','info':'parameter error!【04】'}";
   }
 }
