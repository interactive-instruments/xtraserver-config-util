
package de.interactive_instruments.xtraserver.config.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java-Klasse für SQLFeatureTypeImplType complex type.
 * 
 * &lt;p&gt;Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="SQLFeatureTypeImplType"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{http://www.interactive-instruments.de/namespaces/XtraServer}MappingsSequenceType"&amp;gt;
 *       &amp;lt;attribute name="logging" default="false"&amp;gt;
 *         &amp;lt;simpleType&amp;gt;
 *           &amp;lt;union memberTypes=" {http://www.w3.org/2001/XMLSchema}boolean {http://www.interactive-instruments.de/namespaces/XtraServer}loggingExtensionType"&amp;gt;
 *           &amp;lt;/union&amp;gt;
 *         &amp;lt;/simpleType&amp;gt;
 *       &amp;lt;/attribute&amp;gt;
 *       &amp;lt;attribute name="useTempTable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&amp;gt;
 *       &amp;lt;attribute name="tempTableName" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="FTCode" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SQLFeatureTypeImplType")
public class SQLFeatureTypeImplType
    extends MappingsSequenceType
{

    @XmlAttribute(name = "logging")
    protected String logging;
    @XmlAttribute(name = "useTempTable")
    protected Boolean useTempTable;
    @XmlAttribute(name = "tempTableName")
    protected String tempTableName;
    @XmlAttribute(name = "FTCode")
    protected String ftCode;

    /**
     * Ruft den Wert der logging-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogging() {
        if (logging == null) {
            return "false";
        } else {
            return logging;
        }
    }

    /**
     * Legt den Wert der logging-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogging(String value) {
        this.logging = value;
    }

    /**
     * Ruft den Wert der useTempTable-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isUseTempTable() {
        if (useTempTable == null) {
            return false;
        } else {
            return useTempTable;
        }
    }

    /**
     * Legt den Wert der useTempTable-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseTempTable(Boolean value) {
        this.useTempTable = value;
    }

    /**
     * Ruft den Wert der tempTableName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTempTableName() {
        return tempTableName;
    }

    /**
     * Legt den Wert der tempTableName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTempTableName(String value) {
        this.tempTableName = value;
    }

    /**
     * Ruft den Wert der ftCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFTCode() {
        return ftCode;
    }

    /**
     * Legt den Wert der ftCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFTCode(String value) {
        this.ftCode = value;
    }

}
