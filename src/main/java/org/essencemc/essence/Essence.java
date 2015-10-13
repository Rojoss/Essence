package org.essencemc.essence;

import org.bukkit.plugin.java.JavaPlugin;
import org.essencemc.essence.commands.fun.PushCmd;
import org.essencemc.essence.commands.item.GiveCmd;
import org.essencemc.essence.commands.item.ItemCmd;
import org.essencemc.essence.commands.item.ItemInfoCmd;
import org.essencemc.essence.commands.location.DelWarpCmd;
import org.essencemc.essence.commands.location.SetWarpCmd;
import org.essencemc.essence.commands.location.WarpCmd;
import org.essencemc.essence.commands.location.WarpsCmd;
import org.essencemc.essence.commands.misc.BroadcastCmd;
import org.essencemc.essence.commands.misc.SummonCmd;
import org.essencemc.essence.commands.misc.TestCmd;
import org.essencemc.essence.commands.module.signs.SignsCmd;
import org.essencemc.essence.commands.player.MessageCmd;
import org.essencemc.essence.commands.player.NicknameCmd;
import org.essencemc.essence.commands.player.SudoCmd;
import org.essencemc.essence.commands.player_status.*;
import org.essencemc.essence.commands.plugin.MainPluginCmd;
import org.essencemc.essence.commands.punishments.BanCmd;
import org.essencemc.essence.commands.teleport.*;
import org.essencemc.essence.commands.world.LightningCmd;
import org.essencemc.essence.commands.world.TreeCmd;
import org.essencemc.essence.config.Warps;
import org.essencemc.essence.modules.ban.BanModule;
import org.essencemc.essence.modules.signs.SignModule;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.commands.Commands;
import org.essencemc.essencecore.modules.Modules;

import java.util.logging.Logger;

public class Essence extends JavaPlugin {

    private static Essence instance;
    private static EssenceCore core;

    private Warps warps;

    private final Logger log = Logger.getLogger("Essence");


    @Override
    public void onDisable() {
        instance = null;
        log("disabled");
    }

    @Override
    public void onEnable() {
        instance = this;
        log.setParent(this.getLogger());

        //TODO: Validate that EssenceCore is running and make sure the version is compatible etc.
        core = EssenceCore.inst();

        //TODO: Have a class for modules were it would create the config and such.
        warps = new Warps("plugins/Essence/data/Warps.yml");

        registerCommands();
        registerModules();

        log("loaded successfully");
    }


    public void log(Object msg) {
        log.info("[Essence " + getDescription().getVersion() + "] " + msg.toString());
    }
    public void warn(Object msg) {
        log.warning("[Essence " + getDescription().getVersion() + "] " + msg.toString());
    }
    public void logError(Object msg) {
        log.severe("[Essence " + getDescription().getVersion() + "] " + msg.toString());
    }


    public void registerCommands() {
        Commands cmds = core.getCommands();
        cmds.registerCommand(this, TestCmd.class, "test", "", "", "Command for testing plugin functionality.", new String[]{});
        cmds.registerCommand(this, MainPluginCmd.class, "essence", "", "", "Main plugin command and config reloading", new String[]{"essentials", "essential"});
        cmds.registerCommand(this, HealCmd.class, "heal", "", "heal", "Heal a player", new String[]{"health", "sethealth"});
        cmds.registerCommand(this, FeedCmd.class, "feed", "", "feed", "Feed a player", new String[]{"hunger", "eat"});
        cmds.registerCommand(this, LightningCmd.class, "lightning", "", "lightning", "Strike lightning somewhere", new String[]{"smite"});
        cmds.registerCommand(this, GamemodeCmd.class, "gamemode", "", "gamemode", "Change a player his gamemmode", new String[]{"gm"});
        cmds.registerCommand(this, SetWarpCmd.class, "setwarp", "warps", "setwarp", "Set a warp with the given name", new String[]{"addwarp", "warpset"});
        cmds.registerCommand(this, DelWarpCmd.class, "delwarp", "warps", "delwarp", "Delete a warp with the given name", new String[]{"warpdel", "deletewarp", "rmwarp", "removewarp", "warpdelete", "warprm", "warpremove"});
        cmds.registerCommand(this, WarpsCmd.class, "warps", "warps", "warplist", "List all the warps (for a world)", new String[]{"warplist"});
        cmds.registerCommand(this, WarpCmd.class, "warp", "warps", "warp", "Teleport to a warp", new String[]{});
        cmds.registerCommand(this, TpCmd.class, "tp", "", "tp", "Teleport to a player", new String[]{"teleport", "tele"});
        cmds.registerCommand(this, NicknameCmd.class, "nickname", "", "nickname", "Change your nickname", new String[]{"nick", "displayname", "name"});
        cmds.registerCommand(this, RemoveEffectCmd.class, "removeeffect", "", "removeeffect", "Remove potion effects", new String[]{"remeffect", "remeffects", "cleareffect", "cleareffects", "removeeffects"});
        cmds.registerCommand(this, ItemInfoCmd.class, "iteminfo", "", "iteminfo", "Show item detailed item information.", new String[]{"itemdb"});
        cmds.registerCommand(this, BurnCmd.class, "burn", "", "burn", "Set yourself or another player on fire for the specified amount of seconds. (or ticks)", new String[]{"ignite"});
        cmds.registerCommand(this, FlyCmd.class, "fly", "", "fly", "Toggle flight on/off.", new String[]{"flight"});
        cmds.registerCommand(this, WalkspeedCmd.class, "walkspeed", "", "walkspeed", "Change your walking speed.", new String[]{"walkingspeed"});
        cmds.registerCommand(this, FlyspeedCmd.class, "flyspeed", "", "flyspeed", "Change your flying speed.", new String[]{"flyingspeed"});
        cmds.registerCommand(this, InvseeCmd.class, "invsee", "", "invsee", "View another player's inventory.", new String[]{});
        cmds.registerCommand(this, EnderchestCmd.class, "enderchest", "", "enderchest", "View your or another player's enderchest", new String[]{});
        cmds.registerCommand(this, SuicideCmd.class, "suicide", "", "suicide", "Kill yourself", new String[]{});
        cmds.registerCommand(this, KillCmd.class, "kill", "", "kill", "Kill someone else", new String[]{"slay"});
        cmds.registerCommand(this, TreeCmd.class, "tree", "", "tree", "Generate a tree somewhere in the world", new String[]{});
        cmds.registerCommand(this, GodCmd.class, "god", "", "god", "Turns your or another player's god mode on or off.", new String[]{"immortal", "invulnerable", "immortality", "invulnerability"});
        cmds.registerCommand(this, TpHereCmd.class, "tphere", "", "tphere", "Teleports a player to your location.", new String[]{});
        cmds.registerCommand(this, SudoCmd.class, "sudo", "", "sudo", "Execute a command on someone's behalf.", new String[]{});
        cmds.registerCommand(this, BanCmd.class, "ban", "", "ban", "Bans a player from the server.", new String[]{});
        cmds.registerCommand(this, SummonCmd.class, "summon", "", "summon", "Summons any entity with any specified data.", new String[]{"spawnmob", "sm", "spawnentity", "se"});
        cmds.registerCommand(this, MessageCmd.class, "message", "", "message", "Sends a private message to another online player.", new String[]{"msg", "tell"});
        cmds.registerCommand(this, WorldCmd.class, "world", "", "world", "Teleport to a specific world or show detailed world information.", new String[]{"worldinfo"});
        cmds.registerCommand(this, PushCmd.class, "push", "", "push", "Push yourself with a specific velocity.", new String[]{"velocity", "motion", "vel", "force"});
        cmds.registerCommand(this, ItemCmd.class, "item", "", "item", "Give yourself items. [[http://www.wiki.essencemc.org/meta]{{&7Go to the wiki!}&9&nmeta info}]", new String[]{"i"});
        cmds.registerCommand(this, GiveCmd.class, "give", "", "give", "Give an item to someone. [[http://www.wiki.essencemc.org/meta]{{&7Go to the wiki!}&9&nmeta info}]", new String[]{});
        cmds.registerCommand(this, JumpCmd.class, "jump", "", "jump", "Jump to where you're looking.", new String[]{"jumpto"});
        cmds.registerCommand(this, TopCmd.class, "top", "", "top", "Teleport to the highest block at your location.", new String[]{});
        cmds.registerCommand(this, BroadcastCmd.class, "broadcast", "", "broadcast", "Broadcast a message to all players.", new String[]{"bc","say","announce"});
        cmds.registerCommand(this, SignsCmd.class, "signs", "signs", "signs_cmd", "Open the sign editing menu to add or edit custom signs.", new String[]{});
    }

    public void registerModules() {
        Modules modules = core.getModules();
        modules.registerModule(BanModule.class, "punishments", "ban");
        modules.registerModule(SignModule.class, "signs", "signs_core");
    }


    public static Essence inst() {
        return instance;
    }

    public static EssenceCore core() {
        return core;
    }


    public Warps getWarps() {
        return warps;
    }

}
