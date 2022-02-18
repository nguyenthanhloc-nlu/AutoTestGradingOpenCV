package com.test.grading;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

public class IdStudentFrame {

	public static final double X_ID_STUDENT_PERCENT = 0.55;
	public static final double Y_ID_STUDENT_PERCENT = 0.005;
	public static final double W_ID_STUDENT_PERCENT = 0.27;
	public static final double H_ID_STUDENT_PERCENT = 0.29;

	public Mat getIDStudentFrame(Mat start) {
		int x = (int) (X_ID_STUDENT_PERCENT * start.cols());
		int y = (int) (Y_ID_STUDENT_PERCENT * start.rows());
		int w = (int) (W_ID_STUDENT_PERCENT * start.cols());
		int h = (int) (H_ID_STUDENT_PERCENT * start.rows());
		Mat getStubAnswerFrame = new Mat(start, new Rect(x, y, w, h));
		Imgcodecs.imwrite("src/img/mssv-b1-res.jpg", getStubAnswerFrame);
		return getStubAnswerFrame;
	}

}
