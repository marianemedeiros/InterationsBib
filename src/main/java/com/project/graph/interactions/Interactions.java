package com.project.graph.interactions;

import com.project.bibEntries.Node;
import com.project.bibEntries.Color;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import namePaths.FilePaths;
import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;

/**
 *
 * @author mariane
 */
public abstract class Interactions {

    public static final char FOWARD = 'f';
    public static final char BACKWARD = 'b';
    public static final String STATUS_BIB = "forward";

    BibtexDatabase database;

    Node<BibtexEntry> seed;

    ArrayList<Node<BibtexEntry>> entries; // lista de entries da base.

    HashMap<String, Node<BibtexEntry>> entriesMap; // map de entries da base.

    protected StringWriter writer;

    abstract public void criarFile() throws IOException;

    abstract public void gravaNodes(BibtexEntry entry);

    abstract public void gravaEdges(BibtexEntry source, BibtexEntry target, char type) throws IOException;

    abstract public String fechaFile();

    public void geraFile(String seed) throws IOException, InterruptedException {

        // insere cada elemento da base em um map (bibtexkey -> bibtexentry
        getAllEntries();

        // colocando a semente na lista
        Node<BibtexEntry> b = this.entriesMap.get(seed);
        b.setColor(Color.WHITE);
        this.entriesMap.replace(seed, b);

        this.seed = b;
        this.entries.add(b);

        // Breadth-first search using b as root
        BFS(b);

    }

    private void getAllEntries() {
        for (BibtexEntry entry : database.getEntries()) {
            if (!entry.getField("bibtexkey").startsWith("proceedings") && !entry.getField("bibtexkey").startsWith("journal")) {
                this.entriesMap.put(entry.getField("bibtexkey"), new Node<BibtexEntry>(entry, Color.WHITE));
            }
        }
    }

    private void BFS(Node<BibtexEntry> entry) throws IOException, InterruptedException {
        Integer i = 0;

        criarFile();

        while (!this.entries.isEmpty()) {
            i++;
            String it = "iteration".concat(Integer.toString(i, 10));
            File currentLevelFile = new File(FilePaths.grafo + "/" + it + ".dot"); // novo arquivo gerado 
            FileWriter currentLevelWriter = new FileWriter(currentLevelFile); // responsavel pela escrita no novo arquivo.
            ArrayList<Node<BibtexEntry>> currentLevelNodes = (ArrayList<Node<BibtexEntry>>) this.entries.clone();

            while (!currentLevelNodes.isEmpty()) {
                // Process head of the list
                Node<BibtexEntry> currentNode = this.entries.remove(0);
                currentLevelNodes.remove(0);
                if (currentNode.getColor() == Color.WHITE || currentNode.getColor() == Color.GREY) {
                    if (currentNode.getColor() == Color.WHITE) {
                        gravaNodes(currentNode.getEntry());
                    }
                    /*getting backwarballing*/
                    // Process children of the head of the list
                    ArrayList<Node<BibtexEntry>> childrenOfCurrentNode = getReferences(currentNode);
                    for (Node<BibtexEntry> ref : childrenOfCurrentNode) {
                        if (ref.getColor().equals(Color.WHITE)) {
                            gravaEdges(currentNode.getEntry(), ref.getEntry(), BACKWARD);
                            ref.setColor(Color.GREY);

                            this.entriesMap.replace(ref.getEntry().getField("bibtexkey"), ref);

                            gravaNodes(ref.getEntry());
                            entries.add(ref); // Add new node to BFS 
                        }
                    }
                    /*end of getting backwardballing*/

                    /*now we can get forwardballing =)*/
                    /*getting forwardballing*/
                    for (Node<BibtexEntry> element : this.entriesMap.values()) {
                        if (element.getColor().equals(Color.WHITE) && element.getEntry().getField("status").contains(STATUS_BIB)) {
                            ArrayList<Node<BibtexEntry>> rfs = getReferences(element);
                            if (rfs.contains(currentNode)) {
                                
                                gravaNodes(element.getEntry());
                                gravaEdges(currentNode.getEntry(), element.getEntry(), FOWARD);
                                
                                element.setColor(Color.GREY);
                                this.entriesMap.replace(element.getEntry().getField("bibtexkey"), element);
                            }
                        }
                    }
                    /*enf of getting fowardballing*/

                    currentNode.setColor(Color.BLACK);
                    this.entriesMap.replace(currentNode.getEntry().getField("bibtexkey"), currentNode);
                }
            }

            currentLevelWriter.write(writer.getBuffer().toString());
            currentLevelWriter.write(fechaFile());
            currentLevelWriter.close();
        }
        
        /*
         if (extension.getName().contains(".dot")) {
         its.add("dot -Tpng " + it + ".dot > " + it + ".png");
         // gravaSh(its);
         }
         */
        
        this.writer.close();

        for (Node<BibtexEntry> e : entriesMap.values()) {
            if (e.getColor().equals(Color.WHITE)) {
                System.out.println(e.getEntry().getField("bibtexkey") + " -- " + e.getEntry().getField("status") );
                
            }
        }

    }

    private ArrayList<Node<BibtexEntry>> getReferences(Node<BibtexEntry> e) {
        ArrayList<Node<BibtexEntry>> references = new ArrayList<Node<BibtexEntry>>();

        String r = e.getEntry().getField("references");
        if (r != null) {
            String[] refs = r.split(",");
            for (String aux : refs) {
                aux = aux.replace(" ", "");
                Node<BibtexEntry> newBib = this.entriesMap.get(aux);
                references.add(newBib);
            }
        }
        return references;
    }

    private void generateImages() throws IOException, InterruptedException {
        //dot -Tpng it2.dot  > it2.png
        Runtime run = Runtime.getRuntime();

        String cmd = FilePaths.dot_sh;

        File dir = new File(FilePaths.pattern);
        Process p = run.exec(cmd, null, dir);
        p.waitFor();

    }

    private void gravaSh(ArrayList<String> lista) throws IOException, InterruptedException {
        FileWriter wr = new FileWriter(FilePaths.dot_sh);

        wr.write("#!/bin/sh\n");

        for (String el : lista) {
            wr.write(el);
            wr.write("\n");
        }

        wr.close();

        generateImages();
    }
}
