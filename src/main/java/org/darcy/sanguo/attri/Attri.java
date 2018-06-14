 package org.darcy.sanguo.attri;
 
 import org.darcy.sanguo.util.Calc;
 
 public class Attri
 {
   private int aid;
   private int value;
 
   public Attri(String args)
   {
     int[] ls = Calc.split(args, "\\|");
     this.aid = ls[0];
     this.value = ls[1];
   }
 
   public Attri(int aid, int value) {
     this.aid = aid;
     this.value = value; }
 
   public int getAid() {
     return this.aid; }
 
   public int getValue() {
     return this.value; }
 
   public void setAid(int aid) {
     this.aid = aid; }
 
   public void setValue(int value) {
     this.value = value;
   }
 }
