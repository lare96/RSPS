package com.vencillio.core.cache.map;

import java.util.logging.Logger;

import com.vencillio.core.cache.ByteStreamExt;
import com.vencillio.core.util.Utility;

public final class ObjectDef {
	
	/**
	 * The logger for the class
	 */
	private static Logger logger = Logger.getLogger(ObjectDef.class.getSimpleName());

	private static ByteStreamExt stream;
	public static int[] streamIndices;
	public static ObjectDef class46;

	private static int objects = 0;

	public static int getObjects() {
		return objects;
	}

	public static ObjectDef getObjectDef(int i) {
		if (i > streamIndices.length) {
			i = streamIndices.length - 1;
		}

		for (int j = 0; j < 20; j++) {
			if (cache[j].type == i) {
				return cache[j];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		class46 = cache[cacheIndex];

		if (i > streamIndices.length - 1 || i < 0) {
			return null;
		}

		stream.currentOffset = streamIndices[i];

		class46.type = i;
		class46.setDefaults();
		class46.readValues(stream);

		if (i == 14210 || i == 14211) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = false;
			class46.objectSizeX = 2;
		}

		if (i == 14438 || i == 14437) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
		}

		if (i == 486) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
		}

		if (i == 14695) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
			class46.objectSizeX = 1;
		}

		if (i == 734) {
			class46.aBoolean779 = false;
			class46.aBoolean757 = false;
		}

		if (i == 509 || i == 510 || i == 511) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
		}

		if (i == 9292) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
			class46.objectSizeX = 1;
		}

		if (i == 9382) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
		}

		if (i == 9381) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
		}

		if (i == 9372) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
		}

		if (i == 9360) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
		}

		if (i == 24265) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
		}

		if (i == 9374) {
			class46.aBoolean779 = true;
			class46.aBoolean757 = true;
		}

		if (class46.name != null && class46.name.equalsIgnoreCase("flowerbed")) {
			class46.aBoolean779 = false;
			class46.aBoolean757 = false;
		}

		if (class46.name != null && class46.name.equalsIgnoreCase("jungle plant")) {
			class46.aBoolean779 = false;
			class46.aBoolean757 = false;
		}

		if (class46.name != null && class46.name.equalsIgnoreCase("creeping plant")) {
			class46.aBoolean779 = false;
			class46.aBoolean757 = false;
		}

		if (class46.name != null && class46.name.equalsIgnoreCase("flowers")) {
			class46.aBoolean779 = false;
			class46.aBoolean757 = false;
		}

		if (class46.name != null && class46.name.equalsIgnoreCase("sunflowers")) {
			class46.aBoolean779 = false;
			class46.aBoolean757 = false;
		}

		if (class46.name != null && class46.name.equalsIgnoreCase("flower")) {
			class46.aBoolean779 = false;
			class46.aBoolean757 = false;
		}

		if (class46.name != null && class46.name.equalsIgnoreCase("daisies") || i == 38692) {
			class46.aBoolean779 = false;
			class46.aBoolean757 = false;
		}
		
		switch (i) {
		// hardcode objects that don't have any actions.
		case 26621:
		case 26620:
		case 26619:
		case 26618:
			
		case 26561:
		case 26562:
			
		case 7836:
		case 7837:
		case 7838:
		case 7839:
		case 7848:
		case 7847:
		case 7849:
		case 7850:
		case 8150:
		case 8151:
		case 8152:
		case 8153:
		case 8550:
		case 8551:
		case 8552:
		case 8553:
		case 8554:
		case 8555:
		case 8556:
		case 8557:
		case 20973:			
		case 26606:
		case 26611:
		case 26613:
		case 26626:
		case 26608:
		case 26607:
		case 26603:
		case 26580:
		case 26604:
		case 26609:
		case 26600:
		case 26601:
		case 26610:
		case 26616:
		case 26612:
		case 26605:
		case 26602:
			class46.hasActions = true;
			break;

		case 8720:
		case 26492:
		case 26796:
		case 26797:
		case 26798:
		case 26799:
		case 26800:
		case 26801:
		case 26802:
		case 26803:
		case 26804:
		case 26805:
		case 26806:
		case 26807:
		case 26808:
		case 26809:
		case 26810:
		case 26811:
		case 26812:
		case 26813:
		case 26814:
		case 26815:
		case 26816:
		case 26817:
		case 26818:
		case 26819:
		case 26820:
		case 26821:
			class46.name = "Vote Toll";
			class46.actions = new String[5];
			class46.actions[0] = "Vote";
			class46.actions[1] = "Open store";
			class46.actions[2] = "Info";
			class46.hasActions = true;
			break;

		case 9472:// Shop Exchange
		case 9371:
			class46.name = "Shop Exchange";
			class46.actions = new String[5];
			class46.actions[0] = "Info";
			class46.actions[1] = "Edit Shop";
			class46.actions[2] = "Explore Shops";
			break;
			
		case 22472://Tab Creation
			class46.name = "Tablet";
			class46.actions = new String[5];
			class46.actions[0] = "Create";
			break;
			
        case 2732://Fires
        case 11404:
        case 11405:
        case 11406:
            class46.actions = new String[5];
            class46.actions[0] = "Add-Logs";
            class46.actions[1] = "Change-color";
            break;
            
        case 574:
        	class46.name = "Magic book";
        	class46.actions = new String[5];
        	class46.actions[0] = "Change";
        	break;
        	
        case 575:
        	class46.name = "Altar";
        	class46.actions = new String[5];
        	class46.actions[0] = "Restore";
        	break;
        	
        case 576:
        	class46.name = "Highscores";
        	class46.actions = new String[5];
        	class46.actions[0] = "View";
        	break;
            
        case 4090:
        	class46.name = "Blood altar";
        	class46.actions = new String[5];
        	class46.actions[0] = "Craft";
        	break;

		}

		return class46;
	}

	private void setDefaults() {
		anIntArray773 = null;
		anIntArray776 = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		objectSizeX = 1;
		objectSizeY = 1;
		aBoolean767 = true;
		aBoolean757 = true;
		hasActions = false;
		aBoolean762 = false;
		aBoolean764 = false;
		anInt781 = -1;
		anInt775 = 16;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean779 = true;
		anInt768 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		anInt774 = -1;
		anInt749 = -1;
		childrenIDs = null;
	}

	public static void loadConfig() {
		stream = new ByteStreamExt(getBuffer("loc.dat"));
		ByteStreamExt stream = new ByteStreamExt(getBuffer("loc.idx"));
		objects = stream.readUnsignedWord();
		streamIndices = new int[objects];
		int i = 2;
		for (int j = 0; j < objects; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}
		cache = new ObjectDef[20];
		for (int k = 0; k < 20; k++) {
			cache[k] = new ObjectDef();
		}
		logger.info(Utility.format(objects) + " Objects have been loaded successfully.");
	}

	public static byte[] getBuffer(String s) {
		try {
			java.io.File f = new java.io.File("./data/map/objectdata/" + s);
			if (!f.exists()) {
				return null;
			}
			byte[] buffer = new byte[(int) f.length()];
			java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(f));
			dis.readFully(buffer);
			dis.close();
			return buffer;
		} catch (Exception e) {
		}
		return null;
	}

	private void readValues(ByteStreamExt stream) {

		int flag = -1;
		do {
			int type = stream.readUnsignedByte();
			if (type == 0)
				break;
			if (type == 1) {
				int len = stream.readUnsignedByte();
				if (len > 0) {
					if (anIntArray773 == null || lowMem) {
						anIntArray776 = new int[len];
						anIntArray773 = new int[len];
						for (int k1 = 0; k1 < len; k1++) {
							anIntArray773[k1] = stream.readUnsignedWord();
							anIntArray776[k1] = stream.readUnsignedByte();
						}
					} else {
						stream.currentOffset += len * 3;
					}
				}
			} else if (type == 2)
				name = stream.readString();
			else if (type == 3)
				description = stream.readBytes();
			else if (type == 5) {
				int len = stream.readUnsignedByte();
				if (len > 0) {
					if (anIntArray773 == null || lowMem) {
						anIntArray776 = null;
						anIntArray773 = new int[len];
						for (int l1 = 0; l1 < len; l1++)
							anIntArray773[l1] = stream.readUnsignedWord();
					} else {
						stream.currentOffset += len * 2;
					}
				}
			} else if (type == 14)
				objectSizeX = stream.readUnsignedByte();
			else if (type == 15)
				objectSizeY = stream.readUnsignedByte();
			else if (type == 17)
				aBoolean767 = false;
			else if (type == 18)
				aBoolean757 = false;
			else if (type == 19)
				hasActions = (stream.readUnsignedByte() == 1);
			else if (type == 21)
				aBoolean762 = true;
			else if (type == 22) {
			} else if (type == 23)
				aBoolean764 = true;
			else if (type == 24) {
				anInt781 = stream.readUnsignedWord();
				if (anInt781 == 65535)
					anInt781 = -1;
			} else if (type == 28)
				anInt775 = stream.readUnsignedByte();
			else if (type == 29)
				stream.readSignedByte();
			else if (type == 39)
				stream.readSignedByte();
			else if (type >= 30 && type < 39) {
				if (actions == null)
					actions = new String[5];
				actions[type - 30] = stream.readString();
				if (actions[type - 30].equalsIgnoreCase("hidden"))
					actions[type - 30] = null;
			} else if (type == 40) {
				int i1 = stream.readUnsignedByte();
				modifiedModelColors = new int[i1];
				originalModelColors = new int[i1];
				for (int i2 = 0; i2 < i1; i2++) {
					modifiedModelColors[i2] = stream.readUnsignedWord();
					originalModelColors[i2] = stream.readUnsignedWord();
				}

			} else if (type == 60)
				anInt746 = stream.readUnsignedWord();
			else if (type == 62) {
			} else if (type == 64) {
			} else if (type == 65)
				stream.readUnsignedWord();
			else if (type == 66)
				stream.readUnsignedWord();
			else if (type == 67)
				stream.readUnsignedWord();
			else if (type == 68)
				anInt758 = stream.readUnsignedWord();
			else if (type == 69)
				anInt768 = stream.readUnsignedByte();
			else if (type == 70)
				stream.readSignedWord();
			else if (type == 71)
				stream.readSignedWord();
			else if (type == 72)
				stream.readSignedWord();
			else if (type == 73)
				aBoolean736 = true;
			else if (type == 74)
				aBoolean766 = true;
			else if (type == 75)
				anInt760 = stream.readUnsignedByte();
			else if (type == 77) {
				anInt774 = stream.readUnsignedWord();
				if (anInt774 == 65535)
					anInt774 = -1;
				anInt749 = stream.readUnsignedWord();
				if (anInt749 == 65535)
					anInt749 = -1;
				int j1 = stream.readUnsignedByte();
				childrenIDs = new int[j1 + 1];
				for (int j2 = 0; j2 <= j1; j2++) {
					childrenIDs[j2] = stream.readUnsignedWord();
					if (childrenIDs[j2] == 65535)
						childrenIDs[j2] = -1;
				}
			}
		} while (true);
		if (flag == -1 && name != "null" && name != null) {
			hasActions = anIntArray773 != null && (anIntArray776 == null || anIntArray776[0] == 10);
			if (actions != null)
				hasActions = true;
		}
		if (aBoolean766) {
			aBoolean767 = false;
			aBoolean757 = false;
		}
		if (anInt760 == -1)
			anInt760 = aBoolean767 ? 1 : 0;
	}

	private ObjectDef() {
		type = -1;
	}

	public boolean hasActions() {
		return hasActions || actions != null;
	}

	public boolean hasName() {
		return name != null && name.length() > 1;
	}

	public int xLength() {
		return objectSizeX;
	}

	public int yLength() {
		return objectSizeY;
	}

	/*
	 * public boolean clipped() { return unwalkable; }
	 */

	public boolean aBoolean736;
	public String name;
	public int objectSizeX;
	public int anInt746;
	int[] originalModelColors;
	public int anInt749;
	public static boolean lowMem;
	public int type;
	public boolean aBoolean757;
	public int anInt758;
	public int childrenIDs[];
	private int anInt760;
	public int objectSizeY;
	public boolean aBoolean762;
	public boolean aBoolean764;
	private boolean aBoolean766;
	public boolean aBoolean767;
	public int anInt768;
	private static int cacheIndex;
	int[] anIntArray773;
	public int anInt774;
	public int anInt775;
	int[] anIntArray776;
	public byte description[];
	public boolean hasActions;
	public boolean aBoolean779;
	public int anInt781;
	private static ObjectDef[] cache;
	int[] modifiedModelColors;
	public String actions[];
}