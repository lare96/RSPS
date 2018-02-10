package com.vencillio.rs2.content.cluescroll;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.vencillio.core.util.Utility;
import com.vencillio.core.util.chance.Chance;
import com.vencillio.rs2.content.Emotes.Emote;
import com.vencillio.rs2.content.cluescroll.Clue.ClueType;
import com.vencillio.rs2.content.cluescroll.scroll.EmoteScroll;
import com.vencillio.rs2.content.cluescroll.scroll.MapScroll;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemContainer;
import com.vencillio.rs2.entity.item.ItemContainer.ContainerTypes;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItems;

/**
 * Manages any action regarding clue scrolls.
 * 
 * @author Michael| Chex
 */
public enum ClueScrollManager {

	/**
	 * An instance of {@link ClueScrollManager}.
	 */
	SINGLETON;

	/**
	 * The casket (easy) item id.
	 */
	public static final int CASKET_EASY = 2714;

	/**
	 * The casket (medium) item id.
	 */
	public static final int CASKET_MEDIUM = 2802;

	/**
	 * The casket (hard) item id.
	 */
	public static final int CASKET_HARD = 2724;

	/**
	 * A map containing all the available clue scrolls.
	 */
	private static final HashMap<Integer, ClueScroll> CLUE_SCROLLS = new HashMap<>();

	public static final Chance<Item> CROSS_TRAILS = new Chance<Item>();
	public static final Chance<Item> EASY = new Chance<Item>();
	public static final Chance<Item> MEDIUM = new Chance<Item>();
	public static final Chance<Item> HARD = new Chance<Item>();

	/**
	 * Declares all the functioning clue scrolls to make them accessable in
	 * game.
	 */
	public static void declare() {
		CROSS_TRAILS.add(100, new Item(995, 500)); // Coins
		CROSS_TRAILS.add(100, new Item(995, 750)); // Coins
		CROSS_TRAILS.add(100, new Item(995, 1000)); // Coins
		CROSS_TRAILS.add(100, new Item(995, 5000)); // Coins
		CROSS_TRAILS.add(100, new Item(995, 5500)); // Coins
		CROSS_TRAILS.add(100, new Item(1692, 1)); // Gold amulet
		CROSS_TRAILS.add(100, new Item(1694, 1)); // Sapphire amulet
		CROSS_TRAILS.add(100, new Item(1696, 1)); // Emerald amulet
		CROSS_TRAILS.add(100, new Item(1698, 1)); // Ruby amulet
		CROSS_TRAILS.add(100, new Item(1700, 1)); // Diamond amulet
		CROSS_TRAILS.add(75, new Item(1702, 1)); // Dragonstone amulet
		CROSS_TRAILS.add(75, new Item(10280, 1)); // Willow comp bow
		CROSS_TRAILS.add(75, new Item(10282, 1)); // Yew comp bow
		CROSS_TRAILS.add(75, new Item(10284, 1)); // Magic comp bow
		CROSS_TRAILS.add(100, new Item(554, 885)); // Fire rune
		CROSS_TRAILS.add(100, new Item(555, 553)); // Water rune
		CROSS_TRAILS.add(100, new Item(556, 533)); // Air rune
		CROSS_TRAILS.add(100, new Item(557, 492)); // Earth rune
		CROSS_TRAILS.add(100, new Item(558, 228)); // Mind rune
		CROSS_TRAILS.add(100, new Item(559, 97)); // Body rune
		CROSS_TRAILS.add(100, new Item(560, 612)); // Death rune
		CROSS_TRAILS.add(100, new Item(561, 17)); // Nature rune
		CROSS_TRAILS.add(100, new Item(562, 4)); // Chaos rune
		CROSS_TRAILS.add(100, new Item(563, 745)); // Law rune
		CROSS_TRAILS.add(100, new Item(564, 973)); // Cosmic rune
		CROSS_TRAILS.add(100, new Item(565, 895)); // Blood rune
		CROSS_TRAILS.add(25, new Item(3827, 1)); // Saradomin page 1
		CROSS_TRAILS.add(25, new Item(3828, 1)); // Saradomin page 2
		CROSS_TRAILS.add(25, new Item(3829, 1)); // Saradomin page 3
		CROSS_TRAILS.add(25, new Item(3830, 1)); // Saradomin page 4
		CROSS_TRAILS.add(25, new Item(3831, 1)); // Zamorak page 1
		CROSS_TRAILS.add(25, new Item(3832, 1)); // Zamorak page 2
		CROSS_TRAILS.add(25, new Item(3833, 1)); // Zamorak page 3
		CROSS_TRAILS.add(25, new Item(3834, 1)); // Zamorak page 4
		CROSS_TRAILS.add(25, new Item(3835, 1)); // Guthix page 1
		CROSS_TRAILS.add(25, new Item(3836, 1)); // Guthix page 2
		CROSS_TRAILS.add(25, new Item(3837, 1)); // Guthix page 3
		CROSS_TRAILS.add(25, new Item(3838, 1)); // Guthix page 4

		EASY.add(200, new Item(1077, 1)); // Black platelegs
		EASY.add(200, new Item(1089, 1)); // Black plateskirt
		EASY.add(200, new Item(1107, 1)); // Black chainbody
		EASY.add(200, new Item(1125, 1)); // Black platebody
		EASY.add(200, new Item(1151, 1)); // Black med helm
		EASY.add(200, new Item(1165, 1)); // Black full helm
		EASY.add(200, new Item(1179, 1)); // Black sq shield
		EASY.add(200, new Item(1195, 1)); // Black kiteshield
		EASY.add(200, new Item(1217, 1)); // Black dagger
		EASY.add(200, new Item(1283, 1)); // Black sword
		EASY.add(200, new Item(1297, 1)); // Black longsword
		EASY.add(200, new Item(1313, 1)); // Black 2h sword
		EASY.add(200, new Item(1327, 1)); // Black scimitar
		EASY.add(200, new Item(1341, 1)); // Black warhammer
		EASY.add(200, new Item(1361, 1)); // Black axe
		EASY.add(200, new Item(1367, 1)); // Black battleaxe
		EASY.add(200, new Item(1426, 1)); // Black mace
		EASY.add(200, new Item(8778, 1)); // Oak plank
		EASY.add(200, new Item(849, 1)); // Willow shortbow
		EASY.add(200, new Item(1169, 1)); // Coif
		EASY.add(200, new Item(1095, 1)); // Leather chaps
		EASY.add(200, new Item(1129, 1)); // Leather body
		EASY.add(200, new Item(1131, 1)); // Hardleather body
		EASY.add(200, new Item(1063, 1)); // Leather vambraces
		EASY.add(200, new Item(1061, 1)); // Leather boots
		EASY.add(200, new Item(1059, 1)); // Leather gloves
		EASY.add(200, new Item(1167, 1)); // Leather cowl
		EASY.add(200, new Item(329, 1)); // Salmon
		EASY.add(200, new Item(333, 1)); // Trout
		EASY.add(200, new Item(1438, 1)); // Air talisman
		EASY.add(200, new Item(1440, 1)); // Earth talisman
		EASY.add(200, new Item(1442, 1)); // Fire talisman
		EASY.add(200, new Item(1444, 1)); // Water talisman
		EASY.add(200, new Item(1446, 1)); // Body talisman
		EASY.add(200, new Item(1448, 1)); // Mind talisman
		EASY.add(200, new Item(1269, 1)); // Steel pickaxe
		EASY.add(200, new Item(1452, 1)); // Chaos talisman
		EASY.add(200, new Item(1454, 1)); // Cosmic talisman
		EASY.add(200, new Item(1456, 1)); // Death talisman
		EASY.add(200, new Item(1458, 1)); // Law talisman
		EASY.add(200, new Item(1462, 1)); // Nature talisman
		EASY.add(15, new Item(12205, 1)); // Bronze platebody (g)
		EASY.add(15, new Item(12207, 1)); // Bronze platelegs (g)
		EASY.add(15, new Item(12209, 1)); // Bronze plateskirt (g)
		EASY.add(15, new Item(12211, 1)); // Bronze full helm (g)
		EASY.add(15, new Item(12213, 1)); // Bronze kiteshield (g)
		EASY.add(15, new Item(12215, 1)); // Bronze platebody (t)
		EASY.add(15, new Item(12217, 1)); // Bronze platelegs (t)
		EASY.add(15, new Item(12219, 1)); // Bronze plateskirt (t)
		EASY.add(15, new Item(12221, 1)); // Bronze full helm (t)
		EASY.add(15, new Item(12223, 1)); // Bronze kiteshield (t)
		EASY.add(15, new Item(12225, 1)); // Iron platebody (t)
		EASY.add(15, new Item(12227, 1)); // Iron platelegs (t)
		EASY.add(15, new Item(12229, 1)); // Iron plateskirt (t)
		EASY.add(15, new Item(12231, 1)); // Iron full helm (t)
		EASY.add(15, new Item(12233, 1)); // Iron kiteshield (t)
		EASY.add(15, new Item(12235, 1)); // Iron platebody (g)
		EASY.add(15, new Item(12237, 1)); // Iron platelegs (g)
		EASY.add(15, new Item(12239, 1)); // Iron plateskirt (g)
		EASY.add(15, new Item(12241, 1)); // Iron full helm (g)
		EASY.add(15, new Item(12243, 1)); // Iron kiteshield (g)
		EASY.add(15, new Item(2583, 1)); // Black platebody (t)
		EASY.add(15, new Item(2585, 1)); // Black platelegs (t)
		EASY.add(15, new Item(2587, 1)); // Black full helm (t)
		EASY.add(15, new Item(2589, 1)); // Black kiteshield (t)
		EASY.add(15, new Item(2591, 1)); // Black platebody (g)
		EASY.add(15, new Item(2593, 1)); // Black platelegs (g)
		EASY.add(15, new Item(2595, 1)); // Black full helm (g)
		EASY.add(15, new Item(2597, 1)); // Black kiteshield (g)
		EASY.add(15, new Item(3472, 1)); // Black plateskirt (t)
		EASY.add(15, new Item(3473, 1)); // Black plateskirt (g)
		EASY.add(45, new Item(2635, 1)); // Black beret
		EASY.add(45, new Item(2637, 1)); // White beret
		EASY.add(45, new Item(12247, 1)); // Red beret
		EASY.add(45, new Item(2633, 1)); // Blue beret
		EASY.add(200, new Item(2631, 1)); // Highwayman mask
		EASY.add(25, new Item(12245, 1)); // Beanie
		EASY.add(15, new Item(7386, 1)); // Blue skirt (g)
		EASY.add(15, new Item(7390, 1)); // Blue wizard robe (g)
		EASY.add(15, new Item(7394, 1)); // Blue wizard hat (g)
		EASY.add(15, new Item(7396, 1)); // Blue wizard hat (t)
		EASY.add(15, new Item(7388, 1)); // Blue skirt (t)
		EASY.add(15, new Item(7392, 1)); // Blue wizard robe (t)
		EASY.add(15, new Item(12449, 1)); // Black wizard robe (g)
		EASY.add(15, new Item(12453, 1)); // Black wizard hat (g)
		EASY.add(15, new Item(12445, 1)); // Black skirt (g)
		EASY.add(15, new Item(12447, 1)); // Black skirt (t)
		EASY.add(15, new Item(12451, 1)); // Black wizard robe (t)
		EASY.add(15, new Item(12455, 1)); // Black wizard hat (t)
		EASY.add(15, new Item(7364, 1)); // Studded body (t)
		EASY.add(15, new Item(7368, 1)); // Studded chaps (t)
		EASY.add(15, new Item(7362, 1)); // Studded body (g)
		EASY.add(15, new Item(7366, 1)); // Studded chaps (g)
		EASY.add(175, new Item(7332, 1)); // Black shield (h1)
		EASY.add(175, new Item(7338, 1)); // Black shield (h2)
		EASY.add(175, new Item(7344, 1)); // Black shield (h3)
		EASY.add(175, new Item(7350, 1)); // Black shield (h4)
		EASY.add(175, new Item(7356, 1)); // Black shield (h5)
		EASY.add(175, new Item(10306, 1)); // Black helm (h1)
		EASY.add(175, new Item(10308, 1)); // Black helm (h2)
		EASY.add(175, new Item(10310, 1)); // Black helm (h3)
		EASY.add(175, new Item(10312, 1)); // Black helm (h4)
		EASY.add(175, new Item(10314, 1)); // Black helm (h5)
		EASY.add(25, new Item(10404, 1)); // Red elegant shirt
		EASY.add(25, new Item(10406, 1)); // Red elegant legs
		EASY.add(25, new Item(10424, 1)); // Red elegant blouse
		EASY.add(25, new Item(10426, 1)); // Red elegant skirt
		EASY.add(25, new Item(10412, 1)); // Green elegant shirt
		EASY.add(25, new Item(10414, 1)); // Green elegant legs
		EASY.add(25, new Item(10432, 1)); // Green elegant blouse
		EASY.add(25, new Item(10434, 1)); // Green elegant skirt
		EASY.add(25, new Item(10408, 1)); // Blue elegant shirt
		EASY.add(25, new Item(10410, 1)); // Blue elegant legs
		EASY.add(25, new Item(10428, 1)); // Blue elegant blouse
		EASY.add(25, new Item(10430, 1)); // Blue elegant skirt
		EASY.add(25, new Item(10316, 1)); // Bob's red shirt
		EASY.add(25, new Item(10318, 1)); // Bob's blue shirt
		EASY.add(25, new Item(10320, 1)); // Bob's green shirt
		EASY.add(25, new Item(10322, 1)); // Bob's black shirt
		EASY.add(25, new Item(10324, 1)); // Bob's purple shirt
		EASY.add(27, new Item(10392, 1)); // A powdered wig
		EASY.add(27, new Item(10394, 1)); // Flared trousers
		EASY.add(27, new Item(10396, 1)); // Pantaloons
		EASY.add(27, new Item(10398, 1)); // Sleeping cap
		EASY.add(17, new Item(10366, 1)); // Amulet of magic (t)
		EASY.add(10, new Item(12375, 1)); // Black cane
		EASY.add(10, new Item(12297, 1)); // Black pickaxe
		EASY.add(20, new Item(10458, 1)); // Saradomin robe top
		EASY.add(20, new Item(10464, 1)); // Saradomin robe legs
		EASY.add(20, new Item(10462, 1)); // Guthix robe top
		EASY.add(20, new Item(10466, 1)); // Guthix robe legs
		EASY.add(20, new Item(10460, 1)); // Zamorak robe top
		EASY.add(20, new Item(10468, 1)); // Zamorak robe legs
		EASY.add(20, new Item(12193, 1)); // Ancient robe top
		EASY.add(20, new Item(12195, 1)); // Ancient robe legs
		EASY.add(20, new Item(12253, 1)); // Armadyl robe top
		EASY.add(20, new Item(12255, 1)); // Armadyl robe legs
		EASY.add(20, new Item(12265, 1)); // Bandos robe top
		EASY.add(20, new Item(12267, 1)); // Bandos robe legs

		MEDIUM.add(200, new Item(1073, 1)); // Adamant platelegs
		MEDIUM.add(200, new Item(1091, 1)); // Adamant plateskirt
		MEDIUM.add(200, new Item(1111, 1)); // Adamant chainbody
		MEDIUM.add(200, new Item(1123, 1)); // Adamant platebody
		MEDIUM.add(200, new Item(1145, 1)); // Adamant med helm
		MEDIUM.add(200, new Item(1161, 1)); // Adamant full helm
		MEDIUM.add(200, new Item(1183, 1)); // Adamant sq shield
		MEDIUM.add(200, new Item(1199, 1)); // Adamant kiteshield
		MEDIUM.add(200, new Item(1211, 1)); // Adamant dagger
		MEDIUM.add(200, new Item(1287, 1)); // Adamant sword
		MEDIUM.add(200, new Item(1301, 1)); // Adamant longsword
		MEDIUM.add(200, new Item(1317, 1)); // Adamant 2h sword
		MEDIUM.add(200, new Item(1331, 1)); // Adamant scimitar
		MEDIUM.add(200, new Item(1345, 1)); // Adamant warhammer
		MEDIUM.add(200, new Item(1357, 1)); // Adamant axe
		MEDIUM.add(200, new Item(1371, 1)); // Adamant battleaxe
		MEDIUM.add(200, new Item(1430, 1)); // Adamant mace
		MEDIUM.add(200, new Item(1271, 1)); // Adamant pickaxe
		MEDIUM.add(200, new Item(9183, 1)); // Adamant crossbow
		MEDIUM.add(200, new Item(4823, 15)); // Adamantite nails
		MEDIUM.add(200, new Item(1393, 1)); // Fire battlestaff
		MEDIUM.add(200, new Item(857, 1)); // Yew shortbow
		MEDIUM.add(200, new Item(8780, 1)); // Teak plank
		MEDIUM.add(200, new Item(373, 1)); // Swordfish
		MEDIUM.add(200, new Item(379, 1)); // Lobster
		MEDIUM.add(200, new Item(1099, 1)); // Green d'hide chaps
		MEDIUM.add(200, new Item(1135, 1)); // Green d'hide body
		MEDIUM.add(15, new Item(12293, 1)); // Mithril full helm (t)
		MEDIUM.add(15, new Item(12287, 1)); // Mithril platebody (t)
		MEDIUM.add(15, new Item(12289, 1)); // Mithril platelegs (t)
		MEDIUM.add(15, new Item(12291, 1)); // Mithril kiteshield (t)
		MEDIUM.add(15, new Item(12295, 1)); // Mithril plateskirt (t)
		MEDIUM.add(15, new Item(12283, 1)); // Mithril full helm (g)
		MEDIUM.add(15, new Item(12277, 1)); // Mithril platebody (g)
		MEDIUM.add(15, new Item(12285, 1)); // Mithril plateskirt (g)
		MEDIUM.add(15, new Item(12279, 1)); // Mithril platelegs (g)
		MEDIUM.add(15, new Item(12281, 1)); // Mithril kiteshield (g)
		MEDIUM.add(15, new Item(2605, 1)); // Adamant full helm (t)
		MEDIUM.add(15, new Item(3474, 1)); // Adamant plateskirt (t)
		MEDIUM.add(15, new Item(2603, 1)); // Adamant kiteshield (t)
		MEDIUM.add(15, new Item(2599, 1)); // Adamant platebody (t)
		MEDIUM.add(15, new Item(2601, 1)); // Adamant platelegs (t)
		MEDIUM.add(15, new Item(2607, 1)); // Adamant platebody (g)
		MEDIUM.add(15, new Item(2609, 1)); // Adamant platelegs (g)
		MEDIUM.add(15, new Item(2611, 1)); // Adamant kiteshield (g)
		MEDIUM.add(15, new Item(2613, 1)); // Adamant full helm (g)
		MEDIUM.add(15, new Item(3475, 1)); // Adamant plateskirt (g)
		MEDIUM.add(10, new Item(2577, 1)); // Ranger boots
		MEDIUM.add(10, new Item(12598, 1)); // Holy sandals
		MEDIUM.add(25, new Item(2579, 1)); // Wizard boots
		MEDIUM.add(100, new Item(2647, 1)); // Black headband
		MEDIUM.add(100, new Item(2645, 1)); // Red headband
		MEDIUM.add(100, new Item(2649, 1)); // Brown headband
		MEDIUM.add(100, new Item(12305, 1)); // Pink headband
		MEDIUM.add(100, new Item(12307, 1)); // Green headband
		MEDIUM.add(100, new Item(12301, 1)); // Blue headband
		MEDIUM.add(100, new Item(12299, 1)); // White headband
		MEDIUM.add(100, new Item(12303, 1)); // Gold headband
		MEDIUM.add(35, new Item(7319, 1)); // Red boater
		MEDIUM.add(35, new Item(7321, 1)); // Orange boater
		MEDIUM.add(35, new Item(7323, 1)); // Green boater
		MEDIUM.add(35, new Item(7325, 1)); // Blue boater
		MEDIUM.add(35, new Item(7327, 1)); // Black boater
		MEDIUM.add(35, new Item(12309, 1)); // Pink boater
		MEDIUM.add(35, new Item(12311, 1)); // Purple boater
		MEDIUM.add(35, new Item(12313, 1)); // White boater
		MEDIUM.add(15, new Item(7380, 1)); // Green d'hide chaps (t)
		MEDIUM.add(15, new Item(7372, 1)); // Green d'hide body (t)
		MEDIUM.add(15, new Item(7370, 1)); // Green d'hide body (g)
		MEDIUM.add(15, new Item(7378, 1)); // Green d'hide chaps (g)
		MEDIUM.add(100, new Item(7334, 1)); // Adamant shield (h1)
		MEDIUM.add(100, new Item(7340, 1)); // Adamant shield (h2)
		MEDIUM.add(100, new Item(7346, 1)); // Adamant shield (h3)
		MEDIUM.add(100, new Item(7352, 1)); // Adamant shield (h4)
		MEDIUM.add(100, new Item(7358, 1)); // Adamant shield (h5)
		MEDIUM.add(100, new Item(10296, 1)); // Adamant helm (h1)
		MEDIUM.add(100, new Item(10298, 1)); // Adamant helm (h2)
		MEDIUM.add(100, new Item(10300, 1)); // Adamant helm (h3)
		MEDIUM.add(100, new Item(10302, 1)); // Adamant helm (h4)
		MEDIUM.add(100, new Item(10304, 1)); // Adamant helm (h5)
		MEDIUM.add(25, new Item(10400, 1)); // Black elegant shirt
		MEDIUM.add(25, new Item(10402, 1)); // Black elegant legs
		MEDIUM.add(25, new Item(10416, 1)); // Purple elegant shirt
		MEDIUM.add(25, new Item(10418, 1)); // Purple elegant legs
		MEDIUM.add(25, new Item(12315, 1)); // Pink elegant shirt
		MEDIUM.add(25, new Item(12317, 1)); // Pink elegant legs
		MEDIUM.add(25, new Item(12339, 1)); // Pink elegant blouse
		MEDIUM.add(25, new Item(12341, 1)); // Pink elegant skirt
		MEDIUM.add(25, new Item(12343, 1)); // Gold elegant blouse
		MEDIUM.add(25, new Item(12345, 1)); // Gold elegant skirt
		MEDIUM.add(25, new Item(12347, 1)); // Gold elegant shirt
		MEDIUM.add(25, new Item(12349, 1)); // Gold elegant legs
		MEDIUM.add(25, new Item(10436, 1)); // Purple elegant blouse
		MEDIUM.add(25, new Item(10438, 1)); // Purple elegant skirt
		MEDIUM.add(25, new Item(10420, 1)); // White elegant blouse
		MEDIUM.add(25, new Item(10422, 1)); // White elegant skirt
		MEDIUM.add(10, new Item(12377, 1)); // Adamant cane
		MEDIUM.add(35, new Item(10364, 1)); // Strength amulet (t)
		MEDIUM.add(35, new Item(12361, 1)); // Cat mask
		MEDIUM.add(35, new Item(12428, 1)); // Penguin mask
		MEDIUM.add(35, new Item(12359, 1)); // Leprechaun hat
		MEDIUM.add(35, new Item(12319, 1)); // Crier hat
		MEDIUM.add(20, new Item(10446, 1)); // Saradomin cloak
		MEDIUM.add(20, new Item(10448, 1)); // Guthix cloak
		MEDIUM.add(20, new Item(10450, 1)); // Zamorak cloak
		MEDIUM.add(20, new Item(12197, 1)); // Ancient cloak
		MEDIUM.add(20, new Item(12261, 1)); // Armadyl cloak
		MEDIUM.add(20, new Item(12273, 1)); // Bandos cloak
		MEDIUM.add(20, new Item(10452, 1)); // Saradomin mitre
		MEDIUM.add(20, new Item(10454, 1)); // Guthix mitre
		MEDIUM.add(20, new Item(10456, 1)); // Zamorak mitre
		MEDIUM.add(20, new Item(12203, 1)); // Ancient mitre
		MEDIUM.add(20, new Item(12259, 1)); // Armadyl mitre
		MEDIUM.add(20, new Item(12271, 1)); // Bandos mitre

		HARD.add(200, new Item(1079, 1)); // Rune platelegs
		HARD.add(200, new Item(1093, 1)); // Rune plateskirt
		HARD.add(200, new Item(1113, 1)); // Rune chainbody
		HARD.add(200, new Item(1127, 1)); // Rune platebody
		HARD.add(200, new Item(1147, 1)); // Rune med helm
		HARD.add(200, new Item(1163, 1)); // Rune full helm
		HARD.add(200, new Item(1185, 1)); // Rune sq shield
		HARD.add(200, new Item(1201, 1)); // Rune kiteshield
		HARD.add(200, new Item(1213, 1)); // Rune dagger
		HARD.add(200, new Item(1289, 1)); // Rune sword
		HARD.add(200, new Item(1303, 1)); // Rune longsword
		HARD.add(200, new Item(1319, 1)); // Rune 2h sword
		HARD.add(200, new Item(1333, 1)); // Rune scimitar
		HARD.add(200, new Item(1347, 1)); // Rune warhammer
		HARD.add(200, new Item(1359, 1)); // Rune axe
		HARD.add(200, new Item(1373, 1)); // Rune battleaxe
		HARD.add(200, new Item(1432, 1)); // Rune mace
		HARD.add(200, new Item(859, 1)); // Magic longbow
		HARD.add(200, new Item(861, 1)); // Magic shortbow
		HARD.add(200, new Item(2497, 1)); // Black d'hide chaps
		HARD.add(200, new Item(2503, 1)); // Black d'hide body
		HARD.add(200, new Item(2491, 1)); // Black d'hide vamb
		HARD.add(200, new Item(385, 1)); // Shark
		HARD.add(200, new Item(379, 1)); // Lobster
		HARD.add(35, new Item(12526, 1)); // Fury ornament kit
		HARD.add(35, new Item(12532, 1)); // Dragon sq shield ornament kit
		HARD.add(35, new Item(12534, 1)); // Dragon chainbody ornament kit
		HARD.add(35, new Item(12536, 1)); // Dragon plate/skirt ornament kit
		HARD.add(35, new Item(12538, 1)); // Dragon full helm ornament kit
		HARD.add(35, new Item(12528, 1)); // Dark infinity colour kit
		HARD.add(35, new Item(12530, 1)); // Light infinity colour kit
		HARD.add(15, new Item(12381, 1)); // Black d'hide body (g)
		HARD.add(15, new Item(12383, 1)); // Black d'hide chaps (g)
		HARD.add(15, new Item(12385, 1)); // Black d'hide body (t)
		HARD.add(15, new Item(12387, 1)); // Black d'hide chaps (t)
		HARD.add(15, new Item(2615, 1)); // Rune platebody (g)
		HARD.add(15, new Item(2617, 1)); // Rune platelegs (g)
		HARD.add(15, new Item(2619, 1)); // Rune full helm (g)
		HARD.add(15, new Item(2621, 1)); // Rune kiteshield (g)
		HARD.add(15, new Item(2623, 1)); // Rune platebody (t)
		HARD.add(15, new Item(2625, 1)); // Rune platelegs (t)
		HARD.add(15, new Item(2627, 1)); // Rune full helm (t)
		HARD.add(15, new Item(2629, 1)); // Rune kiteshield (t)
		HARD.add(15, new Item(3476, 1)); // Rune plateskirt (g)
		HARD.add(15, new Item(3477, 1)); // Rune plateskirt (t)
		HARD.add(65, new Item(2669, 1)); // Guthix platebody
		HARD.add(65, new Item(2671, 1)); // Guthix platelegs
		HARD.add(65, new Item(2673, 1)); // Guthix full helm
		HARD.add(65, new Item(2675, 1)); // Guthix kiteshield
		HARD.add(65, new Item(3480, 1)); // Guthix plateskirt
		HARD.add(65, new Item(2653, 1)); // Zamorak platebody
		HARD.add(65, new Item(2655, 1)); // Zamorak platelegs
		HARD.add(65, new Item(2657, 1)); // Zamorak full helm
		HARD.add(65, new Item(2659, 1)); // Zamorak kiteshield
		HARD.add(65, new Item(3478, 1)); // Zamorak plateskirt
		HARD.add(65, new Item(2661, 1)); // Saradomin platebody
		HARD.add(65, new Item(2663, 1)); // Saradomin platelegs
		HARD.add(65, new Item(2665, 1)); // Saradomin full helm
		HARD.add(65, new Item(2667, 1)); // Saradomin kiteshield
		HARD.add(65, new Item(3479, 1)); // Saradomin plateskirt
		HARD.add(25, new Item(3481, 1)); // Gilded platebody
		HARD.add(25, new Item(3483, 1)); // Gilded platelegs
		HARD.add(25, new Item(3485, 1)); // Gilded plateskirt
		HARD.add(25, new Item(3486, 1)); // Gilded full helm
		HARD.add(25, new Item(3488, 1)); // Gilded kiteshield
		HARD.add(25, new Item(12389, 1)); // Gilded scimitar
		HARD.add(25, new Item(12391, 1)); // Gilded boots
		HARD.add(100, new Item(7336, 1)); // Rune shield (h1)
		HARD.add(100, new Item(7342, 1)); // Rune shield (h2)
		HARD.add(100, new Item(7348, 1)); // Rune shield (h3)
		HARD.add(100, new Item(7354, 1)); // Rune shield (h4)
		HARD.add(100, new Item(7360, 1)); // Rune shield (h5)
		HARD.add(100, new Item(10286, 1)); // Rune helm (h1)
		HARD.add(100, new Item(10288, 1)); // Rune helm (h2)
		HARD.add(100, new Item(10290, 1)); // Rune helm (h3)
		HARD.add(100, new Item(10292, 1)); // Rune helm (h4)
		HARD.add(100, new Item(10294, 1)); // Rune helm (h5)
		HARD.add(65, new Item(7374, 1)); // Blue d'hide body (g)
		HARD.add(65, new Item(7376, 1)); // Blue d'hide body (t)
		HARD.add(65, new Item(7382, 1)); // Blue d'hide chaps (g)
		HARD.add(65, new Item(7384, 1)); // Blue d'hide chaps (t)
		HARD.add(65, new Item(7398, 1)); // Enchanted robe
		HARD.add(65, new Item(7399, 1)); // Enchanted top
		HARD.add(65, new Item(7400, 1)); // Enchanted hat
		HARD.add(10, new Item(2581, 1)); // Robin hood hat
		HARD.add(35, new Item(2639, 1)); // Tan cavalier
		HARD.add(35, new Item(2641, 1)); // Dark cavalier
		HARD.add(35, new Item(2643, 1)); // Black cavalier
		HARD.add(35, new Item(12321, 1)); // White cavalier
		HARD.add(35, new Item(12323, 1)); // Red cavalier
		HARD.add(35, new Item(12325, 1)); // Navy cavalier
		HARD.add(45, new Item(2651, 1)); // Pirate's hat
		HARD.add(5, new Item(10346, 1)); // 3rd age platelegs
		HARD.add(5, new Item(10348, 1)); // 3rd age platebody
		HARD.add(5, new Item(10350, 1)); // 3rd age full helmet
		HARD.add(5, new Item(10352, 1)); // 3rd age kiteshield
		HARD.add(5, new Item(10330, 1)); // 3rd age range top
		HARD.add(5, new Item(10332, 1)); // 3rd age range legs
		HARD.add(5, new Item(10334, 1)); // 3rd age range coif
		HARD.add(5, new Item(10336, 1)); // 3rd age vambraces
		HARD.add(5, new Item(10338, 1)); // 3rd age robe top
		HARD.add(5, new Item(10340, 1)); // 3rd age robe
		HARD.add(5, new Item(10342, 1)); // 3rd age mage hat
		HARD.add(5, new Item(10344, 1)); // 3rd age amulet
		HARD.add(5, new Item(12422, 1)); // 3rd age wand
		HARD.add(5, new Item(12424, 1)); // 3rd age bow
		HARD.add(5, new Item(12426, 1)); // 3rd age longsword
		HARD.add(5, new Item(12437, 1)); // 3rd age cloak
		HARD.add(25, new Item(10382, 1)); // Guthix coif
		HARD.add(25, new Item(10380, 1)); // Guthix chaps
		HARD.add(25, new Item(10378, 1)); // Guthix dragonhide
		HARD.add(25, new Item(10376, 1)); // Guthix bracers
		HARD.add(25, new Item(10390, 1)); // Saradomin coif
		HARD.add(25, new Item(10388, 1)); // Saradomin chaps
		HARD.add(25, new Item(10386, 1)); // Saradomin d'hide
		HARD.add(25, new Item(10384, 1)); // Saradomin bracers
		HARD.add(25, new Item(10374, 1)); // Zamorak coif
		HARD.add(25, new Item(10372, 1)); // Zamorak chaps
		HARD.add(25, new Item(10370, 1)); // Zamorak d'hide
		HARD.add(25, new Item(10368, 1)); // Zamorak bracers
		HARD.add(25, new Item(12512, 1)); // Armadyl coif
		HARD.add(25, new Item(12510, 1)); // Armadyl chaps
		HARD.add(25, new Item(12508, 1)); // Armadyl d'hide
		HARD.add(25, new Item(12506, 1)); // Armadyl bracers
		HARD.add(25, new Item(12496, 1)); // Ancient coif
		HARD.add(25, new Item(12494, 1)); // Ancient chaps
		HARD.add(25, new Item(12492, 1)); // Ancient d'hide
		HARD.add(25, new Item(12490, 1)); // Ancient bracers
		HARD.add(25, new Item(12504, 1)); // Bandos coif
		HARD.add(25, new Item(12502, 1)); // Bandos chaps
		HARD.add(25, new Item(12500, 1)); // Bandos d'hide
		HARD.add(25, new Item(12498, 1)); // Bandos bracers
		HARD.add(47, new Item(12518, 1)); // Green dragon mask
		HARD.add(47, new Item(12520, 1)); // Blue dragon mask
		HARD.add(47, new Item(12522, 1)); // Red dragon mask
		HARD.add(47, new Item(12524, 1)); // Black dragon mask
		HARD.add(47, new Item(12516, 1)); // Pith helmet
		HARD.add(20, new Item(12269, 1)); // Bandos stole
		HARD.add(20, new Item(12257, 1)); // Armadyl stole
		HARD.add(20, new Item(12201, 1)); // Ancient stole
		HARD.add(20, new Item(10474, 1)); // Zamorak stole
		HARD.add(20, new Item(10472, 1)); // Guthix stole
		HARD.add(20, new Item(10470, 1)); // Saradomin stole
		HARD.add(20, new Item(12275, 1)); // Bandos crozier
		HARD.add(20, new Item(12263, 1)); // Armadyl crozier
		HARD.add(20, new Item(12199, 1)); // Ancient crozier
		HARD.add(20, new Item(10444, 1)); // Zamorak crozier
		HARD.add(20, new Item(10442, 1)); // Guthix crozier
		HARD.add(20, new Item(10440, 1)); // Saradomin crozier
		HARD.add(10, new Item(12379, 1)); // Rune cane

		CROSS_TRAILS.sort();
		EASY.sort();
		MEDIUM.sort();
		HARD.sort();

		/* Map Scrolls - Easy */
		CLUE_SCROLLS.put(2682, new MapScroll(2682, ClueDifficulty.EASY, new Location(3290, 3372, 0), 7045));
		CLUE_SCROLLS.put(2683, new MapScroll(2683, ClueDifficulty.EASY, new Location(3092, 3226, 0), 7113));
		CLUE_SCROLLS.put(2684, new MapScroll(2684, ClueDifficulty.EASY, new Location(2702, 3428, 0), 7162));
		CLUE_SCROLLS.put(2685, new MapScroll(2685, ClueDifficulty.EASY, new Location(2970, 3414, 0), 17537));

		/* Map Scrolls - Medium */
		CLUE_SCROLLS.put(2803, new MapScroll(2803, ClueDifficulty.MEDIUM, new Location(3043, 3399, 0), 7271));
		CLUE_SCROLLS.put(2805, new MapScroll(2805, ClueDifficulty.MEDIUM, new Location(2616, 3077, 0), 9043));
		CLUE_SCROLLS.put(2807, new MapScroll(2807, ClueDifficulty.MEDIUM, new Location(3109, 3151, 0), 9275));
		CLUE_SCROLLS.put(2809, new MapScroll(2809, ClueDifficulty.MEDIUM, new Location(2722, 3339, 0), 17634));

		/* Emote Scrolls - Easy */
		CLUE_SCROLLS.put(2677, new EmoteScroll(2677, ClueDifficulty.EASY, new Item[] { null, null, null, null, new Item(1133), null, null, new Item(1075), null, null, null, null, null, null }, new Location(2598, 3280, 0), 1, 2110, "", "", "Blow a raspberry at the monkey", "cage in Ardougne Zoo.", "Equip a studded body and bronze", "platelegs.", "", "", ""));
		CLUE_SCROLLS.put(2678, new EmoteScroll(2678, ClueDifficulty.EASY, new Item[] { null, null, new Item(1654), new Item(1237), null, null, null, null, null, null, null, null, new Item(1635), null }, new Location(2977, 3240, 0), 11, 2113, "", "", "Shrug in the mine near Rimmington,", "quip a gold necklace, a gold ring", "and a bronze spear.", "", "", ""));
		CLUE_SCROLLS.put(2679, new EmoteScroll(2679, ClueDifficulty.EASY, new Item[] { null, null, new Item(1696), new Item(845), null, null, null, new Item(1067), null, null, null, null, null, null }, new Location(2728, 3348, 0), 1, 858, "", "", "Bow outside the entrance to the", "Legends' Guild. Equip iron platelegs,", "an emerald amulet and an oak", "longbow.", "", ""));
		CLUE_SCROLLS.put(2680, new EmoteScroll(2680, ClueDifficulty.EASY, new Item[] { new Item(740), null, null, new Item(1307), null, null, null, null, null, null, new Item(1061), null, null, null }, new Location(2926, 3484, 0), 5, 862, "", "", "Cheer at the Druids' Circle. Equip", "a blue wizard hat, a bronze two-", "handed sword and leather boots.", "", "", "", ""));
		CLUE_SCROLLS.put(2681, new EmoteScroll(2681, ClueDifficulty.EASY, new Item[] { null, null, new Item(1694), null, new Item(1103), null, null, null, null, null, null, null, new Item(1639), null }, new Location(2611, 3393, 0), 2, 2106, "", "", "Dance a jig by the entrance to the", "Fishing Guild, equip a sapphire", "amulet, an emerald ring and a", "bronze chainbody", "", "", ""));
		
		/* Emote Scrolls - Medium */
		CLUE_SCROLLS.put(2801, new EmoteScroll(2801, ClueDifficulty.MEDIUM, new Item[] { new Item(658), null, null, new Item(1267), null, null, null, null, null, null, new Item(6328), null, null, null }, new Location(3370, 3428, 0), 1, 859, "", "", "Beckon in the Digsite, near the eastern", "winch. Equip a green hat, snakeskin", "boots and an iron pickaxe.", "", "", "", ""));
	
		/* Emote Scrolls - Hard */
		CLUE_SCROLLS.put(2722, new EmoteScroll(2722, ClueDifficulty.HARD, new Item[] { null, null, null, new Item(1347), null, new Item(2890), null, new Item(2493), null, null, null, null, null, null }, new Location(2587, 3420, 0), 2, 2110, "", "", "Blow a raspberry in the Fishing Guild", "bank. Beware of double agents!", "Equip an elemental shield, blue", "dragonhide chaps and a rune", "warhammer", "", ""));
		CLUE_SCROLLS.put(2723, new EmoteScroll(2723, ClueDifficulty.HARD, new Item[] { null, null, new Item(1731), null, null, null, null, null, null, null, null, null, new Item(1643), null }, new Location(2920, 3163, 0), 10, 2112, "", "Salute in the banana plantation.", "Beware of double agents!", "Equip a diamond ring, amulet", "of power and nothing on", "your chest and legs.", "", "", ""));
		
	}

	/**
	 * Generates a random clue scroll by difficulty.
	 *
	 * @param player
	 *            - The player to generate the clue scroll for.
	 * @param difficulty
	 *            - The difficulty of the clue scroll to generate.
	 * @return The clue scroll as an {@link Item}.
	 */
	public static Item getRandomClue(Player player, ClueDifficulty difficulty) {
		List<ClueScroll> scrolls = CLUE_SCROLLS.values().stream().filter(scroll -> checkScroll(player, scroll, difficulty)).collect(Collectors.toList());

		if (scrolls.isEmpty()) {
			return null;
		}

		return new Item(Utility.randomElement(scrolls).getScrollId());
	}

	private static final boolean checkScroll(Player player, ClueScroll scroll, ClueDifficulty difficulty) {
		int scrollId = scroll.getScrollId();

		List<ClueScroll> difficultyScrolls = CLUE_SCROLLS.values().stream().filter(s -> s.getDifficulty() == difficulty).collect(Collectors.toList());

		for (ClueScroll s : difficultyScrolls) {
			if (player.getBank().hasItemId(s.getScrollId()) || player.getInventory().hasItemId(s.getScrollId())) {
				return false;
			}
		}

		return scroll.getDifficulty().equals(difficulty) && !player.getBank().hasItemId(scrollId) && !player.getInventory().hasItemId(scrollId);
	}

	/**
	 * Gives a reward for a specific scroll.
	 * 
	 * @param player
	 *            - The player receiving the reward.
	 * @param difficulty
	 *            - The rewards table.
	 */
	public static void reward(Player player, int item, ClueDifficulty difficulty) {
		if (player.getInventory().getFreeSlots() < 6) {
			player.send(new SendMessage("You need at least 6 free slots to open this casket."));
			return;
		}

		player.getInventory().remove(item, 1);
		player.send(new SendRemoveInterfaces());

		ItemContainer items = new ItemContainer(9, ContainerTypes.ALWAYS_STACK, true, true) {
			@Override
			public boolean allowZero(int paramInt) {
				return false;
			}

			@Override
			public void onAdd(Item paramItem) {
			}

			@Override
			public void onFillContainer() {
			}

			@Override
			public void onMaxStack() {
			}

			@Override
			public void onRemove(Item paramItem) {
			}

			@Override
			public void update() {
				player.send(new SendUpdateItems(6963, items));
			}
		};

		int length = 3 + Utility.random(3);

		for (int i = 0; i < length; i++) {
			Item reward;

			do {
				reward = difficulty.getRewards().getReward();
			} while (items.hasItemId(reward.getId()));

			items.add(reward, false);

			int amount = reward.getAmount();

			if (amount > 1) {
				amount = Utility.randomNumber(amount) + 1;
			}

			player.getInventory().add(reward.getId(), amount, false);
		}

		items.update();
		player.getInventory().update();
		player.send(new SendInterface(6960));
		
		for (int i = 0; i < items.items.length; i++) {
			if (items.items[i] == null) {
				continue;
			}
			if (items.items[i].getDefinition().getGeneralPrice() >= 500_000 || items.items[i].getDefinition().getName().contains("ornament") || items.items[i].getDefinition().getName().contains("Gilded") || items.items[i].getDefinition().getName().contains("Ranger") || items.items[i].getDefinition().getName().contains("Robin") || items.items[i].getDefinition().getName().contains("3rd")) {
				World.sendGlobalMessage("<img=8> <col=C42BAD>" + player.determineIcon(player) + Utility.formatPlayerName(player.getUsername()) + " has recieved " + Utility.determineIndefiniteArticle(items.items[i].getDefinition().getName()) + " " + items.items[i].getDefinition().getName() + " from a " + Utility.capitalize(difficulty.name().toLowerCase()) + " clue scroll.");
				
			}
		}
		
	}

	/**
	 * Provides a method to retrieve a random reward.
	 *
	 * @author Michael | Chex
	 */
	public interface ClueReward {

		/**
		 * Generates a random reward.
		 *
		 * @return The reward as an {@link com.visionary.entity.item.Item}.
		 */
		public Item getReward();
	}

	/**
	 * Handles item clicking for any related items for clue scrolls.
	 * 
	 * @param player
	 *            - The player opening the scroll.
	 * @param item
	 *            - The item id.
	 * @return <code>True</code> if any code in the method was executed;
	 *         <code>False</code> otherwise.
	 */
	public boolean clickItem(Player player, int item) {
		ClueScroll scroll = CLUE_SCROLLS.get(item);

		if (scroll == null) {
			switch (item) {
			case CASKET_EASY:
				reward(player, item, ClueDifficulty.EASY);
				return true;
			case CASKET_MEDIUM:
				reward(player, item, ClueDifficulty.MEDIUM);
				return true;
			case CASKET_HARD:
				reward(player, item, ClueDifficulty.HARD);
				return true;
			}
			return false;
		}

		scroll.displayClue(player);

		return true;
	}

	public boolean dig(Player player) {
		List<ClueScroll> emoteScrolls = CLUE_SCROLLS.values().stream().filter(scroll -> scroll.getClueType() == ClueType.MAP || scroll.getClueType() == ClueType.COORDINATE).collect(Collectors.toList());

		if (emoteScrolls.isEmpty()) {
			return false;
		}

		for (ClueScroll scroll : emoteScrolls) {
			if (scroll.meetsRequirements(player)) {
				return scroll.execute(player);
			}
		}

		return false;
	}

	public void handleEmote(Player player, Emote emote) {
		List<ClueScroll> emoteScrolls = CLUE_SCROLLS.values().stream().filter(scroll -> scroll.getClueType() == ClueType.EMOTE).collect(Collectors.toList());

		if (emoteScrolls.isEmpty()) {
			return;
		}

		for (ClueScroll scroll : emoteScrolls) {
			if (scroll.meetsRequirements(player)) {
				if (emote.animID == ((EmoteScroll) scroll).getAnimationId()) {
					scroll.execute(player);
				}
				break;
			}
		}
	}

	public static ClueScroll getClue(int id) {
		return CLUE_SCROLLS.get(id);
	}

	public static boolean playerHasScroll(Player player) {
		for (int i = 0; i < player.getInventory().getItems().length; i++) {
			if (player.getInventory().getItems()[i] != null && getClue(player.getInventory().getItems()[i].getId()) != null) {
				return true;
			}
		}
		return false;
	}
}