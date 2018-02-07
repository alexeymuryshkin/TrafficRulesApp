package muryshkin.alexey.pdd.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import muryshkin.alexey.pdd.Helper.ClearCache;
import muryshkin.alexey.pdd.R;
import muryshkin.alexey.pdd.Adapter.TopicsListAdapter;

public class TheoryActivity extends AppCompatActivity {

    private ListView listView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.customized_theory_action_bar);

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        listView = (ListView) findViewById(R.id.listView);
        setListViewAdapter();
    }

    private void setListViewAdapter() {
        List<String> topics = new LinkedList<>();
        topics.add("Правила Дорожного Движения");
        topics.add("Дорожные Знаки");

        TopicsListAdapter adapter = new TopicsListAdapter(this, topics);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    runActivity(PddActivity.class);
                } else if (i == 1) {
                    runActivity(TrafficSignsActivity.class);
                }
            }
        });
    }

    private void runActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        ClearCache.deleteCache(this);
        super.onDestroy();
    }
}
