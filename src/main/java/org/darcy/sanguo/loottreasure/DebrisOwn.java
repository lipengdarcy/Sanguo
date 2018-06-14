 package org.darcy.sanguo.loottreasure;
 
 import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
 
 public class DebrisOwn
   implements Serializable
 {
   private static final long serialVersionUID = -3441930132545665235L;
   private int templateId;
   private List<Integer> owners = new CopyOnWriteArrayList();
 
   public DebrisOwn(int templateId) {
     this.templateId = templateId;
   }
 
   public int getTemplateId() {
     return this.templateId;
   }
 
   public void setTemplateId(int templateId) {
     this.templateId = templateId;
   }
 
   public List<Integer> getOwners() {
     return this.owners;
   }
 
   public void setOwners(List<Integer> owners) {
     this.owners = owners;
   }
 
   public String toString()
   {
     return "DebrisOwn [templateId=" + this.templateId + ", owners=" + this.owners + "]";
   }
 }
