package muryshkin.alexey.pdd.Data;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 123 on 7/7/2016.
 */
public class DataHolder {

    private static DataHolder dataHolder;

    public static DataHolder getDataHolder() {
       if (dataHolder == null)
           dataHolder = new DataHolder();
        return dataHolder;
    }

    public boolean isLoggedIn;

    public List<JSONObject> pdd;
    public List<JSONObject> signs;

    public List<Integer> answers;
    public List<Integer> correctAnswers;
    public int result;
    public int answered;

    public List<Test> bTests;
    public List<Integer> maxCorrect;
    public JSONObject test;

    public JSONObject chosenArticle;

    public DataHolder() {
        isLoggedIn = false;

        chosenArticle = null;

        maxCorrect = new ArrayList<>();
        bTests = new ArrayList<>();
        test = null;

        correctAnswers = new LinkedList<>();
        answers = new LinkedList<>();
        result = 0;
        answered = 0;

        pdd = new LinkedList<>();
        getData("pdd");

        signs = new LinkedList<>();
        getData("signs");
    }

    private void getData(String fileName) {

        String json;

        AssetManager assetManager = Const.context.getAssets();

        try {
            InputStream inputStream = assetManager.open(fileName + ".json", Const.context.MODE_PRIVATE);
            int size = inputStream.available();

            byte[] buffer = new byte[size];
            inputStream.read(buffer);

            inputStream.close();

            json = new String(buffer, "UTF-8");
            JSONArray data = new JSONArray(json);

            requestHelper(data, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestHelper(JSONArray data, String fileName) {

        for (int i = 0; i < data.length(); i++)
            try {
                if (fileName.equals("pdd"))
                    pdd.add(data.getJSONObject(i));
                else if (fileName.equals("signs"))
                    signs.add(data.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
}
