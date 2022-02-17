package com.test.process;

import java.util.List;

import org.opencv.core.Rect;

public class PositonSquare {

	public Rect findRectTopLeft(List<Rect> rects) {

//		System.out.println("PositonSquare: " );

		int minValue = Integer.MAX_VALUE;
		Rect min = null;
		for (Rect rect : rects) {
			if (rect.x + rect.y < minValue) {
				min = rect;
				minValue = rect.x + rect.y;
			}
		}
		return min;
	}

	public Rect findRectBottomRight(List<Rect> rects) {
		int maxValue = Integer.MIN_VALUE;
		Rect max = null;
		for (Rect rect : rects) {
			if (rect.x + rect.y > maxValue) {
				max = rect;
				maxValue = rect.x + rect.y;
			}
		}
		return max;
	}

	public Rect findRectTopRight(List<Rect> rects) {
		int maxValue = Integer.MIN_VALUE;
		Rect max = null;
		for (Rect rect : rects) {
			if (rect.x > maxValue) {
				max = rect;
				maxValue = rect.x;
			}
		}
		return max;
	}

	public Rect findRectBottomLeft(List<Rect> rects) {
		int minValue = Integer.MAX_VALUE;
		Rect min = null;
		for (Rect rect : rects) {
			if (rect.x < minValue) {
				min = rect;
				minValue = rect.x;
			}
		}
		return min;
	}

	public Rect findRectBottomCentral(List<Rect> rects) {
		int minValue = Integer.MIN_VALUE;
		Rect min = null;
		for (Rect rect : rects) {
			if (rect.y > minValue) {
				min = rect;
				minValue = rect.y;
			}
		}
		return min;
	}

	public Rect findRectTopCentral(List<Rect> rects) {
		int minValue = Integer.MAX_VALUE;
		Rect min = null;
		for (Rect rect : rects) {
			if (rect.y < minValue) {
				min = rect;
				minValue = rect.y;
			}
		}
		return min;
	}

}
