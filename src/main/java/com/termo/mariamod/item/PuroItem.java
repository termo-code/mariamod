package com.termo.mariamod.item;

import com.termo.mariamod.config.BalanceConfig;
import com.termo.mariamod.genetics.*;
import com.termo.mariamod.registry.ModDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

// Puro: se fuma con clic derecho sostenido (animación de arco), aplica los efectos de su genética
// y tira el riesgo de bad trip. No necesita encendedor.
public class PuroItem extends Item {
    public PuroItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return BalanceConfig.SMOKE_USE_TICKS.get();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!level.isClientSide) {
            smoke(stack, level, user);
        }
        if (!(user instanceof Player player && player.getAbilities().instabuild)) {
            stack.shrink(1);
        }
        return stack;
    }

    private static void smoke(ItemStack stack, Level level, LivingEntity user) {
        Genetics genetics = stack.get(ModDataComponents.GENETICS.get());
        if (genetics == null) {
            return;
        }
        Species species = speciesOf(stack);

        // Efectos naturales de la especie más los del genoma: la potencia es el nivel y el nivel de duración da los segundos.
        int amplifier = genetics.potency() - 1;
        int ticks = BalanceConfig.durationSeconds(genetics.durationLevel()) * 20;
        for (ResourceLocation id : NaturalEffects.allFor(species, genetics)) {
            Optional<Holder.Reference<MobEffect>> holder = BuiltInRegistries.MOB_EFFECT.getHolder(id);
            if (holder.isEmpty()) {
                continue;
            }
            MobEffect effect = holder.get().value();
            if (effect.isInstantenous()) {
                // Los efectos instantáneos se aplican de golpe: solo cuenta la potencia.
                effect.applyInstantenousEffect(user, user, user, amplifier, 1.0);
            } else {
                user.addEffect(new MobEffectInstance(holder.get(), ticks, amplifier));
            }
        }

        // Bad trip: la probabilidad es el riesgo de la genética.
        if (level.random.nextFloat() < genetics.badTripRisk()) {
            BadTrip.apply(user, species);
        }
    }

    private static Species speciesOf(ItemStack stack) {
        return StackVariants.of(stack).map(PlantVariant::species).orElse(Species.SATIVA_WILD);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        GeneticsTooltip.append(stack, tooltip);
    }
}