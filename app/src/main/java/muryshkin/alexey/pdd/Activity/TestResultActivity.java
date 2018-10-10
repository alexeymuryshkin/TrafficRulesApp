package muryshkin.alexey.pdd.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.Data.Test;
import muryshkin.alexey.pdd.Data.UserTestRelationship;
import muryshkin.alexey.pdd.R;

public class TestResultActivity extends AppCompatActivity {

    private static final String TAG = "TestResultActivity";
    private TextView resultTextView;
    private ImageButton backButton;
    private int testPosition;
    private TextView actionBarTextView;
    private TextView testNumberTextView;
    private Button returnButton;
    private Button mainActivityButton;

    private ImageView firstStarImageView;
    private ImageView secondStarImageView;
    private ImageView thirdStarImageView;
    private BackendlessUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.customized_test_play_action_bar);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        testPosition = getIntent().getExtras().getInt("position");

        actionBarTextView = findViewById(R.id.actionBarTextView);
        actionBarTextView.setText(DataHolder.getDataHolder().bTests.get(testPosition).getTitle());

        testNumberTextView = findViewById(R.id.testNumberTextView);
        testNumberTextView.setText("" + (testPosition + 1));

        firstStarImageView = findViewById(R.id.firstStarImageView);
        secondStarImageView = findViewById(R.id.secondStarImageView);
        thirdStarImageView = findViewById(R.id.thirdStarImageView);

        if (DataHolder.getDataHolder().answered > 0 && DataHolder.getDataHolder().result * 100 / DataHolder.getDataHolder().answered >= 40) {
            firstStarImageView.setImageResource(getResources().getIdentifier("@drawable/yellow_star", null, getPackageName()));

            if (DataHolder.getDataHolder().result * 100 / DataHolder.getDataHolder().answered >= 60) {
                secondStarImageView.setImageResource(getResources().getIdentifier("@drawable/yellow_star", null, getPackageName()));

                if (DataHolder.getDataHolder().result * 100 / DataHolder.getDataHolder().answered >= 80) {
                    thirdStarImageView.setImageResource(getResources().getIdentifier("@drawable/yellow_star", null, getPackageName()));
                }
            }
        }

        saveResult();
        
        resultTextView = findViewById(R.id.resultTextView);

        if (DataHolder.getDataHolder().result == 1)
            resultTextView.setText(DataHolder.getDataHolder().result + " правильный ответ из " + DataHolder.getDataHolder().answers.size());
        else
            resultTextView.setText(DataHolder.getDataHolder().result + " правильных ответов из " + DataHolder.getDataHolder().answers.size());

        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mainActivityButton = findViewById(R.id.mainActivityButton);
        mainActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMainClick();
            }
        });
    }

    private void saveResult() {
        if(DataHolder.getDataHolder().isLoggedIn)
            checkForUserTestRelationShip();
        else
            Log.e(TAG, "User is not logged in");
    }

    private void checkForUserTestRelationShip() {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String testObjectId = DataHolder.getDataHolder().bTests.get(testPosition).getObjectId().toString();

        String whereClause = "user.objectId = '" + user.getObjectId() + "'"
                + " AND " + "test.objectId = '" + testObjectId + "'";

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addRelated("user");
        queryOptions.addRelated("test");
        dataQuery.setQueryOptions(queryOptions);

        Backendless.Data.of(UserTestRelationship.class).find(dataQuery, new AsyncCallback<BackendlessCollection<UserTestRelationship>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserTestRelationship> response) {
                Log.d(TAG, "UserTestRelationship is found");
                if (response.getData().size() > 0)
                    updateUserTestRelationship(response.getData().get(0));
                else
                    createNewUserTestRelationship();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "UserTestRelationship has not been found" + fault.getMessage());
            }
        });
    }

    private void updateUserTestRelationship(UserTestRelationship userTestRelationship) {

        if (DataHolder.getDataHolder().result > userTestRelationship.getMaxCorrect()) {
            userTestRelationship.setMaxCorrect(DataHolder.getDataHolder().result);
            DataHolder.getDataHolder().maxCorrect.set(testPosition, DataHolder.getDataHolder().result);
        }

        Backendless.Persistence.of(UserTestRelationship.class).save(userTestRelationship, new AsyncCallback<UserTestRelationship>() {
            @Override
            public void handleResponse(UserTestRelationship response) {
                Log.d(TAG, "Successfully updated the result!!!");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Failed to update the result!!!" + fault.getMessage());
            }
        });
    }

    private void createNewUserTestRelationship() {

        user = Backendless.UserService.CurrentUser();
        Test test = DataHolder.getDataHolder().bTests.get(testPosition);

        DataHolder.getDataHolder().maxCorrect.set(testPosition, DataHolder.getDataHolder().result);
        int maxCorrect = DataHolder.getDataHolder().result;
        UserTestRelationship userTestRelationship = new UserTestRelationship(user, test, maxCorrect, 0);

        Backendless.Persistence.of(UserTestRelationship.class).save(userTestRelationship, new AsyncCallback<UserTestRelationship>() {
            @Override
            public void handleResponse(UserTestRelationship response) {
                Log.d(TAG, "Succesfully saved result!!!");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Failed to save result!!!");
            }
        });
    }

    private void onMainClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
