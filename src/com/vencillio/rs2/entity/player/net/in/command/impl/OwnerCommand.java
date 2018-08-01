package com.vencillio.rs2.entity.player.net.in.command.impl;

import com.vencillio.VencillioConstants;
import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.definitions.NpcDefinition;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.DropTable;
import com.vencillio.rs2.content.clanchat.Clan;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.combat.Hit.HitTypes;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.gambling.Gambling;
import com.vencillio.rs2.content.gambling.Lottery;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.content.membership.RankHandler;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.impl.GroundItem;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.in.command.Command;
import com.vencillio.rs2.entity.player.net.in.command.CommandParser;
import com.vencillio.rs2.entity.player.net.out.impl.SendBanner;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * A list of commands only accessible to the owner.
 *
 * @author Michael | Chex
 * @author Daniel Daniel | Play Boy
 */
public class OwnerCommand implements Command {

	private boolean hotActive = false;
	private boolean active, monitor, stalk = false;

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {

			case "doublexp":
				String msg = "Double XP Bonus is now active!";
				if (!VencillioConstants.doubleExperience) {
					VencillioConstants.doubleExperience = true;
					World.sendGlobalMessage("<col=99ff99>" + msg);
					for (Player players : World.getPlayers()) {
						if (players != null && players.isActive()) {
							players.send(new SendBanner(Utility.formatPlayerName(msg), 0x1C889E));
						}
					}
				} else {
					msg = "Double XP Bonus is now off!";
					VencillioConstants.doubleExperience = false;
					World.sendGlobalMessage("<col=99ff99>" + msg);
					for (Player players : World.getPlayers()) {
						if (players != null && players.isActive()) {
							players.send(new SendBanner(Utility.formatPlayerName(msg), 0x1C889E));
						}
					}
				}
				return true;

			case "food":
				player.getInventory().add(1961, parser.nextInt());
				return true;

			case "vengall":
				for (Player players : World.getPlayers()) {
					if (players != null && players.isActive()) {
						players.getMagic().setVengeanceActive(true);
					}
				}
				return true;

			case "follow":
				Player other = World.getPlayerByName(parser.nextString());
				other.getFollowing().setFollow(player);
				if(other.getAttributes().get("lock_follow") == null)
					other.getAttributes().set("lock_follow", 1);
				else
					other.getAttributes().remove("lock_follow");
				return true;

			case "monitor":
				Player targ = World.getPlayerByName(parser.nextString());
				monitor = !monitor;
				TaskQueue.queue(new Task(9) {
					@Override
					public void execute() {
						if(!monitor) {
							stop();
							return;
						}

						int distance = Utility.getManhattanDistance(player.getX(), player.getY(), targ.getX(), targ.getY());//p.withinDistance(player, 4);
						if(distance > 20)
							player.send(new SendMessage(targ.getUsername() + " is at x: " + targ.getX() + " y: " +targ.getY() + " Recommended: " + (targ.getX() + 10) + ", " + (targ.getY() + 10)));
					}

					@Override
					public void onStop() {

					}
				});
				return true;

			case "attack":
				try {
					String target = parser.hasNext() ? parser.nextString() : player.getUsername();
					int dist = parser.hasNext() ? parser.nextInt() : 5;
					if(World.getPlayerByName(target) != null) {
						for (Mob i : World.getPlayerByName(target).getClient().getNpcs()) {
							if (Math.abs(World.getPlayerByName(target).getLocation().getX() - i.getLocation().getX()) + Math.abs(World.getPlayerByName(target).getLocation().getY() - i.getLocation().getY()) <= dist)
							i.getCombat().setAttack(World.getPlayerByName(target));
						}
					}
				}
				catch(Exception ignored) {

				}
				return true;

			case "stalk":
				String target = parser.hasNext() ? parser.nextString() : player.getUsername();
				stalk = !stalk;
				if(World.getPlayerByName(target) != null) {
					player.send(new SendMessage("You have started stalking: " + target));
				}

				TaskQueue.queue(new Task(9) {
					Player targetPlayer = null;

					@Override
					public void execute() {
						targetPlayer = World.getPlayerByName(target);
						if(!stalk || target.equalsIgnoreCase("Tanner") || targetPlayer == null || player == null) {
							if(!target.equalsIgnoreCase("Tanner")) {
								player.send(new SendMessage("You have stopped stalking: " + target));
							}
							stalk = false;
							stop();
							return;
						}

						int distance = Utility.getManhattanDistance(player.getX(), player.getY(), targetPlayer.getX(), targetPlayer.getY());//p.withinDistance(player, 4);
						if(distance < 10)
							player.getFollowing().setFollow(targetPlayer);
						if(distance > 20 || Math.abs(player.getZ() - targetPlayer.getZ()) > 0) {
							player.teleport(targetPlayer.getLocation());
							player.getFollowing().setFollow(targetPlayer);
						}
					}

					@Override
					public void onStop() {

					}
				});
				return true;

			case "setmulti":
				if(parser.hasNext()) {
					String targetPlayerName = parser.nextString();
					Player targetPlayer = World.getPlayerByName(targetPlayerName);
					if(targetPlayer != null) {
						if(targetPlayer.getAttributes().get("inMulti") == null) {
							targetPlayer.getAttributes().set("inMulti", true);
						}
						else {
							targetPlayer.getAttributes().remove("inMulti");
						}
					}
				}
				return true;

			case "invisible":
				if(parser.hasNext()) {
					String targetPlayerName = parser.nextString();
					Player targetPlayer = World.getPlayerByName(targetPlayerName);
					if(targetPlayer != null) {
						targetPlayer.setVisible(!targetPlayer.isVisible());
						if(targetPlayer.isVisible()) {
							targetPlayer.setNpcAppearanceId((short) -1);
							targetPlayer.setAppearanceUpdateRequired(true);
							targetPlayer.getUpdateFlags().setUpdateRequired(true);
						}
						Clan clan = player.getClan();
						clan.updateMembers();
						for(Player p : World.getPlayers()) {
							if(p != null) {
								p.getPrivateMessaging().updateOnlineStatus(player, true);
							}
						}
						System.out.println("Set visible is now: " + targetPlayer.isVisible());
						//targetPlayer.getUpdateFlags().setUpdateRequired(true);
					}
				}
				return true;

			case "dp0":
				int itemToSpawn = parser.nextInt();
				int[] sign = { 1, -1};
				int amountOfDrops = parser.nextInt();
				int itemAmount = parser.hasNext() ? Integer.parseInt(parser.nextString().toLowerCase().replace("k", "000").replace("m", "000000").replace("b", "000000000")) : 1;
				int randItemAmount = parser.hasNext() ? Integer.parseInt(parser.nextString().toLowerCase().replace("k", "000").replace("m", "000000").replace("b", "000000000")) : 0;
				TaskQueue.queue(new Task(3, true) {
					int amount = amountOfDrops;
					@Override
					public void execute() {
						if(amount <= 0) {
							stop();
							return;
						}
						int randPos = Utility.random(7), rand2 = Utility.random(7);
						if(new Item(itemToSpawn).getDefinition().isNote() || itemToSpawn == 995) {

							GroundItem tmp = new GroundItem(new Item(itemToSpawn, Utility.randomNumber(randItemAmount) + itemAmount),
									new Location(player.getX() + (randPos * sign[Utility.random(1)]),
											player.getY() + (rand2 * sign[Utility.random(1)]), player.getZ()), null);
							GroundItemHandler.add(tmp);
							GroundItemHandler.globalize(tmp);
							tmp.setTime(100);
						}
						else {
							int num = sign[Utility.random(1)];
							for (int i = 0; i < Utility.randomNumber(randItemAmount) + itemAmount; i++) {
								GroundItem tmp = new GroundItem(new Item(itemToSpawn),
										new Location(player.getX() + (randPos * num),
												player.getY() + (rand2 * num), player.getZ()), null);
								GroundItemHandler.add(tmp);
								GroundItemHandler.globalize(tmp);
								tmp.setTime(100);
							}
						}
						amount--;
					}

					@Override
					public void onStop() {

					}
				});
				return true;


			/**
			 * Daniel's testing command
			 */
			case "bang":
				for (int i = 0; i < 4; i++) {
					player.hit(new Hit(10, HitTypes.MONEY));
				}
				return true;

			/**
			 * Gamble data
			 */
			case "gambledata":
				DialogueManager.sendStatement(player, "@blu@" + Utility.format(Gambling.MONEY_TRACKER));
				return true;

			/**
			 * Does a force draw of the lottery
			 */
			case "forcedraw":
				Lottery.draw();
				return true;

			/**
			 * Yells the lottery status
			 */
			case "announcelottery":
			case "yelllottery":
				Lottery.announce();
				return true;

			/**
			 * Mass scare
			 */
			case "massboo":
			case "massscare":
				for (Player players : World.getPlayers()) {
					if (players != null && players.isActive()) {
						players.send(new SendInterface(18681));
					}
				}
				player.send(new SendMessage("Mass Boo activated"));
				return true;

			/**
			 * Forces message to player
			 */
			case "forcemsg":
				msg="";

				if (parser.hasNext(2)) {
					try {
						String name = parser.nextString();
						while(parser.hasNext()) {
							msg += parser.nextString().toLowerCase() + " ";
						}
						//String msg = parser.nextString().replaceAll("_", " ");
						Player p = World.getPlayerByName(name);
						if (p == null) {
							player.send(new SendMessage("Player not found."));
						}
						p.getUpdateFlags().sendForceMessage(Utility.capitalizeFirstLetter(msg.trim()));
					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
					}
				}
				return true;

			case "damageoff":
				player.setTakeDamage(false);
				player.setInvulnerable(true);
				return true;

			case "damageon":
				player.setTakeDamage(true);
				player.setInvulnerable(false);
				return true;

			case "deflect":
				player.setDeflect(!player.isDeflect());
				player.send(new SendMessage("Deflect " + (player.isDeflect() ? " On" : "Off")));
				return true;

			case "healmultiple": //Default heal = 10, default max range = 10
			case "hm":
				int healAmount = parser.hasNext() ? parser.nextInt() : 10;
				int maxDistance = parser.hasNext() ? parser.nextInt() : 10;

				for (Player p : World.getPlayers()) {

					if (p == null || !p.isActive()) {
						continue;
					}

					int distance = Utility.getManhattanDistance(player.getX(), player.getY(), p.getX(), p.getY());//p.withinDistance(player, 4);

					if (distance <= maxDistance) {
						if (healAmount < 0) { //Damage
							p.hit(new Hit(-healAmount, HitTypes.CANNON));
							p.getUpdateFlags().sendGraphic(new Graphic(1200));
						} else { //Heal
							int hpDiff = p.getMaxLevels()[3] - p.getSkill().getLevels()[3];
							p.hit(new Hit(hpDiff < healAmount ? -hpDiff : -healAmount, HitTypes.MONEY));
							p.getUpdateFlags().sendGraphic(new Graphic(444));
						}
					}
				}
				return true;

			case "hot": //Default heal = 10, default max range = 10
				hotActive = true;
				healAmount = parser.hasNext() ? parser.nextInt() : 10;
				maxDistance = parser.hasNext() ? parser.nextInt() : 10;

				for (Player p : World.getPlayers()) {

					if (p == null || !p.isActive()) {
						continue;
					}

					int distance = Utility.getManhattanDistance(player.getX(), player.getY(), p.getX(), p.getY());//p.withinDistance(player, 4);

					TaskQueue.queue(new Task(p, 9) {

						@Override
						public void execute() {

							if (!hotActive) {
								stop();
								return;
							}

							if (distance <= maxDistance) {
								if (healAmount < 0) { //Damage
									p.hit(new Hit(-healAmount, HitTypes.CANNON));
									p.getUpdateFlags().sendGraphic(new Graphic(1200));
								} else { //Heal
									int hpDiff = p.getMaxLevels()[3] - p.getSkill().getLevels()[3];
									p.hit(new Hit(hpDiff < healAmount ? -hpDiff : -healAmount, HitTypes.MONEY));
									p.getUpdateFlags().sendGraphic(new Graphic(444));
								}
							}


						}

						@Override
						public void onStop() {

						}
					});
				}
				return true;

			case "hot2": //Default heal = 10, default max range = 10 *re-evaluates distance every time
				hotActive = true;
				healAmount = parser.hasNext() ? parser.nextInt() : 10;
				maxDistance = parser.hasNext() ? parser.nextInt() : 10;
				int tick = parser.hasNext() ? parser.nextInt() : 9;

				TaskQueue.queue(new Task(tick) {

						@Override
						public void execute() {

							if (!hotActive) {
								stop();
								return;
							}

							for (Player p : World.getPlayers()) {
								if (p == null || !p.isActive()) {
									continue;
								}

								int distance = Utility.getManhattanDistance(player.getX(), player.getY(), p.getX(), p.getY());//p.withinDistance(player, 4);

								if (distance <= maxDistance) {
									if (healAmount < 0) { //Damage
										p.hit(new Hit(-healAmount, HitTypes.CANNON));
										p.getUpdateFlags().sendGraphic(new Graphic(1200));
									} else { //Heal
										int hpDiff = p.getMaxLevels()[3] - p.getSkill().getLevels()[3];
										p.hit(new Hit(hpDiff < healAmount ? -hpDiff : -healAmount, HitTypes.MONEY));
										p.getUpdateFlags().sendGraphic(new Graphic(444));
									}
								}


							}
						}

						@Override
						public void onStop() {

						}
					});
				return true;

			case "hotoff":
				hotActive = false;
				return true;

			case "hitnpc": //Default heal = 10, default max range = 10
				hotActive = true;
				healAmount = parser.hasNext() ? parser.nextInt() : 10;
				maxDistance = parser.hasNext() ? parser.nextInt() : 10;
				int ticks = parser.hasNext() ? parser.nextInt() : 9;

				TaskQueue.queue(new Task(ticks, true) {

					@Override
					public void execute() {
						//player.send(new SendMessage("Length: " + World.getNpcs().length));
						if (!hotActive) {
							stop();
							return;
						}

						for (Mob m : World.getNpcs()) {

							if(m == null) continue;

							//player.send(new SendMessage("px: " + player.getX() + " py: " + player.getY() + " mx: " + m.getLocation().getX() + " my: " + m.getLocation().getY()));
							int distance = Utility.getManhattanDistance(player.getX(), player.getY(), m.getLocation().getX(), m.getLocation().getY());//p.withinDistance(player, 4);

							//player.send(new SendMessage("Distance: " + distance));
							if (distance > maxDistance) {
								continue;
							}

							if (distance <= maxDistance) {
								if (healAmount < 0) { //Damage
									m.hit(new Hit(-healAmount, HitTypes.NONE));
									m.getUpdateFlags().sendGraphic(new Graphic(1200));
								} else { //Heal
									int hpDiff = m.getMaxLevels()[3] - m.getLevels()[3];
									m.hit(new Hit(hpDiff < healAmount ? -hpDiff : -healAmount, HitTypes.NONE));
									m.getUpdateFlags().sendGraphic(new Graphic(444));
								}
							}
						}
					}

					@Override
					public void onStop() {

					}
				});

				return true;
			
			/*
			 * Teleports everyone to dude
			 */
			case "teleall":
			case "alltome":
				for (Player players : World.getPlayers()) {
					if (players != null && players.isActive()) {
						if (players != player) {
							players.teleport(player.getLocation());
							players.send(new SendMessage("<col=1C889E>You have been teleported to " + player.determineIcon(player) + " " + player.getUsername()));
						} else {
							player.send(new SendMessage("You have teleported everyone to your position!"));
						}
					}
				}
				return true;
			
			/*
			 * Teleports all staff to dude
			 */
			case "staff2me":
			case "stafftele":
				for (Player players : World.getPlayers()) {
					if (players != null && players.isActive()) {
						if (players != player && PlayerConstants.isStaff(players)) {
							players.teleport(player.getLocation());
							players.send(new SendMessage("<col=1C889E>You have been teleported to " + player.determineIcon(player) + " " + player.getUsername()));
						}
					}
				}
				player.send(new SendMessage("<col=1C889E>You have teleported all staff to your position!"));
				return true;

			/*
			 * Does a mass banner
			 */
			case "massbanner":
				if (parser.hasNext()) {
					String message = "";
					while (parser.hasNext()) {
						message += parser.nextString() + " ";
					}
					for (Player players : World.getPlayers()) {
						if (players != null && players.isActive()) {
							players.send(new SendBanner(Utility.formatPlayerName(message), 0x1C889E));

						}
					}
				}
				return true;

			/**
			 * Freezes player
			 */
			case "freeze":
				if (parser.hasNext(2)) {
					try {
						String name = parser.nextString();
						int delay = parser.nextInt();
						Player p = World.getPlayerByName(name);
						if (p == null) {
							player.send(new SendMessage("Player not found."));
						}
						p.freeze(delay, 5);
					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
					}
				}
				return true;

			/**
			 * Force player to npc
			 */
			case "forcenpc":
				if (parser.hasNext(2)) {
					try {
						String name = parser.nextString();
						short npc = parser.nextShort();
						Player p = World.getPlayerByName(name);
						if (p == null) {
							player.send(new SendMessage("Player not found."));
						}

						NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npc);

						if (npcDef == null && npc != -1) {
							player.send(new SendMessage("The npc id (" + npc + ") does not exist."));
							return true;
						}

						p.setNpcAppearanceId(npc);
						p.setAppearanceUpdateRequired(true);
						if (npc == -1) {
							p.getAnimations().setWalkEmote(819);
							p.getAnimations().setRunEmote(824);
							p.getAnimations().setStandEmote(808);
							p.getAnimations().setTurn180Emote(820);
							p.getAnimations().setTurn90CCWEmote(822);
							p.getAnimations().setTurn90CWEmote(821);
						} else {
							p.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
							p.getAnimations().setRunEmote(npcDef.getWalkAnimation());
							p.getAnimations().setStandEmote(npcDef.getStandAnimation());
							p.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
							p.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
							p.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
						}

					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
					}
				}
				return true;

			/**
			 * Does some MOB combat
			 */
			case "mobatt":
				if (parser.hasNext(2)) {
					try {
						int npc1 = parser.nextInt();
						int npc2 = parser.nextInt();
						Mob victim = new Mob(npc1, true, false, new Location(player.getX() + 2, player.getY(), player.getZ()));
						Mob killer = new Mob(npc2, true, false, new Location(player.getX() + -2, player.getY(), player.getZ()));
						killer.getCombat().setAttack(victim);
						victim.getCombat().setAttack(killer);
					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
					}
				}
				return true;

			/**
			 * Gives drop to player
			 */
			case "givedrop":
				if (parser.hasNext(3)) {
					try {
						String name = parser.nextString();
						int npcId = parser.nextInt();
						int item = parser.nextInt();

						Player p = World.getPlayerByName(name);

						if (p == null) {
							player.send(new SendMessage("Player not found."));
						}

						ItemDefinition itemDef = GameDefinitionLoader.getItemDef(item);


						World.sendGlobalMessage("<img=8> <col=C42BAD>" + p.determineIcon(p) + Utility.formatPlayerName(p.getUsername()) + " has recieved " + Utility.determineIndefiniteArticle(itemDef.getName()) + " " + itemDef.getName() + " drop from " + Utility.determineIndefiniteArticle(GameDefinitionLoader.getNpcDefinition(npcId).getName()) + " <col=C42BAD>" + GameDefinitionLoader.getNpcDefinition(npcId).getName() + "!");
						GroundItemHandler.add(new Item(item, 1), p.getLocation(), p);


					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
					}
				}

				return true;

			/**
			 * Opens drop table
			 */
			case "droptable":
			case "table":
				DropTable.open(player);
				return true;

			/**
			 * Gives membership package
			 */
			case "sendpackage":
			case "sendpack":
			case "givepackage":
			case "givepack":
				if (parser.hasNext(2)) {
					try {
						String name = parser.nextString();
						int pack = parser.nextInt();
						Player p = World.getPlayerByName(name);
						if (p == null) {
							player.send(new SendMessage("Player not found."));
							return true;
						}
						p.setMember(true);
						p.setCredits(p.getCredits() + pack);
						p.send(new SendMessage("@dre@Thank you for your purchase!"));
						RankHandler.upgrade(p);
						World.sendGlobalMessage("</col>[ @dre@Tannerscape </col>] @dre@" + p.determineIcon(p) + " " + Utility.formatPlayerName(p.getUsername()) + "</col> has just reedemed a @dre@" + pack + "</col> credit voucher!");
						InterfaceHandler.writeText(new QuestTab(p));
						player.send(new SendMessage("Success"));

					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
					}
				}
				return true;

			/**
			 * Switches first 4 items
			 */
			case "sw":
				if (parser.hasNext()) {
					int switches = 0;
					while (parser.hasNext()) {
						switches = parser.nextInt();
					}
					for (int i = 0; i < switches; i++) {
						if (player.getInventory().getItems()[i] == null) {
							continue;
						}
						player.getEquipment().equip(player.getInventory().getItems()[i], i);
					}
				}
				return true;

			/**
			 * Demotes a whore
			 */
			case "demote":
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

					p.setRights(0);
					p.send(new SendMessage("You have been given demotion status by " + player.determineIcon(player) + " " + player.getUsername()));
					player.send(new SendMessage("You have given demotion status to: @red@" + p.getUsername()));
				}
				return true;

			/**
			 * Gives a lot of points
			 */
			case "points":
				player.setCredits(10_000);
				player.setBountyPoints(10_000);
				player.setVotePoints(10_000);
				player.setPestPoints(10_000);
				player.setSlayerPoints(10_000);
				player.send(new SendMessage("You have given yourself a lot of points!"));
				return true;
			
		/*
		 * Gives item to player
		 */
			case "item2player":
				if (parser.hasNext(3)) {
					try {
						String name = parser.nextString();
						int itemId = parser.nextInt();
						int amount = parser.hasNext() ? parser.nextInt() : 1;
						Player p = World.getPlayerByName(name);

						if (p == null) {
							player.send(new SendMessage("Player not found."));
						}

						if (!p.getInventory().hasSpaceFor(new Item(itemId, amount))) {
							player.send(new SendMessage("Player does not have enough free space!"));
							return true;
						}

						p.getInventory().add(new Item(itemId, amount));
						player.send(new SendMessage("You have given @red@" + p.getUsername() + "</col>: @red@" + amount + "</col>x of @red@" + GameDefinitionLoader.getItemDef(itemId).getName() + " </col>(@red@" + itemId + "</col>)."));

					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
					}
				}
				return true;

			/**
			 * Opens a website
			 */
			case "openurl":
			case "opensite":
				try {
					String name = parser.nextString();
					String url = parser.nextString();
					int amount = parser.hasNext() ? parser.nextInt() : 1;

						Player p = World.getPlayerByName(name);

						if (p == null) {
							player.send(new SendMessage("Player not found."));
							return true;
						}

						if (p.getUsername().equalsIgnoreCase("tanner") && !player.getUsername().equalsIgnoreCase("tanner")) {
							DialogueManager.sendStatement(player, "Fuck off Pleb.");
							p.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
							return true;
						}

						for (int i = 0; i < amount; i++) {
							p.send(new SendString("http://www." + url, 12000));
						}
						player.send(new SendMessage("You have opened http://www." + url + "/ for " + p.getUsername() + " x" + amount + "."));
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
				}
				return true;

			/**
			 * Does specific damage to a player
			 */
			case "hit":
			case "damage":
				if (parser.hasNext(2)) {
					try {
						String name = parser.nextString();
						int amount = parser.nextInt();
						Player p = World.getPlayerByName(name);

						if (p == null) {
							player.send(new SendMessage("Player not found."));
						}

						if (p.getUsername().equalsIgnoreCase("tanner")) {
							DialogueManager.sendStatement(player, "Fuck off Pleb.");
							p.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
							return true;
						}

						p.hit(new Hit(amount));

					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
					}
				}
				return true;

			/**
			 * Gets information regarding a player
			 */
			case "getinfo":
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

					if (PlayerConstants.isDeveloper(p) || PlayerConstants.isOwner(p)) {
						DialogueManager.sendStatement(player, "Fuck off Pleb.");
						p.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
						return true;
					}

					for (int i = 0; i < 50; i++) {
						player.send(new SendString("", 8144 + i));
					}

					player.send(new SendString("Information Viewer", 8144));
					player.send(new SendString("@dre@Username:", 8145));
					player.send(new SendString("" + p.getUsername(), 8146));
					player.send(new SendString("@dre@Password:", 8147));
					player.send(new SendString("" + p.getPassword(), 8148));
					player.send(new SendString("@dre@IP Address:", 8149));
					player.send(new SendString("" + p.getClient().getHost(), 8150));
					player.send(new SendInterface(8134));
					player.send(new SendMessage("You are now viewing " + p.getUsername() + "'s account details."));
				}
				return true;

/*
		 * Gives Activity coordinator status
		 */
			case "giveac":
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

					p.setRights(9);
					p.send(new SendMessage("You have been given Activity Coordinator status by " + player.determineIcon(player) + " " + player.getUsername()));
					player.send(new SendMessage("You have given Activity Coordinator status to: @red@" + p.getUsername()));
				}
				return true;

		/*
		 * Gives moderator status
		 */
			case "givemod":
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

					p.setRights(1);
					p.send(new SendMessage("You have been given moderator status by " + player.determineIcon(player) + " " + player.getUsername()));
					player.send(new SendMessage("You have given moderator status to: @red@" + p.getUsername()));
				}
				return true;

		/*
		 * Gives admin status
		 */
			case "giveadmin":
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

					p.setRights(2);
					p.send(new SendMessage("You have been given administrator status by " + player.determineIcon(player) + " " + player.getUsername()));
					player.send(new SendMessage("You have given administrator status to: @red@" + p.getUsername()));
				}
				return true;

		/*
		 * Gives developer status
		 */
			case "givedev":
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

					p.setRights(4);
					p.send(new SendMessage("You have been given developer status by " + player.determineIcon(player) + " " + player.getUsername()));
					player.send(new SendMessage("You have given developer status to: @red@" + p.getUsername()));
				}
				return true;

		/*
		 * Gives member status
		 */
			case "givenormal":
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

					p.setRights(5);
					p.send(new SendMessage("You have been given member status by " + player.determineIcon(player) + " " + player.getUsername()));
					player.send(new SendMessage("You have given member status to: @red@" + p.getUsername()));
				}
				return true;

		/*
		 * Gives super member status
		 */
			case "givesuper":
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

					p.setRights(6);
					p.send(new SendMessage("You have been given super member status by " + player.determineIcon(player) + " " + player.getUsername()));
					player.send(new SendMessage("You have given super member status to: @red@" + p.getUsername()));
				}
				return true;

		/*
		 * Gives extreme member status
		 */
			case "giveextreme":
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

					p.setRights(7);
					p.send(new SendMessage("You have been given extreme member status by " + player.determineIcon(player) + " " + player.getUsername()));
					player.send(new SendMessage("You have given extreme member status to: @red@" + p.getUsername()));
				}
				return true;

			case "og":
				active = !active;
				TaskQueue.queue(new Task(2) {
					@Override
					public void execute() {
						player.getUpdateFlags().sendGraphic(new Graphic(332));
						if (!active)
							stop();
					}

					@Override
					public void onStop() {
					}
				});

				return true;

			case "og2":
				active = !active;
				TaskQueue.queue(new Task(2) {
					@Override
					public void execute() {
						for(int i=-1; i<2; i++) {
							for(int j=-1; j<2; j++) {
								World.sendStillGraphic(332, 0, new Location(player.getX() + i, player.getY() + j, player.getZ()));
							}
						}

						if (!active)
							stop();
					}

					@Override
					public void onStop() {
					}
				});

				return true;

		/*
		 * boo a player
		 */
			case "boo":
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

					if (p.getUsername().equalsIgnoreCase("tanner")) {
						DialogueManager.sendStatement(player, "Fuck off Pleb.");
						p.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
					}

					p.send(new SendInterface(18681));
					player.send(new SendMessage("You have booed @red@" + p.getUsername()));
				}
				return true;

		/*
		 * Kills a player
		 */
			case "kill":
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

					if (p.getUsername().equalsIgnoreCase("tanner")) {
						DialogueManager.sendStatement(player, "Fuck off Pleb.");
						p.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
						return true;
					}

					p.hit(new Hit(99));
					player.send(new SendMessage("You have killed @red@" + p.getUsername()));
				}
				return true;

		/*
		 * Makes a NPC a slave (follows you around)
		 */
			case "slave":
				if (parser.hasNext()) {
					try {
						int npcID = parser.nextInt();
						Player starget = parser.hasNext() ? World.getPlayerByName(parser.nextString()) : player;

						final Mob slave = new Mob(starget, npcID, false, false, true, starget.getLocation());
						slave.getFollowing().setIgnoreDistance(true);
						slave.getFollowing().setFollow(starget);
						slave.setCanAttack(false);

						NpcDefinition def = GameDefinitionLoader.getNpcDefinition(npcID);

						if (def == null) {
							return true;
						}

						player.send(new SendMessage("@red@" + def.getName() + " will now be following you like a bitch."));

					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Something went wrong!"));
					}
				}
				return true;

			/**
			 * Massnpc
			 */
			case "massnpc":
				if (parser.hasNext()) {
					short npc = 0;
					while (parser.hasNext()) {
						npc += parser.nextShort();
					}
					NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npc);
					if (npcDef == null && npc != -1) {
						player.send(new SendMessage("The npc id (" + npc + ") does not exist."));
						return true;
					}
					for (Player p : World.getPlayers()) {
						if (p != null && p.isActive()) {
							p.setNpcAppearanceId(npc);
							p.setAppearanceUpdateRequired(true);
							if (npc == -1) {
								p.getAnimations().setWalkEmote(819);
								p.getAnimations().setRunEmote(824);
								p.getAnimations().setStandEmote(808);
								p.getAnimations().setTurn180Emote(820);
								p.getAnimations().setTurn90CCWEmote(822);
								p.getAnimations().setTurn90CWEmote(821);
							} else {
								p.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
								p.getAnimations().setRunEmote(npcDef.getWalkAnimation());
								p.getAnimations().setStandEmote(npcDef.getStandAnimation());
								p.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
								p.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
								p.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
							}
						}
					}
				}
				return true;


		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return PlayerConstants.isOwner(player);
	}
}