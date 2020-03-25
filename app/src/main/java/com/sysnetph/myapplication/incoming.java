package com.sysnetph.myapplication;


import android.os.Bundle;
import com.sysnetph.mylibrary2.function.Uc3call;
import com.sysnetph.mylibrary2.function.services.Uc3Service;
import com.sysnetph.mylibrary2.function.services.callendListener;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.tools.Log;


public class incoming extends AppCompatActivity implements callendListener {


    Call mCall;
    private TextView displayname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming);
        Uc3Service.getInstance().callendListener = this;

        displayname = findViewById(R.id.displayname);
        findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uc3Service.acceptcall();
                Uc3Service.startActivity(incoming.this, CallActivity.class);
            }
        });
//        Address address = mCall.getRemoteAddress();
//        String displayName = Uc3call.getAddressDisplayName(address);
//       displayname.setText(displayName);

        findViewById(R.id.decline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3Service.decline();
                finish();
            }
        });

    }



    protected void onDestroy() {

        //Uc3Service = null;
        //corelistub = null;

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();


        Core core = Uc3Service.getCore();
        if (core != null) {

        }
        lookupCurrentCall();
        if (mCall == null) {
            // The incoming call no longer exists.
            Log.d("Couldn't find incoming call");
            finish();
            return;
        }

        String displayName = Uc3Service.getAddressDisplayName(Uc3Service.getCall().getCore().getCurrentCallRemoteAddress());
        displayname.setText(displayName);

        //lookupCurrentCall();
//        if (LinphonePreferences.instance().acceptIncomingEarlyMedia()) {
//            if (mCall.getCurrentParams().videoEnabled()) {
//                findViewById(R.id.avatar_layout).setVisibility(View.GONE);
//                mCall.getCore().setNativeVideoWindowId(mVideoDisplay);
//            }
//        }

    }

    private void lookupCurrentCall() {
        if (Uc3Service.getCore() != null) {
            for (Call call : Uc3Service.getCore().getCalls()) {
                if (Call.State.IncomingReceived == call.getState()
                        || Call.State.IncomingEarlyMedia == call.getState()) {
                    mCall = call;
                    break;



                }
            }
        }
    }

    @Override
    public void EndCall() {
        finish();
    }


}