 package org.darcy.sanguo.account;
 
 public class Account
 {
   public static final int ACCOUNT_TYPE_GUEST = 1;
   public static final int ACCOUNT_TYPE_APP_STORE = 2;
   public static final int ACCOUNT_TYPE_CYOU = 3;
   public static final int ACCOUNT_TYPE_91 = 4;
   public static final int ACCOUNT_TYPE_PP = 5;
   public static final int ACCOUNT_TYPE_WANDOUJIA = 6;
   public static final int ACCOUNT_TYPE_XIAOMI = 7;
   public static final int ACCOUNT_TYPE_UC = 8;
   public static final int ACCOUNT_TYPE_360 = 9;
   public static final int ACCOUNT_TYPE_TONGBUTUI = 10;
   public static final int ACCOUNT_TYPE_ANZHI = 11;
   public static final int ACCOUNT_TYPE_DUOKU = 12;
   public static final int ACCOUNT_TYPE_OPPO = 13;
   public static final int ACCOUNT_TYPE_DANGLE = 14;
   public static final int ACCOUNT_TYPE_JIFENG = 15;
   public static final int ACCOUNT_TYPE_HUAWEI = 16;
   public static final int ACCOUNT_TYPE_LENOVO = 17;
   public static final int ACCOUNT_TYPE_AIYOUXI = 18;
   public static final int ACCOUNT_TYPE_YIWAN = 19;
   public static final int ACCOUNT_TYPE_XY = 20;
   public static final int ACCOUNT_TYPE_IAPPLE = 21;
   public static final int ACCOUNT_TYPE_FACEBOOK = 22;
   public static final int ACCOUNT_TYPE_GY = 23;
   public static final int ACCOUNT_TYPE_KUAIYONG = 24;
   public static final int ACCOUNT_TYPE_ITOOLS = 25;
   public static final int ACCOUNT_TYPE_CHUJIAN = 26;
   public static final int ACCOUNT_TYPE_BAIDU = 27;
   public static final int ACCOUNT_TYPE_HAIMA = 28;
   public static final int ACCOUNT_TYPE_AISI = 29;
   public static final int ACCOUNT_TYPE_9S = 30;
   public static final int ACCOUNT_TYPE_YOUKU = 31;
   public static final int ACCOUNT_TYPE_PPTV = 32;
   public static final int ACCOUNT_TYPE_MUZHIWAN = 33;
   public static final int ACCOUNT_TYPE_PPS = 34;
   public static final int ACCOUNT_TYPE_AYOUXI = 35;
   public static final int ACCOUNT_TYPE_YINGYONGBAO = 36;
   public static final int ACCOUNT_TYPE_KUPAI = 37;
   public static final int ACCOUNT_TYPE_JINLI = 38;
   public static final int ACCOUNT_TYPE_VIVO = 39;
   public static final int ACCOUNT_TYPE_NEWMUZHIWAN = 40;
   private String accountId;
   private int channelType = 1;
   private String ip;
   private String deviceId;
 
   public Account()
   {
   }
 
   public Account(int channelType, String deviceId, String ip)
   {
     this.channelType = channelType;
     this.deviceId = deviceId;
     this.ip = ip;
   }
 
   public String getAccountId() {
     return this.accountId; }
 
   public String getIp() {
     return this.ip; }
 
   public String getDeviceId() {
     return this.deviceId; }
 
   public void setAccountId(String accountId) {
     this.accountId = accountId; }
 
   public void setIp(String ip) {
     this.ip = ip; }
 
   public void setDeviceId(String deviceId) {
     this.deviceId = deviceId; }
 
   public int getChannelType() {
     return this.channelType; }
 
   public void setChannelType(int channelType) {
     this.channelType = channelType;
   }
 }
