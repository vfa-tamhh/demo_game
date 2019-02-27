package jimmy.huynh.snake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nifcloud.mbaas.core.NCMBUser;

import jimmy.huynh.snake.activity.BestScoreActivity;
import jimmy.huynh.snake.activity.LoginActivity;
import jimmy.huynh.snake.activity.SnakeActivity;
import jimmy.huynh.snake.app.Prefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnNewGame, btnBestScores, btnLogout;
    private ImageButton btnImgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNewGame = (Button) findViewById(R.id.btn_new_game);
        btnBestScores = (Button) findViewById(R.id.btn_best_scores);
        btnImgProfile = (ImageButton) findViewById(R.id.btn_profile);
        btnLogout = (Button) findViewById(R.id.btn_logout);

        //Set event click
        btnNewGame.setOnClickListener(this);
        btnBestScores.setOnClickListener(this);
        btnImgProfile.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        if (!Prefs.with(getBaseContext()).isLogged()) {
            login();
        } else {
            //Set init view
            initWithUser(Prefs.with(getBaseContext()).getUser());
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_new_game:
                newGame();
                break;
            case R.id.btn_best_scores:
                listScore();
                break;
            case R.id.btn_profile:
                profile();
                break;
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    private void newGame() {
        Intent intent = new Intent(MainActivity.this, SnakeActivity.class);
        startActivity(intent);
    }

    private void listScore() {
        Intent intent = new Intent(MainActivity.this, BestScoreActivity.class);
        startActivity(intent);
    }

    private void profile() {

    }

    private void login() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    private void initWithUser(NCMBUser user) {
        Toast.makeText(getBaseContext(), "Welcome " + user.getUserName() + " to snacke game.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == MainActivity.RESULT_OK) {
                String result = data.getStringExtra("result");
                Gson gson = new Gson();
                NCMBUser ncmbUser = gson.fromJson(result, NCMBUser.class);
                initWithUser(ncmbUser);
            }
            if (resultCode == MainActivity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void logout() {
        Prefs.with(getBaseContext()).setIsLogged(false);
        login();
    }
}
