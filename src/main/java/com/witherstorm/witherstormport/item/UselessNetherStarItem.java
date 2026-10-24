package com.witherstorm.witherstormport.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Nether Star modificada: dropada pela Wither Storm e propositalmente inutil. */
public class UselessNetherStarItem extends Item {
    public UselessNetherStarItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.witherstormport.useless")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
