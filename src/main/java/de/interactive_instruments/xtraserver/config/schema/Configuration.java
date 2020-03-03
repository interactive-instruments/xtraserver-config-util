package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Configuration")
public class Configuration {

    @XmlElement(name = "Database")
    protected Database database;

    public Database getDatabase() {
        return database;
    }

}
