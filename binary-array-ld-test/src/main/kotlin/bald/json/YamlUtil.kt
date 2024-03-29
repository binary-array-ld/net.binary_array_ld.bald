package bald.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object YamlUtil {
    val mapper by lazy { ObjectMapper(YAMLFactory()).registerKotlinModule() }
}