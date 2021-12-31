package com.test.grading;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.test.process.RectCompareNoise;

public class GetBiggestFrame {
	public static final double X_ID_STUDENT_PERCENT = 0.5576;
	public static final double Y_ID_STUDENT_PERCENT = 0.0132;
	public static final double W_ID_STUDENT_PERCENT = 0.2403;
	public static final double H_ID_STUDENT_PERCENT = 0.2715;

	public static final double X_NAME_STUDENT_PERCENT = 0.25;
	public static final double Y_NAME_STUDENT_PERCENT = 0.0132;
	public static final double W_NAME_STUDENT_PERCENT = 0.3029;
	public static final double H_NAME_STUDENT_PERCENT = 0.2715;

	public static final double X_MADETHI_STUDENT_PERCENT = 0.8077;
	public static final double Y_MADETHI_STUDENT_PERCENT = 0.0132;
	public static final double W_MADETHI_STUDENT_PERCENT = 0.1539;
	public static final double H_MADETHI_STUDENT_PERCENT = 0.2715;

	public static final double X_DAP_AN_PERCENT = 0.0227;
	public static final double Y_DAP_AN_PERCENT = 0.4755;
	public static final double W_DAP_AN_PERCENT = 0.9704;
	public static final double H_DAP_AN_PERCENT = 0.5245;

	public GetBiggestFrame() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public List<MatOfPoint> getContour(Mat src) {
		Mat cannyOutput = new Mat();
		Imgproc.Canny(src, cannyOutput, 10, 100); // 80 200
		List<MatOfPoint> contours = new ArrayList<>();// RETR_TREE
		Mat hierarchy = new Mat();
		Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		hierarchy.release();
		cannyOutput.release();
		return contours;
	}

	public Mat getAreaInner4Square(Mat src) {
		Mat start = new Mat();
		Imgproc.resize(src, start, new Size(1240, 1755));
		Mat thresh = preProcessForAngleDetection(start);
//	Imgcodecs.imwrite("src/img/binary.jpg", thresh);
		List<MatOfPoint> contours = getContour(thresh);

		// lọc phần tử gây nhiễu
		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);
		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			// để có thể lấy ra tọa độ (xmin, ymin) cũng như chiều dài và rộng của hình chữ
			// nhật bao quanh contour.

			if (rect.area() > 1200 && rect.area() < 2500 && Math.abs(rect.width - rect.height) < 8) {

//			System.out.println("rect " + rect);
				if ((rect.y < 250 || rect.y > 1400) && (rect.x < 300 || rect.x > 900) && rect.width < 50
						&& rect.height < 50)

					rects.add(rect);
			}
		}
		ArrayList<Rect> li = new ArrayList<Rect>();
		for (Rect r : rects) {
			li.add(r);
		}

//	System.out.println("List rect: " + li.size() + " : " + li);
		Mat rectInFourRect = new Mat();
		for (int i = 1; i < li.size(); i++) {
			if (Math.abs(li.get(i).x - li.get(0).x) > 800 && Math.abs(li.get(i).y - li.get(0).y) > 800) {
				int width = Math.abs(li.get(i).x - li.get(0).x);
				int height = Math.abs(li.get(i).y - li.get(0).y);

				if (li.get(0).x < 500) {

					rectInFourRect = new Mat(start, new Rect(li.get(0).x, li.get(0).y, width, height));
				} else if (li.get(0).x > 500) {
					rectInFourRect = new Mat(start, new Rect(li.get(0).x, li.get(0).y - height, width, height));
				}
			}
		}

		return rectInFourRect;
	}

// Mat tableIdExam, Mat tableResult
	public Mat getNameFrame(Mat start) {
		int xIdStudent = (int) (X_NAME_STUDENT_PERCENT * start.cols());
		int yIdStudent = (int) (Y_NAME_STUDENT_PERCENT * start.rows());
		int wIdStudent = (int) (W_NAME_STUDENT_PERCENT * start.cols());
		int hIdStudent = (int) (H_NAME_STUDENT_PERCENT * start.rows());
		Mat result = new Mat(start, new Rect(xIdStudent, yIdStudent, wIdStudent, hIdStudent));
		return result;
	}

	public Mat getIDStudentFrame(Mat start) {
		int xIdStudent = (int) (X_ID_STUDENT_PERCENT * start.cols());
		int yIdStudent = (int) (Y_ID_STUDENT_PERCENT * start.rows());
		int wIdStudent = (int) (W_ID_STUDENT_PERCENT * start.cols());
		int hIdStudent = (int) (H_ID_STUDENT_PERCENT * start.rows());
		Mat result = new Mat(start, new Rect(xIdStudent, yIdStudent, wIdStudent, hIdStudent));
		return result;
	}

	public Mat getIDExameFrame(Mat start) {
		int xIdStudent = (int) (X_MADETHI_STUDENT_PERCENT * start.cols());
		int yIdStudent = (int) (Y_MADETHI_STUDENT_PERCENT * start.rows());
		int wIdStudent = (int) (W_MADETHI_STUDENT_PERCENT * start.cols());
		int hIdStudent = (int) (H_MADETHI_STUDENT_PERCENT * start.rows());
		Mat result = new Mat(start, new Rect(xIdStudent, yIdStudent, wIdStudent, hIdStudent));
		return result;
	}

	public Mat getResultFrame(Mat src) {
		int x = (int) (X_DAP_AN_PERCENT * src.cols());
		int y = (int) (Y_DAP_AN_PERCENT * src.rows());
		int w = (int) (W_DAP_AN_PERCENT * src.cols());
		int h = (int) (H_DAP_AN_PERCENT * src.rows());
		Mat result = new Mat(src, new Rect(x + 50, y, w - 100, h));
		return result;
	}

	public Mat straightenAndRomateImg(Mat src) {
		Mat start = new Mat();
		Imgproc.resize(src, start, new Size(1240, 1755));
		Mat straightImage = straightenImage(start);
		return straightImage;
	}

	public Mat preProcessForAngleDetection(Mat image) {
		Mat binary = new Mat();
		// Create binary image
		Imgproc.threshold(image, binary, 80, 255, Imgproc.THRESH_BINARY_INV);
		// "Connect" the letters and words
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 1));
		Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_CLOSE, kernel);
		// Convert the image to gray from RGB
		Imgproc.cvtColor(binary, binary, Imgproc.COLOR_BGR2GRAY);
//	Imgcodecs.imwrite("src/img/processedImage.jpg", binary);
		return binary;
	}

// With this we can detect the rotation angle
// After this function returns we will know the necessary angle
	public double detectRotationAngle(Mat binaryImage) {
		// Store line detections here
		Mat lines = new Mat();
		// Detect lines phát hiện đường thẳng
		Imgproc.HoughLinesP(binaryImage, lines, 1, Math.PI / 180, 100);
		double angle = 0;

		// This is only for debugging and to visualise the process of the straightening
		Mat debugImage = binaryImage.clone();
		Imgproc.cvtColor(debugImage, debugImage, Imgproc.COLOR_GRAY2BGR);

		// Calculate the start and end point and the angle

//	System.out.println("\nlines.cols(): " + lines.cols() + "\n");
		for (int x = 0; x < lines.cols(); x++) {
			double[] vec = lines.get(0, x);

			double x1 = vec[0];
			double y1 = vec[1];
			double x2 = vec[2];
			double y2 = vec[3];

			Point start = new Point(x1, y1);
			Point end = new Point(x2, y2);

			// Draw line on the "debug" image for visualization
			Imgproc.line(debugImage, start, end, new Scalar(255, 255, 0), 5);

			// Calculate the angle we need
			angle = calculateAngleFromPoints(start, end);

		}
//	Imgcodecs.imwrite("src/img/detectedLines.jpg", debugImage);

		System.out.println("Gốc xoay ảnh: " + angle);
		return angle;

	}

// From an end point and from a start point we can calculate the angle
	private double calculateAngleFromPoints(Point start, Point end) {
		double deltaX = end.x - start.x;
		double deltaY = end.y - start.y;
		double rs = Math.atan2(deltaY, deltaX) * (180 / Math.PI);
		if (rs > 80 || rs < -80) {
			return 0;
		}
		return rs;
	}

// Rotation is done here
	private Mat rotateImage(Mat image, double angle) {
		// Calculate image center
		Point imgCenter = new Point(image.cols() / 2, image.rows() / 2);
		// Get the rotation matrix
		Mat rotMtx = Imgproc.getRotationMatrix2D(imgCenter, angle, 1.0);
		// Calculate the bounding box for the new image after the rotation (without this
		// it would be cropped)
		Rect bbox = new RotatedRect(imgCenter, image.size(), angle).boundingRect();

		// Rotate the image
		Mat rotatedImage = image.clone();
		Imgproc.warpAffine(image, rotatedImage, rotMtx, bbox.size());

		return rotatedImage;
	}

// Sums the whole process and returns with the straight image
	private Mat straightenImage(Mat image) {
		Mat rotatedImage = image.clone();
		Mat processed = preProcessForAngleDetection(image);
		double rotationAngle = detectRotationAngle(processed);

		return rotateImage(rotatedImage, rotationAngle);
	}
}
