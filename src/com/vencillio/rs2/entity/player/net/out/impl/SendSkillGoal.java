package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendSkillGoal extends OutgoingPacket {

	private final int skill;
	private final int init;
	private final int goal;
	private final int type;

	public SendSkillGoal(int skill, int init,  int goal, int type) {
		this.skill = skill;
		this.init = init;
		this.goal = goal;
		this.type = type;
	}

	@Override
	public void execute(Client client) {
		client.getPlayer().getSkillGoals()[skill][0] = init;
		client.getPlayer().getSkillGoals()[skill][1] = goal;
		client.getPlayer().getSkillGoals()[skill][2] = type;
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(11);
		out.writeHeader(client.getEncryptor(), 125);
		out.writeByte(skill);
		out.writeInt(init);
		out.writeInt(goal);
		out.writeByte(type);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 125;
	}
}