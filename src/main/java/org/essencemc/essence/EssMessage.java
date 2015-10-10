/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Essence <http://essencemc.org>
 * Copyright (c) 2015 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.essencemc.essence;

import org.bukkit.entity.Player;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.message.EMessage;
import org.essencemc.essencecore.message.EText;
import org.essencemc.essencecore.message.MsgCat;

public enum EssMessage {
    //Command messages
    CMD_ESSENCE_INFO(MsgCat.COMMAND, "&8===== &4&lEssence plugin &8=====\n&8&o{0}\n&6Version&8: &7{1}\n&6Website&8: &9{2}\n&6Authors&8: &7{3}"),
    CMD_ESSENCE_RELOAD(MsgCat.COMMAND, "Configs and commands reloaded."),
    CMD_HEAL_HEALED(MsgCat.COMMAND, "You have been healed!"),
    CMD_HEAL_OTHER(MsgCat.COMMAND, "You have healed &a{0}&6."),
    CMD_HEAL_ALL(MsgCat.COMMAND, "You have healed all players!"),
    CMD_FEED_FEEDED(MsgCat.COMMAND, "You have been fed!"),
    CMD_FEED_OTHER(MsgCat.COMMAND, "You have fed &a{0}&6."),
    CMD_GAMEMODE_CHANGED(MsgCat.COMMAND, "Gamemode changed to &a{0}&6."),
    CMD_GAMEMODE_OTHER(MsgCat.COMMAND, "You have changed &a{0}'s &6gamemode to &a{1}&6."),
    CMD_LIGHTNING(MsgCat.COMMAND, "Lightning has struck!"),
    CMD_WARP_SET(MsgCat.COMMAND, "Warp &a{0} &6set!"),
    CMD_WARP_DELETED(MsgCat.COMMAND, "Warp &a{0} &6deleted!"),
    CMD_WARP_DELETED_AlL(MsgCat.COMMAND, "All warps have been deleted!"),
    CMD_WARP_INVALID(MsgCat.COMMAND, "&cNo warp found with the name &4{0}&c!"),
    CMD_WARPS(MsgCat.COMMAND, "&6&lWarps&8&l: &7{0}&6."),
    CMD_WARPS_NONE(MsgCat.COMMAND, "No warps set yet!"),
    CMD_WARP_USE(MsgCat.COMMAND, "Warping to &a{0}&6..."),
    CMD_WARP_OTHER(MsgCat.COMMAND, "You have sent &a{0} &6to the warp &a{1}&6."),
    CMD_TP(MsgCat.COMMAND, "&6Teleported to &a{0}&6."),
    CMD_TP_OTHER(MsgCat.COMMAND, "&6You have teleported &a{1} &6to &a{0}&6."),
    CMD_NICK_CHANGED(MsgCat.COMMAND, "&6Nickname changed to &r{0}&6."),
    CMD_NICK_OTHER(MsgCat.COMMAND, "&6You have changed &a{1}'s &6nickname to &r{0}&6."),
    CMD_REMOVEEFFECT(MsgCat.COMMAND, "&6Removed &a{0} &6potion effect."),
    CMD_REMOVEEFFECT_ALL(MsgCat.COMMAND, "&6All potion effects removed."),
    CMD_REMOVEEFFECT_OTHER(MsgCat.COMMAND, "&6Removed &a{0}&6's &a{1} &6potion effect."),
    CMD_REMOVEEFFECT_OTHER_ALL(MsgCat.COMMAND, "&6All of &a{0}'s potion effects have been removed."),
    CMD_BURN(MsgCat.COMMAND, "&6You will burn for &a{0} &6seconds."),
    CMD_BURN_OTHER(MsgCat.COMMAND, "&a{0} &6will burn for &a{1} &6seconds."),
    CMD_FLY(MsgCat.COMMAND, "&6Flight state: &a{0}&6."),
    CMD_FLY_OTHER(MsgCat.COMMAND, "&a{0}&6's flight state: &a{1}&6."),
    CMD_WALKSPEED(MsgCat.COMMAND, "&6Your walking speed is now &a{0}&6."),
    CMD_WALKSPEED_OTHER(MsgCat.COMMAND, "&a{0}&6's walking speed is now &a{1}&6."),
    CMD_FLYSPEED(MsgCat.COMMAND, "&6Your flying speed is now &a{0}&6."),
    CMD_FLYSPEED_OTHER(MsgCat.COMMAND, "&a{0}&6's flying speed is now &a{1}&6."),
    CMD_SUICIDE(MsgCat.COMMAND, "&a{0} &6has decided to take his own life."),
    CMD_KILL(MsgCat.COMMAND, "&6You have been killed by &a{0}&6."),
    CMD_KILL_OTHER(MsgCat.COMMAND, "&6You killed &a{0}&6."),
    CMD_KILL_EXEMPT(MsgCat.COMMAND, "&cYou cannot kill &a{0}&c."),
    CMD_TREE(MsgCat.COMMAND, "&6A tree has been generated."),
    CMD_TREE_FAILURE(MsgCat.COMMAND, "&cA tree cannot be generated there."),
    CMD_INVSEE(MsgCat.COMMAND, "&6You're now viewing &a{0}&6's inventory."),
    CMD_INVSEE_EXEMPT(MsgCat.COMMAND, "&cYou cannot view &a{0}&c's inventory."),
    CMD_ENDERCHEST(MsgCat.COMMAND, "&6You are viewing your enderchest."),
    CMD_ENDERCHEST_OTHER(MsgCat.COMMAND, "&cYou cannot view &a{0}&c's enderchest."),
    CMD_TPHERE(MsgCat.COMMAND, "&6You have teleported &a{0} &6here."),
    CMD_SUDO(MsgCat.COMMAND, "&6You made &a{0} &6run &a{1}&6."),
    CMD_SUMMON(MsgCat.COMMAND, "&6Entities summoned!"),
    CMD_MESSAGE(MsgCat.COMMAND, "&6To&c: &a{0}&c &6From&c: &a{1}&c &e{2}"),
    CMD_ITEM_INFO(MsgCat.COMMAND, "&8===== &4&l{amount} {name} &8=====\n&6Type&8: &7{type}&8:&7{data}\n&6Aliases&8: &7{aliases}\n&6String&8: &7<<<{string}>>&7{string}>"),
    CMD_ITEM_INFO_META(MsgCat.COMMAND, "&6{0}&8: &7{1}"),

    //Command modifiers
    MOD_HEAL_ONLY(MsgCat.COMMAND_MODIFIERS, "Only modify the health limited by the maximum health."),
    MOD_HEAL_MAX_ONLY(MsgCat.COMMAND_MODIFIERS, "Only modify the maximum health."),
    MOD_HEAL_ALL(MsgCat.COMMAND_MODIFIERS, "Heals every player on the server."),
    MOD_DELWARP_ALL(MsgCat.COMMAND_MODIFIERS, "Delete all warps"),
    MOD_NICK_REMOVE(MsgCat.COMMAND_MODIFIERS, "Remove your nickname."),
    MOD_REMOVEEFFECT_NEGATIVE(MsgCat.COMMAND_MODIFIERS, "Will ignore all positive potion effects and only remove the negative ones."),
    MOD_REMOVEEFFECT_POSITIVE(MsgCat.COMMAND_MODIFIERS, "Will ignore all negative potion effects and only remove the positive ones."),
    MOD_BURN_INCREMENT(MsgCat.COMMAND_MODIFIERS, "Increment the duration if the player is already burning."),
    MOD_GOD_RESET(MsgCat.COMMAND_MODIFIERS, "Reset any remaining effects like fire ticks, negative potion effects and so on."),
    MOD_RIDE_ENTITY(MsgCat.COMMAND_MODIFIERS, "Ride on top of the summoned entity."),

    //Command options/optional arguments
    OPT_HEAL_FEED(MsgCat.COMMAND_OPTIONS, "Restore hunger?"),
    OPT_HEAL_CLEAR_EFFECTS(MsgCat.COMMAND_OPTIONS, "Remove all active potion effects?"),
    OPT_HEAL_EXTINGUISH(MsgCat.COMMAND_OPTIONS, "Remove remaining fire ticks?"),
    OPT_FEED_SATURATION(MsgCat.COMMAND_OPTIONS, "The amount of saturation given."),
    OPT_FEED_EXHAUSTION(MsgCat.COMMAND_OPTIONS, "Reset exhaustion?"),
    OPT_WARP_PERM_BASED(MsgCat.COMMAND_OPTIONS, "Should warps be permissions based? Like essence.warp.spawn to use /warp spawn"),
    OPT_NICK_PREFIX(MsgCat.COMMAND_OPTIONS, "Prefix added in front of every nickname."),
    OPT_NICK_MIN_CHARS(MsgCat.COMMAND_OPTIONS, "Minimum amount of characters required. (exclusive prefix)"),
    OPT_NICK_MAX_CHARS(MsgCat.COMMAND_OPTIONS, "Maximum amount of characters allowed. (exclusive prefix)"),
    OPT_BURN_TICKS(MsgCat.COMMAND_OPTIONS, "Change the time from seconds to ticks for more precision. There are 20 ticks in 1 second."),
    OPT_ALLOW_FLY(MsgCat.COMMAND_OPTIONS, "If true, it will allow the player to keep toggling flying by double tapping space. If false the player can't start flying when double tapping space."),
    OPT_NO_HUNGER_LOSS(MsgCat.COMMAND_OPTIONS, "Don't lose hunger while in god mode?"),
    OPT_NO_DAMAGE(MsgCat.COMMAND_OPTIONS, "Don't damage other entities while in god mode?"),
    ;

    private EMessage message;

    EssMessage(MsgCat category, String defaultMsg) {
        message = new EMessage(category, this.toString(), defaultMsg, EssenceCore.inst().getMessages());
    }

    public EText msg() {
        return message.getText();
    }

    public EMessage emsg() {
        return message;
    }

    public EText msg(boolean prefix, boolean json) {
        EText text = message.getText();
        if (prefix) {
            text = text.addPrefix();
        }
        if (json) {
            text = text.toJSON();
        }
        return text;
    }

    public EText msg(boolean prefix, boolean json, Player player) {
        EText text = message.getText();
        if (prefix) {
            text = text.addPrefix();
        }
        if (player != null) {
            text = text.parsePlaceholders(player);
        }
        if (json) {
            text = text.toJSON();
        }
        return text;
    }

    public static EMessage fromString(String name) {
        name = name.toLowerCase().replace("_", "");
        name = name.toLowerCase().replace("-", "");
        for (EssMessage msg : values()) {
            if (msg.toString().toLowerCase().replace("_", "").equals(name)) {
                return msg.emsg();
            }
        }
        return null;
    }
}
