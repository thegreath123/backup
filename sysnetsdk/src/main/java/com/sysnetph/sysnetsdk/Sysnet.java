package com.sysnetph.sysnetsdk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;


import org.linphone.core.AccountCreator;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.LogCollectionState;
import org.linphone.core.MediaEncryption;
import org.linphone.core.NatPolicy;
import org.linphone.core.ProxyConfig;
import org.linphone.core.Reason;
import org.linphone.core.RegistrationState;
import org.linphone.core.TransportType;
import org.linphone.mediastream.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

//import org.linphone.core.tools.Log;

public class Sysnet extends Service {
    //private static final String START_LINPHONE_LOGS = " ==== Device information dump ====";
    // Keep a static reference to the Service so we can access it from anywhere in the app
    private static Sysnet sInstance;
    private Handler mHandler;
    public static AccountCreator creator;
    private Timer mTimer;
    public static Call mcall;
    public static Core mCore;
    public static CoreListenerStub mCoreListener;
    public activityListener calllistener;
    public RegistrationAction registrationListener;
    public callactionsListener callactionListener;
    public String basePath;
    public static CallParams params;

    public int status;



//    public void addListener(activityListener listener) {
//        this.calllistener = listener;
//}

    public static boolean isReady() {
        return sInstance != null;
    }

    public static Sysnet getInstance() {

        if (isReady()) return sInstance;
        throw new RuntimeException("LinphoneService not instantiated yet");
    }

    public static Core getCore() {
        return mCore;
    }

    public static Call getCall() {
        return mcall;
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
        //Log.i(START_LINPHONE_LOGS);
        dumpDeviceInformation();
        dumpInstalledLinphoneInformation();

//        for (PayloadType payloadType : Sysnet.getCore().getAudioCodecs()) {
//            Sysnet.enablePayloadType(payloadType, payloadType.getMime().equalsIgnoreCase("opus"));
//        }
        mHandler = new Handler();


        // This will be our main Core listener, it will change activities depending on events

        // params.addCustomHeader("try","Premium");


        mCoreListener = new CoreListenerStub() {
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                //Toast.makeText(Uc3Service.this, message, Toast.LENGTH_SHORT).show();
                //call = mCall;


                if (state == Call.State.IncomingReceived || state == Call.State.IncomingEarlyMedia) {
                    // Toast.makeText(Uc3Service.this, "Incoming call received, answering it automatically", Toast.LENGTH_LONG).show();
                    // For this sample we will automatically answer incoming calls
                    // CallParams params = getCore().createCallParams(call);
                    //params.enableVideo(true);
                    // call.acceptWithParams(params);
                    Response(14);
                    Sysnet.getInstance().calllistener.onIncomingActivity();
                } else if (state == Call.State.Connected) {
                    // This stats means the call has been established, let's start the call activity
                    // Intent intent = new Intent(Uc3Service.this, CallActivity.clas
                    Response(14);
                    Sysnet.getInstance().calllistener.onCallActivity();
                } else if (state == Call.State.OutgoingInit) {
                    // This stats means the call has been established, let's start the call activity
                    Sysnet.getInstance().calllistener.onOutgoing();
                    Response(12);
                }
                else if (state == Call.State.OutgoingRinging) {
                    // This stats means the call has been established, let's start the call activity
                    Response(13);
                }
                else if (state == Call.State.PausedByRemote) {
                    Sysnet.getInstance().callactionListener.HoldCall();
                    Response(6);
                } else if (state == Call.State.StreamsRunning) {
                    Response(0);
                    //Toast.makeText(Uc3Service.this, "Resume", Toast.LENGTH_SHORT).show();
                } else if (state == Call.State.Paused) {
                    Sysnet.getInstance().callactionListener.HoldCall();
                    Response(6);

                } else if (state == Call.State.Resuming) {
                    Sysnet.getInstance().callactionListener.ResumeCall();
                    Response(20);
                }

                else if ((state == Call.State.End) || (state == Call.State.Released)) {
                    // Convert Core message for internalization
                    if (call.getErrorInfo().getReason() == Reason.Declined) {
                        Response(3);
                        Sysnet.getInstance().callactionListener.EndCall();
                    }
                    Sysnet.getInstance().callactionListener.EndCall();
                        Response(16);
                }

                else if (state == Call.State.Error) {
                    // Convert Core message for internalization
                    if (call.getErrorInfo().getReason() == Reason.NotFound) {
                        Response(10);
                        Sysnet.getInstance().callactionListener.EndCall();
                    }
                    if (call.getErrorInfo().getReason() == Reason.NotAnswered) {
                        Response(11);
                        Sysnet.getInstance().callactionListener.EndCall();
                    }
                    Sysnet.getInstance().callactionListener.EndCall();
                    Response(3);
                }





            }






            @Override
            public void onRegistrationStateChanged(Core core, ProxyConfig proxyConfig, RegistrationState state, String s) {

                if (state == RegistrationState.Ok) {
                    Sysnet.getInstance().registrationListener.onRegistrationComplete(1);
                    Response(0);
                    //Log.d("Status", "success");

                } else if (state == RegistrationState.Failed) {
                    Sysnet.getInstance().registrationListener.onRegistrationComplete(2);
                    Response(1);
                } else if (state == RegistrationState.None) {
                    Sysnet.getInstance().registrationListener.onRegistrationComplete(3);
                } else if (state == RegistrationState.Progress) {
                    Sysnet.getInstance().registrationListener.onRegistrationComplete(4);
                }

            }

            @Override
            public void onDtmfReceived(Core lc, Call call, int dtmf) {
                //Toast.makeText(Uc3Service.this, ""+dtmf, Toast.LENGTH_SHORT).show();
                Response(18);
            }
        };


        //Uc3call.RegistrationAction();


        try {
            // Let's copy some RAW resources to the device
            // The default config file must only be installed once (the first time)
            copyIfNotExist(R.raw.linphonerc_default, basePath + "/.linphonerc");
            // The factory config is used to override any other setting, let's copy it each time
            copyFromPackage(R.raw.linphonerc_factory, "linphonerc");
        } catch (IOException ioe) {
            //Log.e(ioe);
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
                //Log.e(userCerts + " can't be created.");
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
        //Log.i(sb.toString());
    }

    private void dumpInstalledLinphoneInformation() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException nnfe) {
            //Log.e(nnfe);
        }

        if (info != null) {
//            Log.i(
//                    "[Service] version is ",
//                    info.versionName + " (" + info.versionCode + ")");
        } else {
            //Log.i("[Service] version is unknown");
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

    public enum Servicetype {
        Free("Free", 0),
        Premium("Premium", 1);

        private String stringValue;
        private int intValue;

        Servicetype(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public enum NetworkType {
        ONNET("ONNET", 0),
        OFFNET("OFFNET", 1);

        private String stringValue;
        private int intValue;

        NetworkType(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }



    //makecall with network
    public void makeCall(String destination, Servicetype servicetype, NetworkType networkType) {
        Address addressToCall = Sysnet.getCore().interpretUrl(destination);
        CallParams params =  Sysnet.getCore().createCallParams(null);
        params.addCustomHeader("X-AppVNO-ServiceType", ""+servicetype);
        params.addCustomHeader("X-AppVNO-NetworkType", "" + networkType);
        // Toast.makeText(sInstance, st1, Toast.LENGTH_SHORT).show();
        if (addressToCall != null) {
            Sysnet.getCore().inviteAddressWithParams(addressToCall, params);
        }
        else{
            Response(10);
        }

    }


    public void endCall() {
        Core core = Sysnet.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.terminate();
        }

    }

    public void toggleMute(boolean m) {
        if (m == false) {
            Core core = Sysnet.getCore();
            if (core.getCallsNb() > 0) {
                Call call = core.getCurrentCall();
                if (call == null) {
                    // Current call can be null if paused for example
                    call = core.getCalls()[0];
                }
                call.setMicrophoneMuted(false);
                Response(9);
            }
        } else {
            muted();
            Response(8);
        }
    }

    public void answerCall() {

        Core core = Sysnet.getCore();
        CallParams params = core.createCallParams(mcall);
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.acceptWithParams(params);
        }
        else {
            Response(15);
        }

    }

    public void pauseCall() {

        Core core = Sysnet.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
                Response(7);
            }
            call.pause();
        }
        else {
            Response(7);
        }
    }

    public void muted() {

        Core core = Sysnet.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
                Response(8);
            }
            call.setMicrophoneMuted(true);
                Response(9);
        }
    }

    public void speakeron() {

        Core core = Sysnet.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.setSpeakerMuted(false);
        }
    }

    public void speakeroff() {

        Core core = Sysnet.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.setSpeakerMuted(true);
        }
    }

    public void resumeCall() {

        Core core = Sysnet.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.resume();
        }

        else {
            Response(21);
        }
    }

    public void decline() {
        Core core = Sysnet.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.decline(Reason.Declined);
        }
    }

    public void inicreate() {
        creator = Sysnet.getCore().createAccountCreator(null);
    }

    public void register(String Username, String Password) {

        creator.setUsername(Username);
        creator.setPassword(Password);
        //creator.setDomain(Domain);
        //creator.setTransport(TransportType.Tls);
//        switch (type) {
//            case 1:
//                creator.setTransport(TransportType.Udp);
//                creator.setDomain("asterisk");
//                break;
//            case 2:
//                creator.setTransport(TransportType.Tcp);
//                creator.setDomain(Domain);
//                break;
//            case 3:
        creator.setDomain("devsip.sysnetph.com:5091;transport=tls");
        creator.setTransport(TransportType.Tls);
        ProxyConfig cfg = creator.createProxyConfig();
        String server = cfg.getServerAddr();
        Address serverAddr = Factory.instance().createAddress(server);
        serverAddr.setTransport(TransportType.Tls);
        Sysnet.getCore().setMediaEncryption(MediaEncryption.SRTP);
        Sysnet.getCore().setStunServer("stun.l.google.com:19302");
        //setStunServer("stun.l.google.com:19302");
        setIceEnabled(true);
        //       break;
        //}

        //ProxyConfig cfg = creator.createProxyConfig();
        // Make sure the newly created one is the default
        Sysnet.getCore().setDefaultProxyConfig(cfg);
        Sysnet.getCore().setUseRfc2833ForDtmf(true);
        Sysnet.getCore().setUseInfoForDtmf(true);


        // cfg.setCustomHeader("trere","testing");
    }




    public NatPolicy getOrCreateNatPolicy() {
        if (getCore() == null) return null;
        NatPolicy nat = getCore().getNatPolicy();
        if (nat == null) {
            nat = getCore().createNatPolicy();
        }
        return nat;
    }

    public void setIceEnabled(boolean enabled) {
        if (getCore() == null) return;
        NatPolicy nat = getOrCreateNatPolicy();
        nat.enableIce(enabled);
        if (enabled) nat.enableStun(true);
        getCore().setNatPolicy(nat);
    }
    public void setStunServer(String stun) {
        if (getCore() == null) return;
        NatPolicy nat = getOrCreateNatPolicy();
        nat.setStunServer(stun);
        getCore().setNatPolicy(nat);
    }


    public void sendDmtf(char c) {
        if (!Sysnet.isReady()) return;
        Core core = Sysnet.getCore();
        core.stopDtmf();

        Call call = core.getCurrentCall();
        if (call != null) {
            Sysnet.getCore().setUseRfc2833ForDtmf(true);
            Sysnet.getCore().setUseInfoForDtmf(true);
            call.sendDtmf(c);
            Response(17);
            //playDtmf(getContext().getContentResolver(), '1');
        }

        else {
            Response(19);
        }
    }

    public void toggleSpeaker(Context context,boolean s) {
        if (s == false) {
            AudioManager audioManager = (AudioManager)
                    context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(false);
            Response(4);
        }
        else {
            AudioManager audioManager = (AudioManager)
                    context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            Response(5);
        }
    }



    public String getAddressDisplayName(Address address) {
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

    public void startActivity(Context context, Class s) {
        Intent intent1 = new Intent(context, s);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

    }

    public  void lookupCurrentCall() {
        if (Sysnet.getCore() != null) {
            for (Call call : Sysnet.getCore().getCalls()) {
                if (Call.State.IncomingReceived == call.getState()
                        || Call.State.IncomingEarlyMedia == call.getState()) {
                    mcall = call;
                    break;


                }
            }
        }
    }

    public void unregister() {
        Sysnet.getCore().clearProxyConfig();
    }

    public String getAddressname() {
        String address = getAddressDisplayName(Sysnet.getCall().getCore().getCurrentCallRemoteAddress());
        return address;
    }

    public String getProfilenameaddress() {
        String profileuseraddress =  Sysnet.getCore().getDefaultProxyConfig().getIdentityAddress().asStringUriOnly();
        return profileuseraddress;
    }

    public String getProfilename() {
        String profilename = getAddressDisplayName(Sysnet.getCore().getDefaultProxyConfig().getIdentityAddress());
        return profilename;

    }







    public  boolean getUserlogin() {

        ProxyConfig users = Sysnet.getCore().getDefaultProxyConfig();

        return users != null;
    }


    public int status(){


        String stats = String.valueOf(Sysnet.getCore().getDefaultProxyConfig().getState());
        if (stats.equals("Ok")){

            return 1;
        }
        if (stats.equals("Failed")){

            return 2;
        }
        if (stats.equals("Progress")){

            return 3;
        }
        return 3;
    }

    public int Response(int s) {

        switch (s){

            case 0:
                status = 0;
                Log.d("Status","Login Success " + status);
                break;

            case 1:
                status = 1;
                Log.d("Status","Wrong Credential " + status);
                break;

            case 2:
                status = 2;
                Log.d("Status","Busy " + status);
                break;

            case 3:
                status = 3;
                Log.d("Status","Decline " + status);
                break;

            case 4:
                status = 4;
                Log.d("Status","Speaker off " + status);
                break;
            case 5:
                status = 5;
                Log.d("Status","Speaker on " + status);
                break;

            case 6:
                status= 6;
                Log.d("Status","Hold " + status);
                break;
            case 7:
                status= 7;
                Log.d("Status","Error holding no valid call " + status);
                break;
            case 8:
                status= 8;
                Log.d("Status","Muted " + status);
                break;
            case 9:
                status= 9;
                Log.d("Status","Unmute " + status);
                break;

            case 10:
                status= 10;
                Log.d("Status","Call not found " + status);
                break;
            case 11:
                status= 11;
                Log.d("Status","not answered " + status);
                break;
            case 12:
                status= 12;
                Log.d("Status","Outgoing " + status);
                break;
            case 13:
                status= 13;
                Log.d("Status","ringing "  + status);
                break;
            case 14:
                status= 14;
                Log.d("Status","Connected " + status);
                break;
            case 15:
                status= 15;
                Log.d("Status","Error Accepting call " + status);
                break;
            case 16:
                status= 16;
                Log.d("Status","Disconnected " + status);
                break;
            case 17:
                status= 17;
                Log.d("Status","DTMF Sent "+ status);
                break;
            case 18:
                status= 18;
                Log.d("Status","DTMF Received "+ status);
                break;

            case 19:
                status= 19;
                Log.d("Status","Error sending DTMF "+ status);
                break;
            case 20:
                status= 20;
                Log.d("Status","Resume Call" +status );
                break;
            case 21:
                status= 21;
                Log.d("Status","error resuming call" + status);
                break;

        }

        return status;
    }



}

