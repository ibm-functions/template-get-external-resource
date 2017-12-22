/**
  *
  * main() will be invoked when you Run This Action.
  *
  * @param Cloud Functions actions accept a single parameter,
  *        which must be a JSON object.
  *
  * In this case, the params variable will look like:
  *     { "message": "xxxx" }
  *
  * @return which must be a JSON object.
  *         It will be the output of this action.
  *
  */

var request = require('request');

function main(params) {
    var location = params.location || 'Vermont';
    var url = 'https://query.yahooapis.com/v1/public/yql?q=select item.condition from weather.forecast where woeid in (select woeid from geo.places(1) where text="' + location + '")&format=json';

    return new Promise(function(resolve, reject) {
        request.get(url, function(error, response, body) {
            if (error) {
                reject(error);
            }
            else {
                /** the response body contains temperature in condition.temp, for example, 20
                  * and short description in condition.text for example, Partly Cloudy
                  * and date in condition.date, for example, Thu, 21 Dec 2017 06:00 PM EST
                  */
                resolve({
                    statusCode: 200,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.parse(body).query.results.channel.item.condition
                });
            }
        });
    });
}

