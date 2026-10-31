package dev.vladeezyy.rampart.server.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

/**
 * Citadel's tool material. Since 1.21.2 vanilla's {@link ToolMaterial} is a record that repairs from an item tag
 * (no {@code Tier}, no repair ingredient): this class keeps Citadel's fields and builds the record with
 * {@link #toolMaterial()}.
 */
public class CustomToolMaterial {
    private final String name;
    private final int harvestLevel;
    private final int durability;
    private final float damage;
    private final float speed;
    private final int enchantability;
    private final TagKey<Block> incorrectDrops;
    private TagKey<Item> repairItems;
    private ToolMaterial material;

    public CustomToolMaterial(String name, int harvestLevel, int durability, float damage, float speed, int enchantability, TagKey<Block> incorrectDrops, TagKey<Item> repairItems) {
        this.name = name;
        this.harvestLevel = harvestLevel;
        this.durability = durability;
        this.damage = damage;
        this.speed = speed;
        this.enchantability = enchantability;
        this.incorrectDrops = incorrectDrops;
        this.repairItems = repairItems;
    }

    public String getName() {
        return name;
    }

    public int getUses() {
        return durability;
    }

    public float getSpeed() {
        return speed;
    }

    public float getAttackDamageBonus() {
        return damage;
    }

    public TagKey<Block> getIncorrectBlocksForDrops() {
        return incorrectDrops;
    }

    public int getLevel() {
        return harvestLevel;
    }

    public int getEnchantmentValue() {
        return enchantability;
    }

    public TagKey<Item> getRepairItems() {
        return repairItems;
    }

    public void setRepairItems(TagKey<Item> repairItems) {
        this.repairItems = repairItems;
        this.material = null;
    }

    public ToolMaterial toolMaterial() {
        if (material == null) {
            material = new ToolMaterial(incorrectDrops, durability, speed, damage, enchantability, repairItems);
        }
        return material;
    }
}
