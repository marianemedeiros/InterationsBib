/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package factory;

import com.project.graph.allGraph.GML;
import com.project.graph.allGraph.NWB;
import com.project.graph.interactions.InteractionsDOT;
import com.project.graph.interactions.InteractionsGML;
import java.io.File;
import java.io.IOException;
import namePaths.FilePaths;

/**
 *
 * @author mariane
 */
public class GrafoFactory {
    public static void geraGrafo(Representacao rep) throws IOException, InterruptedException{
        switch(rep){
            case ALL_GRAPH_GML:
                GML gml = new GML(new File(FilePaths.bib), "Chaves-etal:2013");
                gml.geraFile();
                break;
            case ALL_GRAPH_NWB:
                NWB nwb = new NWB(new File(FilePaths.bib));
                nwb.geraFile();
                break;
            case INTERACTION_GRAPH_DOT:
                InteractionsDOT dot = new InteractionsDOT(new File(FilePaths.bib));
                dot.geraFile("Chaves-etal:2013");
                break;
            case INTERACTION_GRAPH_GML:
                InteractionsGML it_gml = new InteractionsGML(new File(FilePaths.bib));
                it_gml.geraFile("Chaves-etal:2013");
                break;
        }
    }
}
