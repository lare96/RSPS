package com.vencillio.rs2.content.gambling;

import java.util.ArrayList;
import java.util.List;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.FileHandler;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.dialogue.OptionDialogue;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class FlowerGame {
	
	private static boolean busy = false;
	private static List<Mob> gamblers = new ArrayList<>();
	
	public enum Flower {
		RED(2462, 2981, FlowerType.HOT),
		YELLOW(2466, 2983, FlowerType.HOT),
		ORANGE(2470, 2985, FlowerType.HOT),
		WHITE(2474, 2987, FlowerType.HOT),
		BLUE(2464, 2982, FlowerType.COLD),
		PURPLE(2468, 2984, FlowerType.COLD),
		ASSORTED(2460, 2980, FlowerType.COLD),
		BLACK(2476, 2988, FlowerType.COLD),
		RAINBOW(2472, 2986, FlowerType.NEITHER);
		
		private int item;
		private int object;
		private FlowerType type;
		
		private Flower(int item, int object, FlowerType type) {
			this.item = item;
			this.object = object;
			this.type = type;
		}

		public int getItem() {
			return item;
		}
		
		public int getObject() {
			return object;
		}

		public FlowerType getType() {
			return type;
		}
	}
	
	public enum FlowerType {
		HOT,
		COLD,
		NEITHER
	}
	
	public static void setGambler(Mob gambler) {
		FlowerGame.gamblers.add(gambler);
	}
	
	private static Mob getGambler(Player player) {
		Mob mob = gamblers.get(0);
		
		for (Mob gambler : gamblers) {
			if (Math.abs(gambler.getX() - player.getX()) <= Math.abs(mob.getX() - player.getX()) && Math.abs(gambler.getY() - player.getY()) <= Math.abs(mob.getX() - player.getX())) {
				mob = gambler;
			}
		}
		
		return mob;
	}

	public static boolean canPlay(Player player, int amount) {
//		if (PlayerConstants.isStaff(player)) {
//			DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "Sorry, but Daniel has forbidden you from gambling.");
//			return false;
//		} else 

		if (busy) {
			DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "I'm busy with another bet at the moment.");
			return false;
		} else if (player.isPouchPayment()) {
			if (player.getMoneyPouch() < amount) {
				DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "You don't have that much money to bet!");
				return false;
			}
		} else if (!player.getInventory().hasItemAmount(995, amount)) {
			DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "You don't have that much money to bet!");
			return false;
		}

		return true;
	}

	public static void play(Player player, int amount) {
		if (!canPlay(player, amount)) {
			return;
		}
		
		busy = true;
		
		Flower flower = getFlower();
		
		player.send(new SendMessage(flower));
		Mob mob = getGambler(player);
		
		player.start(new OptionDialogue(
			"Bet on hot", p -> {
				plant(mob, flower);
				if (flower.getType() == FlowerType.HOT) {
					results(player, amount, true);
				} else {
					results(player, amount, false);
				}
			},
			"Bet on cold", p -> {
				if (flower.getType() == FlowerType.COLD) {
					results(player, amount, true);
				} else {
					results(player, amount, false);
				}
			}
		));
	}
	
	private static void plant(Mob gambler, Flower flower) {
		TaskQueue.queue(new Task(gambler, 1) {
			int ticks = 0;
			
			@Override
			public void execute() {
				switch (ticks++) {
				case 0:
					gambler.getMovementHandler().walkTo(1, 0);
					gambler.getUpdateFlags().sendAnimation(new Animation(827));
				case 1:
					TaskQueue.queue(new Task(gambler, 6) {
						GameObject obj = new GameObject(flower.getObject(), gambler.getLocation(), 10, 0);
						@Override
						public void execute() {
							ObjectManager.addClippedObject(obj);
						}
						
						@Override
						public void onStop() {
							ObjectManager.remove(obj);
						}
					});
					break;
				case 2:
					stop();
					break;
				}
			}

			@Override
			public void onStop() {
				System.out.println("FUCK SALT");
			}
		});
	}
	
	private static Flower getFlower() {
		return Utility.randomElement(Flower.values());
	}

	public static void results(Player player, int amount, boolean win) {
		busy = false;
		String bet = Utility.format(amount);
		if (win) {
			if (player.isPouchPayment()) {
				player.setMoneyPouch(player.getMoneyPouch() + amount);
				player.send(new SendString(player.getMoneyPouch() + "", 8135));
			} else {
				player.getInventory().add(995, amount);
			}
			if (amount >= 1_000_000) {
				World.sendGlobalMessage("<img=8> <col=C42BAD>" + player.getUsername() + " has just won " + Utility.format(amount) + " from the Gambler!");
			}
			save(amount);
			DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "Congratulations! You have won " + bet + ".");
			return;
		}
		if (player.isPouchPayment()) {
			player.setMoneyPouch(player.getMoneyPouch() - amount);
			player.send(new SendString(player.getMoneyPouch() + "", 8135));
		} else {
			player.getInventory().remove(995, amount);
		}
		DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "Sorry! You have lost " + bet + "!");
		save(-amount);
	}

	public static void save(long amount) {
		Gambling.MONEY_TRACKER += amount;
		FileHandler.saveGambling();
	}

}
