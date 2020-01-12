/**
 * Copyright 2020 interactive instruments GmbH
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
package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.collect.ImmutableList;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;

/**
 * @author zahnen
 */
public class MappingExamples {

    public static final XtraServerMapping EXAMPLE_1 =
            new XtraServerMappingBuilder().featureTypeMapping(new FeatureTypeMappingBuilder().name("ci:City")
                                                                                             .primaryTable(new MappingTableBuilder().name("gn_boeschungkliff_pto")
                                                                                                                                    .primaryKey("id")
                                                                                                                                    .values(ImmutableList.of(new MappingValueBuilder().column()
                                                                                                                                                                                      .value("country")
                                                                                                                                                                                      .targetPath("ci:country")
                                                                                                                                                                                      .build()))
                                                                                                                                    .joiningTable(new MappingTableBuilder().name("o61001")
                                                                                                                                                                           .primaryKey("id")
                                                                                                                                                                           .value(new MappingValueBuilder().column()
                                                                                                                                                                                                           .value("objid")
                                                                                                                                                                                                           .targetPath("@gml:id")
                                                                                                                                                                                                           .build())
                                                                                                                                                                           .joiningTable(new MappingTableBuilder().name("o02341__p0000103000")
                                                                                                                                                                                                                  .primaryKey("id")
                                                                                                                                                                                                                  .joiningTable(new MappingTableBuilder().name("o02341")
                                                                                                                                                                                                                                                         .primaryKey("id")
                                                                                                                                                                                                                                                         .targetPath("ci:location")
                                                                                                                                                                                                                                                         .value(new MappingValueBuilder().geometry()
                                                                                                                                                                                                                                                                                         .value("position")
                                                                                                                                                                                                                                                                                         .targetPath("ci:location")
                                                                                                                                                                                                                                                                                         .build())
                                                                                                                                                                                                                                                         .joinPath(new MappingJoinBuilder().joinCondition(new MappingJoinBuilder.ConditionBuilder().sourceTable("o02341__p0000103000")
                                                                                                                                                                                                                                                                                                                                                   .sourceField("rid")
                                                                                                                                                                                                                                                                                                                                                   .targetTable("o02341")
                                                                                                                                                                                                                                                                                                                                                   .targetField("id")
                                                                                                                                                                                                                                                                                                                                                   .build())
                                                                                                                                                                                                                                                                                           .targetPath("TODO")
                                                                                                                                                                                                                                                                                           .build())
                                                                                                                                                                                                                                                         .build())
                                                                                                                                                                                                                  .joinPath(new MappingJoinBuilder().joinCondition(new MappingJoinBuilder.ConditionBuilder().sourceTable("o61001")
                                                                                                                                                                                                                                                                                                            .sourceField("objid")
                                                                                                                                                                                                                                                                                                            .targetTable("o02341__p0000103000")
                                                                                                                                                                                                                                                                                                            .targetField("p0000103000")
                                                                                                                                                                                                                                                                                                            .build())
                                                                                                                                                                                                                                                    .targetPath("TODO")
                                                                                                                                                                                                                                                    .build())
                                                                                                                                                                                                                  .build())
                                                                                                                                                                           .joinPath(new MappingJoinBuilder().joinCondition(new MappingJoinBuilder.ConditionBuilder().sourceTable("gn_boeschungkliff_pto")
                                                                                                                                                                                                                                                                     .sourceField("id")
                                                                                                                                                                                                                                                                     .targetTable("o61001")
                                                                                                                                                                                                                                                                     .targetField("id")
                                                                                                                                                                                                                                                                     .build())
                                                                                                                                                                                                             .targetPath("TODO")
                                                                                                                                                                                                             .build())
                                                                                                                                                                           .build())
                                                                                                                                    .joiningTable(new MappingTableBuilder().name("o61002")
                                                                                                                                                                           .primaryKey("id")
                                                                                                                                                                           .value(new MappingValueBuilder().column()
                                                                                                                                                                                                           .value("name")
                                                                                                                                                                                                           .targetPath("ci:name")
                                                                                                                                                                                                           .build())
                                                                                                                                                                           .joinPath(new MappingJoinBuilder().joinCondition(new MappingJoinBuilder.ConditionBuilder().sourceTable("gn_boeschungkliff_pto")
                                                                                                                                                                                                                                                                     .sourceField("id")
                                                                                                                                                                                                                                                                     .targetTable("o61002")
                                                                                                                                                                                                                                                                     .targetField("id")
                                                                                                                                                                                                                                                                     .build())
                                                                                                                                                                                                             .targetPath("TODO")
                                                                                                                                                                                                             .build())
                                                                                                                                                                           .build())
                                                                                                                                    .build())
                                                                                             .build())
                                          .build();
}
