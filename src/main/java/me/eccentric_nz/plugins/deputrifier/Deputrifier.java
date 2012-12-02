package me.eccentric_nz.plugins.deputrifier;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class Deputrifier extends JavaPlugin implements Listener {

    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here.
        this.saveConfig();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        if (this.getConfig().getBoolean("enable_craft")) {
            this.deputrify();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if (b != null) {
            Material m = b.getType();

            if (m.equals(Material.CHEST) && this.getConfig().getBoolean("enable_chest")) {
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
                                int ratioAmount = (int) Math.floor(amounts[j] / 3);
                                int lowestAmount = ratioAmount * 3;
                                Inventory inv = chest.getInventory();
                                inv.removeItem(new ItemStack(Material.YELLOW_FLOWER, lowestAmount));
                                inv.removeItem(new ItemStack(Material.ROTTEN_FLESH, lowestAmount));
                                inv.removeItem(new ItemStack(Material.RED_ROSE, lowestAmount));
                                inv.setItem(17, new ItemStack(Material.RAW_BEEF, ratioAmount));
                                chest.update(true);
                                p.sendMessage("The Deputrifier says 'Enjoy!', please remove your meat...");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("dptoggle")) {
            if (args.length == 1 && (args[0].equalsIgnoreCase("chest") || args[0].equalsIgnoreCase("craft"))) {
                String str = "enable_" + args[0];
                boolean bool = this.getConfig().getBoolean(str);
                bool = !bool;
                this.getConfig().set(str, bool);
                sender.sendMessage("Deputrifier says '" + str + " was set to: " + bool + "'");
                this.saveConfig();
                if (args[0].equalsIgnoreCase("craft")) {
                    sender.sendMessage("Deputrifier says 'Changes are effective after a server restart/reload!'");
                }
                return true;
            } else {
                sender.sendMessage("Deputrifier says 'I don't understand that!'");
            }
        }
        return false;
    }

    public ShapedRecipe deputrify() {
        ShapedRecipe recipe = new ShapedRecipe(new ItemStack(Material.RAW_BEEF, 1));
        recipe.shape("AAA", "BBB", "CCC");
        recipe.setIngredient('A', Material.YELLOW_FLOWER);
        recipe.setIngredient('B', Material.ROTTEN_FLESH);
        recipe.setIngredient('C', Material.RED_ROSE);
        this.getServer().addRecipe(recipe);
        return recipe;
    }
}
