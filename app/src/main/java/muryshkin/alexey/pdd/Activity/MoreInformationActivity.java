package muryshkin.alexey.pdd.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import muryshkin.alexey.pdd.Adapter.ArticlesAdapter;
import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.R;

public class MoreInformationActivity extends AppCompatActivity {

    private static final String TAG = "MoreInformationActivity";
    private TextView testTextView;
    private ListView listView;
    private ImageButton backButton;
    private String fromActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_information);

        int position = getIntent().getExtras().getInt("position");
        fromActivity = getIntent().getExtras().getString("from");

        if (fromActivity.equals("PddActivity")) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.customized_pdd_action_bar);
        } else if (fromActivity.equals("TrafficSignsActivity")) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.customized_traffic_signs_action_bar);
        }
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        testTextView = findViewById(R.id.testTextView);

        try {
            if (fromActivity.equals("PddActivity"))
                testTextView.setText(DataHolder.getDataHolder().pdd.get(position).getString("title"));
            else if (fromActivity.equals("TrafficSignsActivity"))
                testTextView.setText(DataHolder.getDataHolder().signs.get(position).getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView = findViewById(R.id.articlesListView);

        try {
            if (fromActivity.equals("PddActivity"))
                getJSONData(DataHolder.getDataHolder().pdd.get(position).getJSONArray("articles"));
            else if (fromActivity.equals("TrafficSignsActivity"))
                getJSONData(DataHolder.getDataHolder().signs.get(position).getJSONArray("articles"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJSONData(JSONArray articles) throws JSONException {

        List<JSONObject> data = new LinkedList<>();
        List<Boolean> isDivider = new LinkedList<>();

        for (int i = 0; i < articles.length(); i++) {
            JSONArray paragraphs = articles.getJSONObject(i).getJSONArray("paragraphs");
            int length = paragraphs.length();

            for (int j = 0; j < length; j++) {
                data.add(paragraphs.getJSONObject(j));

                if (j < length - 1)
                    isDivider.add(false);
                else
                    isDivider.add(true);
            }
        }

        displayInfo(data, isDivider);
    }

    private void displayInfo(List<JSONObject> data, List<Boolean> isDivider) {
        ArticlesAdapter adapter = new ArticlesAdapter(this, data, isDivider, fromActivity);
        listView.setAdapter(adapter);
    }

}
