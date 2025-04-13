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
package io.cdap.wrangler.steps.transform;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.Executor;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.annotations.PublicEvolving;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.List;

@PublicEvolving
public class AggregateStats implements Directive, Executor<List<Row>, List<Row>> {
    private String sourceSizeColumn;
    private String sourceTimeColumn;
    private String targetSizeColumn;
    private String targetTimeColumn;

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
        builder.define("sourceSizeColumn", TokenType.TEXT);
        builder.define("sourceTimeColumn", TokenType.TEXT);
        builder.define("targetSizeColumn", TokenType.TEXT);
        builder.define("targetTimeColumn", TokenType.TEXT);
        return builder.build();
    }

    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        this.sourceSizeColumn = args.value("sourceSizeColumn");
        this.sourceTimeColumn = args.value("sourceTimeColumn");
        this.targetSizeColumn = args.value("targetSizeColumn");
        this.targetTimeColumn = args.value("targetTimeColumn");
        
        if (sourceSizeColumn == null || sourceSizeColumn.isEmpty()) {
            throw new DirectiveParseException("Source size column cannot be null or empty");
        }
        if (sourceTimeColumn == null || sourceTimeColumn.isEmpty()) {
            throw new DirectiveParseException("Source time column cannot be null or empty");
        }
        if (targetSizeColumn == null || targetSizeColumn.isEmpty()) {
            throw new DirectiveParseException("Target size column cannot be null or empty");
        }
        if (targetTimeColumn == null || targetTimeColumn.isEmpty()) {
            throw new DirectiveParseException("Target time column cannot be null or empty");
        }
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context)
            throws DirectiveExecutionException {

        long totalBytes = 0;
        long totalMilliseconds = 0;
        int count = 0;

        for (Row row : rows) {
            try {
                Object sizeObj = row.getValue(sourceSizeColumn);
                Object timeObj = row.getValue(sourceTimeColumn);

                if (sizeObj == null || timeObj == null) {
                    throw new DirectiveExecutionException(
                        String.format("Row is missing values for required columns '%s' or '%s'",
                            sourceSizeColumn, sourceTimeColumn));
                }

                if (!(sizeObj instanceof ByteSize)) {
                    throw new DirectiveExecutionException(
                        String.format("Column '%s' must contain ByteSize values", sourceSizeColumn));
                }
                if (!(timeObj instanceof TimeDuration)) {
                    throw new DirectiveExecutionException(
                        String.format("Column '%s' must contain TimeDuration values", sourceTimeColumn));
                }

                totalBytes += ((ByteSize) sizeObj).getBytes();
                totalMilliseconds += ((TimeDuration) timeObj).getMilliseconds();
                count++;
            } catch (Exception e) {
                throw new DirectiveExecutionException(
                    String.format("Error processing row: %s", e.getMessage()));
            }
        }

        Row result = new Row();
        result.add(targetSizeColumn, totalBytes);
        result.add(targetTimeColumn, totalMilliseconds);
        result.add("count", count);

        return List.of(result);
    }

    @Override
    public void destroy() {
        // Clean up resources if needed
    }
}
