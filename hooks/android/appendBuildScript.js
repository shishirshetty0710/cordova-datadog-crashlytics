
var fs = require('fs');
var path = require('path');

module.exports = function (context) {
    
    console.log("Start changing gradle!");
    var Q = require("q");
    var deferral = new Q.defer();

    var projectRoot = context.opts.cordova.project ? context.opts.cordova.project.root : context.opts.projectRoot;
    var gradleFilePath = path.join(projectRoot,"platforms","android","build.gradle");
    if (fs.existsSync(gradleFilePath)) {
        var content = fs.readFileSync(gradleFilePath, "utf8");

        var regexRepo = new RegExp("repositories {","g");
        var regexDep = new RegExp("dependencies {","g");
        //content = content.replace(regexRepo,"repositories {\n mavenCentral()\n maven { url \"https://dl.bintray.com/datadog/datadog-maven\" }");
        content = content.replace(regexDep,"dependencies { \n classpath(\"com.datadoghq:dd-sdk-android-gradle-plugin:1.1.0\")");
        fs.writeFileSync(gradleFilePath, content);
        console.log("Finished appending buildscript!");
    }else{
        console.log("Error could not find gradle!");
    }
    deferral.resolve();

    return deferral.promise;
}
