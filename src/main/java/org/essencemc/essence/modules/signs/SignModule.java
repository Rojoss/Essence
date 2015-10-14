package org.essencemc.essence.modules.signs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.commands.module.signs.BreakSignCmd;
import org.essencemc.essence.modules.signs.config.SignCfg;
import org.essencemc.essence.modules.signs.config.SignData;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.arguments.internal.Argument;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.modules.Module;
import org.essencemc.essencecore.modules.StorageModule;
import org.essencemc.essencecore.parsers.ItemParser;
import org.essencemc.essencecore.util.Util;

public class SignModule extends Module implements StorageModule {

    private SignCfg config;
    private SignMenu menu;

    public SignModule(String name) {
        super(name);
    }

    @Override
    protected void onEnable() {
        config = new SignCfg("plugins/Essence/modules/signs/Signs.yml");
        menu = new SignMenu(this);
    }

    @Override
    protected void onDisable() {
    }

    @Override
    protected void onReload() {
        config.load();
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onSave() {
    }

    public SignCfg getConfig() {
        return config;
    }

    public SignMenu getMenu() {
        return menu;
    }

    public SignData getMatchingSign(String[] lines) {
        for (int i = 0; i < 4; i++) {
            lines[i] = Util.stripAllColor(lines[i]);
        }
        for (SignData sign : config.getSignList()) {
            if (sign.getLine(sign.getUniqueLine()).equalsIgnoreCase(lines[sign.getUniqueLine()])) {
                return sign;
            }
        }
        return null;
    }

    public Sign getSign(Block block, boolean mustAttach) {
        if (block.getState() instanceof Sign) {
            return (Sign)block.getState();
        }
        //Check for attached blocks.
        BlockFace[] dirs = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
        for (BlockFace dir : dirs) {
            Block relative = block.getRelative(dir);
            if (relative.getState() instanceof Sign) {
                if (!mustAttach) {
                    return (Sign)relative.getState();
                }
                org.bukkit.material.Sign signMat = (org.bukkit.material.Sign)relative.getState().getData();
                if (relative.getRelative(signMat.getAttachedFace()).equals(block)) {
                    return (Sign)relative.getState();
                }
            }
        }
        return null;
    }

    public boolean matchLine(String syntax, String line) {
        if (syntax == null || line == null) {
            return false;
        }
        syntax = Util.stripAllColor(syntax);
        if (syntax.isEmpty()) {
            return true;
        }
        if (syntax.equals(line)) {
            return true;
        }
        String str = syntax.replaceAll("\\{\\w+\\}", "");

        return false;
    }

    @EventHandler
    private void signCreate(SignChangeEvent event) {
        SignData sign = getMatchingSign(event.getLines().clone());
        if (sign == null) {
            return;
        }
        //TODO: Better permission check.
        String perm = "essence.signs.create" + (sign.getSubPermission().isEmpty() ? "" : "." + sign.getSubPermission());
        if (!event.getPlayer().hasPermission(perm)) {
            Message.NO_PERM.msg().send(event.getPlayer(), true, true, Param.P("perm", perm));
            event.setCancelled(true);
        }

        SignParser parser = new SignParser(sign, event.getLines());
        if (parser.isValid()) {
            for (int i = 0; i < 4; i++) {
                event.setLine(i, Util.color(event.getLine(i)));
            }
            EssMessage.CORE_SIGN_CREATED.msg().send(event.getPlayer(), true, true, Param.P("name", sign.getName()));
        } else {
            event.setCancelled(true);
            //event.getBlock().breakNaturally();
            parser.getError().send(event.getPlayer(), true, true);
        }
    }

    @EventHandler
    private void signBreak(BlockBreakEvent event) {
        Sign signBlock = getSign(event.getBlock(), true);
        if (signBlock == null) {
            return;
        }

        SignData sign = getMatchingSign(signBlock.getLines().clone());
        if (sign == null) {
            return;
        }

        EssenceCommand cmd = EssenceCore.inst().getCommands().getCommand(BreakSignCmd.class);
        if (cmd != null) {
            if (!((BreakSignCmd)cmd).inSignBreakList(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                return;
            }
        }

        //TODO: Better permission check.
        String perm = "essence.signs.break" + (sign.getSubPermission().isEmpty() ? "" : "." + sign.getSubPermission());
        if (!event.getPlayer().hasPermission(perm)) {
            Message.NO_PERM.msg().send(event.getPlayer(), true, true, Param.P("perm", perm));
            event.setCancelled(true);
        }

        EssMessage.CORE_SIGN_BROKEN.msg().send(event.getPlayer(), true, true, Param.P("name", sign.getName()),
                Param.P("line-1", signBlock.getLine(0)), Param.P("line-2", signBlock.getLine(1)),
                Param.P("line-3", signBlock.getLine(2)), Param.P("line-4", signBlock.getLine(3)));
    }

    @EventHandler
    private void signUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (event.getAction() == Action.PHYSICAL) {
            block = block.getRelative(BlockFace.DOWN);
        }
        if (block == null || block.getType() == Material.AIR) {
            return;
        }
        Sign signBlock = getSign(block, false);
        if (signBlock == null) {
            return;
        }

        SignData sign = getMatchingSign(signBlock.getLines().clone());
        if (sign == null) {
            return;
        }
        if (!sign.isEnabled()) {
            return;
        }
        if (!sign.isAttachedBlockAction() && !signBlock.getBlock().equals(event.getClickedBlock())) {
            return;
        }

        //TODO: Better permission check.
        String perm = "essence.signs.use" + (sign.getSubPermission().isEmpty() ? "" : "." + sign.getSubPermission());
        if (!player.hasPermission(perm)) {
            Message.NO_PERM.msg().send(player, true, true, Param.P("perm", perm));
            event.setCancelled(true);
        }

        String action = "";
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            action = sign.getLeftAction(player.isSneaking());
        } else {
            action = sign.getRightAction(player.isSneaking());
        }

        if (!action.isEmpty()) {
            SignParser signParser = new SignParser(sign, signBlock.getLines());
            if (signParser.isValid()) {
                action = action.replace("{player}", player.getName());
                action = action.replace("{hand}", new ItemParser(player.getItemInHand()).getString());
                for (Argument arg : signParser.getArguments()) {
                    action = action.replace("{" + arg.getName() + "}", arg.toString());
                }
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), action);
            } else {
                EssMessage.CORE_SIGN_INVALID_SYNTAX.msg().send(player, true, true);
                signParser.getError().send(player, true, true);
            }
        }
    }

    @EventHandler
    private void textInput(AsyncPlayerChatEvent event) {
        if (!menu.hasInput(event.getPlayer())) {
            return;
        }
        menu.setInputResult(event.getPlayer(), event.getMessage());
        event.setCancelled(true);
    }

}
