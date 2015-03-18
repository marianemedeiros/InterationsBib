/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project.bibEntries;

/**
 *
 * @author mariane
 */
public enum Cor {
	Preto(0),
	Cinza(1),
	Branco(2);
	
	private int cor;
	
	Cor(int cor){
            this.setCor(cor);
	}

	public int getCor() {
            return cor;
	}

	public void setCor(int cor) {
            this.cor = cor;
	}
}
