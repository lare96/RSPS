package com.vencillio.rs2.entity.player.net.in.command.impl;

import com.everythingrs.donate.Donation;
import com.motiservice.Motivote;
import com.vencillio.VencillioConstants;
import com.vencillio.core.definitions.NpcCombatDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.PlayersOnline;
import com.vencillio.rs2.content.Yelling;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.OptionDialogue;
import com.vencillio.rs2.content.dialogue.impl.ChangePasswordDialogue;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.CommandInterface;
import com.vencillio.rs2.content.interfaces.impl.TrainingInterface;
import com.vencillio.rs2.content.profiles.PlayerProfiler;
import com.vencillio.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.vencillio.rs2.content.vencilliobot.VencillioBot;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.in.command.Command;
import com.vencillio.rs2.entity.player.net.in.command.CommandParser;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A list of commands accessible to all players disregarding rank.
 * 
 * @author Michael | Chex
 */
public class PlayerCommand implements Command {

	private final static Motivote motivote = new Motivote("rennatscape", "d65e6c6fc6ef5e51c2866a776f25da59");

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {
		
		/*
		 * Claim votes
		 */
		/*case "redeem":
		case "claimvote":
		case "claimvotes":
			//VoteUpdater.update(player);
			String auth = parser.nextString();
			Result r1 = motivote.redeem(SearchField.AUTH_CODE, auth);
			if(r1.success()) {
				player.setVotePoints(player.getVotePoints() + 1);
				player.getInventory().add(995, 1_000_000);
				player.send(new SendMessage("Successfully redeemed. Thanks for voting!"));
				VencillioConstants.LAST_VOTER = player.getUsername();
				VencillioConstants.CURRENT_VOTES++;
				AchievementHandler.activateAchievement(player, AchievementList.VOTE_5_TIMES, 1);
				AchievementHandler.activateAchievement(player, AchievementList.VOTE_15_TIMES, 1);
				AchievementHandler.activateAchievement(player, AchievementList.VOTE_30_TIMES, 1);
			}
			else {
				player.send(new SendMessage("Redemption unsuccessful"));
			}
			return true;

			case "redeemuser":
				auth = parser.nextString();
				r1 = motivote.redeem(SearchField.USER_NAME, auth);
				if(r1.success()) {
					for(int i = 0; i<r1.votes().size(); i++) {
						player.setVotePoints(player.getVotePoints() + 1);
						player.getInventory().add(995, 1_000_000);
					}
					AchievementHandler.activateAchievement(player, AchievementList.VOTE_5_TIMES, r1.votes().size());
					AchievementHandler.activateAchievement(player, AchievementList.VOTE_15_TIMES, r1.votes().size());
					AchievementHandler.activateAchievement(player, AchievementList.VOTE_30_TIMES, r1.votes().size());
					VencillioConstants.LAST_VOTER = player.getUsername();
					VencillioConstants.CURRENT_VOTES++;
					player.send(new SendMessage("Successfully redeemed x" + r1.votes().size() +  ". Thanks for voting!"));
				}
				else {
					player.send(new SendMessage("Redemption unsuccessful"));
				}
				return true;*/

			case "npcinfo":
				if(parser.hasNext(1)) {
					NpcCombatDefinition def = GameDefinitionLoader.getNpcCombatDefinition(parser.nextInt());
					NpcCombatDefinition.Skill[] skills = def.getSkills();
					int hp = 0;
					for (int i = 0; i<skills.length; i++) {
						System.out.println("id: " + skills[i].getId() + " lvl: " + skills[i].getLevel());
						if (skills[i].getId() == 3) {
							System.out.println("Stored hp: " + skills[i].getLevel());
							hp = skills[i].getLevel();
						}
					}
					player.send(new SendMessage("Hp of mob is: " + hp));
				}
				else
					player.send(new SendMessage("Invalid"));
				return true;

			case "reward":
				if (!parser.hasNext(1)) {
					player.send(new SendMessage("Please use [::reward id], [::reward id amount], or [::reward id all]."));
					return true;
				}
				final String playerName = player.getUsername();
				final String id = parser.nextString();
				final String rewardAmount = parser.hasNext(1) ? parser.nextString() : "1";

				com.everythingrs.vote.Vote.service.execute(() -> {
					try {
						com.everythingrs.vote.Vote[] reward = com.everythingrs.vote.Vote.reward("mjijehoz8vrj046m7remte29z1x6ynyo7mc3vh4wfqpbke29btmpjp8709loo4b348svcs1yvi", playerName, id, rewardAmount);
						if (reward[0].message != null) {
							player.send(new SendMessage(reward[0].message));
							return;
						}
						player.getInventory().add(new Item(reward[0].reward_id, reward[0].give_amount));
						player.setVotePoints(player.getVotePoints() + reward[0].give_amount);
						VencillioConstants.LAST_VOTER = player.getUsername();
						VencillioConstants.CURRENT_VOTES += reward[0].give_amount;
						player.send(new SendMessage("Thank you for voting! You now have " + reward[0].vote_points + " vote points."));
					} catch (Exception e) {
						player.send(new SendMessage("Api Services are currently offline. Please check back shortly"));
						e.printStackTrace();
					}
				});
				return true;

			case "claim":
				new Thread(() -> {
					try {
						Donation[] donations = Donation.donations("mjijehoz8vrj046m7remte29z1x6ynyo7mc3vh4wfqpbke29btmpjp8709loo4b348svcs1yvi",
								player.getUsername());
						if (donations.length == 0) {
							player.send(new SendMessage("You currently don't have any items waiting. You must donate first!"));
							return;
						}
						if (donations[0].message != null) {
							player.send(new SendMessage(donations[0].message));
							return;
						}
						for (Donation donate : donations) {
							player.getInventory().add(new Item(donate.product_id, donate.product_amount));
						}
						player.send(new SendMessage("Thank you for donating!"));
					} catch (Exception e) {
						player.send(new SendMessage("Api Services are currently offline. Please check back shortly"));
						e.printStackTrace();
					}
				}).start();
				return true;

		/*
		 * Opens the command list
		 */
			case "command":
			case "commands":
			case "commandlist":
			case "commandslist":
				player.send(new SendString("Tannerscape Command List", 8144));
				InterfaceHandler.writeText(new CommandInterface(player));
				player.send(new SendInterface(8134));
				return true;

			case "wealth":
				double total = 0;
				for (Item item : player.getInventory().getItems()) {
					if (item != null) {
						int itemAmount = item.getAmount();
//						System.out.println(item.getName() + " : " + item.getDefinition().getGeneralPrice()+ " : "+itemAmount);
						total += (long) item.getDefinition().getGeneralPrice() * (long) itemAmount;
					}
				}

				for (Item item : player.getBank().getItems()) {
					if (item != null) {
						int itemAmount = item.getAmount();
//						System.out.println(item.getName() + " : " + item.getDefinition().getGeneralPrice()+ " : "+itemAmount +
//						" : " + (item.getDefinition().getGeneralPrice() * itemAmount));

						total += (long) item.getDefinition().getGeneralPrice() * (long) itemAmount;
					}
				}
				for (Item item : player.getEquipment().getItems()) {
					if (item != null) {
						int itemAmount = item.getAmount();
//						System.out.println(item.getName() + " : " + item.getDefinition().getGeneralPrice() + " : " + itemAmount);
						total += (long) item.getDefinition().getGeneralPrice() * (long) itemAmount;
					}
				}
				total += player.getMoneyPouch();
				DecimalFormat numberFormat = new DecimalFormat("#.00");
				numberFormat.setRoundingMode(RoundingMode.DOWN);
				String str;
				if (total > 1000000000000000000L) {
					str = numberFormat.format( total / 1000000000000000000L) + "Q";
				}
				else if (total > 1000000000000000L) {
					str = numberFormat.format( total / 1000000000000000L) + "q";
				}
				else if (total >= 1000000000000L) {
					str = numberFormat.format( total / 1000000000000L) + "T";
				} else if (total >= 1000000000L) {
					str = numberFormat.format(total / 1000000000) + "B";
				} else if (total >= 1000000L) {
					str = numberFormat.format(total / 1000000) + "M";
				} else
					str = "";

				player.getUpdateFlags().sendForceMessage("<img=3>My total wealth is " + NumberFormat.getNumberInstance(Locale.US).format(total) + " (" + str + ")");
				return true;

			case "playtime":
				try {
					Player person;

					if (parser.hasNext())
						person = World.getPlayerByName(parser.nextString().trim());
					else
						person = player;

					int totalTime = person.getPlayPoints();
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
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				return true;

			case "setmode":
				if (parser.hasNext()) {
					String choice = parser.nextString().trim();
					if(Integer.parseInt(choice) >=0 && Integer.parseInt(choice) <4)
						player.mode = Integer.parseInt(choice);
				}
				return true;

			case "mymode":
				if (player.mode == 0) {
					player.getUpdateFlags().sendForceMessage("<col=0099ff><img=3>I'm playing on game mode: Easy");
				} else if (player.mode == 1) {
					player.getUpdateFlags().sendForceMessage("<col=0000ff><img=3>I'm playing on game mode: Intermediate");
				} else if (player.mode == 2) {
					player.getUpdateFlags().sendForceMessage("<col=3399ff><img=3>I'm playing on game mode: Hard");
				} else if (player.mode == 3) {
					player.getUpdateFlags().sendForceMessage("<col=ff6600><img=3>I'm playing on game mode: Ultimate");
				}
				return true;

		/*
		 * Opens the teleporting interface
		 */
		case "teleport":
		case "teleports":
		case "teleporting":
		case "teleportings":
			InterfaceHandler.writeText(new TrainingInterface(player));
			player.send(new SendInterface(61000));
			player.send(new SendString("Selected: @red@None", 61031));
			player.send(new SendString("Cost: @red@Free", 61032));
			player.send(new SendString("Requirement: @red@None", 61033));
			player.send(new SendString("Other: @red@None", 61034));
			return true;

		/*
		 * Answers TriviaBot
		 */
		case "answer":
			if (parser.hasNext()) {
				String answer = "";
				while (parser.hasNext()) {
					answer += parser.nextString() + " ";
				}
				VencillioBot.answer(player, answer.trim());
			}
			return true;
			
		case "triviasetting":
		case "triviasettings":
			
			player.start(new OptionDialogue("Turn on TriviaBot", p -> {
				p.setWantTrivia(true);
				p.send(new SendMessage("<col=482CB8>You have turned on the TriviaBot."));
				player.send(new SendRemoveInterfaces());
			}, "Turn off TriviaBot", p -> {
				p.setWantTrivia(false);
				p.send(new SendMessage("<col=482CB8>You have turned off the TriviaBot."));
				player.send(new SendRemoveInterfaces());
			}, "Turn on TriviaBot notification", p -> {
				p.setTriviaNotification(true);
				p.send(new SendMessage("<col=482CB8>You have turned on the TriviaBot notification."));
				player.send(new SendRemoveInterfaces());
			}, "Turn off TriviaBot notification", p -> {
				p.setTriviaNotification(false);
				p.send(new SendMessage("<col=482CB8>You have turned off the TriviaBot notification."));
				player.send(new SendRemoveInterfaces());
			}));		
			return true;

		/*
		 * Gets amount of online players
		 */
		case "players":
			player.send(new SendMessage("There are currently @red@" + Utility.format(World.getActivePlayers()) + "</col> players online."));
			PlayersOnline.showPlayers(player, p -> true);
			return true;

		/*
		 * Opens donation page
		 */
		case "donate":
		case "donation":
		case "donating":
		case "store":
		case "credits":
			player.send(new SendString("http://www.bit.ly/2FFVgKj", 12000));
			player.send(new SendMessage("Loading donation page..."));
			return true;

		/*
		 * Opens website page
		 */
		case "forum":
		case "forums":
		case "website":
			player.send(new SendString("http://www.rennatscape.proboards.com/", 12000));
			player.send(new SendMessage("Loading website page..."));
			return true;

		/*
		 * Opens voting page
		 */
		case "vote":
		case "voting":
			player.send(new SendString("http://www.rennatscape.proboards.com/page/voting", 12000));
			player.send(new SendMessage("Loading voting page..."));
			return true;

		/*
		 * Finds player to view profile
		 */
		case "find":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.trim();

				PlayerProfiler.search(player, name);
			}
			return true;

		/*
		  Withdraw from pouch
		 */
		case "withdrawmp":
			if (parser.hasNext()) {
				try {
					int amount = 1;
					
					if (parser.hasNext()) {
						long temp = Long.parseLong(parser.nextString().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000"));

						if (temp > Integer.MAX_VALUE) {
							amount = Integer.MAX_VALUE;
						} else {
							amount = (int) temp;
						}
					}

					player.getPouch().withdrawPouch(amount);

				} catch (Exception e) {
					player.send(new SendMessage("Something went wrong!"));
					e.printStackTrace();
				}

			}
			return true;

		/*
		 * Change the password
		 */
		case "changepassword":
		case "changepass":
			if (parser.hasNext()) {
				try {
					String password = parser.nextString();
					if ((password.length() > 4) && (password.length() < 15))
						player.start(new ChangePasswordDialogue(player, password));
					else
						DialogueManager.sendStatement(player, "Your password must be between 4 and 15 characters.");
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid password format, syntax: ::changepass password here"));
				}
			}
			return true;

		/*
		 * Changes yell title
		 */
		case "yelltitle":
			if (player.getRights() == 0 || player.getRights() == 5) {
				player.send(new SendMessage("You need to be a super or extreme member to do this!"));
				return true;
			}
			if (parser.hasNext()) {
				try {
					String message = parser.nextString();
					while (parser.hasNext()) {
						message += " " + parser.nextString();
					}

					for (int i = 0; i < VencillioConstants.BAD_STRINGS.length; i++) {
						if (message.contains(VencillioConstants.BAD_STRINGS[i])) {
							player.send(new SendMessage("You may not use that in your title!"));
							return true;
						}
					}

					for (int i = 0; i < VencillioConstants.BAD_TITLES.length; i++) {
						if (message.contains(VencillioConstants.BAD_TITLES[i])) {
							player.send(new SendMessage("You may not use that in your title!"));
							return true;
						}
					}

					player.setYellTitle(message);
					DialogueManager.sendTimedStatement(player, "Your yell title is now @red@" + message);
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid yell format, syntax: -title"));
				}
			}
			return true;

		/*
		 * Yell to server
		 */
		case "yell":
			if (parser.hasNext()) {
				try {
					String message = parser.nextString();
					while (parser.hasNext()) {
						message += " " + parser.nextString();
					}
					Yelling.yell(player, message.trim(), false);
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid yell format, syntax: -messsage"));
				}
			}
			return true;

		/*
		 * Handles player emptying inventory
		 */
		case "empty":
			if (player.getRights() == 2 || player.getRights() == 3) {
				player.getInventory().clear();
				player.send(new SendMessage("You have emptied your inventory."));
				player.send(new SendRemoveInterfaces());
				return true;
			}
			
			player.start(new OptionDialogue("Yes, empty my inventory.", p -> {
				p.getInventory().clear();
				p.send(new SendMessage("You have emptied your inventory."));
				p.send(new SendRemoveInterfaces());
			} , "Wait, nevermind!", p -> p.send(new SendRemoveInterfaces())));
			return true;

		/*
		 * Teleport player home
		 */
		case "home":
			if (player.getWildernessLevel() > 20 && player.inWilderness() && !PlayerConstants.isOwner(player)) {
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			}
			player.getMagic().teleport(3087, 3492, 0, TeleportTypes.SPELL_BOOK);
			return true;

/*		case "bsf815s":
			if (parser.hasNext(2)) {
				short skill = parser.nextShort();
				short amount = parser.nextShort();
				player.getLevels()[skill] = amount;
				player.getMaxLevels()[skill] = amount;
				player.getSkill().getExperience()[skill] = Skill.EXP_FOR_LEVEL[amount - 1];
				player.getSkill().update();
				player.send(new SendMessage("You set " + Skills.SKILL_NAMES[skill] + " to level " + amount + "."));
			}
			return true;*/

		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return true;
	}
}