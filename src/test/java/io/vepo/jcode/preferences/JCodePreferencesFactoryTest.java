package io.vepo.jcode.preferences;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.getPreferencesFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JCodePreferencesFactoryTest {

    @BeforeEach
    void cleanup() {
        getPreferencesFile().delete();
    }

    @Test
    @DisplayName("Store value")
    void storeTest() throws BackingStoreException {
        JCodePreferencesFactory factory = new JCodePreferencesFactory();
        Preferences userRoot = factory.userRoot();
        assertThat(userRoot.get("test", "test")).isEqualTo("test");
        userRoot.put("test", "value");
        assertThat(userRoot.get("test", "test")).isEqualTo("value");
        userRoot.flush();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode storedContent = assertDoesNotThrow(() -> mapper.readTree(getPreferencesFile()));
        assertThat(storedContent.get("test").asText()).isEqualTo("value");
    }

    @Test
    @DisplayName("Store value")
    void storeChildTest() throws BackingStoreException {
        JCodePreferencesFactory factory = new JCodePreferencesFactory();
        Preferences userRoot = factory.userRoot();
        Preferences node = userRoot.node("element");
        assertThat(node.get("test", "test")).isEqualTo("test");
        node.put("test", "value");
        assertThat(node.get("test", "test")).isEqualTo("value");
        node.flush();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode storedContent = assertDoesNotThrow(() -> mapper.readTree(getPreferencesFile()));
        assertThat(storedContent.get("element")).isNotEmpty();
        assertThat(storedContent.get("element").get("test").asText()).isEqualTo("value");
    }

    @Test
    @DisplayName("Conflict")
    void conflictTest() throws BackingStoreException {
        JCodePreferencesFactory factory = new JCodePreferencesFactory();
        Preferences userRoot = factory.userRoot();
        userRoot.node("element");
        assertThrows(IllegalStateException.class, () -> userRoot.put("element", "invalidVale"));
    }
}
