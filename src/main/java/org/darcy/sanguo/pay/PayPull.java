 package org.darcy.sanguo.pay;
 
 public class PayPull
 {
   private int playerId;
   private int goodsId;
   private int number;
   private String orderId;
   private String channel;
 
   public int getPlayerId()
   {
     return this.playerId; }
 
   public int getNumber() {
     return this.number; }
 
   public String getOrderId() {
     return this.orderId; }
 
   public String getChannel() {
     return this.channel; }
 
   public void setPlayerId(int playerId) {
     this.playerId = playerId; }
 
   public void setNumber(int number) {
     this.number = number; }
 
   public void setOrderId(String orderId) {
     this.orderId = orderId; }
 
   public void setChannel(String channel) {
     this.channel = channel; }
 
   public int getGoodsId() {
     return this.goodsId; }
 
   public void setGoodsId(int goodsId) {
     this.goodsId = goodsId;
   }
 }
