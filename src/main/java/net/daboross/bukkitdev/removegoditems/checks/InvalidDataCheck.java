package net.daboross.bukkitdev.removegoditems.checks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.daboross.bukkitdev.removegoditems.LogKey;
import net.daboross.bukkitdev.removegoditems.RGICheck;
import net.daboross.bukkitdev.removegoditems.RemoveGodItemsPlugin;
import net.daboross.bukkitdev.removegoditems.SkyLog;

public class InvalidDataCheck implements RGICheck {

	public InvalidDataCheck(RemoveGodItemsPlugin plugin) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack checkItem(ItemStack itemStack, Inventory playerInventory, Location playerLocation, String playerName) {
		if (itemStack == null || itemStack.getType() == Material.AIR)
			return itemStack;
		
		if (itemStack.getType() == Material.DOUBLE_PLANT) {
			int data = itemStack.getData().getData();

			if (data >= 6 && data != 8) {
				SkyLog.log(LogKey.REMOVE_INVALIDDATA, itemStack.getType(), data, playerName);
				itemStack.setType(Material.AIR);
			}
		}

		return itemStack;
	}

}
