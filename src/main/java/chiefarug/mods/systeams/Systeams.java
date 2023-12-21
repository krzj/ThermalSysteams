package chiefarug.mods.systeams;

import chiefarug.mods.systeams.compat.mekanism.SysteamsMekanismCompat;
import chiefarug.mods.systeams.compat.pneumaticcraft.SysteamsPNCRCompat;
import chiefarug.mods.systeams.networking.RecipeCheckerChannel;
import cofh.core.common.config.CoreClientConfig;
import cofh.lib.util.Utils;
import cofh.lib.util.constants.ModIds;
import cofh.lib.util.helpers.StringHelper;
import com.mojang.logging.LogUtils;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

import static net.minecraftforge.eventbus.api.EventPriority.LOWEST;

@Mod("systeams")
@Mod.EventBusSubscriber(modid = Systeams.MODID)
public class Systeams {
    @SuppressWarnings("unused")
    public static final Logger LGGR = LogUtils.getLogger();
    public static final String MODID = "systeams";

    public static final Capability<IAirHandlerMachine> AIR_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IGasHandler> GAS_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<IGasHandler>() {});

    public Systeams() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        SysteamsRegistry.init(bus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SysteamsConfig.spec, "systeams-server.toml");
        RecipeCheckerChannel.init();
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(ConversionKitItem::fillDynamoMap));
        
        if (ModList.get().isLoaded("pneumaticcraft")) {
            SysteamsPNCRCompat.unfoldPressurizedManifold(bus);
        }
        if (ModList.get().isLoaded("mekanism")) {
            SysteamsMekanismCompat.activateMechanisedManifold(bus);
        }
    }

    /**
     * @param first The first tag
     * @param second The second tag. Will override any duplicate values in first
     * @return A new tag
     */
    @Nullable
    @Contract("null,null -> null;!null,_ -> !null;_,!null -> !null")
    public static CompoundTag mergeTags(@Nullable CompoundTag first, @Nullable CompoundTag second) {
        if (first == null && second == null) return null;
        if (first == null)
            return second.copy();
        else if (second == null)
            return first.copy();
        return first.copy().merge(second);
    }

    private static boolean first = true;
    @SubscribeEvent(priority = LOWEST)
    static void loginLogger(PlayerEvent.PlayerLoggedInEvent _e) {
        // if you recognise the quote, good job
        if (first) {
            LGGR.info("Welcome aboard Captain. All Systeams online");
            first = false;
        }
    }

    @SubscribeEvent
    static void tooltipEvent(ItemTooltipEvent event) {
        // copy this because the default check only does cofh namespaces
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        String modid = Utils.getModId(stack.getItem());
        if (!(modid.equals(MODID) || /*CoreClientEvents.NAMESPACES*/ modid.equals(ModIds.ID_THERMAL))) return;


        if (CoreClientConfig.enableKeywords.get()) {
            String translationKey = stack.getDescriptionId() + ".keyword";
            if (StringHelper.canLocalize(translationKey)) {
                if (tooltip.get(0) instanceof MutableComponent mutable) {
                    mutable.append(StringHelper.getKeywordTextComponent(translationKey));
                }
            }
        }

//        if (CoreClientConfig.enableItemDescriptions.get()) {
//            if (stack.getItem().equals(SysteamsRegistry.Items.RF_COIL.get())) {
//                tooltip.add(Component.translatable(stack.getDescriptionId() + ".desc").withStyle(ChatFormatting.GOLD));
//            }
//        }
    }

}
