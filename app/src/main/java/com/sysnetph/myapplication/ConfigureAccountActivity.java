package com.sysnetph.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sysnetph.mylibrary2.function.Uc3call;
import com.sysnetph.mylibrary2.function.services.Corelistub;
import com.sysnetph.mylibrary2.function.services.Registration;
import com.sysnetph.mylibrary2.function.services.Uc3Service;




public class ConfigureAccountActivity extends Activity implements Registration {
    private EditText mUsername, mPassword, mDomain;
    private RadioGroup mTransport;
    private Button mConnect ;

    //private AccountCreator mAccountCreator;
    private Corelistub mCoreListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.configure_account);
        Uc3Service.getInstance().registrationListener = this;
        // Account creator can help you create/config accounts, even not sip.linphone.org ones
        // As we only want to configure an existing account, no need for server URL to make requests
        // to know whether or not account exists, etc...
        //mAccountCreator =  Uc3Service.getCore().createAccountCreator(null);
        Uc3call.inicreate();
        //RegistrationListener = this;
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mDomain = findViewById(R.id.domain);
        mTransport = findViewById(R.id.assistant_transports);

        mConnect = findViewById(R.id.configure);

        //Uc3call.Registration();
        mConnect.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // configureAccount();

                        Uc3call.Uc3account(mUsername.getText().toString(), mPassword.getText().toString(), mDomain.getText().toString());
                       // Uc3call.Registration(ConfigureAccountActivity.this);
                    }
                });


        //Uc3Service.getInstance().registrationListener.RegActivity(Uc3Service.getCore(),Uc3Service.getProxy(),);
            //Uc3Service.getstate();


//
//        mCoreListener = new Corelistub() {
//            @Override
//            public void onRegistrationStateChanged(Core core, ProxyConfig cfg, RegistrationState state, String message) {
//                if (state == RegistrationState.Ok) {
//
//                     Uc3call.startActivity(getApplicationContext(),MainActivity.class);
//                    //Toast.makeText(ConfigureAccountActivity.this, message, Toast.LENGTH_LONG).show();
//                    finish();
//                } else if (state == RegistrationState.Failed) {
//                    //Toast.makeText(ConfigureAccountActivity.this, "Failure: " + message, Toast.LENGTH_LONG).show();
//                    Log.d("Failure", "failerd");
//
//                } else if (state == RegistrationState.None) {
//                    //Toast.makeText(ConfigureAccountActivity.this, message, Toast.LENGTH_LONG).show();
//                }
//
//              // Log.d("logs",+ cfg,)
//            }
//        };



   }





    @Override
    protected void onStart() {
        super.onStart();
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
        super.onDestroy();
    }


    @Override
    public void onRegistrationComplete(int type) {
        switch (type){

            case 1:
                finish();
                break;

            case 2:
                Toast.makeText(this, "Wrong credential", Toast.LENGTH_SHORT).show();
                break;
        }



    }
}

