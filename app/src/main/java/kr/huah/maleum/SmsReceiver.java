package kr.huah.maleum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // parseSms(context, intent);
    }

    void parseSms(Context context, Intent intent) {
        // Get the SMS message received
        final Bundle bundle = intent.getExtras();
        if (bundle != null) {
            // A PDU is a "protocol data unit". This is the industrial standard for SMS message
            final Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (int i = 0; pdusObj != null && i < pdusObj.length; i++) {
                // This will create an SmsMessage object from the received pdu

                SmsMessage sms = null;

//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//                    sms = SmsMessage.createFromPdu((byte[]) pdusObj[i], "3gpp2");
//                } else {
//                    sms = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
//                }
                sms = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                // Get sender phone number
                String number = sms.getDisplayOriginatingAddress();
                String message = sms.getDisplayMessageBody();
                Toast.makeText(context, "sms : " + message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
