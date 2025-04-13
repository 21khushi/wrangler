/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

 package io.cdap.wrangler.api.parser;

 import com.google.gson.JsonElement;
 import com.google.gson.JsonPrimitive;
 
 import java.util.Locale;
 
 /**
  * Token that represents a time duration value (e.g., "10ms", "1.5s", "2m", "1h").
  * Supports conversion to milliseconds.
  */
 public class TimeDuration implements Token {
   private final double value;
   private final String unit;
   private final long milliseconds;
   private final String rawValue;
 
   public TimeDuration(String valueStr) {
     this.rawValue = valueStr;
 
     String trimmed = valueStr.trim().toLowerCase(Locale.ENGLISH);
     int i = 0;
     while (i < trimmed.length() && (Character.isDigit(trimmed.charAt(i)) || trimmed.charAt(i) == '.')) {
       i++;
     }
 
     if (i == 0 || i >= trimmed.length()) {
       throw new IllegalArgumentException("Invalid time duration: " + valueStr);
     }
 
     String numberPart = trimmed.substring(0, i);
     unit = trimmed.substring(i);
 
     try {
       value = Double.parseDouble(numberPart);
     } catch (NumberFormatException e) {
       throw new IllegalArgumentException("Invalid numeric value in time duration: " + valueStr);
     }
 
     switch (unit) {
       case "ms":
         milliseconds = Math.round(value);
         break;
       case "s":
         milliseconds = Math.round(value * 1000);
         break;
       case "m":
         milliseconds = Math.round(value * 60 * 1000);
         break;
       case "h":
         milliseconds = Math.round(value * 60 * 60 * 1000);
         break;
       case "d":
         milliseconds = Math.round(value * 24 * 60 * 60 * 1000);
         break;
       default:
         throw new IllegalArgumentException("Unsupported time unit: " + unit + " in value: " + valueStr);
     }
   }
 
   @Override
   public Object value() {
     return milliseconds;
   }
 
   @Override
   public TokenType type() {
     return TokenType.TIME_DURATION;
   }
 
   @Override
   public JsonElement toJson() {
     return new JsonPrimitive(toString());
   }
 
   public long getMilliseconds() {
     return milliseconds;
   }
 
   public double getValue() {
     return value;
   }
 
   public String getUnit() {
     return unit;
   }
   public long getNanos() {
    return milliseconds * 1_000_000L;
  }
 
   @Override
   public String toString() {
     return value + unit;
   }
 }
 