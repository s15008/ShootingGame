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
    private GameView.GameData mGameData;

    public long mStartTime;

    // デバッグ用モード遷移用
    Handler mTimerHandler;
    Runnable mGotoNextMode;

    // 背景
    private Paint mPaintBackground;
    private Bitmap mBitmapBackground;

    // エネミー残機UI
    private int mEnemyCounter;
    private static final int ENEMY_COUNTER_FRAME_X = 0;
    private static final int ENEMY_COUNTER_FRAME_Y = 0;
    private static final int ENEMY_COUNTER_FRAME_WIDTH = 264;
    private static final int ENEMY_COUNTER_FRAME_HEIGHT = 120;
    private static final int ENEMY_COUNTER_FONT_SIZE = 100;
    private static final int ENEMY_COUNTER_FRAME_LEFT_MARGIN = 40;
    private static final int ENEMY_COUNTER_FRAME_BOTTOM_MARGIN =
            (ENEMY_COUNTER_FRAME_HEIGHT - ENEMY_COUNTER_FONT_SIZE);
    private Bitmap mBitmapEnemyCounterFrame;
    private final Paint mEnemyCounterTextPaint;

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

        // エネミー残機UI
        mBitmapEnemyCounterFrame = getBitmapImageToAssets(context, "game/enemy_counter_frame.png");
        mEnemyCounterTextPaint = new Paint();
        mEnemyCounterTextPaint.setTextSize(ENEMY_COUNTER_FONT_SIZE);
        mEnemyCounterTextPaint.setColor(Color.RED);
        mEnemyCounterTextPaint.setTextAlign(Paint.Align.RIGHT);

        mEnemyList = new ArrayList<>();

        mEnemyManager = new EnemyManager(context);
    }

    /**
     * init
     * モード開始時の初期化を行う
     */
    public void init(GameView.GameData gameData) {
        // モードモードの固定
        mCurrentMode = GameView.Mode.GAME;
        mNextMode = mCurrentMode;

        // プレイヤー初期化
        mPlayer.init();
        mPlayerLeft.init();
        mPlayerRight.init();

        // エネミー生成/管理
        mGameData = gameData;
        mEnemyManager.createEnemyList(mGameData.mLevel);
        mEnemyManager.setStartTime(System.currentTimeMillis());

        mEnemyList = mEnemyManager.getEnemyList();

        // UI
        mEnemyCounter = mEnemyList.size();

        // モード遷移遅延用
        mTimerHandler = new Handler();
        mGotoNextMode = new Runnable() {
            @Override
            public void run() {
                mNextMode = GameView.Mode.OVER;
                mTimerHandler.removeCallbacks(mGotoNextMode);
            }
        };

        Log.d(TAG, String.format("LEVEL : %d\tSCORE : %d", mGameData.mLevel, mGameData.mScore));
    }

    /**
     * update
     * 毎フレームの更新処理
     */
    public void update(GameView.ScaledMotionEvent scaledMotionEvent) {
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
        // エネミーとの判定
        for (Enemy enemy : mEnemyList) {
            // 無効なエネミーはスキップする
            if (!enemy.mIsAlive || !enemy.mIsLaunched) {
                continue;
            }

            // 対弾
            for (Bullet bullet : mBulletList) {
                if (enemy.isHitCircleToCircle(bullet)) {
                    enemy.hit();
                    mEnemyCounter--;
                    mGameData.mScore += 1000;
                    bullet.hit();
                }
            }

            // 対プレイヤー
            if (enemy.isHitGround(mPlayer.mRectGround.top)) {
                mPlayer.hit();
                enemy.hit();
                mEnemyCounter--;
            }
        }

        // ゲームオーバー判定
        if (mPlayer.mLifePoint <= 0) {
            changeGameOverMode();
            return;
        }

        // ゲームクリアー判定
        if (mEnemyCounter <= 0) {
            mEnemyCounter = 0;
            changeGameClearMode();
            return;
        }

        // タッチ処理
        if (scaledMotionEvent.isTouch()) {
            float touchX = scaledMotionEvent.getX();
            float touchY = scaledMotionEvent.getY();


            if (scaledMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                fire(touchX, touchY, mPlayer);
                fire(touchX, touchY, mPlayerLeft);
                fire(touchX, touchY, mPlayerRight);
            }
        }

        // 無効な弾をリリースする
        for (int i = 0; i < mBulletList.size(); i++) {
            if (!mBulletList.get(i).mIsVisible) {
                mBulletList.remove(i);
            }
        }
        Log.d(TAG, "bullets: " + mBulletList.size());
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
        canvas.drawBitmap(mBitmapEnemyCounterFrame,
                ENEMY_COUNTER_FRAME_X, ENEMY_COUNTER_FRAME_Y, mEnemyCounterTextPaint);
        canvas.drawText(String.valueOf(mEnemyCounter),
                ENEMY_COUNTER_FRAME_X + ENEMY_COUNTER_FRAME_WIDTH - ENEMY_COUNTER_FRAME_LEFT_MARGIN,
                ENEMY_COUNTER_FRAME_Y + ENEMY_COUNTER_FRAME_HEIGHT - ENEMY_COUNTER_FRAME_BOTTOM_MARGIN,
                mEnemyCounterTextPaint);

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
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(30);
        int h = GameView.GAME_HEIGHT;
        //canvas.drawText(String.format("TouchX : %f\tTouchY : %f", mTouchX, mTouchY), 0, h - (h / 3), paint);
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
