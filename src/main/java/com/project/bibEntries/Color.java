package com.project.bibEntries;

/**
 *
 * @author mariane
 */
public enum Color {
	BLACK(0),
	GREY(1),
	WHITE(2);
	
	private int color;
	
	Color(int color){
            this.color = color;
	}

	public int getColor() {
            return color;
	}
}
