package jimmy.huynh.snake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBUser;

import jimmy.huynh.snake.activity.BestScoreActivity;
import jimmy.huynh.snake.activity.LoginActivity;
import jimmy.huynh.snake.activity.ProfileActivity;
import jimmy.huynh.snake.activity.SnakeActivity;
import jimmy.huynh.snake.app.Prefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnNewGame, btnBestScores, btnLogout;
    private ImageView btnImgProfile;
    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNewGame = findViewById(R.id.btn_new_game);
        btnBestScores = findViewById(R.id.btn_best_scores);
        btnImgProfile = findViewById(R.id.btn_profile);
        btnLogout = findViewById(R.id.btn_logout);
        tvUserName = findViewById(R.id.tv_username);

        //Set event click
        btnNewGame.setOnClickListener(this);
        btnBestScores.setOnClickListener(this);
        btnImgProfile.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        if (!Prefs.with(this).isLogged()) {
            login();
        } else {
            //Set init view
            initWithUser(NCMBUser.getCurrentUser());
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
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void login() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    private void initWithUser(NCMBUser user) {
        if (null != user.getUserName() && null != user.getMailAddress()) {
            tvUserName.setText(user.getUserName() + " - " + user.getMailAddress());
        } else if (null != user.getUserName()) {
            tvUserName.setText(user.getUserName());
        } else {
            tvUserName.setText(user.getMailAddress());
        }
        String img = user.getString("Thumb");
        if (img != null) {
            loadImg(img);
        }
    }

    private void loadImg(String img) {
        img = img.trim();
        Glide.with(MainActivity.this).load(img)
                .placeholder(R.drawable.holder)
                .fitCenter()
                .circleCrop()
                .signature(new ObjectKey(System.currentTimeMillis()))
                .into(btnImgProfile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == MainActivity.RESULT_OK) {

                NCMBUser ncmbUser = NCMBUser.getCurrentUser();
                initWithUser(ncmbUser);
            }
            if (resultCode == MainActivity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void logout() {
        try {
            NCMBUser.logout();
            Prefs.with(getBaseContext()).setIsLogged(false);
        } catch (NCMBException e) {
            e.printStackTrace();
        }
        login();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NCMBUser.getCurrentUser() != null) {
            initWithUser(NCMBUser.getCurrentUser());
        }
    }
}
