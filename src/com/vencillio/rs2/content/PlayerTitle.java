package com.vencillio.rs2.content;

import java.util.Objects;

public final class PlayerTitle {
	
	private String title;
	
	private int color;
	
	private boolean suffix;
	
	private PlayerTitle(String title, int color, boolean suffix) {
		this.title = title;
		this.color = color;
		this.suffix = suffix;
	}
	
	public static PlayerTitle create(String title, int color, boolean suffix) {
		return new PlayerTitle(title, color, suffix);
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getColor() {
		return color;
	}
	
	public boolean isSuffix() {
		return suffix;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(title, color, suffix);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlayerTitle)) {
			return false;
		}
		
		PlayerTitle title = (PlayerTitle) obj;
		
		return title.getTitle().equals(this.title) && title.getColor() == getColor() && title.isSuffix() == isSuffix();
	}
}