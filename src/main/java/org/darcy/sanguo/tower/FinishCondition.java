 package org.darcy.sanguo.tower;
 
 import java.text.MessageFormat;
 
 public class FinishCondition
 {
   public static final int FC_ROUND_LIMIT = 1;
   public static final int FC_HP_LIMIT = 2;
   public static final int FC_DIE_LIMIT = 3;
   public int type;
   public int value;
 
   public FinishCondition(String fc)
   {
     String[] s = fc.split("\\|");
     this.type = Integer.parseInt(s[0]);
     this.value = Integer.parseInt(s[1]);
   }
 
   public String getName()
   {
     String rst = "消灭全部敌人";
     switch (this.type)
     {
     case 3:
       rst = "我方阵亡武将少于{0}个";
       break;
     case 2:
       rst = "损失血量低于{0}%";
       break;
     case 1:
       rst = "在{0}回合内取得胜利";
     }
 
     return MessageFormat.format(rst, new Object[] { Integer.valueOf(this.value) });
   }
 }
