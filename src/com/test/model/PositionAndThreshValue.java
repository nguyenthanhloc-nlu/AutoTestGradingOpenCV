package com.test.model;

import java.util.List;

import org.opencv.core.Rect;

public class PositionAndThreshValue {
	private List<Rect> listRect;
	private int threshValue;

	public PositionAndThreshValue(List<Rect> listRect, int threshValue) {
		super();
		this.listRect = listRect;
		this.threshValue = threshValue;
	}

	public List<Rect> getListRect() {
		return listRect;
	}

	public void setListRect(List<Rect> listRect) {
		this.listRect = listRect;
	}

	public int getThreshValue() {
		return threshValue;
	}

	public void setThreshValue(int threshValue) {
		this.threshValue = threshValue;
	}

}
