package com.KRacR.s2c;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;

public class RDFtoCypher {
	public static List<String> RDFtoCypherDirect(Model model) {
		 List<String> cypher_qs = new ArrayList<String>();
		 StmtIterator it =  model.listStatements();
		 while (it.hasNext()) {
		      Statement stmt = it.next();
		      String cypher_q = "";
		      CreateCypher visitor = new CreateCypher("MERGE");
		      visitor.set_label("s");
		      cypher_q = cypher_q.concat((String) stmt.getSubject().visitWith(visitor));
		      cypher_q = cypher_q.concat("\n");
		      visitor.set_label("o");
		      cypher_q = cypher_q.concat((String) stmt.getObject().visitWith(visitor));
		      cypher_q = cypher_q.concat("\n");
		      cypher_q = cypher_q.concat(
		    		  String.format("MERGE (s)-[:Edge {uri:\"%s\", stringrep:\"%s\"}]->(o);", stmt.getPredicate().getURI(), stmt.getPredicate().getURI())
		      );
		      cypher_qs.add(cypher_q);
		 }
		 return cypher_qs;
	}
	
	public static void RDFFiletoCypherFIle(String rdfpath, String cypherpath) throws IOException {
		Model rdfmodel = RDFDataMgr.loadModel(rdfpath) ;
		List<String> cypher_qs = RDFtoCypherDirect(rdfmodel);
		
		FileWriter writer = new FileWriter(cypherpath); 
		for(String cypher_q: cypher_qs) {
		  writer.write(cypher_q + System.lineSeparator());
		}
		writer.close();
	}
}
