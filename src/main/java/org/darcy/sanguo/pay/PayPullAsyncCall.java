 package org.darcy.sanguo.pay;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.PayService;
 
 public class PayPullAsyncCall extends AsyncCall
 {
   private List<PayPull> pulls = new ArrayList<PayPull>();
   private List<PayPull> feedBackPulls = new ArrayList<PayPull>();
   private HashMap<String, Receipt> receipts = new HashMap<String, Receipt>();
 
   public PayPullAsyncCall() {
     super(null, null);
   }
 
   public void callback()
   {
     for (PayPull pull : this.pulls) {
       Player player = null;
       try {
         player = Platform.getPlayerManager().getPlayer(pull.getPlayerId(), false, false);
         if (player != null)
         {
           PayItem item = (PayItem)((HashMap)PayService.pays.get(pull.getChannel())).get(Integer.valueOf(pull.getGoodsId()));
           if (item != null)
           {
             Receipt receipt = (Receipt)this.receipts.get(pull.getOrderId());
             PayCheckAsyncCall.checkSuccess(receipt, player, item);
 
             this.feedBackPulls.add(pull); } }
       } catch (Exception e) {
         e.printStackTrace();
       }
     }
 
     if (this.feedBackPulls.size() > 0)
       new Thread(new PayPullFeedBack(this.feedBackPulls), "pullsFeedBack").start();
   }
 
   
   public void netOrDB()
   {
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
   }
 }
