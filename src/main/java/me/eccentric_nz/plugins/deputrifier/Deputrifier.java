package me.eccentric_nz.plugins.deputrifier;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Deputrifier extends JavaPlugin implements Listener {

    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        Material m = b.getType();

        if (m.equals(Material.CHEST)) {
            Block down = b.getRelative(BlockFace.DOWN);
            if (down != null) {
                Material md = down.getType();
                byte d = down.getData();
                if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && md.equals(Material.WOOL) && d == 13) {
                    Chest chest = (Chest) event.getClickedBlock().getState();
                    ItemStack is0 = chest.getInventory().getItem(0);
                    ItemStack is9 = chest.getInventory().getItem(9);
                    ItemStack is18 = chest.getInventory().getItem(18);
                    if (is0 != null && is9 != null && is18 != null) {
                        if (is0.getType() == Material.YELLOW_FLOWER
                                && is9.getType() == Material.ROTTEN_FLESH
                                && is18.getType() == Material.RED_ROSE
                                && p.hasPermission("deputrifier.use")
                                && p.getGameMode() != GameMode.CREATIVE) {
                            int[] amounts = new int[3];
                            amounts[0] = is0.getAmount();
                            amounts[1] = is9.getAmount();
                            amounts[2] = is18.getAmount();
                            int j = 0;
                            // find lowest value
                            for (int i = 1; i < 3; i++) {
                                if (amounts[i] < amounts[j]) {
                                    j = i;
                                }
                            }
                            int lowestAmount = amounts[j];
                            int half = Math.round(lowestAmount / 2);
                            Inventory inv = chest.getInventory();
                            inv.removeItem(new ItemStack(Material.YELLOW_FLOWER, lowestAmount));
                            inv.removeItem(new ItemStack(Material.ROTTEN_FLESH, lowestAmount));
                            inv.removeItem(new ItemStack(Material.RED_ROSE, lowestAmount));
                            inv.setItem(17, new ItemStack(Material.RAW_BEEF, half));
                            chest.update(true);
                            p.sendMessage("The Deputrifier says 'Enjoy!', please remove your meat...");
                        }
                    }
                }
            }
        }
    }
}
