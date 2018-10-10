package muryshkin.alexey.pdd.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;

import muryshkin.alexey.pdd.Adapter.QuestionsAdapter;
import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.Fragment.QuestionFragment;
import muryshkin.alexey.pdd.R;

public class TestPlayActivity extends AppCompatActivity {

    private static final String TAG = "TestPlayActivity";

    private TextView actionBarTextView;
    private ImageButton backButton;
    private ViewPager viewPager;

    private int testPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_play);

        testPosition = getIntent().getExtras().getInt("testPosition");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.customized_test_play_action_bar);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        actionBarTextView = findViewById(R.id.actionBarTextView);
        actionBarTextView.setText(DataHolder.getDataHolder().bTests.get(testPosition).getTitle());

        viewPager = findViewById(R.id.questionsViewPager);

        try {
            displayInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayInfo() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        DataHolder.getDataHolder().answers = new LinkedList<>();
        DataHolder.getDataHolder().correctAnswers = new LinkedList<>();
        DataHolder.getDataHolder().result = 0;
        DataHolder.getDataHolder().answered = 0;

        for (int i = 0; i < DataHolder.getDataHolder().test.getJSONArray("questions").length(); i++) {
            DataHolder.getDataHolder().answers.add(100);
            DataHolder.getDataHolder().correctAnswers.add(100);
            fragments.add(QuestionFragment.newInstance(i));
        }

        QuestionsAdapter adapter = new QuestionsAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    public void setCurrentItem (final int item, final boolean smoothScroll) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        if (DataHolder.getDataHolder().answered >= DataHolder.getDataHolder().answers.size())
                            showResult();

                        if (item < DataHolder.getDataHolder().answers.size())
                            viewPager.setCurrentItem(item, smoothScroll);
                    }
                });
            }
        }, 1500);
    }

    private void showResult() {
        Intent intent = new Intent(this, TestResultActivity.class);
        intent.putExtra("position", testPosition);
        startActivity(intent);
        viewPager.setCurrentItem(0, true);
    }

}
