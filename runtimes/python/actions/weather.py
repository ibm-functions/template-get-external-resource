import sys
import requests

# main() will be invoked when you invoke this action.
#
# When enabled as a web action, use the following URL to invoke this action:
# https://{APIHOST}/api/v1/web/{QUALIFIED ACTION NAME}?location=Austin
#
# For example:
# https://openwhisk.ng.bluemix.net/api/v1/web/myusername@us.ibm.com_myspace/get-resource/weather?location=Austin
#
# In this case, the params variable will look like:
# { "location": "Austin" }

def main(params):
    if 'location' not in params:
        params.update({'location': 'Austin'})
    location = params['location']
    url = "https://httpbin.org/anything?location=%s" % location
    headers = {'accept': 'application/json'}
    r = requests.get(url,headers)
    if r.status_code != 200:
        return {
            'statusCode': r.status_code,
            'headers': { 'Content-Type': 'application/json'},
            'body': {'message': 'Error procesisng your request'}
        }
    else:
        return {
            'statusCode': 200,
            'headers': { 'Content-Type': 'application/json'},
            'body': {'location': r.json()['args']['location']}
        }
