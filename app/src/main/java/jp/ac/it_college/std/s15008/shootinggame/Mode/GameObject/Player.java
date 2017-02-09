package jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

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

    public boolean isInit;
    private Bitmap mBitmap;
    public Rect mRect;
    private Paint mPaint;


    public Player(Bitmap bitmap) {
        mPaint = new Paint();
        this.mBitmap = bitmap;
        mRect = new Rect();

        init();
    }

    void init() {
        // 初期位置の指定(真ん中下らへん)
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int left = (GameView.GAME_WIDTH / 2) - (width / 2);
        int top = (GameView.GAME_HEIGHT - (GameView.GAME_HEIGHT / 4)) - (height / 2);
        int right = left + width;
        int bottom = top + height;
        mRect.set(left, top, right, bottom);
    }

    public void set(int centerX, int centerY) {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int left = centerX - (width / 2);
        int top = centerY - (height / 2);
        int right = left + width;
        int bottom = top + height;
        mRect.set(left, top, right, bottom);
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(-(mBitmap.getWidth()/2), -(mBitmap.getHeight()/2));
        canvas.drawBitmap(mBitmap, mRect.centerX(), mRect.centerY(), mPaint);
        canvas.restore();
    }
}
