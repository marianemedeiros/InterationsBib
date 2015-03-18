/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project.graph.allGraph;

import com.project.bibEntries.PrepareDatabase;
import com.project.bibEntries.PrepareDatabase;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import namePaths.FilePaths;
import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import org.openide.util.Exceptions;

/**
 *
 * @author mariane
 */
public class NWB implements Gerar {

    private ArrayList<BibtexEntry> nodes;

    private BibtexDatabase database;
    private File nwb;
    private FileWriter writer;

    public NWB(File file) throws IOException {
        this.nodes = new ArrayList<BibtexEntry>();

        PrepareDatabase data = new PrepareDatabase(file);
        this.database = data.getDatabase();

        this.nwb = new File(FilePaths.grafo.concat(".nwb"));
        this.writer = new FileWriter(this.nwb);
    }

    @Override
    public void geraFile() {
        try {
            this.writer.write("*Nodes\nid*int label*string\n");
            nodes();

            this.writer.write("*UndirectedEdges\nsource*int target*int");
            edges();

            this.writer.close();
         } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void nodes() {
        for (BibtexEntry entries : this.database.getEntries()) {
            if (!entries.getField("bibtexkey").contains("proceedings") && !entries.getField("bibtexkey").contains("journal")) {
                this.nodes.add(entries);
                try {
                    this.writer.write(Integer.parseInt(entries.getId()) + 1 + " " + "\"" + entries.getField("bibtexkey") + "\"" + "\n");
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void edges() {
        for (BibtexEntry e : this.nodes) {
            getReferences(e);
        }
    }

    /**
     *
     * @param entry
     * @throws IOException
     */
    @Override
    public String[] getReferences(BibtexEntry entry) {
        String r = entry.getField("references");
        String[] refs = null;
        if (r != null) {
            refs = r.split(",");

            for (String s : refs) {
                String aux = s.replaceAll(" ", "");
                for (BibtexEntry e : this.nodes) {
                    if (e.getField("bibtexkey").equals(aux)) {
                        Integer a = Integer.parseInt(entry.getId());
                        Integer b = Integer.parseInt(e.getId());
                        try {
                            this.writer.write("\n" + (a + 1) + " " + (b + 1));
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        break;
                    }
                }
            }
        }
    return refs;
    }
    
}
