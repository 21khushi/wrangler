

/*
 *  Copyright © 2017-2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.cdap.wrangler.directive;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.TestingRig;  // ✅ Adjusted import to match your setup
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsTest {

  @Test
  public void testAggregateStats() throws Exception {
    // Sample input data: 1MB + 512KB = 1.5MB; 1s + 500ms = 1.5s
    List<Row> rows = Arrays.asList(
        new Row().add("data_size", "1MB").add("latency", "1s"),
        new Row().add("data_size", "512KB").add("latency", "500ms")
    );

    // Recipe using your custom directive
    String[] recipe = new String[] {
        "aggregate-stats :data_size :latency total_mb total_sec"
    };

    // Run the recipe
    List<Row> result = TestingRig.execute(recipe, rows);

    // Validate
    Assert.assertEquals(1, result.size());

    Row output = result.get(0);
    double expectedMB = 1.5;
    double expectedSec = 1.5;

    Assert.assertEquals(expectedMB, (Double) output.getValue("total_mb"), 0.001);
    Assert.assertEquals(expectedSec, (Double) output.getValue("total_sec"), 0.001);
  }
}
