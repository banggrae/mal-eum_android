package kr.huah.maleum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        findViewById(R.id.welcome_logo).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnected ) {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(WelcomeActivity.this, "인터넷을 연결해 주세요.", Toast.LENGTH_LONG).show();
                }
                finish();
            }
        }, 1000);

    }
}
