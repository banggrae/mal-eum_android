package kr.huah.maleum;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.UUID;

/**
 * Created by banggrae on 2018. 1. 25..
 */

public class Preference {

    public static final String KEY_AGREE_SMS = "agreeSms";
    public static final String KEY_AGREE_NOTIFY = "agreeNotify";
    public static final String KEY_WEATHER_ALARM = "weatherAlarm";
    public static final String KEY_DEVICE_ID = "deviceId";
    public static final String KEY_CM_TOKEN = "messageToken";

    static final String NAME = Preference.class.getPackage().getName();

    public static boolean agreedSms(Context context) {
        SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return pf.getBoolean(KEY_AGREE_SMS, false);
    }

    public static void setAgreeSms(Context context, boolean agree) {
        SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pf.edit();
        editor.putBoolean(KEY_AGREE_SMS, agree);
        editor.apply();
        if (agree) {
            Toast.makeText(context, "한전 대금문자 활용 동의하셨습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "한전 대금문자 활용 거부하셨습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void setAgreeNotify(Context context, boolean agree) {
        SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pf.edit();
        editor.putBoolean(KEY_AGREE_NOTIFY, agree);
        editor.apply();
        if (agree) {
            Toast.makeText(context, "알림 수신 동의하셨습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "알림 수신 거부하셨습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean agreedNotify(Context context) {
        SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return pf.getBoolean(KEY_AGREE_NOTIFY, false);
    }

    public static void saveDeviceId(Context context) {
        if (TextUtils.isEmpty(getDeviceId(context))) {
            String deviceId = UUID.randomUUID().toString();
            SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pf.edit();
            editor.putString(KEY_DEVICE_ID, deviceId);
            editor.apply();
        }
    }

    public static String getDeviceId(Context context) {
        SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return pf.getString(KEY_DEVICE_ID, "");
    }

    public static String getCmToken(Context context) {
        SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        String token = pf.getString(KEY_CM_TOKEN, "");
        if (TextUtils.isEmpty(token)) {
            token = FirebaseInstanceId.getInstance().getToken();
            SharedPreferences.Editor editor = pf.edit();
            editor.putString(KEY_CM_TOKEN, token);
            editor.apply();
        }
        // Toast.makeText(this, "token : " + token, Toast.LENGTH_SHORT).show();
        return token;
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pf.edit();
        editor.putString(KEY_CM_TOKEN, token);
        editor.apply();
    }

    public static int getWeatherAlarm(Context context) {
        SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return pf.getInt(KEY_WEATHER_ALARM, 0);
    }

    public static void setWeatherAlarm(Context context, int time) {
        SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pf.edit();
        editor.putInt(KEY_WEATHER_ALARM, time);
        editor.apply();
    }
}
