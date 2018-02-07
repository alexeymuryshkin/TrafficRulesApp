package muryshkin.alexey.pdd.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserTokenStorageFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import muryshkin.alexey.pdd.Data.Const;
import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.Helper.ClearCache;
import muryshkin.alexey.pdd.R;
import muryshkin.alexey.pdd.Adapter.SearchResultsAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button theoryButton;
    private Button testButton;
    private ImageButton profileButton;

    private ProgressBar myProgressBar;
    private LinearLayout searchCoverLinearLayout;
    private LinearLayout mainLinearLayout;
    private RelativeLayout preferencesRelativeLayout;
    private RelativeLayout searchRelativeLayout;

    private EditText searchEditText;
    private ListView searchResultListView;
    private List<Pair<JSONObject, Integer>> entries;
    private TextView coincidenceTextView;
    private ImageButton deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Backendless.initApp(this, Const.APP_ID, Const.ANDROID_KEY, Const.APP_VERSION);
        Const.context = getApplicationContext();

        theoryButton = (Button) findViewById(R.id.theoryButton);
        testButton = (Button) findViewById(R.id.testButton);
        profileButton = (ImageButton) findViewById(R.id.profileButton);
        theoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTheoryClick();
            }
        });
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTestClick();
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DataHolder.getDataHolder().isLoggedIn)
                    startLoginActivity();
                else
                    startProfileActivity();
            }
        });

        profileButton.setEnabled(false);
        profileButton.setVisibility(View.INVISIBLE);
        checkForLogin();

        searchCoverLinearLayout = (LinearLayout) findViewById(R.id.searchCoverLinearLayout);
        mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        preferencesRelativeLayout = (RelativeLayout) findViewById(R.id.preferencesRelativeLayout);
        searchRelativeLayout = (RelativeLayout) findViewById(R.id.searchRelativeLayout);

        coincidenceTextView = (TextView) findViewById(R.id.coincidenceTextView);

        myProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        myProgressBar.setVisibility(View.INVISIBLE);

        deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText("");
                displayInfo(new ArrayList<Pair<JSONObject, Integer>>());
                coincidenceTextView.setText("");
                setLayout(1);
            }
        });

        searchResultListView = (ListView) findViewById(R.id.searchResultListView);
        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataHolder.getDataHolder().chosenArticle = entries.get(i).first;
                showMore();
            }
        });

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    setLayout(2);
                    myProgressBar.setVisibility(View.VISIBLE);

                    try {
                        findWord(charSequence.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    displayInfo(new ArrayList<Pair<JSONObject, Integer>>());
                    coincidenceTextView.setText("");
                    setLayout(1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        setLayout(1);
    }

    private void checkForLogin() {

        String userToken = UserTokenStorageFactory.instance().getStorage().get();

        if (userToken != null && !userToken.equals("")) {
            String currentUserObjectId = Backendless.UserService.loggedInUser();
            Backendless.UserService.findById(currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser response) {
                    Backendless.UserService.setCurrentUser(response);
                    DataHolder.getDataHolder().isLoggedIn = true;
                    profileButton.setEnabled(true);
                    profileButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "Failed to find loggedIn User: " + fault.getMessage());
                    profileButton.setEnabled(true);
                    profileButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Log.e(TAG, "User is not logged in");
            profileButton.setEnabled(true);
            profileButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchEditText.getText().length() == 0)
            setLayout(1);
        else
            setLayout(2);
    }

    private void setLayout(int layout) {
        if (layout == 1) {
            searchCoverLinearLayout.setEnabled(false);
            searchCoverLinearLayout.setVisibility(View.INVISIBLE);

            mainLinearLayout.setEnabled(true);
            mainLinearLayout.setVisibility(View.VISIBLE);

            deleteButton.setEnabled(false);
            deleteButton.setVisibility(View.INVISIBLE);

            preferencesRelativeLayout.setEnabled(true);
            preferencesRelativeLayout.setVisibility(View.VISIBLE);

            searchRelativeLayout.setEnabled(true);
            searchRelativeLayout.setVisibility(View.VISIBLE);
        } else if (layout == 2) {
            searchCoverLinearLayout.setEnabled(true);
            searchCoverLinearLayout.setVisibility(View.VISIBLE);

            mainLinearLayout.setEnabled(false);
            mainLinearLayout.setVisibility(View.INVISIBLE);

            deleteButton.setEnabled(true);
            deleteButton.setVisibility(View.VISIBLE);

            preferencesRelativeLayout.setEnabled(false);
            preferencesRelativeLayout.setVisibility(View.INVISIBLE);

            searchRelativeLayout.setEnabled(true);
            searchRelativeLayout.setVisibility(View.VISIBLE);
        } else if (layout == 3) {
            searchCoverLinearLayout.setEnabled(false);
            searchCoverLinearLayout.setVisibility(View.INVISIBLE);

            mainLinearLayout.setEnabled(false);
            mainLinearLayout.setVisibility(View.INVISIBLE);

            deleteButton.setEnabled(false);
            deleteButton.setVisibility(View.INVISIBLE);

            preferencesRelativeLayout.setEnabled(false);
            preferencesRelativeLayout.setVisibility(View.INVISIBLE);

            searchRelativeLayout.setEnabled(false);
            searchRelativeLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void onTestClick() {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    private void onTheoryClick() {
        Intent intent = new Intent(this, TheoryActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void showMore() {
        Intent intent = new Intent(this, ShowTheArticleActivity.class);
        startActivity(intent);
    }

    private void findWord(String s) throws JSONException {
        entries = new ArrayList<>();

        List<JSONObject> pdd = DataHolder.getDataHolder().pdd;

        for (int i = 0; i < pdd.size(); i++) {
            JSONArray articles = pdd.get(i).getJSONArray("articles");

            for (int j = 0; j < articles.length(); j++) {
                JSONArray paragraphs = articles.getJSONObject(j).getJSONArray("paragraphs");
                int counter = 0;

                for (int k = 0; k < paragraphs.length(); k++) {
                    String paragraph = paragraphs.getJSONObject(k).getString("paragraph");
                    counter += kmp(s.toLowerCase(), paragraph.toLowerCase());
                }

                entries.add(Pair.create(articles.getJSONObject(j), counter));
            }
        }

        List<JSONObject> signs = DataHolder.getDataHolder().signs;

        for (int i = 0; i < signs.size(); i++) {
            JSONArray articles = signs.get(i).getJSONArray("articles");

            for (int j = 0; j < articles.length(); j++) {
                JSONArray paragraphs = articles.getJSONObject(j).getJSONArray("paragraphs");
                int counter = 0;

                for (int k = 0; k < paragraphs.length(); k++) {
                    String paragraph = paragraphs.getJSONObject(k).getString("paragraph");
                    Log.e(TAG, paragraph + "!!!!!!!!!!!!!!!!!!");
                    String imageURL = paragraphs.getJSONObject(k).getString("imageURL").replace('_', '.');
                    counter += kmp(s.toLowerCase(), paragraph.toLowerCase() + '@' + imageURL);
                }

                entries.add(Pair.create(articles.getJSONObject(j), counter));
            }
        }

        qsort(entries, 0, entries.size() - 1);

        for (int i = entries.size() - 1; i >= 0; i--)
            if (entries.get(i).second == 0)
                entries.remove(i);

        displayInfo(entries);
    }

    private int kmp(String s, String paragraph) {

        int ans = 0;
        String str = '@' + s + '@' + paragraph;
        List<Integer> arr = new ArrayList<>();
        arr.add(0);
        arr.add(0);

        for (int i = 2; i < str.length(); i++) {

            int j = arr.get(i - 1);

            while (j > 0 && str.charAt(j + 1) != str.charAt(i))
                j = arr.get(j);

            if (str.charAt(j + 1) == str.charAt(i))
                arr.add(j + 1);
            else
                arr.add(0);

            if (arr.get(i) == s.length())
                ans++;
        }

        return ans;
    }

    private void qsort(List<Pair<JSONObject, Integer>> entries, int l, int r) {
        int i = l;
        int j = r;
        int s = entries.get(l + (r - l) / 2).second;

        while (i <= j) {
            while (entries.get(i).second > s) i++;
            while (entries.get(j).second < s) j--;

            if (i <= j) {
                Pair<JSONObject, Integer> d = entries.get(i);
                entries.set(i, entries.get(j));
                entries.set(j, d);
                i++; j--;
            }
        }

        if (l < j) qsort(entries, l, j);
        if (i < r) qsort(entries, i, r);
    }

    private void displayInfo(List<Pair<JSONObject, Integer>> entries) {
        SearchResultsAdapter adapter = new SearchResultsAdapter(this, entries);
        searchResultListView.setAdapter(adapter);
        if (entries.size() > 0 && (entries.size() % 10 > 4 || entries.size() % 10 == 0 || (entries.size() % 100 >= 11 && entries.size() % 100 <= 14)))
            coincidenceTextView.setText("найдено " + entries.size() + " статей");
        else if (entries.size() % 10 > 1)
            coincidenceTextView.setText("найдено " + entries.size() + " статьи");
        else if (entries.size() % 10 == 1)
            coincidenceTextView.setText("найдена " + entries.size() + " статья");
        else
            coincidenceTextView.setText("Ничего не найдено");

        myProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        ClearCache.deleteCache(this);
        super.onDestroy();
    }
}
