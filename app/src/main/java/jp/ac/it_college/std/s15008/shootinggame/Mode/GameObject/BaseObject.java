package jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject;

import android.graphics.Canvas;

/**
 * Created by s15008 on 17/02/06.
 */

public abstract class BaseObject {
    protected float mCenterX;
    protected float mCenterY;

    abstract void draw(Canvas canvas);
    abstract void update();
}
