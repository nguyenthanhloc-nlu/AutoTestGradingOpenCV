package com.test.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import com.test.grading.GetAnswers;
//import com.test.grading.GetFrame;
import com.test.grading.IdExamFrame;
import com.test.grading.IdStudentFrame;
import com.test.grading.ResltExamFrame;
import com.test.model.Line;
import com.test.model.Paper;
import com.test.model.PositionAndThreshValue;
import com.test.process.Plantain_StudentID;

public class test2 {
	Paper paper = new Paper();
//	Paper2 paper2 = new Paper2();
	IdStudentFrame getIdStudentFrame = new IdStudentFrame();
	IdExamFrame getIdExamFrame = new IdExamFrame();
	ResltExamFrame getResltExamFrame = new ResltExamFrame();
	GetAnswers getAnswers = new GetAnswers();
	List<String> list = new ArrayList<String>();

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		test2 test = new test2();
		test.RunMutiple(408);
	}

	// sau khi test 108 ảnh mới
	// phát hiện ra lỗi sai cắt ảnh vì cắt lẹm khúc trên của phần khung đáp án
	// và cắt lẹm khúc trên của phần khung

	public void RunMutiple(int serial) {
		int countImgErr = 0;
		String listNameImgErr = "";

		for (int i = 1; i <= serial; i++) {
			try {
				String imgSerial = "\nHình: " + i;
				System.out.println(imgSerial);

				String filePath = "src/img/" + i + ".jpg";
				Mat src = Imgcodecs.imread(filePath);

				PositionAndThreshValue positionAndThreshValue = paper.getPositionPaperAndThreshValue(src);

				Mat getFrameRomatedBeforeCrop = paper.getFrameRomatedBeforeCrop(positionAndThreshValue.getListRect(),
						src);

				List<Rect> getPositionPaperAfterRomomate = paper
						.getPositionPaperAfterRomomate(getFrameRomatedBeforeCrop, positionAndThreshValue);

				Mat frameRomatedAfterCrop = paper.getFrameRomatedAfterCrop(getPositionPaperAfterRomomate,
						getFrameRomatedBeforeCrop);

				String getRectIn4Rect = "src/img/getRectIn4Rect-" + i + ".jpg";
				Imgcodecs.imwrite(getRectIn4Rect, frameRomatedAfterCrop);

				try {
					String dapAn = "src/img/rs-dap-an-" + i + ".jpg";
					Mat dapAnMat = getResltExamFrame.getAnswerFrame(frameRomatedAfterCrop);
					Imgcodecs.imwrite(dapAn, dapAnMat);
//					Map<Integer, Line> mapAnswers = getAnswers.getAnswers(dapAnMat);
//					print(mapAnswers);
				} catch (Exception e) {
					System.out.println(e);
				}

				try {
					String maSoSv = "src/img/rs-ma-so-sv-" + i + ".jpg";
					Mat idStudent = getIdStudentFrame.getIDStudentFrame(frameRomatedAfterCrop);
					Imgcodecs.imwrite(maSoSv, idStudent);
//					Plantain_StudentID stu = new Plantain_StudentID(idStudent, true);
//					String MSSV = stu.getCodeID();
//					System.out.println("Mã sinh viên hình " + i + "  : " + MSSV);
				} catch (Exception e) {
					System.out.println(e);
				}

				try {
					String maDe = "src/img/rs-rs-ma-de-" + i + ".jpg";
					Mat idExam = getIdExamFrame.getIDExameFrame(frameRomatedAfterCrop);
					Imgcodecs.imwrite(maDe, idExam);
//					Plantain_StudentID planMaDe = new Plantain_StudentID(idExam, false);
//					String MaDe = planMaDe.getCodeID();
//					System.out.println("Mã đề hình " + i + "  : " + MaDe);
				} catch (Exception e) {
					System.out.println(e);
				}

			} catch (Exception e) {
				countImgErr++;
				String nameImgErr = "\nHình " + i + ".";
				listNameImgErr += nameImgErr;
			}
		}
		System.err.println("\n\nTotal img arr: " + countImgErr);
		System.err.println("\nList img err: " + listNameImgErr);

	}

	public static void print(Map<Integer, Line> ok) {
		for (Entry<Integer, Line> en : ok.entrySet()) {
			System.out.println(en.getKey() + "  " + en.getValue().getValue());
		}
	}
}
