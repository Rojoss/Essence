package org.essencemc.essence.commands.item;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.arguments.StringArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;

import java.util.List;

public class SkullCmd extends EssenceCommand {
    public SkullCmd(Plugin plugin, String label, String description, String permission, List<String> aliases) {
        super(plugin, label, description, permission, aliases);

        addArgument("owner", new StringArg(), ArgumentRequirement.REQUIRED);

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Message.CMD_PLAYER_ONLY.msg().send(sender);
            return true;
        }

        ArgumentParseResults result = parseArgs(this, sender, args);

        if(!result.success) {
            return true;
        }

        String owner = ((String) result.getArg("owner"));
        castPlayer(sender).getInventory().addItem(getSkull(owner));

        if(!result.hasModifier("-s")) {
            EssMessage.CMD_SKULL_GIVE.msg().send(sender, Param.P("owner", owner));
        }

        return true;
    }

    private ItemStack getSkull(String owner) {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, ((short) 3));
        SkullMeta meta = ((SkullMeta) stack.getItemMeta());

        meta.setOwner(owner);
        stack.setItemMeta(meta);

        return stack;
    }
}