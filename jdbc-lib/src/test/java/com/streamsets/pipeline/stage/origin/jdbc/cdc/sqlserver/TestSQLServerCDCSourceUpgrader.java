/*
 * Copyright 2017 StreamSets Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.stage.origin.jdbc.cdc.sqlserver;

import com.google.common.collect.ImmutableMap;
import com.streamsets.pipeline.api.Config;
import com.streamsets.pipeline.api.StageException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSQLServerCDCSourceUpgrader {
  private static final String TABLECONFIG = "cdcTableJdbcConfigBean.tableConfigs";
  private static final String SCHEMA_CONFIG = "schema";
  private static final String TABLEPATTERN_CONFIG = "tablePattern";
  private static final String TABLE_EXCLUSION_CONFIG = "tableExclusionPattern";
  private static final String TABLE_INITIALOFFSET_CONFIG = "initialOffset";
  private static final String TABLE_CAPTURE_INSTANCE_CONFIG = "capture_instance";

  @Test
  public void testUpgradeV1toV2WithBasicConfig() throws StageException {
    List<Config> configs = new ArrayList<>();
    List<Map<String, String>> oldTableConfigs = new ArrayList<>();

    final String schema1 = "dbo";
    final String table1 = "table1_%";

    Map<String, String> tableConfig1 = ImmutableMap.of(SCHEMA_CONFIG, schema1, TABLEPATTERN_CONFIG, table1);

    oldTableConfigs.add(tableConfig1);

    configs.add(new Config(TABLECONFIG, oldTableConfigs));

    Assert.assertEquals(1, configs.size());

    SQLServerCDCSourceUpgrader sqlServerCDCSourceUpgrader = new SQLServerCDCSourceUpgrader();
    sqlServerCDCSourceUpgrader.upgrade("a", "b", "c", 1, 2, configs);

    Assert.assertEquals(1, configs.size());

    ArrayList<HashMap<String, String>> tableConfigs = (ArrayList<HashMap<String, String>>)configs.get(0).getValue();
    Assert.assertEquals(1, tableConfigs.size());

    HashMap<String, String> tableConfig = tableConfigs.get(0);

    Assert.assertEquals(schema1 + "_" + table1, tableConfig.get(TABLE_CAPTURE_INSTANCE_CONFIG));
    Assert.assertFalse(tableConfig.containsKey(TABLE_EXCLUSION_CONFIG));
    Assert.assertFalse(tableConfig.containsKey(TABLE_INITIALOFFSET_CONFIG));
  }

  @Test
  public void testUpgradeV1toV2WithFilledConfig() throws StageException {
    List<Config> configs = new ArrayList<>();
    List<Map<String, String>> oldTableConfigs = new ArrayList<>();

    final String schema1 = "dbo";
    final String table1 = "table1_%";
    final String exclusion1 = "exception-%";
    final String initalOffset1 = "0000";

    Map<String, String> tableConfig1 = ImmutableMap.of(
        SCHEMA_CONFIG, schema1,
        TABLEPATTERN_CONFIG, table1,
        TABLE_EXCLUSION_CONFIG, exclusion1,
        TABLE_INITIALOFFSET_CONFIG, initalOffset1
    );

    oldTableConfigs.add(tableConfig1);

    configs.add(new Config(TABLECONFIG, oldTableConfigs));

    Assert.assertEquals(1, configs.size());

    SQLServerCDCSourceUpgrader sqlServerCDCSourceUpgrader = new SQLServerCDCSourceUpgrader();
    sqlServerCDCSourceUpgrader.upgrade("a", "b", "c", 1, 2, configs);

    Assert.assertEquals(1, configs.size());

    ArrayList<HashMap<String, String>> tableConfigs = (ArrayList<HashMap<String, String>>)configs.get(0).getValue();
    Assert.assertEquals(1, tableConfigs.size());

    HashMap<String, String> tableConfig = tableConfigs.get(0);

    Assert.assertEquals(schema1 + "_" + table1, tableConfig.get(TABLE_CAPTURE_INSTANCE_CONFIG));
    Assert.assertEquals(exclusion1, tableConfig.get(TABLE_EXCLUSION_CONFIG));
    Assert.assertEquals(initalOffset1, tableConfig.get(TABLE_INITIALOFFSET_CONFIG));
  }


}
