package com.sysnetph.myapplication;

import android.app.Activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.sysnetph.mylibrary2.function.Uc3call;
import com.sysnetph.mylibrary2.function.services.Corelistub;
import com.sysnetph.mylibrary2.function.services.Registration;
import com.sysnetph.mylibrary2.function.services.Uc3Service;
import com.sysnetph.mylibrary2.function.services.Uc3listener;


import org.linphone.core.Core;
import org.linphone.core.RegistrationState;
import org.linphone.core.tools.Log;

import java.util.ArrayList;

import static android.content.Intent.ACTION_MAIN;


public class MainActivity extends Activity implements Uc3listener, Registration  {
    private ImageView mLed;
    private Corelistub mCoreListener;
    private final int ok = 1,failed = 2, progress = 3;
    private EditText mSipAddressToCall;
    private TextView address,displayName,missedcall;
   // public static CallLog mLogs;

    public static final String CHANNEL_ID = "sysnetnotifid";
    private static final String CHANNEL_NAME = "sysnetnotifname";
    private static final String CHANNEL_DESC = "sysnetnotifdesc";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mLed = findViewById(R.id.led);
        Uc3Service.getInstance().registrationListener = this;
        Uc3Service.getInstance().calllistener = this;
        address = findViewById(R.id.address);
        displayName = findViewById(R.id.MdisplayName);
        missedcall = findViewById(R.id.Missedcall);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESC;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        //status.setImageResource(getStatusIconResource(proxy.getState()));
        //status.setVisibility(View.VISIBLE);
        // Monitors the registration state of our account(s) and update the LED accordingly
//        mCoreListener = new Corelistub() {
//            @Override
//            public void onRegistrationStateChanged(Core core, ProxyConfig cfg, RegistrationState state, String message) {
//                updateLed(state);
//            }
//        };


       //Uc3call.Uc3Led(mLed);

        mSipAddressToCall = findViewById(R.id.address_to_call);
        //mSipAddressToCall.setText(""+mLogs);
        Button callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3call.call(mSipAddressToCall.getText().toString());

//                Core core = Uc3Service.getCore();
//                Address addressToCall = core.interpretUrl(mSipAddressToCall.getText().toString());
//                CallParams params = core.createCallParams(null);
//                if (addressToCall != null) {
//                    core.inviteAddressWithParams(addressToCall, params);
//                }
            }
        });

        findViewById(R.id.notifbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNotif();
            }
        });

    }






    private void displayNotif(){

        Intent landingpage = new Intent(this,MainActivity.class);
        landingpage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent landingpendingpage = PendingIntent.getActivities(this,0, new Intent[]{landingpage},PendingIntent.FLAG_UPDATE_CURRENT);

        Intent Answer = new Intent(this,CallActivity.class);
        landingpage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent LandToCallActivity = PendingIntent.getActivities(this,0, new Intent[]{Answer},PendingIntent.FLAG_UPDATE_CURRENT);

        Intent No = new Intent(this,MainActivity.class);
        landingpage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent LandToCancel = PendingIntent.getActivities(this,0, new Intent[]{No},PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder
                = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_call_black_24dp);
        mBuilder.setContentTitle("Calling");
        mBuilder.setContentText("this is text");
        mBuilder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        mBuilder.setContentIntent(landingpendingpage);
        mBuilder.setAutoCancel(true);
        mBuilder.addAction(R.drawable.ic_call_black_24dp, "Answer", LandToCallActivity);
        mBuilder.addAction(R.drawable.ic_call_black_24dp, "No", LandToCancel);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1,mBuilder.build());

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Ask runtime permissions, such as record audio and camera
        //  once the user has granted them we won't have to ask again
        checkAndRequestCallPermissions();
        Uc3Service.getCore().addListener(mCoreListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // The best way to use Core listeners in Activities is to add them in onResume
        // and to remove them in onPause
        Uc3Service.getCore().addListener(mCoreListener);

        // Manually update the LED registration state, in case it has been registered before
        // we add a chance to register the above listener
        //ProxyConfig proxy = Uc3Service.getCore().getDefaultProxyConfig();
        //address.setText(proxy.getIdentityAddress().asStringUriOnly());
        //displayName.setText(Uc3call.getAddressDisplayName(proxy.getIdentityAddress()));
        //Toast.makeText(this, ""+Uc3Service.getCore().getDefaultProxyConfig().getState(), Toast.LENGTH_SHORT).show();
        //ProxyConfig proxyConfig = Uc3Service.getCore().getDefaultProxyConfig();

        if (Uc3Service.getCore().getDefaultProxyConfig() != null) {
            //String s = ""+proxyConfig.getState();
            //else {
            //mMissedCalls.clearAnimation();
            //mMissedCalls.setVisibility(View.GONE);
            //}
            updateLed(Uc3Service.getCore().getDefaultProxyConfig().getState());
            //ProxyConfig proxy = Uc3Service.getCore().getDefaultProxyConfig();



            address.setText(Uc3Service.getCore().getDefaultProxyConfig().getIdentityAddress().asStringUriOnly());
            displayName.setText(Uc3call.getAddressDisplayName(Uc3Service.getCore().getDefaultProxyConfig().getIdentityAddress()));
            //Toast.makeText(this, ""+proxyConfig.getState(), Toast.LENGTH_SHORT).show();
        } else {
            // No account configured, we display the configuration activity
            startActivity(new Intent(this, ConfigureAccountActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //  remove unused Core listeners in onPause
        Uc3Service.getCore().removeListener(mCoreListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        // Callback for when permissions are asked to the user
        for (int i = 0; i < permissions.length; i++) {
            Log.i(
                    "[Permission] "
                            + permissions[i]
                            + " is "
                            + (grantResults[i] == PackageManager.PERMISSION_GRANTED
                            ? "granted"
                            : "denied"));
        }
    }

    private void updateLed(RegistrationState state) {
        switch (state) {
            case Ok : // This state means you are connected, to can make and receive calls & messages
                mLed.setImageResource(R.drawable.led_connected);
                break;
            case Failed: // This one means an error happened, for example a bad password
                mLed.setImageResource(R.drawable.led_error);
                break;
            case Progress: // Connection is in progress, next state will be either Ok or Failed
                mLed.setImageResource(R.drawable.led_inprogress);
                break;
        }
    }

    private void checkAndRequestCallPermissions() {
        ArrayList<String> permissionsList = new ArrayList<>();

        // Some required permissions needs to be validated manually by the user
        // Here we ask for record audio and camera to be able to make video calls with sound
        // Once granted we don't have to ask them again, but if denied we can
        int recordAudio =
                getPackageManager()
                        .checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName());
        Log.i(
                "[Permission] Record audio permission is "
                        + (recordAudio == PackageManager.PERMISSION_GRANTED
                        ? "granted"
                        : "denied"));
        int camera =
                getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName());
        Log.i(
                "[Permission] Camera permission is "
                        + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for record audio");
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (camera != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for camera");
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            String[] permissions = new String[permissionsList.size()];
            permissions = permissionsList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }


    @Override
    public void onCallActivity() {
        Uc3call.startActivity(this,CallActivity.class);

    }

    @Override
    public void onIncomingActivity() {
        Uc3call.startActivity(this,incoming.class);
        if (!LinphoneService.isReady()) {
            Log.i("[Context] Service not running, starting it");
            Intent intent = new Intent(ACTION_MAIN);
            intent.setClass(this, Uc3Service.class);
            this.startService(intent);
        }
    }

    @Override
    public void onOutgoing() {
        Uc3call.startActivity(this,OutgoingActivity.class);

    }

    @Override
    public void onMisscall() {
        Toast.makeText(this, "Missed call", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRegistrationComplete(int type) {

        switch (type) {
            case ok: // This state means you are connected, to can make and receive calls & messages
                mLed.setImageResource(R.drawable.led_connected);
                break;

            case failed: // This one means an error happened, for example a bad password
                mLed.setImageResource(R.drawable.led_error);
                break;

            case progress: // Connection is in progress, next state will be either Ok or Failed
                mLed.setImageResource(R.drawable.led_inprogress);
                break;
        }
    }


}

