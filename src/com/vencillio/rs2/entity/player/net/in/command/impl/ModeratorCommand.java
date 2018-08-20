package com.vencillio.rs2.entity.player.net.in.command.impl;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.Yelling;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.ModCommandsInterface;
import com.vencillio.rs2.content.io.PlayerSave;
import com.vencillio.rs2.content.io.PlayerSave.PlayerContainer;
import com.vencillio.rs2.content.io.PlayerSave.PlayerDetails;
import com.vencillio.rs2.content.io.PlayerSaveUtil;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.in.command.Command;
import com.vencillio.rs2.entity.player.net.in.command.CommandParser;
import com.vencillio.rs2.entity.player.net.out.impl.*;

/**
 * A list of commands accessible to all players with the moderator's rank.
 *
 * @author Michael | Chex
 * @author Daniel | Play Boy
 */
public class ModeratorCommand implements Command {

	boolean stop = false;
	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {

			/**
			 * Turns on staff stab
			 */
			case "stafftab":
			case "tabon":
				player.send(new SendString("</col>Rank: " + player.determineIcon(player) + " " + player.determineRank(player), 49705));
				if (player.getRights() == 1) {
					player.send(new SendString("You have limited access.", 49704));
				} else {
					player.send(new SendString("You have full access.", 49704));
				}
				player.send(new SendSidebarInterface(2, 49700));
				player.send(new SendOpenTab(2));
				player.send(new SendMessage("Staff tab has been turned on."));
				return true;

			/**
			 * Turns off staff tab
			 */
			case "taboff":
				player.send(new SendSidebarInterface(2, 29400));
				player.send(new SendOpenTab(2));
				player.send(new SendMessage("Staff tab has been turned off."));
				return true;

			case "staffyell":
				if (parser.hasNext()) {
					try {
						String message = parser.nextString();
						while (parser.hasNext()) {
							message += " " + parser.nextString();
						}
						Yelling.yell(player, message.trim(), true);
					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid yell format, syntax: -messsage"));
					}
				}
				return true;

			case "getid":
				String itemName = "";

				if (parser.hasNext())
					itemName = parser.nextString();

				while (parser.hasNext()) {
					itemName += " " + parser.nextString();
				}
				GameDefinitionLoader.getItemID(player, itemName);
				return true;
		/*
		 * Search the economy
		 */
			case "ecosearch":
				if (parser.hasNext()) {
					try {
						int id = parser.nextInt();
						long amount = 0L;
						for (Player p : World.getPlayers()) {
							if ((p != null) && (p.isActive())) {
								amount += p.getInventory().getItemAmount(id);
								amount += p.getBank().getItemAmount(id);
							}
						}
						player.getClient().queueOutgoingPacket(new SendMessage("There is currently @dre@" + Utility.format(amount) + "x @bla@of: " + Item.getDefinition(id).getName() + " in the game."));
					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
					}
				}
				return true;

			case "staffzone":
			case "staffarea":
				if (player.inWilderness()) {
					player.send(new SendMessage("You cannot teleport out of the wilderness."));
					return true;
				}
				player.teleport(PlayerConstants.STAFF_AREA);
				return true;

			case "jailarea":
				player.teleport(new Location(2767, 2795, 0));
				return true;

			case "checkinv":
				String name = "";
				if (!parser.hasNext()) {
					name = player.getUsername();
				}
				else {
					while (parser.hasNext()) {
						name += parser.nextString() + " ";
					}
				}
				try {
					stop = !stop;
					Player target = World.getPlayerByName(name.trim());
					int player2freeslots = target.getInventory().getFreeSlots();
					int player2usedslots = 28 - player2freeslots;
					if(target != player)
					player.send(new SendMessage("<col=DF7401>" + target.getUsername() + "</col> has used <col=DF7401>" + player2usedslots + " </col>slots; Free: <col=DF7401>" + player2freeslots + "</col> inventory slots."));
					Player finalTarget = target;
					TaskQueue.queue(new Task(10, true) {
						@Override
						public void execute() {
							if(!stop) {
								stop();
								return;
							}
							player.send(new SendUpdateItems(5064, finalTarget.getInventory().getItems()));
							player.send(new SendInventory(finalTarget.getInventory().getItems()));
							//player.send(new SendInventoryInterface(5292, 5063));
						}

						@Override
						public void onStop() {
						}
					});
					/*player.send(new SendMessage("Inventory contains: "));
					for (Item item : target.getInventory().getItems()) {
						if (item != null) {
							player.send(new SendMessage("<col=088a08>" + item.getAmount() + "</col><col=BDBDBD> x </col><col=088a08>" + item.getName() + " Item Value: " + item.getDefinition().getGeneralPrice()));
						}
					}*/
				}
				catch (Exception ignored) {}
				return true;

			case "checkbank":
				if (parser.hasNext()) {
					name = parser.nextString();

					while (parser.hasNext()) {
						name += parser.nextString()+ " ";
					}

					Player target = World.getPlayerByName(name.trim());

					if (target == null) {
						target = new Player();
						target.setUsername(name);
						if (!PlayerContainer.loadDetails(target)) {
							player.send(new SendMessage("The player '" + name + "' could not be found."));
							return true;
						}
					}

					player.send(new SendMessage("@blu@" + target.getUsername() + " has " + Utility.format(target.getMoneyPouch()) + " in their pouch."));
					Player finalTarget = target;
					TaskQueue.queue(new Task(8, true) {

						int counter = 0;
						@Override
						public void execute() {
								player.send(new SendUpdateItems(5064, finalTarget.getInventory().getItems()));
								player.send(new SendUpdateItems(5382, finalTarget.getBank().getItems(), finalTarget.getBank().getTabAmounts()));
								player.send(new SendInventory(finalTarget.getInventory().getItems()));
								player.send(new SendString("" + finalTarget.getBank().getTakenSlots(), 22033));
								player.send(new SendInventoryInterface(5292, 5063));

							if(!player.getInterfaceManager().hasBankOpen() && counter > 1) {
								System.out.println("Stopped task");
								stop();
								return;
							}
							counter++;
						}

						@Override
						public void onStop() {

						}
					});

				}
				return true;

		/*
		 * List of Moderator commands
		 */
			case "modcommands":
			case "modcommand":
				player.send(new SendString("Tannerscape Mod Command List", 8144));
				InterfaceHandler.writeText(new ModCommandsInterface(player));
				player.send(new SendInterface(8134));
				return true;



			/*
			 * IP Mute a player
			 */
			case "mute":
				if (parser.hasNext()) {
					try {
						name = parser.nextString();
						int hours = -1;
						if (parser.hasNext()) {
							hours = parser.nextInt();
						}
						Player target = World.getPlayerByName(name);
						boolean save = false;
						if (target == null) {
							target = new Player();
							target.setUsername(Utility.formatPlayerName(name));
							if (!PlayerDetails.loadDetails(target)) {
								player.send(new SendMessage("The player '" + Utility.formatPlayerName(name) + "' was not found."));
								return true;
							}
							save = true;
						}

						if (PlayerConstants.isOwner(target)) {
							DialogueManager.sendStatement(player, "Fuck off Pleb.");
							target.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
							return true;
						}

						String time = "permanently";

						if (hours > 0) {
							time = "for " + hours + " hour(s)";
						}

						player.send(new SendMessage("Successfully muted " + Utility.formatPlayerName(name) + " " + time + "."));
						target.setMuted(true);
						if (hours == -1) {
							target.setMuteLength(-1);
						} else {
							target.setMuteLength(System.currentTimeMillis() + hours * 3_600_000);
						}
						if (save) {
							PlayerSave.save(target);
						} else {
							DialogueManager.sendStatement(target, "You have been muted " + time + ".");
							target.send(new SendMessage("You have been muted " + time + "."));
						}
					} catch (Exception e) {
						player.send(new SendMessage("Invalid format"));
					}
				}
				return true;

			/*
			 * IP Mute a player
			 */
			case "ban":
				if (parser.hasNext()) {
					try {
						name = parser.nextString();
						int hours = -1;

						if (parser.hasNext()) {
							hours = parser.nextInt();
						}

						Player target = World.getPlayerByName(name);
						boolean save = false;
						if (target == null) {
							target = new Player();
							target.setUsername(Utility.formatPlayerName(name));
							if (!PlayerDetails.loadDetails(target)) {
								player.send(new SendMessage("The player '" + Utility.formatPlayerName(name) + "' was not found."));
								return true;
							}
							save = true;
						}

						if (PlayerConstants.isOwner(target)) {
							DialogueManager.sendStatement(player, "Fuck off Pleb.");
							target.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
							return true;
						}

						String time = "permanently";

						if (hours > 0) {
							time = "for " + hours + " hour(s)";
						}

						player.send(new SendMessage("Successfully banned " + Utility.formatPlayerName(name) + " " + time + "."));
						target.setBanned(true);
						if (hours == -1) {
							target.setBanLength(-1);
						} else {
							target.setBanLength(System.currentTimeMillis() + hours * 3_600_000);
						}
						if (save) {
							PlayerSave.save(target);
						} else {
							target.logout(true);
						}
					} catch (Exception e) {
						player.send(new SendMessage("Invalid format"));
					}
				}
				return true;

			case "jail":
				if (parser.hasNext()) {
					try {
						name = parser.nextString();
						int hours = -1;

						if (parser.hasNext()) {
							hours = parser.nextInt();
						}

						Player target = World.getPlayerByName(name);
						boolean save = false;
						if (target == null) {
							target = new Player();
							target.setUsername(Utility.formatPlayerName(name));
							if (!PlayerDetails.loadDetails(target)) {
								player.send(new SendMessage("The player '" + Utility.formatPlayerName(name) + "' was not found."));
								return true;
							}
							save = true;
						}

						if (PlayerConstants.isOwner(target)) {
							DialogueManager.sendStatement(player, "Fuck off Pleb.");
							target.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
							return true;
						}

						String time = "permanently";

						if (hours > 0) {
							time = "for " + hours + " hour(s)";
						}

						player.send(new SendMessage("Successfully jailed " + Utility.formatPlayerName(name) + " " + time + "."));
						target.setJailed(true);
						target.teleport(PlayerConstants.JAILED_AREA);
						if (hours == -1) {
							target.setJailLength(-1);
						} else {
							target.setJailLength(System.currentTimeMillis() + hours * 3_600_000);
						}
						if (save) {
							PlayerSave.save(target);
						} else {
							DialogueManager.sendStatement(target, "You have been jailed " + time + ".");
							target.send(new SendMessage("You have been jailed " + time + "."));
						}
					} catch (Exception e) {
						player.send(new SendMessage("Invalid format"));
					}
				}
				return true;

			case "unjail":
				if (parser.hasNext()) {
					try {
						name = parser.nextString();

						Player target = World.getPlayerByName(name);

						boolean save = false;
						if (target == null) {
							target = new Player();
							target.setUsername(Utility.formatPlayerName(name));
							if (!PlayerDetails.loadDetails(target)) {
								player.send(new SendMessage("The player '" + Utility.formatPlayerName(name) + "' was not found."));
								return true;
							}
							save = true;
						}

						if (PlayerSaveUtil.unJailOfflinePlayer(target.getUsername())) {
							if (target != null) {
								target.setJailed(false);
								if (save) {
									PlayerSave.save(target);
								}
							}
							player.send(new SendMessage("Success."));
						} else {
							player.send(new SendMessage("Player not found."));
						}
					} catch (Exception e) {
						player.send(new SendMessage("Invalid format"));
					}
				}
				return true;

			/*
			 * Unmute a player
			 */
			case "unmute":
				if (parser.hasNext()) {
					try {
						name = parser.nextString();

						Player target = World.getPlayerByName(name);

						boolean save = false;
						if (target == null) {
							target = new Player();
							target.setUsername(Utility.formatPlayerName(name));
							if (!PlayerDetails.loadDetails(target)) {
								player.send(new SendMessage("The player '" + Utility.formatPlayerName(name) + "' was not found."));
								return true;
							}
							save = true;
						}

						if (PlayerSaveUtil.unmuteOfflinePlayer(target.getUsername())) {
							if (target != null) {
								target.setMuted(false);
								target.setMuteLength(0);
								if (save) {
									PlayerSave.save(target);
								}
							}
							player.send(new SendMessage("Success."));
						} else {
							player.send(new SendMessage("Player not found."));
						}
					} catch (Exception e) {
						player.send(new SendMessage("Invalid format"));
					}
				}
				return true;

			/*
			 * IP Mute a player
			 */
			case "unban":
				if (parser.hasNext()) {
					try {
						name = parser.nextString();

						Player target = World.getPlayerByName(name);

						boolean save = false;
						if (target == null) {
							target = new Player();
							target.setUsername(Utility.formatPlayerName(name));
							if (!PlayerDetails.loadDetails(target)) {
								player.send(new SendMessage("The player '" + Utility.formatPlayerName(name) + "' was not found."));
								return true;
							}
							save = true;
						}

						if (PlayerSaveUtil.unbanOfflinePlayer(target.getUsername())) {
							if (target != null) {
								target.setBanned(false);
								if (save) {
									PlayerSave.save(target);
								}
							}
							player.send(new SendMessage("Success."));
						} else {
							player.send(new SendMessage("Player not found."));
						}
					} catch (Exception e) {
						player.send(new SendMessage("Invalid format"));
					}
				}
				return true;

			/*
			 * Teleport to player or teleport player to me
			 */
			case "t2":
			case "teleto":
				if (parser.hasNext()) {
					name = parser.nextString();

					while (parser.hasNext()) {
						name += " " + parser.nextString();
					}

					name = name.trim();

					Player target = World.getPlayerByName(name);

					if (target == null) {
						player.send(new SendMessage("The player \'" + name + "\' could not be found."));
						return true;
					}

					player.teleport(target.getLocation());
					player.send(new SendMessage("You have teleported to \'" + name + "\''s position."));
				}
				return true;

			case "t2m":
			case "teletome":
				if (parser.hasNext()) {
					name = parser.nextString();

					while (parser.hasNext()) {
						name += " " + parser.nextString();
					}

					name = name.trim();

					Player target = World.getPlayerByName(name);

					if (target == null) {
						player.send(new SendMessage("The player \'" + name + "\' could not be found."));
						return true;
					}

					target.teleport(player.getLocation());
					player.send(new SendMessage("You have teleported the player \'" + name + "\' to your position."));

				}
				return true;

			/*
			 * Log a specific player out
			 */
			case "kick":
				if (parser.hasNext()) {
					try {
						name = parser.nextString();

						while (parser.hasNext()) {
							name += " " + parser.nextString();
						}

						Player target = World.getPlayerByName(name);
						if (target == null) {
							player.send(new SendMessage("Player not found."));
						} else {
							if (PlayerConstants.isOwner(target)) {
								DialogueManager.sendStatement(player, "Fuck off Pleb.");
								target.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
								return true;
							}
							target.logout(true);
							player.send(new SendMessage("Kicked."));
						}
					} catch (Exception e) {
						player.send(new SendMessage("Invalid password format, syntax: ::changepass password here"));
					}
				}
				return true;

			/*
			 * Log packets for specific player
			 */
			case "logpackets":
				if (parser.hasNext()) {
					try {
						name = parser.nextString();
						Player p = World.getPlayerByName(name.replaceAll("_", " "));
						if (p == null) {
							player.send(new SendMessage("Player not found."));
						} else {
							p.getClient().setLogPlayer(true);
							player.send(new SendMessage("Now logging incoming packets for: " + p.getUsername() + "."));
						}
					} catch (Exception e) {
						player.send(new SendMessage("Invalid password format, syntax: ::changepass password here"));
					}
				}
				return true;

			/*
			 * Unlog packets for specific player
			 */
			case "unlogpackets":
				if (parser.hasNext()) {
					try {
						name = parser.nextString();
						Player p = World.getPlayerByName(name);
						if (p == null) {
							player.send(new SendMessage("Player not found."));
						} else {
							p.getClient().setLogPlayer(false);
							player.send(new SendMessage("No longer logging incoming packets for: " + p.getUsername() + "."));
						}
					} catch (Exception e) {
						player.send(new SendMessage("Invalid password format, syntax: ::changepass password here"));
					}
				}
				return true;
		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return player.getRights() >= 1 && player.getRights() < 5;
	}
}