package com.test.model;

import java.util.ArrayList;
import java.util.List;
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
import com.test.process.RectCompareNoise;

public class Paper {

	private static double totalArea;

	public Paper() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public Mat getPaper(Mat src) {
		Mat gray = MatProcess.toColorGray(src);
		Mat thresh = MatProcess.toThreshBinary(gray, 100);
		return thresh;
	}

	public List<Rect> getPositionPaper(Mat src) {
		List<Rect> result = new ArrayList<Rect>();

		Mat newSize = new Mat();
		Imgproc.resize(src, newSize, new Size(src.width() / 2, src.height() / 2));
		totalArea = newSize.width() * newSize.height();

		Mat gray = MatProcess.toColorGray(newSize);
		Mat thresh_image = MatProcess.toThreshBinary(gray, 110);
//		Imgcodecs.imwrite("result_test4.jpg", thresh_image);

		List<MatOfPoint> contours = MatProcess.getContour(thresh_image);

		// lọc phần tử gây nhiễu
		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);

		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));

			if (rect.area() > 600 && rect.area() < 1500 && Math
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

		result = filter(rects);
		return result;
	}

	public Mat get4Rect(Mat src) {
		Mat m = new Mat();
		Imgproc.resize(src, m, new Size(src.width() / 2, src.height() / 2));
		totalArea = src.width() / 2 * src.height() / 2;

		System.out.println(totalArea);

		Mat gray = new Mat();
		Mat thresh_image = new Mat();

		gray = MatProcess.toColorGray(m);
		thresh_image = MatProcess.toThreshBinary(gray, 115);
//		Imgcodecs.imwrite("src/img/result_test4.jpg", thresh_image);

		List<MatOfPoint> contours = MatProcess.getContour(thresh_image);

		// lọc phần tử gây nhiễu
		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);

		// 80, 670
		// xác định 6 tọa độ 6 ô đen, lấy cột đán án
		// ctdl Set không chứa phần tử trùng, dùng để remove nhiễu

		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));

			if (rect.area() > 300 && rect.area() < 1500 && Math
					.abs(rect.width - rect.height) < ((rect.width > rect.height) ? rect.width / 2 : rect.height / 2)) {
//				Imgproc.rectangle(m, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
//						new Scalar(0, 255, 0));

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
//		
		List<Rect> rs = filter(rects);
		for (int i = 0; i < rs.size(); i++) {
			Imgproc.rectangle(m, rs.get(i), new Scalar(0, 255, 0));
		}

//		System.out.println("List Rect: " + rs);

//		Imgcodecs.imwrite("src/img/result_test3.jpg", m);
		return m;
	}

	public Mat getRectIn4Rect(Mat src) {
		Mat m = new Mat();
		Imgproc.resize(src, m, new Size(src.width() / 2, src.height() / 2));
		totalArea = src.width() / 2 * src.height() / 2;

		System.out.println("Diện tích: " + src.width() / 2 + " x " + src.height() / 2 + " = " + totalArea);

		Mat gray = new Mat();
		Mat thresh_image = new Mat();

		gray = MatProcess.toColorGray(m);
		thresh_image = MatProcess.toThreshBinary(gray, 115);
//		Imgcodecs.imwrite("src/img/result_test4.jpg", thresh_image);

		List<MatOfPoint> contours = MatProcess.getContour(thresh_image);

		// lọc phần tử gây nhiễu
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
//		
		List<Rect> rs = filter(rects);
		for (int i = 0; i < rs.size(); i++) {
			Imgproc.rectangle(m, rs.get(i), new Scalar(0, 255, 0));
		}

		Mat rectInFourRect = new Mat();
		Rect[] d = new Rect[4];
		rs.toArray(d);
		System.out.println("list: " + rs);
		// width, height Mat inner 4 square
		int width = d[2].x - d[0].x;
		int height = d[1].y - d[0].y + d[1].height;
		// hình chủ nhật trong bốn hình vuông
		Point p1 = new Point(d[0].x, d[0].y);
		Point p2 = new Point(d[1].x, d[1].y);
		Point p3 = new Point(d[2].x, d[2].y);
		double angle = MatProcess.computeAngleRotate(p1, p2, p3);
		System.out.println("Gốc xoay: " + angle);
		Mat imgRomated = MatProcess.rotate(m, angle);
//		Imgcodecs.imwrite("src/img/xoay-anh.jpg", imgRomated);

		Point newPoint0 = MatProcess.getNewPoint(p1, angle);

		int newPointX = (int) newPoint0.x;
		int newPointY = (int) newPoint0.y;

		rectInFourRect = new Mat(imgRomated, new Rect(d[0].x, d[0].y, width, height));
		return rectInFourRect;

	}

	public static List<Rect> filter(Set<Rect> rects) {

		List<Rect> data = rects.stream().collect(Collectors.toList());
		Set<Rect> test = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);
		Set<Rect> test2 = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);
		List<Rect> rs = new ArrayList<Rect>();

		for (int i = 0; i < data.size(); i++) {
			test.add(data.get(i));
			for (int j = i + 1; j < data.size(); j++) {
				if ((data.get(i).x == data.get(j).x || Math.abs(data.get(i).x - data.get(j).x) < data.get(i).width * 2)
						&& Math.abs(data.get(i).y - data.get(j).y) > 700) {
					test.add(data.get(j));
				}
			}
		}
//		System.out.println("test: " + test);
//		System.out.println("data: " + data);

		rs = test.stream().collect(Collectors.toList());
//		List<Rect> rs2 = new ArrayList<Rect>();
		double minCheo = Double.MAX_VALUE;
		if (rs.size() > 4) {
			for (int i = 0; i < rs.size(); i++) {
				for (int j = i + 1; j < rs.size(); j++) {
					for (int k = j + 1; k < rs.size(); k++) {
						for (int l = k + 1; l < rs.size(); l++) {
							// lấy ô min
							// lấy ô max
							// ô lơn hơn ô min là ô trên bên phải
							// ô còn lại góc dưới trái
							int square1 = rs.get(i).x + rs.get(i).y;
							int square2 = rs.get(j).x + rs.get(j).y;
							int square3 = rs.get(k).x + rs.get(k).y;
							int square4 = rs.get(l).x + rs.get(l).y;

							int min = Math.min(square1, Math.min(square2, Math.min(square3, square4)));
							int max = Math.max(square1, Math.max(square2, Math.max(square3, square4)));

							List<Rect> frontier = new ArrayList<Rect>();
							frontier.add(rs.get(i));
							frontier.add(rs.get(j));
							frontier.add(rs.get(k));
							frontier.add(rs.get(l));
							Rect topLeft = null, topRight, bottomLeft = null, bottomRight = null;
							for (int m = 0; m < frontier.size(); m++) {
								if (frontier.get(m).x + frontier.get(m).y == min)
									topLeft = frontier.get(m);
								if (frontier.get(m).x + frontier.get(m).y == max)
									bottomRight = frontier.get(m);
							}
							frontier.remove(topLeft);
							frontier.remove(bottomRight);
							int yMax = 0;
							for (int m = 0; m < frontier.size(); m++) {
								if (frontier.get(m).y > yMax) {
									bottomLeft = frontier.get(m);
									yMax = frontier.get(m).y;
								}
							}
							frontier.remove(bottomLeft);

							if (frontier.get(0).x > topLeft.x && frontier.get(0).y < bottomRight.y) {
								topRight = frontier.get(0);
							} else {
								continue;
							}

							double cheo1 = Math.sqrt(
									Math.pow(topRight.x - bottomLeft.x, 2) + Math.pow(topRight.y - bottomLeft.y, 2));
							double cheo2 = Math.sqrt(
									Math.pow(bottomRight.x - topLeft.x, 2) + Math.pow(bottomRight.y - topLeft.y, 2));

							if (Math.abs(Math.abs(cheo1 - cheo2)) < 1000
									&& Math.abs(topLeft.x - bottomLeft.x) < topLeft.width * 2
									&& Math.abs(topLeft.y - topRight.y) < topLeft.height * 2
									&& Math.abs(bottomRight.x - topRight.x) < bottomRight.width * 2
									&& Math.abs(bottomRight.y - bottomLeft.y) < bottomLeft.height * 2) {

								// hiệu 2 đường chéo nhỏ nhất, diện tích phần được chọn lớn hơn 50% diện tích
								// ảnh
								double area = Math.abs(topRight.x - bottomLeft.x) * Math.abs(topRight.y - bottomLeft.y);
								if (Math.abs(cheo1 - cheo2) < minCheo && area > totalArea / 3) {
									minCheo = Math.abs(cheo1 - cheo2);
									System.out.println(cheo1);
									System.out.println(cheo2);
									test2.clear();
									test2.add(rs.get(i));
									test2.add(rs.get(j));
									test2.add(rs.get(k));
									test2.add(rs.get(l));
								}
							}

						}

					}

				}
			}

			System.out.println(test2.stream().collect(Collectors.toList()));
			return test2.stream().collect(Collectors.toList());
		}
//		System.out.println(rs);
		return rs;
	}

}
