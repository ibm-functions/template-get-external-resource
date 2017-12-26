import sys
import requests

# main() will be invoked when you invoke this action.
#
# When enabled as a web action, use the following URL to invoke this action:
# https://{APIHOST}/api/v1/web/{QUALIFIED ACTION NAME}?location=Austin
#
# For example:
# https://openwhisk.ng.bluemix.net/api/v1/web/myusername@us.ibm.com_myspace/Get%20Resource/weather?location=Austin
#
# In this case, the params variable will look like:
# { "location": "Austin" }

def main(params):
    if 'location' not in params:
        params.update({'location': 'Austin'})
    location = params['location']
    url = """https://query.yahooapis.com/v1/public/yql?q=select item.condition from weather.forecast where woeid in (select woeid from geo.places(1) where text='%s')&format=json""" % location
    r = requests.get(url)
    print(r.json()['query']['results']['channel']['item']['condition'])
    if r.status_code != 200:
        return {
            'statusCode': r.status_code,
            'headers': { 'Content-Type': 'application/json'},
            'body': {'message': 'Error procesisng your request'}
        }
    else:
        # The response body contains temperature data in the following format
        #   { code: '28',
        #   date: 'Tue, 26 Dec 2017 12:00 PM EST',
        #   temp: '18',
        #   text: 'Mostly Cloudy' } }
        return {
            'statusCode': 200,
            'headers': { 'Content-Type': 'application/json'},
            'body': r.json()['query']['results']['channel']['item']['condition']
        }
