package onesolution.sangh;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SendSmsActivity extends AppCompatActivity
{
    @Bind(R.id.edt_message) EditText mMessasgeEdt;
    @Bind(R.id.send_btn) Button mSendBtn;
    //    private String[] mContactNumber = {"8237519575", "8087248667", "9755718930", "7776003630"};
    private SendSMS mSendSms;

    private ArrayList<String> mContactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        ButterKnife.bind(SendSmsActivity.this);
        mSendSms = new SendSMS();
        mContactNumber = new ArrayList<>();
        mContactNumber.add("9755718930");
        mContactNumber.add("8297517595");
        mContactNumber.add("8087248667");
        mContactNumber.add("9665388603");
        mContactNumber.add("7776003630");

        mSendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                for (int i = 0; i < mContactNumber.size(); i++)
                {
                    String message = mMessasgeEdt.getText().toString();
//                    String tempMobileNumber = mContactNumber.get(i).toString();
                    boolean success = mSendSms.sendSMSMessage(mContactNumber.get(i),
                            // This is standard lorem-ipsum text, do not bother
                            // trying to wrap it, there's about 500 characters...
                            message);
                    Log.e("Status", "@@" + success + mContactNumber.get(i));
                    Toast.makeText(SendSmsActivity.this, "Message sent " + (success ? "successfully" : "unsuccessfully"), Toast.LENGTH_SHORT).show();
//                    MultipleSMS(tempMobileNumber, message);
                }
            }
        });
    }

    private void MultipleSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0, Intent arg1)
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        ContentValues values = new ContentValues();
                        for (int i = 0; i < mContactNumber.size() - 1; i++)
                        {
                            values.put("address", mContactNumber.get(i).toString());
                            // txtPhoneNo.getText().toString());
                            values.put("body", mMessasgeEdt.getText().toString());
                        }
                        getContentResolver().insert(
                                Uri.parse("content://sms/sent"), values);
                        Toast.makeText(SendSmsActivity.this, "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(SendSmsActivity.this, "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(SendSmsActivity.this, "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(SendSmsActivity.this, "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(SendSmsActivity.this, "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0, Intent arg1)
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(SendSmsActivity.this, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(SendSmsActivity.this, "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }


}
