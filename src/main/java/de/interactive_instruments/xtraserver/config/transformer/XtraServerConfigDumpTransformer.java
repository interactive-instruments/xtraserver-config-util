package de.interactive_instruments.xtraserver.config.transformer;

//import de.interactive_instruments.xtraserver.config.api.XtraServerConfigDump;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;

import java.net.URI;

public class XtraServerConfigDumpTransformer {

    // todo: XtraServerMapping -> XtraServerConfigDump
    private final XtraServerMapping xtraServerConfigDump;
    private final ApplicationSchema applicationSchema;
    private final URI applicationSchemaUri;

    private XtraServerConfigDumpTransformer(final XtraServerMapping xtraServerConfigDump, final URI applicationSchemaUri) {
        this.xtraServerConfigDump = xtraServerConfigDump;
        this.applicationSchemaUri = applicationSchemaUri;
        this.applicationSchema = new ApplicationSchema(applicationSchemaUri);
    }

    /**
     * Create a new transformer chain builder for the given {@link XtraServerMapping}
     *
     * @param xtraServerConfigDump the config dump that should be transformed
     * @return the transformer builder
     */
    public static SchemaInfo forMapping(final XtraServerMapping xtraServerConfigDump) {
        return new Builder(xtraServerConfigDump);
    }

    private XtraServerMapping transform() {
        XtraServerMapping transformedXtraServerConfigDump = xtraServerConfigDump;
        String description = xtraServerConfigDump.getDescription() + "\n  Transformations:\n    - applySchemaInfo (" + applicationSchemaUri.toString() + ")\n";

        transformedXtraServerConfigDump = new MappingTransformerSchemaInfo(transformedXtraServerConfigDump, applicationSchema).transform();
        // todo: da brauchen wir eine eigene Klasse, die unsere Transformationen macht
        // todo: Aber wie sieht das mit der Superklasse/den Interfaces aus? MappingTransformer ist vom Namen her schon schlecht, da sollten wir eine eigene Klasse haben

        
        /*if (virtualTables) {
            transformedXtraServerMapping = new MappingTransformerMergeTables(transformedXtraServerMapping).transform();
            description += "    - virtualTables\n";
        }
        */

        transformedXtraServerConfigDump = new XtraServerMappingBuilder()
                .copyOf(transformedXtraServerConfigDump)
                .description(description)
                .build();

        return transformedXtraServerConfigDump;

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
         * Executes the transformer chain
         *
         * @return the transformed {@link XtraServerMapping}
         */
        XtraServerMapping transform();
    }

    private static class Builder implements SchemaInfo, Transform {
        private final XtraServerMapping xtraServerConfigDump;
        private URI applicationSchemaUri;

        Builder(final XtraServerMapping xtraServerConfigDump) {
            this.xtraServerConfigDump = xtraServerConfigDump;
        }

        @Override
        public Transform applySchemaInfo(final URI applicationSchemaUri) {
            this.applicationSchemaUri = applicationSchemaUri;
            return this;
        }
        @Override
        public XtraServerMapping transform() {
            return new XtraServerConfigDumpTransformer(xtraServerConfigDump, applicationSchemaUri)
                    .transform();
        }
    }

}