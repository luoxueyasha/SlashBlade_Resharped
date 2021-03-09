package mods.flammpfeil.slashblade.capability.slashblade.combo;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.inputstate.IInputState;
import mods.flammpfeil.slashblade.capability.slashblade.ComboState;
import mods.flammpfeil.slashblade.event.FallHandler;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Extra {

    @CapabilityInject(IInputState.class)
    public static Capability<IInputState> INPUT_STATE = null;

    public static final ResourceLocation exMotionLoc = new ResourceLocation(SlashBlade.modid, "combostate/motion_ex.vmd");

    static List<Map.Entry<EnumSet<InputCommand>, Supplier<ComboState>>> ex_standbyMap =
            new HashMap<EnumSet<InputCommand>, Supplier<ComboState>>(){{
                this.put(EnumSet.of(InputCommand.ON_GROUND, InputCommand.SNEAK, InputCommand.FORWARD, InputCommand.R_CLICK),
                        () -> EX_RAPID_SLASH);
                this.put(EnumSet.of(InputCommand.ON_GROUND, InputCommand.L_CLICK),
                        () -> EX_COMBO_A1);
                this.put(EnumSet.of(InputCommand.ON_GROUND, InputCommand.BACK, InputCommand.SNEAK, InputCommand.R_CLICK),
                        () -> EX_UPPERSLASH);

                this.put(EnumSet.of(InputCommand.ON_GROUND, InputCommand.R_CLICK),
                        () -> EX_COMBO_A1);

                this.put(EnumSet.of(InputCommand.ON_AIR, InputCommand.SNEAK, InputCommand.BACK, InputCommand.R_CLICK),
                        () -> EX_AERIAL_CLEAVE);
                this.put(EnumSet.of(InputCommand.ON_AIR),
                        () -> EX_AERIAL_RAVE_A1);
            }}.entrySet().stream()
                    .collect(Collectors.toList());
    
    //=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=-+-=

    static final Consumer<LivingEntity> QuickSheathSoundAction = (e)->
            e.world.playSound((PlayerEntity) null,e.getPosX(), e.getPosY(), e.getPosZ(),
                    SoundEvents.BLOCK_CHAIN_HIT,
                    SoundCategory.PLAYERS,1.0F,1.0F);

    public static final ComboState STANDBY_EX = new ComboState("standby_ex", 10,
            ()->0,()->1,()->1.0f,()->true,()->1000,
            exMotionLoc, (a)-> {

                    EnumSet<InputCommand> commands =
                            a.getCapability(ComboState.INPUT_STATE).map((state)->state.getCommands(a)).orElseGet(()-> EnumSet.noneOf(InputCommand.class));

                    return ex_standbyMap.stream()
                            .filter((entry)->commands.containsAll(entry.getKey()))
                            //.findFirst()
                            .min(Comparator.comparingInt((entry)-> entry.getValue().get().getPriority()))
                            .map((entry)->entry.getValue().get())
                            .orElseGet(()->ComboState.NONE);

                }, ()-> ComboState.NONE)
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_COMBO_A1 = new ComboState("ex_combo_a1",100,
            ()->1,()->10,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(5, (a)->Extra.EX_COMBO_A2), ()-> Extra.EX_COMBO_A1_END)
            .setClickAction((e)-> AttackManager.doSlash(e,  -10,true))
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_COMBO_A1_END = new ComboState("ex_combo_a1_end",100,
            ()->10,()->21,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->Extra.EX_COMBO_A2, ()-> Extra.EX_COMBO_A1_END2)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_A1_END2 = new ComboState("ex_combo_a1_end2",100,
            ()->21,()->41,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_COMBO_A2 = new ComboState("ex_combo_a2",100,
            ()->100,()->115,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(5,(a)->Extra.EX_COMBO_A3), ()-> Extra.EX_COMBO_A2_END)
            .setClickAction((e)-> AttackManager.doSlash(e,  180-10,true))
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_COMBO_A2_END = new ComboState("ex_combo_a2_end",100,
            ()->115,()->132,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->Extra.EX_COMBO_C, ()-> Extra.EX_COMBO_A2_END2)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_A2_END2 = new ComboState("ex_combo_a2_end2",100,
            ()->132,()->151,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_COMBO_C = new ComboState("ex_combo_c",100,
            ()->400,()->459,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(15,(a)->ComboState.NONE), ()-> Extra.EX_COMBO_C_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(2, (entityIn)->AttackManager.doSlash(entityIn,  -30))
                    .put(3, (entityIn)->AttackManager.doSlash(entityIn,  -35, true))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_COMBO_C_END = new ComboState("ex_combo_c_end",100,
            ()->459,()->488,()->1.0f,()->false,()->0,
            exMotionLoc,(a)-> ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_COMBO_A3 = new ComboState("ex_combo_a3",100,
            ()->200,()->218,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(9,(a)-> a.isPotionActive(Effects.STRENGTH) ? Extra.EX_COMBO_A4EX : Extra.EX_COMBO_A4) , ()-> Extra.EX_COMBO_A3_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(2, (entityIn)->AttackManager.doSlash(entityIn,  -61))
                    .put(6, (entityIn)->AttackManager.doSlash(entityIn,  180-42))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_A3_END = new ComboState("ex_combo_a3_end",100,
            ()->218,()->230,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->Extra.EX_COMBO_B1, ()-> Extra.EX_COMBO_A3_END2)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_A3_END2 = new ComboState("ex_combo_a3_end2",100,
            ()->230,()->281,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> Extra.EX_COMBO_A3_END3)
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_COMBO_A3_END3 = new ComboState("ex_combo_a3_end3",100,
            ()->281,()->314,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_COMBO_A4 = new ComboState("ex_combo_a4",100,
            ()->500,()->576,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(21,(a)->ComboState.NONE), ()-> Extra.EX_COMBO_A4_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(8, (entityIn)->AttackManager.doSlash(entityIn,  45))
                    .put(9, (entityIn)->AttackManager.doSlash(entityIn,  50, true))
                    .build())
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(8+0, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(8+1, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(8+2, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(8+3, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(8+4, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(8+5, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_COMBO_A4_END = new ComboState("ex_combo_a4_end",100,
            ()->576,()->608,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_COMBO_A4EX = new ComboState("ex_combo_a4ex",100,
            ()->800,()->839,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(22,(a)-> Extra.EX_COMBO_A5EX) , ()-> Extra.EX_COMBO_A4EX_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(7, (entityIn)->AttackManager.doSlash(entityIn,  70))
                    .put(14, (entityIn)->AttackManager.doSlash(entityIn,  180+75))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_A4EX_END = new ComboState("ex_combo_a4ex_end",100,
            ()->839,()->877,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> Extra.EX_COMBO_A4EX_END2)
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_COMBO_A4EX_END2 = new ComboState("ex_combo_a4ex_end2",100,
            ()->877,()->894,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);

    public static final ComboState EX_COMBO_A5EX = new ComboState("ex_combo_a5ex",100,
            ()->900,()->1013,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(33,(a)->ComboState.NONE), ()-> Extra.EX_COMBO_A5EX_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(15, (entityIn)->AttackManager.doSlash(entityIn,  35,false,true))
                    .put(17, (entityIn)->AttackManager.doSlash(entityIn,  40,true,true))
                    .put(19, (entityIn)->AttackManager.doSlash(entityIn,  30,true,true))
                    .build())
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(13+0, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(13+1, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(13+2, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(13+3, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(13+4, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(13+5, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_COMBO_A5EX_END = new ComboState("ex_combo_a5ex_end",100,
            ()->1013,()->1061,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);


    private final static float rushDamageBase = 0.1f;
    public static final ComboState EX_COMBO_B1 = new ComboState("ex_combo_b1",100,
            ()->700,()->720,()->1.0f,()->false,()->0,
            exMotionLoc,  ComboState.TimeoutNext.buildFromFrame(13, (a)-> Extra.EX_COMBO_B2) , ()-> Extra.EX_COMBO_B1_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(6, (entityIn)->{
                        AttackManager.doSlash(entityIn,  -30, false, false, 0.25f);
                        AttackManager.doSlash(entityIn,  180-35, true, false, 0.25f);
                    })
                    .put(7+0, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(7+1, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(7+2, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(7+3, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(7+4, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(7+5, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(7+6, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(7+7, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))

                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false);

    public static final ComboState EX_COMBO_B1_END = new ComboState("ex_combo_b1_end",100,
            ()->720,()->743,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->Extra.EX_COMBO_B1_END, ()-> Extra.EX_COMBO_B1_END2)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(12 -3, (entityIn)->AttackManager.doSlash(entityIn,  0, new Vector3d(entityIn.getRNG().nextFloat()-0.5f,0.8f,0), false, true,1.0))
                    .put(13 -3, (entityIn)->AttackManager.doSlash(entityIn,  5, new Vector3d(entityIn.getRNG().nextFloat()-0.5f,0.8f,0), true, false,1.0))
                    .build())
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(12-3 +0, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12-3 +1, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12-3 +2, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12-3 +3, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12-3 +4, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(12-3 +5, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false);

    public static final ComboState EX_COMBO_B1_END2 = new ComboState("ex_combo_b1_end2",100,
            ()->743,()->764,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> Extra.EX_COMBO_B1_END3)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_B1_END3 = new ComboState("ex_combo_b1_end3",100,
            ()->764,()->787,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);


    public static Vector3d genRushOffset(LivingEntity entityIn){
        return new Vector3d(entityIn.getRNG().nextFloat()-0.5f,entityIn.getRNG().nextFloat()-0.5f,0).scale(2.0);
    }
    
    public static final ComboState EX_COMBO_B2 = new ComboState("ex_combo_b2",100,
            ()->710,()->720,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(6, (a)-> Extra.EX_COMBO_B3)  , ()-> Extra.EX_COMBO_B_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(1, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(2, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(3, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(4, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(5, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(6, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_B3 = new ComboState("ex_combo_b3",100,
            ()->710,()->720,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(6, (a)-> Extra.EX_COMBO_B4)  , ()-> Extra.EX_COMBO_B_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(1, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(2, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(3, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(4, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(5, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(6, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_B4 = new ComboState("ex_combo_b4",100,
            ()->710,()->720,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(6, (a)-> Extra.EX_COMBO_B5)  , ()-> Extra.EX_COMBO_B_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(1, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(2, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(3, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(4, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(5, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(6, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_B5 = new ComboState("ex_combo_b5",100,
            ()->710,()->720,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(6, (a)-> Extra.EX_COMBO_B6)  , ()-> Extra.EX_COMBO_B_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(1, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(2, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(3, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(4, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(5, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(6, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_B6 = new ComboState("ex_combo_b6",100,
            ()->710,()->720,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(6, (a)-> Extra.EX_COMBO_B7)  , ()-> Extra.EX_COMBO_B_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(1, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(2, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(3, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(4, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(5, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(6, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_COMBO_B7 = new ComboState("ex_combo_b7",100,
            ()->710,()->764,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(33,(a)->ComboState.NONE), ()-> Extra.EX_COMBO_B7_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(1, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(2, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(3, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(4, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(5, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))
                    .put(6, (entityIn)->AttackManager.doSlash(entityIn,  -90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), false, false, rushDamageBase))
                    .put(7, (entityIn)->AttackManager.doSlash(entityIn,  +90 + 180 * entityIn.getRNG().nextFloat(), genRushOffset(entityIn), true , false, rushDamageBase))

                    .put(12, (entityIn)->AttackManager.doSlash(entityIn,  0, new Vector3d(entityIn.getRNG().nextFloat()-0.5f,0.8f,0), false, true,1.0))
                    .put(13, (entityIn)->AttackManager.doSlash(entityIn,  5, new Vector3d(entityIn.getRNG().nextFloat()-0.5f,0.8f,0), true, false,1.0))
                    .build())
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(12 +0, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12 +1, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12 +2, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12 +3, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12 +4, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(12 +5, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_COMBO_B7_END = new ComboState("ex_combo_b7_end",100,
            ()->764,()->787,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);

    public static final ComboState EX_COMBO_B_END = new ComboState("ex_combo_b_end",100,
            ()->720,()->743,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->Extra.EX_COMBO_B_END, ()-> Extra.EX_COMBO_B_END2)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(12 -3, (entityIn)->AttackManager.doSlash(entityIn,  0, new Vector3d(entityIn.getRNG().nextFloat()-0.5f,0.8f,0), false, true,1.0))
                    .put(13 -3, (entityIn)->AttackManager.doSlash(entityIn,  5, new Vector3d(entityIn.getRNG().nextFloat()-0.5f,0.8f,0), true, false,1.0))
                    .build())
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(12-3 +0, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12-3 +1, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12-3 +2, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12-3 +3, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(12-3 +4, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(12-3 +5, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .build())
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false);

    public static final ComboState EX_COMBO_B_END2 = new ComboState("ex_combo_b_end2",100,
            ()->743,()->764,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> Extra.EX_COMBO_B_END3)
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_COMBO_B_END3 = new ComboState("ex_combo_b_end3",100,
            ()->764,()->787,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);


    //-------------------------------------------------------


    public static final ComboState EX_AERIAL_RAVE_A1 = new ComboState("ex_aerial_rave_a1",80,
            ()->1100,()->1122,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(5, (a)->Extra.EX_AERIAL_RAVE_A2), ()-> Extra.EX_AERIAL_RAVE_A1_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put((int)TimeValueHelper.getTicksFromFrames(3)+0, (entityIn)->AttackManager.doSlash(entityIn,  -20))
                    .build()
                    .andThen(FallHandler::fallDecrease))
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->true)
            .setIsAerial();
    public static final ComboState EX_AERIAL_RAVE_A1_END = new ComboState("ex_aerial_rave_a1_end",80,
            ()->1122,()->1132,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0,QuickSheathSoundAction).build())
            .addTickAction(FallHandler::fallDecrease)
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_AERIAL_RAVE_A2 = new ComboState("ex_aerial_rave_a2",80,
            ()->1200,()->1210,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(5,(a)->Extra.EX_AERIAL_RAVE_A3), ()-> Extra.EX_AERIAL_RAVE_A2_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put((int)TimeValueHelper.getTicksFromFrames(3)+0, (entityIn)->AttackManager.doSlash(entityIn,  180-30))
                    .build())
            .addTickAction(FallHandler::fallDecrease)
            .addHitEffect(StunManager::setStun)
            .setQuickChargeEnabled(()->false)
            .setIsAerial();
    public static final ComboState EX_AERIAL_RAVE_A2_END = new ComboState("ex_aerial_rave_a2_end",80,
            ()->1210,()->1231,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->Extra.EX_AERIAL_RAVE_B3, ()-> Extra.EX_AERIAL_RAVE_A2_END2)
            .addTickAction(FallHandler::fallDecrease)
            .setQuickChargeEnabled(()->true)
            .setIsAerial();
    public static final ComboState EX_AERIAL_RAVE_A2_END2 = new ComboState("ex_aerial_rave_a2_end2",80,
            ()->1231,()->1241,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0,QuickSheathSoundAction).build())
            .addTickAction(FallHandler::fallDecrease)
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_AERIAL_RAVE_A3 = new ComboState("ex_aerial_rave_a3",80,
            ()->1300,()->1328,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(9,(a)->ComboState.NONE), ()-> Extra.EX_AERIAL_RAVE_A3_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put((int)TimeValueHelper.getTicksFromFrames(4)+0, (entityIn)->AttackManager.doSlash(entityIn,  0,Vector3d.ZERO, false, false, 1.0, KnockBacks.smash))
                    .put((int)TimeValueHelper.getTicksFromFrames(4)+1, (entityIn)->AttackManager.doSlash(entityIn,  -3,Vector3d.ZERO, true, true, 1.0, KnockBacks.smash))
                    .build())
            .addTickAction(FallHandler::fallDecrease)
            .addHitEffect(StunManager::setStun)
            .setIsAerial()
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_AERIAL_RAVE_A3_END = new ComboState("ex_aerial_rave_a3_end",80,
            ()->1328,()->1338,()->1.0f,()->false,()->0,
            exMotionLoc,(a)-> ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0,QuickSheathSoundAction).build())
            .addTickAction(FallHandler::fallDecrease)
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_AERIAL_RAVE_B3 = new ComboState("ex_aerial_rave_b3",80,
            ()->1400,()->1437,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(13,(a)->Extra.EX_AERIAL_RAVE_B4) , ()-> Extra.EX_AERIAL_RAVE_B3_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)->{
                        Vector3d motion = entityIn.getMotion();
                        entityIn.setMotion(motion.x, 0.6, motion.z);})
                    .put((int)TimeValueHelper.getTicksFromFrames(5), (entityIn)->AttackManager.doSlash(entityIn,  180+57,Vector3d.ZERO, false, false, 1.0, KnockBacks.toss))
                    .put((int)TimeValueHelper.getTicksFromFrames(10), (entityIn)->AttackManager.doSlash(entityIn,  180+57,Vector3d.ZERO, false, false, 1.0, KnockBacks.toss))
                    .build())
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)->UserPoseOverrider.setRot(entityIn, -90, true))
                    .put(1, (entityIn)->UserPoseOverrider.setRot(entityIn, -90, true))
                    .put(2, (entityIn)->UserPoseOverrider.setRot(entityIn, -90, true))
                    .put(3, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(4, (entityIn)->UserPoseOverrider.setRot(entityIn, -120, true))
                    .put(5, (entityIn)->UserPoseOverrider.setRot(entityIn, -120, true))
                    .put(6, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(7, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .build())
            .addTickAction(FallHandler::fallDecrease)
            .addHitEffect(StunManager::setStun)
            .setIsAerial()
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_AERIAL_RAVE_B3_END = new ComboState("ex_aerial_rave_b3_end",80,
            ()->1437,()->1443,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0,QuickSheathSoundAction).build())
            .addTickAction(FallHandler::fallDecrease)
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_AERIAL_RAVE_B4 = new ComboState("ex_aerial_rave_b4",80,
            ()->1500,()->1537,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(15,(a)->ComboState.NONE), ()-> Extra.EX_AERIAL_RAVE_B4_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put((int)TimeValueHelper.getTicksFromFrames(10)+0, (entityIn)->AttackManager.doSlash(entityIn,  45,Vector3d.ZERO, false, false, 1.0, KnockBacks.meteor))
                    .put((int)TimeValueHelper.getTicksFromFrames(10)+1, (entityIn)->AttackManager.doSlash(entityIn,  50,Vector3d.ZERO, true, true, 1.0, KnockBacks.meteor))
                    .build())
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(5+0, (entityIn)->UserPoseOverrider.setRot(entityIn, 90, true))
                    .put(5+1, (entityIn)->UserPoseOverrider.setRot(entityIn, 90, true))
                    .put(5+2, (entityIn)->UserPoseOverrider.setRot(entityIn, 90, true))
                    .put(5+3, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(5+4, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .build())
            .addTickAction(FallHandler::fallDecrease)
            .addHitEffect(StunManager::setStun)
            .setIsAerial()
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_AERIAL_RAVE_B4_END = new ComboState("ex_aerial_rave_b4_end",80,
            ()->1537,()->1547,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0,QuickSheathSoundAction).build())
            .addTickAction(FallHandler::fallDecrease)
            .setQuickChargeEnabled(()->false);


    //-------------------------------------------------------

    private static final EnumSet<InputCommand> ex_upperslash_command = EnumSet.of(InputCommand.BACK, InputCommand.R_DOWN);
    public static final ComboState EX_UPPERSLASH = new ComboState("ex_upperslash",90,
            ()->1600, ()->1659, ()->1.0f, ()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(11,(a)->ComboState.NONE), ()-> Extra.EX_UPPERSLASH_END)
            .addHoldAction((player) -> {
                int elapsed = player.getItemInUseMaxCount();

                int fireTime = (int)TimeValueHelper.getTicksFromFrames(9);
                if(fireTime != elapsed) return;

                EnumSet<InputCommand> commands =
                        player.getCapability(INPUT_STATE).map((state)->state.getCommands(player)).orElseGet(()-> EnumSet.noneOf(InputCommand.class));

                if (!commands.containsAll(ex_upperslash_command)) return;

                player.getHeldItemMainhand().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state)->{
                    state.updateComboSeq(player, Extra.EX_UPPERSLASH_JUMP);
                });
            })
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put((int)TimeValueHelper.getTicksFromFrames(7), (entityIn)->AttackManager.doSlash(entityIn,  -80,Vector3d.ZERO, false, false, 1.0, KnockBacks.toss))
                    .build())
            .addHitEffect((t,a)->StunManager.setStun(t, 15))
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_UPPERSLASH_END = new ComboState("ex_upperslash_end",90,
            ()->1659, ()->1693, ()->1.0f, ()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_UPPERSLASH_JUMP = new ComboState("ex_upperslash_jump",90,
            ()->1700, ()->1713, ()->1.0f, ()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(5,(a)->ComboState.NONE), ()-> Extra.EX_UPPERSLASH_JUMP_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(1, (entityIn)->{
                        Vector3d motion = entityIn.getMotion();
                        entityIn.setMotion(motion.x, 0.6f, motion.z);})
                    .build())
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)->UserPoseOverrider.setRot(entityIn, 120, true))
                    .put(1, (entityIn)->UserPoseOverrider.setRot(entityIn, 120, true))
                    .put(2, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(3, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .build())
            .addTickAction(FallHandler::fallDecrease)
            .addHitEffect(StunManager::setStun)
            .setIsAerial()
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_UPPERSLASH_JUMP_END = new ComboState("ex_upperslash_jump_end",90,
            ()->1713, ()->1717, ()->1.0f, ()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0,QuickSheathSoundAction).build()
                    .andThen(FallHandler::fallDecrease))
            .setQuickChargeEnabled(()->false);

    //-------------------------------------------------------

    public static final ComboState EX_AERIAL_CLEAVE = new ComboState("ex_aerial_cleave",70,
            ()->1800, ()->1812, ()->1.0f, ()->false,()->0,
            exMotionLoc, (a)->Extra.EX_AERIAL_CLEAVE, ()-> Extra.EX_AERIAL_CLEAVE_LOOP)
            .setClickAction((e)->{
                Vector3d motion = e.getMotion();
                e.setMotion(motion.x, 0.1, motion.z);
            })
            .addTickAction((e)->{
                e.fallDistance = 1;

                long elapsed = e.getHeldItemMainhand().getCapability(ItemSlashBlade.BLADESTATE)
                        .map((state)->state.getElapsedTime(e))
                        .orElseGet(()->0l);

                if(elapsed == 2){
                    e.world.playSound((PlayerEntity)null, e.getPosX(), e.getPosY(), e.getPosZ(),
                            SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 0.75F, 1.0F);
                }

                if(2 < elapsed) {
                    Vector3d motion = e.getMotion();
                    e.setMotion(motion.x, motion.y - 3.0, motion.z);
                }

                if(elapsed % 2 == 0)
                    AttackManager.areaAttack(e, KnockBacks.meteor.action,0.1f,true,false,true);

                if(e.isOnGround()){
                    AttackManager.doSlash(e,  55,Vector3d.ZERO, true, true, 1.0, KnockBacks.meteor);
                    e.getHeldItemMainhand().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state)->{
                        state.updateComboSeq(e,Extra.EX_AERIAL_CLEAVE_LANDING);
                        FallHandler.spawnLandingParticle(e, 20);
                    });
                }
            })
            .setQuickChargeEnabled(()->false);
    //fall loop 1sec timeout
    public static final ComboState EX_AERIAL_CLEAVE_LOOP = new ComboState("ex_aerial_cleave_loop",70,
            ()->1812, ()->1817, ()->1.0f, ()->true,()->1000,
            exMotionLoc, (a)->Extra.EX_AERIAL_CLEAVE_LOOP, ()-> ComboState.NONE)
            .addTickAction((e)->{
                e.fallDistance = 1;
                
                Vector3d motion = e.getMotion();
                e.setMotion(motion.x, motion.y - 3.0, motion.z);
                
                long elapsed = e.getHeldItemMainhand().getCapability(ItemSlashBlade.BLADESTATE)
                        .map((state)->state.getElapsedTime(e))
                        .orElseGet(()->0l);

                if(elapsed % 2 == 0)
                    AttackManager.areaAttack(e, KnockBacks.meteor.action,0.1f,true,false,true);

                if(e.isOnGround()){
                    AttackManager.doSlash(e,  55, Vector3d.ZERO, true, true, 1.0, KnockBacks.meteor);
                    e.getHeldItemMainhand().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state)->{
                        state.updateComboSeq(e,Extra.EX_AERIAL_CLEAVE_LANDING);
                        FallHandler.spawnLandingParticle(e, 20);
                    });
                }
            })
            .addHitEffect((t,a)->StunManager.setStun(t, 15))
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_AERIAL_CLEAVE_LANDING = new ComboState("ex_aerial_cleave_landing",70,
            ()->1816, ()->1859, ()->1.0f, ()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(6,(a)->ComboState.NONE), ()-> Extra.EX_AERIAL_CLEAVE_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)->AttackManager.doSlash(entityIn,  60, Vector3d.ZERO, false, false, 1.0, KnockBacks.meteor))
                    .build())
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_AERIAL_CLEAVE_END = new ComboState("ex_aerial_cleave_end",70,
            ()->1859, ()->1886, ()->1.0f, ()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);

    
    //-------------------------------------------------------


    public static final ComboState EX_RAPID_SLASH = new ComboState("ex_rapid_slash",70,
            ()->2000, ()->2019, ()->1.0f, ()->false,()->0,
            exMotionLoc, (a)-> a.isPotionActive(Effects.STRENGTH) ? Extra.EX_RAPID_SLASH_QUICK : Extra.EX_RAPID_SLASH, ()-> Extra.EX_RAPID_SLASH_END)
            .addHoldAction((e)->{
                AttributeModifier am = new AttributeModifier("SweepingDamageRatio", -3, AttributeModifier.Operation.ADDITION);
                ModifiableAttributeInstance mai = e.getAttribute(ForgeMod.REACH_DISTANCE.get());
                mai.applyNonPersistentModifier(am);
                AttackManager.areaAttack(e, (t)->{
                        boolean isRightDown = e.getCapability(INPUT_STATE)
                                .map((state)->state.getCommands().contains(InputCommand.R_DOWN))
                                .orElse(false);

                        if(isRightDown) {
                            e.getHeldItemMainhand().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
                                if (state.getComboSeq() == Extra.EX_RAPID_SLASH) {
                                    Entity target = state.getTargetEntity(e.world);
                                    if (target == t) {
                                        state.updateComboSeq(e, Extra.EX_RISING_STAR);
                                        e.setMotion(0, 0, 0);
                                    }
                                }
                            });
                        }
                    }, 0.001f, true, false, true);
                mai.removeModifier(am);
            })
            .addTickAction((e)->{
                long elapsed = e.getHeldItemMainhand().getCapability(ItemSlashBlade.BLADESTATE)
                        .map((state)->state.getElapsedTime(e))
                        .orElseGet(()->0l);

                if(elapsed == 0){
                    e.world.playSound((PlayerEntity) null,e.getPosX(), e.getPosY(), e.getPosZ(),
                            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                            SoundCategory.PLAYERS,1.0F,1.0F);
                }

                if(elapsed <= 3 && e.isOnGround())
                    e.moveRelative( e.isInWater() ? 0.35f : 0.8f , new Vector3d(0, 0, 1));

                if(2 <= elapsed && elapsed < 6){
                    float roll = -45 + 90 * e.getRNG().nextFloat();

                    if(elapsed % 2 == 0)
                        roll += 180;

                    boolean critical = e.isPotionActive(Effects.STRENGTH);

                    AttackManager.doSlash(e,  roll, genRushOffset(e), false, critical, rushDamageBase);
                }

                if(elapsed == 7) {
                    AttackManager.doSlash(e, -30, genRushOffset(e), false, true, rushDamageBase);
                }

                if(7 <= elapsed && elapsed <= 10){
                    UserPoseOverrider.setRot(e, 90, true);
                }
                if(10 < elapsed){
                    UserPoseOverrider.setRot(e, 0, false);
                }
            })
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_RAPID_SLASH_QUICK = new ComboState("ex_rapid_slash_quick",70,
            ()->2000, ()->2001, ()->1.0f, ()->false,()->0,
            exMotionLoc, (a)->Extra.EX_RAPID_SLASH_QUICK, ()-> Extra.EX_RAPID_SLASH)
            .setQuickChargeEnabled(()->false);
    public static final ComboState EX_RAPID_SLASH_END = new ComboState("ex_rapid_slash_end",70,
            ()->2019, ()->2054, ()->1.0f, ()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> Extra.EX_RAPID_SLASH_END2)
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_RAPID_SLASH_END2 = new ComboState("ex_rapid_slash_end2",70,
            ()->2054, ()->2073, ()->1.0f, ()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0,QuickSheathSoundAction).build())
            .setQuickChargeEnabled(()->false);


    public static final ComboState EX_RISING_STAR = new ComboState("ex_rising_star",80,
            ()->2100,()->2137,()->1.0f,()->false,()->0,
            exMotionLoc, ComboState.TimeoutNext.buildFromFrame(18,(a)->ComboState.NONE) , ()-> Extra.EX_RISING_STAR_END)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put((int)TimeValueHelper.getTicksFromFrames(0), (entityIn)->AttackManager.doSlash(entityIn,  -57,Vector3d.ZERO, false, false, 1.0, KnockBacks.toss))
                    .put((int)TimeValueHelper.getTicksFromFrames(9), (entityIn)->AttackManager.doSlash(entityIn,  -57,Vector3d.ZERO, false, false, 1.0, KnockBacks.cancel))
                    .build())
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0+0, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(0+1, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(0+2, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(0+3, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(0+4, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(5+0, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(5+1, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(5+2, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(5+3, (entityIn)->UserPoseOverrider.setRot(entityIn, 75, true))
                    .put(5+4, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .put(5+5, (entityIn)->UserPoseOverrider.setRot(entityIn, 0, false))
                    .build())
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0, (entityIn)-> {
                        entityIn.setMotion(0, 0.6, 0);
                    }).build())
            .addTickAction((entityIn)->{
                        Vector3d motion = entityIn.getMotion();
                        entityIn.setMotion(0, motion.y, 0);
                    })
            .addTickAction(FallHandler::fallDecrease)
            .addHitEffect(StunManager::setStun)
            .setIsAerial()
            .setQuickChargeEnabled(()->true);
    public static final ComboState EX_RISING_STAR_END = new ComboState("ex_rising_star_end",80,
            ()->2137,()->2147,()->1.0f,()->false,()->0,
            exMotionLoc, (a)->ComboState.NONE, ()-> ComboState.NONE)
            .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                    .put(0,QuickSheathSoundAction).build())
            .addTickAction(FallHandler::fallDecrease)
            .setQuickChargeEnabled(()->false);
    /**
     *
     * VOID_SLASH
     *
     * JUDGEMENT_CUT
     *
     * 
     *
     */

    
}