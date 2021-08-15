package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class AAC4Inf : SpeedMode("AAC4Inf") {
    override fun onUpdate() {
        if (!MovementUtils.isMoving())
            return;
        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
            mc.timer.timerSpeed = 0.99F;
        }
        if (mc.thePlayer.fallDistance > 0.7 && mc.thePlayer.fallDistance < 1.3) {
            mc.timer.timerSpeed = 1.04F;
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }
}
