package org.darcy.sanguo.sync;

import com.google.protobuf.MessageOrBuilder;

public abstract class DataSync
{
  public abstract MessageOrBuilder genBuilder();
}
