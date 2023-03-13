
package de.interactive_instruments.xtraserver.config.schema;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


/**
 * &lt;p&gt;Java class for MappingsSequenceType complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="MappingsSequenceType"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;choice maxOccurs="unbounded" minOccurs="0"&amp;gt;
 *           &amp;lt;element name="Table"&amp;gt;
 *             &amp;lt;complexType&amp;gt;
 *               &amp;lt;complexContent&amp;gt;
 *                 &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *                   &amp;lt;attribute name="apply_mapping_to_path" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="applyMappingToPath" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="assign" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="assign1" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="db_codes" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="derivation_pattern" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="disambiguate" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="filter_mapping" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&amp;gt;
 *                   &amp;lt;attribute name="for_each_select_id" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="ft_col" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="generator" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="is_reference" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="isMappedGeometry" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="isReference" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="map_targetpath" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="mapped_geometry" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="mapping_mode" default="value"&amp;gt;
 *                     &amp;lt;simpleType&amp;gt;
 *                       &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *                         &amp;lt;enumeration value="value"/&amp;gt;
 *                         &amp;lt;enumeration value="nil"/&amp;gt;
 *                         &amp;lt;enumeration value="nil_attr"/&amp;gt;
 *                         &amp;lt;enumeration value="nilAttr"/&amp;gt;
 *                       &amp;lt;/restriction&amp;gt;
 *                     &amp;lt;/simpleType&amp;gt;
 *                   &amp;lt;/attribute&amp;gt;
 *                   &amp;lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="nil_reason" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="nil_value" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="no_output" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="noOutput" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="oid_col" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="schema_codes" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="select_id" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="significant_for_emptiness" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="significantForEmptiness" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="srid" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="srs" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="suppress_xml_entities_encoding" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="suppressXMLEntitiesEncoding" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *                   &amp;lt;attribute name="table_name" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&amp;gt;
 *                   &amp;lt;attribute name="use_geotypes" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="useGeotypes" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="value_type" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="valueType" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                 &amp;lt;/restriction&amp;gt;
 *               &amp;lt;/complexContent&amp;gt;
 *             &amp;lt;/complexType&amp;gt;
 *           &amp;lt;/element&amp;gt;
 *           &amp;lt;element name="Join"&amp;gt;
 *             &amp;lt;complexType&amp;gt;
 *               &amp;lt;simpleContent&amp;gt;
 *                 &amp;lt;extension base="&amp;lt;http://www.w3.org/2001/XMLSchema&amp;gt;string"&amp;gt;
 *                   &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&amp;gt;
 *                   &amp;lt;attribute name="filter_mapping" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&amp;gt;
 *                   &amp;lt;attribute name="join_path" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="axis" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="idref" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="disambiguate" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                 &amp;lt;/extension&amp;gt;
 *               &amp;lt;/simpleContent&amp;gt;
 *             &amp;lt;/complexType&amp;gt;
 *           &amp;lt;/element&amp;gt;
 *           &amp;lt;element name="AssociationTarget"&amp;gt;
 *             &amp;lt;complexType&amp;gt;
 *               &amp;lt;simpleContent&amp;gt;
 *                 &amp;lt;extension base="&amp;lt;http://www.w3.org/2001/XMLSchema&amp;gt;string"&amp;gt;
 *                   &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&amp;gt;
 *                   &amp;lt;attribute name="object_ref" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                 &amp;lt;/extension&amp;gt;
 *               &amp;lt;/simpleContent&amp;gt;
 *             &amp;lt;/complexType&amp;gt;
 *           &amp;lt;/element&amp;gt;
 *           &amp;lt;element name="Content"&amp;gt;
 *             &amp;lt;complexType&amp;gt;
 *               &amp;lt;simpleContent&amp;gt;
 *                 &amp;lt;extension base="&amp;lt;http://www.w3.org/2001/XMLSchema&amp;gt;string"&amp;gt;
 *                   &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&amp;gt;
 *                   &amp;lt;attribute name="representation" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="implementation" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="mode" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                 &amp;lt;/extension&amp;gt;
 *               &amp;lt;/simpleContent&amp;gt;
 *             &amp;lt;/complexType&amp;gt;
 *           &amp;lt;/element&amp;gt;
 *           &amp;lt;element name="Substitution"&amp;gt;
 *             &amp;lt;complexType&amp;gt;
 *               &amp;lt;complexContent&amp;gt;
 *                 &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *                   &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                   &amp;lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&amp;gt;
 *                   &amp;lt;attribute name="implementation" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                 &amp;lt;/restriction&amp;gt;
 *               &amp;lt;/complexContent&amp;gt;
 *             &amp;lt;/complexType&amp;gt;
 *           &amp;lt;/element&amp;gt;
 *         &amp;lt;/choice&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MappingsSequenceType", propOrder = {
    "tableOrJoinOrAssociationTarget"
})
@XmlSeeAlso({
    SQLFeatureTypeImplType.class
})
public class MappingsSequenceType {

    @XmlElements({
        @XmlElement(name = "Table", type = MappingsSequenceType.Table.class),
        @XmlElement(name = "Join", type = MappingsSequenceType.Join.class),
        @XmlElement(name = "AssociationTarget", type = MappingsSequenceType.AssociationTarget.class),
        @XmlElement(name = "Content", type = MappingsSequenceType.Content.class),
        @XmlElement(name = "Substitution", type = MappingsSequenceType.Substitution.class)
    })
    protected List<Object> tableOrJoinOrAssociationTarget;

    /**
     * Gets the value of the tableOrJoinOrAssociationTarget property.
     * 
     * &lt;p&gt;
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a &lt;CODE&gt;set&lt;/CODE&gt; method for the tableOrJoinOrAssociationTarget property.
     * 
     * &lt;p&gt;
     * For example, to add a new item, do as follows:
     * &lt;pre&gt;
     *    getTableOrJoinOrAssociationTarget().add(newItem);
     * &lt;/pre&gt;
     * 
     * 
     * &lt;p&gt;
     * Objects of the following type(s) are allowed in the list
     * {@link MappingsSequenceType.Table }
     * {@link MappingsSequenceType.Join }
     * {@link MappingsSequenceType.AssociationTarget }
     * {@link MappingsSequenceType.Content }
     * {@link MappingsSequenceType.Substitution }
     * 
     * 
     */
    public List<Object> getTableOrJoinOrAssociationTarget() {
        if (tableOrJoinOrAssociationTarget == null) {
            tableOrJoinOrAssociationTarget = new ArrayList<Object>();
        }
        return this.tableOrJoinOrAssociationTarget;
    }


    /**
     * &lt;p&gt;Java class for anonymous complex type.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
     * 
     * &lt;pre&gt;
     * &amp;lt;complexType&amp;gt;
     *   &amp;lt;simpleContent&amp;gt;
     *     &amp;lt;extension base="&amp;lt;http://www.w3.org/2001/XMLSchema&amp;gt;string"&amp;gt;
     *       &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&amp;gt;
     *       &amp;lt;attribute name="object_ref" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *     &amp;lt;/extension&amp;gt;
     *   &amp;lt;/simpleContent&amp;gt;
     * &amp;lt;/complexType&amp;gt;
     * &lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class AssociationTarget {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "gmlVersion")
        protected String gmlVersion;
        @XmlAttribute(name = "target")
        protected String target;
        @XmlAttribute(name = "object_ref")
        protected String object_Ref;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the gmlVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGmlVersion() {
            return gmlVersion;
        }

        /**
         * Sets the value of the gmlVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGmlVersion(String value) {
            this.gmlVersion = value;
        }

        /**
         * Gets the value of the target property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTarget() {
            if (target == null) {
                return "";
            } else {
                return target;
            }
        }

        /**
         * Sets the value of the target property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * Gets the value of the object_Ref property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getObject_Ref() {
            return object_Ref;
        }

        /**
         * Sets the value of the object_Ref property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setObject_Ref(String value) {
            this.object_Ref = value;
        }

    }


    /**
     * &lt;p&gt;Java class for anonymous complex type.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
     * 
     * &lt;pre&gt;
     * &amp;lt;complexType&amp;gt;
     *   &amp;lt;simpleContent&amp;gt;
     *     &amp;lt;extension base="&amp;lt;http://www.w3.org/2001/XMLSchema&amp;gt;string"&amp;gt;
     *       &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&amp;gt;
     *       &amp;lt;attribute name="representation" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="implementation" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="mode" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *     &amp;lt;/extension&amp;gt;
     *   &amp;lt;/simpleContent&amp;gt;
     * &amp;lt;/complexType&amp;gt;
     * &lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Content {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "gmlVersion")
        protected String gmlVersion;
        @XmlAttribute(name = "target")
        protected String target;
        @XmlAttribute(name = "representation")
        protected String representation;
        @XmlAttribute(name = "implementation")
        protected String implementation;
        @XmlAttribute(name = "mode")
        protected String mode;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the gmlVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGmlVersion() {
            return gmlVersion;
        }

        /**
         * Sets the value of the gmlVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGmlVersion(String value) {
            this.gmlVersion = value;
        }

        /**
         * Gets the value of the target property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTarget() {
            if (target == null) {
                return "";
            } else {
                return target;
            }
        }

        /**
         * Sets the value of the target property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * Gets the value of the representation property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRepresentation() {
            return representation;
        }

        /**
         * Sets the value of the representation property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRepresentation(String value) {
            this.representation = value;
        }

        /**
         * Gets the value of the implementation property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImplementation() {
            return implementation;
        }

        /**
         * Sets the value of the implementation property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImplementation(String value) {
            this.implementation = value;
        }

        /**
         * Gets the value of the mode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMode() {
            return mode;
        }

        /**
         * Sets the value of the mode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMode(String value) {
            this.mode = value;
        }

    }


    /**
     * &lt;p&gt;Java class for anonymous complex type.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
     * 
     * &lt;pre&gt;
     * &amp;lt;complexType&amp;gt;
     *   &amp;lt;simpleContent&amp;gt;
     *     &amp;lt;extension base="&amp;lt;http://www.w3.org/2001/XMLSchema&amp;gt;string"&amp;gt;
     *       &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&amp;gt;
     *       &amp;lt;attribute name="filter_mapping" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&amp;gt;
     *       &amp;lt;attribute name="join_path" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="axis" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="idref" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="disambiguate" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *     &amp;lt;/extension&amp;gt;
     *   &amp;lt;/simpleContent&amp;gt;
     * &amp;lt;/complexType&amp;gt;
     * &lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Join {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "gmlVersion")
        protected String gmlVersion;
        @XmlAttribute(name = "target")
        protected String target;
        @XmlAttribute(name = "filter_mapping")
        protected Boolean filter_Mapping;
        @XmlAttribute(name = "join_path")
        protected String join_Path;
        @XmlAttribute(name = "axis")
        protected String axis;
        @XmlAttribute(name = "idref")
        protected String idref;
        @XmlAttribute(name = "match")
        protected String match;
        @XmlAttribute(name = "disambiguate")
        protected String disambiguate;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the gmlVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGmlVersion() {
            return gmlVersion;
        }

        /**
         * Sets the value of the gmlVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGmlVersion(String value) {
            this.gmlVersion = value;
        }

        /**
         * Gets the value of the target property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTarget() {
            if (target == null) {
                return "";
            } else {
                return target;
            }
        }

        /**
         * Sets the value of the target property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * Gets the value of the filter_Mapping property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isFilter_Mapping() {
            if (filter_Mapping == null) {
                return false;
            } else {
                return filter_Mapping;
            }
        }

        /**
         * Sets the value of the filter_Mapping property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setFilter_Mapping(Boolean value) {
            this.filter_Mapping = value;
        }

        /**
         * Gets the value of the join_Path property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getJoin_Path() {
            return join_Path;
        }

        /**
         * Sets the value of the join_Path property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setJoin_Path(String value) {
            this.join_Path = value;
        }

        /**
         * Gets the value of the axis property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAxis() {
            return axis;
        }

        /**
         * Sets the value of the axis property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAxis(String value) {
            this.axis = value;
        }

        /**
         * Gets the value of the idref property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdref() {
            return idref;
        }

        /**
         * Sets the value of the idref property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdref(String value) {
            this.idref = value;
        }

        /**
         * Gets the value of the match property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMatch() {
            return match;
        }

        /**
         * Sets the value of the match property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMatch(String value) {
            this.match = value;
        }

        /**
         * Gets the value of the disambiguate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDisambiguate() {
            return disambiguate;
        }

        /**
         * Sets the value of the disambiguate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDisambiguate(String value) {
            this.disambiguate = value;
        }

    }


    /**
     * &lt;p&gt;Java class for anonymous complex type.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
     * 
     * &lt;pre&gt;
     * &amp;lt;complexType&amp;gt;
     *   &amp;lt;complexContent&amp;gt;
     *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
     *       &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&amp;gt;
     *       &amp;lt;attribute name="implementation" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *     &amp;lt;/restriction&amp;gt;
     *   &amp;lt;/complexContent&amp;gt;
     * &amp;lt;/complexType&amp;gt;
     * &lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Substitution {

        @XmlAttribute(name = "gmlVersion")
        protected String gmlVersion;
        @XmlAttribute(name = "target")
        protected String target;
        @XmlAttribute(name = "implementation")
        protected String implementation;

        /**
         * Gets the value of the gmlVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGmlVersion() {
            return gmlVersion;
        }

        /**
         * Sets the value of the gmlVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGmlVersion(String value) {
            this.gmlVersion = value;
        }

        /**
         * Gets the value of the target property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTarget() {
            if (target == null) {
                return "";
            } else {
                return target;
            }
        }

        /**
         * Sets the value of the target property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * Gets the value of the implementation property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImplementation() {
            return implementation;
        }

        /**
         * Sets the value of the implementation property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImplementation(String value) {
            this.implementation = value;
        }

    }


    /**
     * &lt;p&gt;Java class for anonymous complex type.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
     * 
     * &lt;pre&gt;
     * &amp;lt;complexType&amp;gt;
     *   &amp;lt;complexContent&amp;gt;
     *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
     *       &amp;lt;attribute name="apply_mapping_to_path" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="applyMappingToPath" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="assign" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="assign1" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="db_codes" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="derivation_pattern" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="disambiguate" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="filter_mapping" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&amp;gt;
     *       &amp;lt;attribute name="for_each_select_id" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="ft_col" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="generator" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="is_reference" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="isMappedGeometry" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="isReference" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="map_targetpath" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="mapped_geometry" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="mapping_mode" default="value"&amp;gt;
     *         &amp;lt;simpleType&amp;gt;
     *           &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
     *             &amp;lt;enumeration value="value"/&amp;gt;
     *             &amp;lt;enumeration value="nil"/&amp;gt;
     *             &amp;lt;enumeration value="nil_attr"/&amp;gt;
     *             &amp;lt;enumeration value="nilAttr"/&amp;gt;
     *           &amp;lt;/restriction&amp;gt;
     *         &amp;lt;/simpleType&amp;gt;
     *       &amp;lt;/attribute&amp;gt;
     *       &amp;lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="nil_reason" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="nil_value" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="no_output" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="noOutput" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="oid_col" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="schema_codes" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="select_id" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="significant_for_emptiness" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="significantForEmptiness" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="srid" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="srs" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="suppress_xml_entities_encoding" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="suppressXMLEntitiesEncoding" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
     *       &amp;lt;attribute name="table_name" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&amp;gt;
     *       &amp;lt;attribute name="use_geotypes" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="useGeotypes" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="value_type" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *       &amp;lt;attribute name="valueType" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *     &amp;lt;/restriction&amp;gt;
     *   &amp;lt;/complexContent&amp;gt;
     * &amp;lt;/complexType&amp;gt;
     * &lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Table {

        @XmlAttribute(name = "apply_mapping_to_path")
        protected Boolean apply_Mapping_To_Path;
        @XmlAttribute(name = "applyMappingToPath")
        protected Boolean applyMappingToPath;
        @XmlAttribute(name = "assign")
        protected String assign;
        @XmlAttribute(name = "assign1")
        protected String assign1;
        @XmlAttribute(name = "db_codes")
        protected String db_Codes;
        @XmlAttribute(name = "derivation_pattern")
        protected String derivation_Pattern;
        @XmlAttribute(name = "disambiguate")
        protected String disambiguate;
        @XmlAttribute(name = "filter_mapping")
        protected Boolean filter_Mapping;
        @XmlAttribute(name = "for_each_select_id")
        protected String for_Each_Select_Id;
        @XmlAttribute(name = "ft_col")
        protected String ft_Col;
        @XmlAttribute(name = "generator")
        protected String generator;
        @XmlAttribute(name = "gmlVersion")
        protected String gmlVersion;
        @XmlAttribute(name = "id")
        protected String id;
        @XmlAttribute(name = "is_reference")
        protected Boolean is_Reference;
        @XmlAttribute(name = "isMappedGeometry")
        protected Boolean isMappedGeometry;
        @XmlAttribute(name = "isReference")
        protected Boolean isReference;
        @XmlAttribute(name = "map_targetpath")
        protected Boolean map_Targetpath;
        @XmlAttribute(name = "mapped_geometry")
        protected Boolean mapped_Geometry;
        @XmlAttribute(name = "mapping_mode")
        protected String mapping_Mode;
        @XmlAttribute(name = "match")
        protected String match;
        @XmlAttribute(name = "nil_reason")
        protected String nil_Reason;
        @XmlAttribute(name = "nil_value")
        protected String nil_Value;
        @XmlAttribute(name = "no_output")
        protected Boolean no_Output;
        @XmlAttribute(name = "noOutput")
        protected Boolean noOutput;
        @XmlAttribute(name = "oid_col")
        protected String oid_Col;
        @XmlAttribute(name = "schema_codes")
        protected String schema_Codes;
        @XmlAttribute(name = "select_id")
        protected String select_Id;
        @XmlAttribute(name = "significant_for_emptiness")
        protected Boolean significant_For_Emptiness;
        @XmlAttribute(name = "significantForEmptiness")
        protected Boolean significantForEmptiness;
        @XmlAttribute(name = "srid")
        protected String srid;
        @XmlAttribute(name = "srs")
        protected String srs;
        @XmlAttribute(name = "suppress_xml_entities_encoding")
        protected Boolean suppress_Xml_Entities_Encoding;
        @XmlAttribute(name = "suppressXMLEntitiesEncoding")
        protected Boolean suppressXMLEntitiesEncoding;
        @XmlAttribute(name = "table_name")
        protected String table_Name;
        @XmlAttribute(name = "target")
        protected String target;
        @XmlAttribute(name = "use_geotypes")
        protected String use_Geotypes;
        @XmlAttribute(name = "useGeotypes")
        protected String useGeotypes;
        @XmlAttribute(name = "value")
        protected String value;
        @XmlAttribute(name = "value_type")
        protected String value_Type;
        @XmlAttribute(name = "valueType")
        protected String valueType;

        /**
         * Gets the value of the apply_Mapping_To_Path property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isApply_Mapping_To_Path() {
            return apply_Mapping_To_Path;
        }

        /**
         * Sets the value of the apply_Mapping_To_Path property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setApply_Mapping_To_Path(Boolean value) {
            this.apply_Mapping_To_Path = value;
        }

        /**
         * Gets the value of the applyMappingToPath property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isApplyMappingToPath() {
            return applyMappingToPath;
        }

        /**
         * Sets the value of the applyMappingToPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setApplyMappingToPath(Boolean value) {
            this.applyMappingToPath = value;
        }

        /**
         * Gets the value of the assign property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAssign() {
            return assign;
        }

        /**
         * Sets the value of the assign property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAssign(String value) {
            this.assign = value;
        }

        /**
         * Gets the value of the assign1 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAssign1() {
            return assign1;
        }

        /**
         * Sets the value of the assign1 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAssign1(String value) {
            this.assign1 = value;
        }

        /**
         * Gets the value of the db_Codes property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDb_Codes() {
            return db_Codes;
        }

        /**
         * Sets the value of the db_Codes property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDb_Codes(String value) {
            this.db_Codes = value;
        }

        /**
         * Gets the value of the derivation_Pattern property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDerivation_Pattern() {
            return derivation_Pattern;
        }

        /**
         * Sets the value of the derivation_Pattern property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDerivation_Pattern(String value) {
            this.derivation_Pattern = value;
        }

        /**
         * Gets the value of the disambiguate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDisambiguate() {
            return disambiguate;
        }

        /**
         * Sets the value of the disambiguate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDisambiguate(String value) {
            this.disambiguate = value;
        }

        /**
         * Gets the value of the filter_Mapping property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isFilter_Mapping() {
            if (filter_Mapping == null) {
                return false;
            } else {
                return filter_Mapping;
            }
        }

        /**
         * Sets the value of the filter_Mapping property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setFilter_Mapping(Boolean value) {
            this.filter_Mapping = value;
        }

        /**
         * Gets the value of the for_Each_Select_Id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFor_Each_Select_Id() {
            return for_Each_Select_Id;
        }

        /**
         * Sets the value of the for_Each_Select_Id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFor_Each_Select_Id(String value) {
            this.for_Each_Select_Id = value;
        }

        /**
         * Gets the value of the ft_Col property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFt_Col() {
            return ft_Col;
        }

        /**
         * Sets the value of the ft_Col property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFt_Col(String value) {
            this.ft_Col = value;
        }

        /**
         * Gets the value of the generator property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGenerator() {
            return generator;
        }

        /**
         * Sets the value of the generator property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGenerator(String value) {
            this.generator = value;
        }

        /**
         * Gets the value of the gmlVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGmlVersion() {
            return gmlVersion;
        }

        /**
         * Sets the value of the gmlVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGmlVersion(String value) {
            this.gmlVersion = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Gets the value of the is_Reference property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isIs_Reference() {
            return is_Reference;
        }

        /**
         * Sets the value of the is_Reference property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setIs_Reference(Boolean value) {
            this.is_Reference = value;
        }

        /**
         * Gets the value of the isMappedGeometry property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isIsMappedGeometry() {
            return isMappedGeometry;
        }

        /**
         * Sets the value of the isMappedGeometry property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setIsMappedGeometry(Boolean value) {
            this.isMappedGeometry = value;
        }

        /**
         * Gets the value of the isReference property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isIsReference() {
            return isReference;
        }

        /**
         * Sets the value of the isReference property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setIsReference(Boolean value) {
            this.isReference = value;
        }

        /**
         * Gets the value of the map_Targetpath property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isMap_Targetpath() {
            return map_Targetpath;
        }

        /**
         * Sets the value of the map_Targetpath property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setMap_Targetpath(Boolean value) {
            this.map_Targetpath = value;
        }

        /**
         * Gets the value of the mapped_Geometry property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isMapped_Geometry() {
            return mapped_Geometry;
        }

        /**
         * Sets the value of the mapped_Geometry property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setMapped_Geometry(Boolean value) {
            this.mapped_Geometry = value;
        }

        /**
         * Gets the value of the mapping_Mode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMapping_Mode() {
            if (mapping_Mode == null) {
                return "value";
            } else {
                return mapping_Mode;
            }
        }

        /**
         * Sets the value of the mapping_Mode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMapping_Mode(String value) {
            this.mapping_Mode = value;
        }

        /**
         * Gets the value of the match property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMatch() {
            return match;
        }

        /**
         * Sets the value of the match property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMatch(String value) {
            this.match = value;
        }

        /**
         * Gets the value of the nil_Reason property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNil_Reason() {
            return nil_Reason;
        }

        /**
         * Sets the value of the nil_Reason property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNil_Reason(String value) {
            this.nil_Reason = value;
        }

        /**
         * Gets the value of the nil_Value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNil_Value() {
            return nil_Value;
        }

        /**
         * Sets the value of the nil_Value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNil_Value(String value) {
            this.nil_Value = value;
        }

        /**
         * Gets the value of the no_Output property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isNo_Output() {
            return no_Output;
        }

        /**
         * Sets the value of the no_Output property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setNo_Output(Boolean value) {
            this.no_Output = value;
        }

        /**
         * Gets the value of the noOutput property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isNoOutput() {
            return noOutput;
        }

        /**
         * Sets the value of the noOutput property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setNoOutput(Boolean value) {
            this.noOutput = value;
        }

        /**
         * Gets the value of the oid_Col property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOid_Col() {
            return oid_Col;
        }

        /**
         * Sets the value of the oid_Col property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOid_Col(String value) {
            this.oid_Col = value;
        }

        /**
         * Gets the value of the schema_Codes property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSchema_Codes() {
            return schema_Codes;
        }

        /**
         * Sets the value of the schema_Codes property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSchema_Codes(String value) {
            this.schema_Codes = value;
        }

        /**
         * Gets the value of the select_Id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSelect_Id() {
            return select_Id;
        }

        /**
         * Sets the value of the select_Id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSelect_Id(String value) {
            this.select_Id = value;
        }

        /**
         * Gets the value of the significant_For_Emptiness property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSignificant_For_Emptiness() {
            return significant_For_Emptiness;
        }

        /**
         * Sets the value of the significant_For_Emptiness property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSignificant_For_Emptiness(Boolean value) {
            this.significant_For_Emptiness = value;
        }

        /**
         * Gets the value of the significantForEmptiness property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSignificantForEmptiness() {
            return significantForEmptiness;
        }

        /**
         * Sets the value of the significantForEmptiness property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSignificantForEmptiness(Boolean value) {
            this.significantForEmptiness = value;
        }

        /**
         * Gets the value of the srid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSrid() {
            return srid;
        }

        /**
         * Sets the value of the srid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSrid(String value) {
            this.srid = value;
        }

        /**
         * Gets the value of the srs property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSrs() {
            return srs;
        }

        /**
         * Sets the value of the srs property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSrs(String value) {
            this.srs = value;
        }

        /**
         * Gets the value of the suppress_Xml_Entities_Encoding property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSuppress_Xml_Entities_Encoding() {
            return suppress_Xml_Entities_Encoding;
        }

        /**
         * Sets the value of the suppress_Xml_Entities_Encoding property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSuppress_Xml_Entities_Encoding(Boolean value) {
            this.suppress_Xml_Entities_Encoding = value;
        }

        /**
         * Gets the value of the suppressXMLEntitiesEncoding property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSuppressXMLEntitiesEncoding() {
            return suppressXMLEntitiesEncoding;
        }

        /**
         * Sets the value of the suppressXMLEntitiesEncoding property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSuppressXMLEntitiesEncoding(Boolean value) {
            this.suppressXMLEntitiesEncoding = value;
        }

        /**
         * Gets the value of the table_Name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTable_Name() {
            return table_Name;
        }

        /**
         * Sets the value of the table_Name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTable_Name(String value) {
            this.table_Name = value;
        }

        /**
         * Gets the value of the target property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTarget() {
            if (target == null) {
                return "";
            } else {
                return target;
            }
        }

        /**
         * Sets the value of the target property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * Gets the value of the use_Geotypes property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUse_Geotypes() {
            return use_Geotypes;
        }

        /**
         * Sets the value of the use_Geotypes property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUse_Geotypes(String value) {
            this.use_Geotypes = value;
        }

        /**
         * Gets the value of the useGeotypes property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUseGeotypes() {
            return useGeotypes;
        }

        /**
         * Sets the value of the useGeotypes property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUseGeotypes(String value) {
            this.useGeotypes = value;
        }

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the value_Type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue_Type() {
            return value_Type;
        }

        /**
         * Sets the value of the value_Type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue_Type(String value) {
            this.value_Type = value;
        }

        /**
         * Gets the value of the valueType property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValueType() {
            return valueType;
        }

        /**
         * Sets the value of the valueType property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValueType(String value) {
            this.valueType = value;
        }

    }

}
