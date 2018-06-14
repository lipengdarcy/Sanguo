 package org.darcy.sanguo.activity;
 
 import java.util.List;

import org.darcy.sanguo.drop.Reward;
 
 public class Rank7Reward
 {
   private int id;
   private int startRank;
   private int endRank;
   private List<Reward> rewards;
 
   public int getId()
   {
     return this.id; }
 
   public int getStartRank() {
     return this.startRank; }
 
   public int getEndRank() {
     return this.endRank; }
 
   public List<Reward> getRewards() {
     return this.rewards; }
 
   public void setId(int id) {
     this.id = id; }
 
   public void setStartRank(int startRank) {
     this.startRank = startRank; }
 
   public void setEndRank(int endRank) {
     this.endRank = endRank; }
 
   public void setRewards(List<Reward> rewards) {
     this.rewards = rewards;
   }
 }
