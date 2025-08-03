package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.api.spells.Sign;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Optional;

import static alexthw.eidolon_repraised.Eidolon.MODID;

public class AdvancementTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, MODID);

    static final HashMap<ResourceLocation, CriterionTrigger<?>> triggers = new HashMap<>();

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> WICKED = register("wicked_path"), SACRED = register("sacred_path"), SACRIFICE = register("sacrifice"), INCENSE = register("incense"), VSACRIFICE = register("villager_sacrifice"), LAY_ON_HANDS = register("lay_on_hands"), ZOMBIFY = register("zombify"), CURE_ZOMBIE = register("cure_zombie"), ENTHRALL = register("enthrall_undead"), SMITE = register("smite_undead");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> FLAME = register("flame_spell"), FROST = register("frost_spell");

    public static Criterion<?> createCriterion(DeferredHolder<CriterionTrigger<?>, PlayerTrigger> holder) {
        return holder.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }

    public static void rewardNearbyPlayers(PlayerTrigger criteria, ServerLevel level, BlockPos pos, int radius) {
        AABB aabb = new AABB(pos).inflate(radius);
        for (ServerPlayer player : level.players()) {
            if (aabb.contains(player.getX(), player.getY(), player.getZ())) {
                criteria.trigger(player);
            }
        }
    }

    public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, PlayerTrigger> register(String pName) {
        return register(pName, new PlayerTrigger());
    }

    public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, T> register(String pName, T pTrigger) {
        return TRIGGERS.register(pName, () -> pTrigger);
    }

    public static <T extends CriterionTrigger<?>> T register(T trigger, ResourceLocation id) {
        T cTrigger = CriteriaTriggers.register(id.toString(),trigger);
        triggers.put(id, cTrigger);
        return cTrigger;
    }


    public static void triggerSign(Sign sign, ServerPlayer player) {
        switch (sign.getRegistryName().toString()) {
            case "eidolon_repraised:wicked" -> WICKED.get().trigger(player);
            case "eidolon_repraised:sacred" -> SACRED.get().trigger(player);
        }
    }

    public static void triggerResearch(ResourceLocation research, ServerPlayer player) {
        var trigger = triggers.get(research);
        if (trigger instanceof PlayerTrigger playerTrigger) {
            playerTrigger.trigger(player);
        } else switch (research.toString()) {
            case "eidolon_repraised:frost" -> FROST.get().trigger(player);
            case "eidolon_repraised:flames" -> FLAME.get().trigger(player);
            case "eidolon_repraised:sacrifice_mob" -> SACRIFICE.get().trigger(player);
            case "eidolon_repraised:basic_incense" -> INCENSE.get().trigger(player);
            case "eidolon_repraised:sacrifice_villager" -> VSACRIFICE.get().trigger(player);
            case "eidolon_repraised:heal_villager" -> LAY_ON_HANDS.get().trigger(player);
            case "eidolon_repraised:zombify_villager" -> ZOMBIFY.get().trigger(player);
            case "eidolon_repraised:cure_zombie" -> CURE_ZOMBIE.get().trigger(player);
            case "eidolon_repraised:enthrall_undead" -> ENTHRALL.get().trigger(player);
            case "eidolon_repraised:smite_undead" -> SMITE.get().trigger(player);
        }
    }

}
