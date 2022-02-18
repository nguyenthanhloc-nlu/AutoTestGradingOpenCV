package com.test.process;

import java.util.ArrayList;
import java.util.List;

//import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MatProcess {

	public static Mat toColorGray(Mat src) {
		Mat srcGray = new Mat();
		Mat dest = new Mat();
		Imgproc.GaussianBlur(src, srcGray, new Size(5, 5), 0);
		Imgproc.cvtColor(srcGray, dest, Imgproc.COLOR_BGR2GRAY);
		return dest;
	}

	public static Mat toThreshBinary(Mat src, double thresh) {
		Mat roi_thresh = new Mat();
		Imgproc.threshold(src, roi_thresh, thresh, 250, Imgproc.THRESH_BINARY_INV);
		return roi_thresh;
	}

	public static Mat toThreshBinary(Mat src) {
		Mat roi_thresh = new Mat();
//		Imgproc.threshold(src, roi_thresh, thresh, 250, Imgproc.THRESH_BINARY_INV);
		Imgproc.adaptiveThreshold(src, roi_thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV,
				31, 11);
		return roi_thresh;
	}

	public static List<MatOfPoint> getContour(Mat src) {
//		System.out.println("MatProcess: getContour");

		Mat cannyOutput = new Mat();
		Imgproc.Canny(src, cannyOutput, 10, 100); // 80 200

		List<MatOfPoint> contours = new ArrayList<>();// RETR_TREE
		Mat hierarchy = new Mat();
		Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		hierarchy.release();
		cannyOutput.release();
		return contours;
	}

//	---
	public static List<MatOfPoint> getContour_1(Mat src) {

//		System.out.println("MatProcess: getContour_1");
		Mat cannyOutput = new Mat();
		Imgproc.Canny(src, cannyOutput, 127, 255);

		List<MatOfPoint> contours = new ArrayList<>();// RETR_TREE
		Mat hierarchy = new Mat();
		Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		hierarchy.release();
		cannyOutput.release();
		return contours;
	}

	public static Mat rotate(Mat src, double angle) {

//		System.out.println("MatProcess: rotate");
		int width = src.width();
		int height = src.height();
		Mat rotate = Imgproc.getRotationMatrix2D(new Point(src.width() / 2, src.height() / 2), angle, 1);
		Mat dst = new Mat();
		Imgproc.warpAffine(src, dst, rotate, new Size(width, height));
		return dst;
	}

	public static double computeAngleRotate(Point p1, Point p2, Point p3) {

//		System.out.println("MatProcess: computeAngleRotate");

		double iY = (p1.y + p2.y) / 2 + p1.y;
		double ix = (p1.x + p3.x) / 2 + p1.x;

		double a = (p1.x - ix) * (p2.x - ix) + (p1.y - iY) * (p1.y - iY);
		double b = Math.sqrt(Math.pow(p1.x - ix, 2) + Math.pow(p1.y - iY, 2));
		double c = Math.sqrt(Math.pow(p2.x - ix, 2) + Math.pow(p1.y - iY, 2));
		double cosa = a / (b * c);
		double angle = Math.acos(cosa);
		if (p1.x - p2.x > 0)
			return Math.toDegrees(angle);

		return -Math.toDegrees(angle);

	}

	public static double computeAngleRotate(Point p1, Point p2, int ix, int iY) {

//		System.out.println("MatProcess: computeAngleRotate");

		double a = (p1.x - ix) * (p2.x - ix) + (p1.y - iY) * (p1.y - iY);
		double b = Math.sqrt(Math.pow(p1.x - ix, 2) + Math.pow(p1.y - iY, 2));
		double c = Math.sqrt(Math.pow(p2.x - ix, 2) + Math.pow(p1.y - iY, 2));
		double cosa = a / (b * c);
		double angle = Math.acos(cosa);
//		System.out.println(angle + "fdfd");
		if (Double.isNaN(angle)) {
			return 0;
		}

		if (p1.x - p2.x > 0)
			return Math.toDegrees(angle);

		return -Math.toDegrees(angle);
	}

	public static Point pointAfterRotate(Point oldPoint, double angle, Point central) {

//		System.out.println("MatProcess: pointAfterRotate");

		double x = (oldPoint.x - central.x) * Math.cos(Math.toRadians(angle))
				- (oldPoint.y - central.y) * Math.sin(Math.toRadians(angle));
		double y = (oldPoint.x - central.x) * Math.sin(Math.toRadians(angle))
				+ (oldPoint.y - central.y) * Math.cos(Math.toRadians(angle));
		return new Point(x + central.x, y + central.y);
	}

	public static Mat reSize(Mat mat, int w, int h) {
		Mat destMat = new Mat();
		Imgproc.resize(mat, destMat, new Size(w, h), 0, 0, Imgproc.INTER_AREA);
		return destMat;
	}
	// images gray
	public static Mat imgGray(Mat matPathImg) {
		Mat dstImg = new Mat();
		Imgproc.cvtColor(matPathImg, dstImg, Imgproc.COLOR_BGR2GRAY);
		Mat dstGray = new Mat(dstImg.rows(), dstImg.cols(), dstImg.type());
		Imgproc.GaussianBlur(dstImg, dstGray, new Size(3, 3), 0);
		return dstGray;
	}

	// threshold resize
	public static Mat threshold(Mat matPathImg,int wBoxPlan_StuID,int hBoxP_StuID ) {
		Mat imgGrayResize = reSize(imgGray(matPathImg), wBoxPlan_StuID, hBoxP_StuID);
		Mat dstThres = new Mat(imgGrayResize.rows(), imgGrayResize.cols(), imgGrayResize.type(), new Scalar(0));
		Imgproc.adaptiveThreshold(imgGrayResize, dstThres, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
				Imgproc.THRESH_BINARY_INV, 31, 30);
		return dstThres;
	}
}
