package com.test.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.test.process.MatProcess;
import com.test.process.PositonSquare;
import com.test.process.RectCompareNoise;

public class Paper {

	private static double totalArea;

	public Paper() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public Mat getPaper(Mat src) {

		System.out.println("Paper: getPaper");

		Mat result = new Mat();

		return result;
	}

	public PositionAndThreshValue getPositionPaperAndThreshValue(Mat src) {
		List<Rect> result = new ArrayList<Rect>();

		Mat newSize = new Mat();
		Imgproc.resize(src, newSize, new Size(src.width() / 2, src.height() / 2));
		totalArea = newSize.width() * newSize.height();

		Mat gray = MatProcess.toColorGray(newSize);

		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);
		int theshIs = 0;
		for (int thresh_ = 135; thresh_ > 100; thresh_ -= 0.5) {

			rects.clear();
			Mat thresh_image = MatProcess.toThreshBinary(gray, thresh_);
			Imgcodecs.imwrite("src/img/getPositionPaper-thresh_image.jpg", thresh_image);
			List<MatOfPoint> contours = MatProcess.getContour(thresh_image);

			for (int i = 0; i < contours.size(); i++) {
				Rect rect = Imgproc.boundingRect(contours.get(i));

				if ((rect.width > 18 || rect.width < 35) && (rect.height > 18 || rect.height < 35)
						&& rect.area() > (18 * 18) && rect.area() < (35 * 35)
						&& Math.abs(rect.width - rect.height) < 5) {
					rects.add(rect);
				}
			}
			result = filterForId_Exam(rects);
			theshIs = thresh_;
		}
//		System.out.println("getPositionPaper: Final thresh " + theshIs);
//		System.out.println(result);

		PositionAndThreshValue positionAndThreshValue = new PositionAndThreshValue(result, theshIs);
		return positionAndThreshValue;
	}

	public List<Rect> getPositionPaperAfterRomomate(Mat src, PositionAndThreshValue positionThreshValue) {

		List<Rect> result = new ArrayList<Rect>();

		totalArea = src.width() * src.height();

		Mat gray = MatProcess.toColorGray(src);

		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);

		int theshIss = positionThreshValue.getThreshValue();

//		System.out.println("positionThreshValue.getThreshValue(): " + theshIss);

		Mat thresh_image = MatProcess.toThreshBinary(gray, theshIss);
		Imgcodecs.imwrite("src/img/getPositionPaperAfterRomomate-thresh_image.jpg", thresh_image);
		List<MatOfPoint> contours = MatProcess.getContour(thresh_image);

		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));

			if ((rect.width > 16 || rect.width < 35) && (rect.height > 16 || rect.height < 35)
					&& rect.area() > (16 * 16) && rect.area() < (35 * 35) && Math.abs(rect.width - rect.height) < 5) {
				rects.add(rect);
			}
		}
		result = filterForId_Exam(rects);
		System.out.println("getPositionPaperAfterRomomate " + result);
		return result;
	}

	public static List<Rect> filterForId_Exam(Set<Rect> rects) {

//		final long startTime = System.currentTimeMillis();

//		System.out.println("Paper: filterForId_Exam");

		if (rects.size() == 4) {
			ArrayList<Rect> list = new ArrayList<Rect>();
			for (Rect r : rects) {
				list.add(r);
			}
			return list;
		} else {

			List<Rect> data = rects.stream().collect(Collectors.toList());

			Set<Rect> test = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);
			Set<Rect> test2 = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);
			List<Rect> rs = new ArrayList<Rect>();

			rs = test.stream().collect(Collectors.toList());
			if (data.size() > 4) {
				for (int i = 0; i < data.size(); i++) {
					for (int j = i + 1; j < data.size(); j++) {
						for (int k = j + 1; k < data.size(); k++) {
							for (int l = k + 1; l < data.size(); l++) {
								List<Rect> tmp = new ArrayList<Rect>();
								List<Rect> returnList = new ArrayList<Rect>();
								tmp.add(data.get(i));
								tmp.add(data.get(j));
								tmp.add(data.get(k));
								tmp.add(data.get(l));
								returnList.addAll(tmp);

								PositonSquare positonSquare = new PositonSquare();

								Rect topLeft = positonSquare.findRectTopLeft(tmp);
								tmp.remove(topLeft);
								Rect bottomRight = positonSquare.findRectBottomRight(tmp);
								tmp.remove(bottomRight);
								Rect topRight = positonSquare.findRectTopRight(tmp);
								tmp.remove(topRight);
								Rect bottomLeft = positonSquare.findRectBottomLeft(tmp);
								tmp.remove(bottomLeft);

								int width1 = topRight.x - topLeft.x; // 1
								int width2 = bottomRight.x - bottomLeft.x; // 2

								int height1 = bottomLeft.y - topLeft.y;
								int height2 = bottomRight.y - topRight.y;

								double cheo1 = Math.sqrt(Math.pow(width2, 2) + Math.pow(height1, 2));
//								System.out.println("duong cheo 1 : " + cheo1);
								double cheo2 = Math.sqrt(Math.pow(width1, 2) + Math.pow(height2, 2));
//								System.out.println("duong cheo2 : " + cheo2);

								if (Math.abs(width1 - width2) < 100 && Math.abs(height2 - height1) < 100
										&& Math.abs(cheo1 - cheo2) < 200
										&& Math.abs(topLeft.y - topRight.y) < topLeft.height * 2
										&& Math.abs(bottomLeft.y - bottomRight.y) < bottomLeft.height * 2
										&& Math.abs(bottomLeft.x - topLeft.x) < bottomLeft.width * 2
										&& Math.abs(bottomRight.x - topRight.x) < bottomRight.width * 2
										&& cheo1 > 600) {
									return returnList;
								}
							}
						}
					}
				}

//				final long endTime = System.currentTimeMillis();
//				System.err.println("Time execute filterForId_Exam: " + (endTime - startTime));
				return test2.stream().collect(Collectors.toList());
			}
//			final long endTime = System.currentTimeMillis();
//			System.err.println("Time execute filterForId_Exam: " + (endTime - startTime));
			return rs;
		}
	}

	public Mat getFrameRomatedBeforeCrop(List<Rect> listRect, Mat src) {

		Mat m = new Mat();
		Imgproc.resize(src, m, new Size(src.width() / 2, src.height() / 2));

		Imgcodecs.imwrite("src/img/getFrameRomatedBeforeCrop-resize.jpg", m);

		totalArea = src.width() / 2 * src.height() / 2;

		for (int i = 0; i < listRect.size(); i++) {
			Imgproc.rectangle(m, listRect.get(i), new Scalar(0, 255, 0));
		}

		System.out.println("getFrameRomatedBeforeCrop listRect: " + listRect);

		double angle = MatProcess.computeAngleRotate(new Point(listRect.get(0).x, listRect.get(0).y),
				new Point(listRect.get(1).x, listRect.get(1).y), new Point(listRect.get(2).x, listRect.get(2).y));

		Mat table = MatProcess.rotate(m, angle);
//		Imgcodecs.imwrite("src/img/getFrameRomatedBeforeCrop-romated.jpg", table);
		return table;
	}

	public Mat getFrameRomatedAfterCrop(List<Rect> listRect, Mat src) {

		Mat m = new Mat();
		Imgproc.resize(src, m, new Size(src.width(), src.height()));

		totalArea = src.width() * src.height();

		int xTopLeft = listRect.get(0).x;
		int yTopLeft = listRect.get(0).y;

		int xTopRight = listRect.get(2).x;

		int yBottomLeft = listRect.get(1).y;

		int wAfterRomate = (int) ((xTopRight - xTopLeft));
		int hAfterRomate = (int) ((yBottomLeft - yTopLeft));

		Imgcodecs.imwrite("src/img/getFrameRomatedAfterCrop-mmm.jpg", m);

		Mat drop_Mat = new Mat(m, new Rect((xTopLeft), (yTopLeft) - 5, wAfterRomate, (hAfterRomate) + 25));

		Imgcodecs.imwrite("src/img/paper-getFrameRomatedAfterCrop-drop-matbefore-.jpg", m);
		Imgcodecs.imwrite("src/img/paper-getFrameRomatedAfterCrop-drop-mat.jpg", drop_Mat);

		return drop_Mat;
	}

	public Mat getRectIn4Rect(List<Rect> listRect, Mat src) {

		final long startTime = System.currentTimeMillis();

		System.out.println("Paper: getRectIn4RectForResult");

		Mat m = new Mat();
		Imgproc.resize(src, m, new Size(src.width() / 2, src.height() / 2));

		Imgcodecs.imwrite("src/img/paper-m.jpg", m);

		totalArea = src.width() / 2 * src.height() / 2;

		for (int i = 0; i < listRect.size(); i++) {
			Imgproc.rectangle(m, listRect.get(i), new Scalar(0, 255, 0));
		}

		System.out.println("paper listRect: " + listRect);

		double angle = MatProcess.computeAngleRotate(new Point(listRect.get(0).x, listRect.get(0).y),
				new Point(listRect.get(1).x, listRect.get(1).y), new Point(listRect.get(2).x, listRect.get(2).y));

		Imgcodecs.imwrite("src/img/paper-table-before-romate.jpg", m);

		Mat table = MatProcess.rotate(m, angle);
		Imgcodecs.imwrite("src/img/paper-table-after-romate.jpg", table);

		int xTopLeft = listRect.get(0).x;
		int yTopLeft = listRect.get(0).y;

		int xTopRight = listRect.get(2).x;
		int yTopRight = listRect.get(2).y;

		int xBottomLeft = listRect.get(1).x;
		int yBottomLeft = listRect.get(1).y;

		int xi = ((xTopRight - xTopLeft) / 2) + xTopLeft;
		int yi = ((yBottomLeft - yTopLeft) / 2) + yTopLeft;

		System.out.println("");

		Point i = new Point(xi, yi);

		Point newPointTopLeft = MatProcess.pointAfterRotate(new Point(xTopLeft, yTopLeft), angle, i);
		Point newPointTopRight = MatProcess.pointAfterRotate(new Point(xTopRight, yTopRight), angle, i);
		Point newPointBottomLeft = MatProcess.pointAfterRotate(new Point(xBottomLeft, yBottomLeft), angle, i);

		int xAfterRomate = (int) newPointTopLeft.x;
		int yAfterRomate = (int) newPointTopLeft.y;
		int wAfterRomate = (int) ((newPointTopRight.x - newPointTopLeft.x));
		int hAfterRomate = (int) ((newPointBottomLeft.y - newPointTopLeft.y) + 40);

		Mat drop_Mat = new Mat(table, new Rect(xAfterRomate, yAfterRomate, wAfterRomate, hAfterRomate));

		Imgcodecs.imwrite("src/img/paper-result_filter.jpg", table);
		Imgcodecs.imwrite("src/img/paper-drop_mat.jpg", drop_Mat);

		final long endTime = System.currentTimeMillis();
		System.err.println("Time getRectIn4RectForResult: " + (endTime - startTime));
		return drop_Mat;

	}

	public Mat getRectIn4RectForId(Mat src) {
		final long startTime = System.currentTimeMillis();
		System.out.println("Paper: getRectIn4RectForId");

		Mat m = new Mat();
		Imgproc.resize(src, m, new Size(src.width() / 2, src.height() / 2));
		totalArea = src.width() / 2 * src.height() / 2;

		Mat gray = new Mat();
		Mat thresh_image = new Mat();

		gray = MatProcess.toColorGray(m);
		thresh_image = MatProcess.toThreshBinary(gray, 100);

		List<MatOfPoint> contours = MatProcess.getContour(thresh_image);

		// l???c ph???n t??? g??y nhi???u
		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);

		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));

			if (rect.area() > 300 && rect.area() < 1500 && Math
					.abs(rect.width - rect.height) < ((rect.width > rect.height) ? rect.width / 2 : rect.height / 2)) {
				MatOfPoint src2 = contours.get(i);

				MatOfPoint2f dst = new MatOfPoint2f();
				src2.convertTo(dst, CvType.CV_32F);
				double peri = Imgproc.arcLength(dst, true);
				MatOfPoint2f approxCurve = new MatOfPoint2f();
				Imgproc.approxPolyDP(dst, approxCurve, peri * 0.05, true);

				if (approxCurve.toArray().length == 4)
					rects.add(rect);
			}
		}
		List<Rect> rs = filterForId_Exam(rects);
		for (int i = 0; i < rs.size(); i++) {
			Imgproc.rectangle(m, rs.get(i), new Scalar(0, 255, 0));
		}

		Mat rectInFourRect = new Mat();
		Rect[] d = new Rect[4];
		rs.toArray(d);
		int width = d[2].x - d[0].x;
		int height = d[1].y - d[0].y + d[1].height;
		// h??nh ch??? nh???t trong b???n h??nh vu??ng
		Point p1 = new Point(d[0].x, d[0].y);
		Point p2 = new Point(d[1].x, d[1].y);
		Point p3 = new Point(d[2].x, d[2].y);
		double angle = MatProcess.computeAngleRotate(p1, p2, p3);
		Mat imgRomated = MatProcess.rotate(m, angle);

		rectInFourRect = new Mat(imgRomated, new Rect(d[0].x, d[0].y, width, height));

		final long endTime = System.currentTimeMillis();
		System.err.println("Time getRectIn4RectForId: " + (endTime - startTime));

		return rectInFourRect;

	}
}
