package io.vepo.jcode.preferences;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.getPreferencesFile;
import static java.util.Objects.isNull;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonPreferences extends AbstractPreferences {
    private static final Logger log = Logger.getLogger(JsonPreferences.class.getName());
    private static ObjectMapper mapper = new ObjectMapper();

    private ObjectNode root;
    private Map<String, JsonPreferences> children;

    public JsonPreferences() {
        this(null, "");
    }

    public JsonPreferences(JsonPreferences parent, String name) {
        super(parent, name);

        if (isNull(parent)) {
            if (getPreferencesFile().exists()) {
                try {
                    root = (ObjectNode) mapper.readTree(getPreferencesFile());
                } catch (IOException e) {
                    log.warning("Invalid file!");
                    root = mapper.createObjectNode();
                }
            } else {
                root = mapper.createObjectNode();
            }
        } else {
            if (parent.root.has(name) && parent.root.get(name).isObject()) {
                root = (ObjectNode) parent.root.get(name);
            } else {
                root = mapper.createObjectNode();
                parent.root.set(name, root);
            }
        }

        children = new HashMap<>();

        try {
            sync();
        } catch (BackingStoreException e) {
            log.log(Level.SEVERE, String.format("Unable to sync on creation of node %s", name), e);
        }
    }

    protected void putSpi(String key, String value) {
        if (root.has(key) && root.get(key).isObject()) {
            throw new IllegalStateException("Exists a node with the same key");
        }
        root.put(key, value);
        try {
            flush();
        } catch (BackingStoreException e) {
            log.log(Level.SEVERE, String.format("Unable to flush after putting %s", key), e);
        }
    }

    protected String getSpi(String key) {
        return root.get(key).asText();
    }

    protected void removeSpi(String key) {
        root.remove(key);
        try {
            flush();
        } catch (BackingStoreException e) {
            log.log(Level.SEVERE, String.format("Unable to flush after removing %s", key), e);
        }
    }

    protected void removeNodeSpi() throws BackingStoreException {
        JsonPreferences parent = (JsonPreferences) parent();
        if (Objects.nonNull(parent)) {
            parent.root.remove(name());
        }
        flush();
    }

    protected String[] keysSpi() throws BackingStoreException {
        return stream(spliteratorUnknownSize(root.fieldNames(), Spliterator.ORDERED), false)
                                                                                            .toArray(String[]::new);
    }

    protected String[] childrenNamesSpi() throws BackingStoreException {
        return stream(spliteratorUnknownSize(root.fieldNames(), Spliterator.ORDERED), false)
                                                                                            .filter(field -> root.get(field)
                                                                                                                 .isObject())
                                                                                            .toArray(String[]::new);
    }

    protected JsonPreferences childSpi(String name) {
        JsonPreferences child = children.get(name);
        if (child == null || child.isRemoved()) {
            child = new JsonPreferences(this, name);
            children.put(name, child);
        }
        return child;
    }

    public List<String> getList(String name) {
        if (root.has(name) && root.get(name).isArray()) {
            ArrayNode arrayNode = (ArrayNode) root.get(name);
            List<String> values = new ArrayList<>(arrayNode.size());
            arrayNode.forEach(node -> values.add(node.asText()));
            return values;
        } else {
            return Collections.emptyList();
        }
    }

    protected void syncSpi() throws BackingStoreException {
        if (isRemoved()) {
            return;
        }

        final File file = getPreferencesFile();

        if (!file.exists()) {
            return;
        }

        if (isNull(parent())) {
            synchronized (file) {
                try {
                    root = (ObjectNode) mapper.readTree(file);
                } catch (IOException e) {
                    throw new BackingStoreException(e);
                }
            }
        } else {
            root = getJsonNode(root, this);
        }
    }

    protected void flushSpi() throws BackingStoreException {
        if (this.parent() == null) {
            final File file = getPreferencesFile();
            synchronized (file) {
                try {
                    mapper.writeValue(file, root);
                } catch (IOException e) {
                    throw new BackingStoreException(e);
                }
            }
        } else {
            ((JsonPreferences) this.parent()).flushSpi();
        }
    }

    private ObjectNode getJsonNode(ObjectNode node, JsonPreferences element) {
        if (isNull(element.parent())) {
            return node;
        } else {
            ObjectNode parentNode = ((JsonPreferences) element.parent()).root;
            if (parentNode.has(name())) {
                return (ObjectNode) parentNode.get(name());
            } else {
                return parentNode.set(name(), mapper.createObjectNode());
            }
        }
    }
}