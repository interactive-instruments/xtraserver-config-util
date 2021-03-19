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

import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;

import java.net.URI;

/**
 * Transformer chain builder for {@link XtraServerMapping}s.
 * Every transformer takes an immutable {@link XtraServerMapping} as input and will generate a new immutable {@link XtraServerMapping} as output.
 *
 * @author zahnen
 */
public class XtraServerMappingTransformer {

    private final XtraServerMapping xtraServerMapping;
    private final ApplicationSchema applicationSchema;
    private final URI applicationSchemaUri;
    private final boolean flattenInheritance;
    private final boolean fanOutInheritance;
    private final boolean ensureRelationNavigability;
    private final boolean fixMultiplicity;
    private final boolean virtualTables;
    private final boolean applyChoicePredicates;
    private final boolean cloneColumns;
    private final boolean joinTypes;
    private final boolean multiJoins;
    private final boolean cleanNilChildren;

    private XtraServerMappingTransformer(final XtraServerMapping xtraServerMapping,
        final URI applicationSchemaUri, final boolean flattenInheritance,
        final boolean fanOutInheritance, final boolean ensureRelationNavigability,
        boolean fixMultiplicity, boolean virtualTables, boolean applyChoicePredicates,
        boolean cloneColumns, boolean joinTypes, boolean multiJoins, boolean cleanNilChildren) {
        this.xtraServerMapping = xtraServerMapping;
        this.applicationSchemaUri = applicationSchemaUri;
        this.applicationSchema = new ApplicationSchema(applicationSchemaUri);
        this.flattenInheritance = flattenInheritance;
        this.fanOutInheritance = fanOutInheritance;
        this.ensureRelationNavigability = ensureRelationNavigability;
        this.fixMultiplicity = fixMultiplicity;
        this.virtualTables = virtualTables;
        this.applyChoicePredicates = applyChoicePredicates;
        this.cloneColumns = cloneColumns;
        this.joinTypes = joinTypes;
        this.multiJoins = multiJoins;
        this.cleanNilChildren = cleanNilChildren;
    }

    /**
     * Create a new transformer chain builder for the given {@link XtraServerMapping}
     *
     * @param xtraServerMapping the mapping that should be transformed
     * @return the transformer builder
     */
    public static SchemaInfo forMapping(final XtraServerMapping xtraServerMapping) {
        return new Builder(xtraServerMapping);
    }

    private XtraServerMapping transform() {
        XtraServerMapping transformedXtraServerMapping = xtraServerMapping;
        String description = xtraServerMapping.getDescription() + "\n  Transformations:\n    - applySchemaInfo (" + applicationSchemaUri.toString() + ")\n";

        transformedXtraServerMapping = new MappingTransformerSchemaInfo(transformedXtraServerMapping, applicationSchema).transform();

        if (cleanNilChildren) {
            transformedXtraServerMapping = new MappingTransformerCleanNilChildren(transformedXtraServerMapping).transform();
            description += "    - cleanNilChildren\n";
        }
        if (multiJoins) {
            transformedXtraServerMapping = new MappingTransformerMultiJoins(transformedXtraServerMapping).transform();
            transformedXtraServerMapping = new MappingTransformerSchemaInfo(transformedXtraServerMapping, applicationSchema).transform();
            description += "    - multiJoins\n";
        }
        if (cloneColumns) {
            transformedXtraServerMapping = new MappingTransformerCloneColumns(transformedXtraServerMapping).transform();
            transformedXtraServerMapping = new MappingTransformerSchemaInfo(transformedXtraServerMapping, applicationSchema).transform();
            description += "    - cloneColumns\n";
        }
        if (joinTypes) {
            transformedXtraServerMapping = new MappingTransformerJoinTypeHint(transformedXtraServerMapping, applicationSchema).transform();
            transformedXtraServerMapping = new MappingTransformerSchemaInfo(transformedXtraServerMapping, applicationSchema).transform();
            description += "    - joinTypes\n";
        }
        if (virtualTables) {
            transformedXtraServerMapping = new MappingTransformerMergeTables(transformedXtraServerMapping).transform();
            description += "    - virtualTables\n";
        }
        if (flattenInheritance) {
            transformedXtraServerMapping = new MappingTransformerFlattenInheritance(transformedXtraServerMapping).transform();
            transformedXtraServerMapping = new MappingTransformerSchemaInfo(transformedXtraServerMapping, applicationSchema).transform();
            description += "    - flattenInheritance\n";
        }
        if (fanOutInheritance) {
            transformedXtraServerMapping = new MappingTransformerFanOutInheritance(transformedXtraServerMapping, applicationSchema).transform();
            transformedXtraServerMapping = new MappingTransformerSchemaInfo(transformedXtraServerMapping, applicationSchema).transform();
            description += "    - fanOutInheritance\n";
        }
        if (fixMultiplicity) {
            transformedXtraServerMapping = new MappingTransformerMultiplicity(transformedXtraServerMapping, applicationSchema).transform();
            transformedXtraServerMapping = new MappingTransformerSchemaInfo(transformedXtraServerMapping, applicationSchema).transform();
            description += "    - fixMultiplicity\n";
        }
        if (ensureRelationNavigability) {
            transformedXtraServerMapping = new MappingTransformerRelationNavigability(transformedXtraServerMapping).transform();
            description += "    - ensureRelationNavigability\n";
        }
        if (applyChoicePredicates) {
            transformedXtraServerMapping = new MappingTransformerChoice(transformedXtraServerMapping, applicationSchema).transform();
            description += "    - applyChoicePredicates\n";
        }

        transformedXtraServerMapping = new XtraServerMappingBuilder()
                .copyOf(transformedXtraServerMapping)
                .description(description)
                .build();

        return transformedXtraServerMapping;
    }

    /**
     * Schema info transformers for {@link XtraServerMapping}s
     */
    public interface SchemaInfo {
        /**
         * Adds schema info like qualified names, super types and geometry properties to the mapping.
         * Prerequisite for all other transformers
         *
         * @param applicationSchemaUri the application schema URI
         * @return the transformer builder
         */
        Transform applySchemaInfo(URI applicationSchemaUri);
    }

    /**
     * Structural transformers for {@link XtraServerMapping}s
     */
    public interface Transform {
        /**
         * Merges mappings attached to super types down to the lowest sub types in the inheritance tree.
         * For example mappings for gml:id that are contained in the feature type mapping for gml:AbstractFeature
         * will be moved to the respective sub type mappings and the mapping for gml:AbstractFeature will be removed.
         *
         * @return the transformer builder
         */
        Transform flattenInheritance();

        /**
         * Fans out mappings attached to sub types to the corresponding super types in the inheritance tree.
         * For example mappings for gml:id that are contained in a feature type mapping will be moved to the mapping
         * for gml:AbstractFeature, which will be created if does not exist yet.
         *
         * @return the transformer builder
         */
        Transform fanOutInheritance();

        /**
         * Ensures that XtraServer will be able to navigate relations, e.g. for a GetFeature request with resolveDepth.
         * For example the transformer will check if for each reference mapping joins are provided that establish a
         * connection to all of the referenced feature types primary tables. Missing joins will then be added.
         *
         * @return the transformer builder
         */
        Transform ensureRelationNavigability();

        /**
         * This transformer fixes cases where multiplicity should be mapped without a join by using different columns
         * from the same table. These cases are automatically detected and will be enhanced with for_each_select_id
         * if necessary.
         *
         * @return the transformer builder
         */
        Transform fixMultiplicity();

        /**
         * This transformer adds support for merged tables. XtraServer supports joins only to represent multiplicity.
         * This transformer enables cases where a simple property should be mapped from a table different than the
         * feature instance table. If such cases are detected then according VirtualTables will be created.
         *
         * @return the transformer builder
         */
        Transform virtualTables();

        /**
         * This transformer adds support for choices by adding predicates to a group of mappings so that only one of
         * the mappings is ever applied.
         * This transformer is applied if multiple mappings for the same target path are detected, and either the
         * target path does not contain any multiple properties or the transformation hint CHOICE is set
         * for these mappings.
         *
         * @return the transformer builder
         */
        Transform applyChoicePredicates();

        /**
         *
         *
         * @return the transformer builder
         */
        Transform cloneColumns();

        /**
         *
         *
         * @return the transformer builder
         */
        Transform joinTypes();

        /**
         *
         *
         * @return the transformer builder
         */
        Transform multiJoins();

        /**
         *
         *
         * @return the transformer builder
         */
        Transform cleanNilChildren();

        /**
         * Executes the transformer chain
         *
         * @return the transformed {@link XtraServerMapping}
         */
        XtraServerMapping transform();
    }

    private static class Builder implements SchemaInfo, Transform {
        private final XtraServerMapping xtraServerMapping;
        private URI applicationSchemaUri;
        private boolean flattenInheritance;
        private boolean fanOutInheritance;
        private boolean ensureRelationNavigability;
        private boolean fixMultiplicity;
        private boolean virtualTables;
        private boolean applyChoicePredicates;
        private boolean cloneColumns;
        private boolean joinTypes;
        private boolean multiJoins;
        private boolean cleanNilChildren;

        Builder(final XtraServerMapping xtraServerMapping) {
            this.xtraServerMapping = xtraServerMapping;
        }

        @Override
        public Transform applySchemaInfo(final URI applicationSchemaUri) {
            this.applicationSchemaUri = applicationSchemaUri;
            return this;
        }

        @Override
        public Transform flattenInheritance() {
            this.flattenInheritance = true;
            return this;
        }

        @Override
        public Transform fanOutInheritance() {
            this.fanOutInheritance = true;
            return this;
        }

        @Override
        public Transform ensureRelationNavigability() {
            this.ensureRelationNavigability = true;
            return this;
        }

        @Override
        public Transform fixMultiplicity() {
            this.fixMultiplicity = true;
            return this;
        }

        @Override
        public Transform virtualTables() {
            this.virtualTables = true;
            return this;
        }

        @Override
        public Transform applyChoicePredicates() {
            this.applyChoicePredicates = true;
            return this;
        }

        @Override
        public Transform cloneColumns() {
            this.cloneColumns = true;
            return this;
        }

        @Override
        public Transform joinTypes() {
            this.joinTypes = true;
            return this;
        }

        @Override
        public Transform multiJoins() {
            this.multiJoins = true;
            return this;
        }

        @Override
        public Transform cleanNilChildren() {
            this.cleanNilChildren = true;
            return this;
        }

        @Override
        public XtraServerMapping transform() {
            return new XtraServerMappingTransformer(xtraServerMapping, applicationSchemaUri, flattenInheritance, fanOutInheritance, ensureRelationNavigability, fixMultiplicity, virtualTables,
                applyChoicePredicates, cloneColumns, joinTypes, multiJoins, cleanNilChildren)
                    .transform();
        }
    }
}
