package com.sysnetph.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.sysnetph.mylibrary2.function.Uc3call;
import com.sysnetph.mylibrary2.function.services.Corelistub;
import com.sysnetph.mylibrary2.function.services.Uc3Service;
import com.sysnetph.mylibrary2.function.services.callendListener;




public class CallActivity extends Activity implements callendListener {
    // We use 2 TextureView, one for remote video and one for local camera preview
    private TextureView mVideoView;
    private TextureView mCaptureView;
    private Corelistub mCoreListener;

    TelephonyManager manager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.call);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        Uc3Service.getInstance().callendListener = this;
        manager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        //Core core = Uc3Service.getCore();
        // We need to tell the core in which to display what
       // core.setNativeVideoWindowId(mVideoView);
       //core.setNativePreviewWindowId(mCaptureView);

        // Listen for call state changes
//        mCoreListener = new Corelistub() {
//            @Override
//            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
//                if (state == Call.State.End || state == Call.State.Released) {
//                    // Once call is finished (end state), terminate the activity
//                    // We also check for released state (called a few seconds later) just in case
//                    // we missed the first one
//                    finish();
//                }
//            }
//        };
//


        findViewById(R.id.terminate_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Core core = Uc3Service.getCore();
//                if (core.getCallsNb() > 0) {
//                    Call call = core.getCurrentCall();
//                    if (call == null) {
//                        // Current call can be null if paused for example
//                        call = core.getCalls()[0];
//                    }
//                    call.terminate();
//                }

                Uc3call.terminate();
                finish();
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    // As it is the Service that is starting the activity, we have to give this flag
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);




            }
        });


        findViewById(R.id.SpeakerOn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager audioManager = (AudioManager)
                        getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(true);
                Toast.makeText(CallActivity.this, "loudspeak on", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.SpeakerOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager audioManager = (AudioManager)
                        getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(false);
                Toast.makeText(CallActivity.this, "loudspeak off", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3call.paused();
                Toast.makeText(CallActivity.this, "paused", Toast.LENGTH_SHORT).show();
            }
        });


        findViewById(R.id.resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3call.resume();
                Toast.makeText(CallActivity.this, "resume", Toast.LENGTH_SHORT).show();
            }
        });
        }





    @Override
    protected void onStart() {
        super.onStart();
        Uc3Service.getCore().addListener(mCoreListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uc3Service.getCore().addListener(mCoreListener);

    }

    @Override
    protected void onPause() {
        Uc3Service.getCore().removeListener(mCoreListener);

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        Uc3Service.getCore().removeListener(mCoreListener);
        mCoreListener = null;

        super.onDestroy();
    }

    @TargetApi(24)
//    @Override
//    public void onUserLeaveHint() {
//        // If the device supports Picture in Picture let's use it
//        boolean supportsPip =
//                getPackageManager()
//                        .hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE);
//        Log.i("[Call] Is picture in picture supported: " + supportsPip);
//        if (supportsPip && Version.sdkAboveOrEqual(24)) {
//            enterPictureInPictureMode();
//        }
//    }

    @Override
    public void onPictureInPictureModeChanged(
            boolean isInPictureInPictureMode, Configuration newConfig) {
        if (isInPictureInPictureMode) {
            // Currently nothing to do has we only display video
            // But if we had controls or other UI elements we should hide them
        } else {
            // If we did hide something, let's make them visible again
        }
    }


    @Override
    public void EndCall() {
        Uc3call.startActivity(this,MainActivity.class);
        AudioManager audioManager = (AudioManager)
                getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
    }
}
