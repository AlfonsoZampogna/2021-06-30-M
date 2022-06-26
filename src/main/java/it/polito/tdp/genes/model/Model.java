package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.genes.db.GenesDao;


public class Model {

	private Graph<Integer,DefaultWeightedEdge> grafo;
	private GenesDao dao;
	private List<Integer> chromosomes = new ArrayList<Integer>();
	private List<Adiacenza> archi = new ArrayList<Adiacenza>();
	
	private List<Integer> percorsoMigliore;
	
	public Model() {
		this.dao = new GenesDao();
	}
	
	public List<Integer> getChromosomes(){
		if(this.chromosomes.isEmpty())
			this.chromosomes = this.dao.getAllChromosomes();
		return this.chromosomes;
	}
	
	public List<Adiacenza> getArchi(){
		if(this.archi.isEmpty())
			this.archi = this.dao.getArchi();
		return archi;
	}
	
	public double getPesoMinimo() {
		double minimo = 500.0;
		for(Adiacenza a : this.getArchi()) {
			if(a.getPeso()<minimo) {
				minimo = a.getPeso();
			}
		}
		return minimo;
	}
	
	public List<Adiacenza> getArchiSottoSoglia(double soglia){
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		for(Adiacenza a : this.getArchi()) {
			if(a.getPeso()<soglia)
				result.add(a);
		}
		return result;
	}
	
	public double getPesoMassimo() {
		double massimo = 0.0;
		for(Adiacenza a : this.getArchi()) {
			if(a.getPeso()>massimo) {
				massimo = a.getPeso();
			}
		}
		return massimo;
	}
	
	public List<Adiacenza> getArchiSopraSoglia(double soglia){
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		for(Adiacenza a : this.getArchi()) {
			if(a.getPeso()>soglia)
				result.add(a);
		}
		return result;
	}
	
	public void CreaGrafo() {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, this.getChromosomes());
		
		for(Adiacenza a : this.getArchi()) 
			Graphs.addEdgeWithVertices(this.grafo, a.getG1().getChromosome(), a.getG2().getChromosome(), a.getPeso());
		
		System.out.println("#VERTICI : "+this.grafo.vertexSet().size());
		System.out.println("#ARCHI : "+this.grafo.edgeSet().size());
		
	}
	
	public List<Integer> ricorsione(){
		List<Integer> parziale = new ArrayList<Integer>();
		double pesoMax = 0.0;
		double pesoTemp = 0.0;
		int cont = 1;
		
		this.run(parziale,this.getChromosomes().size(),pesoMax,pesoTemp,cont);
		
		return percorsoMigliore;
	}
	
	
	private void run(List<Integer> parziale, int numChromosomes, double pesoMax, double pesoTemp,int cont) {
		//caso di terminazione
		if(parziale.size()-1==numChromosomes) {
			if(pesoTemp>pesoMax) {
				pesoMax=pesoTemp;
				percorsoMigliore = new ArrayList<Integer>(parziale);
			}
			return;
		}
		
		for(Integer i : Graphs.neighborListOf(this.grafo, cont)) {
			DefaultWeightedEdge e = this.grafo.getEdge(cont, i);
			if(e!=null) {
			 	double pesoArco = this.grafo.getEdgeWeight(e);
     			if(!parziale.contains(i) && pesoArco>=0) {
	    			pesoTemp+=pesoArco;
		    		parziale.add(i);
		        	run(parziale,numChromosomes,pesoMax,pesoTemp,cont++);
			        parziale.remove(parziale.size()-1);
			        pesoTemp-=pesoArco;
			    }
			}
		}
	
	}
	
	
	
}