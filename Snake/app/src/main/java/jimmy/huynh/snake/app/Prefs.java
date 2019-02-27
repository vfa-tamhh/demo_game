package jimmy.huynh.snake.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nifcloud.mbaas.core.NCMBUser;

public class Prefs {
    private static final String PRE_LOAD = "preLoad";
    private static final String PREFS_NAME = "prefs";
    private static final String IS_LOGGED = "isLogged";
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

    public void setUserId(NCMBUser user) {
        Gson gson = new Gson();
        String sUser = gson.toJson(user);

        sharedPreferences
                .edit()
                .putString(USER_INFO, sUser)
                .apply();
    }

    public NCMBUser getUser() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(USER_INFO, "");
        NCMBUser ncmbUser = gson.fromJson(json, NCMBUser.class);
        return ncmbUser;
    }
}
