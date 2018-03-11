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
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json._

import spray.json.DefaultJsonProtocol._

@RunWith(classOf[JUnitRunner])
class WeatherTests extends TestHelpers
    with WskTestHelpers
    with BeforeAndAfterAll {

    implicit val wskprops = WskProps()
    val wsk = new Wsk()

    val deployTestRepo = "https://github.com/ibm-functions/template-get-external-resource"
    val getExternalResourceActionPackage = "myPackage/weather"
    val deployAction = "/whisk.system/deployWeb/wskdeploy"
    val deployActionURL = s"https://${wskprops.apihost}/api/v1/web${deployAction}.http"

    //set parameters for deploy tests
    val node8RuntimePath = "runtimes/nodejs"
    val nodejs8folder = "../runtimes/nodejs/actions";
    val nodejs8kind = JsString("nodejs:8")
    val node6RuntimePath = "runtimes/nodejs-6"
    val nodejs6folder = "../runtimes/nodejs-6/actions";
    val nodejs6kind = JsString("nodejs:6")
    val pythonRuntimePath = "runtimes/python"
    val pythonfolder = "../runtimes/python/actions";
    val pythonkind = JsString("python-jessie:3")

    // statuses from deployWeb
    val successStatus = """"status":"success""""

    def makePostCallWithExpectedResult(params: JsObject, expectedResult: String, expectedCode: Int) = {
      val response = RestAssured.given()
          .contentType("application/json\r\n")
          .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
          .body(params.toString())
          .post(deployActionURL)
      assert(response.statusCode() == expectedCode)
      response.body.asString should include(expectedResult)
      response.body.asString.parseJson.asJsObject.getFields("activationId") should have length 1
    }

    def verifyAction(action: RunResult, name: String, kindValue: JsString): Unit = {
      val stdout = action.stdout
      assert(stdout.startsWith(s"ok: got action $name\n"))
      wsk.parseJsonString(stdout).fields("exec").asJsObject.fields("kind") shouldBe kindValue
    }

    behavior of "Get External Resource Template"

    // test to create the get external resource template from github url.  Will use preinstalled folder.
    it should "create the nodejs 8 get external resource action from github url" in {
      makePostCallWithExpectedResult(JsObject(
        "gitUrl" -> JsString(deployTestRepo),
        "manifestPath" -> JsString(node8RuntimePath),
        "envData" -> JsObject("PACKAGE_NAME" -> JsString("myPackage")),
        "wskApiHost" -> JsString(wskprops.apihost),
        "wskAuth" -> JsString(wskprops.authKey)
      ), successStatus, 200);

      withActivation(wsk.activation, wsk.action.invoke(getExternalResourceActionPackage)) {
        _.response.result.get.toString should include("temp")
      }

      val action = wsk.action.get(getExternalResourceActionPackage)
      verifyAction(action, getExternalResourceActionPackage, nodejs8kind)

      // clean up after test
      wsk.action.delete(getExternalResourceActionPackage)
    }

    // test to create the get external resource template from github url.  Will use preinstalled folder.
    it should "create the nodejs 6 get external resource action from github url" in {
      makePostCallWithExpectedResult(JsObject(
        "gitUrl" -> JsString(deployTestRepo),
        "manifestPath" -> JsString(node6RuntimePath),
        "envData" -> JsObject("PACKAGE_NAME" -> JsString("myPackage")),
        "wskApiHost" -> JsString(wskprops.apihost),
        "wskAuth" -> JsString(wskprops.authKey)
      ), successStatus, 200);

      withActivation(wsk.activation, wsk.action.invoke(getExternalResourceActionPackage)) {
        _.response.result.get.toString should include("temp")
      }

      val action = wsk.action.get(getExternalResourceActionPackage)
      verifyAction(action, getExternalResourceActionPackage, nodejs6kind)

      // clean up after test
      wsk.action.delete(getExternalResourceActionPackage)
    }

    // test to create the get external resource template from github url.  Will use preinstalled folder.
    it should "create the python get external resource action from github url" in {
      makePostCallWithExpectedResult(JsObject(
        "gitUrl" -> JsString(deployTestRepo),
        "manifestPath" -> JsString(pythonRuntimePath),
        "envData" -> JsObject("PACKAGE_NAME" -> JsString("myPackage")),
        "wskApiHost" -> JsString(wskprops.apihost),
        "wskAuth" -> JsString(wskprops.authKey)
      ), successStatus, 200);

      withActivation(wsk.activation, wsk.action.invoke(getExternalResourceActionPackage)) {
        _.response.result.get.toString should include("temp")
      }

      val action = wsk.action.get(getExternalResourceActionPackage)
      verifyAction(action, getExternalResourceActionPackage, pythonkind)

      // clean up after test
      wsk.action.delete(getExternalResourceActionPackage)
    }
    /**
     * Test the nodejs 6 "Get External Resource" template
     */
     it should "invoke nodejs-6 weather.js and get the result" in withAssetCleaner(wskprops) { (wp, assetHelper) =>
       val name = "weatherNode6"
       val file = Some(new File(nodejs6folder, "weather.js").toString());
       assetHelper.withCleaner(wsk.action, name) { (action, _) =>
         action.create(name, file)
       }

       withActivation(wsk.activation, wsk.action.invoke(name, Map("location" -> "Paris".toJson))) {
         activation =>
          activation.response.success shouldBe true
          activation.response.result.get.toString should include("temp")
       }
     }
     it should "invoke nodejs-6 weather.js without input and get weather for Vermont" in withAssetCleaner(wskprops) { (wp, assetHelper) =>
        val name = "weatherNode6"
        val file = Some(new File(nodejs6folder, "weather.js").toString());
        assetHelper.withCleaner(wsk.action, name) { (action, _) =>
          action.create(name, file)
        }

        withActivation(wsk.activation, wsk.action.invoke(name)) {
          activation =>
           activation.response.success shouldBe true
           activation.response.result.get.toString should include("temp")
        }
      }

      /**
       * Test the nodejs-8 "Get External Resource" template
       */
       it should "invoke nodejs-8 weather.js and get the result" in withAssetCleaner(wskprops) { (wp, assetHelper) =>
         val name = "weatherNode8"
         val file = Some(new File(nodejs8folder, "weather.js").toString());
         assetHelper.withCleaner(wsk.action, name) { (action, _) =>
           action.create(name, file, kind = Some("nodejs:8"))
         }

         withActivation(wsk.activation, wsk.action.invoke(name, Map("location" -> "Paris".toJson))) {
           activation =>
            activation.response.success shouldBe true
            activation.response.result.get.toString should include("temp")
         }
       }
        it should "invoke nodejs-8 weather.js without input and get weather for Vermont" in withAssetCleaner(wskprops) { (wp, assetHelper) =>
          val name = "weatherNode8"
          val file = Some(new File(nodejs8folder, "weather.js").toString());
          assetHelper.withCleaner(wsk.action, name) { (action, _) =>
            action.create(name, file, kind = Some("nodejs:8"))
          }

          withActivation(wsk.activation, wsk.action.invoke(name)) {
            activation =>
             activation.response.success shouldBe true
             activation.response.result.get.toString should include("temp")
          }
        }

      /**
       * Test the python "Get External Resource" template
       */
       it should "invoke weather.py and get the result" in withAssetCleaner(wskprops) { (wp, assetHelper) =>
         val name = "weatherPython"
         val file = Some(new File(pythonfolder, "weather.py").toString());
         assetHelper.withCleaner(wsk.action, name) { (action, _) =>
           action.create(name, file)
         }

         withActivation(wsk.activation, wsk.action.invoke(name, Map("location" -> "Paris".toJson))) {
           activation =>
            activation.response.success shouldBe true
            activation.response.result.get.toString should include("temp")
          }
       }
        it should "invoke weather.py without input and get weather for Vermont" in withAssetCleaner(wskprops) { (wp, assetHelper) =>
          val name = "weatherPython"
          val file = Some(new File(pythonfolder, "weather.py").toString());
          assetHelper.withCleaner(wsk.action, name) { (action, _) =>
            action.create(name, file)
          }

          withActivation(wsk.activation, wsk.action.invoke(name)) {
            activation =>
             activation.response.success shouldBe true
             activation.response.result.get.toString should include("temp")
          }
        }
}
