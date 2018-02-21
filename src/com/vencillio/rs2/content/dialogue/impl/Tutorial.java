package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.StarterKit;
import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.DefaultController;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendSidebarInterface;

public class Tutorial extends Dialogue {

	public static class TutorialController extends DefaultController {

		@Override
		public boolean canAttackNPC() {
			return false;
		}

		@Override
		public boolean canClick() {
			return false;
		}

		@Override
		public boolean canMove(Player p) {
			return false;
		}

		@Override
		public boolean canTeleport() {
			return false;
		}

		@Override
		public boolean canTrade() {
			return false;
		}

		@Override
		public void onDisconnect(Player p) {
		}

		@Override
		public boolean transitionOnWalk(Player p) {
			return false;
		}
	}

	public static final TutorialController TUTORIAL_CONTROLLER = new TutorialController();

	public static final int GUIDE = 306;

	public Tutorial(Player player) {
		this.player = player;
		player.setController(TUTORIAL_CONTROLLER);
	}

	@Override
	public boolean clickButton(int id) {
		System.out.println("id: " + id);
		switch (id) {
			case 9157:
				next = 3;
				execute();
				return true;
			case 9158:
				option = 2;
				next = 12;
				execute();
				return true;
			case 9178:
				player.mode = 0;
				next = option == 2 ? 27 : 14;
				execute();
				return true;
			case 9179:
				player.mode = 1;
				next = option == 2 ? 27 : 14;
				execute();
				return true;
			case 9180:
				player.mode = 2;
				next = option == 2 ? 27 : 14;
				execute();
				return true;
			case 9181:
				player.mode = 3;
				next = option == 2 ? 27 : 14;
				execute();
				return true;
		}
		return false;
	}

	public static final int[] SIDEBAR_INTERFACE_IDS = {-1, -1, -1, 3213, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

	@Override
	public void execute() {

		for (int i = 0; i < SIDEBAR_INTERFACE_IDS.length; i++) {
			player.send(new SendSidebarInterface(i, SIDEBAR_INTERFACE_IDS[i]));
		}

		switch (next) {

			case 0:
				DialogueManager.sendNpcChat(player, GUIDE, Emotion.HAPPY_TALK, "Hello @blu@" + player.getUsername() + "</col>, Welcome to Tannerscape!", "Would you like a tutorial of our lands?");
				next++;
				break;
			case 1:
				DialogueManager.sendOption(player, new String[]{"Yes.", "No."});
				option = 1;
				break;
			case 2:
				end();
				StarterKit.handle(player, 202051);
				player.send(new SendInterface(51750));
				break;
			case 3:
				nChat(new String[]{"Hello @blu@" + player.getUsername() + "</col>, Welcome to Tannerscape!", "Let's get started with the tutorial!"});
				break;
			case 4:
				nChat(new String[]{"In addition to normal, ironman, and hardcore ironman"});
				break;
			case 5:
				nChat(new String[]{"There is an additional mode here"});
				break;
			case 6:
				nChat(new String[]{"There are 4 difficulties", "which affect various aspects of gameplay", "(Remember this)"});
				break;
			case 7:
				nChat(new String[]{"Easy (Fast Experience)", "Medium (Average experience)", "Hard (Slightly slower xp)", "Ultimate (Slowest Experience)"});
				break;
			case 8:
				nChat(new String[]{"In addition to experience rates, special attack restore rate,"});
				break;
			case 9:
				nChat(new String[]{"prayer drain, and damage are also affected"});
				break;
			case 10:
				nChat(new String[]{"The harder the difficulty chosen"});
				break;
			case 11:
				nChat(new String[]{"the slower the xp but the better the advantages", "previously mentioned"});
				break;
			case 12:
				nChat(new String[]{"Choose your difficulty on the next screen"});
				break;
			case 13:
				DialogueManager.sendOption(player, "Easy", "Medium", "Hard", "Ultimate");
				break;
			case 14:
				nChat(new String[]{"Clicking on the World Map will allow you to teleport", "to various different locations.", "Including minigames, PvP, PvM, etc"});
				break;
			case 15:
				tele(3082, 3485);
				nChat(new String[]{"You can change your magic books here."});
				break;
			case 16:
				tele(3096, 3486);
				nChat(new String[]{"This is Vannaka; he can give you a slayer task.", "You may also get a co-op slayer task."});
				break;
			case 17:
				tele(3098, 3498);
				nChat(new String[]{"This is our exchange post.", "They are located all over Tannerscape.", "You may sell/buy items from other players here."});
				break;
			case 18:
				tele(3096, 3505);
				nChat(new String[]{"These are the shops.", "You may buy all your necessary items from here."});
				break;
			case 19:
				tele(3099, 3500);
				nChat(new String[]{"This is the home thieving area.", "This is a very easy method to make quick money."});
				break;
			case 20:
				tele(3086, 3500);
				nChat(new String[]{"This is the Emblem Trader.", "He will reward you for all your hard work from the", "Bounty Hunter."});
				break;
			case 21:
				tele(3084, 3489);
				nChat(new String[]{"Nieve here is in charge of our Prestige system.", "After reaching 99 in a statistic you may prestige it."});
				break;
			case 22:
				tele(3082, 3505);
				nChat(new String[]{"If you are ever curious about a NPC, talk to Hari.", "He will be able to help you out."});
				break;
			case 23:
				tele(3078, 3505);
				nChat(new String[]{"These three young fellows can be very useful for you.", "They can change your appeareance, give you skillcapes,", "and decant your potions. For a cost of course!"});
				break;
			case 24:
				tele(3086, 3489);
				nChat(new String[]{"If you have any more questions please speak to a", "<img=0>@blu@ Moderator</col> or any other staff member."});
				break;
			case 25:
				nChat(new String[]{"You may view the rules on our forums (@red@www.rennatscape.prsetoboards.com/</col>)", "Make sure to vote to keep the server active."});
				break;
			case 26:
				nChat(new String[]{"There are tons more of content to explore.", "Good luck with your adventurer!"});
				break;
			case 27:
				end();
				StarterKit.handle(player, 202051);
				player.send(new SendInterface(51750));
				break;
		}

	}

	public void nChat(String[] chat) {
		DialogueManager.sendNpcChat(player, GUIDE, Emotion.HAPPY_TALK, chat);
		next += 1;
	}

	public void pChat(String[] chat) {
		DialogueManager.sendPlayerChat(player, Emotion.HAPPY, chat);
		next += 1;
	}

	public void tele(int x, int y) {
		player.teleport(new Location(x, y, 0));
	}

	public void tele(int x, int y, int z) {
		player.teleport(new Location(x, y, z));
	}
}
