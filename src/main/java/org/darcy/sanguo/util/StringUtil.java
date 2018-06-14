 package org.darcy.sanguo.util;
 
 import java.util.regex.Pattern;
 
 public class StringUtil
 {
   static Character.UnicodeBlock[] validChars = { 
     Character.UnicodeBlock.CJK_COMPATIBILITY, 
     Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS, 
     Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS, 
     Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT, 
     Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT, 
     Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION, 
     Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS, 
     Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A, 
     Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B, 
     Character.UnicodeBlock.MATHEMATICAL_OPERATORS, 
     Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS, 
     Character.UnicodeBlock.BASIC_LATIN };
 
   public static boolean isNumberString(String value)
   {
     if (value == null) {
       return false;
     }
     for (int i = 0; i < value.length(); ++i) {
       char c = value.charAt(i);
       if ((c < '0') || (c > '9')) {
         return false;
       }
     }
     return true;
   }
 
   public static boolean isMailString(String value) {
     Pattern pattern = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
 
     return (!(pattern.matcher(value).matches()));
   }
 
   static boolean isValidChar(char c)
   {
     Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
     for (Character.UnicodeBlock cub : validChars) {
       if (cub == ub)
         return true;
     }
     return false;
   }
 
   public static int getLength(String s) {
     int rst = 0;
     for (int i = 0; i < s.length(); ++i) {
       char c = s.charAt(i);
       Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
       if (ub == Character.UnicodeBlock.BASIC_LATIN)
         ++rst;
       else {
         rst += 2;
       }
     }
 
     return rst;
   }
 
   public static boolean isValidString(String src)
   {
     for (int i = 0; i < src.length(); ++i) {
       char c = src.charAt(i);
 
       if (!(isValidChar(c))) {
         return false;
       }
     }
 
     return true;
   }
 
   public static void main(String[] args) {
     String s = "a";
     System.out.println(isValidString(s));
   }
 }
