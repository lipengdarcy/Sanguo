package org.darcy.sanguo.en;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.service.common.ItemService;

public class En {
	public int id;
	public int[] itemList;
	public int[] attriIndexs;
	public int[] attriValues;
	public String name;
	public String description;
	public int groupId;
	public int order;

	public List<ItemTemplate> getItems() {
		List<ItemTemplate> list = new ArrayList<ItemTemplate>(this.itemList.length);
		for (int i : this.itemList) {
			ItemTemplate tplt = ItemService.getItemTemplate(i);
			if (tplt != null) {
				list.add(tplt);
			}
		}
		return list;
	}
}
