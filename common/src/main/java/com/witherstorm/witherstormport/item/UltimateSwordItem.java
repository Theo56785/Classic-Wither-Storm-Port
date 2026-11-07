package com.witherstorm.witherstormport.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * minecraft_storymode:ultimate_sword (max_damage 100000, foil) + player.json: segurando-a o dano vira 100000.
 * O atributo de dano do jogador e limitado a 2048, entao o dano extra e aplicado em hurtEnemy.
 */
public class UltimateSwordItem extends SwordItem {
    public static final float ULTIMATE_DAMAGE = 100_000.0F;

    public UltimateSwordItem(Properties props) {
        super(UltimateTier.INSTANCE, 3, -2.4F, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide) {
            DamageSource src = attacker instanceof Player p
                    ? attacker.damageSources().playerAttack(p)
                    : attacker.damageSources().mobAttack(attacker);
            target.invulnerableTime = 0;
            target.hurt(src, ULTIMATE_DAMAGE);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    public enum UltimateTier implements Tier {
        INSTANCE;

        @Override public int getUses() { return 100_000; }
        @Override public float getSpeed() { return 8.0F; }
        @Override public float getAttackDamageBonus() { return 7.0F; }
        @Override public int getLevel() { return 4; }
        @Override public int getEnchantmentValue() { return 15; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(Items.NETHER_STAR); }
    }
}
