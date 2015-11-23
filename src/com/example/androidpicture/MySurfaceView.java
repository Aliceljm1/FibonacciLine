package com.example.androidpicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author 刘泾铭
 * @version 创建时间：2015-11-2 下午7:05:06 类说明
 */

public class MySurfaceView extends SurfaceView implements Runnable, Callback {

	private SurfaceHolder mHolder; // 用于控制SurfaceView

	private Canvas mCanvas; // 声明一张画布

	private Paint mPaint, qPaint; // 声明两只画笔

	private Path mPath, qPath, tPath; // 声明三条路径

	private int mX, mY; // 用于控制图形的坐标

	// 分别 代表贝塞尔曲线的开始坐标,结束坐标,控制点坐标
	private int qStartX, qStartY, qEndX, qEndY, qControlX, qCOntrolY;

	private int screenW, screenH; // 用于屏幕的宽高

	private Thread mThread; // 声明一个线程

	// flag用于线程的标识,xReturn用于标识图形坐标是否返回,cReturn用于标识贝塞尔曲线的控制点坐标是否返回
	private boolean flag, xReturn, cReturn;

	/**
	 * 构造函数，主要对一些对象初始化
	 */
	public MySurfaceView(Context context) {
		super(context);

		mHolder = this.getHolder(); // 获得SurfaceHolder对象
		mHolder.addCallback(this); // 添加状态监听

		mPaint = new Paint(); // 创建一个画笔对象
		mPaint.setColor(Color.WHITE); // 设置画笔的颜色为白色

		qPaint = new Paint(); // 创建一个画笔对象
		qPaint.setAntiAlias(true); // 消除锯齿
		qPaint.setStyle(Style.STROKE); // 设置画笔风格为描边
		qPaint.setStrokeWidth(3); // 设置描边的宽度为3
		qPaint.setColor(Color.GREEN); // 设置画笔的颜色为绿色

		// 创建路径对象
		mPath = new Path();
		qPath = new Path();
		tPath = new Path();

		// 设置坐标为50,100
		mX = 50;
		mY = 100;

		// 设置贝塞尔曲线的开始坐标为(10,200)
		qStartX = 10;
		qStartY = 200;

		setFocusable(true); // 设置焦点

	}

	/**
	 * 当SurfaceView创建的时候调用
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// 获得屏幕的宽和高
		screenW = this.getWidth();
		screenH = this.getHeight();

		qEndX = screenW - 10; // 设置贝塞尔曲线的终点横坐标为屏幕宽度减10
		qEndY = qStartY; // 设置贝塞尔曲线的终点纵坐标和起点纵坐标一样

		// 设置贝塞尔曲线的控制点坐标为终点坐标与起点坐标对应的差值的一半,注意这里不是曲线的中点
		qControlX = (qEndX - qStartX) / 2;
		qCOntrolY = (qEndY - qStartY) / 2;

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
	 * 线程运行的方法,当线程start后执行
	 */
	@Override
	public void run() {

		while (flag) {
			mDraw(); // 调用自定义的绘图方法
			mGameLogic(); // 调用自定义的逻辑方法
			try {
				Thread.sleep(50); // 让线程休息50毫秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 自定义的绘图方法
	 */
	public void mDraw() {

		mCanvas = mHolder.lockCanvas(); // 获得画布对象,开始对画布画画

		mCanvas.drawColor(Color.GREEN); // 设置画布颜色为黑色

		drawBitMap(mCanvas);
		//
		// canvasMethod(mCanvas); // 调用自定义的方法,主要是在传进去的画布对象上画画

		mHolder.unlockCanvasAndPost(mCanvas); // 把画布显示在屏幕上
	}

	public void drawBitMap(Canvas mCanvas) {
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
			int h = 100;
		int w = 100;
		mBitmap= Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		
		int[] buffer = new int[h * w];
		int i = 0;
		for (int dy = 0; dy < h; dy++) {
			for (int dx = 0; dx < w; dx++) {
				if ((dx + dy) % 2 == 0)
					buffer[i++] = Color.BLACK;
				else
					buffer[i++] = Color.WHITE;
			}
		}

		mBitmap.setPixels(buffer, 0, w, 0, 0, w, h);
		//mCanvas.drawBitmap(buffer, 0, w, 10, 10, w, h, false, null);// 绘制的时候忽略了alpha

		//mCanvas.drawBitmap(createColors(), 0, STRIDE,10,10,WIDTH,HEIGHT,false,null);
	  mCanvas.drawBitmap(mBitmap, mX, mY, mPaint);
	}

	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;
	private static final int STRIDE = 100; // must be >= WIDTH

	private static int[] createColors() {
		int[] colors = new int[STRIDE * HEIGHT];
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				int r = x * 255 / (WIDTH - 1);
				int g = y * 255 / (HEIGHT - 1);
				int b = 255 - Math.min(r, g);
				int a = Math.max(r, g);
				
				//4个int值 拼凑一个int颜色值
				colors[y * STRIDE + x] = (a << 24) | (r << 16) | (g << 8) | b;
			}
		}
		return colors;
	}

	/**
	 * 自定义的方法,简单绘制一些基本图形
	 * 
	 * @param mCanvas
	 *            把图形画在mCanvas上
	 */
	public void canvasMethod(Canvas mCanvas) {

		// 创建对应坐标的矩形区域
		RectF mArc = new RectF(mX, mY - 70, mX + 50, mY - 20);
		// 画填充弧,在矩形区域内,从弧的最右边开始,画270度,然后再通过连接圆心来填充
		mCanvas.drawArc(mArc, 0, 270, true, mPaint);

		// 获得icon的Bitmap对象
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		// 画图片
		mCanvas.drawBitmap(mBitmap, mX, mY, mPaint);

		// 画圆,(x轴,y轴,半径,画笔)
		mCanvas.drawCircle(mX + 10, mY + 60, 10, mPaint);

		// 画一条线,(起点横坐标,起点纵坐标,终点横坐标,终点纵坐标,画笔)
		mCanvas.drawLine(mX, mY + 75, mX + 20, mY + 75, mPaint);
		// 画多条线,(坐标数组,画笔)坐标数组里每四个值构成一条线
		mCanvas.drawLines(new float[] { mX + 50, mY + 45, mX + 50, mY + 75,
				mX + 60, mY + 45, mX + 60, mY + 75 }, mPaint);

		// 创建对应矩形区域
		RectF mOval = new RectF(mX, mY + 80, mX + 60, mY + 110);
		// 画椭圆
		mCanvas.drawOval(mOval, mPaint);

		/*
		 * Paint qPaint = new Paint(); qPaint.setColor(Color.RED);
		 * mCanvas.drawPaint(qPaint);
		 */

		// 重置Path里的所有路径
		mPath.reset();
		// 设置Path的起点
		mPath.moveTo(mX, mY + 120);
		// 第二个点
		mPath.lineTo(screenW - 10, mY + 120);
		// 第三个点
		mPath.lineTo(screenW - 10, mY + 150);
		// 画出路径,这里画的是三角形
		mCanvas.drawPath(mPath, mPaint);

		// 重置Path里的所有路径
		qPath.reset();
		// 设置Path的起点
		qPath.moveTo(qStartX, qStartY);
		// 设置贝塞尔曲线的控制点坐标和终点坐标
		qPath.quadTo(qControlX, qCOntrolY, qEndX, qEndY);
		// 画出贝塞尔曲线
		mCanvas.drawPath(qPath, qPaint);

		// 画点
		mCanvas.drawPoint(mX, mY + 155, qPaint);
		// 画多个点,坐标数组每两个值代表一个点的坐标
		mCanvas.drawPoints(new float[] { mX, mY + 160, mX + 5, mY + 160,
				mX + 5, mY + 160 }, qPaint);

		// 画矩形
		mCanvas.drawRect(mX, mY + 170, mX + 100, mY + 220, mPaint);

		// 设置矩形区域
		RectF mRect = new RectF(mX, mY + 230, mX + 100, mY + 260);
		// 画圆角矩形,这个方法的第二第三个参数在后面有图讲解
		mCanvas.drawRoundRect(mRect, 10, 10, mPaint);

		// 画文本
		mCanvas.drawText("drawText", mX, mY + 290, mPaint);
		// 画文本,数组里每两个值代表文本的一个字符的坐标，数组的坐标可以比字符串里的字符多，但不可以少
		mCanvas.drawPosText("哈哈你好", new float[] { mX, mY + 310, mX + 20,
				mY + 310, mX + 40, mY + 310, mX + 60, mY + 310 }, mPaint);

		// 重置Path
		tPath.reset();
		// 添加一个圆形路径,坐标,半径,方向(顺时针还是逆时针)
		tPath.addCircle(mX + 10, mY + 340, 10, Path.Direction.CW);
		// 画出路径
		mCanvas.drawPath(tPath, qPaint);
		// 把文本画在路径上,但不会画出路径
		mCanvas.drawTextOnPath("draw", tPath, 30, 0, mPaint);

	}

	/**
	 * 自定义游戏逻辑方法
	 */
	public void mGameLogic() {

		// 判断图形横坐标是否返回
		if (!xReturn) { // 横坐标不返回
			// 判断图形横坐标是否小于屏幕宽度减去100
			if (mX < (screenW - 100)) { // 小于
				mX += 3; // 横坐标往右3
			} else { // 不小于
				xReturn = true; // 设置横坐标返回
			}
		} else { // 横坐标返回
			// 判断横坐标是否大于10
			if (mX > 10) { // 大于
				mX -= 3; // 横坐标往左3
			} else { // 不大于
				xReturn = false; // 设置横坐标不返回
			}
		}

		// 判断贝塞尔曲线的控制点横坐标是否返回
		if (!cReturn) { // 控制点横坐标不返回
			// 判断控制点横坐标是否小于终点横坐标减3
			if (qControlX < (qEndX - 3)) { // 小于
				qControlX += 3; // 控制点横坐标往右3
			} else { // 不小于
				cReturn = true; // 设置控制点横坐标返回
			}
		} else { // 控制点横坐标返回
			// 判断控制点横坐标是否大于起点横坐标加3
			if (qControlX > (qStartX + 3)) { // 大于
				qControlX -= 3; // 控制点横坐标减3
			} else { // 不大于
				cReturn = false; // 设置控制点横坐标不返回
			}
		}
	}

	/**
	 * 当屏幕被触摸时调用
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// 设置贝塞尔曲线的坐标为触摸时坐标
		qStartX = (int) event.getX();
		qStartY = (int) event.getY();

		// 设置贝塞尔曲线的控制点坐标为终点坐标与起点坐标对应的差值的一半,注意这里不是曲线的中点
		qControlX = Math.abs(qEndX - qStartX) / 2;
		qCOntrolY = Math.abs(qEndY - qStartY) / 2;

		// 设置控制点的横坐标不返回
		cReturn = false;
		return super.onTouchEvent(event);
	}
}
