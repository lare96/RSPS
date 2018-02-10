package com.vencillio.rs2.content.minigames.fightcave;

public enum TzharrData {
	 
	WAVE_1(new short[] { NPCS_DETAILS.TZ_KIH, NPCS_DETAILS.TZ_KIH }),
	WAVE_2(new short[] { NPCS_DETAILS.TZ_KEK }),
	WAVE_3(new short[] { NPCS_DETAILS.TZ_KEK, NPCS_DETAILS.TZ_KIH }),
	WAVE_4(new short[] { NPCS_DETAILS.TZ_KEK, NPCS_DETAILS.TZ_KIH, NPCS_DETAILS.TZ_KIH }),
	WAVE_5(new short[] { NPCS_DETAILS.TZ_KEK, NPCS_DETAILS.TZ_KEK }),
	WAVE_6(new short[] { NPCS_DETAILS.TOK_XIL }),
	WAVE_7(new short[] { NPCS_DETAILS.TOK_XIL, NPCS_DETAILS.TZ_KIH }),
	WAVE_8(new short[] { NPCS_DETAILS.TOK_XIL, NPCS_DETAILS.TZ_KIH, NPCS_DETAILS.TZ_KIH }),
	WAVE_9(new short[] { NPCS_DETAILS.TOK_XIL, NPCS_DETAILS.TZ_KEK }),
	WAVE_10(new short[] { NPCS_DETAILS.TOK_XIL, NPCS_DETAILS.TOK_XIL }),
	WAVE_11(new short[] { NPCS_DETAILS.YT_MEJKOT, NPCS_DETAILS.TOK_XIL }),
	WAVE_12(new short[] { NPCS_DETAILS.YT_MEJKOT, NPCS_DETAILS.TOK_XIL, NPCS_DETAILS.TOK_XIL }),
	WAVE_13(new short[] { NPCS_DETAILS.YT_MEJKOT, NPCS_DETAILS.YT_MEJKOT }),
	WAVE_14(new short[] { NPCS_DETAILS.TZ_KEK, NPCS_DETAILS.YT_MEJKOT, NPCS_DETAILS.YT_MEJKOT, NPCS_DETAILS.TOK_XIL  }),
	WAVE_15(new short[] { NPCS_DETAILS.TZTOK_JAD });

	public static final class NPCS_DETAILS {
		public static final short TZ_KIH = 2189;
		public static final short TZ_KEK_SPAWN = 2191;
		public static final short TZ_KEK = 2192;
		public static final short TOK_XIL = TZ_KIH;//2193;
		public static final short YT_MEJKOT = 3123;
		public static final short KET_ZEK = 3125;
		public static final short TZTOK_JAD = 3127;
	}

	private short[] npcs;

	private TzharrData(short[] npcs) {
		this.npcs = npcs;
	}

	public short[] getNpcs() {
		return npcs;
	}

	public int toInteger() {
		return ordinal();
	}
}
