/**
 * Copyright 2020 interactive instruments GmbH
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.io;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import de.interactive_instruments.xtraserver.config.api.*;
import de.interactive_instruments.xtraserver.config.schema.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Helper methods for JAXB marshalling */
class JaxbWriter {

  private static final String MAPPING_FILE = "XtraSrvConfig_Mapping.inc.xml";
  private static final String GML_ABSTRACT_FEATURE = "gml:AbstractFeature";
  private static final QName XLINK_HREF = new QName("http://www.w3.org/1999/xlink", "@href");

  private final XtraServerMapping xtraServerMapping;
  private final ObjectFactory objectFactory;

  JaxbWriter(final XtraServerMapping xtraServerMapping) {
    this.xtraServerMapping = xtraServerMapping;
    this.objectFactory = new ObjectFactory();
  }

  void writeToStream(
      final OutputStream outputStream, final boolean createArchiveWithAdditionalFiles)
      throws IOException, JAXBException, SAXException, XMLStreamException {
    final FeatureTypesWithComment featureTypes = new FeatureTypesWithComment();
    String comment = "\n  created by xtraserver-config-util - " + new Date().toString() + "\n";
    if (xtraServerMapping.getDescription() != null) {
      comment += xtraServerMapping.getDescription();
    }
    featureTypes.setComment(comment);

    featureTypes.getFeatureTypeOrAdditionalMappings().addAll(createFeatureTypes());
    featureTypes.getFeatureTypeOrAdditionalMappings().addAll(createAdditionalMappings());

    if (createArchiveWithAdditionalFiles) {

      final ZipOutputStream zipStream = new ZipOutputStream(outputStream);

      zipStream.putNextEntry(new ZipEntry(MAPPING_FILE));
      marshal(zipStream, featureTypes);

      new AdditionalFilesWriter().generate(zipStream, xtraServerMapping);

      zipStream.close();

    } else {
      marshal(outputStream, featureTypes);
      outputStream.close();
    }
  }

  private List<FeatureType> createFeatureTypes() {
    final List<FeatureType> featureTypes =
        xtraServerMapping.getFeatureTypeMappings().stream()
            .filter(featureTypeMapping -> !featureTypeMapping.isAbstract())
            .map(createFeatureType())
            .collect(Collectors.toList());

    // add gml:AbstractFeature despite being abstract
    if (xtraServerMapping.hasFeatureType(GML_ABSTRACT_FEATURE)) {
      featureTypes.add(
          createFeatureType()
              .apply(xtraServerMapping.getFeatureTypeMapping(GML_ABSTRACT_FEATURE).get()));
    }

    return featureTypes;
  }

  private Function<FeatureTypeMapping, FeatureType> createFeatureType() {
    return featureTypeMapping -> {
      final SQLFeatureTypeImplType sqlFeatureTypeImplType =
          objectFactory.createSQLFeatureTypeImplType();

      createMappings(sqlFeatureTypeImplType, featureTypeMapping);
      createXtraServerParameters(sqlFeatureTypeImplType, featureTypeMapping);

      final FeatureType featureType = objectFactory.createFeatureType();
      featureType.setName(featureTypeMapping.getName());
      featureType.setPGISFeatureTypeImpl(sqlFeatureTypeImplType);

      return featureType;
    };
  }

  private List<AdditionalMappings> createAdditionalMappings() {
    return xtraServerMapping.getFeatureTypeMappings().stream()
        // exclude gml:AbstractFeature despite being abstract
        .filter(
            featureTypeMapping ->
                featureTypeMapping.isAbstract()
                    && !featureTypeMapping.getName().equals(GML_ABSTRACT_FEATURE))
        .map(
            additionalMapping -> {
              MappingsSequenceType mappingsSequenceType =
                  objectFactory.createMappingsSequenceType();

              createMappings(mappingsSequenceType, additionalMapping);

              AdditionalMappings additionalMappings = objectFactory.createAdditionalMappings();
              additionalMappings.setRootElementName(additionalMapping.getName());
              additionalMappings.setMappings(mappingsSequenceType);

              return additionalMappings;
            })
        .collect(Collectors.toList());
  }

  private void createMappings(
      final MappingsSequenceType mappingsSequenceType,
      final FeatureTypeMapping featureTypeMapping) {
    featureTypeMapping
        .getPrimaryTables()
        .forEach(mappingTable -> createTableMapping(mappingsSequenceType, mappingTable));
  }

  private void createTableMapping(
      final MappingsSequenceType mappingsSequenceType, final MappingTable mappingTable) {
    if (!mappingTable.isJoined() || !mappingTable.getValues().isEmpty()) {
      final TableWithComment table =
          new TableWithComment(); // objectFactory.createMappingsSequenceTypeTable();
      table.setTable_Name(mappingTable.getName());
      if (mappingTable.getPredicate() != null && !mappingTable.getPredicate().isEmpty()) {
        table.setTable_Name(mappingTable.getName() + "[" + mappingTable.getPredicate() + "]");
      }
      table.setOid_Col(mappingTable.getPrimaryKey());
      table.setTarget(mappingTable.getTargetPath());
      /*if (((MappingTableImpl) mappingTable).isReference()) {
          table.setComment(mappingTable.getTargetPath().split(":")[1]);
      } else*/
      if (mappingTable.isPrimary() && mappingTable.getDescription() != null) {
        table.setComment("# " + mappingTable.getDescription() + " #");
      } else if (!mappingTable.isPrimary()) {
        table.setComment(mappingTable.getDescription());
      }

      // TODO
      if (mappingTable.isForEachSelectId()) {
        table.setFor_Each_Select_Id(mappingTable.getSelectIds());
      }

      mappingsSequenceType.getTableOrJoinOrAssociationTarget().add(table);
    }

    mappingsSequenceType
        .getTableOrJoinOrAssociationTarget()
        .addAll(
            mappingTable.getJoinPaths().stream()
                .map(
                    mappingJoin -> {
                      JoinWithComment join = new JoinWithComment();
                      join.setAxis("parent");
                      join.setTarget(mappingTable.getTargetPath());
                      join.setJoin_Path(buildJoinPath(mappingJoin.getJoinConditions()));
                      join.setComment(mappingJoin.getDescription());
                      return join;
                    })
                .collect(Collectors.toList()));

    // mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
    mappingTable
        .getValues()
        .forEach(
            mappingValue -> {
              TableWithComment value =
                  new TableWithComment(); // objectFactory.createMappingsSequenceTypeTable();
              value.setTable_Name(mappingTable.getName());

              if (Objects.nonNull(mappingTable.getPredicate())) {
                value.setTable_Name(
                    String.format("%s[%s]", mappingTable.getName(), mappingTable.getPredicate()));
              } else if (Objects.nonNull(mappingValue.getPredicate())) {
                value.setTable_Name(
                    String.format("%s[%s]", mappingTable.getName(), mappingValue.getPredicate()));
              } else if (Objects.nonNull(mappingTable.getPredicate())
                  && Objects.nonNull(mappingValue.getPredicate())) {
                value.setTable_Name(
                    String.format("%s[(%s) AND (%s)]", mappingTable.getName(), mappingTable.getPredicate(), mappingValue.getPredicate()));
              }
              value.setTarget(mappingValue.getTargetPath());
              if (mappingValue.getValue() != null && !mappingValue.getValue().equals("")) {
                value.setValue(mappingValue.getValue());
              }
              value.setValue_Type(buildValueType(mappingValue));
              if (mappingValue.isNil()) {
                value.setMapping_Mode("nil");
              }
              if (mappingValue.isClassification() || mappingValue.isNil()) {
                value.setDb_Codes(
                    Joiner.on(' ').join(((MappingValueClassification) mappingValue).getKeys()));
                value.setSchema_Codes(
                    Joiner.on(' ').join(((MappingValueClassification) mappingValue).getValues()));
              }

              // TODO
              if (!Objects.isNull(mappingValue.getSelectId())) {
                value.setSelect_Id(mappingValue.getSelectId().toString());
                if (!mappingValue.isReference()
                    && mappingValue
                        .getQualifiedTargetPath()
                        .get(mappingValue.getQualifiedTargetPath().size() - 1)
                        .getLocalPart()
                        .startsWith("@")) {
                  value.setSignificant_For_Emptiness(false);
                }
              }
              if (!mappingValue.getSignificantForEmptiness()) {
                value.setSignificant_For_Emptiness(false);
              }

              if (!mappingValue.isReference()
                  && mappingValue
                      .getQualifiedTargetPath()
                      .get(mappingValue.getQualifiedTargetPath().size() - 1)
                      .equals(XLINK_HREF)) {
                value.setIs_Reference(false);
              }

              value.setComment(mappingValue.getDescription());
              // return value;
              mappingsSequenceType.getTableOrJoinOrAssociationTarget().add(value);

              if (mappingValue.isReference()) {
                final MappingsSequenceType.AssociationTarget associationTarget =
                    objectFactory.createMappingsSequenceTypeAssociationTarget();
                associationTarget.setObject_Ref(
                    ((MappingValueReference) mappingValue).getReferencedFeatureType());
                associationTarget.setTarget(
                    ((MappingValueReference) mappingValue).getReferencedTarget());
                mappingsSequenceType.getTableOrJoinOrAssociationTarget().add(associationTarget);
              }
            }); // .collect(Collectors.toList())
    // );

    /*mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
            mappingTable.getValues().stream()
                    .filter(MappingValue::isReference)
                    .map(mappingValue -> {
                        MappingsSequenceType.AssociationTarget associationTarget = objectFactory.createMappingsSequenceTypeAssociationTarget();
                        associationTarget.setObject_Ref(((MappingValueReference) mappingValue).getReferencedFeatureType());
                        associationTarget.setTarget(((MappingValueReference) mappingValue).getReferencedTarget());
                        return associationTarget;
                    }).collect(Collectors.toList())
    );*/

    mappingTable.getJoiningTables().stream()
        .filter(joiningTable -> !joiningTable.isMerged())
        .forEach(joiningTable -> createTableMapping(mappingsSequenceType, joiningTable));
  }

  private String buildValueType(final MappingValue mappingValue) {
    if (mappingValue.isExpression()) {
      return "expression";
    } else if (mappingValue.isConstant()) {
      return "constant";
    } else if (mappingValue.isReference()) {
      // TODO: might be expression or column
      return "expression";
    }

    return null;
  }

  private String buildJoinPath(final List<MappingJoin.Condition> conditions) {
    final StringBuilder stringBuilder = new StringBuilder();

    for (final MappingJoin.Condition condition : conditions) {
      if (stringBuilder.length() == 0) {
        stringBuilder.insert(0, condition.getSourceTable());
      }
      stringBuilder.insert(0, ")::");
      stringBuilder.insert(0, condition.getSourceField());
      stringBuilder.insert(0, ":");
      stringBuilder.insert(0, condition.getTargetField());
      stringBuilder.insert(0, "/ref(");
      stringBuilder.insert(0, condition.getTargetTable());
    }

    return stringBuilder.toString();
  }

  private void createXtraServerParameters(
      final SQLFeatureTypeImplType sqlFeatureTypeImplType,
      final FeatureTypeMapping featureTypeMapping) {
    sqlFeatureTypeImplType.setLogging("false");
    sqlFeatureTypeImplType.setUseTempTable(false);
    if (!featureTypeMapping.getName().endsWith(":AbstractFeature")) {
      sqlFeatureTypeImplType.setTempTableName(
          "_xsv_tmp_" + Joiner.on('_').join(featureTypeMapping.getPrimaryTableNames()));
    }
  }

  private void marshal(final OutputStream outputStream, final FeatureTypes featureTypes)
      throws JAXBException, SAXException, XMLStreamException {
    final SchemaFactory schemaFactory =
        SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    final Schema schema =
        schemaFactory.newSchema(Resources.getResource(JaxbReader.class, JaxbReader.MAPPING_SCHEMA));
    final JAXBContext jaxbContext =
        JAXBContext.newInstance(FeatureTypes.class.getPackage().getName());

    final XMLOutputFactory xof = XMLOutputFactory.newFactory();
    final XMLStreamWriter xsw =
        new IndentingUTF8XMLStreamWriter(xof.createXMLStreamWriter(outputStream, "UTF-8"));

    final Marshaller marshaller = jaxbContext.createMarshaller();
    marshaller.setSchema(schema);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.setListener(new JaxbCommentsWriter(xsw));
    marshaller.marshal(featureTypes, xsw);
    xsw.close();
  }
}
