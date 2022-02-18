package com.test.grading;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class ResltExamFrame {

	public static final double X_DAP_AN_PERCENT = 0.1;
	public static final double Y_DAP_AN_PERCENT = 0.4755;
	public static final double H_DAP_AN_PERCENT = 0.5245;

	public Mat getAnswerFrame(Mat src) {
		int x = (int) (X_DAP_AN_PERCENT * src.cols());
		int y = (int) (Y_DAP_AN_PERCENT * src.rows() - 10);
		int w = (int) ((1 - X_DAP_AN_PERCENT) * src.cols() - 10);
		int h = (int) (H_DAP_AN_PERCENT * src.rows());

		Mat res = new Mat(src, new Rect(x, y, w, h));

		return res;
	}
}
