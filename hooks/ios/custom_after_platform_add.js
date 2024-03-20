const fs = require('fs');
const path = require('path');

module.exports = function (context) {
    const platformRoot = path.join(context.opts.projectRoot, 'platforms/ios');
    const podfilePath = path.join(platformRoot, 'Podfile');

    // Check if the custom Podfile exists
    const datadogSDKPodfile = path.join(context.opts.projectRoot, 'pods/DatadogSDK');
    if (fs.existsSync(datadogSDKPodfile)) {
        fs.copyFileSync(datadogSDKPodfile, podfilePath);
    }

    // Check if the custom Podfile exists
    const datadogSDKCrashReportingPodfile = path.join(context.opts.projectRoot, 'pods/DatadogSDKCrashReporting');
    if (fs.existsSync(datadogSDKCrashReportingPodfile)) {
        fs.copyFileSync(datadogSDKCrashReportingPodfile, podfilePath);
    }


};
