package muryshkin.alexey.pdd.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import muryshkin.alexey.pdd.Helper.ClearCache;
import muryshkin.alexey.pdd.R;
import muryshkin.alexey.pdd.Adapter.TrafficSignsChaptersListAdapter;

public class TrafficSignsActivity extends AppCompatActivity {

    private static final String TAG = "TrafficSignsActivity";

    private ImageButton backButton;
    private ProgressBar myProgressBar;
    private ListView listView;
    private TrafficSignsChaptersListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_signs);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.customized_traffic_signs_action_bar);

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        myProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        myProgressBar.setVisibility(View.VISIBLE);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onTitleClick(i);
            }
        });

        displayInfo();
    }

    private void onTitleClick(int i) {
        Intent intent = new Intent(this, MoreInformationActivity.class);
        intent.putExtra("position", i);
        intent.putExtra("from", "TrafficSignsActivity");
        startActivity(intent);
    }

    private void displayInfo() {
        adapter = new TrafficSignsChaptersListAdapter(this);
        listView.setAdapter(adapter);

        myProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        ClearCache.deleteCache(this);
        super.onDestroy();
    }
}
