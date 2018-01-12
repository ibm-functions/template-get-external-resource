# template-get-external-resource

### Overview
You can use this template to deploy some IBM Cloud Functions assets for you.  The assets created by this template are described in the manifest.yaml file, which can be found at `template-get-external-resource/runtimes/your_language_choice/manifest.yaml`

The only assets described by this get external resource template are a single action, named weather, which takes as input a location parameter.

You can use the wskdeploy tool to deploy this asset yourself using the manifest and available code.

You can invoke this asset via web using `curl https://openwhisk.ng.bluemix.net/api/v1/web/<namespace>/$PACKAGE_NAME/weather?location=Paris`

For example:
`curl https://openwhisk.ng.bluemix.net/api/v1/web/myusername@us.ibm.com_myspace/Get%20Resource/weather?location=Austin`

### Available Languages
This template is available in node.js and python.
