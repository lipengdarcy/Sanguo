 package org.darcy.sanguo.keyword;
 
 import java.util.HashMap;
 
 public class KeyWordTree
 {
   HashMap<Character, KeyWordNode> children = new HashMap();
 
   public void addKeyWord(String word) {
     if (word.length() == 0) {
       return;
     }
     char sc = word.charAt(0);
     KeyWordNode lastKeyWordNode = (KeyWordNode)this.children.get(Character.valueOf(sc));
     if (lastKeyWordNode == null) {
       lastKeyWordNode = new KeyWordNode(sc);
       this.children.put(Character.valueOf(sc), lastKeyWordNode);
     }
     if (word.length() == 1) {
       KeyWordNode node = new KeyWordNode('|');
       lastKeyWordNode.addChild(node);
     } else if (word.length() > 1) {
       for (int i = 1; i < word.length(); ++i) {
         char c = word.charAt(i);
         KeyWordNode kn = lastKeyWordNode.getChild(c);
         if (kn != null) {
           lastKeyWordNode = kn;
         }
         else {
           KeyWordNode node = new KeyWordNode(c);
           lastKeyWordNode.addChild(node);
           lastKeyWordNode = node;
 
           if (i == word.length() - 1) {
             node = new KeyWordNode('|');
             lastKeyWordNode.addChild(node); }
         }
       }
     }
   }
 
   public String mark(String word) {
     String ret = "";
     int beginIndex = 0;
     int endIndex = 0;
     for (int i = 0; i < word.length(); ++i) {
       KeyWordNode lastnode = (KeyWordNode)this.children.get(Character.valueOf(word.charAt(i)));
       if (lastnode == null) {
         continue;
       }
       endIndex = i;
       if (lastnode.getChild('|') != null) {
         ret = ret + word.substring(beginIndex, endIndex) + "O(∩_∩)O";
         beginIndex = i + 1;
       }
       else {
         for (int j = i + 1; j < word.length(); ++j) {
           KeyWordNode node = lastnode.getChild(word.charAt(j));
           if (node == null) {
             if (lastnode.getChild('|') == null) break;
             ret = ret + word.substring(beginIndex, endIndex) + "O(∩_∩)O";
             i = j;
             beginIndex = j + 1;
 
             break;
           }
           lastnode = node;
           if (node.getChild('|') != null) {
             ret = ret + word.substring(beginIndex, endIndex) + "O(∩_∩)O";
             i = j;
             beginIndex = j + 1;
             break;
           }
         }
       }
 
     }
 
     ret = ret + word.substring(beginIndex, word.length());
     return ret;
   }
 
   public boolean containsKeyWork(String word) {
     for (int i = 0; i < word.length(); ++i) {
       KeyWordNode lastnode = (KeyWordNode)this.children.get(Character.valueOf(word.charAt(i)));
       if (lastnode == null) {
         continue;
       }
       if (lastnode.getChild('|') != null) {
         return true;
       }
       for (int j = i + 1; j < word.length(); ++j) {
         KeyWordNode node = lastnode.getChild(word.charAt(j));
         if (node == null) {
           if (lastnode.getChild('|') == null) break;
           return true;
         }
 
         lastnode = node;
         if (node.getChild('|') != null) {
           return true;
         }
 
       }
 
     }
 
     return false;
   }
 }
