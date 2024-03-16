package cc.valdemar.foz.fozquests.utils.serializers;

import com.google.gson.reflect.TypeToken;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemSerializer implements TypeSerializer<ItemStack> {
    public static final ItemSerializer INSTANCE = new ItemSerializer();

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Component name = node.node("name").get(Component.class);
        if(name == null) throw new RuntimeException("Name was not found for: " + node);

        Material material = node.node("material").get(Material.class, Material.STONE);
        List<Component> lore = node.node("lore").getList(Component.class, new ArrayList<>());
        List<ItemFlag> flags = node.node("item-flags").getList(ItemFlag.class, new ArrayList<>());

        int amount = node.node("amount").getInt(1);
        int customModelData = node.node("model-data").getInt(1);
        boolean glow = node.node("glow").getBoolean(false);
        boolean unbreakable = node.node("unbreakable").getBoolean(false);

        ItemStack item = ItemBuilder.from(material)
                .name(name)
                .lore(lore)
                .flags(flags.toArray(ItemFlag[]::new))
                .amount(amount)
                .model(customModelData)
                .glow(glow)
                .unbreakable(unbreakable)
                .build();

        if(item.hasItemMeta()) {
            item.editMeta(meta -> {
                PersistentDataContainer pdc = meta.getPersistentDataContainer();

                String pdcText = node.node("pdc").getString();
                if(pdcText != null) {
                    String[] pdcSplit = pdcText.split(":");
                    NamespacedKey key = new NamespacedKey(pdcSplit[0], pdcSplit[1]);
                    String value = pdcSplit[2];
                    pdc.set(key, PersistentDataType.STRING, value);
                }
            });
        }

        return item;
    }

    private final Type componentListType = new TypeToken<List<Component>>() {}.getType();
    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if(obj == null) return;
        node.node("material").set(obj.getType());
        node.node("lore").set(componentListType, obj.lore());

        Set<ItemFlag> itemFlags = obj.getItemFlags();
        if(!itemFlags.isEmpty()) node.node("item-flags").set(itemFlags);

        int amount = obj.getAmount();
        if(amount > 1) node.node("amount").set(obj.getAmount());

        if(!obj.hasItemMeta()) return;

        ItemMeta meta = obj.getItemMeta();
        node.node("name").set(meta.displayName());
        if(meta.hasCustomModelData()) {
            node.node("model-data").set(meta.getCustomModelData());
        }
        boolean glow = meta.hasEnchant(Enchantment.LURE);
        if(glow) node.node("glow").set(true);

        if(meta.isUnbreakable()) node.node("unbreakable").set(true);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if(!pdc.isEmpty()) {
            NamespacedKey key = pdc.getKeys().stream().findFirst().orElseThrow();
            node.node("pdc").set(key.getNamespace() + ":" + key.getKey() + ":" + pdc.get(key, PersistentDataType.STRING));
        }
    }
}