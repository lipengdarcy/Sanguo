 package org.darcy.sanguo.charge;
 
 public class Charge
 {
   public static final String HQL = "from Charge c where c.done = 0";
   private int id;
   private String channel;
   private String name;
   private int goodsId;
   private int done;
   private int rmb;
 
   public int getId()
   {
     return this.id; }
 
   public void setId(int id) {
     this.id = id; }
 
   public String getName() {
     return this.name; }
 
   public void setName(String name) {
     this.name = name; }
 
   public int getDone() {
     return this.done; }
 
   public void setDone(int done) {
     this.done = done; }
 
   public int getGoodsId() {
     return this.goodsId; }
 
   public void setGoodsId(int goodsId) {
     this.goodsId = goodsId; }
 
   public String getChannel() {
     return this.channel; }
 
   public void setChannel(String channel) {
     this.channel = channel; }
 
   public int getRmb() {
     return this.rmb; }
 
   public void setRmb(int rmb) {
     this.rmb = rmb;
   }
 }
