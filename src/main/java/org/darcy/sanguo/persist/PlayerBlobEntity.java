package org.darcy.sanguo.persist;

import java.io.Serializable;

public abstract interface PlayerBlobEntity extends Serializable
{
  public static final int BLOB_WARRIORS = 1;
  public static final int BLOB_BAGS = 2;
  public static final int BLOB_LOOTTREASURE = 3;
  public static final int BLOB_RECRUIT_RECORD = 4;
  public static final int BLOB_MAP_RECORD = 5;
  public static final int BLOB_POOL = 6;
  public static final int BLOB_EXCHANGES = 7;
  public static final int BLOB_GLOBAL_DROP = 8;
  public static final int BLOB_TOWER = 9;
  public static final int BLOB_RANDOM_SHOP = 10;
  public static final int BLOB_BOSS = 11;
  public static final int BLOB_REWARD_RECORD = 12;
  public static final int BLOB_DESTINY = 13;
  public static final int BLOB_TACTIC_RECORD = 14;
  public static final int BLOB_COUP = 15;
  public static final int BLOB_DIVINE = 16;
  public static final int BLOB_STAR = 17;
  public static final int BLOB_TASK = 18;
  public static final int BLOB_ACTIVITY_RECORD = 19;
  public static final int BLOB_GLORY_RECORD = 20;
  public static final int BLOB_STARCATALOG = 21;

  public abstract int getBlobId();
}
