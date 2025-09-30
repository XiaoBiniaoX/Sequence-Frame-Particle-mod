package com.xulielizi.xulielizimod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(XulieliziMod.MODID)
public class XulieliziMod {
    public static final String MODID = "xulielizimod";

    public XulieliziMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        NetworkHandler.register(); // 注册网络包

        // 注册指令
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommand);
    }

    private void onRegisterCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(ParticleCommand.build());
    }
}
