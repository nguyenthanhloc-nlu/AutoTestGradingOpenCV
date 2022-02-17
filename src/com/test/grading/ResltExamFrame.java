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

public class ResltExamFrame {

	public static final double X_DAP_AN_PERCENT = 0.1;
	public static final double Y_DAP_AN_PERCENT = 0.4755;
	public static final double H_DAP_AN_PERCENT = 0.5245;

	public static int thresh = 100;

	public Mat getAnswerFrame(Mat src) {
//		System.out.println("GetResltExamFrame: getResultFrame");

		// Step 1: GetStubAnswerFrame
		Mat res = getStubAnswerFrame(src);
		Imgcodecs.imwrite("src/img/dap-an-b1-res.jpg", res);

		// Step 2: Image Processing consists of gray and thresh
		Mat gray = MatProcess.toColorGray(res);
		Mat thresh_image = MatProcess.toThreshBinary(gray, 100);
		Imgcodecs.imwrite("src/img/dap-an-b2-thresh_image.jpg", thresh_image);

		// Step 3: Get Contour
		List<MatOfPoint> contours = MatProcess.getContour_1(thresh_image);

		// Step 4: get positioning squares
		Set<Rect> rects = getRects(contours, thresh_image);

		// Step 5: convert Set To ArrayList
		ArrayList<Rect> list = convertSetToArrayList(rects);

		// Step 6: get list Position of rects
		ArrayList<Rect> listPosition = getListPosition(list);

		// Step 7: perform romate again
		Mat frameAfterRomate = frameAfterRomate(listPosition, res);

		// Step 8: drop standard answerframe
		Mat drop_mat = drop_mat(listPosition, frameAfterRomate);

		return drop_mat;
	}

	public Mat getStubAnswerFrame(Mat src) {
//		System.out.println("Class: ResltExamFrame");

		int x = (int) (X_DAP_AN_PERCENT * src.cols());
		int y = (int) (Y_DAP_AN_PERCENT * src.rows() - 20);
		int w = (int) ((1 - X_DAP_AN_PERCENT) * src.cols() - 50);
		int h = (int) (H_DAP_AN_PERCENT * src.rows() + 20);

		Mat res = new Mat(src, new Rect(x, y, w, h));

		return res;
	}

	public Set<Rect> getRects(List<MatOfPoint> contours, Mat thresh_image) {

		System.out.println();
		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE_Y);
		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (rect.area() > 50 && rect.area() < 400 && rect.x < (thresh_image.cols() * 0.2)
					&& Math.abs(rect.width - rect.height) < 5) {
				rects.add(rect);
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

		Mat frameAfterRomate = MatProcess.rotate(stubAnswerFrame, MatProcess.computeAngleRotate(p1, p2, iX, iY));
		Imgcodecs.imwrite("src/img/dap-an-b4-after-romate.jpg", frameAfterRomate);

		return frameAfterRomate;

	}

	public Mat drop_mat(ArrayList<Rect> listPosition, Mat frameAfterRomate) {
		int x_drop = ((listPosition.get(0).x) - 15 < 0) ? 0 : (listPosition.get(0).x) - 15;
		int y_drop = ((listPosition.get(0).y) - 15 < 0) ? 0 : (listPosition.get(0).y) - 15;

		int w_drop_temp = listPosition.get(0).width * 50;
		int w_drop = ((w_drop_temp + x_drop) >= frameAfterRomate.width()) ? frameAfterRomate.width() - x_drop - 1
				: w_drop_temp - 1;

		int h_drop_temp = listPosition.get(0).height * 55;
		int h_drop = ((h_drop_temp + y_drop) >= frameAfterRomate.height()) ? frameAfterRomate.height() - y_drop - 1
				: h_drop_temp - 1;

		Mat drop_Mat = new Mat(frameAfterRomate, new Rect(x_drop, y_drop, w_drop, h_drop));
		return drop_Mat;

	}
}
