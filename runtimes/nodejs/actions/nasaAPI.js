const needle = require('needle');

async function main() {
  try {
    let response = await needle('get', `https://eonet.sci.gsfc.nasa.gov/api/v2.1/events ?limit=5&days=20&source=InciWeb&status=open`);
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
