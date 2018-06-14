package org.darcy.sanguo.pay;

import java.util.Date;

public class PayItem
{
  public int goodsId;
  public String name;
  public int count;
  public int price;
  public boolean firstRecomend;
  public int firstGive;
  public int nomalGive;
  public String coGoodsId;
  public String iconId;
  public boolean isMonthCard;
  public boolean isShowInPayPage;
  public boolean isLimit;
  public Date limitStart;
  public Date limitEnd;
  public int limitGive;
  public boolean isEveryDayFirst;
}
