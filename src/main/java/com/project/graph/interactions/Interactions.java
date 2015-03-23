package com.project.graph.interactions;

import com.project.bibEntries.Node;
import com.project.bibEntries.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
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

    BibtexDatabase database;
    
    Node<BibtexEntry> seed;
    
    ArrayList<Node<BibtexEntry>> entries; // lista de entries da base.
    
    HashMap<String, Node<BibtexEntry>> entriesMap; // map de entries da base.
    
    protected StringWriter writer;

    abstract public void criarFile() throws IOException;

    abstract public void gravaNodes(BibtexEntry entry);

    abstract public void gravaEdges(BibtexEntry source, BibtexEntry target) throws IOException;

    abstract public String fechaFile();

    public void geraFile(String seed) throws IOException, InterruptedException {

    	// insere cada elemento da base em um map (bibtexkey -> bibtexentry
        getAllEntries();

        // colocando a semente na lista
        Node<BibtexEntry> b = this.entriesMap.get(seed);
        b.setColor(Color.WHITE);
        this.entries.add(b);

        // Breadth-first search using b as root
        BFS(b);

    }

    private void getAllEntries() {
        for (BibtexEntry entry : database.getEntries()) {
            if (! entry.getField("bibtexkey").startsWith("proceedings") && ! entry.getField("bibtexkey").startsWith("journal")) {
                this.entriesMap.put(entry.getField("bibtexkey"), new Node<BibtexEntry>(entry, Color.WHITE));
            }
        }
    }

    private void BFS(Node<BibtexEntry> entry) throws IOException, InterruptedException {
        int i = 0;

        criarFile();
        while (! this.entries.isEmpty()) {
            i++;
        	String it = "iteration".concat(Integer.toString(i, 10));
            File currentLevelFile = new File(FilePaths.grafo + "/" + it + ".dot"); // novo arquivo gerado
            FileWriter currentLevelWriter = new FileWriter(currentLevelFile); // responsavel pela escrita no novo arquivo.
        	ArrayList<Node<BibtexEntry>> currentLevelNodes = (ArrayList<Node<BibtexEntry>>) this.entries.clone();

            while (! currentLevelNodes.isEmpty()) {
            	// Process head of the list
                Node<BibtexEntry> currentNode = this.entries.remove(0);
                currentLevelNodes.remove(0);
                if (currentNode.getColor() == Color.WHITE || currentNode.getColor() == Color.GREY) {
                	if (currentNode.getColor() == Color.WHITE) {
                		gravaNodes(currentNode.getEntry());
                	}

               		// Process children of the head of the list
	                ArrayList<Node<BibtexEntry>> childrenOfCurrentNode = getReferences(currentNode);
	                for (Node<BibtexEntry> ref : childrenOfCurrentNode) {
	                    if (ref.getColor().equals(Color.WHITE)) {
	                    	gravaEdges(currentNode.getEntry(), ref.getEntry());
	                        ref.setColor(Color.GREY);
	                        gravaNodes(ref.getEntry());
	                        entries.add(ref); // Add new node to BFS 
	                    }
	                }
	                currentNode.setColor(Color.BLACK);
                }
            }

            currentLevelWriter.write(writer.getBuffer().toString());
            currentLevelWriter.write(fechaFile());
            currentLevelWriter.close();
            
            // copy(it);

            // deletar este arquivo
            // destroyFile();
        }
        /*
        if (extension.getName().contains(".dot")) {
            its.add("dot -Tpng " + it + ".dot > " + it + ".png");
            // gravaSh(its);
        }
        */
        this.writer.close();

    }

    private void destroyFile() throws IOException {
        // this.extension.delete();
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

    private void copy(String it) throws FileNotFoundException, IOException {
    	/*
        File destino = null;

        if (extension.getName().contains(".dot")) {
            destino = new File(this.extension.getParent().concat("/").concat(it).concat(".dot"));
        } else if (extension.getName().contains(".gml")) {
            destino = new File(this.extension.getParent().concat("/").concat(it).concat(".gml"));
        }

        FileInputStream fisOrigem = new FileInputStream(this.extension);
        FileOutputStream fisDestino = new FileOutputStream(destino);
        FileChannel fcOrigem = fisOrigem.getChannel();
        FileChannel fcDestino = fisDestino.getChannel();
        fcOrigem.transferTo(0, fcOrigem.size(), fcDestino);
        fisOrigem.close();
        fisDestino.close();
        */
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
