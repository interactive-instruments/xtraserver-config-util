
package de.interactive_instruments.xtraserver.config.schema;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java-Klasse für loggingExtensionType.
 * 
 * &lt;p&gt;Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * &lt;pre&gt;
 * &amp;lt;simpleType name="loggingExtensionType"&amp;gt;
 *   &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *     &amp;lt;enumeration value="short"/&amp;gt;
 *   &amp;lt;/restriction&amp;gt;
 * &amp;lt;/simpleType&amp;gt;
 * &lt;/pre&gt;
 * 
 */
@XmlType(name = "loggingExtensionType")
@XmlEnum
public enum LoggingExtensionType {

    @XmlEnumValue("short")
    SHORT("short");
    private final String value;

    LoggingExtensionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LoggingExtensionType fromValue(String v) {
        for (LoggingExtensionType c: LoggingExtensionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
