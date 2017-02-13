package jp.ac.it_college.std.s15008.shootinggame.Mode;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.it_college.std.s15008.shootinggame.GameView;
import jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject.Bullet;
import jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject.Enemy;
import jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject.EnemyManager;
import jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject.Player;


/**
 * Created by s15008 on 17/01/30.
 */

public class GameMode {
    private static final String TAG = "GameMode";

    public GameView.Mode mCurrentMode;
    public GameView.Mode mNextMode;
    public int mCurrentLevel;

    public long mStartTime;

    // デバッグ用モード遷移用
    Handler mTimerHandler;
    Runnable mGotoNextMode;

    // 背景
    private Paint mPaintBackground;
    private Bitmap mBitmapBackground;

    // キャラクターオブジェクト
    private Player mPlayer;
    private List<Bullet> mBulletList;
    private Bitmap mBitmapBullet;
    private List<Enemy> mEnemyList;
    private Bitmap mBitmapEnemy;
    private EnemyManager mEnemyManager;

    // デバッグ用
    private float mTouchX =  0f;
    private float mTouchY = 0f;

    public GameMode(Context context) {
        mCurrentMode = GameView.Mode.INIT;
        mNextMode = mCurrentMode;

        mPaintBackground = new Paint();
        mBitmapBackground = getBitmapImageToAssets(context, "game/background_starry_sky.png");

        Bitmap bitmap = getBitmapImageToAssets(context, "game/player.png");
        mPlayer = new Player(bitmap);

        mBulletList = new ArrayList<>();
        mBitmapBullet = getBitmapImageToAssets(context, "game/bullet.png");

        mEnemyList = new ArrayList<>();
        mBitmapEnemy = getBitmapImageToAssets(context, "game/enemy_carrot.png");

        mEnemyManager = new EnemyManager(context);

    }

    /**
     * init
     * モード開始時の初期化を行う
     */
    public void init() {
        // モードモードの固定
        mCurrentMode = GameView.Mode.GAME;
        mNextMode = mCurrentMode;

        // 時間計測

        // エネミー生成/管理
        mCurrentLevel = 1;
        mEnemyManager.createEnemyList(mCurrentLevel);
        mEnemyManager.setStartTime(System.currentTimeMillis());

        mEnemyList = mEnemyManager.getEnemyList();

        // デバッグ
        mTimerHandler = new Handler();
        mGotoNextMode = new Runnable() {
            @Override
            public void run() {
                mNextMode = GameView.Mode.CLEAR;
                mTimerHandler.removeCallbacks(mGotoNextMode);
            }
        };

        //mTimerHandler.postDelayed(mGotoNextMode, 8000);
    }

    /**
     * update
     * 毎フレームの更新処理
     */
    public void update(MotionEvent motionEvent) {
        // オブジェクト更新処理
        mEnemyManager.update(System.currentTimeMillis());
        mEnemyList = mEnemyManager.getEnemyList();
        mPlayer.update();
        for (Bullet bullet : mBulletList) {
            bullet.update();
        }
        for (Enemy enemy : mEnemyList) {
            enemy.update();
        }

        // オブジェクトあたり処理
        for (Enemy enemy : mEnemyList) {
            for (Bullet bullet : mBulletList) {
                if (enemy.isHitCircleToCircle(bullet)) {
                    enemy.hit();
                    bullet.hit();
                }
            }
        }

        // タッチ処理
        if (motionEvent != null) {
            float touchX = motionEvent.getX();
            float touchY = motionEvent.getY();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mTouchX = touchX;
                mTouchY = touchY;
                fire(mTouchX, mTouchY);
            }
        }
    }

    /**
     * draw
     * 毎フレームの描画処理
     * @param canvas
     */
    public void draw(Canvas canvas) {
        // 背景描画
        canvas.drawBitmap(mBitmapBackground, 0, 0, mPaintBackground);

        // UI描画

        // オブジェクト描画
        mPlayer.draw(canvas);
        for (Bullet bullet : mBulletList) {
            bullet.draw(canvas);
        }
        for (Enemy enemy : mEnemyList) {
            enemy.draw(canvas);
        }

        // デバッグ
        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(30);
        int h = GameView.GAME_HEIGHT;
        canvas.drawText(String.format("TouchX : %f\tTouchY : %f", mTouchX, mTouchY), 0, h - (h / 3), paint);
    }


    class MyButton {
        private final Paint mRectPaint;
        private Rect mRect;
        private int mWidth;
        private int mHeight;

        public MyButton(int x, int y) {
            mRectPaint = new Paint(Color.BLUE);
            mWidth = 100;
            mHeight = 100;
            this.mRect = new Rect(x, y, x+mWidth, y+mHeight);
        }

        public void draw(Canvas canvas) {
            canvas.drawRect(mRect, mRectPaint);
        }
    }
    GameMode.MyButton mMyButton;


    public GameMode() {
//        paintText = new Paint();
//        paintText.setColor(Color.RED);
//        paintText.setTextSize(25);
//        paintText.setTextAlign(Paint.Align.CENTER);
//        paintBackground = new Paint();
//        paintBackground.setColor(Color.argb(255, 255, 153, 255));
//
//        init();
    }

    // 更新処理
    public void update(Canvas canvas, MotionEvent motionEvent, GameView.Mode currentMode) {
//
//        // オブジェクトの初期化処理
//        if (mMyButton == null) {
//            int x = canvas.getWidth() - (100 * 2);
//            int y = canvas.getHeight() - (100 * 2);
//            mMyButton = new GameMode.MyButton(x, y - (canvas.getHeight() / 4));
//        }
//
//        // ゲームモードじゃない場合はこうしん処理はスキップする
//        if (currentMode != mCurrentMode) {
//            return;
//        }
//
//        // タッチ処理
//        if (motionEvent != null) {
//            float touchX = motionEvent.getX();
//            float touchY = motionEvent.getY();
//            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                mTouchX = touchX;
//                mTouchY = touchY;
//                if (mMyButton.mRect.contains((int) mTouchX, (int) mTouchY)) {
//                    mNextMode = GameView.Mode.OVER;
//                }
//            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                mTouchX = touchX;
//                mTouchY = touchY;
//            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
//                mTouchX = touchX;
//                mTouchY = touchY;
//            }
//        }
    }

    /**
     * プレイヤーのショット処理
     * @param x
     * @param y
     */
    private void fire(float x, float y) {
        float alignX = (x - mPlayer.mRect.centerX()) / Math.abs(y - mPlayer.mRect.centerY());

        Bullet bullet = new Bullet(mBitmapBullet);
        bullet.set(mPlayer.mRect.centerX(), mPlayer.mRect.centerY(), alignX);
        mBulletList.add(0, bullet);
    }


    /**
     * 指定した画像イメージを取得する
     * @param context
     * @param filePath
     * @return bitmap image
     */
    public static Bitmap getBitmapImageToAssets(Context context, String filePath) {
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
}
