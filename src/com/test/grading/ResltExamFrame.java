package com.test.grading;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class ResltExamFrame {

	public static final double X_DAP_AN_PERCENT = 0.1;
	public static final double Y_DAP_AN_PERCENT = 0.452;

	public Mat getAnswerFrame(Mat src) {
		int x = (int) (X_DAP_AN_PERCENT * src.cols());
		int y = (int) (Y_DAP_AN_PERCENT * src.rows());
		int w = (int) ((1 - X_DAP_AN_PERCENT) * src.cols() - 20);
		int h = (int) ((1 - Y_DAP_AN_PERCENT) * src.rows());

		Mat res = new Mat(src, new Rect(x, y, w, h));
		return res;
	}
}
