/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project.graph.interactions;

import com.project.bibEntries.BibEntries;
import com.project.bibEntries.Cor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import namePaths.FilePaths;
import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import org.openide.util.Exceptions;

/**
 *
 * @author mariane
 */
public abstract class Interactions {

    BibtexDatabase database;
    File extension; // novo arquivo gerado
    FileWriter writer; // responsavel pela escrita no novo arquivo.

    BibEntries seed;
    ArrayList<BibEntries> entries; // lista de entries da base.
    HashMap<String, BibEntries> entriesMap; // map de entries da base.

    abstract public void criarFile() throws IOException;

    abstract public void gravaNodes(BibtexEntry entry);

    abstract public void gravaEdges(BibtexEntry source, BibtexEntry target) throws IOException;

    abstract public void fechaFile();

    public void geraFile(String seed) throws IOException, InterruptedException {

        getAllEntries(); // insere cada elemento da base em um map e também grava os nodes (elemento) no arquivo .gml

        // colocando a semente na lista
        BibEntries b = this.entriesMap.get(seed);
        b.setColor(Cor.Branco);
        this.entries.add(b);

        BFS(b);

    }

    private void getAllEntries() {
        for (BibtexEntry entry : database.getEntries()) {
            if (!entry.getField("bibtexkey").contains("proceedings") && !entry.getField("bibtexkey").contains("journal")) {
                this.entriesMap.put(entry.getField("bibtexkey"), new BibEntries(entry, Cor.Branco));
            }
        }
    }

    private void BFS(BibEntries entry) throws IOException, InterruptedException {
        ArrayList<String> its = new ArrayList<String>();

        Integer i = 0;
        while (!this.entries.isEmpty()) {
            ArrayList<BibEntries> copyEntries = (ArrayList<BibEntries>) this.entries.clone();
            i++;
            criarFile();

            while (!copyEntries.isEmpty()) {
                BibEntries e = this.entries.remove(0);
                copyEntries.remove(0);

                gravaNodes(e.getEntry());

                ArrayList<BibEntries> refs = getReferences(e);
                for (BibEntries ref : refs) {
                    if (ref.getColor().equals(Cor.Branco)) {
                        ref.setColor(Cor.Cinza);
                        this.entries.add(ref);

                        gravaNodes(ref.getEntry());
                    }
                }

                for (BibEntries ref : refs) {
                    gravaEdges(e.getEntry(), ref.getEntry());
                }
                e.setColor(Cor.Preto);
            }

            fechaFile();
            this.writer.close();

            String it = "iteration".concat(i.toString());
            // dot -Tpng iteration1.dot  > iteration1.png
            its.add("dot -Tpng " + it + ".dot > " + it + ".png");

            copy(it);

            // deletar este arquivo
            destroyFile();

        }
        if (extension.getName().contains(".dot")) {
            gravaSh(its);
        }
    }

    private void destroyFile() throws IOException {
        this.extension.delete();
    }

    private ArrayList<BibEntries> getReferences(BibEntries e) {
        ArrayList<BibEntries> references = new ArrayList<BibEntries>();

        String r = e.getEntry().getField("references");
        String[] refs = null;
        if (r != null) {
            refs = r.split(",");

            for (String aux : refs) {
                aux = aux.replace(" ", "");
                BibEntries newBib = this.entriesMap.get(aux);
                references.add(newBib);
            }
        }
        return references;
    }

    private void copy(String it) throws FileNotFoundException, IOException {
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
