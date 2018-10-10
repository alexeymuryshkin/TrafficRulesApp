package muryshkin.alexey.pdd.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import org.json.JSONObject;

import java.util.List;

import muryshkin.alexey.pdd.Adapter.TestListViewAdapter;
import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.Data.Test;
import muryshkin.alexey.pdd.Data.UserTestRelationship;
import muryshkin.alexey.pdd.Helper.InternetHelper;
import muryshkin.alexey.pdd.R;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    private ListView listView;
    private ProgressBar myProgressBar;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.customized_test_action_bar);

        myProgressBar = findViewById(R.id.loading_spinner);
        myProgressBar.setVisibility(View.VISIBLE);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView = findViewById(R.id.testsListView);

        if (DataHolder.getDataHolder().bTests.size() == 0) {
            if (InternetHelper.isOnline())
                getData();
            else {
                Toast.makeText(this, "Проверьте Интернет Соединение",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else if (DataHolder.getDataHolder().maxCorrect.size() == 0 && DataHolder.getDataHolder().isLoggedIn) {
            if (InternetHelper.isOnline())
                getUserTestRelationships(0);
            else {
                Toast.makeText(this, "Проверьте Интернет Соединение",Toast.LENGTH_SHORT).show();
                finish();
            }
        } else
            displayInfo();
    }

    @Override
    protected void onResume() {
        listView.setVisibility(View.VISIBLE);
        listView.setEnabled(true);
        super.onResume();
    }

    private void getData() {

        Backendless.Data.of(Test.class).find(new AsyncCallback<BackendlessCollection<Test>>() {
            @Override
            public void handleResponse(BackendlessCollection<Test> response) {
                List<Test> data = response.getData();
                saveTestsToDataHolder(data);

                if (DataHolder.getDataHolder().maxCorrect.size() == 0 && DataHolder.getDataHolder().isLoggedIn)
                    getUserTestRelationships(0);
                else
                    displayInfo();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Error loading tests!!! " + fault.getMessage());
            }
        });
    }

    private void saveTestsToDataHolder(List<Test> data) {
        for (int i = data.size() - 1; i >= 0; i--)
            DataHolder.getDataHolder().bTests.add(data.get(i));
    }

    private void getUserTestRelationships(final int i) {

        if (i >= DataHolder.getDataHolder().bTests.size()) {
            displayInfo();
            return;
        }

        BackendlessUser user = Backendless.UserService.CurrentUser();

        String whereClause = "user.objectId = '" + user.getObjectId() + "'"
                + " AND " + "test.objectId = '" + DataHolder.getDataHolder().bTests.get(i).getObjectId() + "'";

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addRelated("user");
        queryOptions.addRelated("test");
        dataQuery.setQueryOptions(queryOptions);

        Backendless.Data.of(UserTestRelationship.class).find(dataQuery, new AsyncCallback<BackendlessCollection<UserTestRelationship>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserTestRelationship> response) {
                Log.d(TAG, "Search completed successfully!!!");

                if (response.getData().size() > 0)
                    DataHolder.getDataHolder().maxCorrect.add(response.getData().get(0).getMaxCorrect());
                else
                    DataHolder.getDataHolder().maxCorrect.add(-1);

                getUserTestRelationships(i + 1);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Error: " + fault.getMessage());
                DataHolder.getDataHolder().maxCorrect.add(-1);
                getUserTestRelationships(i + 1);
            }
        });
    }

    private void displayInfo() {
        TestListViewAdapter adapter = new TestListViewAdapter(this);
        listView.setAdapter(adapter);

        myProgressBar.setVisibility(View.INVISIBLE);
    }

    public void getJSONTest(final int testPosition) {

        myProgressBar.setVisibility(View.VISIBLE);
        listView.setEnabled(false);
        listView.setVisibility(View.INVISIBLE);

        String url = DataHolder.getDataHolder().bTests.get(testPosition).getTestFile();
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                DataHolder.getDataHolder().setTest(response);
                onTestClick(testPosition);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error loading test: " + error.getMessage());
            }
        });

        queue.add(request);
    }

    private void onTestClick(int testPosition) {
        Intent intent = new Intent(this, TestPlayActivity.class);
        //Intent intent = new Intent(this, TestResultActivity.class);
        intent.putExtra("testPosition", testPosition);
        this.startActivity(intent);

        myProgressBar.setVisibility(View.INVISIBLE);
    }

}
