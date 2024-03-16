package cc.valdemar.foz.fozquests.utils.serializers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ComponentSerializer implements TypeSerializer<Component> {
    public static final ComponentSerializer INSTANCE = new ComponentSerializer();
    @Override
    public Component deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return MiniMessage.miniMessage().deserialize(node.require(String.class));
    }

    @Override
    public void serialize(Type type, @Nullable Component obj, ConfigurationNode node) throws SerializationException {
        if(obj != null) node.set(MiniMessage.miniMessage().serialize(obj));
    }
}
