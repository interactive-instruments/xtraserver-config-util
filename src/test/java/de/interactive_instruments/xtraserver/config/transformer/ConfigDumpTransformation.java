package de.interactive_instruments.xtraserver.config.transformer;

import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.io.XtraServerMappingFile;
import de.interactive_instruments.xtraserver.config.schema.Configuration;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import javax.xml.bind.JAXBException;

public class ConfigDumpTransformation {

    @Test
    public void forMapping() {

        try {

            /*final URI localApplicationSchema = new File("/home/zahnen/development/XSProjects/AAA-Suite/schema/NAS/6.0/schema/AAA-Fachschema_XtraServer.xsd").toURI();
            final String inputFile = "/home/zahnen/development/XSProjects/AAA-Suite/config/alkis/sf/includes/1/includes/XtraSrvConfig_Mapping.inc.xml";
            final String outputFile = "/home/zahnen/Downloads/alkis-mapping2.xml";*/
            final URI localApplicationSchema = new File("C:/Users/finis/dev/XtraServerProjects/AAA-Suite/schema/NAS/6.0/schema/AAA-Fachschema_XtraServer.xsd").toURI();
            final String inputFile = "C:/Users/finis/dev/XtraServerProjects/AAA-Suite/config/alkis/sf/XtraSrvConfig-dump.xml";
            final String outputFile = "C:/Users/finis/dev/Material/XtraServer Entwicklung/Analyse/xs-nextgen/output/xmlalkis-config.xml";



            //XtraServerMapping xtraServerMappingImport = XtraServerMapping.createFromStream(new FileInputStream(inputFile), localApplicationSchema);
            final Configuration configurationImport = XtraServerMappingFile.read()
                    .fromStream(new FileInputStream(inputFile));

            String nativeSRS = configurationImport.getDatabase().getpGISDataImpl().getGeometryExtent().getNativeSRS();
            String xmin = configurationImport.getDatabase().getpGISDataImpl().getGeometryExtent().getBoundingBox().getXmin();
            String ymin = configurationImport.getDatabase().getpGISDataImpl().getGeometryExtent().getBoundingBox().getYmin();
            String xmax = configurationImport.getDatabase().getpGISDataImpl().getGeometryExtent().getBoundingBox().getXmax();
            String ymax = configurationImport.getDatabase().getpGISDataImpl().getGeometryExtent().getBoundingBox().getYmax();

            /*
            final XtraServerMapping xtraServerMappingNav = XtraServerMappingTransformer
                    .forMapping(xtraServerMappingImport)
                    .applySchemaInfo(localApplicationSchema)
                    .flattenInheritance()
                    .fanOutInheritance()
                    .ensureRelationNavigability()
                    .transform();
            */

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String description = nativeSRS + " " +
                    xmin + ", " +
                    ymin + ", " +
                    xmax + ", " +
                    ymax;
            outputStream.write(description.getBytes());


            /*
            XtraServerMappingFile.write()
                    .mapping(xtraServerMappingNav)
                    //.createArchiveWithAdditionalFiles()
                    .toStream(outputStream);
            */

            //System.out.println(outputStream.toString());
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(outputStream.toString()
                    .getBytes());
            fos.flush();
            fos.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        // todo: nötigenfalls weiter differenzieren?
        catch( Exception e) {
            String msg = e.getMessage();
            return;
        }

    }
}
