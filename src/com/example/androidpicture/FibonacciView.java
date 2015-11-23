package com.example.androidpicture;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.RectF;
import android.text.style.BulletSpan;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author 刘泾铭
 * @version 创建时间：2015-11-2 下午7:05:06 类说明
 */

public class FibonacciView extends SurfaceView implements Runnable, Callback {

	private SurfaceHolder mHolder; // 用于控制SurfaceView

	private Canvas mCanvas; // 声明一张画布

	private Paint mPaint, qPaint; // 声明两只画笔

	private Path mPath, qPath, tPath; // 声明三条路径

	private static int mX, mY; // 用于控制图形的坐标

	// 分别 代表贝塞尔曲线的开始坐标,结束坐标,控制点坐标
	private int qStartX, qStartY, qEndX, qEndY, qControlX, qCOntrolY;

	private int screenW, screenH; // 用于屏幕的宽高

	private Thread mThread; // 声明一个线程

	// flag用于线程的标识,xReturn用于标识图形坐标是否返回,cReturn用于标识贝塞尔曲线的控制点坐标是否返回
	private boolean flag, xReturn, cReturn;
	private HashMap<Integer, FB> fbMap;
	private static int ARC_COUNT = 10;
	private static int V_ONE = 10;
	

	/**
	 * 构造函数，主要对一些对象初始化
	 */
	@SuppressLint("UseSparseArrays")
	public FibonacciView(Context context) {
		super(context);

		mHolder = this.getHolder(); // 获得SurfaceHolder对象
		mHolder.addCallback(this); // 添加状态监听

		mPaint = new Paint(); // 创建一个画笔对象
		mPaint.setColor(Color.WHITE); // 设置画笔的颜色为白色
		mPaint.setStyle(Paint.Style.STROKE);
		// 创建路径对象
		mPath = new Path();

		mX = 500;
		mY = 250;
		int levelAngle = 180;
		fbMap = new HashMap<Integer, FB>();
		FB fb1 = new FB();
		fb1.setV(V_ONE);
		fb1.setRang(3);
		fb1.setAngle(levelAngle + 0, levelAngle - 90);
		fb1.setPoint(mX, mY, mX + fb1.getV(), mY + fb1.getV());
		fbMap.put(1, fb1);

		FB fb2 = new FB();
		fb2.setV(V_ONE);
		fb2.setRang(fb1.getRang() + 1);
		fb2.setAngle(fb1.getAngleB(), fb1.getAngleB() - 90);
		fb2.setPoint(mX + fb2.getV(), mY, mX + fb2.getV() + fb1.getV(), mY
				+ fb1.getV());
		fbMap.put(2, fb2);
		for (int i = 3; i < ARC_COUNT; i++) {
			FB fb = new FB();

			FB fb_1 = fbMap.get(i - 1);
			FB fb_2 = fbMap.get(i - 2);
			int length = fb_1.getV() + fb_2.getV();
			fb.setV(length);
			fb.setRang((fb_1.getRang() + 1) % 4);
			if (fb.getRang() == 0)
				fb.setRang(4);
			fb.setAngle(fb_1.getAngleB() % 360, (fb_1.getAngleB() - 90) % 360);

			switch (fb.getRang()) {
			case 1:
				Point p2 = fb_1.getP2();// 上一个矩形的2号坐标
				fb.setPoint(p2.x - length, p2.y - length, p2.x, p2.y);
				break;
			case 2:
				Point p1 = fb_1.getP1();
				fb.setPoint(p1.x - length, p1.y, p1.x, p1.y + length);
				break;
			case 3:
				Point p3 = fb_1.getP3();
				fb.setPoint(p3.x, p3.y, p3.x + length, p3.y + length);
				break;
			case 4:
				Point p4 = fb_1.getP4();
				fb.setPoint(p4.x, p4.y - length, p4.x + length, p4.y);
				break;

			default:
				break;
			}

			fbMap.put(i, fb);
		}

		setFocusable(true); // 设置焦点

	}

	public void drawDoor(Canvas mCanvas) {
		
		for (int i = 1; i < ARC_COUNT; i++) {
			FB fb = fbMap.get(i);
			RectF mArc = new RectF(fb.getBLeft(), fb.getBTop(), fb.getBRight(),
					fb.getBBottom());
			//mCanvas.drawRect(mArc, mPaint);
			mCanvas.drawArc(mArc, fb.getAngleB(), 90, false, mPaint);// 顺时针绘图
		}

		// RectF mArc = new RectF(mX, mY, mX+100,mY+100);
		// mPaint.setStyle(Paint.Style.STROKE);
		// mCanvas.drawArc(mArc, 0, 90, false, mPaint);

		
//		Path path = new Path();  
//		RectF rect =  new RectF(100, 100, 300, 300);  
//		path.addArc(rect, 0, 360);  
//		 mCanvas.drawRect(rect, mPaint); 
//		mCanvas.drawPath(path, mPaint);//画出200*200 正方形内的 一个内切圆  
		
		
		
	}

	/**
	 * 线程运行的方法,当线程start后执行
	 */
	@Override
	public void run() {

		while (flag) {
			mDraw(); // 调用自定义的绘图方法
			try {
				Thread.sleep(50); // 让线程休息50毫秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 当SurfaceView创建的时候调用
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// 获得屏幕的宽和高
		screenW = this.getWidth();
		screenH = this.getHeight();

		mThread = new Thread(this); // 创建线程对象
		flag = true; // 设置线程标识为true
		xReturn = false; // 设置图形坐标不返回
		cReturn = false; // 设置贝塞尔曲线控制点坐标不返回
		mThread.start(); // 启动线程

	}

	/**
	 * 当SurfaceView视图发生改变的时候调用
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	/**
	 * 当SurfaceView销毁的时候调用
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false; // 设置线程的标识为false
	}

	/**
	 * 自定义的绘图方法
	 */
	public void mDraw() {

		mCanvas = mHolder.lockCanvas(); // 获得画布对象,开始对画布画画

		mCanvas.drawColor(Color.MAGENTA); // 设置画布颜色为黑色

		drawDoor(mCanvas);
		mHolder.unlockCanvasAndPost(mCanvas); // 把画布显示在屏幕上
	}

	public void drawF(Canvas mCanvas) {
	}


	/**
	 * 当屏幕被触摸时调用
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);
	}

}
