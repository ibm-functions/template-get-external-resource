/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package packages

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterAll
import org.scalatest.junit.JUnitRunner
import common.TestUtils.RunResult
import common.{TestHelpers, Wsk, WskProps, WskTestHelpers}
import java.io._
import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.SSLConfig
import spray.json._
import spray.json.DefaultJsonProtocol._

@RunWith(classOf[JUnitRunner])
class WeatherTests
    extends TestHelpers
    with WskTestHelpers
    with BeforeAndAfterAll {

  implicit val wskprops = WskProps()
  val wsk = new Wsk()

  val deployTestRepo =
    "https://github.com/ibm-functions/template-get-external-resource"
  val getExternalResourceAction = "weather"
  val deployAction = "/whisk.system/deployWeb/wskdeploy"
  val deployActionURL =
    s"https://${wskprops.apihost}/api/v1/web${deployAction}.http"
  val packageName = "myPackage"

  //set parameters for deploy tests
  val nodejsRuntimePath = "runtimes/nodejs"
  val nodejsfolder = "../runtimes/nodejs/actions";
  val nodejskind = "nodejs:10"
  val pythonRuntimePath = "runtimes/python"
  val pythonfolder = "../runtimes/python/actions";
  val pythonkind = "python:3.7"

  // statuses from deployWeb
  val successStatus = """"status": "success""""

  behavior of "Get External Resource Template"

  // test to create the get external resource template from github url.  Will use preinstalled folder.
  it should "create the nodejs 10 get external resource action from github url" in {
    val timestamp: String = System.currentTimeMillis.toString
    val nodejsPackage = packageName + timestamp
    val nodejsGetResourceAction = nodejsPackage + "/" + getExternalResourceAction

    makePostCallWithExpectedResult(
      JsObject(
        "gitUrl" -> JsString(deployTestRepo),
        "manifestPath" -> JsString(nodejsRuntimePath),
        "envData" -> JsObject("PACKAGE_NAME" -> JsString(nodejsPackage)),
        "wskApiHost" -> JsString(wskprops.apihost),
        "wskAuth" -> JsString(wskprops.authKey)
      ),
      successStatus,
      200
    );

    withActivation(wsk.activation, wsk.action.invoke(nodejsGetResourceAction)) {
      _.response.result.get.toString should include("location")
    }

    val action = wsk.action.get(nodejsGetResourceAction)
    verifyAction(action, nodejsGetResourceAction, JsString(nodejskind))

    // clean up after test
    wsk.action.delete(nodejsGetResourceAction)
  }

  // test to create the get external resource template from github url.  Will use preinstalled folder.
  it should "create the python get external resource action from github url" in {
    val timestamp: String = System.currentTimeMillis.toString
    val pythonPackage = packageName + timestamp
    val pythonGetResourceAction = pythonPackage + "/" + getExternalResourceAction

    makePostCallWithExpectedResult(
      JsObject(
        "gitUrl" -> JsString(deployTestRepo),
        "manifestPath" -> JsString(pythonRuntimePath),
        "envData" -> JsObject("PACKAGE_NAME" -> JsString(pythonPackage)),
        "wskApiHost" -> JsString(wskprops.apihost),
        "wskAuth" -> JsString(wskprops.authKey)
      ),
      successStatus,
      200
    );

    withActivation(wsk.activation, wsk.action.invoke(pythonGetResourceAction)) {
      _.response.result.get.toString should include("location")
    }

    val action = wsk.action.get(pythonGetResourceAction)
    verifyAction(action, pythonGetResourceAction, JsString(pythonkind))

    // clean up after test
    wsk.action.delete(pythonGetResourceAction)
  }

  /**
    * Test the nodejs-8 "Get External Resource" template
    */
  it should "invoke nodejs 10 weather.js and get the result" in withAssetCleaner(
    wskprops) { (wp, assetHelper) =>
    val timestamp: String = System.currentTimeMillis.toString
    val name = "weatherNodeJS" + timestamp
    val file = Some(new File(nodejsfolder, "weather.js").toString());
    assetHelper.withCleaner(wsk.action, name) { (action, _) =>
      action.create(name, file, kind = Some(nodejskind))
    }

    withActivation(wsk.activation,
                   wsk.action.invoke(name, Map("location" -> "Paris".toJson))) {
      activation =>
        activation.response.success shouldBe true
        activation.response.result.get.toString should include("location")
    }
  }

  it should "invoke nodejs 10 weather.js without input and get weather for Vermont" in withAssetCleaner(
    wskprops) { (wp, assetHelper) =>
    val timestamp: String = System.currentTimeMillis.toString
    val name = "weatherNodeJS" + timestamp
    val file = Some(new File(nodejsfolder, "weather.js").toString());
    assetHelper.withCleaner(wsk.action, name) { (action, _) =>
      action.create(name, file, kind = Some(nodejskind))
    }

    withActivation(wsk.activation, wsk.action.invoke(name)) { activation =>
      activation.response.success shouldBe true
      activation.response.result.get.toString should include("location")
    }
  }

  /**
    * Test the python "Get External Resource" template
    */
  it should "invoke weather.py and get the result" in withAssetCleaner(wskprops) {
    (wp, assetHelper) =>
      val timestamp: String = System.currentTimeMillis.toString
      val name = "weatherPython" + timestamp
      val file = Some(new File(pythonfolder, "weather.py").toString());
      assetHelper.withCleaner(wsk.action, name) { (action, _) =>
        action.create(name, file, kind = Some(pythonkind))
      }

      withActivation(
        wsk.activation,
        wsk.action.invoke(name, Map("location" -> "Paris".toJson))) {
        activation =>
          activation.response.success shouldBe true
          activation.response.result.get.toString should include("location")
      }
  }
  it should "invoke weather.py without input and get weather for Vermont" in withAssetCleaner(
    wskprops) { (wp, assetHelper) =>
    val timestamp: String = System.currentTimeMillis.toString
    val name = "weatherPython" + timestamp
    val file = Some(new File(pythonfolder, "weather.py").toString());
    assetHelper.withCleaner(wsk.action, name) { (action, _) =>
      action.create(name, file, kind = Some(pythonkind))
    }

    withActivation(wsk.activation, wsk.action.invoke(name)) { activation =>
      activation.response.success shouldBe true
      activation.response.result.get.toString should include("location")
    }
  }

  private def makePostCallWithExpectedResult(params: JsObject,
                                             expectedResult: String,
                                             expectedCode: Int) = {
    val response = RestAssured
      .given()
      .contentType("application/json\r\n")
      .config(
        RestAssured
          .config()
          .sslConfig(new SSLConfig().relaxedHTTPSValidation()))
      .body(params.toString())
      .post(deployActionURL)
    assert(response.statusCode() == expectedCode)
    response.body.asString should include(expectedResult)
    response.body.asString.parseJson.asJsObject
      .getFields("activationId") should have length 1
  }

  private def verifyAction(action: RunResult,
                           name: String,
                           kindValue: JsString): Unit = {
    val stdout = action.stdout
    assert(stdout.startsWith(s"ok: got action $name\n"))
    wsk
      .parseJsonString(stdout)
      .fields("exec")
      .asJsObject
      .fields("kind") shouldBe kindValue
  }
}
