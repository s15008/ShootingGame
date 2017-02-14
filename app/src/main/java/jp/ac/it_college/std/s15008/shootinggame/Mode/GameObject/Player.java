package jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import jp.ac.it_college.std.s15008.shootinggame.GameView;

/**
 * Created by s15008 on 17/02/06.
 */

/**
 * Playerクラス
 * あたり判定はRectのcenter値で行う
 */
public class Player extends BaseObject {
    private static final String TAG = "Player";


    // 自機
    private static final int WIDTH = 0;
    private static final int HEIGHT = 0;
    private static final int CENTER_X = 450;
    private static final int CENTER_Y = 1480;
    public static final int LEFT_X = 150;
    public static final int LEFT_Y = 1680;
    public static final int RIGHT_X = 720;
    public static final int RIGHT_Y = 1680;

    private static final int SIZE = 80;
    private static final int LIFE_MAX = 10;
    private Bitmap mBitmap;
    public Rect mRect;
    private Paint mPaint;
    private int mLifePoint;
    private float mRod;

    // 地表
    private final int GROUND_HEIGHT = 100;
    public Rect mRectGround;

    // debug
    private final Paint dPaint = new Paint();


    public Player(Bitmap bitmap) {
        // 自機
        mPaint = new Paint();
        this.mBitmap = bitmap;
        mRect = new Rect();
        mR = SIZE;
        mRod = 0f;

        // 地表
        mRectGround = new Rect(0, (GameView.GAME_HEIGHT - GROUND_HEIGHT),
                GameView.GAME_WIDTH, GameView.GAME_HEIGHT);

        // debug
        dPaint.setColor(Color.RED);
        dPaint.setStyle(Paint.Style.STROKE);
        dPaint.setStrokeWidth(10);

        init();
    }

    void init() {
        mLifePoint = LIFE_MAX;

        // 初期位置の指定
        set(CENTER_X, CENTER_Y);

    }

    public void set(int centerX, int centerY) {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int left = centerX - (width / 2);
        int top = centerY - height;
        int right = left + width;
        int bottom = top + height;
        mRect.set(left, top, right, bottom);

        // 将来的にこっちに移行(円形での処理)
        mCenterX = mRect.centerX();
        mCenterY = mRect.centerY();
    }

    public void hit() {
        Log.d(TAG, "hit");
        mLifePoint--;

        if (mLifePoint <= 0) {
            Log.d(TAG, "Game over");
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        // プレイヤー
        canvas.rotate(mRod, mCenterX, mCenterY);
        canvas.translate(-(mBitmap.getWidth()/2), -(mBitmap.getHeight()/2));
        canvas.drawBitmap(mBitmap, mCenterX, mCenterY, mPaint);
        canvas.restore();

        // 地表
        canvas.drawRect(mRectGround, dPaint);
    }

    public void setAngle(float x, float y) {
        mRod = getDegree(mCenterX, mCenterY, x, y) + 90;
    }

    private float getDegree(float x, float y, float x2, float y2) {
        double radian = Math.atan2(y2 - y,x2 - x);
        return (float) (radian * 180d / Math.PI);
    }
}
