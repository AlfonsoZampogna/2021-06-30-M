package it.polito.tdp.genes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.genes.model.Adiacenza;
import it.polito.tdp.genes.model.Genes;
import it.polito.tdp.genes.model.Interactions;


public class GenesDao {
	
	public List<Genes> getAllGenes(){
		String sql = "SELECT DISTINCT GeneID, Essential, Chromosome FROM Genes";
		List<Genes> result = new ArrayList<Genes>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				result.add(genes);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	public List<Integer> getAllChromosomes(){
		String sql = "SELECT distinct chromosome "
				+ "FROM genes "
				+ "WHERE chromosome!=0";
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Integer chromosome = res.getInt("Chromosome");
				result.add(chromosome);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}

	public List<Adiacenza> getArchi(){
		String sql = "SELECT g1.GeneID AS geneID1, g1.Essential AS essential1, g1.Chromosome AS chromosome1 "
				+ "      ,g2.GeneID AS geneID2, g2.Essential AS essential2, g2.Chromosome AS chromosome2 "
				+ "	   , sum(distinct i.Expression_Corr) as peso "
				+ "FROM interactions i, genes g1, genes g2 "
				+ "WHERE g1.chromosome!=0 AND g2.Chromosome!=0 AND g1.GeneID=i.GeneID1 "
				+ " AND g2.GeneID=i.GeneID2 AND g1.Chromosome!=g2.Chromosome "
				+ " GROUP BY g1.Chromosome, g2.Chromosome";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes1 = new Genes(res.getString("GeneID1"), 
						res.getString("essential1"), 
						res.getInt("chromosome1"));
				Genes genes2 = new Genes(res.getString("GeneID2"), 
						res.getString("essential2"), 
						res.getInt("chromosome2"));
				
				double peso = res.getDouble("peso");
				
				result.add(new Adiacenza(genes1,genes2,peso));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}

	
}
