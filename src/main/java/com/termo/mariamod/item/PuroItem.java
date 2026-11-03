package com.termo.mariamod.item;

import com.termo.mariamod.config.BalanceConfig;
import com.termo.mariamod.genetics.BadTrip;
import com.termo.mariamod.genetics.Genetics;
import com.termo.mariamod.genetics.GeneticsTooltip;
import com.termo.mariamod.genetics.NaturalEffects;
import com.termo.mariamod.genetics.PlantVariant;
import com.termo.mariamod.genetics.Species;
import com.termo.mariamod.genetics.StackVariants;
import com.termo.mariamod.registry.ModDataComponents;
import com.termo.mariamod.registry.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

// Puro: se fuma con clic derecho sostenido (animación de arco). Necesita un encendedor (tag mariamod:lighters)
// en cualquier lugar del inventario, que se gasta con cada puro. Aplica los efectos de su genética y tira el bad trip.
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
        ItemStack stack = player.getItemInHand(hand);
        if (findLighter(player).isEmpty()) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.mariamod.need_lighter"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            level.playSound(null, player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,
                    1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        }
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!(user instanceof Player player)) {
            return stack;
        }
        Optional<ItemStack> lighter = findLighter(player);
        if (lighter.isEmpty()) {
            return stack;
        }
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            smoke(stack, level, user);
            useLighter(lighter.get(), serverPlayer);
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return stack;
    }

    // Busca un encendedor en cualquier casilla del inventario.
    private static Optional<ItemStack> findLighter(Player player) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack candidate = inventory.getItem(slot);
            if (candidate.is(ModTags.LIGHTERS)) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    // hurtAndBreak respeta Irrompibilidad, no gasta en creativo y rompe el ítem al llegar a 0.
    private static void useLighter(ItemStack lighter, ServerPlayer player) {
        lighter.hurtAndBreak(BalanceConfig.LIGHTER_DAMAGE.get(), player.serverLevel(), player,
                item -> player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK,
                        SoundSource.PLAYERS, 0.8F, 0.8F + player.getRandom().nextFloat() * 0.4F));
    }

    private static void smoke(ItemStack stack, Level level, LivingEntity user) {
        Genetics genetics = stack.get(ModDataComponents.GENETICS.get());
        if (genetics == null) {
            return;
        }
        Species species = speciesOf(stack);

        int amplifier = genetics.potency() - 1;
        int ticks = BalanceConfig.durationSeconds(genetics.durationLevel()) * 20;
        for (ResourceLocation id : NaturalEffects.allFor(species, genetics)) {
            Optional<Holder.Reference<MobEffect>> holder = BuiltInRegistries.MOB_EFFECT.getHolder(id);
            if (holder.isEmpty()) {
                continue;
            }
            MobEffect effect = holder.get().value();
            if (effect.isInstantenous()) {
                effect.applyInstantenousEffect(user, user, user, amplifier, 1.0);
            } else {
                user.addEffect(new MobEffectInstance(holder.get(), ticks, amplifier));
            }
        }

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