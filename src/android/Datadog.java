package com.outsystems.datadog;

import com.datadog.android.core.configuration.Configuration;
import com.datadog.android.core.configuration.Credentials;
import com.datadog.android.privacy.TrackingConsent;
import com.datadog.android.rum.GlobalRum;
import com.datadog.android.rum.RumMonitor;
import com.datadog.android.rum.RumSessionListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class Datadog extends CordovaPlugin {
    public static final String TAG = "Datadog Plugin";

    private CallbackContext callback;
    RumMonitor monitor;
    private String currSessionId = "";
    private String wkSessionID = "";
    private Boolean initialized = false;

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArry of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;
        switch (action) {
            case "Init":
                if(!initialized){
                    init(args.getString(0),args.getString(1),args.getString(2));
                    initialized=true;
                    if(!wkSessionID.equals("")){
                        GlobalRum.addAttribute("wk_UniqueIDForSession", wkSessionID);
                    }
                }
                return true;
            case "crashtest":
                testCrash();
                return true;
            case "getSessionId":
                getSessionId();
                return true;
            case "setCustomFieldSessionId":
                if (!wkSessionID.equals("")){
                    GlobalRum.removeAttribute("wk_UniqueIDForSession");
                }
                wkSessionID = args.getString(0);
                if(initialized){
                    GlobalRum.addAttribute("wk_UniqueIDForSession", wkSessionID);
                }
                return true;
        }
        return false;
    }
    private void init(String clientToken,String envName,String appID){
        Configuration config = new Configuration.Builder(false,true,true,true)
                .trackInteractions()
                .trackLongTasks()
                .build();
        Credentials cred = new Credentials(clientToken,envName,Credentials.NO_VARIANT,appID,null);
        com.datadog.android.Datadog.initialize(cordova.getActivity().getApplicationContext(),cred,config, TrackingConsent.GRANTED);
        initRUM();
    }

    private void initRUM(){
        monitor = new RumMonitor.Builder().setSessionListener(new RumSessionListener() {
            @Override
            public void onSessionStarted(String sessionId, boolean isDiscarded) {
                currSessionId = sessionId;
            }
        }).build();
        GlobalRum.registerIfAbsent(monitor);
    }

    private void getSessionId(){
        PluginResult result = new PluginResult(PluginResult.Status.OK,currSessionId);
        result.setKeepCallback(false);
        callback.sendPluginResult(result);
    }

    private void testCrash(){
        throw new RuntimeException("crash testing");
    }
}