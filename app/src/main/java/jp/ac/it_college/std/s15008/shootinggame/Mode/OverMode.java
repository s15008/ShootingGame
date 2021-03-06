package jp.ac.it_college.std.s15008.shootinggame.Mode;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;

import jp.ac.it_college.std.s15008.shootinggame.GameView;
import jp.ac.it_college.std.s15008.shootinggame.TitleActivity;


/**
 * Created by s15008 on 17/01/31.
 */

public class OverMode {
    private static final String TAG = "OverMode";

    private Context mContext;

    public GameView.Mode mCurrentMode;
    public GameView.Mode mNextMode;

    public GameView.GameData mGameData;

    // スケール倍率
    private float mScaleX;
    private float mScaleY;

    // 画像イメージ
    private Bitmap mBitmapLogo;
    private MyImageButton mButtonLogo;
    private MyImageButton mButtonContinue;
    private MyImageButton mButtonTitleBack;

    // 音声イメージ
    private final int SOUND_MAX = 1;
    private SoundPool mSoundPool;
    private int mSoundWolf;

    // デバッグ用
    Paint paintText;
    Paint paintBackground;

    /**
     * イメージボタンクラス
     */
    class MyImageButton {
        private final Paint mPaint;
        private Bitmap mBitmap;
        private Rect mRect;

        // アニメーション用
        private ValueAnimator mValueAnimation;
        private boolean mIsAnimate;
        private Rect mRectOrigin;

        // デバッグ描画
        private final Paint mDebugPaint;

        public MyImageButton(Bitmap bitmap, int x, int y, int width, int height) {
            mBitmap = bitmap;
            mPaint = new Paint();
            mRect = new Rect(x, y, (x + width), (y + height));

            mValueAnimation = null;
            mIsAnimate = false;
            mRectOrigin = mRect;

            // デバッグ描画
            mDebugPaint = new Paint();
            mDebugPaint.setColor(Color.RED);
            mDebugPaint.setStyle(Paint.Style.STROKE);
        }

        public void update() {
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(mBitmap, mRect.left, mRect.top, mPaint);

            // デバッグ描画
            //canvas.drawRect(mRect, mDebugPaint);
        }
    }


    /**
     * Constructor
     * インスタンスの生成
     * @param context
     */
    public OverMode(Context context) {
        mContext = context;
        mCurrentMode = GameView.Mode.INIT;
        mNextMode = mCurrentMode;

        // デバッグ用
        paintText = new Paint();
        paintText.setColor(Color.RED);
        paintText.setTextSize(25);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintBackground = new Paint();
        paintBackground.setColor(Color.argb(200, 0, 0, 0));

        // サウンドイメージ
        mSoundPool = new SoundPool(SOUND_MAX, AudioManager.STREAM_MUSIC, 0);
        AssetManager assetManager = context.getAssets();
        try {
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd("over/se_wolf.mp3");
            mSoundWolf = mSoundPool.load(assetFileDescriptor, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ゲームオーバーロゴオブジェクト
        Bitmap bitmap = getBitmapImageToAssets(context, "over/logo_gameover.png");
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int x = GameView.GAME_WIDTH;
        int y = (GameView.GAME_HEIGHT / 3) - (height / 2);
        mButtonLogo = new MyImageButton(bitmap, x, y, width, height);

        // コンティニューボタンオブジェクト
        bitmap = getBitmapImageToAssets(mContext, "over/logo_continue.png");
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        x = -width;
        y = (GameView.GAME_HEIGHT - (GameView.GAME_HEIGHT / 4)) - (height / 2) - (height);
        mButtonContinue = new MyImageButton(bitmap, x, y, width, height);

        // タイトルバックボタンオブジェクト
        bitmap = getBitmapImageToAssets(mContext, "over/logo_titleback.png");
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        x = -width;
        y = (GameView.GAME_HEIGHT - (GameView.GAME_HEIGHT / 4)) - (height / 2) + (height);
        mButtonTitleBack = new MyImageButton(bitmap, x, y, width, height);
    }

    // 初期化処理
    public void init(GameView.GameData gameData, float scaleX, float scaleY) {
        mCurrentMode = GameView.Mode.OVER;
        mNextMode = mCurrentMode;

        mGameData = gameData;

        mScaleX = scaleX;
        mScaleY = scaleY;

        // ロゴの位置の初期化
        mButtonLogo.mRect.set(mButtonLogo.mRectOrigin);

        // ロゴアニメーション設定(右スライドイン)
        mButtonLogo.mRectOrigin = new Rect(mButtonLogo.mRect);
        mButtonLogo.mValueAnimation = ValueAnimator.ofInt(0, GameView.GAME_WIDTH/2 + mButtonLogo.mRect.width() / 2);
        mButtonLogo.mValueAnimation.setDuration(1000);
        mButtonLogo.mValueAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int x = mButtonLogo.mRectOrigin.left - (int) valueAnimator.getAnimatedValue();
                int y = mButtonLogo.mRectOrigin.top;
                mButtonLogo.mRect.offsetTo(x, y);
            }
        });

        // コンティニューアニメーション設定(左スライドイン)
        mButtonContinue.mRect.set(mButtonContinue.mRectOrigin);

        // アニメーション設定
        mButtonContinue.mRectOrigin = new Rect(mButtonContinue.mRect);
        mButtonContinue.mValueAnimation = ValueAnimator.ofInt(
                0, (mButtonContinue.mRect.width() / 2) + (GameView.GAME_WIDTH / 2));
        mButtonContinue.mValueAnimation.setDuration(2000);
        mButtonContinue.mValueAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int x = mButtonContinue.mRectOrigin.left + (int) valueAnimator.getAnimatedValue();
                int y = mButtonContinue.mRectOrigin.top;
                mButtonContinue.mRect.offsetTo(x, y);
            }
        });

        // タイトルバックアニメーション設定(左スライドイン)
        mButtonTitleBack.mRect.set(mButtonTitleBack.mRectOrigin);

        // アニメーション設定
        mButtonTitleBack.mRectOrigin = new Rect(mButtonTitleBack.mRect);
        mButtonTitleBack.mValueAnimation = ValueAnimator.ofInt(
                0, (mButtonTitleBack.mRect.width() / 2) + (GameView.GAME_WIDTH / 2));
        mButtonTitleBack.mValueAnimation.setDuration(2000);
        mButtonTitleBack.mValueAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int x = mButtonTitleBack.mRectOrigin.left + (int) valueAnimator.getAnimatedValue();
                int y = mButtonTitleBack.mRectOrigin.top;
                mButtonTitleBack.mRect.offsetTo(x, y);
            }
        });

        // アニメーションスタート
        mButtonLogo.mValueAnimation.start();
        mButtonContinue.mValueAnimation.start();
        mButtonTitleBack.mValueAnimation.start();

        Log.d(TAG, String.format("LEVEL : %d\tSCORE : %d", mGameData.mLevel, mGameData.mScore));
    }

    /**
     * 指定した画像イメージを取得する
     * @param context
     * @param filePath
     * @return bitmap image
     */
    private static Bitmap getBitmapImageToAssets(Context context, String filePath) {
        final AssetManager assetManager = context.getAssets();
        BufferedInputStream bufferedInputStream = null;

        try {
            bufferedInputStream = new BufferedInputStream(assetManager.open(filePath));
            return BitmapFactory.decodeStream(bufferedInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    // 更新処理
    public void update(GameView.ScaledMotionEvent scaledMotionEvent) {
        // タッチ処理
        if (scaledMotionEvent.isTouch()) {
            float touchX = scaledMotionEvent.getX();
            float touchY = scaledMotionEvent.getY();
            if (scaledMotionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (mButtonLogo.mRect.contains((int) touchX, (int) touchY)) {
                    // GameOverボタンをタッチしたとき
                    mSoundPool.play(mSoundWolf, 1f, 1f, 0, 0, 1);
                    mButtonLogo.mValueAnimation.reverse();
                }
                if (mButtonContinue.mRect.contains((int) touchX, (int) touchY)) {
                    // Continueボタンをタッチしたとき
                    mGameData.init();
                    mNextMode = GameView.Mode.INTRO;
                }
                if (mButtonTitleBack.mRect.contains((int) touchX, (int) touchY)) {
                    // TitleBackボタンをタッチしたとき
                    mContext.startActivity(new Intent(mContext, TitleActivity.class));
                }
            }
        }
    }

    // 描画処理
    public void draw(Canvas canvas) {
        // 背景フィルター
        canvas.drawRect(0, 0, GameView.GAME_WIDTH, GameView.GAME_HEIGHT, paintBackground);

        // ボタン
        mButtonLogo.draw(canvas);
        mButtonContinue.draw(canvas);
        mButtonTitleBack.draw(canvas);
    }
}
