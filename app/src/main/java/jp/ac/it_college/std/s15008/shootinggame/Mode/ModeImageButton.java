package jp.ac.it_college.std.s15008.shootinggame.Mode;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by s15008 on 17/02/03.
 */

public class ModeImageButton {
    private final Paint mPaint;
    private Bitmap mBitmap;
    private Rect mRect;

    // アニメーション用
    private ValueAnimator mValueAnimation;
    private boolean mIsAnimate;
    private Rect mRectOrigin;

    public ModeImageButton(Canvas canvas, Bitmap bitmap, int x, int y, int width, int height) {
        mBitmap = bitmap;
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mRect = new Rect(x, y, (x + width), (y + height));

        mValueAnimation = null;
        mIsAnimate = false;
        mRectOrigin = mRect;
    }

    public void action() {
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, mRect.left, mRect.top, mPaint);
        canvas.drawRect(mRect, mPaint);
    }
}
