package jp.ac.it_college.std.s15008.shootinggame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import jp.ac.it_college.std.s15008.shootinggame.Mode.ClearMode;
import jp.ac.it_college.std.s15008.shootinggame.Mode.GameMode;
import jp.ac.it_college.std.s15008.shootinggame.Mode.IntroMode;
import jp.ac.it_college.std.s15008.shootinggame.Mode.OverMode;


/**
 * Created by s15008 on 17/01/30.
 */

public class GameView extends View {
    private static final String TAG = "GameView";

    public static final int GAME_WIDTH = 900;
    public static final int GAME_HEIGHT = 1600;

    private float mScaleX;
    private float mScaleY;

    private MotionEvent mMotionEvent;

    // ゲームモード
    public enum Mode {
        INIT,
        INTRO,
        GAME,
        OVER,
        CLEAR,
    }
    // 現在のゲームモード
    private Mode mCurrentMode;

    // ゲームモードのオブジェクト
    private GameMode mGameMode;
    private IntroMode mIntroMode;
    private OverMode mOverMode;
    private ClearMode mClearMode;

    public int mCurrentLevel;

    // デバッグ用
    Paint paintText = new Paint();


    /**
     * GameView
     * コンストラクタ
     * @param context
     * @param attrs
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // タッチイベント関係
        mMotionEvent = null;

        mCurrentMode = Mode.INTRO;

        // ゲームモードオブジェクト
        mGameMode = new GameMode(context);
        mIntroMode = new IntroMode(context);
        mOverMode = new OverMode(context);
        mClearMode = new ClearMode(context);

        mCurrentLevel = 1;

        // デバッグ用
        paintText = new Paint();
        paintText.setColor(Color.RED);
        paintText.setTextSize(50);
        paintText.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * onLayout
     * レイアウトげ生成されるタイミングで実行される
     * 実質コンストラクタ的に使う
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mMotionEvent = null;

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mScaleX = (float) width / (float) GameView.GAME_WIDTH;
        mScaleY = (float) height / (float) GameView.GAME_HEIGHT;

        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * onDraw
     * 更新処理と描画処理を行う
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 背景を設定する(暫定白色)
        setBackgroundColor(Color.WHITE);

        // 倍率を画面解像度に調整する
        canvas.scale(mScaleX, mScaleY);

        // 背景としてゲーム画面を描画
        if (mCurrentMode != Mode.GAME) {
            mGameMode.draw(canvas);
        }

        // モード描画
        if (mCurrentMode == Mode.INTRO) {
            // ステージイントロ時の処理
            if (mIntroMode.mCurrentMode == Mode.INIT || mIntroMode.mCurrentMode != Mode.INTRO) {
                // モードの初期化
                mIntroMode.init(mCurrentLevel);
            }

            mIntroMode.update(mMotionEvent);
            mIntroMode.draw(canvas);

            if (mIntroMode.mNextMode != mIntroMode.mCurrentMode) {
                // モード遷移
                mCurrentMode = mIntroMode.mNextMode;
                mIntroMode.mCurrentMode = mIntroMode.mNextMode;
                invalidate();
            }
        } else if (mCurrentMode == Mode.GAME) {
            // ゲーム時の処理
            if (mGameMode.mCurrentMode == Mode.INIT || mGameMode.mCurrentMode != Mode.GAME) {
                // モードの初期化
                mGameMode.init(mCurrentLevel);
            }

            mGameMode.update(mMotionEvent);
            mGameMode.draw(canvas);

            if (mGameMode.mNextMode != mGameMode.mCurrentMode) {
                // モード遷移
                mCurrentMode = mGameMode.mNextMode;
                mGameMode.mCurrentMode = mGameMode.mNextMode;
                invalidate();
            }
        } else if (mCurrentMode == Mode.CLEAR) {
            // ステージクリア時の処理
            if (mClearMode.mCurrentMode == Mode.INIT || mClearMode.mCurrentMode != Mode.CLEAR) {
                // モードの初期化
                mClearMode.init();
            }

            mClearMode.update(mMotionEvent);
            mClearMode.draw(canvas);

            if (mClearMode.mNextMode != mClearMode.mCurrentMode) {
                // モード遷移
                mCurrentMode = mClearMode.mNextMode;
                mClearMode.mCurrentMode = mClearMode.mNextMode;
                // レベルアップ処理
                mCurrentLevel++;
                if (mCurrentLevel >= 2) {
                    mCurrentLevel = 1;
                }
                invalidate();
            }
        } else if (mCurrentMode == Mode.OVER) {
            // ゲームオーバー時の処理
            if (mOverMode.mCurrentMode == Mode.INIT || mOverMode.mCurrentMode != Mode.OVER) {
                // モードの初期化
                mOverMode.init(mScaleX, mScaleY);
            }

            mOverMode.update(mMotionEvent);
            mOverMode.draw(canvas);

            if (mOverMode.mNextMode != mOverMode.mCurrentMode) {
                // モード遷移
                mCurrentMode = mOverMode.mNextMode;
                mOverMode.mCurrentMode = mOverMode.mNextMode;
                // レベル初期化処理
                mCurrentLevel = 1;
                invalidate();
            }
        }

        mMotionEvent = null;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mMotionEvent = event;

        return true;
    }
}
