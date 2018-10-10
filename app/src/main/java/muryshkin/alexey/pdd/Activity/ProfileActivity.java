package muryshkin.alexey.pdd.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;

import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.R;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private ImageView carImageView;
    private TextView usernameTextView;
    private TextView titleTextView;
    private TextView carTextView;

    private TextView experienceTextView;
    private TextView scoreWordTextView;

    private TextView experienceLeftTextView;

    private LinearLayout favouriteLinearLayout;
    private LinearLayout mistakesLinearLayout;
    private LinearLayout logoutLinearLayout;
    private int totalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutLinearLayout = findViewById(R.id.logoutLinearLayout);
        logoutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogoutClick();
            }
        });

        usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(Backendless.UserService.CurrentUser().getProperty("name").toString());

        totalScore = (Integer) Backendless.UserService.CurrentUser().getProperty("totalScore");
        experienceTextView = findViewById(R.id.experienceTextView);
        experienceTextView.setText("" + totalScore);

        scoreWordTextView = findViewById(R.id.scoreWordTextView);
        if (totalScore % 100 != 11 && totalScore % 10 == 1)
            scoreWordTextView.setText("очко");
        else if ((totalScore % 100 < 12 || totalScore % 100 > 14) && (totalScore % 10 >= 2 && totalScore % 10 <= 4))
            scoreWordTextView.setText("очка");
        else
            scoreWordTextView.setText("очков");
    }

    private void onLogoutClick() {

        Log.d(TAG, "Logging out started!!!");

        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Log.d(TAG, "Logged out successfully!!!");
                DataHolder.getDataHolder().isLoggedIn = false;
                DataHolder.getDataHolder().maxCorrect = new ArrayList<Integer>();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Logging out failed! " + fault.getMessage());
                Toast.makeText(getApplicationContext(), "Logging Out Failed!!!", Toast.LENGTH_SHORT);
            }
        });
    }

}
