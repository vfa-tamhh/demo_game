package jimmy.huynh.snake.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import jimmy.huynh.snake.R;

public class DialogConfirm {
    private Context context;
    private Dialog dialog;
    private Button btnNewGame;
    private Button btnExit;

    private boolean isShown = false;

    public interface DialogConfirmListener {
        void onNewGame();

        void onExit();
    }

    private DialogConfirmListener listener;

    public DialogConfirm(Context context, DialogConfirmListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.setTitle("");
        dialog.setCancelable(false);
        final Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.dimAmount = 0.5f;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;

        btnNewGame = (Button) dialog.findViewById(R.id.btn_new_game);
        btnExit = (Button) dialog.findViewById(R.id.btn_exit);

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isShown = false;
                listener.onNewGame();
            }
        });


        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isShown =false;
                listener.onExit();
            }
        });

        isShown = true;
        dialog.show();
    }

    public boolean getIsShown() {
        return isShown;
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
