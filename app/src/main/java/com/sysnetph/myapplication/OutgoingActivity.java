package com.sysnetph.myapplication;

import android.os.Bundle;



import com.sysnetph.mylibrary2.function.Uc3call;

import com.sysnetph.mylibrary2.function.services.Uc3Service;
import com.sysnetph.mylibrary2.function.services.callendListener;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;


public class OutgoingActivity extends AppCompatActivity implements callendListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing);
        Uc3Service.getInstance().callendListener = this;
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uc3Service.terminate();
                finish();
            }
        });


    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Uc3Service.isReady()
                && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
            Uc3Service.terminate();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void EndCall() {
        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();


    }




}
