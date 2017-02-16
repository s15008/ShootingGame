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
import android.util.Log;
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
    private int mCurrentLevel;

    public long mStartTime;

    // デバッグ用モード遷移用
    Handler mTimerHandler;
    Runnable mGotoNextMode;

    // 背景
    private Paint mPaintBackground;
    private Bitmap mBitmapBackground;

    // キャラクターオブジェクト
    private Player mPlayer;
    private Player mPlayerLeft;
    private Player mPlayerRight;
    private List<Bullet> mBulletList;
    private Bitmap mBitmapBullet;
    private List<Enemy> mEnemyList;
    private EnemyManager mEnemyManager;

    // デバッグ用
    private float mTouchX =  0f;
    private float mTouchY = 0f;

    public GameMode(Context context) {
        mCurrentMode = GameView.Mode.INIT;
        mNextMode = mCurrentMode;

        mPaintBackground = new Paint();
        mBitmapBackground = getBitmapImageToAssets(context, "game/background_starry_sky.png");

        Bitmap bitmap = getBitmapImageToAssets(context, "game/player_octopus_vase.png");
        mPlayer = new Player(bitmap, true);
        mPlayerLeft = new Player(bitmap, false);
        mPlayerLeft.set(Player.LEFT_X, Player.LEFT_Y);
        mPlayerRight = new Player(bitmap, false);
        mPlayerRight.set(Player.RIGHT_X, Player.RIGHT_Y);

        mBulletList = new ArrayList<>();
        mBitmapBullet = getBitmapImageToAssets(context, "game/bullet.png");

        mEnemyList = new ArrayList<>();

        mEnemyManager = new EnemyManager(context);
    }

    /**
     * init
     * モード開始時の初期化を行う
     */
    public void init(int currentLevel) {
        // モードモードの固定
        mCurrentMode = GameView.Mode.GAME;
        mNextMode = mCurrentMode;

        // プレイヤー初期化
        mPlayer.init();
        mPlayerLeft.init();
        mPlayerRight.init();

        // エネミー生成/管理
        mCurrentLevel = currentLevel;
        mEnemyManager.createEnemyList(mCurrentLevel);
        mEnemyManager.setStartTime(System.currentTimeMillis());

        mEnemyList = mEnemyManager.getEnemyList();

        // モード遷移遅延用
        mTimerHandler = new Handler();
        mGotoNextMode = new Runnable() {
            @Override
            public void run() {
                mNextMode = GameView.Mode.OVER;
                mTimerHandler.removeCallbacks(mGotoNextMode);
            }
        };
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
            // 無効なエネミーはスキップする
            if (!enemy.mIsAlive || !enemy.mIsLaunched) {
                continue;
            }

            // 対弾
            for (Bullet bullet : mBulletList) {
                if (enemy.isHitCircleToCircle(bullet)) {
                    enemy.hit();
                    bullet.hit();
                }
            }

            // 対プレイヤー
            if (enemy.isHitGround(mPlayer.mRectGround.top)) {
                mPlayer.hit();
                enemy.hit();
                if (mPlayer.mLifePoint <= 0) {
                    Log.d(TAG, "LifePoint: " + mPlayer.mLifePoint);
                    changeGameOverMode();
                }
            }
        }

        int enemyCount = mEnemyList.size();
        for (Enemy enemy : mEnemyList) {
            if (enemy.mIsLaunched && !enemy.mIsAlive) {
                enemyCount--;
                if (enemyCount <= 0) {
                    changeGameClearMode();
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
                fire(mTouchX, mTouchY, mPlayer);
                fire(mTouchX, mTouchY, mPlayerLeft);
                fire(mTouchX, mTouchY, mPlayerRight);
            }
        }

        // 無効な弾をリリースする
        for (int i = 0; i < mBulletList.size(); i++) {
            if (!mBulletList.get(i).mIsVisible) {
                mBulletList.remove(i);
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
        mPlayerLeft.draw(canvas);
        mPlayerRight.draw(canvas);
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

        int enemyCount = mEnemyList.size();
        for (Enemy enemy : mEnemyList) {
            if (enemy.mIsLaunched && !enemy.mIsAlive) {
                enemyCount--;
            }
        }
        canvas.drawText(String.format("ENEMY : %d", enemyCount), 0, paint.getTextSize(), paint);
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

    /**
     * プレイヤーのショット処理
     * @param x
     * @param y
     */
    private void fire(float x, float y, Player player) {
        float alignX = (x - player.mRect.centerX()) / Math.abs(y - player.mRect.centerY());

        Bullet bullet = new Bullet(mBitmapBullet);
        bullet.set(player.mRect.centerX(), player.mRect.centerY() - 10, alignX);
        mBulletList.add(0, bullet);

        // 砲塔角度調整
        player.setAngle(x, y);
    }

    private void changeGameOverMode() {
        mTimerHandler.post(mGotoNextMode);
    }

    private void changeGameClearMode() {
        mNextMode = GameView.Mode.CLEAR;
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
