package com.test.grading;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class IdExamFrame {

	public static final double X_MADETHI_STUDENT_PERCENT = 0.8;
	public static final double Y_MADETHI_STUDENT_PERCENT = 0.000002;
	public static final double W_MADETHI_STUDENT_PERCENT = 0.1739;
	public static final double H_MADETHI_STUDENT_PERCENT = 0.29;

	public Mat getIDExameFrame(Mat start) {

		int x = (int) (X_MADETHI_STUDENT_PERCENT * start.cols());
		int y = (int) (Y_MADETHI_STUDENT_PERCENT * start.rows());
		int w = (int) (W_MADETHI_STUDENT_PERCENT * start.cols());
		int h = (int) (H_MADETHI_STUDENT_PERCENT * start.rows());
		Mat getStubAnswerFrame = new Mat(start, new Rect(x, y, w, h));

		return getStubAnswerFrame;

	}

}
