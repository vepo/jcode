package io.vepo.jcode.preferences;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonPreferences extends AbstractPreferences {
    private static final Logger log = Logger.getLogger(JsonPreferences.class.getName());
    private static ObjectMapper MAPPER = new ObjectMapper();

    private ObjectNode root;
    private Map<String, JsonPreferences> children;

    public JsonPreferences() {
        this(null, "");
    }

    public JsonPreferences(JsonPreferences parent, String name) {
        super(parent, name);

        log.finest("Instantiating node " + name);

        root = MAPPER.createObjectNode();
        if (Objects.nonNull(parent)) {
            parent.root.set(name, root);
        }
        children = new TreeMap<String, JsonPreferences>();

        try {
            sync();
        } catch (BackingStoreException e) {
            log.log(Level.SEVERE, "Unable to sync on creation of node " + name, e);
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
            log.log(Level.SEVERE, "Unable to flush after putting " + key, e);
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
            log.log(Level.SEVERE, "Unable to flush after removing " + key, e);
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
        return children.keySet().toArray(new String[children.keySet().size()]);
    }

    protected JsonPreferences childSpi(String name) {
        JsonPreferences child = children.get(name);
        if (child == null || child.isRemoved()) {
            child = new JsonPreferences(this, name);
            children.put(name, child);
        }
        return child;
    }

    protected void syncSpi() throws BackingStoreException {
        if (isRemoved()) {
            return;
        }

        final File file = JCodePreferencesFactory.getPreferencesFile();

        System.out.println("File: " + file.getAbsolutePath() + " exists? " + file.exists());
        if (!file.exists()) {
            return;
        }

        synchronized (file) {
            try {
                root = getJsonNode((ObjectNode) MAPPER.readTree(file), this);
            } catch (IOException e) {
                throw new BackingStoreException(e);
            }
        }
    }

    protected void flushSpi() throws BackingStoreException {
        if (this.parent() == null) {
            final File file = JCodePreferencesFactory.getPreferencesFile();
            synchronized (file) {
                try {
                    MAPPER.writeValue(file, root);
                } catch (IOException e) {
                    throw new BackingStoreException(e);
                }
            }
        } else {
            ((JsonPreferences) this.parent()).flushSpi();
        }
    }

    private ObjectNode getJsonNode(ObjectNode node, JsonPreferences element) {
        if (element == null) {
            return node;
        } else {
            ObjectNode parentNode = getJsonNode(node, (JsonPreferences) element.parent());
            if (parentNode.has(name())) {
                return (ObjectNode) parentNode.get(name());
            } else {
                return parentNode.set(name(), MAPPER.createObjectNode());
            }
        }
    }
}