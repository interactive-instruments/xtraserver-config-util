package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Database")
public class Database {
    @XmlElement(name = "PGISDataImpl")
    protected PGISDataImpl pgisDataImpl;

    public PGISDataImpl getpGISDataImpl() {
        return pgisDataImpl;
    }
}
