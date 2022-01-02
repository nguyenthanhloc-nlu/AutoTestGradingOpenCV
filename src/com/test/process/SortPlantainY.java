package com.test.process;

import java.util.Comparator;
import org.opencv.core.Rect;

public class SortPlantainY {
	public static final Comparator<Rect> SORT_PLANTAIN = new Comparator<Rect>() {

		@Override
		public int compare(Rect o1, Rect o2) {
			if (Math.abs(o1.x - o2.x) <= 10) // cùng 1 cột loại bỏ
				return 0;
			else
				return Integer.compare(o1.x, o2.x); // khác cột sắp xếp theo x
		}

	};

}
