package kr.huah.maleum;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by banggrae on 2018. 1. 24..
 */

public class CMInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("InstanceIDService", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        // TODO sendRegistrationToServer(refreshedToken);
        saveToken(refreshedToken);
        updateToken(refreshedToken);
    }

    void updateToken(String token) {
        SharedPreferences pf = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        if (pf.getBoolean("agreeNotify", false)) {
            // TODO update token on Server
        }
    }

    void saveToken(String token) {
        SharedPreferences pf = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = pf.edit();
        editor.putString("messageToken", token);
        editor.apply();
    }
}
