/*
 * Copyright 2017-2021 Cloudopt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cloudopt.next.web.config

import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Maper
import kotlin.reflect.KClass


/*
 * @author: Cloudopt
 * @Time: 2018/1/15
 * @Description: Used to manage the configuration file
 */
object ConfigManager {

    private const val CONFIG_JSON_FILENAME = "application.json"

    @JvmStatic
    var config: WebConfigBean = WebConfigBean()

    // Init web config
    var configMap: MutableMap<String, Any> = mutableMapOf()

    val logger = Logger.getLogger(ConfigManager::class)

    init {

        try {
            configMap = Jsoner.read(CONFIG_JSON_FILENAME)
        } catch (e: RuntimeException) {
            logger.warn("[COFIG] Configuration we not found!")
        }

        config = Maper.toObject(configMap, WebConfigBean::class) as WebConfigBean

        if (config.env.isNotBlank()) {
            val newConfigFileName = "application-${config.env}.json"
            configMap.putAll(Jsoner.read(newConfigFileName))
        }

    }

    /**
     * Get the data in the configuration file according to the specified prefix and convert it to a map object.
     * @param prefix prefix name
     * @return MutableMap<String, Any>
     */
    @JvmStatic
    open fun init(prefix: String): MutableMap<String, Any> {
        var newMap = configMap.toMutableMap()
        for (key in prefix.split(".")) {
            if (newMap[key] != null) {
                newMap = newMap[key] as MutableMap<String, Any>
            } else {
                return mutableMapOf()
            }
        }
        return newMap
    }

    /**
     * Get the data in the configuration file according to the specified prefix and convert to the specified object.
     * @param prefix prefix name
     * @param clazz class name
     * @return MutableMap<String, Any>
     */
    @JvmStatic
    open fun initObject(prefix: String, clazz: KClass<*>): Any {
        return Jsoner.toObject(Jsoner.toJsonString(init(prefix)), clazz)
    }
}