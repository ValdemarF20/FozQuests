package cc.valdemar.foz.fozquests.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ChatUtil {
    public static Component deserialize(String text, TagResolver... placeholders) {
        return MiniMessage.miniMessage().deserialize(text, placeholders);
    }

    public static String serialize(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    public static String serializePlain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component).replace("[", "").replace("]", "");
    }
}
