
package de.interactive_instruments.xtraserver.config.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
     * Gets the value of the rootElementName property.
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
     * Sets the value of the rootElementName property.
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
     * Gets the value of the mappings property.
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
     * Sets the value of the mappings property.
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
