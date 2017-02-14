package jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.ac.it_college.std.s15008.shootinggame.GameView;
import jp.ac.it_college.std.s15008.shootinggame.Mode.GameMode;

/**
 * Created by s15008 on 17/02/07.
 */

public class EnemyManager {
    private static final String TAG = "EnemyManager";

    private Context mContext;
    private long mStartTime;

    private List<Enemy> mEnemyList;
    private Bitmap mBitmapEnemyCarrot;


    public EnemyManager(Context context) {
        mContext = context;
        mEnemyList = new ArrayList<>();
        mBitmapEnemyCarrot = GameMode.getBitmapImageToAssets(context, "game/enemy_carrot.png");
    }

    public void update(long currentTime) {
        long elapsedTime = currentTime - mStartTime;

        for (Enemy enemy : mEnemyList) {
            if (!enemy.mIsLaunched) {
                if (enemy.mLaunchTime < elapsedTime) {
                    enemy.launch();
                }
            }
        }
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public void createEnemyList(int level) {
        // ステージデータの読み込み
        String fileName = String.format("stage_data/stage%d.json", level);
        String text = null;
        try {
            text = loadTextAsset(fileName, mContext);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // エネミーデータへの変換
        List<JsonParser.EnemyData> enemyDataList = JsonParser.parseToEnemyDataList(text);
        Log.d(TAG, enemyDataList.toString());

        // エネミーオブジェクトの生成
        mEnemyList.clear();
        for (JsonParser.EnemyData enemyData : enemyDataList) {
            Enemy enemy = new Enemy(mBitmapEnemyCarrot);
            int type = enemyData.mType;
            float x = enemyData.mX;
            float y = enemyData.mY;
            long launch = enemyData.mLaunchTime;
            enemy.set(type, x, y, launch, 0);
            mEnemyList.add(enemy);
        }
    }

    public List<Enemy> getEnemyList() {
        return mEnemyList;
    }

    //設定値
    private static final String DEFAULT_ENCORDING = "UTF-8";

    //assets フォルダから、テキストファイルを読み込む(Android 用)
    public static final String loadTextAsset(String fileName, Context context) throws IOException {
        final AssetManager assetManager = context.getAssets();
        InputStream is = assetManager.open(fileName);
        return loadText(is, DEFAULT_ENCORDING);
    }

    //設定値
    private static final int DEFAULT_READ_LENGTH = 8192;

    //ストリームから読み込み、バイト配列で返す
    public static final byte[] readStream(InputStream inputStream, int readLength) throws IOException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream(readLength);
        final byte[] bytes = new byte[readLength];
        final BufferedInputStream bis = new BufferedInputStream(inputStream, readLength);

        try {
            int len = 0;
            while ((len = bis.read(bytes, 0, readLength)) > 0) {
                byteStream.write(bytes, 0, len);
            }
            return byteStream.toByteArray();

        } finally {
            try {
                byteStream.reset();
                bis.close();
            } catch (Exception e) {
                //IOException
            }
        }
    }

    //ストリームから読み込み、テキストエンコードして返す
    public static final String loadText(InputStream inputStream, String charsetName)
            throws IOException, UnsupportedEncodingException {
        return new String(readStream(inputStream, DEFAULT_READ_LENGTH), charsetName);
    }
}