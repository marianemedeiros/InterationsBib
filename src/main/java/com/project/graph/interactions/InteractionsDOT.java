package com.project.graph.interactions;


import com.project.bibEntries.Node;
import com.project.bibEntries.PrepareDatabase;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jabref.BibtexEntry;

/**
 *
 * @author mariane
 */
public class InteractionsDOT extends Interactions {
    
    public static final String  EX = ".dot";
  
    public InteractionsDOT(File file) throws IOException {
        PrepareDatabase data = new PrepareDatabase(file);
        database = data.getDatabase();
        entries = new ArrayList<Node<BibtexEntry>>();
        entriesMap = new HashMap<String,Node<BibtexEntry>>();
        this.writer = new StringWriter();
    }

    @Override
    public void criarFile() throws IOException{
        writer.write("graph {\n");
    }
    
    @Override
    public String fechaFile(){
        return "}";
    }
    
    @Override
    public void gravaNodes(BibtexEntry entry) {
    	writer.write( "   \"" + entry.getField("bibtexkey") + "\";" + "\n");
    }

    @Override
    public void gravaEdges(BibtexEntry source, BibtexEntry target, char type) throws IOException {
        if(type == FOWARD){
            writer.write( "   \"" + source.getField("bibtexkey") + "\"" + "-- " + "\"" + target.getField("bibtexkey") + "\""
                    + " [style=dotted];\n");
        }else if(type == BACKWARD){
            writer.write( "   \"" + source.getField("bibtexkey") + "\"" + "-- " + "\"" + target.getField("bibtexkey") + "\";" + "\n");
        }
    }

}
