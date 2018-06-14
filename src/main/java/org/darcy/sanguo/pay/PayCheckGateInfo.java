 package org.darcy.sanguo.pay;
 
 import java.util.List;
 
 public class PayCheckGateInfo
 {
   public static final String CHANNEL_MYCARD = "MY_CARD";
   public static final String CHANNEL_APPSTORE = "APP_STORE";
   public static final String CHANNEL_GOOGLE_PLAY = "GOOGLE_PLAY";
   public static final String CHANNEL_TONGBUTUI = "TONGBUTUI";
   public static final String CHANNEL_ITOOLS = "ITOOLS";
   public static final String CHANNEL_KUAIYONG = "KUAIYONG";
   public static final String CHANNEL_UC = "UC";
   public static final String CHANNEL_360 = "QIHU360";
   private int playerId;
   private String orderId;
   private int price;
   private String payChannel;
   private String coGoodsId;
   private List<String> parameters;
 
   public int getPlayerId()
   {
     return this.playerId; }
 
   public String getOrderId() {
     return this.orderId; }
 
   public int getPrice() {
     return this.price; }
 
   public List<String> getParameters() {
     return this.parameters; }
 
   public void setPlayerId(int playerId) {
     this.playerId = playerId; }
 
   public void setOrderId(String orderId) {
     this.orderId = orderId; }
 
   public void setPrice(int price) {
     this.price = price; }
 
   public void setParameters(List<String> parameters) {
     this.parameters = parameters; }
 
   public String getPayChannel() {
     return this.payChannel; }
 
   public void setPayChannel(String payChannel) {
     this.payChannel = payChannel; }
 
   public String getCoGoodsId() {
     return this.coGoodsId; }
 
   public void setCoGoodsId(String coGoodsId) {
     this.coGoodsId = coGoodsId;
   }
 }
