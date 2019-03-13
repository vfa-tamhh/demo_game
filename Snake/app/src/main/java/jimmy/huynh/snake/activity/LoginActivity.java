package jimmy.huynh.snake.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nifcloud.mbaas.core.LoginCallback;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import jimmy.huynh.snake.MainActivity;
import jimmy.huynh.snake.R;
import jimmy.huynh.snake.app.Prefs;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;
    @BindView(R.id.input_email)
    EditText _email;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //Init progressDialog
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

    }

    public void login() {

        if (!validate()) {
            onLoginFailed("Invalid input.");
            return;
        }

        _loginButton.setEnabled(false);


        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _email.getText().toString();
        String password = _passwordText.getText().toString();

        if (!name.isEmpty()) {
            loginByUserName(name, password);
        } else {
            loginByEmail(email, password);
        }

    }

    public void onLoginSuccess(NCMBUser user) {
        _loginButton.setEnabled(true);
        Prefs.with(LoginActivity.this).setIsLogged(true);
//        Prefs.with(LoginActivity.this).setUserId(user);
//
//        Gson gson = new Gson();
//        String result = gson.toJson(user);
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("result", result);
        setResult(MainActivity.RESULT_OK);
        finish();
    }

    public void onLoginFailed(String error) {
        Toast.makeText(getBaseContext(), "Login failed: " + error, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _email.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() && email.isEmpty()) {
            if (name.isEmpty()) {
                _nameText.setError("enter username");
            }
            if (email.isEmpty()) {
                _email.setError("enter your email");
            }
            valid = false;
        } else {
            _nameText.setError(null);
            _email.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    //Login by username and password
    private void loginByUserName(String userName, String pass) {
        try {
            NCMBUser.loginInBackground(userName, pass, new LoginCallback() {
                @Override
                public void done(final NCMBUser user, NCMBException e) {
                    if (e != null) {
                        //エラー時の処理
                        progressDialog.dismiss();
                        onLoginFailed(e.getMessage());

                    } else {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // On complete call either onLoginSuccess or onLoginFailed
                                        onLoginSuccess(user);
                                        // onLoginFailed();
                                        progressDialog.dismiss();
                                    }
                                }, 3000);
                    }
                }
            });
        } catch (NCMBException e) {
            e.printStackTrace();
        }
    }

    //Login by email and password
    private void loginByEmail(String email, String pass) {
        NCMBUser.loginWithMailAddressInBackground(email, pass, new LoginCallback() {
            @Override
            public void done(final NCMBUser user, NCMBException e) {
                if (e != null) {
                    progressDialog.dismiss();
                    onLoginFailed(e.getMessage());
                } else {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    progressDialog.dismiss();
                                    onLoginSuccess(user);
                                }
                            }, 3000);
                }
            }
        });
    }
}
