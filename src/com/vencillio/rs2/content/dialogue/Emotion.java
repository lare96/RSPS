package com.vencillio.rs2.content.dialogue;

public enum Emotion {

    HAPPY(588),
    HAPPY_TALK(588),
    CALM(589),
    CALM_CONTINUED(590),
    DEFAULT(591),
    EVIL(592),
    EVIL_CONTINUED(593),
    DELIGHTED_EVIL(594),
    ANNOYED(595),
    DISTRESSED(596),
    DISTRESSED_CONTINUED(597),
    DISORIENTED_LEFT(600),
    DISORIENTED_RIGHT(601),
    UNINTERESTED(602),
    SLEEPY(603),
    PLAIN_EVIL(604),
    LAUGHING(605),
    LAUGHING_2(608),
    LONGER_LAUGHING(606),
    LONGER_LAUGHING_2(607),
    EVIL_LAUGH_SHORT(609),
    SLIGHTLY_SAD(610),
    SAD(599),
    VERY_SAD(611),
    OTHER(612),
    NEAR_TEARS(598),
    NEAR_TEARS_2(613),
    ANGRY_1(614),
    ANGRY_2(615),
    ANGRY_3(616),
    ANGRY_4(617);

	private int emoteId;

	private Emotion(int emoteId) {
		this.emoteId = emoteId;
	}

	public int getEmoteId() {
		return emoteId;
	}
}
