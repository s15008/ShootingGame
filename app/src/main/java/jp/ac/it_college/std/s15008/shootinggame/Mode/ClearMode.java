package jp.ac.it_college.std.s15008.shootinggame.Mode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    // デバッグ用モード遷移用
    Handler mTimerHandler;
    Runnable mGotoNextMode;

    Paint paintLiner;
    Paint paintScore;
    private int textSize = 75;

    private int topLineX;
    private int topLineY;
    private int bottomLineX;
    private int bottomLineY;


    public ClearMode(Context context) {
        mCurrentMode = GameView.Mode.INIT;
        mNextMode = mCurrentMode;
    }

    /**
     * init
     * モード開始前の初期化
     */
    public void init() {
        mCurrentMode = GameView.Mode.CLEAR;
        mNextMode = mCurrentMode;

        // モード遷移処理
        mTimerHandler = new Handler();
        mGotoNextMode = new Runnable() {
            @Override
            public void run() {
                mNextMode = GameView.Mode.INTRO;
                mTimerHandler.removeCallbacks(mGotoNextMode);
            }
        };

        mTimerHandler.postDelayed(mGotoNextMode, 3000);
    }

    public void draw(Canvas canvas) {
        setDrawScore(canvas);
        setDrawLiner(canvas);
    }

    private Paint setDrawScore(Canvas score) {
        paintScore = new Paint();
        paintScore.setColor(Color.BLUE);
        paintScore.setAntiAlias(true);   //文字をなめらかにする処理
        paintScore.setTextSize(textSize);
        paintScore.setTextAlign(Paint.Align.CENTER);
        paintScore.setTextSkewX((float) -0.5f); // 斜め文字

        int height = 300;
        int width = score.getWidth() / 2;
        String Score = "Score:  ";
        for (int i = 0; i < 5; i++) {
            score.drawText(Score + i, width, height, paintScore);
            height = height + textSize;
        }

        return paintScore;
    }

    private Paint setDrawLiner(Canvas liner) {
        paintLiner = new Paint();
        paintLiner.setColor(Color.BLACK);
        paintLiner.setAntiAlias(true);   //文字をなめらかにする処理

        topLineX = liner.getWidth();
        topLineY = 200;
        bottomLineX = 0;
        bottomLineY = 800;


        //TODO: 変更求む

        return paintLiner;
    }

    public void update(MotionEvent motionEvent) {
        // モード遷移はinit()で行っています
        // init()はモード開始時に一回処理されるメソッドです

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mNextMode = GameView.Mode.INTRO;
//            }
//        }, 3000);
    }
}
