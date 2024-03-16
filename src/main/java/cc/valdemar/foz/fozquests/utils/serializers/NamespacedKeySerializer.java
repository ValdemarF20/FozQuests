package cc.valdemar.foz.fozquests.utils.serializers;

import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class NamespacedKeySerializer implements TypeSerializer<NamespacedKey> {
    public static final NamespacedKeySerializer INSTANCE = new NamespacedKeySerializer();

    @Override
    public NamespacedKey deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String identifier = node.getString();
        if(identifier == null) return null;
        String[] split = identifier.split(":");
        return new NamespacedKey(split[0], split[1]);
    }

    @Override
    public void serialize(Type type, @Nullable NamespacedKey obj, ConfigurationNode node) throws SerializationException {
        if(obj == null) return;
        node.set(obj.getNamespace() + ":" + obj.getKey());
    }
}