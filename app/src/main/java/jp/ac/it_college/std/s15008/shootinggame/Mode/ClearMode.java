package jp.ac.it_college.std.s15008.shootinggame.Mode;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import jp.ac.it_college.std.s15008.shootinggame.GameView;

/**
 * Created by s15008 on 17/02/09.
 */

public class ClearMode {
    private static String TAG = "ClearMode";

    public GameView.Mode mCurrentMode;
    public GameView.Mode mNextMode;

    public GameView.GameData mGameData;

    // デバッグ用モード遷移用
    Handler mTimerHandler;
    Runnable mGotoNextMode;

    Paint mPaintLiner;
    Bitmap mBitmapLiner;
    Paint paintScore;
    private int textSize = 75;

    private boolean startScore;
    private ValueAnimator mValueAnimation;
    private int line_x;


    public ClearMode(Context context) {
        mCurrentMode = GameView.Mode.INIT;
        mNextMode = mCurrentMode;


        mPaintLiner = new Paint();
        mBitmapLiner = GameMode.getBitmapImageToAssets(context, "clear/score_line.png");


        paintScore = new Paint();
        paintScore.setColor(Color.WHITE);
        paintScore.setAntiAlias(true);   //文字をなめらかにする処理
        paintScore.setTextSize(textSize);
        paintScore.setTextAlign(Paint.Align.CENTER);
        paintScore.setTextSkewX((float) -0.5f); // 斜め文字
    }

    /**
     * init
     * モード開始前の初期化
     */
    public void init(GameView.GameData gameData) {
        mCurrentMode = GameView.Mode.CLEAR;
        mNextMode = mCurrentMode;

        mGameData = gameData;
        startScore = false;

        line_x = GameView.GAME_WIDTH;
        mValueAnimation = ValueAnimator.ofInt(0, GameView.GAME_WIDTH);
        mValueAnimation.setDuration(1000);
        mValueAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                line_x = (int)valueAnimator.getAnimatedValue();
                Log.d(TAG, (int)valueAnimator.getAnimatedValue() + "");
            }
        });

        mValueAnimation.start();


        // モード遷移処理
        mTimerHandler = new Handler();
        mGotoNextMode = new Runnable() {
            @Override
            public void run() {
                mNextMode = GameView.Mode.INTRO;
                mGameData.mLevel++;
                if (mGameData.mLevel > GameView.GameData.LEVEL_MAX) {
                    mGameData.mLevel = GameView.GameData.LEVEL_DEFAULT;
                }
                mTimerHandler.removeCallbacks(mGotoNextMode);
            }
        };

        mTimerHandler.postDelayed(mGotoNextMode, 7000);

        // デバッグ出力
        //Log.d(TAG, String.format("LEVEL : %d\tSCORE : %d", mGameData.mLevel, mGameData.mScore));
    }

    public void draw(Canvas canvas) {
        setDrawLiner(canvas);
        if (startScore) {
            setDrawScore(canvas);
        }
    }

    private Paint setDrawScore(Canvas score) {

        int height = GameView.GAME_HEIGHT / 2;
        int width = GameView.GAME_WIDTH / 2;
        String wave = "Wave" + mGameData.mLevel;
        String waveScore = "Score:   " + mGameData.mScore;
        score.drawText(wave, width, height - textSize, paintScore);
        score.drawText(waveScore, width, height + textSize, paintScore);

        return paintScore;
    }

    private Paint setDrawLiner(Canvas liner) {

        liner.drawBitmap(mBitmapLiner, GameView.GAME_WIDTH - line_x, GameView.GAME_HEIGHT / 2 - 200, mPaintLiner);
        liner.drawBitmap(mBitmapLiner, line_x - GameView.GAME_WIDTH, GameView.GAME_HEIGHT / 2 + 200, mPaintLiner);

        return mPaintLiner;
    }

    public void update(GameView.ScaledMotionEvent scaledMotionEvent) {
        if (!mValueAnimation.isRunning()) {
            startScore = true;
        }
    }
}
