 package org.darcy.sanguo.pay;
 
 import java.util.Date;
 
 public class Receipt
 {
   private int pid;
   private String orderId;
   private String coOrderId;
   private int goodsId;
   private String coGoodsId;
   private int price;
   private int state;
   private Date updateTime;
   private Date createTime;
   private String channel;
 
   public Receipt()
   {
     this.createTime = new Date();
     this.updateTime = new Date();
   }
 
   public int getPid() {
     return this.pid; }
 
   public String getOrderId() {
     return this.orderId; }
 
   public String getCoOrderId() {
     return this.coOrderId; }
 
   public int getGoodsId() {
     return this.goodsId; }
 
   public String getCoGoodsId() {
     return this.coGoodsId; }
 
   public int getPrice() {
     return this.price; }
 
   public int getState() {
     return this.state; }
 
   public Date getUpdateTime() {
     return this.updateTime; }
 
   public Date getCreateTime() {
     return this.createTime; }
 
   public String getChannel() {
     return this.channel; }
 
   public void setPid(int pid) {
     this.pid = pid; }
 
   public void setOrderId(String orderId) {
     this.orderId = orderId; }
 
   public void setCoOrderId(String coOrderId) {
     this.coOrderId = coOrderId; }
 
   public void setGoodsId(int goodsId) {
     this.goodsId = goodsId; }
 
   public void setCoGoodsId(String coGoodsId) {
     this.coGoodsId = coGoodsId; }
 
   public void setPrice(int price) {
     this.price = price; }
 
   public void setState(int state) {
     this.state = state; }
 
   public void setUpdateTime(Date updateTime) {
     this.updateTime = updateTime; }
 
   public void setCreateTime(Date createTime) {
     this.createTime = createTime; }
 
   public void setChannel(String channel) {
     this.channel = channel;
   }
 }
