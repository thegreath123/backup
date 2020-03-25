package com.sysnetph.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.sysnetph.myapplication.utils.onclickbutton;
import com.sysnetph.mylibrary2.function.Uc3call;
import com.sysnetph.mylibrary2.function.services.Uc3Service;
import com.sysnetph.mylibrary2.function.services.callendListener;

//import org.linphone.core.Call;



public class CallActivity extends Activity implements callendListener, View.OnClickListener {
    // We use 2 TextureView, one for remote video and one for local camera preview

    TelephonyManager manager;
    EditText dtmf;
    //Call call;
    Button one,two,three,four,five,six,seven,eight,nine,zeor,ast,numsign;

    Button num;
    onclickbutton onclickButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        Uc3Service.getInstance().callendListener = this;

        manager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        one = (Button)findViewById(R.id.one);
        two = (Button)findViewById(R.id.two);
        three = (Button)findViewById(R.id.three);
        four = (Button)findViewById(R.id.four);
        five = (Button)findViewById(R.id.five);
        six = (Button)findViewById(R.id.six);
        seven = (Button)findViewById(R.id.seven);
        eight = (Button)findViewById(R.id.eight);
        nine = (Button)findViewById(R.id.nine);
        zeor = (Button)findViewById(R.id.zero);
        ast = (Button)findViewById(R.id.asterisk);
        numsign = (Button)findViewById(R.id.numsign);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zeor.setOnClickListener(this);
        ast.setOnClickListener(this);
        numsign.setOnClickListener(this);




        findViewById(R.id.terminate_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3Service.terminate();
                finish();
            }
        });
        findViewById(R.id.mute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3Service.muted();

            }
        });


        findViewById(R.id.unmute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3call.unmuted();
            }
        });

        findViewById(R.id.SpeakerOn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Uc3Service.setspeakerOn(getApplicationContext());
               // Toast.makeText(CallActivity.this, "loudspeak on", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.SpeakerOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AudioManager audioManager = (AudioManager)
//                        getSystemService(Context.AUDIO_SERVICE);
//                audioManager.setMode(AudioManager.MODE_IN_CALL);
//                audioManager.setMode(AudioManager.MODE_NORMAL);
//                audioManager.setSpeakerphoneOn(false);
                Uc3Service.setspeakeroff(getApplicationContext());
               // Toast.makeText(CallActivity.this, "loudspeak off", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3Service.paused();


            }
        });


        findViewById(R.id.resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3Service.resume();

            }
        });




                onclickButton = new onclickbutton() {
                    @Override
                    public void OnClicklistener(View view) {
                        switch (view.getId()){

                            case R.id.one:
                                Uc3Service.sendDmtf('1');
                                break;
                            case R.id.two:
                                Uc3Service.sendDmtf('2');
                                break;
                            case R.id.three:
                                Uc3Service.sendDmtf('3');
                                break;
                            case R.id.four:
                                Uc3Service.sendDmtf('4');
                                break;
                            case R.id.five:
                                Uc3Service.sendDmtf('5');
                                break;
                            case R.id.six:
                                Uc3Service.sendDmtf('6');
                                break;
                            case R.id.seven:
                                Uc3Service.sendDmtf('7');
                                break;
                            case R.id.eight:
                                Uc3Service.sendDmtf('8');
                                break;
                            case R.id.nine:
                                Uc3Service.sendDmtf('9');
                                break;
                            case R.id.asterisk:
                                Uc3Service.sendDmtf('*');
                                break;
                            case R.id.zero:
                                Uc3Service.sendDmtf('0');
                                break;
                            case R.id.numsign:
                                Uc3Service.sendDmtf('#');
                                break;
                        }


                    }
                } ;

    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onPause() {


        super.onPause();
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
    }



    @Override
    public void EndCall() {
        Uc3Service.startActivity(this, MainActivity.class);
//        AudioManager audioManager = (AudioManager)
//                getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setMode(AudioManager.MODE_IN_CALL);
//        audioManager.setMode(AudioManager.MODE_NORMAL);
//        audioManager.setSpeakerphoneOn(false);
        Uc3Service.setspeakeroff(getApplicationContext());
    }





//    public void playDtmf(ContentResolver r, char dtmf) {
//        try {
//            if (Settings.System.getInt(r, Settings.System.DTMF_TONE_WHEN_DIALING) == 0) {
//                // audible touch disabled: don't play on speaker, only send in outgoing stream
//                return;
//            }
//        } catch (Settings.SettingNotFoundException e) {
//            Log.e("[Call Manager] playDtmf exception: " + e);
//        }
//        Uc3Service.getCore().playDtmf(dtmf, -1);
//    }






    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.one:
                Uc3Service.sendDmtf('1');
                break;
            case R.id.two:
                Uc3Service.sendDmtf('2');
                break;
            case R.id.three:
                Uc3Service.sendDmtf('3');
                break;
            case R.id.four:
                Uc3Service.sendDmtf('4');
                break;
            case R.id.five:
                Uc3Service.sendDmtf('5');
                break;
            case R.id.six:
                Uc3Service.sendDmtf('6');
                break;
            case R.id.seven:
                Uc3Service.sendDmtf('7');
                break;
            case R.id.eight:
                Uc3Service.sendDmtf('8');
                break;
            case R.id.nine:
                Uc3Service.sendDmtf('9');
                break;
            case R.id.asterisk:
                Uc3Service.sendDmtf('*');
                break;
            case R.id.zero:
                Uc3Service.sendDmtf('0');
                break;
            case R.id.numsign:
                Uc3Service.sendDmtf('#');
                break;
        }


    }


}
