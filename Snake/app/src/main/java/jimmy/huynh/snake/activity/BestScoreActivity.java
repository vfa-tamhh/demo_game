package jimmy.huynh.snake.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.nifcloud.mbaas.core.DoneCallback;
import com.nifcloud.mbaas.core.FindCallback;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBObject;
import com.nifcloud.mbaas.core.NCMBQuery;
import com.nifcloud.mbaas.core.NCMBUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jimmy.huynh.snake.Game;
import jimmy.huynh.snake.R;
import jimmy.huynh.snake.adapter.GameAdapter;
import jimmy.huynh.snake.app.Prefs;

public class BestScoreActivity extends AppCompatActivity {

    private GameAdapter gameAdapter;
    @BindView(R.id.rc_view)
    RecyclerView rc_score;
    List<Game> games = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_score);
        ButterKnife.bind(this);
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
        final List<Game> gameList = new ArrayList<>();
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("GameScore");
        NCMBUser ncmbUser = Prefs.with(getApplicationContext()).getUser();
        query.whereEqualTo("name", ncmbUser.getObjectId());
        query.addOrderByDescending("score");

        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> results, NCMBException e) {
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
                    initView();
                }
            }
        });
    }
}
