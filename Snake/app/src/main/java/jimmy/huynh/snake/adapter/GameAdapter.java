package jimmy.huynh.snake.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jimmy.huynh.snake.Game;
import jimmy.huynh.snake.R;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private Context mContext;
    private List<Game> gameList;

    public interface GameListener {
        void onSelect(View v, int position);
    }

    private GameListener listener = null;

    public GameAdapter(List<Game> games, Context context) {
        gameList = games;
        mContext = context;
    }

    public void setListener(GameListener slistener) {
        listener = slistener;
    }

    @NonNull
    @Override
    public GameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.items, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GameAdapter.ViewHolder viewHolder, int i) {
        Game game = gameList.get(i);
        TextView textView = viewHolder.score;
        Button button = viewHolder.btnDelete;
        textView.setText(game.getScore());

    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_score)
        TextView score;
        @BindView(R.id.btn_delete)
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_delete:
                    if (null != listener) {
                        listener.onSelect(view, getAdapterPosition());
                    }
                    break;
            }
        }
    }
}
