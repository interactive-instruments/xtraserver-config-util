package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class BoundingBox {
    @XmlAttribute(name = "xmin")
    protected String xmin;

    @XmlAttribute(name = "xmax")
    protected String xmax;

    @XmlAttribute(name = "ymin")
    protected String ymin;

    @XmlAttribute(name = "ymax")
    protected String ymax;


    public String getXmin() {
        return xmin;
    }

    public String getXmax() {
        return xmax;
    }

    public String getYmin() {
        return ymin;
    }

    public String getYmax() {
        return ymax;
    }

}
