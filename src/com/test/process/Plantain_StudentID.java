package com.test.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Plantain_StudentID {
	private String pathImg;
	private int wBoxP = 150; // width của box mã đề
	private int wBoxStuID = 240;// width của box mssv
	private int wPlan = 120;// width của mã đề
	private int wStuID = 210;// width của mssv
	private int wBoxPlan_StuID; // width khung khi chưa cắt 2 ô vuông đen --biến thay đổi
	private int wPlan_StuID;// width khung sau khi cắt bỏ 2 ô vuông đen --biến thay đổi
	private static final int hBoxP_StuID = 400;// heigth của khung khi chưa cắt 2 ô vuông đen--biến khong đổi
	private static final int hPlan_StuID = 330;// heigth của khung khi cắt bỏ 2 ô vuông đen--biến khong đổi
	private boolean StuID = true; // mã số sinh viên
	private boolean Plan = false;// mã đề
	private boolean StuID_Plantain;

	public Plantain_StudentID(String pathImg, boolean StuID_Plantain) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.pathImg = pathImg;
		this.StuID_Plantain = StuID_Plantain;
		this.check();
	}

	// kiểm tra là mã đề hay mssv
	public void check() {
		if (StuID_Plantain == StuID) {
			this.wBoxPlan_StuID = wBoxStuID;
			this.wPlan_StuID = wStuID;
		} else if (StuID_Plantain == Plan) {
			this.wBoxPlan_StuID = wBoxP;
			this.wPlan_StuID = wPlan;
		}
	}

	// images gray
	public Mat imgGray() {
		Mat src = Imgcodecs.imread(pathImg);
		Mat dstImg = new Mat();
		Imgproc.cvtColor(src, dstImg, Imgproc.COLOR_BGR2GRAY);
		Mat dstGray = new Mat(dstImg.rows(), dstImg.cols(), dstImg.type());
		Imgproc.GaussianBlur(dstImg, dstGray, new Size(3, 3), 0);
		return dstGray;
	}

	// threshold resize
	public Mat threshold() {
		Mat imgGrayResize = reSize(imgGray(), wBoxPlan_StuID, hBoxP_StuID);
		Mat dstThres = new Mat(imgGrayResize.rows(), imgGrayResize.cols(), imgGrayResize.type(), new Scalar(0));
		Imgproc.adaptiveThreshold(imgGrayResize, dstThres, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
				Imgproc.THRESH_BINARY_INV, 31, 5);
		return dstThres;
	}

	// crop lấy ra ô đen mã đề or MSSV
	public Mat imgPlan_StuID(Mat boxImgPlan_stuID) {
		Set<Rect> listRect = getTwoNumber(boxImgPlan_stuID);
		Rect[] arrR = new Rect[2];
		listRect.toArray(arrR);
		Rect rectOn = arrR[0];
		Rect rectBottom = arrR[1];

		int xP_S = rectOn.x + rectOn.width;
		int yP_S = rectOn.y + rectOn.height;
		int wP_S = Math.abs(wBoxPlan_StuID - xP_S);
		int hP_S = Math.abs(rectBottom.y - yP_S);
//		System.out.println(xP_S + " " + yP_S + " " + wP_S + " " + hP_S);
		return cropImgResult(xP_S, yP_S, wP_S, hP_S);

	}

	// lấy vị trí 2 ô vuông định vị mã đề or MSSV
	public Set<Rect> getTwoNumber(Mat src) {
		List<MatOfPoint> contours = getListContours(src);
		// lọc phần tử gây nhiễu
		Set<Rect> rectTree = new TreeSet<Rect>(RectComparePlan.RECT_COMPARE);
		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
//			System.err.println(rect);
			if (rect.area() > 280 && rect.area() < 600) {
				if (((rect.y > 5 && rect.y < 50) || (rect.y > 290 && rect.y < hBoxP_StuID)) && (rect.x < 38)
						&& (rect.width > 15 && rect.width < 25) && (rect.height > 15 && rect.height < 25)) {
					rectTree.add(rect);
				}
			}
		}
//		System.err.println(rectTree);
		return rectTree;
	}

	// lấy ra mã đề or MSSV
	public String code() {
		List<MatOfPoint> contours = getListContours(imgPlan_StuID(threshold()));
		Set<Rect> rectTree = new TreeSet<Rect>(SortPlantainY.SORT_PLANTAIN);
		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
//			System.out.println(rect);
			if (rect.area() > 250 && rect.area() < 530) {
				if ((rect.y > 5 && rect.y < hBoxP_StuID) && (rect.x > 0 && rect.x < wPlan_StuID)
						&& (rect.width > 13 && rect.width < 27) && (rect.height > 13 && rect.height < 27)) {
					rectTree.add(rect);
				}
			}
		}
		String res = "";
		for (Rect rect : rectTree) {
			res += getnumberRow(rect, rectTree.size());
		}
//		System.err.println(rectTree);
		return res;
	}

	// lấy ra số từng cột trong mã đề or MSSV
	public String getnumberRow(Rect rect, int size) {
		String res = "";
		int count = 9;
		int coordinates = 330;
		while (count != -1) {
			if (rect.y < coordinates && rect.y > coordinates - 35) {
				res = String.valueOf(count);
				break;
			} else {
				count--;
				coordinates -= 35;
				continue;
			}
		}
		return res;
	}

	// lấy ra list các contours
	public List<MatOfPoint> getListContours(Mat src) {
		Mat cannyOutput = new Mat();
		Imgproc.Canny(src, cannyOutput, 10, 100);
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		return contours;

	}

	// crop lấy kết quả của các ô tô đen
	public Mat cropImgResult(int xP_S, int yP_S, int wP_S, int hP_S) {
		Mat mat = new Mat(threshold(), new Rect(xP_S, yP_S, wP_S, hP_S));
		return reSize(mat, wPlan_StuID, hPlan_StuID);
	}

	// thay đổi kích thước ảnh
	public Mat reSize(Mat mat, int w, int h) {
		Mat destMat = new Mat();
		Imgproc.resize(mat, destMat, new Size(w, h), 0, 0, Imgproc.INTER_AREA);
		return destMat;
	}

}
