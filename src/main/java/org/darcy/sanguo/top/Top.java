 package org.darcy.sanguo.top;
 
 import org.darcy.sanguo.util.Calc;
 
 public class Top
 {
   public static final int TOWER = 0;
   public static final int MAP = 1;
   public static final int BTLCAP = 2;
   public static final int LEVEL = 3;
   public static final int ARENA = 4;
   public static final int COMPETITION = 5;
   public static final int LEAGUE = 6;
   public static final int BOSS_LEAGUE = 7;
   private int id;
   private int rank;
   private int type;
   private int value;
   private int pid;
   private String note;
 
   public int compareNote(String note)
   {
     if (this.type == 1) {
       int[] myNotes = Calc.split(this.note, ",");
       int[] notes = Calc.split(note, ",");
       if (myNotes[0] > notes[0])
         return 1;
       if (myNotes[0] < notes[0]) {
         return -1;
       }
       return (myNotes[1] - notes[1]);
     }
 
     return -1;
   }
 
   public int getId()
   {
     return this.id; }
 
   public int getRank() {
     return this.rank; }
 
   public int getType() {
     return this.type; }
 
   public int getPid() {
     return this.pid; }
 
   public void setId(int id) {
     this.id = id; }
 
   public void setRank(int rank) {
     this.rank = rank; }
 
   public int getValue() {
     return this.value; }
 
   public void setValue(int value) {
     this.value = value; }
 
   public void setType(int type) {
     this.type = type; }
 
   public void setPid(int pid) {
     this.pid = pid; }
 
   public String getNote() {
     return this.note; }
 
   public void setNote(String note) {
     this.note = note;
   }
 }
