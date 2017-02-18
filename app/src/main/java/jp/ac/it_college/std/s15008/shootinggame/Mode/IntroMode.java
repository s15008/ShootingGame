package jp.ac.it_college.std.s15008.shootinggame.Mode;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import jp.ac.it_college.std.s15008.shootinggame.GameView;


/**
 * Created by s15008 on 17/01/30.
 */

public class IntroMode {
    private static final String TAG = "IntroMode";

    public GameView.Mode mCurrentMode;
    public GameView.Mode mNextMode;

    private float mTouchX;
    private float mTouchY;

    Handler mTimerHandler;
    Runnable mGotoNextMode;

    private GameView.GameData mGameData;


    // デバッグ用
    Paint paintText;

    Context context;

    MyButton mMyButton;
    class MyButton {
        private final Paint mRectPaint;
        private Rect mRect;
        private int mWidth;
        private int mHeight;

        public MyButton(int x, int y) {
            mRectPaint = new Paint(Color.BLUE);
            mWidth = 300;
            mHeight = 250;
            this.mRect = new Rect(x, y, x+mWidth, y+mHeight);
        }

        public void draw(Canvas canvas) {
            canvas.drawRect(mRect, mRectPaint);
        }
    }


    public IntroMode(Context context) {
        mCurrentMode = GameView.Mode.INIT;
        mNextMode = mCurrentMode;

        paintText = new Paint();
        paintText.setColor(Color.RED);
        paintText.setTextSize(250);
        paintText.setTextAlign(Paint.Align.CENTER);
        this.context = context;
    }

    // 初期化処理
    public void init(GameView.GameData gameData) {
        mCurrentMode = GameView.Mode.INTRO;
        mNextMode = mCurrentMode;
        mGameData = gameData;

        // モード遷移処理
        mTimerHandler = new Handler();
        mGotoNextMode = new Runnable() {
            @Override
            public void run() {
                mNextMode = GameView.Mode.GAME;
                mTimerHandler.removeCallbacks(mGotoNextMode);
            }
        };

        mTimerHandler.postDelayed(mGotoNextMode, 3000);
        Log.d(TAG, String.format("WAVE : %d", mGameData.mLevel));
    }

    // 更新処理
    public void update(GameView.ScaledMotionEvent scaledMotionEvent) {
        // タッチ処理
        if (scaledMotionEvent.isTouch()) {
            float touchX = scaledMotionEvent.getX();
            float touchY = scaledMotionEvent.getY();

            if (scaledMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            }
        }
    }

    // 描画処理
    public void draw(Canvas canvas) {
        canvas.drawText("WAVE " + mGameData.mLevel,
                GameView.GAME_WIDTH / 2, GameView.GAME_HEIGHT / 2, paintText);
    }
}
