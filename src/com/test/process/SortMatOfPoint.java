package com.test.process;

import java.util.Comparator;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class SortMatOfPoint {
	public static final Comparator<MatOfPoint> MAT_OF_POINT_COMPARE_BY_Y = new Comparator<MatOfPoint>() {

		@Override
		public int compare(MatOfPoint o1, MatOfPoint o2) {
			Rect r1 = Imgproc.boundingRect(o1);
			Rect r2 = Imgproc.boundingRect(o2);
			return Integer.compare(r1.y, r2.y);
		}
		
	};
}
