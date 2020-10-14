/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.mirror;

import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class MirrorConnectorConfigTest {

    private Map<String, String> makeProps(String... keyValues) {
        Map<String, String> props = new HashMap<>();
        props.put("name", "ConnectorName");
        props.put("connector.class", "ConnectorClass");
        props.put("source.cluster.alias", "source1");
        props.put("target.cluster.alias", "target2");
        for (int i = 0; i < keyValues.length; i += 2) {
            props.put(keyValues[i], keyValues[i + 1]);
        }
        return props;
    }

    @Test
    public void testTaskConfigTopicPartitions() {
        List<TopicPartition> topicPartitions = Arrays.asList(new TopicPartition("topic-1", 2),
            new TopicPartition("topic-3", 4), new TopicPartition("topic-5", 6));
        MirrorConnectorConfig config = new MirrorConnectorConfig(makeProps());
        Map<String, String> props = config.taskConfigForTopicPartitions(topicPartitions);
        MirrorTaskConfig taskConfig = new MirrorTaskConfig(props);
        assertEquals(taskConfig.taskTopicPartitions(), new HashSet<>(topicPartitions));
    }

    @Test
    public void testTaskConfigConsumerGroups() {
        List<String> groups = Arrays.asList("consumer-1", "consumer-2", "consumer-3");
        MirrorConnectorConfig config = new MirrorConnectorConfig(makeProps());
        Map<String, String> props = config.taskConfigForConsumerGroups(groups);
        MirrorTaskConfig taskConfig = new MirrorTaskConfig(props);
        assertEquals(taskConfig.taskConsumerGroups(), new HashSet<>(groups));
    }

    @Test
    public void testTopicMatching() {
        MirrorConnectorConfig config = new MirrorConnectorConfig(makeProps("topics", "topic1"));
        assertTrue(config.topicFilter().shouldReplicateTopic("topic1"));
        assertFalse(config.topicFilter().shouldReplicateTopic("topic2"));
    }

    @Test
    public void testGroupMatching() {
        MirrorConnectorConfig config = new MirrorConnectorConfig(makeProps("groups", "group1"));
        assertTrue(config.groupFilter().shouldReplicateGroup("group1"));
        assertFalse(config.groupFilter().shouldReplicateGroup("group2"));
    }

    @Test
    public void testConfigPropertyMatching() {
        MirrorConnectorConfig config = new MirrorConnectorConfig(
            makeProps("config.properties.blacklist", "prop2"));
        assertTrue(config.configPropertyFilter().shouldReplicateConfigProperty("prop1"));
        assertFalse(config.configPropertyFilter().shouldReplicateConfigProperty("prop2"));
    }

    @Test
    public void testNoTopics() {
        MirrorConnectorConfig config = new MirrorConnectorConfig(makeProps("topics", ""));
        assertFalse(config.topicFilter().shouldReplicateTopic("topic1"));
        assertFalse(config.topicFilter().shouldReplicateTopic("topic2"));
        assertFalse(config.topicFilter().shouldReplicateTopic(""));
    }

    @Test
    public void testAllTopics() {
        MirrorConnectorConfig config = new MirrorConnectorConfig(makeProps("topics", ".*"));
        assertTrue(config.topicFilter().shouldReplicateTopic("topic1"));
        assertTrue(config.topicFilter().shouldReplicateTopic("topic2"));
    }

    @Test
    public void testListOfTopics() {
        MirrorConnectorConfig config = new MirrorConnectorConfig(makeProps("topics", "topic1, topic2"));
        assertTrue(config.topicFilter().shouldReplicateTopic("topic1"));
        assertTrue(config.topicFilter().shouldReplicateTopic("topic2"));
        assertFalse(config.topicFilter().shouldReplicateTopic("topic3"));
    }
<<<<<<< HEAD
=======

    @Test
    public void testConsumerRestOffsetLatestConfs() {
        MirrorConnectorConfig config = new MirrorConnectorConfig(makeProps("consumer.auto.offset.reset", "latest"));
        Map<String, Object> sourceConf = config.sourceConsumerConfig();
        assertEquals("latest", sourceConf.get("auto.offset.reset"));
    }

    @Test
    public void testConsumerDefaultConfs() {
        MirrorConnectorConfig config = new MirrorConnectorConfig(makeProps());
        Map<String, Object> sourceConf = config.sourceConsumerConfig();
        assertEquals("earliest", sourceConf.get("auto.offset.reset"));
    }

    @Test
    public void testNonMutationOfConfigDef() {
        Collection<String> taskSpecificProperties = Arrays.asList(
            MirrorConnectorConfig.TASK_TOPIC_PARTITIONS,
            MirrorConnectorConfig.TASK_CONSUMER_GROUPS
        );

        // Sanity check to make sure that these properties are actually defined for the task config,
        // and that the task config class has been loaded and statically initialized by the JVM
        ConfigDef taskConfigDef = MirrorTaskConfig.TASK_CONFIG_DEF;
        taskSpecificProperties.forEach(taskSpecificProperty -> assertTrue(
            taskSpecificProperty + " should be defined for task ConfigDef",
            taskConfigDef.names().contains(taskSpecificProperty)
        ));

        // Ensure that the task config class hasn't accidentally modified the connector config
        ConfigDef connectorConfigDef = MirrorConnectorConfig.CONNECTOR_CONFIG_DEF;
        taskSpecificProperties.forEach(taskSpecificProperty -> assertFalse(
            taskSpecificProperty + " should not be defined for connector ConfigDef",
            connectorConfigDef.names().contains(taskSpecificProperty)
        ));
    }
>>>>>>> 2eb10ecac... KAFKA-10160: Kafka MM2 consumer configuration (#8921)
}
