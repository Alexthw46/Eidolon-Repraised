package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.common.deity.DeityLocks;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

public class SmiteSpell extends StaticSpell {
    public SmiteSpell(ResourceLocation name, Sign... signs) {
        super(name, 40, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        var ray = rayTrace(player, player.getBlockReach(), 0, true);

        if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            return livingEntity.getMobType() == MobType.UNDEAD;
        }

        return false;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

        var ray = rayTrace(player, player.getBlockReach(), 0, true);

        if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity.getMobType() == MobType.UNDEAD) {
                if (world instanceof ServerLevel) {
                    if (livingEntity.hurt(livingEntity.damageSources().magic(), DAMAGE == null ? 10.0f : DAMAGE.get())) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
                        KnowledgeUtil.grantResearchNoToast(player, DeityLocks.SMITE_UNDEAD);
                    }
                } else {
                    //TODO add cool client effects
                }
            }
        }

    }

    public @Nullable ForgeConfigSpec.ConfigValue<Float> DAMAGE;

    @Override
    public void buildConfig(ForgeConfigSpec.Builder spellBuilder) {
        super.buildConfig(spellBuilder);
        DAMAGE = spellBuilder.comment("The amount of damage dealt by the spell").define("damage", 10.0F);
    }
}
