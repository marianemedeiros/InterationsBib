package com.project.bibEntries;

/**
 *
 * @author mariane
 */
public class Node<T> {
    
    private T entry;
    
    private Color color;

    public Node(T e, Color c) {
        this.entry = e;
        this.color = c;
    }

    /**
     * @return the entry
     */
    public T getEntry() {
        return entry;
    }

    /**
     * @param entry the entry to set
     */
    public void setEntry(T entry) {
        this.entry = entry;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }
}

