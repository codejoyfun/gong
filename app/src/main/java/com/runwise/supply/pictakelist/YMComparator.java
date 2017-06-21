package com.runwise.supply.pictakelist;

import java.util.Comparator;

public class YMComparator implements Comparator<PicTake> {

	@Override
	public int compare(PicTake o1, PicTake o2) {
		return o2.getCreateTime().compareTo(o1.getCreateTime());
	}

}
