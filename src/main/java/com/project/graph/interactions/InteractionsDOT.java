/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project.graph.interactions;


import com.project.bibEntries.BibEntries;
import com.project.bibEntries.Cor;
import com.project.bibEntries.PrepareDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import namePaths.FilePaths;
import net.sf.jabref.BibtexEntry;
import org.openide.util.Exceptions;

/**
 *
 * @author mariane
 */

public class InteractionsDOT extends Interactions{
    
    public static final String  EX = ".dot";

    public InteractionsDOT(File file) throws IOException {
        PrepareDatabase data = new PrepareDatabase(file);
        database = data.getDatabase();
        
        
        entries = new ArrayList<BibEntries>();
        entriesMap = new HashMap<String,BibEntries>();
    }

    @Override
    public void criarFile() throws IOException{
        extension = new File(FilePaths.grafo.concat(EX));
        writer = new FileWriter(extension);
        writer.write("graph {\n");
    }
    
    @Override
    public void fechaFile(){
        try {
            writer.write("}");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    @Override
    public void gravaNodes(BibtexEntry entry) {
        try {
            writer.write( "   \"" + entry.getField("bibtexkey") + "\";" + "\n");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void gravaEdges(BibtexEntry source, BibtexEntry target) throws IOException {
        writer.write( "   \"" + source.getField("bibtexkey") + "\"" + "-- " + "\"" + target.getField("bibtexkey") + "\";" + "\n");
    }

}
