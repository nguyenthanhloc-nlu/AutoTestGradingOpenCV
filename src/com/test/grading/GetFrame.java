package com.test.grading;

import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import com.test.model.Paper;
import com.test.model.PositionAndThreshValue;

public class GetFrame {

	Paper paper = new Paper();
	IdExamFrame getIdExamFrame = new IdExamFrame();
	IdStudentFrame getIdStudentFrame = new IdStudentFrame();
	ResltExamFrame getResltExamFrame = new ResltExamFrame();
//	GetAnswers getAnswers = new GetAnswers();

	public void execute(Mat src, Mat idExam, Mat idStudent, Mat resultExam) {

		final long startTime = System.currentTimeMillis();
		System.out.println("\nGetFrame: execute");

		PositionAndThreshValue positionAndThreshValue = paper.getPositionPaperAndThreshValue(src);

		Mat getFrameRomatedBeforeCrop = paper.getFrameRomatedBeforeCrop(positionAndThreshValue.getListRect(), src);

		List<Rect> getPositionPaperAfterRomomate = paper.getPositionPaperAfterRomomate(getFrameRomatedBeforeCrop,
				positionAndThreshValue);

		Mat frameRomatedAfterCrop = paper.getFrameRomatedAfterCrop(getPositionPaperAfterRomomate,
				getFrameRomatedBeforeCrop);

		Imgcodecs.imwrite("src/img/frameRomatedAfterCrop.jpg", frameRomatedAfterCrop);

		getResltExamFrame.getAnswerFrame(frameRomatedAfterCrop).copyTo(resultExam);
		Imgcodecs.imwrite("src/img/rs-dap-an.jpg", resultExam);

		getIdStudentFrame.getIDStudentFrame(frameRomatedAfterCrop).copyTo(idStudent);
		Imgcodecs.imwrite("src/img/rs-ma-so-sv.jpg", idStudent);

		getIdExamFrame.getIDExameFrame(frameRomatedAfterCrop).copyTo(idExam);
		Imgcodecs.imwrite("src/img/rs-rs-ma-de.jpg", idExam);

		final long endTime = System.currentTimeMillis();
		System.err.println("Time execute: " + (endTime - startTime));
	}

}
