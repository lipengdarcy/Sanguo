 package org.darcy.sanguo.pay;
 
 public class PayCheckGateRst
 {
   private int state;
   private String errorInfo;
 
   public int getState()
   {
     return this.state; }
 
   public String getErrorInfo() {
     return this.errorInfo; }
 
   public void setState(int state) {
     this.state = state; }
 
   public void setErrorInfo(String errorInfo) {
     this.errorInfo = errorInfo;
   }
 }
