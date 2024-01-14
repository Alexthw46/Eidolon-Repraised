package elucent.eidolon.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import elucent.eidolon.common.entity.ZombieBruteEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class ZombieBruteModel extends HumanoidModel<ZombieBruteEntity> {

	public ZombieBruteModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0);
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition chest = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(20, 16).addBox(-6.0F, 0.0F, -3.0F, 12.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));


		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 37).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, -3.5F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(56, 16).mirror().addBox(-2.5F, 0.0F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(48, 4).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, -2.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(48, 4).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(56, 16).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, -2.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 15.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 10.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.5F, 0.0F, -2.5F, 5.0F, 15.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(3.0F, 10.0F, 0.0F));

		PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(48, 40).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -16.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 96, 64);
	}

	private float quadraticArmUpdate(float pLimbSwing) {
		return -65.0F * pLimbSwing + pLimbSwing * pLimbSwing;
	}

	@Override
	public void setupAnim(@NotNull ZombieBruteEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

		boolean flag = pEntity.getFallFlyingTicks() > 4;
		boolean flag1 = pEntity.isVisuallySwimming();
		this.head.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
		if (flag) {
			this.head.xRot = -(float) Math.PI / 4F;
		} else if (this.swimAmount > 0.0F) {
			if (flag1) {
				this.head.xRot = this.rotlerpRad(this.swimAmount, this.head.xRot, -(float) Math.PI / 4F);
			} else {
				this.head.xRot = this.rotlerpRad(this.swimAmount, this.head.xRot, pHeadPitch * ((float) Math.PI / 180F));
			}
		} else {
			this.head.xRot = pHeadPitch * ((float) Math.PI / 180F);
		}

		this.body.yRot = 0.0F;
		this.rightArm.z = 0.0F;
		this.rightArm.x = -9.0F;
		this.leftArm.z = 0.0F;
		this.leftArm.x = 9.0F;
		float f = 1.0F;
		if (flag) {
			f = (float) pEntity.getDeltaMovement().lengthSqr();
			f /= 0.2F;
			f *= f * f;
		}

		if (f < 1.0F) {
			f = 1.0F;
		}

		this.rightArm.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 2.0F * pLimbSwingAmount * 0.5F / f;
		this.leftArm.xRot = Mth.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F / f;
		this.rightArm.zRot = 0.0F;
		this.leftArm.zRot = 0.0F;
		this.rightLeg.xRot = Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount / f;
		this.leftLeg.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 1.4F * pLimbSwingAmount / f;
		this.rightLeg.yRot = 0.005F;
		this.leftLeg.yRot = -0.005F;
		this.rightLeg.zRot = 0.005F;
		this.leftLeg.zRot = -0.005F;
		if (this.riding) {
			this.rightArm.xRot += -(float) Math.PI / 5F;
			this.leftArm.xRot += -(float) Math.PI / 5F;
			this.rightLeg.xRot = -1.4137167F;
			this.rightLeg.yRot = (float) Math.PI / 10F;
			this.rightLeg.zRot = 0.07853982F;
			this.leftLeg.xRot = -1.4137167F;
			this.leftLeg.yRot = -(float) Math.PI / 10F;
			this.leftLeg.zRot = -0.07853982F;
		}

		this.rightArm.yRot = 0.0F;
		this.leftArm.yRot = 0.0F;

		this.setupAttackAnimation(pEntity, pAgeInTicks);
        /*
        if (this.crouching) {
            this.body.xRot = 0.5F;
            this.rightArm.xRot += 0.4F;
            this.leftArm.xRot += 0.4F;
            this.rightLeg.z = 4.0F;
            this.leftLeg.z = 4.0F;
            this.rightLeg.y = 12.2F;
            this.leftLeg.y = 12.2F;
            this.head.y = 4.2F;
            this.body.y = 3.2F;
            this.leftArm.y = 5.2F;
            this.rightArm.y = 5.2F;
        } else {
            this.body.xRot = 0.0F;
            this.rightLeg.z = 0.0F;
            this.leftLeg.z = 0.0F;
            this.rightLeg.y = 12.0F;
            this.leftLeg.y = 12.0F;
            this.head.y = 0.0F;
            this.body.y = 0.0F;
            this.leftArm.y = 2.0F;
            this.rightArm.y = 2.0F;
        }*/

		if (this.rightArmPose != HumanoidModel.ArmPose.SPYGLASS) {
			AnimationUtils.bobModelPart(this.rightArm, pAgeInTicks, 1.0F);
		}

		if (this.leftArmPose != HumanoidModel.ArmPose.SPYGLASS) {
			AnimationUtils.bobModelPart(this.leftArm, pAgeInTicks, -1.0F);
		}

		if (this.swimAmount > 0.0F) {
			float f5 = pLimbSwing % 26.0F;
			HumanoidArm humanoidarm = pEntity.getMainArm();
			float f1 = humanoidarm == HumanoidArm.RIGHT && this.attackTime > 0.0F ? 0.0F : this.swimAmount;
			float f2 = humanoidarm == HumanoidArm.LEFT && this.attackTime > 0.0F ? 0.0F : this.swimAmount;
			if (!pEntity.isUsingItem()) {
				if (f5 < 14.0F) {
					this.leftArm.xRot = this.rotlerpRad(f2, this.leftArm.xRot, 0.0F);
					this.rightArm.xRot = Mth.lerp(f1, this.rightArm.xRot, 0.0F);
					this.leftArm.yRot = this.rotlerpRad(f2, this.leftArm.yRot, (float) Math.PI);
					this.rightArm.yRot = Mth.lerp(f1, this.rightArm.yRot, (float) Math.PI);
					this.leftArm.zRot = this.rotlerpRad(f2, this.leftArm.zRot, (float) Math.PI + 1.8707964F * this.quadraticArmUpdate(f5) / this.quadraticArmUpdate(14.0F));
					this.rightArm.zRot = Mth.lerp(f1, this.rightArm.zRot, (float) Math.PI - 1.8707964F * this.quadraticArmUpdate(f5) / this.quadraticArmUpdate(14.0F));
				} else if (f5 >= 14.0F && f5 < 22.0F) {
					float f6 = (f5 - 14.0F) / 8.0F;
					this.leftArm.xRot = this.rotlerpRad(f2, this.leftArm.xRot, (float) Math.PI / 2F * f6);
					this.rightArm.xRot = Mth.lerp(f1, this.rightArm.xRot, (float) Math.PI / 2F * f6);
					this.leftArm.yRot = this.rotlerpRad(f2, this.leftArm.yRot, (float) Math.PI);
					this.rightArm.yRot = Mth.lerp(f1, this.rightArm.yRot, (float) Math.PI);
					this.leftArm.zRot = this.rotlerpRad(f2, this.leftArm.zRot, 5.012389F - 1.8707964F * f6);
					this.rightArm.zRot = Mth.lerp(f1, this.rightArm.zRot, 1.2707963F + 1.8707964F * f6);
				} else if (f5 >= 22.0F && f5 < 26.0F) {
					float f3 = (f5 - 22.0F) / 4.0F;
					this.leftArm.xRot = this.rotlerpRad(f2, this.leftArm.xRot, (float) Math.PI / 2F - (float) Math.PI / 2F * f3);
					this.rightArm.xRot = Mth.lerp(f1, this.rightArm.xRot, (float) Math.PI / 2F - (float) Math.PI / 2F * f3);
					this.leftArm.yRot = this.rotlerpRad(f2, this.leftArm.yRot, (float) Math.PI);
					this.rightArm.yRot = Mth.lerp(f1, this.rightArm.yRot, (float) Math.PI);
					this.leftArm.zRot = this.rotlerpRad(f2, this.leftArm.zRot, (float) Math.PI);
					this.rightArm.zRot = Mth.lerp(f1, this.rightArm.zRot, (float) Math.PI);
				}
			}

			float f7 = 0.3F;
			float f4 = 0.33333334F;
			this.leftLeg.xRot = Mth.lerp(this.swimAmount, this.leftLeg.xRot, f7 * Mth.cos(pLimbSwing * f4 + (float) Math.PI));
			this.rightLeg.xRot = Mth.lerp(this.swimAmount, this.rightLeg.xRot, f7 * Mth.cos(pLimbSwing * f4));
		}

		this.hat.copyFrom(this.head);

		ItemStack itemstack = pEntity.getMainHandItem();
		if (pEntity.isAggressive() && (itemstack.isEmpty() || !itemstack.is(Items.BOW))) {
			AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, pEntity.isAggressive(), this.attackTime, pAgeInTicks);
		}
	}


	public void translateToHand(@NotNull HumanoidArm pSide, @NotNull PoseStack pPoseStack) {
		float f = pSide == HumanoidArm.RIGHT ? 1.0F : -1.0F;
		float y = 2.0F;
		ModelPart modelpart = this.getArm(pSide);
		modelpart.x += f;
		modelpart.y += y;
		modelpart.translateAndRotate(pPoseStack);
		modelpart.x -= f;
		modelpart.y -= y;
	}
	
}