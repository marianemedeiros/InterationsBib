/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project.bibEntries;


import factory.GrafoFactory;
import factory.Representacao;
import java.io.IOException;

/**
 *
 * @author mariane
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException{
        
        GrafoFactory.geraGrafo(Representacao.INTERACTION_GRAPH_DOT);
    }
}
