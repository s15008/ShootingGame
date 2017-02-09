package jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import jp.ac.it_college.std.s15008.shootinggame.Mode.GameMode;

/**
 * Created by s15008 on 17/02/07.
 */

public class EnemyManager {
    private static final String TAG = "EnemyManager";

    private static final int EN_MAX_LEVEL_1 = 10;

    private List<Enemy> mEnemyList;
    private Bitmap mBitmapEnemyCarrot;

    public EnemyManager(Context context) {
        mEnemyList = new ArrayList<>();
        mBitmapEnemyCarrot = GameMode.getBitmapImageToAssets(context, "game/enemy_carrot.png");
    }

    public void createEnemyList(int level) {
        if (level == 1) {
            for (int i = 0; i < EN_MAX_LEVEL_1; i++) {
                Enemy enemy = new Enemy(mBitmapEnemyCarrot);
                mEnemyList.add(0, enemy);
            }
        }
    }

    public List<Enemy> getEnemyList() {
        return mEnemyList;
    }

    void readyEnemyList(int num) {
        for (int i = 0; i < num; i++) {
            Enemy enemy = null;
            for (Enemy en : mEnemyList) {
                if (!en.mIsVisible) {
                    enemy = en;
                    break;
                }
            }
            if (enemy == null) {
                enemy = new Enemy(mBitmapEnemyCarrot);
                mEnemyList.add(0, enemy);
            }
        }
    }
}

//      Enemy enemy = null;
//      for (Enemy en : mEnemyList) {
//        if (!en.mIsVisible) {
//          enemy = en;
//          break;
//        }
//       }
//        if (enemy == null) {
//        enemy = new Enemy(mBitmapEnemy);
//        mEnemyList.add(0, enemy);
//        }
//        enemy.set(x, 0, alignX);