package com.test.grading;

import java.io.File;

public class FF {
	public static void main(String[] args) {
		for (int i = 60; i < 121; i++) {
			File f = new File("D:\\anh\\hinh_mo_ta_san_pham\\"+i);
			f.mkdirs();
		}
	}

}
