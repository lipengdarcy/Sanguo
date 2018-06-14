 package org.darcy.sanguo.keyword;
 
 import java.util.HashMap;
 
 public class KeyWordNode
 {
   char value;
   HashMap<Character, KeyWordNode> children = new HashMap();
 
   public KeyWordNode(char value) {
     this.value = value;
   }
 
   public void addChild(KeyWordNode node) {
     this.children.put(Character.valueOf(node.value), node);
   }
 
   public KeyWordNode getChild(char key) {
     KeyWordNode node = (KeyWordNode)this.children.get(Character.valueOf(key));
     if (node == null) {
       if ((key >= 'a') && (key <= 'z'))
         return ((KeyWordNode)this.children.get(Character.valueOf((char)(key - ' '))));
       if ((key >= 'A') && (key <= 'Z')) {
         return ((KeyWordNode)this.children.get(Character.valueOf((char)(key + ' '))));
       }
     }
     return node;
   }
 }
