/*
 * Copyright (C) 2014 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.removegoditems.checks;

import net.daboross.bukkitdev.removegoditems.LogKey;
import net.daboross.bukkitdev.removegoditems.RGICheck;
import net.daboross.bukkitdev.removegoditems.RemoveGodItemsPlugin;
import net.daboross.bukkitdev.removegoditems.SkyLog;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class PotionEffectCheck implements RGICheck {

    private final RemoveGodItemsPlugin plugin;

    public PotionEffectCheck(final RemoveGodItemsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack checkItem(final ItemStack itemStack, final Inventory inventory, final Location location, final String playerName) {
        if (itemStack != null && itemStack.getType() == Material.POTION) {
        	PotionMeta potion = (PotionMeta) itemStack.getItemMeta();
        	
        	if (!potion.hasCustomEffects()) return itemStack;
        	if (potion.clearCustomEffects()) {
        		SkyLog.log(LogKey.REMOVE_OVERPOTION, itemStack.getType(), playerName);
        		itemStack.setItemMeta(potion);
        		
        		return itemStack;
        	}
        }
        
        return itemStack;
    }
}
