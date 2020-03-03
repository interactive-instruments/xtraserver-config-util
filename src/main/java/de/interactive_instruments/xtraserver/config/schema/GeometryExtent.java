package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GeometryExtent")
public class GeometryExtent {

    @XmlElement(name = "NativeSRS")
    protected String nativeSRS;

    @XmlElement(name = "BoundingBox")
    protected BoundingBox boundingBox;

    public String getNativeSRS() {
        return nativeSRS;
    }
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
