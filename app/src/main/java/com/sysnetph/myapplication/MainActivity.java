package com.sysnetph.myapplication;

import android.app.Activity;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.sysnetph.mylibrary2.function.Uc3call;
import com.sysnetph.mylibrary2.function.services.Registration;
import com.sysnetph.mylibrary2.function.services.Uc3Service;
import com.sysnetph.mylibrary2.function.services.Uc3listener;

import org.linphone.core.CoreListenerStub;
import org.linphone.core.RegistrationState;


import java.util.ArrayList;

import static android.content.Intent.ACTION_MAIN;



public class MainActivity extends Activity implements Uc3listener, Registration {
    private ImageView mLed;

    private final int ok = 1,failed = 2, progress = 3;
    private EditText mSipAddressToCall;
    private TextView address,displayName,missedcall;
   // public static CallLog mLogs;
    public CoreListenerStub mCoreListener;




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





       //Uc3call.Uc3Led(mLed);

        mSipAddressToCall = findViewById(R.id.address_to_call);
        //mSipAddressToCall.setText(""+mLogs);
        Button callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3Service.call(mSipAddressToCall.getText().toString());


            }
        });

        findViewById(R.id.notifbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent();
              intent.setAction("com.sysnetph.myapplication");
              intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
              sendBroadcast(intent);
            }
        });

    }







    @Override
    protected void onStart() {
        super.onStart();

        // Ask runtime permissions, such as record audio and camera
        //  once the user has granted them we won't have to ask again
        checkAndRequestCallPermissions();
        //Uc3Service.getCore().addListener(mCoreListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // The best way to use Core listeners in Activities is to add them in onResume
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
            if (Uc3Service.getCore().getDefaultProxyConfig().equals("Ok")){
                missedcall.setText("ok");
            }

            updateLed(Uc3Service.getCore().getDefaultProxyConfig().getState());

            //ProxyConfig proxy = Uc3Service.getCore().getDefaultProxyConfig();
            address.setText(Uc3Service.getCore().getDefaultProxyConfig().getIdentityAddress().asStringUriOnly());
            displayName.setText(Uc3Service.getAddressDisplayName(Uc3Service.getCore().getDefaultProxyConfig().getIdentityAddress()));
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
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        // Callback for when permissions are asked to the user
        for (int i = 0; i < permissions.length; i++) {
//            Log.i(
//                    "[Permission] "
//                            + permissions[i]
//                            + " is "
//                            + (grantResults[i] == PackageManager.PERMISSION_GRANTED
//                            ? "granted"
//                            : "denied"));
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
//        Log.i(
//                "[Permission] Record audio permission is "
//                        + (recordAudio == PackageManager.PERMISSION_GRANTED
//                        ? "granted"
//                        : "denied"));
        int camera =
                getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName());
//        Log.i(
//                "[Permission] Camera permission is "
//                        + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
//            Log.i("[Permission] Asking for record audio");
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (camera != PackageManager.PERMISSION_GRANTED) {
//            Log.i("[Permission] Asking for camera");
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
        Uc3Service.startActivity(this,CallActivity.class);

    }

    @Override
    public void onIncomingActivity() {
        Uc3Service.startActivity(this,incoming.class);

        if (!Uc3Service.isReady()) {
//            Log.i("[Context] Service not running, starting it");
            Intent intent = new Intent(ACTION_MAIN);
            intent.setClass(this, Uc3Service.class);
            this.startService(intent);
        }

        //displayNotif();
    }

    @Override
    public void onOutgoing() {
        Uc3Service.startActivity(this,OutgoingActivity.class);

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

