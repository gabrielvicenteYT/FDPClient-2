package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHUDDesigner

@ModuleInfo(name = "HUDDesigner", category = ModuleCategory.CLIENT, canEnable = false)
class HUDDesigner : Module() {
    override fun onEnable(){
        mc.displayGuiScreen(GuiHudDesigner())
    }
}
