package com.vencillio.rs2.content.dialogue;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendChatBoxInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterfaceConfig;
import com.vencillio.rs2.entity.player.net.out.impl.SendModelAnimation;
import com.vencillio.rs2.entity.player.net.out.impl.SendNPCDialogueHead;
import com.vencillio.rs2.entity.player.net.out.impl.SendPlayerDialogueHead;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class DialogueManager {
	
    public static void sendItem2(Player player, String text1, String text2, int item1, int item2) {
    	player.send(new SendString(text1, 6232));
    	player.send(new SendString(text2, 6233));
    	player.send(new SendInterfaceConfig(6235, 170, item1));
    	player.send(new SendInterfaceConfig(6236, 170, item2));
		player.send(new SendChatBoxInterface(6231));
	}
    
    public static void sendItem2zoom(Player player, String text1, String text2, int item1, int item2) {
    	player.send(new SendString(text1, 6232));
    	player.send(new SendString(text2, 6233));
    	player.send(new SendInterfaceConfig(6235, 130, item1));
    	player.send(new SendInterfaceConfig(6236, 100, item2));
    	player.send(new SendChatBoxInterface(6231));
    }
	
	public static void sendItem1(Player player, String text, int item) {
		player.send(new SendString(text, 308));
		player.send(new SendInterfaceConfig(307, 200, item));
		player.send(new SendChatBoxInterface(306));
	}
	
	public static void makeItem3(Player player, int itemId1, String itemName1, int itemId2, String itemName2, int itemId3, String itemName3) {
		player.send(new SendChatBoxInterface(8880));
		player.send(new SendInterfaceConfig(8883, 190, itemId1));
		player.send(new SendInterfaceConfig(8884, 190, itemId2));
		player.send(new SendInterfaceConfig(8885, 190, itemId3));
		player.send(new SendString(itemName1, 8889));
		player.send(new SendString(itemName2, 8893));
		player.send(new SendString(itemName3, 8897));
	}

	public static void sendInformationBox(Player player, String title, String line1, String line2, String line3, String line4) {
		player.send(new SendString(title, 6180));
		player.send(new SendString(line1, 6181));
		player.send(new SendString(line2, 6182));
		player.send(new SendString(line3, 6183));
		player.send(new SendString(line4, 6184));
		player.send(new SendChatBoxInterface(6179));
	}

	public static void sendNpcChat(Player player, int npcId, Emotion emotion, String... lines) {
		String npcName = GameDefinitionLoader.getNpcDefinition(npcId).getName();
		switch (lines.length) {
		case 1:
			player.send(new SendModelAnimation(4883, emotion.getEmoteId()));
			player.send(new SendString(npcName, 4884));
			player.send(new SendString(lines[0], 4885));
			player.send(new SendNPCDialogueHead(npcId, 4883));
			player.send(new SendChatBoxInterface(4882));
			break;
		case 2:
			player.send(new SendModelAnimation(4888, emotion.getEmoteId()));
			player.send(new SendString(npcName, 4889));
			player.send(new SendString(lines[0], 4890));
			player.send(new SendString(lines[1], 4891));
			player.send(new SendNPCDialogueHead(npcId, 4888));
			player.send(new SendChatBoxInterface(4887));
			break;
		case 3:
			player.send(new SendModelAnimation(4894, emotion.getEmoteId()));
			player.send(new SendString(npcName, 4895));
			player.send(new SendString(lines[0], 4896));
			player.send(new SendString(lines[1], 4897));
			player.send(new SendString(lines[2], 4898));
			player.send(new SendNPCDialogueHead(npcId, 4894));
			player.send(new SendChatBoxInterface(4893));
			break;
		case 4:
			player.send(new SendModelAnimation(4901, emotion.getEmoteId()));
			player.send(new SendString(npcName, 4902));
			player.send(new SendString(lines[0], 4903));
			player.send(new SendString(lines[1], 4904));
			player.send(new SendString(lines[2], 4905));
			player.send(new SendString(lines[3], 4906));
			player.send(new SendNPCDialogueHead(npcId, 4901));
			player.send(new SendChatBoxInterface(4900));
		}
	}

	public static void sendOption(Player player, String... options) {
		if (options.length < 2) {
			return;
		}
		switch (options.length) {
		case 1:
			throw new IllegalArgumentException("1 option is not possible! (DialogueManager.java)");
		case 2:
			player.send(new SendString(options[0], 2461));
			player.send(new SendString(options[1], 2462));
			player.send(new SendChatBoxInterface(2459));
			break;
		case 3:
			player.send(new SendString(options[0], 2471));
			player.send(new SendString(options[1], 2472));
			player.send(new SendString(options[2], 2473));
			player.send(new SendChatBoxInterface(2469));
			break;
		case 4:
			player.send(new SendString(options[0], 2482));
			player.send(new SendString(options[1], 2483));
			player.send(new SendString(options[2], 2484));
			player.send(new SendString(options[3], 2485));
			player.send(new SendChatBoxInterface(2480));
			break;
		case 5:
			player.send(new SendString(options[0], 2494));
			player.send(new SendString(options[1], 2495));
			player.send(new SendString(options[2], 2496));
			player.send(new SendString(options[3], 2497));
			player.send(new SendString(options[4], 2498));
			player.send(new SendChatBoxInterface(2492));
		}
	}

	public static void sendPlayerChat(Player player, Emotion emotion, String... lines) {
		switch (lines.length) {
		case 1:
			player.send(new SendModelAnimation(969, emotion.getEmoteId()));
			player.send(new SendString(player.getUsername(), 970));
			player.send(new SendString(lines[0], 971));
			player.send(new SendPlayerDialogueHead(969));
			player.send(new SendChatBoxInterface(968));
			break;
		case 2:
			player.send(new SendModelAnimation(974, emotion.getEmoteId()));
			player.send(new SendString(player.getUsername(), 975));
			player.send(new SendString(lines[0], 976));
			player.send(new SendString(lines[1], 977));
			player.send(new SendPlayerDialogueHead(974));
			player.send(new SendChatBoxInterface(973));
			break;
		case 3:
			player.send(new SendModelAnimation(980, emotion.getEmoteId()));
			player.send(new SendString(player.getUsername(), 981));
			player.send(new SendString(lines[0], 982));
			player.send(new SendString(lines[1], 983));
			player.send(new SendString(lines[2], 984));
			player.send(new SendPlayerDialogueHead(980));
			player.send(new SendChatBoxInterface(979));
			break;
		case 4:
			player.send(new SendModelAnimation(987, emotion.getEmoteId()));
			player.send(new SendString(player.getUsername(), 988));
			player.send(new SendString(lines[0], 989));
			player.send(new SendString(lines[1], 990));
			player.send(new SendString(lines[2], 991));
			player.send(new SendString(lines[3], 992));
			player.send(new SendPlayerDialogueHead(987));
			player.send(new SendChatBoxInterface(986));
		}
	}

	public static void sendStatement(Player player, String... lines) {
		switch (lines.length) {
		case 1:
			player.send(new SendString(lines[0], 357));
			player.send(new SendChatBoxInterface(356));
			break;
		case 2:
			player.send(new SendString(lines[0], 360));
			player.send(new SendString(lines[1], 361));
			player.send(new SendChatBoxInterface(359));
			break;
		case 3:
			player.send(new SendString(lines[0], 364));
			player.send(new SendString(lines[1], 365));
			player.send(new SendString(lines[2], 366));
			player.send(new SendChatBoxInterface(363));
			break;
		case 4:
			player.send(new SendString(lines[0], 369));
			player.send(new SendString(lines[1], 370));
			player.send(new SendString(lines[2], 371));
			player.send(new SendString(lines[3], 372));
			player.send(new SendChatBoxInterface(368));
			break;
		case 5:
			player.send(new SendString(lines[0], 375));
			player.send(new SendString(lines[1], 376));
			player.send(new SendString(lines[2], 377));
			player.send(new SendString(lines[3], 378));
			player.send(new SendString(lines[4], 379));
			player.send(new SendChatBoxInterface(374));
		}
	}

	public static void sendTimedNpcChat(Player player, int npcId, Emotion emotion, String... lines) {
		String npcName = GameDefinitionLoader.getNpcDefinition(npcId).getName();
		switch (lines.length) {
		case 2:
			player.send(new SendModelAnimation(12379, emotion.getEmoteId()));
			player.send(new SendString(npcName, 12380));
			player.send(new SendString(lines[0], 12381));
			player.send(new SendString(lines[1], 12382));
			player.send(new SendNPCDialogueHead(npcId, 12379));
			player.send(new SendChatBoxInterface(12378));
			break;
		case 3:
			player.send(new SendModelAnimation(12384, emotion.getEmoteId()));
			player.send(new SendString(npcName, 12385));
			player.send(new SendString(lines[0], 12386));
			player.send(new SendString(lines[1], 12387));
			player.send(new SendString(lines[2], 12388));
			player.send(new SendNPCDialogueHead(npcId, 12384));
			player.send(new SendChatBoxInterface(12383));
			break;
		case 4:
			player.send(new SendModelAnimation(11892, emotion.getEmoteId()));
			player.send(new SendString(npcName, 11893));
			player.send(new SendString(lines[0], 11894));
			player.send(new SendString(lines[1], 11895));
			player.send(new SendString(lines[2], 11896));
			player.send(new SendString(lines[3], 11897));
			player.send(new SendNPCDialogueHead(npcId, 11892));
			player.send(new SendChatBoxInterface(11891));
		}
	}

	public static void sendTimedPlayerChat(Player player, Emotion emotion, String... lines) {
		switch (lines.length) {
		case 1:
			player.send(new SendModelAnimation(12774, emotion.getEmoteId()));
			player.send(new SendString(player.getUsername(), 12775));
			player.send(new SendString(lines[0], 12776));
			player.send(new SendPlayerDialogueHead(12774));
			player.send(new SendChatBoxInterface(12773));
			break;
		case 2:
			player.send(new SendModelAnimation(12778, emotion.getEmoteId()));
			player.send(new SendString(player.getUsername(), 12779));
			player.send(new SendString(lines[0], 12780));
			player.send(new SendString(lines[1], 12781));
			player.send(new SendPlayerDialogueHead(12778));
			player.send(new SendChatBoxInterface(12777));
			break;
		case 3:
			player.send(new SendModelAnimation(12783, emotion.getEmoteId()));
			player.send(new SendString(player.getUsername(), 12784));
			player.send(new SendString(lines[0], 12785));
			player.send(new SendString(lines[1], 12786));
			player.send(new SendString(lines[2], 12787));
			player.send(new SendPlayerDialogueHead(12783));
			player.send(new SendChatBoxInterface(12782));
			break;
		case 4:
			player.send(new SendModelAnimation(11885, emotion.getEmoteId()));
			player.send(new SendString(player.getUsername(), 11886));
			player.send(new SendString(lines[0], 11887));
			player.send(new SendString(lines[1], 11888));
			player.send(new SendString(lines[2], 11889));
			player.send(new SendString(lines[3], 11890));
			player.send(new SendPlayerDialogueHead(11885));
			player.send(new SendChatBoxInterface(11884));
		}
	}

	public static void sendTimedStatement(Player player, String... lines) {
		switch (lines.length) {
		case 1:
			player.send(new SendString(lines[0], 12789));
			player.send(new SendChatBoxInterface(12788));
			break;
		case 2:
			player.send(new SendString(lines[0], 12791));
			player.send(new SendString(lines[1], 12792));
			player.send(new SendChatBoxInterface(12790));
			break;
		case 3:
			player.send(new SendString(lines[0], 12794));
			player.send(new SendString(lines[1], 12795));
			player.send(new SendString(lines[2], 12796));
			player.send(new SendChatBoxInterface(12793));
			break;
		case 4:
			player.send(new SendString(lines[0], 12798));
			player.send(new SendString(lines[1], 12799));
			player.send(new SendString(lines[2], 12800));
			player.send(new SendString(lines[3], 12801));
			player.send(new SendChatBoxInterface(12797));
			break;
		case 5:
			player.send(new SendString(lines[0], 12803));
			player.send(new SendString(lines[1], 12804));
			player.send(new SendString(lines[2], 12805));
			player.send(new SendString(lines[3], 12806));
			player.send(new SendString(lines[4], 12807));
			player.send(new SendChatBoxInterface(12802));
		}
	}
}
