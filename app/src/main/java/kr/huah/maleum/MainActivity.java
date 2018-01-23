package kr.huah.maleum;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private WebView myWebView;
    private TextView mTextMessage;
    private boolean mSigned;
    private String mDeviceId;
    BottomNavigationView mNavigation;

    // private String mDomain = "https://www.mal-eum.com/";
    // private String mDomain = "http://10.10.131.24:8090/";
    // private String mDomain = "http://192.168.0.8:8090/";
    // private String mDomain = "http://192.168.0.2:8090/";
    private String mDomain = "http://10.10.121.53:8090/";
    Map<String, String> mHeaders = new HashMap<String, String>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    myWebView.loadUrl(mDomain, mHeaders);
                    return true;
                case R.id.navigation_dashboard:
                    myWebView.loadUrl(mDomain + "qna", mHeaders);
                    return true;
                case R.id.navigation_notifications:
                    myWebView.loadUrl(mDomain + "settings", mHeaders);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        final Activity activity = this;
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        myWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        mHeaders.put("from", "app");
        myWebView.loadUrl(mDomain + "auth", mHeaders);
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        // mTextMessage = (TextView) findViewById(R.id.message);
        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigation.setVisibility(View.GONE);
        saveDeviceId();
    }

    void saveDeviceId() {
        if (TextUtils.isEmpty(getDeviceId())) {
            mDeviceId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            SharedPreferences pf = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = pf.edit();
            editor.putString("deviceId", mDeviceId);
            editor.apply();
        }
    }

    String getDeviceId() {
        SharedPreferences pf = getPreferences(MODE_PRIVATE);
        return pf.getString("deviceId", "");
    }

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    void setWeatherAlarm(int time) {
        SharedPreferences pf = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pf.edit();
        editor.putInt("alarmTime", time);
        editor.apply();

        if (time > 0) {
            // set alarm
            alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, WeatherAlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, time);

            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        } else {
            // cancel alarm

        }
    }

    int getWeatherAlarmTime() {
        SharedPreferences pf = getPreferences(MODE_PRIVATE);
        return pf.getInt("alarmTime", 0);
    }

    void setAgreeSms(boolean agree) {
        SharedPreferences pf = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pf.edit();
        editor.putBoolean("agreeSms", agree);
        editor.apply();
    }

    boolean agreedSms() {
        SharedPreferences pf = getPreferences(MODE_PRIVATE);
        return pf.getBoolean("agreeSms", false);
    }

    void setAgreeNotify(boolean agree) {
        SharedPreferences pf = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pf.edit();
        editor.putBoolean("agreeNotify", agree);
        editor.apply();
    }

    boolean agreedNotify() {
        SharedPreferences pf = getPreferences(MODE_PRIVATE);
        return pf.getBoolean("agreeNotify", false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            if (mDomain.equals(myWebView.getUrl())) {
                return super.onKeyDown(keyCode, event);
            } else {
                myWebView.loadUrl(mDomain);
                return true;
            }
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    public void showBottomNavigation(boolean show) {
        if (show) {
            mNavigation.post(new Runnable() {
                @Override
                public void run() {
                    mNavigation.setVisibility(View.VISIBLE);
                }
            });
        } else {
            mNavigation.post(new Runnable() {
                @Override
                public void run() {
                    mNavigation.setVisibility(View.GONE);
                }
            });
        }
    }

    static class WebAppInterface {

        MainActivity mContext;

        WebAppInterface(MainActivity c) {
            mContext = c;
        }

        @JavascriptInterface
        public String getDeviceId() {
            return mContext.getDeviceId();
        }

        @JavascriptInterface
        public void setSigned(boolean signed) {
            mContext.mSigned = signed;
            mContext.showBottomNavigation(signed);
        }

        @JavascriptInterface
        public void showToast(String msg) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public int getWeatherAlarmTime() {
            return mContext.getWeatherAlarmTime();
        }

        @JavascriptInterface
        public boolean agreedSms() {
            return mContext.agreedSms();
        }

        @JavascriptInterface
        public boolean agreedNotify() {
            return mContext.agreedNotify();
        }

        @JavascriptInterface
        public void setAgreeSms(boolean agree) {
            mContext.setAgreeSms(agree);

        }

        @JavascriptInterface
        public void setAgreeNotify(boolean agree) {
            mContext.setAgreeNotify(agree);
        }

        @JavascriptInterface
        public void setWeatherAlarm(int time) {
            mContext.setWeatherAlarm(time);
        }
    }
}
