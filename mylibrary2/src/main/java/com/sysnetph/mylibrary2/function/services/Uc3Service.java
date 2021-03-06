package com.sysnetph.mylibrary2.function.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sysnetph.mylibrary2.R;
import com.sysnetph.mylibrary2.function.Uc3call;


import org.linphone.core.AccountCreator;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.LogCollectionState;
import org.linphone.core.ProxyConfig;
import org.linphone.core.Reason;
import org.linphone.core.RegistrationState;
import org.linphone.core.TransportType;
import org.linphone.core.tools.Log;
import org.linphone.mediastream.Version;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class Uc3Service extends Service {
    private static final String START_LINPHONE_LOGS = " ==== Device information dump ====";
    // Keep a static reference to the Service so we can access it from anywhere in the app
    private static Uc3Service sInstance;

    private Handler mHandler;
    public static AccountCreator creator;
    private Timer mTimer;
    public static Core mCore;
    public static Call mCall;
    public static RegistrationState status;
    public static CoreListenerStub mCoreListener;

    public Uc3listener calllistener;
    public Registration registrationListener;
    public callendListener callendListener;

    public String basePath;
    public static final String CHANNEL_ID = "sysnetnotifid";
    public static final int NOTIF_ID = 2;
    public void addListener(Uc3listener listener) {
        this.calllistener = listener;
    }

    public static boolean isReady() {
        return sInstance != null;
    }

    public static Uc3Service getInstance() {

        if (isReady()) return sInstance;
        throw new RuntimeException("LinphoneService not instantiated yet");
    }

    public static Core getCore() {
        return sInstance.mCore;
    }

    public static Call getCall() {
        return sInstance.mCall;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // The first call to liblinphone SDK MUST BE to a Factory method
        // So let's enable the library debug logs & log collection

        basePath = getFilesDir().getAbsolutePath();
        Factory.instance().setLogCollectionPath(basePath);
        //getCore().setCallLogsDatabasePath(basePath);
        Factory.instance().enableLogCollection(LogCollectionState.Enabled);
        Factory.instance().setDebugMode(true, getString(R.string.app_name));
        // Dump some useful information about the device we're running on
        Log.i(START_LINPHONE_LOGS);
        dumpDeviceInformation();
        dumpInstalledLinphoneInformation();
        mHandler = new Handler();
        // This will be our main Core listener, it will change activities depending on events
        mCoreListener = new CoreListenerStub() {
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                //Toast.makeText(Uc3Service.this, message, Toast.LENGTH_SHORT).show();
                call = mCall;
                if (state == Call.State.IncomingReceived || state == Call.State.IncomingEarlyMedia) {
                    // Toast.makeText(Uc3Service.this, "Incoming call received, answering it automatically", Toast.LENGTH_LONG).show();
                    // For this sample we will automatically answer incoming calls
                    // CallParams params = getCore().createCallParams(call);
                    //params.enableVideo(true);
                    // call.acceptWithParams(params);
                    Uc3Service.getInstance().calllistener.onIncomingActivity();
                } else if (state == Call.State.Connected) {
                    // This stats means the call has been established, let's start the call activity
                    // Intent intent = new Intent(Uc3Service.this, CallActivity.class);
                    // As it is the Service that is starting the activity, we have to give this flag
                    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //Toast.makeText(Uc3Service.this, "coneectedsadasd", Toast.LENGTH_SHORT).show();
                    //startActivity(intent);
                    Uc3Service.getInstance().calllistener.onCallActivity();
                } else if (state == Call.State.OutgoingInit) {
                    // This stats means the call has been established, let's start the call activity
                    // Intent intent = new Intent(Uc3Service.this, CallActivity.class);
                    // As it is the Service that is starting the activity, we have to give this flag
                    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //Toast.makeText(Uc3Service.this, "coneectedsadasd", Toast.LENGTH_SHORT).show();
                    //startActivity(intent);
                    Uc3Service.getInstance().calllistener.onOutgoing();
                } else if (state == Call.State.PausedByRemote) {
                    // This stats means the call has been established, let's start the call activity
                    // Intent intent = new Intent(Uc3Service.this, CallActivity.class);
                    // As it is the Service that is starting the activity, we have to give this flag
                    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // Toast.makeText(Uc3Service.this, "Paused", Toast.LENGTH_SHORT).show();
                    //startActivity(intent);
                    //Uc3Service.getInstance().calllistener.onOutgoing();
                } else if (state == Call.State.StreamsRunning) {
                    //Toast.makeText(Uc3Service.this, "Resume", Toast.LENGTH_SHORT).show();
                }


                else if (state == Call.State.Paused){

                }

                else if (state == Call.State.Resuming){

                }

//                else if (state == Call.State.End || state == Call.State.Released) {
//                        if (call.getErrorInfo().getReason() == Reason.Declined) {
//                            Toast.makeText(
//                                    Uc3Service.this,
//                                    "Decline",
//                                    Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                        if (state == Call.State.Released) {
//
//                            Toast.makeText(Uc3Service.this, "miss call", Toast.LENGTH_SHORT).show();
//                        }
//                    Uc3Service.getInstance().callendListener.EndCall();
//                    }
                else if ((state == Call.State.End) || (state == Call.State.Released)){
                    // Convert Core message for internalization
                    if (call.getErrorInfo().getReason() == Reason.Declined) {
                            //Toast.makeText(Uc3Service.this, "busy", Toast.LENGTH_SHORT).show();
                          Uc3Service.getInstance().callendListener.EndCall();
                           }
                          Uc3Service.getInstance().callendListener.EndCall();

                }


            }


            @Override
            public void onRegistrationStateChanged(Core core, ProxyConfig proxyConfig, RegistrationState state, String s) {

                if (state == RegistrationState.Ok) {

                    Uc3Service.getInstance().registrationListener.onRegistrationComplete(1);
                } else if (state == RegistrationState.Failed) {
                    Uc3Service.getInstance().registrationListener.onRegistrationComplete(2);
                } else if (state == RegistrationState.None) {
                    Uc3Service.getInstance().registrationListener.onRegistrationComplete(3);
                }else if (state == RegistrationState.Progress) {
                    Uc3Service.getInstance().registrationListener.onRegistrationComplete(4);
                }

            }

            @Override
            public void onDtmfReceived(Core lc, Call call, int dtmf) {
                //Toast.makeText(Uc3Service.this, ""+dtmf, Toast.LENGTH_SHORT).show();



            }
        };





       //Uc3call.Registration();




        try {
            // Let's copy some RAW resources to the device
            // The default config file must only be installed once (the first time)
            copyIfNotExist(R.raw.linphonerc_default, basePath + "/.linphonerc");
            // The factory config is used to override any other setting, let's copy it each time
            copyFromPackage(R.raw.linphonerc_factory, "linphonerc");
        } catch (IOException ioe) {
            Log.e(ioe);
        }
        // Create the Core and add our listener
        mCore = Factory.instance()
                .createCore(basePath + "/.linphonerc", getApplicationContext().getFilesDir().getAbsolutePath() + "/linphonerc", this);
        mCore.addListener(mCoreListener);

        // Core is ready to be configured
        configureCore();
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // If our Service is already running, no need to continue
        if (sInstance != null) {
            return START_STICKY;
        }
        // Our Service has been started, we can keep our reference on it
        // From now one the Launcher will be able to call onServiceReady()
        sInstance = this;
        // Core must be started after being created and configured
        mCore.enterBackground();
        mCore.start();
        // We also MUST call the iterate() method of the Core on a regular basis
        TimerTask lTask =
                new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mCore != null) {
                                            mCore.iterate();
                                        }
                                    }
                                });
                    }
                };
        mTimer = new Timer("Linphone scheduler");
        mTimer.schedule(lTask, 0, 20);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mCore.removeListener(mCoreListener);
        mTimer.cancel();
        mCore.stop();
        // A stopped Core can be started again
        // To ensure resources are freed, we must ensure it will be garbage collected
        mCore = null;
        // Don't forget to free the singleton as well
        sInstance = null;

        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // For this sample we will kill the Service at the same time we kill the app
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    private void configureCore() {
        // We will create a directory for user signed certificates if needed
        String basePath = getFilesDir().getAbsolutePath();
        String userCerts = basePath + "/user-certs";
        File f = new File(userCerts);
        if (!f.exists()) {
            if (!f.mkdir()) {
                Log.e(userCerts + " can't be created.");
            }
        }
        mCore.setUserCertificatesPath(userCerts);
    }

    private void dumpDeviceInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("DEVICE=").append(Build.DEVICE).append("\n");
        sb.append("MODEL=").append(Build.MODEL).append("\n");
        sb.append("MANUFACTURER=").append(Build.MANUFACTURER).append("\n");
        sb.append("SDK=").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("Supported ABIs=");
        for (String abi : Version.getCpuAbis()) {
            sb.append(abi).append(", ");
        }
        sb.append("\n");
        Log.i(sb.toString());
    }

    private void dumpInstalledLinphoneInformation() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(nnfe);
        }

        if (info != null) {
            Log.i(
                    "[Service] version is ",
                    info.versionName + " (" + info.versionCode + ")");
        } else {
            Log.i("[Service] version is unknown");
        }
    }

    private void copyIfNotExist(int ressourceId, String target) throws IOException {
        File lFileToCopy = new File(target);
        if (!lFileToCopy.exists()) {
            copyFromPackage(ressourceId, lFileToCopy.getName());
        }
    }


    private void copyFromPackage(int ressourceId, String target) throws IOException {
        FileOutputStream lOutputStream = openFileOutput(target, 0);
        InputStream lInputStream = getResources().openRawResource(ressourceId);
        int readByte;
        byte[] buff = new byte[8048];
        while ((readByte = lInputStream.read(buff)) != -1) {
            lOutputStream.write(buff, 0, readByte);
        }
        lOutputStream.flush();
        lOutputStream.close();
        lInputStream.close();
    }

//    public NotificationsManager getNotificationManager() {
//        return mNotificationManager;
//    }

    public static void call(String to) {

        Core core = Uc3Service.getCore();
        Address addressToCall = core.interpretUrl(to);
        CallParams params = core.createCallParams(null);
        if (addressToCall != null) {
            core.inviteAddressWithParams(addressToCall, params);
        }

    }

    public static void terminate() {
        Core core = Uc3Service.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.terminate();


        }
    }

    public static void acceptcall() {

        Core core = Uc3Service.getCore();
        CallParams params = core.createCallParams(mCall);
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.acceptWithParams(params);
        }
    }

    public static void paused() {

        Core core = Uc3Service.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.pause();
        }
    }

    public static void muted() {

        Core core = Uc3Service.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.setMicrophoneMuted(true);
        }
    }

    public static void speakeron() {

        Core core = Uc3Service.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.setSpeakerMuted(false);
        }
    }
    public static void speakeroff() {

        Core core = Uc3Service.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.setSpeakerMuted(true);
        }
    }

    public static void resume() {

        Core core = Uc3Service.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.resume();
        }
    }

    public static void decline() {
        Core core = Uc3Service.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.decline(Reason.Declined);
        }
    }
    public  static  void inicreate (){
      creator = Uc3Service.getCore().createAccountCreator(null);
    }

    public static  void Uc3account (String Username, String Password, String Domain) {

        creator.setUsername(Username);
        creator.setPassword(Password);
        creator.setDomain(Domain);
        creator.setTransport(TransportType.Udp);

        ProxyConfig cfg = creator.createProxyConfig();
        // Make sure the newly created one is the default
        Uc3Service.getCore().setDefaultProxyConfig(cfg);
        Uc3Service.getCore().setUseRfc2833ForDtmf(true);
        Uc3Service.getCore().setUseInfoForDtmf(true);


    }

    public static void sendDmtf(char c) {
        if (!Uc3Service.isReady()) return;
        Core core = Uc3Service.getCore();
        core.stopDtmf();

        Call call = core.getCurrentCall();
        if (call != null) {
            Uc3Service.getCore().setUseRfc2833ForDtmf(true);
            Uc3Service.getCore().setUseInfoForDtmf(true);
            call.sendDtmf(c);
            //playDtmf(getContext().getContentResolver(), '1');
        }


    }

    public static void setspeakeroff(Context context){
        AudioManager audioManager = (AudioManager)
                context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);

    }

    public static void setspeakerOn(Context context) {
        AudioManager audioManager = (AudioManager)
                context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);

    }

    public static String getAddressDisplayName(Address address) {
        if (address == null) return null;

        String displayName = address.getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            displayName = address.getUsername();
        }
        if (displayName == null || displayName.isEmpty()) {
            displayName = address.asStringUriOnly();
        }
        return displayName;
    }

    public static void startActivity(Context context, Class s)
    {
        Intent intent1 = new Intent(context,s);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

    }

    public static void  lookupCurrentCall() {
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


}

