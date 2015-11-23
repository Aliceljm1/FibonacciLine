package com.example.androidpicture;

import android.graphics.Point;

/**
 * @author 刘泾铭
 * @version 创建时间：2015-11-23 上午10:23:31 
 * 正方形模型
 */
public class FB {

	// 点坐标
	private int left;
	private int top;
	private int right;
	private int bottom;

	private int v;// 正方形宽度
	private int rang;// 弧度所处的象限‘

	private int angleA;
	private int angleB;

	public int getBLeft() {
		switch (this.getRang()) {
		case 1:
			return left - getV();
		case 2:
			return left;
		case 3:
			return left;
		case 4:
			return left - getV();
		default:
			return left;
		}
	}

	public int getBTop() {
		switch (this.getRang()) {
		case 1:
			return top;
		case 2:
			return top;
		case 3:
			return top - getV();
		case 4:
			return top - getV();
		default:
			return top;
		}
	}

	public int getBRight() {
		switch (this.getRang()) {
		case 1:
			return right;
		case 2:
			return right + getV();
		case 3:
			return right + getV();
		case 4:
			return right;

		default:
			return right;
		}
	}

	public int getBBottom() {
		switch (this.getRang()) {
		case 1:
			return bottom + getV();
		case 2:
			return bottom + getV();
		case 3:
			return bottom;
		case 4:
			return bottom;
		default:
			return bottom;
		}
	}

	public void setPoint(int l, int t, int r, int b) {
		left = l;
		top = t;
		right = r;
		bottom = b;
	}

	public void setAngle(int start, int end) {
		angleA = start;
		angleB = end;
	}

	public Point getP1() {
		// TODO 自动生成的方法存根
		return new Point(left, top);
	}

	public Point getP2() {
		return new Point(right, top);
	}

	public Point getP3() {
		return new Point(left, bottom);
	}

	public Point getP4() {
		return new Point(right, bottom);
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public int getStartAngle() {
		return angleA;
	}

	public void setAngleA(int angleA) {
		this.angleA = angleA;
	}

	public int getAngleB() {
		return angleB;
	}

	public void setEndAngle(int angleb) {
		this.angleB = angleb;
	}

	public void setV(int i) {
		v = i;
	}

	public int getV() {
		return v;
	}

	public void setRang(int i) {
		// TODO 自动生成的方法存根
		rang = i;
	}

	public int getRang() {
		return rang;
	}

}
