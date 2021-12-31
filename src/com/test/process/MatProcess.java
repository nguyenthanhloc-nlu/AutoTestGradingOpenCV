package com.test.process;

import java.util.ArrayList;
import java.util.List;

//import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
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

	public static List<MatOfPoint> getContour(Mat src) {
		Mat cannyOutput = new Mat();
		Imgproc.Canny(src, cannyOutput, 10, 100); // 80 200

		List<MatOfPoint> contours = new ArrayList<>();// RETR_TREE
		Mat hierarchy = new Mat();
		Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		hierarchy.release();
		cannyOutput.release();
		return contours;
	}

	public static Mat rotate(Mat src, double angle) {
		int width = src.width();
		int height = src.height();
		Mat rotate = Imgproc.getRotationMatrix2D(new Point(src.width() / 2, src.height() / 2), angle, 1);
		Mat dst = new Mat();
		Imgproc.warpAffine(src, dst, rotate, new Size(width, height));
		return dst;
	}

	public static double computeAngleRotate(Point p1, Point p2, Point p3) {
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
		double a = (p1.x - ix) * (p2.x - ix) + (p1.y - iY) * (p1.y - iY);
		double b = Math.sqrt(Math.pow(p1.x - ix, 2) + Math.pow(p1.y - iY, 2));
		double c = Math.sqrt(Math.pow(p2.x - ix, 2) + Math.pow(p1.y - iY, 2));
		double cosa = a / (b * c);
		double angle = Math.acos(cosa);
		if (p1.x - p2.x > 0)
			return Math.toDegrees(angle);

		return -Math.toDegrees(angle);
	}

	public static Point getNewPoint(Point point, double angle) {

//		double degreeDelta = Math.toDegrees(angle);

//		System.out.println("gốc xoay tính bằng độ: " + degreeDelta);
		double sinDelta = Math.sin(angle);
		System.out.println("sinDelta: " + sinDelta);
		double cosDelta = Math.cos(angle);
		System.out.println("cosDelta: " + cosDelta);
		int x = (int) point.x;
		int y = (int) point.y;

		int newX = (int) (x * cosDelta - y * sinDelta);
		int newY = (int) (x * sinDelta + y * cosDelta);

		System.out.println("old x,y: " + point.x + "  " + point.y);
		System.out.println("new x,y: " + newX + "  " + newY);

		Point newPoint = new Point(newX, newY);
		return newPoint;
	}

}
