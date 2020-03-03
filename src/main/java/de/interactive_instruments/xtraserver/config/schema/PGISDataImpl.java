package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PGISDataImpl")
public class PGISDataImpl {
    @XmlElement(name = "GeometryExtent")
    protected GeometryExtent geometryExtent;

    public GeometryExtent getGeometryExtent() {
        return geometryExtent;
    }
}
