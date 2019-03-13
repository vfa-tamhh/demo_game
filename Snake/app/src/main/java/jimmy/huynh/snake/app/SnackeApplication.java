package jimmy.huynh.snake.app;

import android.app.Application;

import com.nifcloud.mbaas.core.NCMB;

public class SnackeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NCMB.initialize(this, "APPLICATION_KEY", "CLIENT_KEY");
        NCMB.setTimeout(90000);
    }
}
