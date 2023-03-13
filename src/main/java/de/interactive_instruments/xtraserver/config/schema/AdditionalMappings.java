
package de.interactive_instruments.xtraserver.config.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
 *         &amp;lt;element name="RootElementName" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
 *         &amp;lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}Mappings"/&amp;gt;
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
    "rootElementName",
    "mappings"
})
@XmlRootElement(name = "AdditionalMappings")
public class AdditionalMappings {

    @XmlElement(name = "RootElementName", required = true)
    protected String rootElementName;
    @XmlElement(name = "Mappings", required = true)
    protected MappingsSequenceType mappings;

    /**
     * Ruft den Wert der rootElementName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootElementName() {
        return rootElementName;
    }

    /**
     * Legt den Wert der rootElementName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootElementName(String value) {
        this.rootElementName = value;
    }

    /**
     * Ruft den Wert der mappings-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MappingsSequenceType }
     *     
     */
    public MappingsSequenceType getMappings() {
        return mappings;
    }

    /**
     * Legt den Wert der mappings-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MappingsSequenceType }
     *     
     */
    public void setMappings(MappingsSequenceType value) {
        this.mappings = value;
    }

}
