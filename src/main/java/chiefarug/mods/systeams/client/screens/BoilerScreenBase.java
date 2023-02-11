package chiefarug.mods.systeams.client.screens;

import chiefarug.mods.systeams.block_entities.BoilerBlockEntityBase;
import chiefarug.mods.systeams.containers.BoilerContainerBase;
import cofh.core.client.gui.CoreTextures;
import cofh.core.client.gui.element.panel.ResourcePanel;
import cofh.core.util.helpers.GuiHelper;
import cofh.thermal.core.client.gui.ThermalGuiHelper;
import cofh.thermal.lib.client.gui.AugmentableScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BoilerScreenBase<T extends BoilerContainerBase<?>> extends AugmentableScreen<T> {

	protected BoilerBlockEntityBase blockEntity;

	public BoilerScreenBase(String id, T container, Inventory inv, BoilerBlockEntityBase blockEntity, Component titleIn) {
		super(container, inv, blockEntity, titleIn);
		this.blockEntity = blockEntity;
		info = GuiHelper.appendLine(GuiHelper.generatePanelInfo("info.systeams." + id + "_boiler"), "info.systeams.boiler.throttle");
	}

	@Override
	public void init() {
		super.init();

		addPanel(new ResourcePanel(this)
				.setResource(CoreTextures.ICON_ENERGY, "info.systeams.steam", true)
				.setEfficiency(tile::getEfficiency)
				.setCurrent(tile::getCurSpeed, "info.systeams.steam_prod", "info.cofh.unit_mb_t")
				.setMax(blockEntity::getWaterConsumption, "info.systeams.water_cons", "info.cofh.unit_mb_t")
		);


		addElement(ThermalGuiHelper.createDefaultDuration(this, 80, 35, GuiHelper.SCALE_FLAME, tile));
		addElement(GuiHelper.setClearable(GuiHelper.createMediumFluidStorage(this, 10, 22, blockEntity.waterTank), blockEntity, 0));
		addElement(GuiHelper.setClearable(GuiHelper.createMediumFluidStorage(this, 128, 22, blockEntity.steamTank), blockEntity, 1));
	}
}
