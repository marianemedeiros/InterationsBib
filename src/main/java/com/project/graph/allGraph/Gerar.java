/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project.graph.allGraph;


import net.sf.jabref.BibtexEntry;

/**
 *
 * @author mariane
 */
public interface Gerar {
    
    public void geraFile();
    public void nodes();
    public void edges();
    public String[] getReferences(BibtexEntry entry);
    
}
