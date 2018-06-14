 package org.darcy.sanguo.pay;
 
 import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;

import net.sf.json.JSONArray;
 
 public class PayPullFeedBack
   implements Runnable
 {
   private List<PayPull> feedBackPulls;
 
   public PayPullFeedBack(List<PayPull> feedBackPulls)
   {
     this.feedBackPulls = feedBackPulls;
   }
 
   public void run()
   {
     String[] ids = new String[this.feedBackPulls.size()];
     int index = 0;
     for (PayPull pull : this.feedBackPulls) {
       ids[(index++)] = pull.getOrderId();
     }
     JSONArray array = JSONArray.fromObject(ids);
     try
     {
       String code;
       URL httpUrl = new URL(Configuration.billingAdd + "/pullFeedBack");
       HttpURLConnection http = (HttpURLConnection)httpUrl.openConnection();
       http.setConnectTimeout(5000);
       http.setReadTimeout(30000);
       http.setRequestMethod("POST");
       http.setDoInput(true);
       http.setDoOutput(true);
       OutputStream out = http.getOutputStream();
       String data = "data=" + array.toString();
       out.write(data.getBytes(Charset.forName("utf-8")));
       out.flush();
       StringBuffer result = new StringBuffer();
       InputStream is = http.getInputStream();
       InputStreamReader read = new InputStreamReader(is, "UTF-8");
       BufferedReader reader = new BufferedReader(read);
 
       while ((code = reader.readLine()) != null) {
         result.append(code);
       }
       String message = result.toString();
       Platform.getLog().logWorld("PayPullFeedBack: " + message);
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 }
