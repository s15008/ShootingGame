package jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import jp.ac.it_college.std.s15008.shootinggame.GameView;

/**
 * Created by s15008 on 17/02/07.
 */

public class Enemy extends BaseObject {
    private static final String TAG = "Enemy";

    public static enum ENEMY_TYPE {
        STRAIGHT,
    }

    public boolean mIsLaunched;
    public boolean mIsAlive;
    private Bitmap mBitmap;
    private Paint mPaint;
    private final int MOVE_WEIGHT = 5;
    private float mAlignX;
    public float mLaunchTime;
    public int mType;

    public Enemy(Bitmap bitmap) {
        mPaint = new Paint();
        this.mBitmap = bitmap;

        init();
    }

    public void init() {
        mCenterX = 0;
        mCenterY = 0;
        mIsLaunched = false;
        mIsAlive = false;
    }

    public void set(int type, float centerX, float centerY, float launchTime, float alignValue) {
        mType = type;
        mCenterX = centerX;
        mCenterY = centerY;
        mLaunchTime = launchTime;
        mAlignX = alignValue;
    }

    public void hit() {
        Log.d(TAG, "hit");
        mIsAlive = false;
    }

    public void launch() {
        mIsAlive = true;
        mIsLaunched = true;
    }

    @Override
    public void update() {
        if (!mIsAlive) return;

        mCenterX += mAlignX;
        mCenterY += MOVE_WEIGHT;

        // 画面外に出たら消す
        if (mCenterY < 0 || mCenterY > GameView.GAME_HEIGHT || mCenterX < 0 || mCenterX > GameView.GAME_WIDTH) {
            mIsAlive = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mIsAlive) return;

        canvas.save();
        canvas.translate(-(mBitmap.getWidth() / 2), -(mBitmap.getHeight() / 2));
        canvas.drawBitmap(mBitmap, mCenterX, mCenterY, mPaint);
        canvas.restore();
    }
}
