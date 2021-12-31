package com.test.test;

import java.util.Map;
import java.util.Map.Entry;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.test.grading.GetAnswers;
import com.test.model.Line;


public class Test {
	public static void main(String[] args) {

		String filePath = "src/dapan4.jpg";

		GetAnswers getAns = new GetAnswers();
		Mat src = Imgcodecs.imread(filePath);
		Map<Integer, Line> ok = getAns.getAnswers(src);
		
		print(ok);

	}
	
	public static void print(Map<Integer, Line> ok) {
		for (Entry<Integer, Line> en : ok.entrySet()) {
			System.out.println(en.getKey() + "  " + en.getValue().getValue());
		}
	}
}
