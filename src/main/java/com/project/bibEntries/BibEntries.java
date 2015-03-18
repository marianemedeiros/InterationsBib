/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project.bibEntries;

import net.sf.jabref.BibtexEntry;

/**
 *
 * @author mariane
 */
public class BibEntries {
    
    private BibtexEntry entry;
    private Cor color;

    public BibEntries(BibtexEntry e, Cor c) {
        this.entry = e;
        this.color = c;
    }

    /**
     * @return the entry
     */
    public BibtexEntry getEntry() {
        return entry;
    }

    /**
     * @param entry the entry to set
     */
    public void setEntry(BibtexEntry entry) {
        this.entry = entry;
    }

    /**
     * @return the color
     */
    public Cor getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Cor color) {
        this.color = color;
    }

    

}

