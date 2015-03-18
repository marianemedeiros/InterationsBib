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
public class GML implements Gerar {

    private ArrayList<BibtexEntry> nodes;

    private BibtexDatabase database;
    private File gml;
    private FileWriter writer;

    private BibtexEntry seed;
    private ArrayList<BibtexEntry> currentEntries;

    public GML(File file, String s) throws IOException {
        this.nodes = new ArrayList<BibtexEntry>();
        this.currentEntries = new ArrayList<>();

        PrepareDatabase data = new PrepareDatabase(file);
        this.database = data.getDatabase();

        this.seed = getSeed(s);
        this.currentEntries.add(seed);

        if (seed == null) {
            System.err.println("Semente não econtrada na base");
        }

    }

    @Override
    public void geraFile() {
        gml = new File(FilePaths.grafo.concat(".gml"));

        try {
            this.writer = new FileWriter(gml);
            this.writer.write("graph\n[");

            //nodes(1)
            nodes();
            edges();

            this.writer.write("\n]");
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
                    this.writer.write("\n  node\n  [\n   id " + entries.getField("bibtexkey") + "\n  ]");
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    public void nodes(int i) {
        for (BibtexEntry entry : this.currentEntries) {
            String refs[] = getReferences(entry);
            
            ArrayList<BibtexEntry> newEntries = new ArrayList<>();

            for (String ref : refs) {
                String aux = ref.replaceAll(" ", "");
                takeRefEntries(aux, newEntries);
            }

            //this.currentEntries.remove(entry);
            
            for (BibtexEntry newEntry : newEntries) {
                this.currentEntries.add(newEntry);
            }
            System.out.println(this.currentEntries.size());
        }
    }

    @Override
    public void edges() {
        for (BibtexEntry element : this.nodes) {
            getReferences(element);
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

            for (String aux : refs) {
                String e = aux.replaceAll(" ", "");
                try {
                    this.writer.write("\n  edge\n  [\n   source " + entry.getField("bibtexkey") + "\n   target " + e + "\n  ]");
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return refs;
    }

    private BibtexEntry getSeed(String s) {
        for (BibtexEntry entries : this.database.getEntries()) {
            if (entries.getField("bibtexkey").equals(s)) {
                return entries;
            }
        }
        return null;
    }

    /**
     * @return the gml
     */
    public File getGml() {
        return gml;
    }

    private void takeRefEntries(String ref, ArrayList<BibtexEntry> newEntries) {
        for (BibtexEntry entries : this.database.getEntries()) {
            if (entries.getField("bibtexkey").equals(ref)) {
                newEntries.add(entries);
                break;
            }
        }
    }
    

 

}
