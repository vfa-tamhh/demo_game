package jimmy.huynh.snake.app;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String PRE_LOAD = "preLoad";
    private static final String PREFS_NAME = "prefs";
    private static final String IS_LOGGED = "isLoggedApp";
    private static final String USER_INFO = "userInfo";
    private static Prefs instance;
    private final SharedPreferences sharedPreferences;

    public Prefs(Context context) {

        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static Prefs with(Context context) {

        if (instance == null) {
            instance = new Prefs(context);
        }
        return instance;
    }

    public void setPreLoad(boolean totalTime) {

        sharedPreferences
                .edit()
                .putBoolean(PRE_LOAD, totalTime)
                .apply();
    }

    public boolean getPreLoad() {
        return sharedPreferences.getBoolean(PRE_LOAD, false);
    }

    public void setIsLogged(boolean value) {
        sharedPreferences
                .edit()
                .putBoolean(IS_LOGGED, value)
                .apply();
    }

    public boolean isLogged() {
        return sharedPreferences.getBoolean(IS_LOGGED, false);
    }

}
