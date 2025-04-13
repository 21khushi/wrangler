
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


 package io.cdap.wrangler.parser;
import io.cdap.wrangler.api.parser.TimeDuration;
import org.junit.Assert;
import org.junit.Test;

public class TimeDurationTest {
  @Test
  public void testTimeDurationParsing() {
    Assert.assertEquals(5000000L, new TimeDuration("5ms").getNanos());
    Assert.assertEquals(2100000000L, new TimeDuration("2.1s").getNanos());
    Assert.assertEquals(60000000000L, new TimeDuration("1m").getNanos());
    Assert.assertEquals(3600000000000L, new TimeDuration("1h").getNanos());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTimeDuration() {
    new TimeDuration("invalid");
  }
}
