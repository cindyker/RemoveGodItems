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
package net.daboross.bukkitdev.removegoditems;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.daboross.bukkitdev.removegoditems.checks.*;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MasterChecker {

    private final RemoveGodItemsPlugin plugin;
    private RGICheck[] checks = new RGICheck[0];

    public MasterChecker(RemoveGodItemsPlugin plugin) {
        this.plugin = plugin;
        
        loadChecks();
    }

    public void loadChecks() {
        List<String> checkNames = plugin.getConfig().getStringList("checks");
        List<RGICheck> checksTemp = new ArrayList<RGICheck>(checkNames.size());
        for (String checkName : checkNames) {
            checkName = checkName.toLowerCase();
            RGICheck check;
            if (checkName.equals("enchantments")) {
                check = new EnchantmentCheck(plugin);
            } else if (checkName.equals("oversized")) {
                check = new OversizedCheck(plugin);
            } else if (checkName.equals("allattributes")) {
                check = new AttributesCheck(plugin);
            } else if (checkName.equals("namelength")) {
                check = new NameLengthCheck(plugin);
            } else if (checkName.equals("invaliddata")) {
                check = new InvalidDataCheck(plugin);
            } else {
                plugin.getLogger().log(Level.WARNING, "Unknown listener ''{0}''.", checkName);
                continue;
            }
            checksTemp.add(check);
        }
        checks = checksTemp.toArray(new RGICheck[checksTemp.size()]);
    }

    public void checkItems(HumanEntity player) {
        String name = player.getName();
        PlayerInventory inv = player.getInventory();
        Location loc = player.getLocation();
        ItemStack[] armor = new ItemStack[inv.getArmorContents().length];
        ItemStack[] contents = new ItemStack[inv.getContents().length];
        int i = 0;
        for (ItemStack it : inv.getArmorContents()) {
            armor[i] = checkItem(it, inv, loc, name);
            i++;
        }
        i = 0;
        for (ItemStack it : inv.getContents()) {
            contents[i] = checkItem(it, inv, loc, name);
            i++;
        }
        
        inv.setArmorContents(armor);
        inv.setContents(contents);
    }

    public ItemStack checkItem(ItemStack itemStack, HumanEntity p) {
        return checkItem(itemStack, p.getInventory(), p.getLocation(), p.getName());
    }

    public ItemStack checkItem(ItemStack itemStack, Inventory playerInventory, Location playerLocation, String playerName) {
        for (RGICheck check : checks) {
            itemStack = check.checkItem(itemStack, playerInventory, playerLocation, playerName);
        }
        
        return itemStack;
    }

    public void checkItemsNextSecond(Player p) {
        plugin.getServer().getScheduler().runTaskLater(plugin, new GodItemFixRunnable(p), 20);
    }

    public void checkItemsNextTick(HumanEntity p, Iterable<Integer> slots) {
        plugin.getServer().getScheduler().runTask(plugin, new VariedCheckRunnable(p, slots));
    }

    public void checkItemsNextTick(HumanEntity p, Inventory i) {
        plugin.getServer().getScheduler().runTask(plugin, new InventoryCheckRunnable(p.getName(), i, p.getLocation()));
    }

    public class GodItemFixRunnable implements Runnable {

        private final HumanEntity p;

        public GodItemFixRunnable(HumanEntity p) {
            this.p = p;
        }

        @Override
        public void run() {
            checkItems(p);
        }
    }

    public class VariedCheckRunnable implements Runnable {

        private final HumanEntity p;
        private final Iterable<Integer> items;

        public VariedCheckRunnable(HumanEntity p, Iterable<Integer> items) {
            this.p = p;
            this.items = items;
        }

        @Override
        public void run() {
            String name = p.getName();
            Inventory inv = p.getInventory();
            int size = inv.getSize();
            for (Integer i : items) {
                if (i > 0 && i < size) {
                    checkItem(inv.getItem(i), inv, p.getLocation(), name);
                }
            }
        }
    }

    public class InventoryCheckRunnable implements Runnable {

        private final String name;
        private final Inventory inv;
        private final Location location;

        public InventoryCheckRunnable(final String name, final Inventory inv, final Location location) {
            this.name = name;
            this.inv = inv;
            this.location = location;
        }

        @Override
        public void run() {
            for (int i = 0; i < inv.getSize(); i++) {
                checkItem(inv.getItem(i), inv, location, name);
            }
        }
    }
}
