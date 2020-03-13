package com.sysnetph.myapplication;

import android.os.Bundle;



import com.sysnetph.mylibrary2.function.Uc3call;
import com.sysnetph.mylibrary2.function.services.Corelistub;
import com.sysnetph.mylibrary2.function.services.Uc3Service;
import com.sysnetph.mylibrary2.function.services.callendListener;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;


public class OutgoingActivity extends AppCompatActivity implements callendListener {
//    public Call mCall;
    public Corelistub mCoreListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing);
        Uc3Service.getInstance().callendListener = this;
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3call.terminate();
                finish();
            }
        });


    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Uc3Service.isReady()
                && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
            Uc3call.terminate();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uc3Service.getCore().addListener(mCoreListener);
//        if (Uc3Service.getCore() != null) {
//            for (Call call : Uc3Service.getCore().getCalls()) {
//                Call.State cstate = call.getState();
//                if (Call.State.OutgoingInit == cstate
//                        || Call.State.OutgoingProgress == cstate
//                        || Call.State.OutgoingRinging == cstate
//                        || Call.State.OutgoingEarlyMedia == cstate) {
//                    mCall = call;
//                    break;
//                }
//            }
//        }
//
//        if (mCall == null) {
//            Log.e("[Call Outgoing Activity] Couldn't find outgoing call");
//            finish();
//            return;
//        }
    }

    @Override
    public void EndCall() {
        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //  remove unused Core listeners in onPause
        Uc3Service.getCore().removeListener(mCoreListener);
    }




}
