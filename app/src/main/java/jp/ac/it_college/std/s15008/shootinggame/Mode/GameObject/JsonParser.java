package jp.ac.it_college.std.s15008.shootinggame.Mode.GameObject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s194964 on 17/01/18.
 */

public class JsonParser {

    public static class EnemyData {
        public int mType;
        public float mX;
        public float mY;
        public long mLaunchTime;
    }

    public static List<EnemyData> parseToEnemyDataList(String jsonString) {

        List<EnemyData> enemyDataList = new ArrayList<>();

        try {
            JSONArray responseArray = new JSONArray(jsonString);
            for (int i = 0; i < responseArray.length(); i++) {
                EnemyData enemyData = new EnemyData();
                enemyData.mType = responseArray.getJSONObject(i).getInt("type");
                enemyData.mX = (float) responseArray.getJSONObject(i).getDouble("x");
                enemyData.mY = (float) responseArray.getJSONObject(i).getDouble("y");
                enemyData.mLaunchTime = responseArray.getJSONObject(i).getLong("launch");
                enemyDataList.add(enemyData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return enemyDataList;
    }
}
