# blueprint-get-external-resource

### Overview
You can use this blueprint to deploy some IBM Cloud Functions assets for you.  The assets created by this blueprint are described in the manifest.yaml file, which can be found at `blueprint-get-external-resource/runtimes/your_language_choice/manifest.yaml`

The only assets described by this get external resource blueprint are a single action, named weather, which takes as input a location parameter.

You can use the wskdeploy tool to deploy this asset yourself using the manifest and available code.

You can invoke this asset via web using `curl https://openwhisk.ng.bluemix.net/api/v1/web/<namespace>/$PACKAGE_NAME/weather.json?location=Paris`

### Available Languages
This blueprint is available in node.js.
