package chiefarug.mods.systeams.block_entities;

import cofh.lib.api.StorageGroup;
import cofh.lib.inventory.ItemStorageCoFH;
import cofh.thermal.core.config.ThermalCoreConfig;
import cofh.thermal.lib.util.managers.SingleItemFuelManager;
import cofh.thermal.lib.util.recipes.internal.IDynamoFuel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ItemBoilerBlockEntityBase extends BoilerBlockEntityBase {

	protected ItemStorageCoFH fuelSlot = new ItemStorageCoFH(item -> filter.valid(item) && getFuelManager().validFuel(item));

	public ItemBoilerBlockEntityBase(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		inventory.addSlot(fuelSlot, StorageGroup.INPUT);

		addAugmentSlots(ThermalCoreConfig.dynamoAugments);
	}

	@Override
	protected abstract SingleItemFuelManager getFuelManager();

	@Override
	protected int getEnergy() {
		IDynamoFuel fuel = getFuelManager().getFuel(this);
		return fuel == null ? 0 : fuel.getEnergy();
	}

	@Override
	protected int consumeFuel() {
		int energy = getEnergy();;
		fuelSlot.consume(1);
		return energy;
	}
}
