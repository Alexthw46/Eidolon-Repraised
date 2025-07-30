package elucent.eidolon.registries;

import elucent.eidolon.api.spells.Sign;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;

import static elucent.eidolon.Eidolon.prefix;

public class AdvancementTriggers {

    static final HashMap<ResourceLocation, CriterionTrigger<?>> triggers = new HashMap<>();

    public static void init() {
        WICKED = register(new PlayerTrigger(), prefix("wicked_path"));
        SACRED = register(new PlayerTrigger(), prefix("sacred_path"));
        SACRIFICE = register(new PlayerTrigger(), prefix("sacrifice"));
        INCENSE = register(new PlayerTrigger(), prefix("incense"));
        VSACRIFICE = register(new PlayerTrigger(), prefix("villager_sacrifice"));
        LAY_ON_HANDS = register(new PlayerTrigger(), prefix("lay_on_hands"));
        ZOMBIFY = register(new PlayerTrigger(), prefix("zombify"));
        CURE_ZOMBIE = register(new PlayerTrigger(), prefix("cure_zombie"));

        ENTHRALL = register(new PlayerTrigger(), prefix("enthrall_undead"));
        SMITE = register(new PlayerTrigger(), prefix("smite_undead"));

        FLAME = register(new PlayerTrigger(), prefix("flame_spell"));
        FROST = register(new PlayerTrigger(), prefix("frost_spell"));

    }

    public static PlayerTrigger WICKED, SACRED, SACRIFICE, INCENSE, VSACRIFICE, LAY_ON_HANDS, ZOMBIFY, CURE_ZOMBIE, ENTHRALL, SMITE;
    public static PlayerTrigger FLAME, FROST;

    public static void rewardNearbyPlayers(PlayerTrigger criteria, ServerLevel level, BlockPos pos, int radius) {
        AABB aabb = new AABB(pos).inflate(radius);
        for (ServerPlayer player : level.players()) {
            if (aabb.contains(player.getX(), player.getY(), player.getZ())) {
                criteria.trigger(player);
            }
        }
    }

    public static <T extends CriterionTrigger<?>> T register(T trigger, ResourceLocation id) {
        T cTrigger = CriteriaTriggers.register(id.toString(),trigger);
        triggers.put(id, cTrigger);
        return cTrigger;
    }


    public static void triggerSign(Sign sign, ServerPlayer player) {
        switch (sign.getRegistryName().toString()) {
            case "eidolon:wicked" -> WICKED.trigger(player);
            case "eidolon:sacred" -> SACRED.trigger(player);
        }
    }

    public static void triggerResearch(ResourceLocation research, ServerPlayer player) {
        var trigger = triggers.get(research);
        if (trigger instanceof PlayerTrigger playerTrigger) {
            playerTrigger.trigger(player);
        } else switch (research.toString()) {
            case "eidolon:frost" -> FROST.trigger(player);
            case "eidolon:flames" -> FLAME.trigger(player);
            case "eidolon:sacrifice_mob" -> SACRIFICE.trigger(player);
            case "eidolon:basic_incense" -> INCENSE.trigger(player);
            case "eidolon:sacrifice_villager" -> VSACRIFICE.trigger(player);
            case "eidolon:heal_villager" -> LAY_ON_HANDS.trigger(player);
            case "eidolon:zombify_villager" -> ZOMBIFY.trigger(player);
            case "eidolon:cure_zombie" -> CURE_ZOMBIE.trigger(player);
            case "eidolon:enthrall_undead" -> ENTHRALL.trigger(player);
            case "eidolon:smite_undead" -> SMITE.trigger(player);
        }
    }

}
