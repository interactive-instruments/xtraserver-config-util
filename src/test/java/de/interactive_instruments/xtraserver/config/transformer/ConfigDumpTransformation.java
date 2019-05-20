package de.interactive_instruments.xtraserver.config.transformer;

import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.io.XtraServerMappingFile;

import java.io.*;
import java.net.URI;
import javax.xml.bind.JAXBException;

public class ConfigDumpTransformation {
    public static void main(String[] args) {
        // todo: MappingTransformer benutzbar machen

        try {

            /*final URI localApplicationSchema = new File("/home/zahnen/development/XSProjects/AAA-Suite/schema/NAS/6.0/schema/AAA-Fachschema_XtraServer.xsd").toURI();
            final String inputFile = "/home/zahnen/development/XSProjects/AAA-Suite/config/alkis/sf/includes/1/includes/XtraSrvConfig_Mapping.inc.xml";
            final String outputFile = "/home/zahnen/Downloads/alkis-mapping2.xml";*/
            // Diese Pfade als args
            if( args.length < 3) {
                return;
            }
            final URI localApplicationSchema = new File(args[0]).toURI();
            final String inputFile = args[1];
            final String outputFile = args[2]; // todo: was muss damit gemacht werden? Oder brauchen wir es hier gar nicht? Wir schreiben den output dorthin

            //XtraServerMapping xtraServerMappingImport = XtraServerMapping.createFromStream(new FileInputStream(inputFile), localApplicationSchema);
            final XtraServerMapping xtraServerMappingImport = XtraServerMappingFile.read()
                    .fromStream(new FileInputStream(inputFile));

            final XtraServerMapping xtraServerMappingNav = XtraServerMappingTransformer
                    .forMapping(xtraServerMappingImport)
                    .applySchemaInfo(localApplicationSchema)
                    .flattenInheritance()
                    .fanOutInheritance()
                    .ensureRelationNavigability()
                    .transform();

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            XtraServerMappingFile.write()
                    .mapping(xtraServerMappingNav)
                    //.createArchiveWithAdditionalFiles()
                    .toStream(outputStream);

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
            return;
        }

    }
}
