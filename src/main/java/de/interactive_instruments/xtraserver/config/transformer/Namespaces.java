/**
 * Copyright 2020 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
class Namespaces {
    private final static Splitter SPLITTER_SLASH = Splitter.on("/");
    private final static Splitter SPLITTER_SLASH_HTTP = Splitter.on(Pattern.compile("/(?=http)"));
    private final static Splitter SPLITTER_COLON = Splitter.on(":");
    private final static Splitter SPLITTER_COLON_HTTP = Splitter.on(Pattern.compile("(?<!http):"));
    private final BiMap<String, String> namespaces;

    /**
     * Construct a Namespaces object and add namespaces URIs to namespace prefixes mappings.
     * Predefined, existing prefixes are not overridden.
     *
     * @param namespaceUriToPrefixMapping a map with namespace URI keys and the associated prefixes
     */
    Namespaces(final Map<String, String> namespaceUriToPrefixMapping) {
        this.namespaces = HashBiMap.create();

        // required for xsi:nil, normally not defined in app schema
        namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        namespaceUriToPrefixMapping.forEach((key, value) -> {
            // containsValue() check required as bimap does not handle putIfAbsent for values
            if (key != null && !key.isEmpty() && !this.namespaces.containsValue(key)) {
                // Change NS URI -> prefix to internal representation prefix -> NS URI
                this.namespaces.putIfAbsent(value, key);
            }
        });
    }

    public QName getQualifiedName(final String prefixedName) {
        final List<String> name = (prefixedName.contains("http") ? SPLITTER_COLON_HTTP : SPLITTER_COLON).splitToList(prefixedName);

        String namespacePrefix = name.size() == 2 ? name.get(0) : "";
        String localName = name.size() == 2 ? name.get(1) : prefixedName;

        if (namespacePrefix.startsWith("@")) {
            namespacePrefix = namespacePrefix.substring(1);
            localName = "@" + localName;
        }

        if (name.size() == 2 && namespaces.get(namespacePrefix) != null) {
            return new QName(namespaces.get(namespacePrefix), localName, namespacePrefix);
        } else if (name.size() == 2 && namespaces.inverse().get(namespacePrefix) != null) {
            return new QName(namespacePrefix, localName, namespaces.inverse().get(namespacePrefix));
        } else if (name.size() == 1) {
            return new QName(localName);
        }

        return null;
    }

    public String getPrefixedName(final QName qualifiedName) {
        String attributePrefix = "";
        String namespacePrefix = "";
        String localName = qualifiedName.getLocalPart();

        if (localName.startsWith("@")) {
            attributePrefix = "@";
            localName = localName.substring(1);
        }
        if (namespaces.inverse().get(qualifiedName.getNamespaceURI()) != null) {
            namespacePrefix = namespaces.inverse().get(qualifiedName.getNamespaceURI()) + ":";
        }
        else if (Strings.isNullOrEmpty(qualifiedName.getNamespaceURI()) && !Strings.isNullOrEmpty(qualifiedName.getPrefix())) {
            namespacePrefix = qualifiedName.getPrefix() + ":";
        }

        return attributePrefix + namespacePrefix + localName;
    }

    public List<QName> getQualifiedPathElements(final String prefixedPath) {
        if (prefixedPath.isBlank()) {
            return List.of();
        }
        return (prefixedPath.contains("http") ? SPLITTER_SLASH_HTTP : SPLITTER_SLASH)
                .splitToList(prefixedPath)
                .stream()
                .map(this::getQualifiedName)
                .collect(Collectors.toList());
    }

    public String getPrefixedPath(final List<QName> qualifiedPathElements) {
        return qualifiedPathElements.stream()
                .map(this::getPrefixedName)
                .collect(Collectors.joining("/"));
    }
}
