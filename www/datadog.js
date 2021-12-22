var exec = require('cordova/exec');

exports.init = function (success, error,clientToken,enviourment,appID,trackingConsentInt) {
    exec(success, error, 'Datadog', 'Init', [clientToken,enviourment,appID,trackingConsentInt]);
};

exports.getSessionId = function (success, error) {
    exec(success, error, 'Datadog', 'getSessionId',[]);
};

exports.setCustomFieldSessionId = function (success, error,browserSessionId) {
    exec(success, error, 'Datadog', 'setCustomFieldSessionId',[browserSessionId]);
};

exports.setTrackingConsent = function (success, error,trackingConsentInt) {
    exec(success, error, 'Datadog', 'setTrackingConsent',[trackingConsentInt]);
};

