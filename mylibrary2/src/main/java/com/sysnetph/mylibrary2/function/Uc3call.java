package com.sysnetph.mylibrary2.function;

import android.content.Context;
import android.content.Intent;

import com.sysnetph.mylibrary2.function.services.Uc3Service;


import org.linphone.core.AccountCreator;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.ProxyConfig;
import org.linphone.core.Reason;
import org.linphone.core.TransportType;

public class Uc3call {

    public static AccountCreator creator;

    public static Call call;
    Context context;
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
        CallParams params = core.createCallParams(call);
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


}












