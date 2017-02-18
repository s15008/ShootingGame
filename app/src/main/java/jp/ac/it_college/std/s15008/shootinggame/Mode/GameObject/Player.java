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
    private static final int CENTER_X = 448;
    private static final int CENTER_Y = 1488;
    public static final int LEFT_X = 160;
    public static final int LEFT_Y = 1520;
    public static final int RIGHT_X = 739;
    public static final int RIGHT_Y = 1527;

    private static final int SIZE = 80;
    private static final int LIFE_POINT_MAX = 10;
    private Bitmap mBitmap;
    public Rect mRect;
    private Paint mPaint;
    public int mLifePoint;
    private float mRod;
    private boolean mIsRealBody;

    // 地表
    private final int GROUND_HEIGHT = 100;
    public Rect mRectGround;

    // ライフバー
    private final static int LIFE_POINT_BAR_W = 800;
    private final static int LIFE_POINT_BAR_H = 40;
    private final static int LIFE_POINT_BAR_X = 50;
    private final static int LIFE_POINT_BAR_Y = 1600 - 40 - 20;
    private final Paint mPaintLifePointBar = new Paint();
    private Rect mRectLifePointBar;

    // debug
    private final Paint dPaint = new Paint();


    public Player(Bitmap bitmap, boolean isRealBody) {
        // 自機
        mPaint = new Paint();
        this.mBitmap = bitmap;
        mRect = new Rect();
        mR = SIZE;
        mIsRealBody = isRealBody;

        // 地表
        mRectGround = new Rect(0, (GameView.GAME_HEIGHT - GROUND_HEIGHT),
                GameView.GAME_WIDTH, GameView.GAME_HEIGHT);

        // debug
        dPaint.setColor(Color.RED);
        dPaint.setStyle(Paint.Style.STROKE);
        dPaint.setStrokeWidth(5);

        // 初期位置の指定
        set(CENTER_X, CENTER_Y);
        init();
    }

    /**
     * init
     * モード開始時の初期化処理
     */
    public void init() {
        // プレイヤーデータ初期化
        mLifePoint = LIFE_POINT_MAX;
        mRod = 0f;

        // ライフバー初期化
        mPaintLifePointBar.setColor(Color.GREEN);
        mRectLifePointBar = new Rect(LIFE_POINT_BAR_X, LIFE_POINT_BAR_Y,
                LIFE_POINT_BAR_X + LIFE_POINT_BAR_W, LIFE_POINT_BAR_Y + LIFE_POINT_BAR_H);
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
        int left = LIFE_POINT_BAR_X;
        int top = LIFE_POINT_BAR_Y;
        int right = (int) ((double) LIFE_POINT_BAR_W * ((double) mLifePoint / (double) LIFE_POINT_MAX)) + LIFE_POINT_BAR_X;
        int bottom = LIFE_POINT_BAR_Y + LIFE_POINT_BAR_H;
        mRectLifePointBar.set(left, top, right, bottom);
        Log.d(TAG, "" + mRectLifePointBar.width());
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

        // 本体以外は壺以外の描画をスキップ
        if (!mIsRealBody) {
            return;
        }

        // ライフポイントバー
        canvas.drawRect(mRectLifePointBar, mPaintLifePointBar);

        // debug 地表
        //canvas.drawRect(mRectGround, dPaint);
    }

    public void setAngle(float x, float y) {
        mRod = getDegree(mCenterX, mCenterY, x, y) + 90;
    }

    private float getDegree(float x, float y, float x2, float y2) {
        double radian = Math.atan2(y2 - y,x2 - x);
        return (float) (radian * 180d / Math.PI);
    }
}
