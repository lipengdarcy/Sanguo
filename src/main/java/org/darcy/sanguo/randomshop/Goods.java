 package org.darcy.sanguo.randomshop;
 
 public class Goods
 {
   public static final int GOODS_TYPE_S_SOUL = 1;
   public static final int GOODS_TYPE_A_SOUL = 2;
   public static final int GOODS_TYPE_B_WARRIOR = 3;
   public static final int GOODS_TYPE_ITEM = 4;
   public static final int GOODS_TYPE_EQUIPMENT = 5;
   public static final int GOODS_TYPE_TREASURE = 6;
   private int id;
   private String name;
   private int itemId;
   private int count;
   private int moneyType;
   private int price;
   private int groupCount;
   private int goodsType;
 
   public Goods(int id, String name, int itemId, int groupCount, int count, int moneyType, int price, int goodsType)
   {
     this.id = id;
     this.name = name;
     this.itemId = itemId;
     this.count = count;
     this.groupCount = groupCount;
     this.moneyType = moneyType;
     this.price = price;
     this.goodsType = goodsType; }
 
   public int getId() {
     return this.id; }
 
   public void setId(int id) {
     this.id = id; }
 
   public String getName() {
     return this.name; }
 
   public int getItemId() {
     return this.itemId; }
 
   public int getCount() {
     return this.count; }
 
   public int getMoneyType() {
     return this.moneyType; }
 
   public int getPrice() {
     return this.price; }
 
   public void setName(String name) {
     this.name = name; }
 
   public void setItemId(int itemId) {
     this.itemId = itemId; }
 
   public void setCount(int count) {
     this.count = count; }
 
   public void setMoneyType(int moneyType) {
     this.moneyType = moneyType; }
 
   public void setPrice(int price) {
     this.price = price; }
 
   public int getGroupCount() {
     return this.groupCount; }
 
   public void setGroupCount(int groupCount) {
     this.groupCount = groupCount; }
 
   public int getGoodsType() {
     return this.goodsType; }
 
   public void setGoodsType(int goodsType) {
     this.goodsType = goodsType;
   }
 }
