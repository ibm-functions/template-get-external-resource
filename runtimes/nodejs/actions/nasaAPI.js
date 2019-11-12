const needle = require('needle');
/*
    Here is a small list with possible sources for the https-request:
        - SIVolcano
        - EO
        - NASA_DISP
        - PDC
*/

let source = "EO"; //change the source here


const limit = 3;
const numberOfDays = 200;
const status = 'open'; //other option is closed
async function main() {
  try {
    let response = await needle('get', `https://eonet.sci.gsfc.nasa.gov/api/v2.1/events?limit=${limit}&days=${numberOfDays}&source=${source}&status=${status}`);
    return {
      statusCode: 200,
      headers: { 'Content-Type': 'application/json' },
      body: {location: response.body.events},
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

