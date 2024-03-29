
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
 * &lt;p&gt;Java-Klasse für anonymous complex type.
 * 
 * &lt;p&gt;Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
     * Ruft den Wert der name-Eigenschaft ab.
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
     * Legt den Wert der name-Eigenschaft fest.
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
     * Ruft den Wert der title-Eigenschaft ab.
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
     * Legt den Wert der title-Eigenschaft fest.
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
     * Ruft den Wert der abstract-Eigenschaft ab.
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
     * Legt den Wert der abstract-Eigenschaft fest.
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
     * Ruft den Wert der suppressIdentity-Eigenschaft ab.
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
     * Legt den Wert der suppressIdentity-Eigenschaft fest.
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
     * Ruft den Wert der pathAliases-Eigenschaft ab.
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
     * Legt den Wert der pathAliases-Eigenschaft fest.
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
     * Ruft den Wert der oraSFeatureTypeImpl-Eigenschaft ab.
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
     * Legt den Wert der oraSFeatureTypeImpl-Eigenschaft fest.
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
     * Ruft den Wert der pgisFeatureTypeImpl-Eigenschaft ab.
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
     * Legt den Wert der pgisFeatureTypeImpl-Eigenschaft fest.
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
     * Ruft den Wert der gdbsqlFeatureTypeImpl-Eigenschaft ab.
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
     * Legt den Wert der gdbsqlFeatureTypeImpl-Eigenschaft fest.
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
     * Ruft den Wert der includeDerivations-Eigenschaft ab.
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
     * Legt den Wert der includeDerivations-Eigenschaft fest.
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
     * Ruft den Wert der mode-Eigenschaft ab.
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
     * Legt den Wert der mode-Eigenschaft fest.
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
     * &lt;p&gt;Java-Klasse für anonymous complex type.
     * 
     * &lt;p&gt;Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
         * Ruft den Wert der value-Eigenschaft ab.
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
         * Legt den Wert der value-Eigenschaft fest.
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
         * Ruft den Wert der wfsVersion-Eigenschaft ab.
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
         * Legt den Wert der wfsVersion-Eigenschaft fest.
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
     * &lt;p&gt;Java-Klasse für anonymous complex type.
     * 
     * &lt;p&gt;Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
         * &lt;p&gt;Java-Klasse für anonymous complex type.
         * 
         * &lt;p&gt;Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
             * Ruft den Wert der pattern-Eigenschaft ab.
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
             * Legt den Wert der pattern-Eigenschaft fest.
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
             * Ruft den Wert der replacement-Eigenschaft ab.
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
             * Legt den Wert der replacement-Eigenschaft fest.
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
             * Ruft den Wert der externalUse-Eigenschaft ab.
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
             * Legt den Wert der externalUse-Eigenschaft fest.
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
             * Ruft den Wert der gmlVersion-Eigenschaft ab.
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
             * Legt den Wert der gmlVersion-Eigenschaft fest.
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
