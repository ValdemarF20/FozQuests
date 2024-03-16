package cc.valdemar.foz.fozquests.utils.config;

import cc.valdemar.foz.fozquests.utils.ResourceUtils;
import cc.valdemar.foz.fozquests.utils.serializers.ComponentSerializer;
import cc.valdemar.foz.fozquests.utils.serializers.ItemSerializer;
import cc.valdemar.foz.fozquests.utils.serializers.JsonObjectSerializer;
import cc.valdemar.foz.fozquests.utils.serializers.NamespacedKeySerializer;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigurationWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationWrapper.class);
    private final ClassLoader classLoader;
    private final Path dataPath;
    private final String resourcePath;

    private File dataFile;
    private CommentedConfigurationNode root;
    private final YamlConfigurationLoader loader;

    private ValueReference<? extends LocaleReference, CommentedConfigurationNode> localeReference;
    private ConfigurationReference<CommentedConfigurationNode> configurationReference;

    private int configVersion = 0;

    public ConfigurationWrapper(Class<? extends LocaleReference> reference, ClassLoader classLoader, Path dataPath, String resourcePath) {
        this.classLoader = classLoader;
        this.dataPath = dataPath;
        this.resourcePath = resourcePath;
        this.loader = setupConfig(true);
        setLocaleReference(reference);
        loadConfig();
        saveConfig();
    }

    public ConfigurationWrapper(ClassLoader classLoader, Path dataPath, String resourcePath) {
        this.classLoader = classLoader;
        this.dataPath = dataPath;
        this.resourcePath = resourcePath;
        this.loader = setupConfig(false);
        loadConfig();
        saveConfig();
    }

    private YamlConfigurationLoader setupConfig(boolean useLocaleReference) {
        Path path = dataPath.resolve(resourcePath);
        if(!useLocaleReference) {
            dataFile = ResourceUtils.saveResource(classLoader, dataPath, resourcePath);
        }

        return YamlConfigurationLoader.builder()
                .path(path)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .commentsEnabled(true)
                .defaultOptions(opts -> opts.serializers(build -> build
                        .register(Component.class, ComponentSerializer.INSTANCE)
                        .register(JsonObject.class, JsonObjectSerializer.INSTANCE)
                        .register(ItemStack.class, ItemSerializer.INSTANCE)
                        .register(NamespacedKey.class, NamespacedKeySerializer.INSTANCE)))
                .build();
    }

    public void setLocaleReference(Class<? extends LocaleReference> reference) {
        if(reference == null) return;
        try {
            localeReference = (configurationReference = loader.loadToReference()).referenceTo(reference);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
        if(root != null && !root.empty()) localeReference.node().from(root);
        root = localeReference.node();
    }

    /**
     * Save resource from memory into file
     */
    public void saveConfig() {
        try {
            if(localeReference != null && configurationReference != null) {
                configurationReference.save();
            } else {
                loader.save(root);
            }
        } catch (IOException e) {
            LOGGER.error("Exception while saving " + resourcePath, e);
        }
    }

    /**
     * Load resource from file into memory
     */
    public void loadConfig() {
        try {
            root = loader.load();
            if(localeReference != null && configurationReference != null) {
                localeReference = (configurationReference = loader.loadToReference()).referenceTo(localeReference.get().getClass());
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while loading the configuration for " + resourcePath, e);
        }
    }

    /**
     * Update a config using the config-version section
     */
    public void updateConfig() {
        final int version = root.node("config-version").getInt(0);

        if (version == configVersion) {
            return;
        }

        LOGGER.info("Updating " + resourcePath + " to version " + configVersion);

        try {
            if(!dataFile.delete()) {
                LOGGER.error("Error occurred when deleting old config - invalid path");
            }
        } catch (SecurityException e) {
            throw new RuntimeException("Could not update config", e);
        }
        if(localeReference == null) {
            dataFile = ResourceUtils.saveResource(classLoader, dataPath, resourcePath);
        }
    }

    /**
     * Gets the locale reference (only used for classes with @ConfigSerializable)
     * @return Locale reference that will have values from config
     */
    public LocaleReference get() {
        return localeReference.get();
    }

    public CommentedConfigurationNode getRoot() {
        return localeReference == null ? root : localeReference.node();
    }

    public void setRoot(CommentedConfigurationNode root) {
        this.root = root;
    }

    public CommentedConfigurationNode node(Object ... path) {
        return getRoot().node(path);
    }

    /**
     * Set the latest config version
     * @param configVersion Integer value of the version
     */
    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    /**
     * Get the config version
     * @return Integer value representing the version
     */
    public int getConfigVersion() {
        return configVersion;
    }
}