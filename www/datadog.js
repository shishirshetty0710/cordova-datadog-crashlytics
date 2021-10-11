var exec = require('cordova/exec');

exports.init = function (success, error,clientToken,enviourment,appID) {
    exec(success, error, 'Datadog', 'Init', [clientToken,enviourment,appID]);
};
exports.crashtest = function (success, error) {
    exec(success, error, 'Datadog', 'crashtest',[]);
};

exports.getSessionId = function (success, error) {
    exec(success, error, 'Datadog', 'getSessionId',[]);
};

exports.setCustomFieldSessionId = function (success, error,browserSessionId) {
    exec(success, error, 'Datadog', 'setCustomFieldSessionId',[browserSessionId]);
};
