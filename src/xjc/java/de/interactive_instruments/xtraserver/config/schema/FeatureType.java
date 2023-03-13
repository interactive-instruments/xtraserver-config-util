
package de.interactive_instruments.xtraserver.config.schema;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


/**
 * &lt;p&gt;Java class for anonymous complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}Name" minOccurs="0"/&amp;gt;
 *         &amp;lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}Title" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="Abstract" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="Keyword" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="OutputFormat" maxOccurs="unbounded" minOccurs="0"&amp;gt;
 *           &amp;lt;complexType&amp;gt;
 *             &amp;lt;simpleContent&amp;gt;
 *               &amp;lt;extension base="&amp;lt;http://www.w3.org/2001/XMLSchema&amp;gt;string"&amp;gt;
 *                 &amp;lt;attribute name="wfsVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *               &amp;lt;/extension&amp;gt;
 *             &amp;lt;/simpleContent&amp;gt;
 *           &amp;lt;/complexType&amp;gt;
 *         &amp;lt;/element&amp;gt;
 *         &amp;lt;element name="SuppressIdentity" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="PathAliases" minOccurs="0"&amp;gt;
 *           &amp;lt;complexType&amp;gt;
 *             &amp;lt;complexContent&amp;gt;
 *               &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *                 &amp;lt;sequence&amp;gt;
 *                   &amp;lt;element name="PathAlias" maxOccurs="unbounded" minOccurs="0"&amp;gt;
 *                     &amp;lt;complexType&amp;gt;
 *                       &amp;lt;complexContent&amp;gt;
 *                         &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *                           &amp;lt;sequence&amp;gt;
 *                             &amp;lt;element name="Pattern" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
 *                             &amp;lt;element name="Replacement" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
 *                           &amp;lt;/sequence&amp;gt;
 *                           &amp;lt;attribute name="externalUse" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&amp;gt;
 *                           &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *                         &amp;lt;/restriction&amp;gt;
 *                       &amp;lt;/complexContent&amp;gt;
 *                     &amp;lt;/complexType&amp;gt;
 *                   &amp;lt;/element&amp;gt;
 *                 &amp;lt;/sequence&amp;gt;
 *               &amp;lt;/restriction&amp;gt;
 *             &amp;lt;/complexContent&amp;gt;
 *           &amp;lt;/complexType&amp;gt;
 *         &amp;lt;/element&amp;gt;
 *         &amp;lt;choice&amp;gt;
 *           &amp;lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}OraSFeatureTypeImpl" minOccurs="0"/&amp;gt;
 *           &amp;lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}PGISFeatureTypeImpl" minOccurs="0"/&amp;gt;
 *           &amp;lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}GDBSQLFeatureTypeImpl" minOccurs="0"/&amp;gt;
 *         &amp;lt;/choice&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="includeDerivations" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&amp;gt;
 *       &amp;lt;attribute name="mode" type="{http://www.interactive-instruments.de/namespaces/XtraServer}EnablingType" default="enabled" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "title",
    "_abstract",
    "keyword",
    "outputFormat",
    "suppressIdentity",
    "pathAliases",
    "oraSFeatureTypeImpl",
    "pgisFeatureTypeImpl",
    "gdbsqlFeatureTypeImpl"
})
@XmlRootElement(name = "FeatureType")
public class FeatureType {

    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Title")
    protected String title;
    @XmlElement(name = "Abstract")
    protected String _abstract;
    @XmlElement(name = "Keyword")
    protected List<String> keyword;
    @XmlElement(name = "OutputFormat")
    protected List<FeatureType.OutputFormat> outputFormat;
    @XmlElement(name = "SuppressIdentity")
    protected Boolean suppressIdentity;
    @XmlElement(name = "PathAliases")
    protected FeatureType.PathAliases pathAliases;
    @XmlElement(name = "OraSFeatureTypeImpl")
    protected SQLFeatureTypeImplType oraSFeatureTypeImpl;
    @XmlElement(name = "PGISFeatureTypeImpl")
    protected SQLFeatureTypeImplType pgisFeatureTypeImpl;
    @XmlElement(name = "GDBSQLFeatureTypeImpl")
    protected SQLFeatureTypeImplType gdbsqlFeatureTypeImpl;
    @XmlAttribute(name = "includeDerivations")
    protected Boolean includeDerivations;
    @XmlAttribute(name = "mode")
    protected EnablingType mode;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the abstract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Sets the value of the abstract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbstract(String value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the keyword property.
     * 
     * &lt;p&gt;
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a &lt;CODE&gt;set&lt;/CODE&gt; method for the keyword property.
     * 
     * &lt;p&gt;
     * For example, to add a new item, do as follows:
     * &lt;pre&gt;
     *    getKeyword().add(newItem);
     * &lt;/pre&gt;
     * 
     * 
     * &lt;p&gt;
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getKeyword() {
        if (keyword == null) {
            keyword = new ArrayList<String>();
        }
        return this.keyword;
    }

    /**
     * Gets the value of the outputFormat property.
     * 
     * &lt;p&gt;
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a &lt;CODE&gt;set&lt;/CODE&gt; method for the outputFormat property.
     * 
     * &lt;p&gt;
     * For example, to add a new item, do as follows:
     * &lt;pre&gt;
     *    getOutputFormat().add(newItem);
     * &lt;/pre&gt;
     * 
     * 
     * &lt;p&gt;
     * Objects of the following type(s) are allowed in the list
     * {@link FeatureType.OutputFormat }
     * 
     * 
     */
    public List<FeatureType.OutputFormat> getOutputFormat() {
        if (outputFormat == null) {
            outputFormat = new ArrayList<FeatureType.OutputFormat>();
        }
        return this.outputFormat;
    }

    /**
     * Gets the value of the suppressIdentity property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSuppressIdentity() {
        return suppressIdentity;
    }

    /**
     * Sets the value of the suppressIdentity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSuppressIdentity(Boolean value) {
        this.suppressIdentity = value;
    }

    /**
     * Gets the value of the pathAliases property.
     * 
     * @return
     *     possible object is
     *     {@link FeatureType.PathAliases }
     *     
     */
    public FeatureType.PathAliases getPathAliases() {
        return pathAliases;
    }

    /**
     * Sets the value of the pathAliases property.
     * 
     * @param value
     *     allowed object is
     *     {@link FeatureType.PathAliases }
     *     
     */
    public void setPathAliases(FeatureType.PathAliases value) {
        this.pathAliases = value;
    }

    /**
     * Gets the value of the oraSFeatureTypeImpl property.
     * 
     * @return
     *     possible object is
     *     {@link SQLFeatureTypeImplType }
     *     
     */
    public SQLFeatureTypeImplType getOraSFeatureTypeImpl() {
        return oraSFeatureTypeImpl;
    }

    /**
     * Sets the value of the oraSFeatureTypeImpl property.
     * 
     * @param value
     *     allowed object is
     *     {@link SQLFeatureTypeImplType }
     *     
     */
    public void setOraSFeatureTypeImpl(SQLFeatureTypeImplType value) {
        this.oraSFeatureTypeImpl = value;
    }

    /**
     * Gets the value of the pgisFeatureTypeImpl property.
     * 
     * @return
     *     possible object is
     *     {@link SQLFeatureTypeImplType }
     *     
     */
    public SQLFeatureTypeImplType getPGISFeatureTypeImpl() {
        return pgisFeatureTypeImpl;
    }

    /**
     * Sets the value of the pgisFeatureTypeImpl property.
     * 
     * @param value
     *     allowed object is
     *     {@link SQLFeatureTypeImplType }
     *     
     */
    public void setPGISFeatureTypeImpl(SQLFeatureTypeImplType value) {
        this.pgisFeatureTypeImpl = value;
    }

    /**
     * Gets the value of the gdbsqlFeatureTypeImpl property.
     * 
     * @return
     *     possible object is
     *     {@link SQLFeatureTypeImplType }
     *     
     */
    public SQLFeatureTypeImplType getGDBSQLFeatureTypeImpl() {
        return gdbsqlFeatureTypeImpl;
    }

    /**
     * Sets the value of the gdbsqlFeatureTypeImpl property.
     * 
     * @param value
     *     allowed object is
     *     {@link SQLFeatureTypeImplType }
     *     
     */
    public void setGDBSQLFeatureTypeImpl(SQLFeatureTypeImplType value) {
        this.gdbsqlFeatureTypeImpl = value;
    }

    /**
     * Gets the value of the includeDerivations property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIncludeDerivations() {
        if (includeDerivations == null) {
            return false;
        } else {
            return includeDerivations;
        }
    }

    /**
     * Sets the value of the includeDerivations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeDerivations(Boolean value) {
        this.includeDerivations = value;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link EnablingType }
     *     
     */
    public EnablingType getMode() {
        if (mode == null) {
            return EnablingType.ENABLED;
        } else {
            return mode;
        }
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnablingType }
     *     
     */
    public void setMode(EnablingType value) {
        this.mode = value;
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
     *       &amp;lt;attribute name="wfsVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
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
    public static class OutputFormat {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "wfsVersion")
        protected String wfsVersion;

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
         * Gets the value of the wfsVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWfsVersion() {
            return wfsVersion;
        }

        /**
         * Sets the value of the wfsVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWfsVersion(String value) {
            this.wfsVersion = value;
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
     *       &amp;lt;sequence&amp;gt;
     *         &amp;lt;element name="PathAlias" maxOccurs="unbounded" minOccurs="0"&amp;gt;
     *           &amp;lt;complexType&amp;gt;
     *             &amp;lt;complexContent&amp;gt;
     *               &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
     *                 &amp;lt;sequence&amp;gt;
     *                   &amp;lt;element name="Pattern" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
     *                   &amp;lt;element name="Replacement" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
     *                 &amp;lt;/sequence&amp;gt;
     *                 &amp;lt;attribute name="externalUse" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&amp;gt;
     *                 &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
     *               &amp;lt;/restriction&amp;gt;
     *             &amp;lt;/complexContent&amp;gt;
     *           &amp;lt;/complexType&amp;gt;
     *         &amp;lt;/element&amp;gt;
     *       &amp;lt;/sequence&amp;gt;
     *     &amp;lt;/restriction&amp;gt;
     *   &amp;lt;/complexContent&amp;gt;
     * &amp;lt;/complexType&amp;gt;
     * &lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "pathAlias"
    })
    public static class PathAliases {

        @XmlElement(name = "PathAlias")
        protected List<FeatureType.PathAliases.PathAlias> pathAlias;

        /**
         * Gets the value of the pathAlias property.
         * 
         * &lt;p&gt;
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a &lt;CODE&gt;set&lt;/CODE&gt; method for the pathAlias property.
         * 
         * &lt;p&gt;
         * For example, to add a new item, do as follows:
         * &lt;pre&gt;
         *    getPathAlias().add(newItem);
         * &lt;/pre&gt;
         * 
         * 
         * &lt;p&gt;
         * Objects of the following type(s) are allowed in the list
         * {@link FeatureType.PathAliases.PathAlias }
         * 
         * 
         */
        public List<FeatureType.PathAliases.PathAlias> getPathAlias() {
            if (pathAlias == null) {
                pathAlias = new ArrayList<FeatureType.PathAliases.PathAlias>();
            }
            return this.pathAlias;
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
         *       &amp;lt;sequence&amp;gt;
         *         &amp;lt;element name="Pattern" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
         *         &amp;lt;element name="Replacement" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
         *       &amp;lt;/sequence&amp;gt;
         *       &amp;lt;attribute name="externalUse" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&amp;gt;
         *       &amp;lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
         *     &amp;lt;/restriction&amp;gt;
         *   &amp;lt;/complexContent&amp;gt;
         * &amp;lt;/complexType&amp;gt;
         * &lt;/pre&gt;
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "pattern",
            "replacement"
        })
        public static class PathAlias {

            @XmlElement(name = "Pattern", required = true)
            protected String pattern;
            @XmlElement(name = "Replacement", required = true)
            protected String replacement;
            @XmlAttribute(name = "externalUse")
            protected Boolean externalUse;
            @XmlAttribute(name = "gmlVersion")
            protected String gmlVersion;

            /**
             * Gets the value of the pattern property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPattern() {
                return pattern;
            }

            /**
             * Sets the value of the pattern property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPattern(String value) {
                this.pattern = value;
            }

            /**
             * Gets the value of the replacement property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getReplacement() {
                return replacement;
            }

            /**
             * Sets the value of the replacement property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setReplacement(String value) {
                this.replacement = value;
            }

            /**
             * Gets the value of the externalUse property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public boolean isExternalUse() {
                if (externalUse == null) {
                    return false;
                } else {
                    return externalUse;
                }
            }

            /**
             * Sets the value of the externalUse property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setExternalUse(Boolean value) {
                this.externalUse = value;
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

        }

    }

}
