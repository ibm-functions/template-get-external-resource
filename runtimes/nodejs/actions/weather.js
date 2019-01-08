/**
  * main() will be invoked when you Run This Action.
  *
  * When enabled as a Web Action, use the following URL to invoke this action:
  * https://{APIHOST}/api/v1/web/{QUALIFIED ACTION NAME}?location=Austin
  *
  * For example:
  * https://openwhisk.ng.bluemix.net/api/v1/web/myusername@us.ibm.com_myspace/get-resource/weather?location=Austin
  *
  * In this case, the params variable will look like:
  *     { "location": "Austin" }
  *
  */

const needle = require('needle');

async function main({location = 'Austin'}) {
  try {
    let response = await needle('get', `https://httpbin.org/anything?location=${location}`, { headers: { 'accept': 'application/json' } });
    return {
      statusCode: 200,
      headers: { 'Content-Type': 'application/json' },
      body: {location: response.body.args.location},
    };
  } catch (err) {
    console.log(err)
    return Promise.reject({
      statusCode: 500,
      headers: { 'Content-Type': 'application/json' },
      body: { message: err.message },
    });
  }
}
exports.main = main;
