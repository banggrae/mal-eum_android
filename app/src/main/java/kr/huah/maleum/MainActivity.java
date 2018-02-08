package kr.huah.maleum;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

    BottomNavigationView mNavigation;
    Map<String, String> mHeaders = new HashMap<String, String>();
    private WebView myWebView;
    private TextView mTextMessage;
    private boolean mSigned;
    private String mDeviceId;
    private String mDomain = "https://www.mal-eum.com/";
    // private String mDomain = "http://10.10.131.24:8090/";
    // private String mDomain = "http://192.168.0.8:8090/";
    // private String mDomain = "http://192.168.0.2:8090/";
    // private String mDomain = "http://10.10.121.53:8090/";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    myWebView.loadUrl(mDomain + "mypage", mHeaders);
                    return true;
                case R.id.navigation_home:
                    myWebView.loadUrl(mDomain, mHeaders);
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

        String message = getIntent().getStringExtra("message");
        String gcm = getIntent().getStringExtra("gcm");
        if (message != null) {
            myWebView.loadUrl(mDomain + "mypage?message=" + message, mHeaders);
        } else if ("smp".equals(gcm)) {
            myWebView.loadUrl(mDomain + "smp/trend", mHeaders);
        } else if ("rec".equals(gcm)) {
            myWebView.loadUrl(mDomain + "rec/trend", mHeaders);
        } else if ("main".equals(gcm)) {
            myWebView.loadUrl(mDomain, mHeaders);
        } else {
            myWebView.loadUrl(mDomain + "auth", mHeaders);
        }
        myWebView.addJavascriptInterface(new WebAppInterface(this), WebAppInterface.NAME);

        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigation.setVisibility(View.GONE);
        saveDeviceId();

        // FirebaseMessaging.getInstance().subscribeToTopic("sun");
    }

    void requestReadMmsPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.RECEIVE_MMS
            }, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
                return;
            }
        }
    }

    void saveDeviceId() {
        Preference.saveDeviceId(this);
    }

    String getDeviceId() {
        return Preference.getDeviceId(this);
    }

    String getCmToken() {
        return Preference.getCmToken(this);
    }

    int getWeatherAlarm() {
        return Preference.getWeatherAlarm(this);
    }

    void setWeatherAlarm(int time) {
        Preference.setWeatherAlarm(this, time);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, WeatherAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = getPackageManager();

        if (time > 0) {
            // set alarm
            // alarmMgr.cancel(alarmIntent);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, time);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
            // enable boot receiver
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            Toast.makeText(this, "매일 " + time + "시 날씨 알람이 설정되었습니다", Toast.LENGTH_SHORT).show();
        } else {
            // cancel alarm
            alarmMgr.cancel(alarmIntent);
            // disable boot receiver
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            Toast.makeText(this, "날씨 알람이 취소되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    void setAgreeSms(boolean agree) {
        Preference.setAgreeSms(this, agree);
        if (agree) {
            requestReadMmsPermission();
        }
    }

    boolean agreedSms() {
        return Preference.agreedSms(this);
    }

    SharedPreferences getPreferences() {
        return getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }

    void setAgreeNotify(boolean agree) {
        Preference.setAgreeNotify(this, agree);
    }

    boolean agreedNotify() {
        return Preference.agreedNotify(this);
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

    public void setPlants(String id, String name) {
        // TODO
    }

    static class WebAppInterface {

        public static final String NAME = "MaleumAppJs";
        MainActivity mContext;

        WebAppInterface(MainActivity c) {
            mContext = c;
        }

        @JavascriptInterface
        public String getDeviceId() {
            return mContext.getDeviceId();
        }

        @JavascriptInterface
        public String getCmToken() {
            return mContext.getCmToken();
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
        public int getWeatherAlarm() {
            return mContext.getWeatherAlarm();
        }

        @JavascriptInterface
        public void setWeatherAlarm(int time) {
            mContext.setWeatherAlarm(time);
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
        public void setPlants(String id, String name) {
            mContext.setPlants(id, name);
        }
    }
}
