package jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject;

import android.graphics.Canvas;

/**
 * Created by s15008 on 17/02/06.
 */

public abstract class BaseObject {
    protected float mCenterX;
    protected float mCenterY;
    protected float mR;

    abstract void draw(Canvas canvas);
    abstract void update();

    /**
     * isHitCircleToCircle
     * 円と円での衝突判定
     * @param target 判定したいオブジェクト
     * @return 衝突していたらtrueを返す
     */
    public boolean isHitCircleToCircle(BaseObject target) {
        float a = (mCenterX - target.mCenterX) * (mCenterX - target.mCenterX);
        float b = (mCenterY - target.mCenterY) * (mCenterY - target.mCenterY);
        float c = (mR + target.mR) * (mR + target.mR);
        if (a + b <= c) {
            return true;
        }

        return false;
    }
}
