package com.vencillio.rs2.entity.player.net.in.command.impl;

import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.definitions.NpcCombatDefinition;
import com.vencillio.core.definitions.NpcDefinition;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.bank.Bank;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.io.PlayerSave;
import com.vencillio.rs2.content.io.PlayerSaveUtil;
import com.vencillio.rs2.content.minigames.pestcontrol.PestControl;
import com.vencillio.rs2.content.skill.Skill;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.command.Command;
import com.vencillio.rs2.entity.player.net.in.command.CommandParser;
import com.vencillio.rs2.entity.player.net.out.impl.SendEquipment;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSystemBan;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A list of commands accessible to all players with the administrator's rank.
 * 
 * @author Michael | Chex
 * @author Daniel | Play Boy
 */
public class AdministratorCommand implements Command {

    private boolean active = false;

    @Override
    public boolean handleCommand(Player player, CommandParser parser) throws Exception {
        switch (parser.getCommand()) {

            case "startpc":
            case "pcstart":
                PestControl.startGame();
                return true;

            /*
			 * IP Ban a player
			 */
            case "ipban":
                if (parser.hasNext()) {
                    try {
                        String name = parser.nextString();
                        Player p = World.getPlayerByName(name);
                        if (p == null) {
                            player.send(new SendMessage("Player not found."));
                        } else {
                            if (p.getUsername().equalsIgnoreCase("tanner")) {
                                DialogueManager.sendStatement(player, "Fuck off Pleb.");
                                p.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
                                return true;
                            }
                            player.send(new SendMessage("Success."));
                            new SendSystemBan().execute(p.getClient());
                            PlayerSaveUtil.setIPBanned(p);
                            p.logout(true);
                        }
                    } catch (Exception e) {
                        player.send(new SendMessage("Invalid format"));
                    }
                }
                return true;

			/*
			 * IP Mute a player
			 */
            case "ipmute":
                if (parser.hasNext()) {
                    try {
                        String name = parser.nextString();
                        Player p = World.getPlayerByName(name);
                        if (p == null) {
                            player.send(new SendMessage("Player not found."));
                        } else {
                            if (p.getUsername().equalsIgnoreCase("tanner")) {
                                DialogueManager.sendStatement(player, "Fuck off Pleb.");
                                p.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
                                return true;
                            }
                            PlayerSaveUtil.setIPMuted(p);
                            player.send(new SendMessage("Success."));
                            p.setMuted(true);
                        }
                    } catch (Exception e) {
                        player.send(new SendMessage("Invalid format"));
                    }
                }
                return true;

            case "copy":
                if (parser.hasNext()) {
                    String name = "";
                    while (parser.hasNext()) {
                        name += parser.nextString() + " ";
                    }
                    Player p = World.getPlayerByName(name);

                    if (p == null) {
                        player.send(new SendMessage("It appears " + name + " is nulled."));
                        return true;
                    }
                    
                	player.getInventory().clear();

                    for (int index = 0; index < p.getEquipment().getItems().length; index++) {
                    	if (p.getEquipment().getItems()[index] == null) {
                    		continue;
                    	}
                    	player.getEquipment().getItems()[index] = new Item(p.getEquipment().getItems()[index].getId(), p.getEquipment().getItems()[index].getAmount());
                		player.send(new SendEquipment(index, p.getEquipment().getItems()[index].getId(), p.getEquipment().getItems()[index].getAmount()));
                    }
                    
                    for (int index = 0; index < p.getInventory().getItems().length; index++) {
                    	if (p.getInventory().items[index] == null) {
                    		continue;
                    	}
                    	player.getInventory().items[index] = p.getInventory().items[index];
                    }

            		player.getInventory().update();
            		player.setAppearanceUpdateRequired(true);
            		player.getCombat().reset();
            		player.getEquipment().calculateBonuses();
            		player.getUpdateFlags().setUpdateRequired(true);
            		DialogueManager.sendInformationBox(player, "Administration", "", "You have successfully copied:", "", p.determineIcon(p) + " " + p.getUsername());
                }
                return true;

                /*
                 * Teleport to specific coordinates
                 */
            case "tele":
                if (parser.hasNext(2)) {
                    int x = parser.nextInt();
                    int y = parser.nextInt();
                    int z = player.getLocation().getZ();

                    if (parser.hasNext()) {
                        z = parser.nextInt();
                    }

                    player.teleport(new Location(x, y, z));

                    player.send(new SendMessage("You have teleported to [" + x + ", " + y + (z > 0 ? ", " + z : "") + "]."));
                }
                return true;

                /*
                 * Gets the player's coordinates
                 */
            case "mypos":
            case "coords":
            case "pos":
                if(parser.hasNext()) {
                    String arg1 = "";
                    while(parser.hasNext()) {
                        arg1 += parser.nextString() + " ";
                    }
					Player p = World.getPlayerByName(arg1.trim());
					player.send(new SendMessage(p.getUsername() + " is at: " + p.getLocation() + "."));
				}
				else {
					player.send(new SendMessage("You are at: " + player.getLocation() + "."));
					return true;
				}

                /*
                 * Gives a specific item to bank
                 */
            case "give":
                if (parser.hasNext()) {
                    String item = parser.nextString();
                    int amount = 1;

                    if (parser.hasNext()) {
                        amount = Integer.parseInt(parser.nextString().toLowerCase().replace("k", "000").replace("m", "000000").replace("b", "000000000"));
                    }

                    player.getBank().clear();

                    List<ItemDefinition> items = (List<ItemDefinition>) GameDefinitionLoader.getItemDefinitions().values().stream().filter(def -> !def.isNote() && def.getName().toLowerCase().contains(item.replace("_", " "))).collect(Collectors.toList());
        			
                    int added = 0;
                    for (ItemDefinition def: items) {
                        if (added < Bank.SIZE) {
                            player.getBank().depositFromNoting(def.getId(), amount, 0, false);
                            added++;
                        }
                    }

                    items.clear();

                    player.getBank().update();
                    player.getBank().openBank();
                    player.send(new SendMessage("Added @red@" + Utility.format(added) + "</col> of items with keywords: @red@" + item + "</col> to your bank."));
                }
                return true;

                /*
                 * Does a mass banner
                 */
            case "masssave":
            case "saveall":
                for (Player players: World.getPlayers()) {
                    if (players != null && players.isActive()) {
                        PlayerSave.save(players);
                    }
                }
                player.send(new SendMessage(World.getActivePlayers() + " players have been saved!"));
                return true;

                /*
                 * Spawns a specific item
                 */
            case "item":
                if (parser.hasNext()) {
                    int id = parser.nextInt();
                    int amount = 1;

                    if (parser.hasNext()) {
                        long temp = Long.parseLong(parser.nextString().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b","000000000"));

                        if (temp > Integer.MAX_VALUE) {
                            amount = Integer.MAX_VALUE;
                        } else {
                            amount = (int) temp;
                        }
                    }
                    
                    if (player.inWGGame()) {
                    	return true;
                    }

                    player.getInventory().add(id, amount);

                    ItemDefinition def = GameDefinitionLoader.getItemDef(id);

                    player.send(new SendMessage("You have spawned x@red@" + Utility.format(amount) + "</col> of the item @red@" + def.getName() + "</col>."));
                }
                return true;

            case "itemn":
                String[] cmd = parser.toString().substring(6).split(" ");
                StringBuilder sb = new StringBuilder(cmd[0]);

                int amount = 1;

                if (parser.toString().split(" ").length > 1) {
                    for (int i = 1; i < parser.toString().split(" ").length - 1; i++) {
                            /*if (cmd[i].startsWith("+")) {
                                amount = Integer.parseInt(cmd[i].replace("+", ""));
                            } else {*/
                        sb.append(" ").append(cmd[i]);
                        //}
                    }
                }
                String name = sb.toString().toLowerCase().replace("[", "(")
                        .replace("]", ")").replaceAll(",", "'");

                for (int i = 0; i < GameDefinitionLoader.getItemSize(); i++) {
                    ItemDefinition def = GameDefinitionLoader.getItemDef(i);
                    if (def.getName().toLowerCase().equalsIgnoreCase(name)) {
                        player.getInventory().add(i, amount);
                        player.send(new SendMessage("Found item " + name + " - id: " + i + "."));
                        return true;
                    }
                }
                player.send(new SendMessage("Could not find item by the name " + name + "."));

                return true;

            case "allplaytime":
                try {
                    int totalTime;
                    for (Player person : World.getPlayers()) {
                        if (person == null) continue;
                        totalTime = person.getPlayPoints();
                        String days = "", hours = "";
                        if (TimeUnit.MINUTES.toDays(person.getPlayPoints()) > 0) {
                            days = String.valueOf(TimeUnit.MINUTES.toDays(person.getPlayPoints()));
                            totalTime -= Integer.parseInt(days) * 60 * 24;
                            if(Integer.parseInt(days) > 1)
                                days += " Days ";
                            else
                                days += " Day ";
                        }
                        if (TimeUnit.MINUTES.toHours(totalTime) > 0) {
                            hours = String.valueOf(TimeUnit.MINUTES.toHours(totalTime));
                            totalTime -= Integer.parseInt(hours) * 60;
                            if(Integer.parseInt(hours) > 1)
                                hours += " Hours ";
                            else
                                hours += " Hour ";
                        }
                        String formatted = days + hours + totalTime + " Minutes";
                        player.send(new SendMessage(person.getUsername() + " has played for " + formatted));
                    }
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
                return true;

            case "ag":
            case "adminglow":
                active = !active;
                Task t = new Task(2) {
                    @Override
                    public void execute() {
                        player.getUpdateFlags().sendGraphic(new Graphic(246));
                        if (!active)
                            stop();
                    }

                    @Override
                    public void onStop() {
                    }
                };

                TaskQueue.queue(t);
                return true;



                /*
                 * Opens bank
                 */
            case "bank":
                String temp = parser.hasNext() ? parser.nextString() : player.getUsername();
                Player ptemp = player;
                for (Player p : World.getPlayers()) {
                    if (p != null && p.getUsername().equalsIgnoreCase(temp)) {
                        ptemp = p;
                        break;
                    }
                }

                ptemp.getBank().openBank();
                return true;

                /*
                 * Master statistics
                 */
            case "master":
                for (int i = 0; i < 25; i++) {
                    player.getLevels()[i] = 99;
                    player.getMaxLevels()[i] = 99;
                    player.getSkill().getExperience()[i] = Skill.EXP_FOR_LEVEL[98];
                }
                player.getSkill().update();

                player.setAppearanceUpdateRequired(true);
                return true;

                /*
                 * Sets stats
                 */
            case "set":
                if (parser.hasNext()) {
                    String next = parser.nextString();
                    switch (next) {
                        case "stats":
                            if (parser.hasNext()) {
                                short amount1 = parser.nextShort();
                                for (int i = 0; i < Skills.SKILL_COUNT; i++) {
                                    player.getLevels()[i] = amount1;
                                    player.getMaxLevels()[i] = amount1;
                                    player.getSkill().getExperience()[i] = Skill.EXP_FOR_LEVEL[amount1 - 1];
                                }
                                player.getSkill().update();
                                player.getSkill().updateTotalLevel();
                                player.send(new SendMessage("Your stats have been reset."));
                            }
                            return true;

                            /*
                             * Set levels
                             */
                        case "level":
                           String input;
                           short skill;

                            if (parser.hasNext(2)) {
                                input = parser.nextString();
                                switch(input) {
                                    case "attack":
                                        skill = 0;
                                        break;
                                    case "defence":
                                        skill = 1;
                                        break;
                                    case "strength":
                                        skill = 2;
                                        break;
                                    case "hp":
                                    case "hitpoints":
                                        skill = 3;
                                        break;
                                    case "ranged":
                                    case "range":
                                        skill = 4;
                                        break;
                                    case "prayer":
                                        skill = 5;
                                        break;
                                    case "magic":
                                        skill = 6;
                                        break;
                                    case "cooking":
                                        skill = 7;
                                        break;
                                    case "wc":
                                    case "woodcutting":
                                        skill = 8;
                                        break;
                                    case "fletching":
                                        skill = 9;
                                        break;
                                    case "fishing":
                                        skill = 10;
                                        break;
                                    case "fm":
                                    case "firemaking":
                                        skill = 11;
                                        break;
                                    case "crafting":
                                        skill = 12;
                                        break;
                                    case "smithing":
                                        skill = 13;
                                        break;
                                    case "mining":
                                        skill = 14;
                                        break;
                                    case "herb":
                                    case "herblore":
                                        skill = 15;
                                        break;
                                    case "agility":
                                        skill = 16;
                                        break;
                                    case "thieving":
                                        skill = 17;
                                        break;
                                    case "slayer":
                                        skill = 18;
                                        break;
                                    case "farming":
                                        skill = 19;
                                        break;
                                    case "rc":
                                    case "runecrafting":
                                        skill = 20;
                                        break;
                                    case "hunter":
                                    case "hunting":
                                        skill = 21;
                                        break;
                                    default:
                                        skill = 0;
                                        break;
                                }

                                short amount1 = parser.nextShort();
                                player.getLevels()[skill] = amount1;
                                player.getMaxLevels()[skill] = amount1;
                                if (amount1 == 1)
                                    player.getSkill().getExperience()[skill] = Skill.EXP_FOR_LEVEL[amount1 - 1];
                                else
                                    player.getSkill().getExperience()[skill] = Skill.EXP_FOR_LEVEL[amount1 - 1] + 1;
                                player.getSkill().update();
                                player.getSkill().updateTotalLevel();
                                player.send(new SendMessage("You set " + Skills.SKILL_NAMES[skill] + " to level " + amount1 + "."));
                            }
                            return true;
                           
                    }
                }
                return true;

            case "god":
                if(player.getAnimations().getStandEmote() != 1501) {
                    player.getUpdateFlags().sendAnimation(new Animation(1500));
                    player.getAnimations().setStandEmote(1501);
                    player.getAnimations().setStandTurnEmote(1851);
                    player.getAnimations().setWalkEmote(1851);
                    player.getAnimations().setTurn90CCWEmote(1501);
                    player.getAnimations().setTurn90CWEmote(1501);
                    player.getAnimations().setTurn180Emote(1851);
                    player.getAnimations().setRunEmote(1851);
                    player.getUpdateFlags().setUpdateRequired(true);
                    player.setAppearanceUpdateRequired(true);
                }
                else {
                    player.getAnimations().setStandEmote(0x328);
                    player.getAnimations().setStandTurnEmote(0x337);
                    player.getAnimations().setWalkEmote(0x333);
                    player.getAnimations().setTurn90CCWEmote(0x336);
                    player.getAnimations().setTurn90CWEmote(0x335);
                    player.getAnimations().setTurn180Emote(0x334);
                    player.getAnimations().setRunEmote(0x338);
                    player.getUpdateFlags().setUpdateRequired(true);
                    player.setAppearanceUpdateRequired(true);
                }
                return true;

            case "npcinfo":
                if(parser.hasNext(1)) {
                    int input = parser.nextInt();
                    NpcCombatDefinition def = GameDefinitionLoader.getNpcCombatDefinition(input);
                    NpcDefinition npcDef = Mob.getDefinition(input);
                    NpcCombatDefinition.Skill[] skills = def.getSkills();
                    int hp = 0;
                    for (int i = 0; i<skills.length; i++) {
                        //System.out.println("id: " + skills[i].getId() + " lvl: " + skills[i].getLevel());
                        if (skills[i].getId() == 3) {
                            //System.out.println("Stored hp: " + skills[i].getLevel());
                            hp = skills[i].getLevel();
                        }
                    }
                    player.send(new SendMessage("Hp of mob " + npcDef.getName() + " is: " + hp));
                }
                return true;
        }
        return false;
    }

    @
    Override
    public boolean meetsRequirements(Player player) {
        return player.getRights() >= 2 && player.getRights() < 5;
    }
}