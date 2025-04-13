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
  * Token that represents a byte size value (e.g., "10KB", "1.5MB", "2GB").
  * Converts the value into a canonical byte representation.
  */
 public class ByteSize implements Token {
   private final double value;
   private final String unit;
   private final long bytes;
   private final String rawValue;
 
   public ByteSize(String valueStr) {
     this.rawValue = valueStr;
 
     String trimmed = valueStr.trim().toLowerCase(Locale.ENGLISH);
     int i = 0;
     while (i < trimmed.length() && (Character.isDigit(trimmed.charAt(i)) || trimmed.charAt(i) == '.')) {
       i++;
     }
 
     if (i == 0 || i >= trimmed.length()) {
       throw new IllegalArgumentException("Invalid byte size: " + valueStr);
     }
 
     String numberPart = trimmed.substring(0, i);
     unit = trimmed.substring(i);
 
     try {
       value = Double.parseDouble(numberPart);
     } catch (NumberFormatException e) {
       throw new IllegalArgumentException("Invalid numeric value in byte size: " + valueStr);
     }
 
     switch (unit) {
       case "b":
         bytes = Math.round(value);
         break;
       case "kb":
         bytes = Math.round(value * 1024);
         break;
       case "mb":
         bytes = Math.round(value * 1024 * 1024);
         break;
       case "gb":
         bytes = Math.round(value * 1024 * 1024 * 1024);
         break;
       case "tb":
         bytes = Math.round(value * 1024L * 1024 * 1024 * 1024);
         break;
       case "pb":
         bytes = Math.round(value * 1024L * 1024 * 1024 * 1024 * 1024);
         break;
       default:
         throw new IllegalArgumentException("Unsupported byte unit: " + unit + " in value: " + valueStr);
     }
   }
 
   @Override
   public Object value() {
     return bytes;
   }
 
   @Override
   public TokenType type() {
     return TokenType.BYTE_SIZE;
   }
 
   @Override
   public JsonElement toJson() {
     return new JsonPrimitive(toString());
   }
 
   public long getBytes() {
     return bytes;
   }
 
   public double getValue() {
     return value;
   }
 
   public String getUnit() {
     return unit;
   }
 
   @Override
   public String toString() {
     return value + unit;
   }
 }
 