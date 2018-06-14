 package org.darcy.sanguo.talent;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.util.Calc;
 
 public class Talent
 {
   private UnlockType unlockType;
   private int level;
   private int buffId;
 
   public Talent(String talent)
   {
     int[] args = Calc.split(talent, "\\|");
     if (args.length != 3) {
       throw new IllegalArgumentException(talent);
     }
 
     if (args[0] == 1)
       this.unlockType = UnlockType.LEVEL;
     else {
       this.unlockType = UnlockType.ADVANCE_LEVE;
     }
 
     this.level = args[1];
     this.buffId = args[2];
   }
 
   public UnlockType getUnlockType()
   {
     return this.unlockType;
   }
 
   public int getLevel() {
     return this.level;
   }
 
   public int getBuffId() {
     return this.buffId;
   }
 
   public Buff getBuff() {
     Buff buff = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getBuff(this.buffId);
     return buff.copy();
   }
 
   public void setUnlockType(UnlockType unlockType) {
     this.unlockType = unlockType;
   }
 
   public void setLevel(int level) {
     this.level = level;
   }
 
   public void setBuffId(int buffId) {
     this.buffId = buffId;
   }
 
   public static enum UnlockType
   {
     LEVEL, ADVANCE_LEVE;
   }
 }
