package com.vencillio.rs2.entity.object;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import com.vencillio.core.cache.map.MapLoading;
import com.vencillio.core.cache.map.RSObject;
import com.vencillio.core.cache.map.Region;
import com.vencillio.rs2.content.skill.firemaking.FireColor;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;

@SuppressWarnings("all")
public class ObjectManager {

	public static final int BLANK_OBJECT_ID = 2376;

	private static final List<GameObject> active = new LinkedList<GameObject>();
	private static final Deque<GameObject> register = new LinkedList<GameObject>();

	private static final Queue<GameObject> send = new ConcurrentLinkedQueue<GameObject>();

	public static void add(GameObject o) {
		active.add(o);
	}

	public static void addClippedObject(GameObject o) {
		register.add(o);
	}

	public static void declare() {

		for (GameObject i : active) {
			send(getBlankObject(i.getLocation()));
		}

		active.clear();
		
		/** Home Area */
		spawnWithObject(410, 3078, 3484, 0, 10, 0);//Lunar Altar
		spawnWithObject(6552, 3084, 3483, 0, 10, 0);//Ancient Altar
		spawnWithObject(409, 3088, 3483, 0, 10, 0);//Altar	
		spawnWithObject(4875, 3094, 3500, 0, 10, 5);//Food stall
		spawnWithObject(4876, 3095, 3500, 0, 10, 5);//General stall
		spawnWithObject(4874, 3096, 3500, 0, 10, 5);//Crafting stall
		spawnWithObject(4877, 3097, 3500, 0, 10, 5);//Magic stall
		spawnWithObject(4878, 3098, 3500, 0, 10, 5);//Scmitar stall
		spawnWithObject(2191, 3081, 3499, 0, 10, 3);//Crystal chest
		spawnWithObject(9472, 3090, 3492, 0, 10, 0);//Shop Exchange
		
		/* Membership Area */
		deleteWithObject(2822, 3356, 0);
		deleteWithObject(2822, 3355, 0);
		deleteWithObject(2822, 3351, 0);
		deleteWithObject(2822, 3350, 0);
		deleteWithObject(2818, 3351, 0);
		deleteWithObject(2818, 3355, 0);
		deleteWithObject(2817, 3355, 0);
		deleteWithObject(2816, 3354, 0);
		deleteWithObject(2818, 3356, 0);
		deleteWithObject(2816, 3352, 0);
		deleteWithObject(2817, 3353, 0);
		deleteWithObject(2816, 3351, 0);
		deleteWithObject(2821, 3357, 0);
		deleteWithObject(2822, 3360, 0);
		deleteWithObject(2822, 3361, 0);
		deleteWithObject(2821, 3360, 0);
		deleteWithObject(2821, 3361, 0);
		deleteWithObject(2820, 3360, 0);
		deleteWithObject(2820, 3361, 0);	
		deleteWithObject(2819, 3360, 0);
		deleteWithObject(2819, 3361, 0);
		deleteWithObject(2818, 3360, 0);
		deleteWithObject(2818, 3361, 0);
		deleteWithObject(2817, 3360, 0);
		deleteWithObject(2817, 3361, 0);
		deleteWithObject(2817, 3359, 0);
		deleteWithObject(2817, 3358, 0);
		deleteWithObject(2817, 3357, 0);
		deleteWithObject(2857, 3338, 0);
		deleteWithObject(2859, 3338, 0);
		deleteWithObject(2860, 3338, 0);
		deleteWithObject(2862, 3338, 0);
		deleteWithObject(2861, 3335, 0);
		deleteWithObject(2862, 3335, 0);	
		deleteWithObject(2844, 3333, 0);
		deleteWithObject(2845, 3337, 0);
		deleteWithObject(2844, 3337, 0);
		deleteWithObject(2845, 3338, 0);
		deleteWithObject(2844, 3338, 0);
		deleteWithObject(2853, 3355, 0);
		deleteWithObject(2853, 3353, 0);
		deleteWithObject(2849, 3353, 0);
		deleteWithObject(2849, 3354, 0);
		deleteWithObject(2849, 3355, 0);
		deleteWithObject(2851, 3353, 0);
		deleteWithObject(2809, 3341, 0);
		deleteWithObject(2812, 3341, 0);
		deleteWithObject(2812, 3343, 0);
		deleteWithObject(2810, 3342, 0);
		deleteWithObject(2808, 3343, 0);
		deleteWithObject(2808, 3346, 0);
		deleteWithObject(2809, 3346, 0);
		deleteWithObject(2810, 3346, 0);
		deleteWithObject(2812, 3346, 0);
		deleteWithObject(2807, 3354, 0);
		deleteWithObject(2807, 3355, 0);
		deleteWithObject(2806, 3355, 0);
		deleteWithObject(2806, 3356, 0);
		deleteWithObject(2807, 3356, 0);
		deleteWithObject(2808, 3356, 0);				
		deleteWithObject(2830, 3350, 0);
		deleteWithObject(2831, 3348, 0);
		deleteWithObject(2830, 3349, 0);
		deleteWithObject(2816, 3361, 0);	
		deleteWithObject(2812, 3364, 0);
		deleteWithObject(2814, 3364, 0);
		deleteWithObject(2816, 3363, 0);
		deleteWithObject(2818, 3363, 0);
		deleteWithObject(2819, 3362, 0);	
		deleteWithObject(2815, 3358, 0);
		deleteWithObject(2814, 3357, 0);	
		deleteWithObject(2835, 3355, 0);	
		spawnWithObject(11744, 2816, 3358, 0, 10, 3);//Banks
		spawnWithObject(11744, 2816, 3357, 0, 10, 3);//Banks	
		spawnWithObject(11744, 2810, 3343, 0, 10, 3);//Banks
		spawnWithObject(11744, 2874, 3339, 0, 10, 3);//Banks
		spawnWithObject(11744, 2874, 3340, 0, 10, 3);//Banks
		spawnWithObject(11744, 2829, 3351, 0, 10, 3);//Banks
		spawnWithObject(11744, 2816, 3356, 0, 10, 3);//Banks
		spawnWithObject(11744, 2816, 3355, 0, 10, 3);//Banks
		spawnWithObject(11744, 2816, 3354, 0, 10, 3);//Banks
		spawnWithObject(11744, 2816, 3353, 0, 10, 3);//Banks
		spawnWithObject(11744, 2816, 3352, 0, 10, 3);//Banks
		spawnWithObject(11744, 2816, 3351, 0, 10, 3);//Banks		
		spawnWithObject(11744, 2809, 3347, 0, 10, 0);//Banks				
		spawnWithObject(11744, 2827, 3355, 0, 10, 0);//Banks		
		spawnWithObject(9472, 2818, 3351, 0, 10, 5);//Shop Exchange
		spawnWithObject(11744, 2857, 3338, 0, 10, 0);//Banks
		spawnWithObject(4875, 2863, 3338, 0, 10, 5);//Food stall
		spawnWithObject(4876, 2862, 3338, 0, 10, 5);//General stall
		spawnWithObject(4874, 2861, 3338, 0, 10, 5);//Crafting stall
		spawnWithObject(4877, 2860, 3338, 0, 10, 5);//Magic stall
		spawnWithObject(4878, 2859, 3338, 0, 10, 5);//Scmitar stall
		spawnWithObject(26181, 2874, 3333, 0, 10, 0);//Range
		spawnWithObject(4309, 2847, 3333, 0, 10, 2);//Spinning wheel
		spawnWithObject(11601, 2845, 3333, 0, 10, 3);//Pottery
		spawnWithObject(22472, 2844, 3338, 0, 10, 2);//Tab creation
		spawnWithObject(13618, 2850, 3355, 0, 10, 0);//Wyvern teleport
		spawnWithObject(13619, 2853, 3353, 0, 10, 1);//Fountain of rune teleport
		spawnWithObject(2191, 2818, 3356, 0, 10, 4);//Crystal chest
		spawnWithObject(18772, 2821, 3358, 0, 10, 1);//MysteryBox chest
		spawnWithObject(2097, 2830, 3349, 0, 10, 1);//Anvil
		spawnWithObject(11764, 2811, 3361, 0, 10, 1);//Magic Tree
		spawnWithObject(11764, 2810, 3359, 0, 10, 1);//Magic Tree
		spawnWithObject(11764, 2815, 3361, 0, 10, 1);//Magic Tree
		spawnWithObject(11764, 2815, 3359, 0, 10, 1);//Magic Tree
		spawnWithObject(11764, 2812, 3364, 0, 10, 1);//Magic Tree
		spawnWithObject(11764, 2814, 3364, 0, 10, 1);//Magic Tree
		spawnWithObject(11758, 2809, 3356, 0, 10, 1);//Yew Tree
		spawnWithObject(11758, 2809, 3353, 0, 10, 1);//Yew Tree
		spawnWithObject(11758, 2809, 3350, 0, 10, 1);//Yew Tree
		spawnWithObject(11762, 2804, 3344, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2804, 3346, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2806, 3348, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2806, 3351, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2805, 3353, 0, 10, 1);//Maple Tree
		spawnWithObject(14175, 2824, 3359, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2824, 3358, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2824, 3357, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2826, 3359, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2825, 3356, 0, 10, 1);//Rune Ore		
		spawnWithObject(13720, 2828, 3358, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2829, 3358, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2830, 3358, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2831, 3357, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2831, 3356, 0, 10, 1);//Adamant Ore		
		spawnWithObject(13707, 2830, 3354, 0, 10, 1);//Gold Ore
		spawnWithObject(13707, 2831, 3354, 0, 10, 1);//Gold Ore
		spawnWithObject(13707, 2832, 3354, 0, 10, 1);//Gold Ore
		spawnWithObject(13707, 2833, 3354, 0, 10, 1);//Gold Ore
		spawnWithObject(13706, 2833, 3356, 0, 10, 1);//Coal
		spawnWithObject(13706, 2834, 3356, 0, 10, 1);//Coal
		spawnWithObject(13706, 2835, 3356, 0, 10, 1);//Coal
		spawnWithObject(13706, 2835, 3354, 0, 10, 1);//Coal
		
		
		/* Wilderness Resource Arena */
		spawnWithObject(14175, 3195, 3942, 0, 10, 3);
		spawnWithObject(14175, 3194, 3943, 0, 10, 3);
		spawnWithObject(14175, 3175, 3937, 0, 10, 3);
		spawnWithObject(14175, 3175, 3943, 0, 10, 3);
		
		/* Blood crafting */
		spawnWithObject(4090, 2792, 3322, 0, 10, 0);//Altar
	
		/* Crafting */
		spawnWithObject(4309, 2751, 3446, 0, 10, 3);//Spinning wheel
		spawnWithObject(11601, 2751, 3449, 0, 10, 2);//Pottery
		
		/** Rune ores at mining */
		spawnWithObject(14175, 3051, 9765, 0, 10, 3);
		spawnWithObject(14175, 3052, 9766, 0, 10, 3);
		
		/** Smelting furnace */
		spawnWithObject(2030, 3191, 3425, 0, 10, 0);
		
		/** Weapon Game **/
		deleteWithObject(1863, 5328, 0);
		deleteWithObject(1863, 5326, 0);
		deleteWithObject(1863, 5323, 0);
		deleteWithObject(1862, 5327, 0);
		deleteWithObject(1862, 5326, 0);
		deleteWithObject(1862, 5325, 0);
		deleteWithObject(1865, 5325, 0);
		deleteWithObject(1863, 5321, 0);
		deleteWithObject(1865, 5321, 0);
		deleteWithObject(1865, 5323, 0);
		deleteWithObject(1863, 5319, 0);
		deleteWithObject(1862, 5319, 0);
		deleteWithObject(1863, 5317, 0);
		deleteWithObject(1865, 5319, 0);
		deleteWithObject(1862, 5321, 0);
		deleteWithObject(1862, 5323, 0);
		spawnWithObject(1, 1866, 5323, 0, 10, 0);//Barrier	
		spawnWithObject(1, 1865, 5323, 0, 10, 0);//Barrier	
		spawnWithObject(11005, 1864, 5323, 0, 10, 1);//Barrier	
		spawnWithObject(11005, 1863, 5323, 0, 10, 1);//Barrier	
		spawnWithObject(1, 1862, 5323, 0, 10, 0);//Barrier	
		spawnWithObject(1, 1861, 5323, 0, 10, 0);//Barrier	
		spawnWithObject(11744, 1861, 5330, 0, 10, 0);//Barrier	
		spawnWithObject(11744, 1862, 5330, 0, 10, 0);//Barrier	
		spawnWithObject(11744, 1863, 5330, 0, 10, 0);//Barrier	
		spawnWithObject(11744, 1864, 5330, 0, 10, 0);//Barrier
		spawnWithObject(11744, 1865, 5330, 0, 10, 0);//Barrier
		spawnWithObject(11744, 1866, 5330, 0, 10, 0);//Barrier
		
		/** Duel Arena */
		spawnWithObject(409, 3366, 3271, 0, 10, 10);//Altar	
		spawnWithObject(6552, 3370, 3271, 0, 10, 10);//Ancient Altar
		
		/* Crafting Area */
		spawnWithObject(11744, 2748, 3451, 0, 10, 0);// Banks
		
		/** Farming Areas */
		spawnWithObject(11744, 2804, 3463, 0, 10, 1);// Catherby Banks
		spawnWithObject(11744, 3599, 3522, 0, 10, 0);// Banks
		spawnWithObject(11744, 3056, 3311, 0, 10, 0);// Banks
		spawnWithObject(11744, 2662, 3375, 0, 10, 0);// Banks

		/** Mining banks */
		spawnWithObject(11744, 3047, 9765, 0, 10, 0);
		spawnWithObject(11744, 3045, 9765, 0, 10, 0);
		spawnWithObject(11744, 3044, 9776, 0, 10, 0);
		spawnWithObject(11744, 3045, 9776, 0, 10, 0);
		spawnWithObject(11744, 3046, 9776, 0, 10, 0);
		spawnWithObject(11744, 2930, 4821, 0, 10, 0);// Essences
		
		/** Deleting Objects */
		delete(3079, 3501, 0);//Home gate
		delete(3080, 3501, 0);//Home gate
		delete(3445, 3554, 2);//Slayer tower door
		
		/** New Home */
		deleteWithObject(3284, 3510, 0);		
		deleteWithObject(3286, 3497, 0);
		deleteWithObject(3286, 3498, 0);
		deleteWithObject(3286, 3500, 0);
		deleteWithObject(3288, 3502, 0);		
		deleteWithObject(3288, 3497, 0);	
		deleteWithObject(3286, 3502, 0);
		deleteWithObject(3287, 3498, 0);
		deleteWithObject(3287, 3500, 0);
		deleteWithObject(3284, 3510, 0);
		deleteWithObject(3282, 3497, 0);
		deleteWithObject(3283, 3497, 0);
		deleteWithObject(3283, 3500, 0);
		deleteWithObject(3283, 3500, 0);
		deleteWithObject(3277, 3498, 0);
		deleteWithObject(3278, 3497, 0);
		deleteWithObject(3277, 3497, 0);
		deleteWithObject(3278, 3500, 0);
		deleteWithObject(3277, 3500, 0);
		deleteWithObject(3282, 3499, 0);
		deleteWithObject(3277, 3493, 0);
		deleteWithObject(3279, 3493, 0);
		deleteWithObject(3276, 3493, 0);
		deleteWithObject(3276, 3494, 0);
		deleteWithObject(3278, 3492, 0);
		deleteWithObject(3282, 3493, 0);
		deleteWithObject(3282, 3495, 0);
		deleteWithObject(3283, 3495, 0);
		deleteWithObject(3285, 3493, 0);
		deleteWithObject(3284, 3503, 0);
		deleteWithObject(3285, 3503, 0);
		deleteWithObject(3286, 3503, 0);
		deleteWithObject(3286, 3504, 0);
		deleteWithObject(3287, 3504, 0);
		deleteWithObject(3288, 3504, 0);
		deleteWithObject(3284, 3504, 0);
		deleteWithObject(3276, 3503, 0);
		deleteWithObject(3277, 3503, 0);
		deleteWithObject(3278, 3503, 0);		
		deleteWithObject(3282, 3498, 0);		
		deleteWithObject(3284, 3501, 0);	
		deleteWithObject(3284, 3496, 0);
		deleteWithObject(3282, 3500, 0);
		deleteWithObject(3277, 3496, 0);
		
		deleteWithObject(3278, 3504, 0);
		deleteWithObject(3276, 3504, 0);
		deleteWithObject(3275, 3496, 0);
		deleteWithObject(3275, 3497, 0);
		deleteWithObject(3277, 3501, 0);
		
		remove(3286, 3508, 0);
		remove(3286, 3509, 0);
		remove(3286, 3510, 0);		
		remove(3275, 3509, 0);
		remove(3275, 3510, 0);
		remove(3276, 3510, 0);
		
		spawnWithObject(11744, 3287, 3502, 0, 10, 1);// Banks
		spawnWithObject(11744, 3287, 3501, 0, 10, 1);// Banks
		spawnWithObject(11744, 3287, 3500, 0, 10, 1);// Banks
		spawnWithObject(11744, 3287, 3499, 0, 10, 1);// Banks		
		spawnWithObject(11744, 3287, 3498, 0, 10, 1);// Banks
		spawnWithObject(11744, 3287, 3497, 0, 10, 1);// Banks		
		spawnWithObject(9472, 3286, 3495, 0, 10, 5);//Shop Exchange
		spawnWithObject(8720, 3286, 3494, 0, 10, 2);//Vote		
		spawnWithObject(4875, 3282, 3507, 0, 10, 5);//Food stall
		spawnWithObject(4876, 3283, 3507, 0, 10, 5);//General stall
		spawnWithObject(4874, 3284, 3507, 0, 10, 5);//Crafting stall
		spawnWithObject(4877, 3285, 3507, 0, 10, 5);//Magic stall
		spawnWithObject(4878, 3286, 3507, 0, 10, 5);//Scimitar stall
		
		
		spawnWithObject(409, 3275, 3508, 0, 10, 1);//Altar	
		
		spawnWithObject(412, 3277, 3507, 0, 10, 10);//Ancient Altar
		
		
		/** Webs */
		delete(3105, 3958, 0);
		delete(3106, 3958, 0);
		delete(3093, 3957, 0);
		delete(3095, 3957, 0);
		delete(3092, 3957, 0);
		delete(3158, 3951, 0);
		deleteWithObject(2543, 4715, 0);
		spawnWithObject(734, 3105, 3958, 0, 10, 3);
		spawnWithObject(734, 3106, 3958, 0, 10, 3);
		spawnWithObject(734, 3158, 3951, 0, 10, 1);
		spawnWithObject(734, 3093, 3957, 0, 10, 0);
		spawnWithObject(734, 3095, 3957, 0, 10, 0);	
		delete(2543, 4715, 0);	
		delete(2855, 3546, 0);
		delete(2854, 3546, 0);

		/** Clipping */
		setClipToZero(3445, 3554, 2);
		setClipToZero(3119, 9850, 0);
		setClipToZero(3002, 3961, 0);
		setClipToZero(3002, 3960, 0);
		setClipToZero(2539, 4716, 0);
		setClipToZero(3068, 10255, 0);
		setClipToZero(3068, 10256, 0);
		setClipToZero(3068, 10258, 0);
		setClipToZero(3067, 10255, 0);
		setClipToZero(3066, 10256, 0);
		setClipToZero(3426, 3555, 1);
		setClipToZero(3427, 3555, 1);
		setClipToZero(3005, 3953, 0);
		setClipToZero(3005, 3952, 0);
		setClipToZero(2551, 3554, 0);
		setClipToZero(2551, 3555, 0);
		setClipToZero(2833, 3352, 0);
		setClipToZero(2996, 3960, 0);

		for (GameObject i : active) {
			send(i);
		}

		logger.info("All object spawns have been loaded successfully.");
	}
	
	private static Logger logger = Logger.getLogger(MapLoading.class.getSimpleName());

	private static final void delete(int x, int y, int z) {
		RSObject object = Region.getObject(x, y, z);

		if (Region.getDoor(x, y, z) != null) {
			Region.removeDoor(x, y, z);
		}

		if (object == null) {
			if (z > 0)
				active.add(new GameObject(2376, x, y, z, 10, 0));
			return;
		}

		MapLoading.removeObject(object.getId(), x, y, z, object.getType(), object.getFace());

		if ((object.getType() != 10) || (z > 0))
			active.add(new GameObject(2376, x, y, z, object.getType(), 0));
	}

	private static final void deleteWithObject(int x, int y, int z) {
		RSObject object = Region.getObject(x, y, z);

		if (Region.getDoor(x, y, z) != null) {
			Region.removeDoor(x, y, z);
		}

		if (object == null) {
			active.add(new GameObject(2376, x, y, z, 10, 0));
			return;
		}

		MapLoading.removeObject(object.getId(), x, y, z, object.getType(), object.getFace());

		active.add(new GameObject(2376, x, y, z, object.getType(), 0));
	}
	
	private static final void remove(int x, int y, int z) {
		RSObject object = Region.getObject(x, y, z);
		
		if (Region.getDoor(x, y, z) != null) {
			Region.removeDoor(x, y, z);
		}
		
		if (object == null) {
			active.add(new GameObject(2376, x, y, z, 10, 0));
			return;
		}
		
		MapLoading.removeObject(object.getId(), x, y, z, object.getType(), object.getFace());
		
		active.add(new GameObject(2376, x, y, z, object.getType(), 0));
		Region region = Region.getRegion(x, y);

		region.setClipToZero(x, y, z);
	}
	

	private static final void deleteWithObject(int x, int y, int z, int type) {
		active.add(new GameObject(2376, x, y, z, type, 0));
	}

	public static List<GameObject> getActive() {
		return active;
	}

	public static final GameObject getBlankObject(Location p) {
		return new GameObject(2376, p.getX(), p.getY(), p.getZ(), 10, 0, false);
	}

	public static GameObject getBlankObject(Location p, int type) {
		return new GameObject(2376, p.getX(), p.getY(), p.getZ(), type, 0, false);
	}

	public static GameObject getGameObject(int x, int y, int z) {
		int index = active.indexOf(new GameObject(x, y, z));

		if (index == -1) {
			return null;
		}

		return active.get(index);
	}

	public static Queue<GameObject> getSend() {
		return send;
	}

	public static boolean objectExists(Location location) {
		for (GameObject object : active) {
			if (location.equals(object.getLocation())) {
				return true;
			}
		}
		return false;
	}

	public static void process() {
		for (Iterator<GameObject> i = register.iterator(); i.hasNext();) {
			GameObject reg = i.next();
			active.remove(reg);
			active.add(reg);
			send.add(reg);

			i.remove();
		}
	}

	public static void queueSend(GameObject o) {
		send.add(o);
	}

	public static void register(GameObject o) {
		register.add(o);
	}

	public static void remove(GameObject o) {
		removeFromList(o);
		send.add(getBlankObject(o.getLocation(), o.getType()));
	}

	public static void remove2(GameObject o) {
		send.add(getBlankObject(o.getLocation(), o.getType()));
	}

	public static void removeFromList(GameObject o) {
		active.remove(o);
	}

	private static final void removeWithoutClip(int x, int y, int z, int type) {
	}

	public static void send(GameObject o) {
		for (Player player : World.getPlayers())
			if ((player != null) && (player.isActive())) {
				if ((player.withinRegion(o.getLocation())) && (player.getLocation().getZ() % 4 == o.getLocation().getZ() % 4))
					player.getObjects().add(o);
			}
	}

	public static void setClipToZero(int x, int y, int z) {
		Region region = Region.getRegion(x, y);

		region.setClipToZero(x, y, z);
	}

	public static void setClipped(int x, int y, int z) {
		Region region = Region.getRegion(x, y);

		region.setClipping(x, y, z, 0x12801ff);
	}

	public static void setProjecileClipToInfinity(int x, int y, int z) {
		Region region = Region.getRegion(x, y);

		region.setProjecileClipToInfinity(x, y, z);
	}

	private static final void spawn(int id, int x, int y, int z, int type, int face) {
		MapLoading.addObject(false, id, x, y, z, type, face);
	}
	
	public static final void spawnWithObject(int id, Location location, int type, int face) {
		active.add(new GameObject(id, location.getX(), location.getY(), location.getZ(), type, face));
		MapLoading.addObject(false, id, location.getX(), location.getY(), location.getZ(), type, face);
	
		send(new GameObject(id, location.getX(), location.getY(), location.getZ(), type, face));
	}

	public static final void spawnWithObject(int id, int x, int y, int z, int type, int face) {
		active.add(new GameObject(id, x, y, z, type, face));
		MapLoading.addObject(false, id, x, y, z, type, face);

		send(new GameObject(id, x, y, z, type, face));
	}
}
