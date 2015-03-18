/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package factory;

/**
 *
 * @author mariane
 */
public enum Representacao {
    ALL_GRAPH_GML(1),
    ALL_GRAPH_NWB(2),
    
    INTERACTION_GRAPH_GML(1),
    INTERACTION_GRAPH_DOT(2);

    private int representacao;
    
    Representacao(int representacao){
		this.setRepresentacao(representacao);
	}

	public int getRepresentacao() {
		return representacao;
	}

	public void setRepresentacao(int representacao) {
		this.representacao = representacao;
	}
}
