package com.test.grading;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.test.process.MatProcess;
import com.test.process.RectCompareNoise;

public class IdStudentFrame {

	public static final double X_ID_STUDENT_PERCENT = 0.55;
	public static final double Y_ID_STUDENT_PERCENT = 0.005;
	public static final double W_ID_STUDENT_PERCENT = 0.4;
	public static final double H_ID_STUDENT_PERCENT = 0.3;

	private int thresh = 100;

	public int getThresh() {
		return thresh;
	}

	public void setThresh(int thresh) {
		this.thresh = thresh;
	}

	public Mat getStubAnswerFrame(Mat start) {
//		System.out.println("Class: ResltExamFrame");

		System.out.println("GetIdStudentFrame: getIDStudentFrame");
//		System.err.println("\nstart mssv");
		int x = (int) (X_ID_STUDENT_PERCENT * start.cols());
		int y = (int) (Y_ID_STUDENT_PERCENT * start.rows());
		int w = (int) (W_ID_STUDENT_PERCENT * start.cols());
		int h = (int) (H_ID_STUDENT_PERCENT * start.rows());
		Mat getStubAnswerFrame = new Mat(start, new Rect(x, y, w, h));
		Imgcodecs.imwrite("src/img/mssv-b1-res.jpg", getStubAnswerFrame);
		return getStubAnswerFrame;
	}

	public Set<Rect> getRects(Mat gray) {

		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE_Y);
		for (int thresh_ = 150; thresh_ > 90; thresh_ -= 1) {
			rects.clear();
			Mat thresh_image = MatProcess.toThreshBinary(gray, thresh_);
			Imgcodecs.imwrite("src/img/mssv-b2-thresh.jpg", thresh_image);
			List<MatOfPoint> contours = MatProcess.getContour_1(thresh_image);

//			System.out.println("rects: " + rects);
			for (int i = 0; i < contours.size(); i++) {
				Rect rect = Imgproc.boundingRect(contours.get(i));
				if (rect.area() > 50 && rect.area() < 250 && rect.x < (thresh_image.cols() * 0.12)) {
					rects.add(rect);
				}
			}
			if (rects.size() == 2) {
				this.thresh = thresh_;
				break;
			}
		}

		return rects;
	}

	public ArrayList<Rect> convertSetToArrayList(Set<Rect> rects) {
		ArrayList<Rect> list = new ArrayList<Rect>();
		for (Rect r : rects) {
			list.add(r);
		}
		return list;
	}

	public ArrayList<Rect> getListPosition(ArrayList<Rect> list) {

		ArrayList<Rect> listPosition = new ArrayList<Rect>();
		Rect rectFirst = list.get(0);
		Rect rectLast = list.get(list.size() - 1);

		listPosition.add(rectFirst);
		listPosition.add(rectLast);

		return listPosition;
	}

	public Mat frameAfterRomate(ArrayList<Rect> listPosition, Mat stubAnswerFrame) {

		Point p1 = new Point((listPosition.get(0).x), (listPosition.get(0).y));
		Point p2 = new Point((listPosition.get(1).x), (listPosition.get(1).y));
		int iX = (stubAnswerFrame.width()) / 2;
		int iY = ((listPosition.get(1).y) - (listPosition.get(0).y)) / 2 + (listPosition.get(0).y);

		Imgcodecs.imwrite("src/img/mssv-b3-res-before-romate-mssv.jpg", stubAnswerFrame);
//		System.out.println("xoay ảnh mã đề");
		Mat frameAfterRomate = MatProcess.rotate(stubAnswerFrame, MatProcess.computeAngleRotate(p1, p2, iX, iY));
		Imgcodecs.imwrite("src/img/mssv-b4-after-romate.jpg", frameAfterRomate);

		return frameAfterRomate;

	}

	public Mat drop_mat(ArrayList<Rect> listPosition, Mat frameAfterRomate) {

		int x_drop = ((listPosition.get(0).x) - 10 < 0) ? 0 : (listPosition.get(0).x) - 10;
		int y_drop = ((listPosition.get(0).y) - 10 < 0) ? 0 : (listPosition.get(0).y) - 10;
		int w_drop_temp = listPosition.get(0).width * 13;
		int w_drop = ((w_drop_temp + x_drop) > frameAfterRomate.width()) ? frameAfterRomate.width() - x_drop
				: w_drop_temp;

		int h_drop_temp = listPosition.get(0).height * 23;
		int h_drop = ((h_drop_temp + y_drop) > frameAfterRomate.height()) ? frameAfterRomate.height() - y_drop
				: h_drop_temp;

		Mat drop_Mat = new Mat(frameAfterRomate, new Rect(x_drop, y_drop, w_drop, h_drop));

		return drop_Mat;

	}

	public Mat getIDStudentFrame(Mat start) {

		// Step 1: GetStubAnswerFrame
		Mat getStubAnswerFrame = getStubAnswerFrame(start);

		// Step 2: Image Processing consists of gray
		Mat gray = MatProcess.toColorGray(getStubAnswerFrame);

		// Step 3: Get Contour and thresh
		Set<Rect> rects = getRects(gray);

		// Step 4: convert Set To ArrayList
		ArrayList<Rect> list = convertSetToArrayList(rects);

		// Step 5: get list Position of rects
		ArrayList<Rect> listPosition = getListPosition(list);

		// Step 6: perform romate again
		Mat frameAfterRomate = frameAfterRomate(listPosition, getStubAnswerFrame);

		// Step 7: drop standard id exam frame
		Mat drop_mat = drop_mat(listPosition, frameAfterRomate);

		return drop_mat;
	}

}
