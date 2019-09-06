/**
 * Copyright 2019 interactive instruments GmbH
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
package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author zahnen
 */
public class FeatureTypesWithComment extends FeatureTypes implements WithComment {
    //private MappingsSequenceType.Table table;

    @XmlTransient
    String comment;

    /*public TableCommentDecorator(MappingsSequenceType.Table table) {
        this.table = table;
    }*/

    @Override
    public boolean hasComment() {
        return this.comment != null && !this.comment.isEmpty();
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }
}