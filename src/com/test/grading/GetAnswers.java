package com.test.grading;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.test.model.Line;
import com.test.process.MatProcess;
import com.test.process.RectCompareNoise;
import com.test.process.SortMatOfPoint;

public class GetAnswers {
	public static final int W_SQUARE_MAX = 30;
	public static final int H_SQUARE_MAX = 30;

	public static final int MARGIN = 21;

	public static final int W_ANSWER = 21;
	public static final int H_ANSWER = 21;

	public static final int SPACE_ANSWER = 18;

	public static final int UPPER_BOUND_Y = 715;
	public static final int LOWER_BOUND_Y = 70;

	public static final int UPPER_BOUND_X = 600;
	public static final int LOWER_BOUND_X = 30;

	public GetAnswers() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/*
	 * 6 Ă´ vuĂ´ng Ä‘á»‹nh vá»‹ Ä‘Ă¡p Ă¡n
	 */
	public Set<Rect> getPositionAnsewer(Mat src) {
		// láº¥y contour
		// láº¥y vá»‹ trĂ­ 6 Ă´ vuĂ´ng
		Mat gray = MatProcess.toColorGray(src);
		Mat thresh = MatProcess.toThreshBinary(gray, 100);
		List<MatOfPoint> contours = MatProcess.getContour(thresh);

		// lá»�c pháº§n tá»­ gĂ¢y nhiá»…u
		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);

		// 80, 670
		// xĂ¡c Ä‘á»‹nh 6 tá»�a Ä‘á»™ 6 Ă´ Ä‘en, láº¥y cá»™t Ä‘Ă¡n Ă¡n
		// ctdl Set khĂ´ng chá»©a pháº§n tá»­ trĂ¹ng, dĂ¹ng Ä‘á»ƒ remove nhiá»…u

		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (rect.area() > 200 && rect.area() < 500) {
				Imgproc.rectangle(src, rect, new Scalar(0, 255, 0));
				if ((rect.y < LOWER_BOUND_Y || rect.y > UPPER_BOUND_Y)
						&& (rect.x < UPPER_BOUND_X && rect.x > LOWER_BOUND_X) && rect.width < W_SQUARE_MAX
						&& rect.height < H_SQUARE_MAX)
					rects.add(rect);
			}
		}
		Imgcodecs.imwrite("result_test7.jpg", src);
		return rects;
	}

	public void getColumnAnswer(Mat table, Mat col_1, Mat col_2, Mat col_3) {

		Set<Rect> r = getPositionAnsewer(table);
		if (r.size() == 6) {
			Rect[] d = new Rect[6];
			r.toArray(d);

			// width, height column answer
			int width = d[2].x - d[0].x - d[0].width;
			int height = d[1].y - d[0].y - d[0].height;

			// rotate
			Point p1 = new Point(d[0].x, d[0].y);
			Point p2 = new Point(d[1].x, d[1].y);
			int iX = (d[5].x - d[0].x) / 2 + d[0].x;
			int iY = (d[1].y - d[0].y) / 2 + d[0].y;
			Mat table2 = MatProcess.rotate(table, MatProcess.computeAngleRotate(p1, p2, iX, iY));

			// column 1
			// x = x1, y = y2, w = x3 - x1, h = y2 - y1
			int xAnswer1 = d[0].x + d[0].width;
			int yAnswer1 = d[0].y + d[0].height;
			Mat col1 = new Mat(table2, new Rect(xAnswer1, yAnswer1, width, height));
			col1.copyTo(col_1);

			// column 2
			// x = x3, y = y3, w = x5 - x3, h = y4 - y3
			p1 = new Point(d[2].x, d[2].y);
			p2 = new Point(d[3].x, d[3].y);
			int xAnswer2 = d[2].x + d[2].width;
			int yAnswer2 = d[2].y + d[2].height;
			table2 = MatProcess.rotate(table, MatProcess.computeAngleRotate(p1, p2, iX, iY));
			Mat col2 = new Mat(table2, new Rect(xAnswer2, yAnswer2, width, height));
			col2.copyTo(col_2);

			// column 3
			// x = x5, y = y5,
			p1 = new Point(d[4].x, d[4].y);
			p2 = new Point(d[5].x, d[5].y);
			int xAnswer3 = d[4].x + d[4].width;
			int yAnswer3 = d[4].y + d[4].height;
			table2 = MatProcess.rotate(table, MatProcess.computeAngleRotate(p1, p2, iX, iY));
			Mat col3 = new Mat(table2, new Rect(xAnswer3, yAnswer3, width, height));
			col3.copyTo(col_3);
		}

	}

	//// 1 Ă´ 22*22
	// line 18
	public Map<Integer, Line> getRowAnswer(Mat colAnswer, int start) {
		Map<Integer, Line> result = new HashMap<Integer, Line>();

		Mat gray = MatProcess.toColorGray(colAnswer);
		Mat thresh = new Mat();
		thresh = MatProcess.toThreshBinary(gray, 120);

		List<MatOfPoint> contours = MatProcess.getContour(thresh);
		Imgcodecs.imwrite("result_test4.jpg", thresh);
		// remove contour cĂ³ area() < 250
		removeContourHasAreaLess_N(contours, 250);

		// sort theo y, duyá»‡t tá»« trĂªn xuá»‘ng
		Collections.sort(contours, SortMatOfPoint.MAT_OF_POINT_COMPARE_BY_Y);

		if (contours.isEmpty())
			return result;

		int lineCurrent = 0;
		double lineHeight = (double) (colAnswer.height()) / 17;

		while (lineCurrent < 17 && contours.size() > 0) {
			lineCurrent++;
			// láº¥y ra contour cĂ³ y min lĂ  Ă´ Ä‘Ă¡p Ă¡n tiáº¿p theo
			Rect min = Imgproc.boundingRect(contours.get(0));

			// nhá»¯ng dĂ²ng khoanh lá»›n hÆ¡n 2 Ă´, bá»� qua
			boolean check = rowHasTwoAnswer(contours, min);

			// remove nhiá»…u theo y, láº¥y sai sá»‘ 21 - height of bounding answer
			removeNoiseAroundAnswer(contours, min.y);
			if (check)
				continue;

			int line = (int) ((min.y) / lineHeight + start + 1);
			addAnswer(result, line, min);
		}
		return result;
	}

	public void removeContourHasAreaLess_N(List<MatOfPoint> contours, int N) {
		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (rect.area() < N)
				contours.remove(i--);
		}
	}

	public void removeNoiseAroundAnswer(List<MatOfPoint> contours, int y) {
		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (Math.abs(y - rect.y) < 21)// +21 sai sá»‘
				contours.remove(i--);
		}
	}

	public boolean rowHasTwoAnswer(List<MatOfPoint> contours, Rect answer) {
		for (int i = 1; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (Math.abs(answer.x - rect.x) > 21 && Math.abs(answer.y - rect.y) < 21) { // +21 sai sá»‘
				return true;
			}
		}
		return false;
	}

	public String converNumToTextAns(int n) {
		if (n == 3)
			return "D";
		if (n == 2)
			return "C";
		if (n == 1)
			return "B";
		return "A";
	}

	public void addAnswer(Map<Integer, Line> output, int line, Rect rectAns) {
		int x = rectAns.x;
<<<<<<< HEAD
		// 21 là ô
		// khoảng cách ô
		for (int i = 3; i >=0; i--) {
			if (MARGIN + SPACE_ANSWER * (i - 1) + W_ANSWER * i < x) {
=======
		// 21 lĂ  Ă´
		// khoáº£ng cĂ¡ch Ă´
		for (int i = 3; i >= 0; i--) {
			if (21 + SPACE_ANSWER * (i - 1) + W_ANSWER * i < x) {
>>>>>>> b62e3f2b7b6971328c791f1397f2850ec994e011
				output.put(line, new Line(line, converNumToTextAns(i)));
				break;
			}
		}
	}

	public Map<Integer, Line> getAnswers(Mat src) {

		Mat table2 = new Mat();
		Imgproc.resize(src, table2, new Size(800, 800));

		Mat col1 = new Mat();
		Mat col2 = new Mat();
		Mat col3 = new Mat();
		getColumnAnswer(table2, col1, col2, col3);
		Imgcodecs.imwrite("result_test3.jpg", table2);

		Map<Integer, Line> result = new HashMap<Integer, Line>();
		result.putAll(getRowAnswer(col1, 0));
		result.putAll(getRowAnswer(col2, 17));
		result.putAll(getRowAnswer(col3, 34));

		return result;
	}

}
