package jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import jp.ac.it_college.std.s15008.shootinggame.GameView;

/**
 * Created by s15008 on 17/02/07.
 */

public class Enemy extends BaseObject {
    private static final String TAG = "Enemy";

    public boolean mIsVisible;
    private Bitmap mBitmap;
    private Paint mPaint;
    private final int MOVE_WEIGHT = 5;
    private float mAlignX;
    private float mLaunchTime;

    public Enemy(Bitmap bitmap) {
        mPaint = new Paint();
        this.mBitmap = bitmap;

        // テスト
        mCenterX = 0;
        mCenterY = 0;

        init();
    }

    void init() {
        mIsVisible = false;

    }


    public void set(float centerX, float centerY, float launchTime, float alignValue) {
        mIsVisible = true;
        mCenterX = centerX;
        mCenterY = centerY;
        mLaunchTime = launchTime;
        mAlignX = alignValue;
    }

    @Override
    public void update() {
        if (!mIsVisible) return;

        mCenterY += MOVE_WEIGHT;

        // 画面外に出たら消す
        if (mCenterY < 0 || mCenterY > GameView.GAME_HEIGHT || mCenterX < 0 || mCenterX > GameView.GAME_WIDTH) {
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
}
