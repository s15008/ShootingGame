package jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import jp.ac.it_college.std.s15008.shootinggame.GameView;

/**
 * Created by s15008 on 17/02/07.
 */

public class Bullet extends BaseObject {
    private static final String TAG = "Bullet";

    private static final float SIZE = 20;

    public boolean mIsVisible;
    private Bitmap mBitmap;
    private Paint mPaint;
    private final int MOVE_WEIGHT = 5;
    private float mAlignX;


    public Bullet(Bitmap bitmap) {
        mPaint = new Paint();
        this.mBitmap = bitmap;

        // テスト
        mCenterX = 0;
        mCenterY = 0;
        mR = SIZE;

        init();
    }

    void init() {
        mIsVisible = false;
    }

    public void set(int centerX, int centerY, float alignValue) {
        mIsVisible = true;
        mCenterX = centerX;
        mCenterY = centerY;
        mAlignX = alignValue;
    }

    @Override
    public void update() {
        if (!mIsVisible) return;

        mCenterY -= MOVE_WEIGHT;
        mCenterX += mAlignX * MOVE_WEIGHT;

        // 画面外に出たら消す
        if (mCenterY < 0 || mCenterX < 0 || mCenterX > GameView.GAME_WIDTH) {
            mIsVisible = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mIsVisible) return;

        canvas.save();
        canvas.translate(-(mBitmap.getWidth() / 2), -(mBitmap.getHeight() / 2));
        canvas.drawBitmap(mBitmap, mCenterX, mCenterY, mPaint);
        canvas.restore();
    }

    public void hit() {
        Log.d(TAG, "hit");
    }
}
