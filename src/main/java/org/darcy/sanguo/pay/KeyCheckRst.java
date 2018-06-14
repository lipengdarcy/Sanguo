 package org.darcy.sanguo.pay;
 
 public class KeyCheckRst
 {
   public static final int ERROR_CODE_MAX = 1;
   public static final int ERROR_CODE_INVALID = 2;
   public static final int ERROR_CODE_WRONT_FORMAT = 3;
   public static final int ERROR_CODE_OVERDUE = 4;
   public static final int ERROR_CODE_BUSY = 5;
   public static final int ERROR_CODE_BEFORE = 6;
   private boolean result;
   private String errorInfo;
   private int errorCode;
   private int type = -1;
   private int drop = -1;
 
   public int getDrop()
   {
     return this.drop; }
 
   public void setDrop(int drop) {
     this.drop = drop; }
 
   public boolean isResult() {
     return this.result; }
 
   public void setResult(boolean result) {
     this.result = result; }
 
   public boolean getResult() {
     return this.result; }
 
   public String getErrorInfo() {
     return this.errorInfo; }
 
   public void setErrorInfo(String errorInfo) {
     this.errorInfo = errorInfo; }
 
   public int getType() {
     return this.type; }
 
   public void setType(int type) {
     this.type = type; }
 
   public int getErrorCode() {
     return this.errorCode; }
 
   public void setErrorCode(int errorCode) {
     this.errorCode = errorCode;
   }
 }
