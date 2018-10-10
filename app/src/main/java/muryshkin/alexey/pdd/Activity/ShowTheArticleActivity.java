package muryshkin.alexey.pdd.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import muryshkin.alexey.pdd.Adapter.SingleArticleAdapter;
import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.R;

public class ShowTheArticleActivity extends AppCompatActivity {

    private ListView listView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_the_article);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.customized_pdd_action_bar);

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        listView = findViewById(R.id.articleListView);

        try {
            displayInfo(DataHolder.getDataHolder().chosenArticle.getJSONArray("paragraphs"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayInfo(JSONArray paragraphs) {
        SingleArticleAdapter adapter = new SingleArticleAdapter(this, paragraphs);
        listView.setAdapter(adapter);
    }

}
