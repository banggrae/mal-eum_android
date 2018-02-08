package kr.huah.maleum;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.text.MessageFormat;

public class CMJobService extends JobService {

    private static final String TAG = "MyJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        parseMMS();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


    private void parseMMS() {
        ContentResolver cr = getContentResolver();
        final String[] projection = new String[]{"_id, sub, sub_cs"};
//      //  String selection = "address = ?";
        // String[] selectionArgs = new String[]{"0614703204"};
        Cursor cursor = cr.query(Telephony.Mms.CONTENT_URI, null, null, null, "_id desc limit 2");

        if (cursor.getCount() == 0) {
            cursor.close();
            return;
        }
//        mColumns = {String[53]@6041}
//        0 = "_id"
//        1 = "thread_id"
//        2 = "date"
//        3 = "date_sent"
//        4 = "msg_box"
//        5 = "read"
//        6 = "m_id"
//        7 = "sub"
//        8 = "sub_cs"
//        9 = "ct_t"
//        10 = "ct_l"
//        11 = "exp"
//        12 = "m_cls"
//        13 = "m_type"
//        14 = "v"
//        15 = "m_size"
//        16 = "pri"
//        17 = "rr"
//        18 = "rpt_a"
//        19 = "resp_st"
//        20 = "st"
//        21 = "tr_id"
//        22 = "retr_st"
//        23 = "retr_txt"
//        24 = "retr_txt_cs"
//        25 = "read_status"
//        26 = "ct_cls"
//        27 = "resp_txt"
//        28 = "d_tm"
//        29 = "d_rpt"
//        30 = "locked"
//        31 = "seen"
//        32 = "sub_id"
//        33 = "phone_id"
//        34 = "creator"
//        35 = "imsi_data"
//        36 = "group_id"
//        37 = "save_call_type"
//        38 = "msg_boxtype"
//        39 = "type"
//        40 = "address"
//        41 = "name"
//        42 = "tag"
//        43 = "tag_eng"
//        44 = "spam_report"
//        45 = "reserve_time"
//        46 = "insert_time"
//        47 = "sender_num"
//        48 = "textlink"
//        49 = "text_only"
//        50 = "c0_iei"
//        51 = "kt_tm_send_type"
//        52 = "line_address"
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndex("_id"));
        String sub = cursor.getString(cursor.getColumnIndex("sub"));
        String address = cursor.getString(cursor.getColumnIndex("address"));
        String sender_num = cursor.getString(cursor.getColumnIndex("sender_num"));
        int sub_cs = cursor.getInt(cursor.getColumnIndex("sub_cs"));
        cursor.close();

        String number = parseNumber(id);
        String msg = parseMessage(id);
//        Toast.makeText(this, "mms : " + msg, Toast.LENGTH_LONG).show();
        Log.i("MMSReceiver", "|" + number + "|" + sub
                + "|" + sub_cs + "|" + msg);
    }

    private String parseNumber(String $id) {
        String result = null;

        Uri uri = Uri.parse(MessageFormat.format("content://mms/{0}/addr", $id));
        String[] projection = new String[]{"address"};
        String selection = "msg_id = ? and type = 137";// type=137은 발신자
        String[] selectionArgs = new String[]{$id};

        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, "_id asc limit 1");

        if (cursor.getCount() == 0) {
            cursor.close();
            return result;
        }

        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("address"));
        cursor.close();

        return result;
    }

    private String parseMessage(String $id) {
        String result = null;
        String selection = "mid = ?";
        String[] selectionArgs = new String[]{$id};
        String[] columns = new String[]{"mid", "ct", "name", "text"};
        Cursor cursor = getContentResolver().query(Uri.parse("content://mms/part"), null, selection, selectionArgs, null);

//        mColumns = {String[17]@6050}
//        0 = "_id"
//        1 = "mid"
//        2 = "seq"
//        3 = "ct"
//        4 = "name"
//        5 = "chset"
//        6 = "cd"
//        7 = "fn"
//        8 = "cid"
//        9 = "cl"
//        10 = "ctt_s"
//        11 = "ctt_t"
//        12 = "_data"
//        13 = "text"
//        14 = "label_data1"
//        15 = "label_data2"
//        16 = "label_data3"

        Log.i("MMSReceiver", "|mms 메시지 갯수 : " + cursor.getCount() + "|" + cursor.getColumnNames());
        if (cursor.getCount() == 0) {
            cursor.close();
            return result;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String type = cursor.getString(cursor.getColumnIndex("ct"));
            if ("text/plain".equals(type)) {
                result = cursor.getString(cursor.getColumnIndex("text"));
                break;
            }
            cursor.moveToNext();
        }
        cursor.close();

        if (result.contains("구입대금") && result.contains("공급가액") && result.contains("부가세") && result.contains("월분"))
        {
            showNotification(result);
        }

        return result;
    }

    void showNotification(String message) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_sun)
                        .setContentTitle("한전 전력대금구입 문자 수신")
                        .setContentText("지금 발전량과 수익을 입력하시겠습니까? 여기를 꾹 눌러 주세요.");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("message", message);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        3,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(3, mBuilder.build());
    }

}
