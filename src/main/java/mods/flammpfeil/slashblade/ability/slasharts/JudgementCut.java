package mods.flammpfeil.slashblade.ability.slasharts;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityJudgementCut;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.RayTraceHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

public class JudgementCut {
    static public void doJudgementCut(LivingEntity user){

        World worldIn = user.world;

        worldIn.playSound((PlayerEntity)null, user.posX, user.posY, user.posZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.5F, 0.8F / (user.getRNG().nextFloat() * 0.4F + 0.8F));

        Vec3d eyePos = user.getEyePosition(1.0f);
        final double airReach = 5;
        final double entityReach = 7;

        ItemStack stack = user.getHeldItemMainhand();
        Optional<Vec3d> resultPos = stack.getCapability(ItemSlashBlade.BLADESTATE)
                .filter(s->s.getTargetEntity(worldIn) != null)
                .map(s->Optional.of(s.getTargetEntity(worldIn).getEyePosition(0)))
                .orElseGet(()->Optional.empty());


        if(!resultPos.isPresent()) {
            Optional<RayTraceResult> raytraceresult = RayTraceHelper.rayTrace(
                    worldIn, user, eyePos, user.getLookVec(), airReach, entityReach,
                    (entity) -> {
                        return !entity.isSpectator() && entity.isAlive() && entity.canBeCollidedWith() && (entity != user);
                    });

            resultPos = raytraceresult.map((rtr) -> {
                Vec3d pos = null;
                RayTraceResult.Type type = rtr.getType();
                switch (type) {
                    case ENTITY:
                        Entity target = ((EntityRayTraceResult) rtr).getEntity();
                        pos = target.getPositionVec().add(0, target.getEyeHeight() / 2.0f, 0);
                        break;
                    case BLOCK:
                        Vec3d hitVec = rtr.getHitVec();
                        pos = hitVec;
                        break;
                }
                return pos;
            });
        }

        Vec3d pos = resultPos.orElseGet(() -> eyePos.add(user.getLookVec().scale(airReach)));

        EntityJudgementCut jc = new EntityJudgementCut(SlashBlade.RegistryEvents.JudgementCut, worldIn);
        jc.setPosition(pos.x ,pos.y ,pos.z);
        jc.setShooter(user);
        worldIn.addEntity(jc);
    }
}