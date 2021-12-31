package com.test.test;

import java.util.Map;
import java.util.Map.Entry;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.test.model.Paper;
import com.test.grading.GetBiggestFrame;
import com.test.grading.GetAnswers;
import com.test.model.Line;

public class Test {

	public Paper paper = new Paper();
	public GetBiggestFrame getBiggestFrame = new GetBiggestFrame();

	public void Run(String filePath) {
		System.out.println("\nHình: ");
		Mat src = Imgcodecs.imread(filePath);
		Mat get_Area = paper.getRectIn4Rect(src);
		Imgcodecs.imwrite("src/img/getRectIn4Rect.jpg", get_Area);
		Imgcodecs.imwrite("src/img/rs-dap-an.jpg", getBiggestFrame.getResultFrame(get_Area));
		Imgcodecs.imwrite("src/img/rs-ma-so-sv.jpg", getBiggestFrame.getIDStudentFrame(get_Area));
		Imgcodecs.imwrite("src/img/rs-ma-de.jpg", getBiggestFrame.getIDExameFrame(get_Area));
		Imgcodecs.imwrite("src/img/rs-ten-sv.jpg", getBiggestFrame.getNameFrame(get_Area));
	}

	public void RunMutiple(int serial) {

		for (int i = 1; i <= serial; i++) {

			String imgSerial = "\nHình: " + i;
			String filePath = "src/img/" + i + ".jpg";
			String getRectIn4Rect = "src/img/getRectIn4Rect-" + i + ".jpg";
			String dapAn = "src/img/rs-dap-an-" + i + ".jpg";
			String maSoSv = "src/img/rs-ma-so-sv-" + i + ".jpg";
			String maDe = "src/img/rs-ma-de-" + i + ".jpg";
			String tenSv = "src/img/rs-ten-sv-" + i + ".jpg";

			System.out.println(imgSerial);
			Mat src = Imgcodecs.imread(filePath);
			Mat get_Area = paper.getRectIn4Rect(src);
			Imgcodecs.imwrite(getRectIn4Rect, get_Area);
			Imgcodecs.imwrite(dapAn, getBiggestFrame.getResultFrame(get_Area));
			Imgcodecs.imwrite(maSoSv, getBiggestFrame.getIDStudentFrame(get_Area));
			Imgcodecs.imwrite(maDe, getBiggestFrame.getIDExameFrame(get_Area));
			Imgcodecs.imwrite(tenSv, getBiggestFrame.getNameFrame(get_Area));
		}
	}

	public static void print(Map<Integer, Line> ok) {
		for (Entry<Integer, Line> en : ok.entrySet()) {
			System.out.println(en.getKey() + "  " + en.getValue().getValue());
		}
	}

	public static void main(String[] args) {

		Test test = new Test();
		test.RunMutiple(20);

//		String filePath = "src/dapan4.jpg";
//
//		GetAnswers getAns = new GetAnswers();
//		Mat src = Imgcodecs.imread(filePath);
//		Map<Integer, Line> ok = getAns.getAnswers(src);
//
//		print(ok);

	}

}
