package muryshkin.alexey.pdd.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import muryshkin.alexey.pdd.Data.Const;
import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.R;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout loginRelativeLayout;
    private RelativeLayout registrationRelativeLayout;

    private ImageView appImageView;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registrationTextView;

    private String email;
    private String username;
    private String password;
    private String retryPassword;

    private ImageButton backImageButton;
    private Button registrationButton;
    private EditText usernameRegEditText;
    private EditText emailRegEditText;
    private EditText passwordRegEditText;
    private EditText retryPasswordRegEditText;
    private ImageView appRegImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Backendless.initApp(this, Const.APP_ID, Const.ANDROID_KEY, Const.APP_VERSION);

        loginRelativeLayout = findViewById(R.id.loginRelativeLayout);
        registrationRelativeLayout = findViewById(R.id.registationRelativeLayout);

        setLayout(1);

        appImageView = findViewById(R.id.appImageView);
        appImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAppImageClick();
            }
        });

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (username == "" || password == "")
                    Toast.makeText(getApplicationContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                else
                    onLoginClick();
            }
        });

        registrationTextView = findViewById(R.id.registrationTextView);
        registrationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayout(2);
            }
        });

        backImageButton = findViewById(R.id.backImageButton);
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayout(1);
            }
        });

        appRegImageView = findViewById(R.id.appImageView);
        appRegImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAppImageClick();
            }
        });
        
        usernameRegEditText = findViewById(R.id.usernameRegEditText);
        emailRegEditText = findViewById(R.id.emailRegEditText);
        passwordRegEditText = findViewById(R.id.passwordRegEditText);
        retryPasswordRegEditText = findViewById(R.id.retryPasswordRegEditText);

        registrationButton = findViewById(R.id.registrationButton);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameRegEditText.getText().toString();
                email = emailRegEditText.getText().toString();
                password = passwordRegEditText.getText().toString();
                retryPassword = retryPasswordRegEditText.getText().toString();
                if (username == "" || email == "" || password == "" || retryPassword == "")
                    Toast.makeText(getApplicationContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                else if (!password.equals(retryPassword))
                    Toast.makeText(getApplicationContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                else
                    onRegistrationClick();
            }
        });
    }

    private void onRegistrationClick() {

        BackendlessUser user = new BackendlessUser();
        user.setProperty("email", email);
        user.setProperty("name", username);
        user.setPassword(password);

        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>()
        {
            public void handleResponse(BackendlessUser registeredUser)
            {
                Toast.makeText(getApplicationContext(), "Вы успешно зарегистрированы", Toast.LENGTH_SHORT).show();
                onLoginClick();
            }

            public void handleFault(BackendlessFault fault)
            {
                Toast.makeText(getApplicationContext(), "Ошибка: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onLoginClick() {
        Backendless.UserService.login(username, password, new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser user) {
                Toast.makeText(getApplicationContext(), "Вы вошли!", Toast.LENGTH_LONG).show();
                Backendless.UserService.setCurrentUser(user);
                DataHolder.getDataHolder().isLoggedIn = true;
                startProfileActivity();
                finish();
            }

            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(), "Ошибка: " + fault.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, true);
    }

    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void onAppImageClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setLayout(int layout) {
        if (layout == 1) {
            loginRelativeLayout.setEnabled(true);
            loginRelativeLayout.setVisibility(View.VISIBLE);

            registrationRelativeLayout.setEnabled(false);
            registrationRelativeLayout.setVisibility(View.INVISIBLE);
        } else if (layout == 2) {
            loginRelativeLayout.setEnabled(false);
            loginRelativeLayout.setVisibility(View.INVISIBLE);

            registrationRelativeLayout.setEnabled(true);
            registrationRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

}
