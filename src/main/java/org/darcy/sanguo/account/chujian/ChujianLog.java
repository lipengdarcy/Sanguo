 package org.darcy.sanguo.account.chujian;
 
 public class ChujianLog
 {
   public String channel;
   public String accountId;
   public int level;
   public String orderId;
   public String itemId;
   public float currencyAmount;
   public int vcAmount;
   public int serverId;
   public int count;
   public long lastSendTime;
 
   public ChujianLog(String channel, String accountId, int level, String orderId, String itemId, float currencyAmount, int vcAmount, int serverId)
   {
     this.channel = channel;
     this.accountId = accountId;
     this.level = level;
     this.orderId = orderId;
     this.itemId = itemId;
     this.currencyAmount = currencyAmount;
     this.vcAmount = vcAmount;
     this.serverId = serverId;
     this.count = 0;
   }
 
   public String toString()
   {
     return "ChujianLog [channel=" + this.channel + ", accountId=" + this.accountId + 
       ", level=" + this.level + ", orderId=" + this.orderId + ", itemId=" + 
       this.itemId + ", currencyAmount=" + this.currencyAmount + ", vcAmount=" + 
       this.vcAmount + ", serverId=" + this.serverId + "]";
   }
 }
