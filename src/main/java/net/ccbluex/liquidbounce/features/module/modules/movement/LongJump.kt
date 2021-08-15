/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * http://proxy.liulihaocai.pw/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.JumpEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.EnumAutoDisableType
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.util.EnumFacing

@ModuleInfo(name = "LongJump", category = ModuleCategory.MOVEMENT, autoDisable = EnumAutoDisableType.FLAG)
class LongJump : Module() {
    private val modeValue = ListValue("Mode", arrayOf("NCP", "AACv1", "AACv2", "AACv3", "Mineplex", "Mineplex2", "Mineplex3", "RedeskyTest", "Redesky", "Redesky2", "Redesky3", "BlocksMC", "BlocksMC2", "HYT4v4"), "NCP")
    private val ncpBoostValue = FloatValue("NCPBoost", 4.25f, 1f, 10f)

    // redesky
    private val rsJumpMovementValue = FloatValue("RedeskyJumpMovement",0.13F,0.05F,0.25F).displayable { modeValue.get().equals("Redesky",true) }
    private val rsMotionYValue = FloatValue("RedeskyMotionY",0.81F,0.05F,1F).displayable { modeValue.get().equals("Redesky",true) }
    private val rsMoveReducerValue = BoolValue("RedeskyMovementReducer", true).displayable { modeValue.get().equals("Redesky",true) }
    private val rsReduceMovementValue = FloatValue("RedeskyReduceMovement",0.08F,0.05F,0.25F).displayable { modeValue.get().equals("Redesky",true) }
    private val rsMotYReducerValue = BoolValue("RedeskyMotionYReducer", true).displayable { modeValue.get().equals("Redesky",true) }
    private val rsReduceYMotionValue = FloatValue("RedeskyReduceYMotion",0.15F,0.01F,0.20F).displayable { modeValue.get().equals("Redesky",true) }
    private val rsUseTimerValue = BoolValue("RedeskyTimer", true).displayable { modeValue.get().equals("Redesky",true) }
    private val rsTimerValue = FloatValue("RedeskyTimer",0.30F,0.1F,1F).displayable { modeValue.get().equals("Redesky",true) }

    // redesky2
    private val rs2AirSpeedValue = FloatValue("Redesky2AirSpeed",0.1F,0.05F,0.25F).displayable { modeValue.get().equals("Redesky2",true) }
    private val rs2MinAirSpeedValue = FloatValue("Redesky2MinAirSpeed",0.08F,0.05F,0.25F).displayable { modeValue.get().equals("Redesky2",true) }
    private val rs2ReduceAirSpeedValue = FloatValue("Redesky2ReduceAirSpeed",0.16F,0.05F,0.25F).displayable { modeValue.get().equals("Redesky2",true) }
    private val rs2AirSpeedReducerValue = BoolValue("Redesky2AirSpeedReducer", true).displayable { modeValue.get().equals("Redesky2",true) }
    private val rs2YMotionValue = FloatValue("Redesky2YMotion",0.08F,0.01F,0.20F).displayable { modeValue.get().equals("Redesky2",true) }
    private val rs2MinYMotionValue = FloatValue("Redesky2MinYMotion",0.04F,0.01F,0.20F).displayable { modeValue.get().equals("Redesky2",true) }
    private val rs2ReduceYMotionValue = FloatValue("Redesky2ReduceYMotion",0.15F,0.01F,0.20F).displayable { modeValue.get().equals("Redesky2",true) }
    private val rs2YMotionReducerValue = BoolValue("Redesky2YMotionReducer", true).displayable { modeValue.get().equals("Redesky2",true) }
    private val rs3JumpTimeValue= IntegerValue("Redesky3JumpTime",500,300,1500).displayable { modeValue.get().equals("Redesky3",true) }
    private val rs3BoostValue= FloatValue("Redesky3Boost",1F,0.3F,1.5F).displayable { modeValue.get().equals("Redesky3",true) }
    private val rs3HeightValue= FloatValue("Redesky3Height",1F,0.3F,1.5F).displayable { modeValue.get().equals("Redesky3",true) }
    private val rs3TimerValue = FloatValue("Redesky3Timer",1F,0.1F,5F).displayable { modeValue.get().equals("Redesky3",true) }

    // settings
    private val autoJumpValue = BoolValue("AutoJump", true)
    private val autoDisableValue = BoolValue("AutoDisable", true)
    private var jumped = false
    private var hasJumped=false
    private var canBoost = false
    private var teleported = false
    private var canMineplexBoost = false
    private var timer=MSTimer()
    var airTicks=0

    override fun onEnable() {
        airTicks=0
        hasJumped=false
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1F
        when(modeValue.get().toLowerCase()){
            "redesky2" -> {
                mc.thePlayer.speedInAir = 0.02F
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer ?: return

        if (jumped) {
            val mode = modeValue.get()

            if(!mc.thePlayer.onGround){
                airTicks++
            }else{
                airTicks=0
            }

            if (mc.thePlayer.onGround || mc.thePlayer.capabilities.isFlying) {
                jumped = false
                canMineplexBoost = false

                if (mode.equals("NCP", ignoreCase = true)) {
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                }
                return
            }
            run {
                when (mode.toLowerCase()) {
                    "ncp" -> {
                        MovementUtils.strafe(MovementUtils.getSpeed() * if (canBoost) ncpBoostValue.get() else 1f)
                        canBoost = false
                    }

                    "aacv1" -> {
                        mc.thePlayer.motionY += 0.05999
                        MovementUtils.strafe(MovementUtils.getSpeed() * 1.08f)
                    }

                    "aacv2", "mineplex3" -> {
                        mc.thePlayer.jumpMovementFactor = 0.09f
                        mc.thePlayer.motionY += 0.0132099999999999999999999999999
                        mc.thePlayer.jumpMovementFactor = 0.08f
                        MovementUtils.strafe()
                    }

                    "aacv3" -> {
                        if (mc.thePlayer.fallDistance > 0.5f && !teleported) {
                            val value = 3.0
                            var x = 0.0
                            var z = 0.0

                            when (mc.thePlayer.horizontalFacing) {
                                EnumFacing.NORTH -> z = -value
                                EnumFacing.EAST -> x = +value
                                EnumFacing.SOUTH -> z = +value
                                EnumFacing.WEST -> x = -value
                            }

                            mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z)
                            teleported = true
                        }
                    }

                    "mineplex" -> {
                        mc.thePlayer.motionY += 0.0132099999999999999999999999999
                        mc.thePlayer.jumpMovementFactor = 0.08f
                        MovementUtils.strafe()
                    }

                    "mineplex2" -> {
                        if (!canMineplexBoost)
                            return@run

                        mc.thePlayer.jumpMovementFactor = 0.1f
                        if (mc.thePlayer.fallDistance > 1.5f) {
                            mc.thePlayer.jumpMovementFactor = 0f
                            mc.thePlayer.motionY = (-10f).toDouble()
                        }

                        MovementUtils.strafe()
                    }

                    "redesky" -> {
                        if (!mc.thePlayer.onGround) {
                            if (rsMoveReducerValue.get()) {
                                mc.thePlayer.jumpMovementFactor = rsJumpMovementValue.get() -(airTicks*(rsReduceMovementValue.get()/100))
                            } else {
                                mc.thePlayer.jumpMovementFactor = rsJumpMovementValue.get()
                            }
                            if (rsMotYReducerValue.get()){
                                mc.thePlayer.motionY += (rsMotionYValue.get() / 10F)-(airTicks*(rsReduceYMotionValue.get()/100))
                            }else{
                                mc.thePlayer.motionY += rsMotionYValue.get() / 10F
                            }
                            if (rsUseTimerValue.get()) {
                                mc.timer.timerSpeed = rsTimerValue.get()
                            }
                        }
                    }

                    "redesky2" -> {
                        if (!mc.thePlayer.onGround){
                            if(rs2YMotionReducerValue.get()){
                                val motY=rs2YMotionValue.get()-(airTicks*(rs2ReduceYMotionValue.get()/100))
                                if(motY<rs2MinYMotionValue.get()){
                                    mc.thePlayer.motionY += rs2MinYMotionValue.get()
                                }else{
                                    mc.thePlayer.motionY += motY
                                }
                            }else{
                                mc.thePlayer.motionY += rs2YMotionValue.get()
                            }
                            //as reduce
                            if(rs2AirSpeedReducerValue.get()){
                                val airSpeed=rs2AirSpeedValue.get()-(airTicks*(rs2ReduceAirSpeedValue.get()/100))
                                if(airSpeed<rs2MinAirSpeedValue.get()){
                                    mc.thePlayer.speedInAir = rs2MinAirSpeedValue.get()
                                }else{
                                    mc.thePlayer.speedInAir = airSpeed
                                }
                            }else{
                                mc.thePlayer.speedInAir = rs2AirSpeedValue.get()
                            }
                        }
                    }

                    "redesky3" -> {
                        if(!timer.hasTimePassed(rs3JumpTimeValue.get().toLong())){
                            mc.thePlayer.motionY+=rs3HeightValue.get()/10F
                            MovementUtils.move(rs3BoostValue.get()/10F)
                            mc.timer.timerSpeed = rs3TimerValue.get()
                        }else{
                            mc.timer.timerSpeed = 1F
                        }
                    }

                    "blocksmc" -> {
                        mc.thePlayer.jumpMovementFactor = 0.1f
                        mc.thePlayer.motionY += 0.0132
                        mc.thePlayer.jumpMovementFactor = 0.09f
                        mc.timer.timerSpeed = 0.8f
                        MovementUtils.strafe()
                    }

                    "blocksmc2" -> {
                        mc.thePlayer.motionY += 0.01554
                        MovementUtils.strafe(MovementUtils.getSpeed() * 1.114514f)
                        mc.timer.timerSpeed = 0.917555f
                    }
                    
                    "redeskytest" -> {
                        mc.thePlayer.motionY = 0.42
                        MovementUtils.strafe(MovementUtils.getSpeed() * 1.12f)
                        mc.timer.timerSpeed = 0.8f
                    }

                    "hyt4v4" -> {
                        mc.thePlayer.motionY += 0.031470000997
                        MovementUtils.strafe(MovementUtils.getSpeed() * 1.0114514f)
                        mc.timer.timerSpeed = 1.0114514f
                    }
                }
            }
        }

        if (autoJumpValue.get() && mc.thePlayer.onGround && MovementUtils.isMoving()) {
            jumped = true
            if(hasJumped&&autoDisableValue.get()){
                state=false
                return
            }
            mc.thePlayer.jump()
            hasJumped=true
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        mc.thePlayer ?: return
        val mode = modeValue.get()

        if (mode.equals("mineplex3", ignoreCase = true)) {
            if (mc.thePlayer.fallDistance != 0.0f)
                mc.thePlayer.motionY += 0.037
        } else if (mode.equals("ncp", ignoreCase = true) && !MovementUtils.isMoving() && jumped) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            event.zeroXZ()
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        jumped = true
        canBoost = true
        teleported = false

        timer.reset()

        if (state) {
            when (modeValue.get().toLowerCase()) {
                "mineplex" -> event.motion = event.motion * 4.08f
                "mineplex2" -> {
                    if (mc.thePlayer!!.isCollidedHorizontally) {
                        event.motion = 2.31f
                        canMineplexBoost = true
                        mc.thePlayer!!.onGround = false
                    }
                }
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}
