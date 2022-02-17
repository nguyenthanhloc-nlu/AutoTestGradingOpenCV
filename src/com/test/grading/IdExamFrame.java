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

public class IdExamFrame {

	public static final double X_MADETHI_STUDENT_PERCENT = 0.8;
	public static final double Y_MADETHI_STUDENT_PERCENT = 0.005;
	public static final double W_MADETHI_STUDENT_PERCENT = 0.1739;
	public static final double H_MADETHI_STUDENT_PERCENT = 0.3;

	private int thresh = 100;

	public int getThresh() {
		return thresh;
	}

	public void setThresh(int thresh) {
		this.thresh = thresh;
	}

	public Mat getStubAnswerFrame(Mat start) {
//		System.out.println("Class: ResltExamFrame");

		System.out.println("GetIdExamFrame: getIDExameFrame");
		int x = (int) (X_MADETHI_STUDENT_PERCENT * start.cols() - 5);
		int y = (int) (Y_MADETHI_STUDENT_PERCENT * start.rows());
		int w = (int) (W_MADETHI_STUDENT_PERCENT * start.cols());
		int h = (int) (H_MADETHI_STUDENT_PERCENT * start.rows());
		Mat getStubAnswerFrame = new Mat(start, new Rect(x, y, w, h));
		Imgcodecs.imwrite("src/img/ma-de-b1-res.jpg", getStubAnswerFrame);

		return getStubAnswerFrame;
	}

	public Set<Rect> getRects(Mat gray) {

		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE_Y);
		for (int thresh_ = 140; thresh_ > 90; thresh_ -= 0.5) {
//		for (int thresh_ = 135; thresh_ > 110; thresh_ -= 0.5) {
			rects.clear();
			Mat thresh_image = MatProcess.toThreshBinary(gray, thresh_);
			Imgcodecs.imwrite("src/img/ma-de-b2-res.jpg", thresh_image);
			List<MatOfPoint> contours = MatProcess.getContour_1(thresh_image);
			for (int i = 0; i < contours.size(); i++) {
				Rect rect = Imgproc.boundingRect(contours.get(i));
				if (rect.area() > 50 && rect.area() < 250 && rect.x < (thresh_image.cols() * 0.29)) {
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

		Imgcodecs.imwrite("src/img/ma-de-b3-before-romate.jpg", stubAnswerFrame);
//		System.out.println("xoay ảnh mã đề");
		Mat frameAfterRomate = MatProcess.rotate(stubAnswerFrame, MatProcess.computeAngleRotate(p1, p2, iX, iY));
		Imgcodecs.imwrite("src/img/ma-de-b4-after-romate.jpg", frameAfterRomate);

		return frameAfterRomate;

	}

	public Mat drop_mat(ArrayList<Rect> listPosition, Mat frameAfterRomate) {
		int x_drop = ((listPosition.get(0).x) - 10 < 0) ? 0 : (listPosition.get(0).x) - 10;
		int y_drop = ((listPosition.get(0).y) - 10 < 0) ? 0 : (listPosition.get(0).y) - 10;

		int w_drop_temp = listPosition.get(0).width * 10;
		int w_drop = ((w_drop_temp + x_drop) >= frameAfterRomate.width()) ? frameAfterRomate.width() - x_drop - 2
				: w_drop_temp - 2;

		int h_drop_temp = listPosition.get(0).height * 23;
		int h_drop = ((h_drop_temp + y_drop) >= frameAfterRomate.height()) ? frameAfterRomate.height() - y_drop - 2
				: h_drop_temp - 2;

		Mat drop_Mat = new Mat(frameAfterRomate, new Rect(x_drop, y_drop, w_drop, h_drop));
		return drop_Mat;

	}

	public Mat getIDExameFrame(Mat start) {

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