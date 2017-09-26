package de.interactive_instruments.xtraserver.config.util;

import java.io.*;
import java.text.DecimalFormat;
import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.*;
import org.xml.sax.SAXException;

/** Hilfsmethoden fuer JAXB-Marshalling und -Unmarshalling */
public class MappingParser
{
   static final DecimalFormat DF_2 = new DecimalFormat( "#,##0.00" );

   public static <T> T unmarshal( String xsdSchema, String xmlDatei, Class<T> clss )
   throws JAXBException, SAXException
   {
      // Schema und JAXBContext sind multithreadingsicher ("thread safe"):
      SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
      Schema        schema        = ( xsdSchema == null || xsdSchema.trim().length() == 0 )
                                    ? null : schemaFactory.newSchema( new File( xsdSchema ) );
      JAXBContext   jaxbContext   = JAXBContext.newInstance( clss.getPackage().getName() );
      return unmarshal( jaxbContext, schema, xmlDatei, clss );
   }

   public static <T> T unmarshal( JAXBContext jaxbContext, Schema schema, String xmlDatei, Class<T> clss )
   throws JAXBException
   {
      // Unmarshaller ist nicht multithreadingsicher:
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      unmarshaller.setSchema( schema );
      return clss.cast( unmarshaller.unmarshal( new File( xmlDatei ) ) );
   }

   public static <T> T unmarshal( String xsdSchema, InputStream inputStream, Class<T> clss )
           throws JAXBException, SAXException, IOException {
      // Schema und JAXBContext sind multithreadingsicher ("thread safe"):
      SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
      Schema        schema        = ( xsdSchema == null || xsdSchema.trim().length() == 0 )
              ? null : schemaFactory.newSchema( new File( xsdSchema ) );
      JAXBContext   jaxbContext   = JAXBContext.newInstance( clss.getPackage().getName() );
      return unmarshal( jaxbContext, schema, inputStream, clss );
   }

   public static <T> T unmarshal(JAXBContext jaxbContext, Schema schema, InputStream inputStream, Class<T> clss )
           throws JAXBException, IOException {

      PipedInputStream in = new PipedInputStream();
      PipedOutputStream out2 = new PipedOutputStream(in);
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      SubstitutionProcessor substitutionProcessor = new SubstitutionProcessor();
      substitutionProcessor.addParameter("xpathAliasPattern.AX_Flurstueck.15", "foo");
      substitutionProcessor.addParameter("xpathAliasReplacement.AX_Flurstueck.15", "bar");
      new Thread(
              new Runnable(){
                 public void run(){
                    try {
                       substitutionProcessor.process(new InputStreamReader(inputStream), new OutputStreamWriter(out2));
                    } catch (IOException e) {
                       e.printStackTrace();
                    }
                 }
              }
      ).start();

      /*System.out.println("BLA");
         System.out.println(out.toString());


      new Thread(
            new Runnable(){
             public void run(){
              try {
                 out2.write(out.toByteArray());
            } catch (IOException e) {
             e.printStackTrace();
        }
      }
      }
      ).start();
*/

      // Unmarshaller ist nicht multithreadingsicher:
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      unmarshaller.setSchema( schema );
      return clss.cast( unmarshaller.unmarshal( in ) );
   }

   public static void marshal( String xsdSchema, String xmlDatei, Object jaxbElement )
   throws JAXBException, SAXException
   {
      SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
      Schema        schema        = ( xsdSchema == null || xsdSchema.trim().length() == 0 )
                                    ? null : schemaFactory.newSchema( new File( xsdSchema ) );
      JAXBContext   jaxbContext   = JAXBContext.newInstance( jaxbElement.getClass().getPackage().getName() );
      marshal( jaxbContext, schema, xmlDatei, jaxbElement );
   }

   public static void marshal( JAXBContext jaxbContext, Schema schema, String xmlDatei, Object jaxbElement )
   throws JAXBException
   {
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setSchema( schema );
      marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
      marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      marshaller.marshal( jaxbElement, new File( xmlDatei ) );
   }

   /** Die main()-Methode ist nur fuer Testzwecke */
   public static void main( String[] args ) throws JAXBException, SAXException, ClassNotFoundException
   {
      if( args.length != 3 ) {
         System.out.println( "\nBitte XSD-Schema, XML-Dokument und Zielklasse angeben." );
         return;
      }
      System.out.println( "\nSchema: " + args[0] + ", XML-Dokument: " + args[1] + ", Zielklasse: " + args[2] + "\n" );

      // Unmarshalling-Test:
      long startSpeicherverbrauch = ermittleSpeicherverbrauch();
      long startZeit = System.nanoTime();
      Object obj = unmarshal( args[0], args[1], Class.forName( args[2] ) );
      String dauer = ermittleDauer( startZeit );
      String speicherverbrauch = formatiereSpeichergroesse( ermittleSpeicherverbrauch() - startSpeicherverbrauch );
      System.out.println( "Parsingspeicherverbrauch = " + speicherverbrauch + ", Parsingdauer = " + dauer );
      System.out.println( obj.getClass() );
      // Die folgende Ausgabe macht nur Sinn, wenn es eine sinnvolle toString()-Methode gibt:
      System.out.println( obj );

      // Marshalling-Test:
      startZeit = System.nanoTime();
      marshal( args[0], args[1] + "-output.xml", obj );
      dauer = ermittleDauer( startZeit );
      System.out.println( "\n'" + args[1] + "-output.xml' erzeugt in " + dauer + "." );
   }

   static String ermittleDauer( long startZeitNanoSek )
   {
      long dauerMs = (System.nanoTime() - startZeitNanoSek) / 1000 / 1000;
      if( dauerMs < 1000 ) return "" + dauerMs + " ms";
      return DF_2.format( dauerMs / 1000. ) + " s";
   }

   static long ermittleSpeicherverbrauch()
   {
      System.gc();
      System.gc();
      return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
   }

   static String formatiereSpeichergroesse( long bytes )
   {
      if( bytes < 0 ) return "0 Byte";
      if( bytes < 1024 ) return "" + bytes + " Byte";
      double b = bytes / 1024.;
      if( b < 1024. ) return DF_2.format( b ) + " KByte";
      return DF_2.format( b / 1024. ) + " MByte";
   }
}
