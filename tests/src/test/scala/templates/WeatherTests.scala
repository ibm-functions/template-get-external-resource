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
import common.{TestHelpers, Wsk, WskProps, WskTestHelpers}
import java.io._
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json.pimpAny

@RunWith(classOf[JUnitRunner])
class WeatherTests extends TestHelpers
    with WskTestHelpers
    with BeforeAndAfterAll {

    implicit val wskprops = WskProps()
    val wsk = new Wsk()

    //set parameters for deploy tests
    val nodejs6folder = "../runtimes/nodejs-6/actions";
    val nodejs8folder = "../runtimes/nodejs/actions";
    val pythonfolder = "../runtimes/python/actions";

    behavior of "Get External Resource Template"

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
