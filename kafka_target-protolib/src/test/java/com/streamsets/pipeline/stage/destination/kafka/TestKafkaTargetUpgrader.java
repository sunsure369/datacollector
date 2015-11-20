/**
 * Copyright 2015 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.stage.destination.kafka;

import com.streamsets.pipeline.api.Config;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.config.CsvHeader;
import com.streamsets.pipeline.config.CsvMode;
import com.streamsets.pipeline.config.DataFormat;
import com.streamsets.pipeline.config.JsonMode;
import com.streamsets.pipeline.kafka.api.PartitionStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestKafkaTargetUpgrader {

  @Test
  public void testKafkaTargetUpgrader() throws StageException {

    Map<String, String> kafkaProducerConfig = new HashMap();
    kafkaProducerConfig.put("request.required.acks", "2");
    kafkaProducerConfig.put("request.timeout.ms", "2000");

    List<Config> configs = new ArrayList<>();
    configs.add(new Config("metadataBrokerList", "localhost:9001,localhost:9002,localhost:9003"));
    configs.add(new Config("runtimeTopicResolution", true));
    configs.add(new Config("topicExpression", "${record:value('/topic')}"));
    configs.add(new Config("topicWhiteList", "*"));
    configs.add(new Config("topic", null));
    configs.add(new Config("partitionStrategy", PartitionStrategy.EXPRESSION));
    configs.add(new Config("partition", "${record:value('/partition') % 5}"));
    configs.add(new Config("singleMessagePerBatch", true));
    configs.add(new Config("kafkaProducerConfigs", kafkaProducerConfig));
    configs.add(new Config("charset", "UTF-8"));
    configs.add(new Config("dataFormat", DataFormat.TEXT));
    configs.add(new Config("csvFileFormat", CsvMode.EXCEL));
    configs.add(new Config("csvHeader", CsvHeader.WITH_HEADER));
    configs.add(new Config("csvReplaceNewLines", false));
    configs.add(new Config("jsonMode", JsonMode.ARRAY_OBJECTS));
    configs.add(new Config("textFieldPath", "/myField"));
    configs.add(new Config("textEmptyLineIfNull", true));
    configs.add(new Config("avroSchema", "hello!!"));
    configs.add(new Config("includeSchema", true));
    configs.add(new Config("binaryFieldPath", "/binaryField"));

    KafkaTargetUpgrader kafkaTargetUpgrader = new KafkaTargetUpgrader();
    kafkaTargetUpgrader.upgrade("a", "b", "c", 1, 2, configs);

    Assert.assertEquals(23, configs.size());

    HashMap<String, Object> configValues = new HashMap<>();
    for(Config c : configs) {
      configValues.put(c.getName(), c.getValue());
    }

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.kafkaConfig.metadataBrokerList"));
    Assert.assertEquals("localhost:9001,localhost:9002,localhost:9003", configValues.get("kafkaConfigBean.kafkaConfig.metadataBrokerList"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.kafkaConfig.runtimeTopicResolution"));
    Assert.assertEquals(true, configValues.get("kafkaConfigBean.kafkaConfig.runtimeTopicResolution"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.kafkaConfig.topicExpression"));
    Assert.assertEquals("${record:value('/topic')}", configValues.get("kafkaConfigBean.kafkaConfig.topicExpression"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.kafkaConfig.topicWhiteList"));
    Assert.assertEquals("*", configValues.get("kafkaConfigBean.kafkaConfig.topicWhiteList"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.kafkaConfig.topic"));
    Assert.assertEquals(null, configValues.get("kafkaConfigBean.kafkaConfig.topic"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.kafkaConfig.partitionStrategy"));
    Assert.assertEquals(PartitionStrategy.EXPRESSION, configValues.get("kafkaConfigBean.kafkaConfig.partitionStrategy"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.kafkaConfig.partition"));
    Assert.assertEquals("${record:value('/partition') % 5}", configValues.get("kafkaConfigBean.kafkaConfig.partition"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.kafkaConfig.singleMessagePerBatch"));
    Assert.assertEquals(true, configValues.get("kafkaConfigBean.kafkaConfig.singleMessagePerBatch"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.kafkaConfig.kafkaProducerConfigs"));
    Assert.assertEquals(kafkaProducerConfig, configValues.get("kafkaConfigBean.kafkaConfig.kafkaProducerConfigs"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataFormat"));
    Assert.assertEquals(DataFormat.TEXT, configValues.get("kafkaConfigBean.dataFormat"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.charset"));
    Assert.assertEquals("UTF-8", configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.charset"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.csvFileFormat"));
    Assert.assertEquals(CsvMode.EXCEL, configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.csvFileFormat"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.csvHeader"));
    Assert.assertEquals(CsvHeader.WITH_HEADER, configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.csvHeader"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.csvReplaceNewLines"));
    Assert.assertEquals(false, configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.csvReplaceNewLines"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.jsonMode"));
    Assert.assertEquals(JsonMode.ARRAY_OBJECTS, configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.jsonMode"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.textFieldPath"));
    Assert.assertEquals("/myField", configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.textFieldPath"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.textEmptyLineIfNull"));
    Assert.assertEquals(true, configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.textEmptyLineIfNull"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.avroSchema"));
    Assert.assertEquals("hello!!", configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.avroSchema"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.includeSchema"));
    Assert.assertEquals(true, configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.includeSchema"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.binaryFieldPath"));
    Assert.assertEquals("/binaryField", configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.binaryFieldPath"));

    //newly added configs

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.csvCustomDelimiter"));
    Assert.assertEquals('|', configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.csvCustomDelimiter"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.csvCustomEscape"));
    Assert.assertEquals('\\', configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.csvCustomEscape"));

    Assert.assertTrue(configValues.containsKey("kafkaConfigBean.dataGeneratorFormatConfig.csvCustomQuote"));
    Assert.assertEquals('\"', configValues.get("kafkaConfigBean.dataGeneratorFormatConfig.csvCustomQuote"));

  }
}
