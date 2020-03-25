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
import com.sysnetph.mylibrary2.function.services.Registration;
import com.sysnetph.mylibrary2.function.services.Uc3Service;




public class ConfigureAccountActivity extends Activity implements Registration {
    private EditText mUsername, mPassword, mDomain;
    private RadioGroup mTransport;
    private Button mConnect ;

    //private AccountCreator mAccountCreator;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.configure_account);
        Uc3Service.getInstance().registrationListener = this;
        // Account creator can help you create/config accounts, even not sip.linphone.org ones
        // As we only want to configure an existing account, no need for server URL to make requests
        // to know whether or not account exists, etc...
        //mAccountCreator =  Uc3Service.getCore().createAccountCreator(null);
        Uc3Service.inicreate();
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

                        Uc3Service.Uc3account(mUsername.getText().toString(), mPassword.getText().toString(), mDomain.getText().toString());
                       //Uc3call.Registration(ConfigureAccountActivity.this);
                    }
                });





   }





    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

       // Uc3Service.getCore().addListener(mCoreListener);
    }

    @Override
    protected void onPause() {
        //Uc3Service.getCore().removeListener(mCoreListener);

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
                Uc3Service.getCore().clearProxyConfig();
                Toast.makeText(this, "error login", Toast.LENGTH_SHORT).show();
                break;
        }



    }
}

