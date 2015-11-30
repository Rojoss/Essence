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

import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.message.EMessage;
import org.essencemc.essencecore.message.EText;
import org.essencemc.essencecore.message.MsgCat;

public enum EssMessage {
    //General
    INPUT_MODE_DISABLED(MsgCat.GENERAL, "{p} &6Input mode disabled. &7You may chat again."),

    //Module messages
    CORE_SIGN_CREATED(MsgCat.CORE_MODULE, "{p} &a&l{name} &6sign created!"),
    CORE_SIGN_BROKEN(MsgCat.CORE_MODULE, "{p} &6You broke a &a&l{name} &6sign. {{{line-1}\n{line-2}\n{line-3}\n{line-4}}&8&o[&7&oSignData&8&o]}"),
    CORE_SIGN_ADD(MsgCat.CORE_MODULE, "{p} &6Please specify a &aname &6for the sign.\n&7You can type the name in chat to set it.\n" +
            "&7This name is used to identify the sign and it &ccan't be changed &7later on!\n&7Type &c&lq &7to disable chat input mode."),
    CORE_SIGN_INVALID_NAME(MsgCat.CORE_MODULE, "{p} &cThe specified name &4{input} &cis not a valid name!\n&7Please specify a new name without spaces that only contains alphanumeric characters.\n" +
            "&7Type &c&lq &7to disable chat input mode."),
    CORE_SIGN_NAME_EXISTS(MsgCat.CORE_MODULE, "{p} &cA sign with this name already exisits!\n&7Please specify a new name."),
    CORE_SIGN_LINE_NOT_UNIQUE(MsgCat.CORE_MODULE, "{p} &cThis line is marked as unique but the value specified is already used by the &4{sign} &csign."),
    CORE_SIGN_SET_LINE(MsgCat.CORE_MODULE, "{p} &6Type &6&lin chat &6what should should be displayed on &aline {line}&6.\n&7Type &c&lq &7to disable chat input mode."),
    CORE_SIGN_SET_ACTION(MsgCat.CORE_MODULE, "{p} &6Type &6&lin chat &6what action should happen for &a{action}&6.\n&7Type &c&lq &7to disable chat input mode."),
    CORE_SIGN_INVALID_SYNTAX(MsgCat.CORE_MODULE, "{p} &cThe syntax of this sign is no longer valid."),
    CORE_SIGN_SYNTAX_MISMATCH(MsgCat.CORE_MODULE, "{p} &cSyntax mismatch at &8'&c{char}&8' &con line &4{line} &cexpected &8'&c{expected}&8'&c. &4&lSyntax&8: &c&l{syntax}"),
    CORE_KITS_MENU_INFO(MsgCat.CORE_MODULE, "&aLeft &7click on any of the kits to &aget &7it.\n&eRight &7click to &edisplay &7the kit contents."),
    CORE_KITS_MENU_TITLE(MsgCat.CORE_MODULE, "&9&lKits"),
    CORE_KIT_DISPLAY_MENU_INFO(MsgCat.CORE_MODULE, "&7This menu displays the kit contents.\n&7Click on the kit icon to spawn it in."),
    CORE_KIT_DISPLAY_MENU_ICON_INFO(MsgCat.CORE_MODULE, "&7Displaying kit contents for the kit &a{kit}&7!\n&eClick &7to &egive &7yourself the kit."),
    CORE_KIT_DISPLAY_MENU_TITLE(MsgCat.CORE_MODULE, "&9&lKit Display"),
    CORE_SHOPITEMS_DISPLAY(MsgCat.CORE_MODULE, "&8===== &4&lShop Items &7[&c&l{page}&7] &8=====\n{items}\n" +
            "&8===== <<{cmd} {prev}>&6&lPREV>  &8- <<<{cmd} item:>>&a&lNEW> &8- " +
            "<<{cmd}>&c&lCLOSE> &8- <<{cmd} {next}>&6&lNEXT> &8====="),
    CORE_SHOPITEMS_DISPLAY_ITEM(MsgCat.CORE_MODULE, "&6&l{item} &8- " +
            "[[&7The buy price.\n&aClick to edit.]&a{currency}{buy}]&7/" +
            "[[&7The sell price.\n&aClick to edit.]&c{currency}{sell}] &8- " +
            "[[&7Can the item be placed on the market?\n&aClick to toggle.]&6Market&7({market}&7)]"),
    CORE_BAN_BANNED(MsgCat.CORE_MODULE, "&4&lYou've been banned from the server!\n&cReason&8: &7&o{reason}\n&cTime left&8: &7{remaining} &cby&8: &7{punisher}"),
    CORE_NO_REASON(MsgCat.CORE_MODULE, "No reason specified."),
    CORE_KICK_MESSAGE(MsgCat.CORE_MODULE, "&4&lYou've been kicked from the server!\n&cReason&8: &7&o{reason}\n&cBy&8: &7{punisher}"),

    //Command messages
    CMD_BACK_NULL(MsgCat.COMMAND, "{p} &cYou don't have a previous location to teleport to."),
    CMD_BACK_NULL_OTHER(MsgCat.COMMAND, "{p} &4{player} &cdoesn't have a previous location to teleport to."),
    CMD_BACK_TP(MsgCat.COMMAND, "{p} &6You have been teleported to your previous location."),
    CMD_BACK_TP_OTHER(MsgCat.COMMAND, "{p} &6You have teleported &2{player} &6to their previous location."),
    CMD_GOD_SENDER_ON(MsgCat.COMMAND, "{p} &6You enabled god mode for &2{player}&6!"),
    CMD_GOD_SENDER_OFF(MsgCat.COMMAND, "{p} &cYou disabled god mode for &4{player}&c!"),
    CMD_GOD_RECEIVER_ON(MsgCat.COMMAND, "{p} &6You are now a god!"),
    CMD_GOD_RECEIVER_OFF(MsgCat.COMMAND, "{p} &cYou are no longer a god!"),
    CMD_ESSENCE_INFO(MsgCat.COMMAND, "&8===== &4&lEssence plugin &8=====\n&8&o{0}\n&6Version&8: &7{1}\n&6Website&8: &9{2}\n&6Authors&8: &7{3}"),
    CMD_ESSENCE_RELOAD(MsgCat.COMMAND, "{p} &6Configs and commands reloaded."),
    CMD_HEAL_HEALED(MsgCat.COMMAND, "{p} &6You have been healed!"),
    CMD_DELSPAWN_ALL(MsgCat.COMMAND, "{p} &cDeleted all spawns from database."),
    CMD_DELSPAWN_ALLPLAYERS(MsgCat.COMMAND, "{p} &cDeleted all player spawns from database."),
    CMD_DELSPAWN_MAIN(MsgCat.COMMAND, "{p} &cDeleted main spawn from database."),
    CMD_DELSPAWN_PLAYER(MsgCat.COMMAND, "{p} &cDeleted &4{player}'s &cpersonal spawn."),
    CMD_HEAL_OTHER(MsgCat.COMMAND, "{p} &6You have healed &a{player}&6."),
    CMD_HEAL_ALL(MsgCat.COMMAND, "{p} &6You have healed all players!"),
    CMD_FEED_FEEDED(MsgCat.COMMAND, "{p} &6You have been fed!"),
    CMD_FEED_OTHER(MsgCat.COMMAND, "{p} &6You have fed &a{player}&6."),
    CMD_GAMEMODE_CHANGED(MsgCat.COMMAND, "{p} &6Gamemode changed to &a{mode}&6."),
    CMD_GAMEMODE_OTHER(MsgCat.COMMAND, "{p} &6You have changed &a{player}'s &6gamemode to &a{mode}&6."),
    CMD_WEATHER_INFO(MsgCat.COMMAND, "&6It is &a{type} &6in &a{world}&6."),
    CMD_WEATHER_CHANGED(MsgCat.COMMAND, "&6You have changed the weather in &a{world} &6to &a{type} &6for &a{duration} &6seconds."),
    CMD_LIGHTNING(MsgCat.COMMAND, "{p} &6Lightning has struck!"),
    CMD_LIST_PLAYERLIST(MsgCat.COMMAND, "&8===== &2&lPlayer list &8=====\n&fOnline: [{online}/{slots}]&a{players}"),
    CMD_WARP_SET(MsgCat.COMMAND, "{p} &6Warp &a{warp} &6set!"),
    CMD_WARP_DELETED(MsgCat.COMMAND, "{p} &6Warp &a{warp} &6deleted!"),
    CMD_WARP_DELETED_AlL(MsgCat.COMMAND, "{p} &6All warps have been deleted!"),
    CMD_WARP_INVALID(MsgCat.COMMAND, "{p} &cNo warp found with the name &4{warp}&c!"),
    CMD_WARPS(MsgCat.COMMAND, "{p} &6&lWarps&8&l: &7{warps}&6."),
    CMD_WARPS_NONE(MsgCat.COMMAND, "&cNo warps set yet!"),
    CMD_WARP_USE(MsgCat.COMMAND, "{p} &6Warping to &a{warp}&6..."),
    CMD_WARP_OTHER(MsgCat.COMMAND, "{p} &6You have sent &a{player} &6to the warp &a{warp}&6."),
    CMD_TP(MsgCat.COMMAND, "{p} &6Teleported to &a{player}&6."),
    CMD_TP_OTHER(MsgCat.COMMAND, "{p} &6You have teleported &a{player1} &6to &a{player2}&6."),
    CMD_NICK_CHANGED(MsgCat.COMMAND, "{p} &6Nickname changed to &r{nick}&6."),
    CMD_NICK_OTHER(MsgCat.COMMAND, "{p} &6You have changed &a{player}'s &6nickname to &r{nick}&6."),
    CMD_REMOVEEFFECT(MsgCat.COMMAND, "{p} &6Removed &a{effect} &6potion effect."),
    CMD_REMOVEEFFECT_ALL(MsgCat.COMMAND, "{p} &6All potion effects removed."),
    CMD_REMOVEEFFECT_OTHER(MsgCat.COMMAND, "{p} &6Removed &a{player}&6's &a{effect} &6potion effect."),
    CMD_REMOVEEFFECT_OTHER_ALL(MsgCat.COMMAND, "{p} &6All of &a{player}'s potion effects have been removed."),
    CMD_BURN(MsgCat.COMMAND, "{p} &6You will burn for &a{ticks} &6ticks."),
    CMD_BURN_OTHER(MsgCat.COMMAND, "{p} &a{player} &6will burn for &a{ticks} &6ticks."),
    CMD_FLY(MsgCat.COMMAND, "{p} &6Flight state: &a{state}&6."),
    CMD_FLY_OTHER(MsgCat.COMMAND, "{p} &a{player}&6's flight state: &a{state}&6."),
    CMD_GOD_TOGGLEON(MsgCat.COMMAND, "{p}&6You are now god!"),
    CMD_GOD_TOGGLEOFF(MsgCat.COMMAND, "{p}&4You are no longer god!"),
    CMD_WALKSPEED(MsgCat.COMMAND, "{p} &6Your walking speed is now &a{speed}&6."),
    CMD_WALKSPEED_OTHER(MsgCat.COMMAND, "{p} &a{player}&6's walking speed is now &a{speed}&6."),
    CMD_FLYSPEED(MsgCat.COMMAND, "{p} &6Your flying speed is now &a{speed}&6."),
    CMD_FLYSPEED_OTHER(MsgCat.COMMAND, "{p} &a{player}&6's flying speed is now &a{speed}&6."),
    CMD_SUICIDE(MsgCat.COMMAND, "{p} &a{player} &6has decided to take his own life."),
    CMD_KILL(MsgCat.COMMAND, "{p} &6You have been killed by &a{player}&6."),
    CMD_KILL_OTHER(MsgCat.COMMAND, "{p} &6You killed &a{player}&6."),
    CMD_KILL_EXEMPT(MsgCat.COMMAND, "{p} &cYou cannot kill &a{player}&c."),
    CMD_TREE(MsgCat.COMMAND, "{p} &a{type} &6tree has been generated."),
    CMD_TREE_FAILURE(MsgCat.COMMAND, "{p} &A tree cannot be generated there."),
    CMD_INVSEE(MsgCat.COMMAND, "{p} &6You're now viewing &a{player}&6's inventory."),
    CMD_INVSEE_EXEMPT(MsgCat.COMMAND, "{p} &cYou cannot view &a{player}&c's inventory."),
    CMD_ENDERCHEST(MsgCat.COMMAND, "{p} &6You are viewing your enderchest."),
    CMD_ENDERCHEST_OTHER(MsgCat.COMMAND, "{p} &cYou cannot view &a{player}&c's enderchest."),
    CMD_TPHERE(MsgCat.COMMAND, "{p} &6You have teleported &a{player} &6here."),
    CMD_SUDO(MsgCat.COMMAND, "{p} &6You made &a{player} &6run &a{cmd}&6."),
    CMD_SUMMON(MsgCat.COMMAND, "{p} &6Entities summoned!"),
    CMD_REPLY_NULL(MsgCat.COMMAND, "{p} &cYou have no one to reply."),
    CMD_MESSAGE_SENT(MsgCat.COMMAND, "&a&l@&a{receiver}&8>> &7&o{msg}"),
    CMD_MESSAGE_RECEIVE(MsgCat.COMMAND, "&a&l{sender}&8&l: &7&l&o{msg}"),
    CMD_ME_MESSAGE(MsgCat.COMMAND, "&7{sender} &f* &3{msg}"),
    CMD_ITEM_INFO(MsgCat.COMMAND, "&8===== &4&l{amount} {name} &8=====\n&6Type&8: &7{type}&8:&7{data}\n&6Aliases&8: &7{aliases}\n&6String&8: &7<<<{string}>>&7{string}>"),
    CMD_ITEM_INFO_META(MsgCat.COMMAND, "&6{key}&8: &7{value}"),
    CMD_WORLD_INFO(MsgCat.COMMAND, "&8===== &4&l{name} &8=====\n&6UUID&8: &7{uuid}\n&6Seed&8: &7{seed}\n&6Type&8: &7{type}\n&6Environment&8: &7{environment}\n&6Difficulty&8: &7{difficulty}\n&6Loaded chunks&8: &7{chunks}\n&6Spawn&8: &7{spawn}"),
    CMD_WORLD_TELEPORTED(MsgCat.COMMAND, "{p} &6Teleported to &a{world}&6."),
    CMD_WORLD_TELEPORTED_OTHER(MsgCat.COMMAND, "{p} &6Teleported &a{player} &6to &a{world}&6."),
    CMD_WORLD_LIST(MsgCat.COMMAND, "{p} &6You're currently in the world&8: &a{world}\n&6&lWorlds&8: &7{worlds}"),
    CMD_PUSH(MsgCat.COMMAND, "{p} &6Pushed with &c$vec.bx({velocity}) &a$vec.by({velocity}) &9$vec.by({velocity}) &6velocity."),
    CMD_PUSH_OTHER(MsgCat.COMMAND, "{p} &6Pushed &a{player} &6with &c$vel.bx({velocity}) &a$vel.by({velocity}) &9$vel.by({velocity}) &6velocity."),
    CMD_JUMP(MsgCat.COMMAND, "{p} &6You've jumped somewhere."),
    CMD_JUMP_ERROR(MsgCat.COMMAND, "{p} &cThere's nothing there, or it's too far away!"),
    CMD_TOP(MsgCat.COMMAND, "{p} &6You're now on the highest block."),
    CMD_TOP_NONE(MsgCat.COMMAND, "{p} &cEvery block at your location is empty."),
    CMD_TOP_SAME(MsgCat.COMMAND, "{p} &cYou're already standing on the highest block."),
    CMD_SPAWN_TELEPORT(MsgCat.COMMAND, "{p} &6Teleported to spawn!"),
    CMD_SETSPAWN_SET(MsgCat.COMMAND, "{p} &6Spawn point set at for {variable}!"),
    CANT_SPAWN_AIR(MsgCat.COMMAND, "{p} &cYou can't spawn items that are air!"),
    CMD_ITEM_GIVE(MsgCat.COMMAND, "{p} &6You have been given &a&l{amount} &a{item}&6."),
    CMD_ITEM_GIVE_OTHER(MsgCat.COMMAND, "{p} &6You have given {player} &a&l{amount} &a{item}&6."),
    CMD_ITEM_META_HELP(MsgCat.COMMAND, "&6Meta tags&8: {tags}"),
    CMD_META_HELP_ENTRY(MsgCat.COMMAND, "{{&7{desc}}&7{tag}}"),
    CMD_META_HELP_ENTRY_EXTRA(MsgCat.COMMAND, "{{&7{desc}\n&b{values}}&7{tag}}"),
    CMD_EFFECT(MsgCat.COMMAND, "{p} &a{effect} &a&l{amplifier} &6applied for &a{duration} &6seconds."),
    CMD_EFFECT_OTHER(MsgCat.COMMAND, "{p} &a{effect} &a&l{amplifier} &6applied for &a{duration} &6seconds for &a{player}&6."),
    CMD_BREAKSIGN_ENABLED(MsgCat.COMMAND, "{p} &6You can now break custom signs."),
    CMD_BREAKSIGN_DISABLED(MsgCat.COMMAND, "{p} &6You can no longer break custom signs."),
    CMD_KIT_LIST(MsgCat.COMMAND, "&6&lKits&8&l: &7{kits}"),
    CMD_KIT_INVALID(MsgCat.COMMAND, "{p} &cNo kit found with the name &4{input}&c!"),
    CMD_KIT_DISABLED(MsgCat.COMMAND, "{p} &cThe &4{kit} &ckit is disabled!"),
    CMD_KIT_GIVEN(MsgCat.COMMAND, "{p} &6You received the &a{kit} &6kit!"),
    CMD_KIT_GIVEN_OTHER(MsgCat.COMMAND, "{p} &6You gave &a{player} &6the &a{kit} &6kit."),
    CMD_KIT_CREATED(MsgCat.COMMAND, "{p} &6Kit &a{kit} &6created!"),
    CMD_KIT_SET(MsgCat.COMMAND, "{p} &6Kit contents for the kit &a{kit} &6updated."),
    CMD_KIT_TOGGLE(MsgCat.COMMAND, "{p} &6The &a{kit} &6kit is now {state}."),
    CMD_KIT_ICON_AIR(MsgCat.COMMAND, "{p} &cYou can't set the kit icon to air."),
    CMD_KIT_ICON(MsgCat.COMMAND, "{p} &6Kit icon updated for the kit &a{kit}&6."),
    CMD_LAUNCH(MsgCat.COMMAND, "{p} &6You've launched a &a{projectile}&6."),
    CMD_BAN_BANNED(MsgCat.COMMAND, "{p} &6You've banned &a{player}&6!\n&6Duration&8: &7{duration}\n&6Reason&8: &7&o{reason}"),
    CMD_BAN_ALREADY_BANNED(MsgCat.COMMAND, "{p} &4{player} &cis already banned!"),
    CMD_BAN_BROADCAST(MsgCat.COMMAND, "{p} &4&l{player} &cgot banned by &4&l{punisher}&c!"),
    CMD_UNBAN_UNBANNED(MsgCat.COMMAND, "{p} &6You've unbanned &a{player}&6!"),
    CMD_UNBAN_NOT_BANNED(MsgCat.COMMAND, "{p} &4{player} &cisn't banned!"),
    CMD_KICK_KICKED(MsgCat.COMMAND, "{p} &6You've kicked &a{player}&6!\n&6Reason&8: &7&o{reason}"),
    CMD_KICK_BROADCAST(MsgCat.COMMAND, "{p} &4&l{player} &cgot kicked by &4&l{punisher}&c!"),
    CMD_SKULL_GIVE(MsgCat.COMMAND,"{p} &6You have been given the skull from &a{owner}&6."),

    //Command modifiers
    MOD_SETSPAWN_PLAYER(MsgCat.COMMAND_MODIFIERS, "Set personal spawn at specified player location."),
    MOD_DELSPAWN_ALL(MsgCat.COMMAND_MODIFIERS, "Delete all spawns."),
    MOD_DELSPAWN_ALLPLAYERS(MsgCat.COMMAND_MODIFIERS, "Delete all player spawns."),
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
    MOD_WORLD_INFO(MsgCat.COMMAND_MODIFIERS, "Show detailed information about the world."),
    MOD_PUSH_RELATIVE(MsgCat.COMMAND_MODIFIERS, "Makes the velocity relative to the player. (x,z are multipliers and y is raw value)"),
    MOD_BROACAST_NO_PREFIX(MsgCat.COMMAND_MODIFIERS, "Remove the prefix from the broadcasted message."),
    MOD_BROACAST_BAR(MsgCat.COMMAND_MODIFIERS, "Broadcast the message to people their action bar instead."),
    MOD_EFFECT_AMBIENT(MsgCat.COMMAND_MODIFIERS, "Makes the effect an ambient effect.\nThe particles will be barely visible."),
    MOD_EFFECT_FORCE(MsgCat.COMMAND_MODIFIERS, "Force apply the specified effect.\nWill override any existing effect."),
    MOD_KIT_DISPLAY(MsgCat.COMMAND_MODIFIERS, "Open the kit display menu."),
    MOD_KIT_LIST(MsgCat.COMMAND_MODIFIERS, "List all kit names that can be used."),
    MOD_KIT_ICON(MsgCat.COMMAND_MODIFIERS, "Sets the kit icon used in the kits menu."),
    MOD_KIT_TOGGLE(MsgCat.COMMAND_MODIFIERS, "Toggles the kit on/off."),
    MOD_VANISH_FAKE_QUIT(MsgCat.COMMAND_MODIFIERS, "Sends a fakse quit message when vanishing."),
    MOD_VANISH_FAKE_JOIN(MsgCat.COMMAND_MODIFIERS, "Sends a fake join message when unvanishing."),
    MOD_BAN_PERMANENT(MsgCat.COMMAND_MODIFIERS, "Makes the ban last forever unless manually unbanned."),

    //Command options/optional arguments
    OPT_HEAL_FEED(MsgCat.COMMAND_OPTIONS, "Restore hunger?"),
    OPT_HEAL_CLEAR_EFFECTS(MsgCat.COMMAND_OPTIONS, "Remove all active potion effects?"),
    OPT_HEAL_EXTINGUISH(MsgCat.COMMAND_OPTIONS, "Remove remaining fire ticks?"),
    OPT_FEED_SATURATION(MsgCat.COMMAND_OPTIONS, "The amount of saturation given."),
    OPT_FEED_EXHAUSTION(MsgCat.COMMAND_OPTIONS, "Reset exhaustion?"),
    OPT_WARP_PERM_BASED(MsgCat.COMMAND_OPTIONS, "Should warps be permissions based?\nLike essence.warps.spawn to use /warp spawn"),
    OPT_NICK_PREFIX(MsgCat.COMMAND_OPTIONS, "Prefix added in front of every nickname."),
    OPT_NICK_MIN_CHARS(MsgCat.COMMAND_OPTIONS, "Minimum amount of characters required. (exclusive prefix)"),
    OPT_NICK_MAX_CHARS(MsgCat.COMMAND_OPTIONS, "Maximum amount of characters allowed. (exclusive prefix)"),
    OPT_BURN_TICKS(MsgCat.COMMAND_OPTIONS, "Change the time from seconds to ticks for more precision.\nThere are 20 ticks in 1 second."),
    OPT_ALLOW_FLY(MsgCat.COMMAND_OPTIONS, "If true, it will allow the player to keep toggling\nflying by double tapping space.\nIf false the player can't start flying when double tapping space."),
    OPT_NO_HUNGER_LOSS(MsgCat.COMMAND_OPTIONS, "Don't lose hunger while in god mode?"),
    OPT_NO_DAMAGE(MsgCat.COMMAND_OPTIONS, "Don't damage other entities while in god mode?"),
    OPT_ITEM_STACK(MsgCat.COMMAND_OPTIONS, "Ignore item stack sizes and stack all items?"),
    OPT_ITEM_AMOUNT(MsgCat.COMMAND_OPTIONS, "The default amount of items when no amount is specified."),
    OPT_ITEM_DROP(MsgCat.COMMAND_OPTIONS, "Drop items on the ground when the inventory is full?"),
    OPT_BROADCAST_WORLD(MsgCat.COMMAND_OPTIONS, "Only broadcast the message to this world."),
    OPT_BROADCAST_PREFIX(MsgCat.COMMAND_OPTIONS, "The prefix that will be added in front of the message."),
    OPT_SHOPITEM_ITEMSPERPAGE(MsgCat.COMMAND_OPTIONS, "The amount of items displayed per page."),
    OPT_VANISH_CHAT(MsgCat.COMMAND_OPTIONS, "Can chat while vanished?"),
    OPT_VANISH_ATTACK(MsgCat.COMMAND_OPTIONS, "Can attack entities while vanished?"),
    OPT_VANISH_DAMAGE(MsgCat.COMMAND_OPTIONS, "Can take damage while vanished?"),
    OPT_VANISH_INTERACT(MsgCat.COMMAND_OPTIONS, "Can interact with the world while vanished?"),
    OPT_VANISH_PICKUP(MsgCat.COMMAND_OPTIONS, "Can pickup items while vanished?"),
    OPT_VANISH_TARGET(MsgCat.COMMAND_OPTIONS, "Can be targeted by entities while vanished?"),
    OPT_VANISH_SILENT_JOIN(MsgCat.COMMAND_OPTIONS, "Hide join message when logging in while vanished?"),
    OPT_VANISH_SILENT_QUIT(MsgCat.COMMAND_OPTIONS, "Hide quit/leave message when logging out while vanished?"),
    OPT_VANISH_SCOREBOARD_TEAM(MsgCat.COMMAND_OPTIONS, "Add vanished players to a scoreboard team.\nVanished players can see eachother as ghosts."),
    OPT_VANISH_INVIS_POTION(MsgCat.COMMAND_OPTIONS, "Add invisibility effect to vanished players. (for visuals only)"),
    OPT_BAN_DEFAULT_DURATION(MsgCat.COMMAND_OPTIONS, "The default duration if it's not specified."),
    OPT_BAN_BROADCAST(MsgCat.COMMAND_OPTIONS, "Broadcast a message to the server when banning someone?");

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
