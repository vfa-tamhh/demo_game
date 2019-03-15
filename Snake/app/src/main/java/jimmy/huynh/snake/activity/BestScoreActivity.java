package jimmy.huynh.snake.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nifcloud.mbaas.core.DoneCallback;
import com.nifcloud.mbaas.core.ExecuteScriptCallback;
import com.nifcloud.mbaas.core.FindCallback;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBObject;
import com.nifcloud.mbaas.core.NCMBQuery;
import com.nifcloud.mbaas.core.NCMBScript;
import com.nifcloud.mbaas.core.NCMBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import jimmy.huynh.snake.Game;
import jimmy.huynh.snake.R;
import jimmy.huynh.snake.adapter.GameAdapter;
import jimmy.huynh.snake.app.Prefs;

public class BestScoreActivity extends AppCompatActivity implements View.OnClickListener{

    private GameAdapter gameAdapter;
    @BindView(R.id.rc_view)
    RecyclerView rc_score;
    @BindView(R.id.tv_info)
    TextView tvInfo;

    @BindView(R.id.btn_highest_score)
    Button btnHighestScore;

    @BindView(R.id.tv_high_score)
    TextView tvHighScore;

    List<Game> games = new ArrayList<>();

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_score);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");

        btnHighestScore.setOnClickListener(this);

        getScore();

    }

    private void initView() {
        GameAdapter.GameListener gameListener = new GameAdapter.GameListener() {
            @Override
            public void onSelect(View v, final int position) {
                NCMBObject obj = new NCMBObject("GameScore");
                try {
                    obj.setObjectId(games.get(position).getId());
                    obj.deleteObjectInBackground(new DoneCallback() {
                        @Override
                        public void done(NCMBException e) {
                            if (e != null) {
                                Toast.makeText(getBaseContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                games.remove(position);
                                gameAdapter.notifyDataSetChanged();
                                if (games.size()> 0) {
                                    tvInfo.setVisibility(View.GONE);
                                } else {
                                    tvInfo.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                } catch (NCMBException e) {
                    e.printStackTrace();
                }

            }
        };
        gameAdapter = new GameAdapter(games, getApplicationContext());
        gameAdapter.setListener(gameListener);
        rc_score.setAdapter(gameAdapter);
        rc_score.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    private void getScore() {
        progressDialog.show();
        final List<Game> gameList = new ArrayList<>();
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("GameScore");
        NCMBUser ncmbUser = NCMBUser.getCurrentUser();
        query.whereEqualTo("name", ncmbUser.getObjectId());
        query.addOrderByDescending("score");

        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> results, NCMBException e) {
                progressDialog.dismiss();
                if (e != null) {

                    //検索失敗時の処理
                } else {
                    for (NCMBObject ncmbObject : results) {
                        Game game = new Game();
                        game.setScore(ncmbObject.getString("score"));
                        game.setId(ncmbObject.getString("objectId"));
                        gameList.add(game);
                    }
                    games = gameList;
                    if (games.size()> 0) {
                        tvInfo.setVisibility(View.GONE);
                    } else {
                        tvInfo.setVisibility(View.VISIBLE);
                    }
                    initView();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_highest_score:
                getHighestScore();
                break;
        }
    }

    private void getHighestScore() {
        progressDialog.show();
        // 呼び出すスクリプトファイルとメソッドを指定
        NCMBScript script = new NCMBScript("testScript.js", NCMBScript.MethodType.POST);
        JSONObject body = null;
        Map<String, String> header = new HashMap<>();
        NCMBUser ncmbUser = NCMBUser.getCurrentUser();
        try {
            body = new JSONObject();
            body.put("name", ncmbUser.getObjectId());

            script.executeInBackground(header, body, null, new ExecuteScriptCallback() {
                @Override
                public void done(byte[] result, NCMBException e) {
                    progressDialog.dismiss();
                    if (e != null) {
                        // 実行失敗時の処理
                        Log.d("Error", e.getMessage());
                    } else {
                        // 実行成功時の処理
                        Log.d("Info", new String(result));
                        String jsonResult = new String(result);
                        Gson gson = new Gson();
                        Game game = gson.fromJson(jsonResult, Game.class);
                        tvHighScore.setVisibility(View.VISIBLE);
                        tvHighScore.setText(game.getScore());
                    }
                }
            });

        } catch (JSONException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }
}
