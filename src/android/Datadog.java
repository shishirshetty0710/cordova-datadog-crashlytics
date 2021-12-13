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

import java.util.UUID;

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
        PluginResult result;
        switch (action) {
            case "Init":
                if(!initialized){
                    String clientToken = args.getString(0);
                    String envName = args.getString(1);
                    String appID = args.getString(2);
                    Integer trackingConsent = args.getInt(3);
                    init(clientToken,envName,appID,trackingConsent);
                    initialized=true;
                    if(!wkSessionID.equals("")){
                        GlobalRum.addAttribute("wk_UniqueIDForSession", wkSessionID);
                    }
                    result = new PluginResult(PluginResult.Status.OK);
                }else{
                    result = new PluginResult(PluginResult.Status.OK,"Already Initialized!");
                }

                result.setKeepCallback(false);
                callback.sendPluginResult(result);
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
                result = new PluginResult(PluginResult.Status.OK);
                result.setKeepCallback(false);
                callback.sendPluginResult(result);
                return true;
            case "setTrackingConsent":
                TrackingConsent trackingConsent;
                Integer trackingConsentInt = args.getInt(0);
                switch (trackingConsentInt){
                    case 0:
                        trackingConsent= TrackingConsent.NOT_GRANTED;
                        break;
                    case 1:
                        trackingConsent= TrackingConsent.GRANTED;
                        break;
                    default:
                        trackingConsent= TrackingConsent.PENDING;
                        break;
                }
                if(initialized){
                    com.datadog.android.Datadog.setTrackingConsent(trackingConsent);
                }
                result = new PluginResult(PluginResult.Status.OK);
                result.setKeepCallback(false);
                callback.sendPluginResult(result);
                return true;
        }
        result = new PluginResult(PluginResult.Status.ERROR,"Unidentified Action!");
        result.setKeepCallback(false);
        callback.sendPluginResult(result);
        return false;
    }
    private void init(String clientToken,String envName,String appID,Integer trackingConsentInt){
        Configuration config = new Configuration.Builder(false,true,true,true)
                .trackInteractions()
                .trackLongTasks()
                .trackBackgroundRumEvents(true)
                .build();
        Credentials cred = new Credentials(clientToken,envName,Credentials.NO_VARIANT,appID,null);
        TrackingConsent trackingConsent;
        switch (trackingConsentInt){
            case 0:
                trackingConsent= TrackingConsent.NOT_GRANTED;
                break;
            case 1:
                trackingConsent= TrackingConsent.GRANTED;
                break;
            default:
                trackingConsent= TrackingConsent.PENDING;
                break;
        }
        com.datadog.android.Datadog.initialize(cordova.getActivity().getApplicationContext(),cred,config, trackingConsent);
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
        String mskuuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        //PluginResult result = new PluginResult(PluginResult.Status.OK,currSessionId);
        PluginResult result = new PluginResult(PluginResult.Status.OK,mskuuid);
        result.setKeepCallback(false);
        callback.sendPluginResult(result);
    }
    
    private void testCrash(){
        throw new RuntimeException("Crash Test");
    }
}
