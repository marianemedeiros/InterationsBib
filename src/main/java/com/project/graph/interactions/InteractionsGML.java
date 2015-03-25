/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project.graph.interactions;

import com.project.bibEntries.Node;
import com.project.bibEntries.PrepareDatabase;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import namePaths.FilePaths;
import net.sf.jabref.BibtexEntry;
import org.openide.util.Exceptions;

/**
 *
 * @author mariane
 */

public class InteractionsGML extends Interactions{
    
    public static final String  EX = ".gml";

    public InteractionsGML(File file) throws IOException {
        PrepareDatabase data = new PrepareDatabase(file);
        database = data.getDatabase();
        
        
        entries = new ArrayList<Node<BibtexEntry>>();
        entriesMap = new HashMap<String, Node<BibtexEntry>>();
        writer = new StringWriter();
    }

    @Override
    public void criarFile() throws IOException{
        //extension = new File(FilePaths.grafo.concat(EX));
        writer.write("graph\n[");
    }
    
    @Override
    public String fechaFile(){
        //this.writer.write("\n]");
        return "\n]";
    }
    
    @Override
    public void gravaNodes(BibtexEntry entry) {
        this.writer.write("\n  node\n  [\n   id " + (Integer.parseInt(entry.getId()) + 1) +
                "\n   label " + "\""+ entry.getField("bibtexkey") + "\""+ "\n  ]");
    }

    @Override
    public void gravaEdges(BibtexEntry source, BibtexEntry target) throws IOException {
         this.writer.write("\n  edge\n  [\n   source " + (Integer.parseInt(source.getId()) + 1) + 
                "\n   target " + (Integer.parseInt(target.getId()) + 1) + "\n  ]");
    }





}
