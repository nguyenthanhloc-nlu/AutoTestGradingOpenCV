package com.test.grading;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class IdStudentFrame {

	public static final double X_ID_STUDENT_PERCENT = 0.55;
	public static final double Y_ID_STUDENT_PERCENT = 0.0043;
	public static final double W_ID_STUDENT_PERCENT = 0.27;
	public static final double H_ID_STUDENT_PERCENT = 0.285;

	public Mat getIDStudentFrame(Mat start) {
		int x = (int) (X_ID_STUDENT_PERCENT * start.cols());
		int y = (int) (Y_ID_STUDENT_PERCENT * start.rows());
		int w = (int) (W_ID_STUDENT_PERCENT * start.cols());
		int h = (int) (H_ID_STUDENT_PERCENT * start.rows());
		Mat getStubAnswerFrame = new Mat(start, new Rect(x, y, w, h));
		return getStubAnswerFrame;
	}

}
