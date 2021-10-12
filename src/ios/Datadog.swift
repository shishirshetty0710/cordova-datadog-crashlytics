//
//  Datadog.swift
//  test
//
//  Created by Luis Bouça on 04/10/2021.
//

import Foundation
import Datadog
import DatadogCrashReporting

@objc(Datadog) class DatadogCrash: CDVPlugin{
    
    var wkSessionId:String = " "
    var isInitialized:Bool = false
    
    @objc(Init:)func Init(command : CDVInvokedUrlCommand){
        if(!isInitialized){
            let clientToken = command.argument(at: 0) as! String
            let enviourment = command.argument(at: 1) as! String
            let appID = command.argument(at: 2) as! String
            Datadog.initialize(
                appContext: .init(),
                trackingConsent: .granted,
                configuration: Datadog.Configuration
                .builderUsing(
                    rumApplicationID: appID,
                    clientToken: clientToken,
                    environment: enviourment
                )
                .trackUIKitRUMViews()
                .enableCrashReporting(using: DDCrashReportingPlugin())
                .build()
            )
            Global.rum = RUMMonitor.initialize()
            if self.wkSessionId.compare(" ") != .orderedSame {
                Global.rum.addAttribute(forKey: "wk_UniqueIDForSession", value: wkSessionId)
            }
            let result = CDVPluginResult.init(status: CDVCommandStatus_OK)
            self.commandDelegate.send(result, callbackId: command.callbackId)
        }else{
            let result = CDVPluginResult.init(status: CDVCommandStatus_OK, messageAs: "Already Initialized!")
            self.commandDelegate.send(result, callbackId: command.callbackId)
        }
    }
    @objc(crashtest:)func crashtest(command : CDVInvokedUrlCommand){
        fatalError();
    }
    @objc(setCustomFieldSessionId:)func setCustomFieldSessionId(command : CDVInvokedUrlCommand){
        
        wkSessionId = command.argument(at: 0) as! String
        if isInitialized {
            if self.wkSessionId.compare(" ") != .orderedSame {
                Global.rum.removeAttribute(forKey: "wk_UniqueIDForSession")
            }
            Global.rum.addAttribute(forKey: "wk_UniqueIDForSession", value: wkSessionId)
        }
        let result = CDVPluginResult.init(status: CDVCommandStatus_OK)
        self.commandDelegate.send(result, callbackId: command.callbackId)
    }
    @objc(getSessionId:)func getSessionId(command : CDVInvokedUrlCommand){
        let uuid = UUID().uuidString
        let result = CDVPluginResult.init(status: CDVCommandStatus_OK, messageAs: uuid)
        self.commandDelegate.send(result, callbackId: command.callbackId)
    }

    @objc(setTrackingConsent:)func setTrackingConsent(command : CDVInvokedUrlCommand){
        
        let result = CDVPluginResult.init(status: CDVCommandStatus_OK)
        self.commandDelegate.send(result, callbackId: command.callbackId)
    }
}
