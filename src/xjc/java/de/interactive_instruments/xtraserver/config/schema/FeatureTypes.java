
package de.interactive_instruments.xtraserver.config.schema;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
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
 *       &amp;lt;choice maxOccurs="unbounded" minOccurs="0"&amp;gt;
 *         &amp;lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}FeatureType"/&amp;gt;
 *         &amp;lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}AdditionalMappings"/&amp;gt;
 *       &amp;lt;/choice&amp;gt;
 *       &amp;lt;attribute name="defaultDbSchema" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="appSchemaGenerator" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "featureTypeOrAdditionalMappings"
})
@XmlRootElement(name = "FeatureTypes")
public class FeatureTypes {

    @XmlElements({
        @XmlElement(name = "FeatureType", type = FeatureType.class),
        @XmlElement(name = "AdditionalMappings", type = AdditionalMappings.class)
    })
    protected List<Object> featureTypeOrAdditionalMappings;
    @XmlAttribute(name = "defaultDbSchema")
    protected String defaultDbSchema;
    @XmlAttribute(name = "appSchemaGenerator")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object appSchemaGenerator;

    /**
     * Gets the value of the featureTypeOrAdditionalMappings property.
     * 
     * &lt;p&gt;
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a &lt;CODE&gt;set&lt;/CODE&gt; method for the featureTypeOrAdditionalMappings property.
     * 
     * &lt;p&gt;
     * For example, to add a new item, do as follows:
     * &lt;pre&gt;
     *    getFeatureTypeOrAdditionalMappings().add(newItem);
     * &lt;/pre&gt;
     * 
     * 
     * &lt;p&gt;
     * Objects of the following type(s) are allowed in the list
     * {@link FeatureType }
     * {@link AdditionalMappings }
     * 
     * 
     */
    public List<Object> getFeatureTypeOrAdditionalMappings() {
        if (featureTypeOrAdditionalMappings == null) {
            featureTypeOrAdditionalMappings = new ArrayList<Object>();
        }
        return this.featureTypeOrAdditionalMappings;
    }

    /**
     * Gets the value of the defaultDbSchema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultDbSchema() {
        return defaultDbSchema;
    }

    /**
     * Sets the value of the defaultDbSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultDbSchema(String value) {
        this.defaultDbSchema = value;
    }

    /**
     * Gets the value of the appSchemaGenerator property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getAppSchemaGenerator() {
        return appSchemaGenerator;
    }

    /**
     * Sets the value of the appSchemaGenerator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setAppSchemaGenerator(Object value) {
        this.appSchemaGenerator = value;
    }

}
