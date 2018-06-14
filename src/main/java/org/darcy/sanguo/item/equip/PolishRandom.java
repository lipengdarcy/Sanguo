package org.darcy.sanguo.item.equip;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 洗练随机数
 */
public class PolishRandom {
	public int id;
	public Map<Integer, RandAttr> randAttrs = new HashMap<Integer, RandAttr>();

	public Map<Integer, Boolean> getPolishChange() {
		Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
		Iterator<Integer> itx = this.randAttrs.keySet().iterator();
		while (itx.hasNext()) {
			int index = ((Integer) itx.next()).intValue();
			RandAttr attr = (RandAttr) this.randAttrs.get(Integer.valueOf(index));
			double radom = attr.addRatio / 10000.0D;
			if (Math.random() > radom)
				map.put(Integer.valueOf(index), Boolean.FALSE);
			else {
				map.put(Integer.valueOf(index), Boolean.TRUE);
			}
		}
		return map;
	}

	public class RandAttr {
		public int addRatio;
		public int calLimit;
	}
}
