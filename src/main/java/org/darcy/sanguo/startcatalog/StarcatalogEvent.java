 package org.darcy.sanguo.startcatalog;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.attri.Attri;
import org.darcy.sanguo.drop.Reward;
 
 public class StarcatalogEvent
 {
   public static final int Type_ActiveWarrior = 1;
   public static final int Type_ActiveAppointWarrior = 2;
   public static final int Type_ActiveAppointCampWarrior = 3;
   public static final int Type_FavorAmount = 4;
   public static final int Type_FavorAppoint = 5;
   public static final int Type_FavorCampAppoint = 6;
   public static final int Status_Unreceive = 0;
   public static final int Status_Received = 1;
   public static final int Status_Unfinish = 2;
   private int id;
   private List<Reward> rewards;
   private List<Attri> attrs;
   private List<Integer> last;
   private List<Integer> next;
   private int type;
   private List<String> params = new ArrayList();
   private String content;
 
   public String getContent()
   {
     return this.content; }
 
   public void setContent(String content) {
     this.content = content; }
 
   public int getId() {
     return this.id; }
 
   public void setId(int id) {
     this.id = id; }
 
   public List<Reward> getRewards() {
     return this.rewards; }
 
   public void setRewards(List<Reward> rewards) {
     this.rewards = rewards; }
 
   public List<Attri> getAttrs() {
     return this.attrs; }
 
   public void setAttrs(List<Attri> attrs) {
     this.attrs = attrs; }
 
   public List<Integer> getLast() {
     return this.last; }
 
   public void setLast(List<Integer> last) {
     this.last = last; }
 
   public List<Integer> getNext() {
     return this.next; }
 
   public void setNext(List<Integer> next) {
     this.next = next; }
 
   public int getType() {
     return this.type; }
 
   public void setType(int type) {
     this.type = type; }
 
   public List<String> getParams() {
     return this.params; }
 
   public void setParams(List<String> params) {
     this.params = params;
   }
 }
