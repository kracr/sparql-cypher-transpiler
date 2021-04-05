package com.KRacR.s2c;
//package test.java.com.KRacR.s2c;

//import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import com.KRacR.s2c.RDFtoCypher;
import com.KRacR.s2c.SparqlToCypher;

/**
 * Unit test for Sparql to Cypher
 */
public class SparqlToCypherTest{
	private Neo4j embeddedDatabaseServer;
	private void run_TTL_Automated_Test(String folder){
		Path rdf_path = Paths.get(folder, "rdf.ttl");
		
		// Convert the RDF to PG, it is saved under $folder/Output.json
		// Use PG_Bench RDF_to_PG_PG_Bench(rdf_path, folder);
		
		// Create RDF model from ttl
		Model rdf_model = RDFDataMgr.loadModel(rdf_path.toString());
		
		// Load the converted RDF into Neo4j
		try(Transaction tx = embeddedDatabaseServer.databaseManagementService().database("neo4j").beginTx()) {
            for(String q: RDFtoCypher.RDFtoCypherDirect(rdf_model)) {
            	tx.execute(q);
            }
            tx.commit();
        }catch(Exception e) {
        	e.printStackTrace();
        	fail("Error executing RDF converted cypher queries in neo4j");
        }
		
		// Iterate over all sparql files in queries folder
		FileFilter sparqlFilter = new FileFilter() {
			public boolean accept(File file) {
				String extension = "";

				int i = file.getName().lastIndexOf('.');
				if (i > 0) {
				    extension = file.getName().substring(i+1);
				}
				
				return file.isFile() && (extension.equals("sparql"));
			}
		};
		File[] query_files = Paths.get(folder, "queries").toFile().listFiles(sparqlFilter);
		for (File query_file : query_files) {
			String sparql_query = null;
			try {
				sparql_query = new String(Files.readAllBytes(query_file.toPath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Run the Sparql to Cypher converter
			String cypher_query = SparqlToCypher.convert(sparql_query);
			
			// Execute the cypher query on the database
			Result result = null;
			try(Transaction tx = embeddedDatabaseServer.databaseManagementService().database("neo4j").beginTx()) {
	            result = tx.execute(cypher_query);
	        }
			System.out.println("Cypher Columns: " + String.join(", ", result.columns()));
			
			// Execute the sparql query on the database
			Query query = QueryFactory.create(sparql_query);
			QueryExecution qe = QueryExecutionFactory.create(query, rdf_model);
			ResultSet results = qe.execSelect();
			ResultSetFormatter.out(System.out, results, query);
		}
	}

    private void RDF_to_PG_PG_Bench(Path rdf_path, String folder) {
    	Path path_to_pg_bench = Paths.get("lib/RDFtoPGConverter.jar");
    	ProcessBuilder pb = new ProcessBuilder("java", "-jar", path_to_pg_bench.toAbsolutePath().toString(), rdf_path.toString());
		pb.directory(new File(folder));
		try {
			Process p = pb.start();
			int status = p.waitFor();
			assertEquals(0, status);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Failed to start process RDFtoPGConverter for folder " + folder);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("RDFtoPGConverter interrupted for folder " + folder);
		}
	}

	@Test
    public void All_TTL_Automated_Test() throws IOException{
    	// Code for iterating over all directories:
    	// Source: http://www.avajava.com/tutorials/lessons/how-do-i-use-a-filefilter-to-display-only-the-directories-within-a-directory.html
    	File f = new File("src/test/resources/ttl_automated_tests"); // current directory

		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		File[] files = f.listFiles(directoryFilter);
		setupNeo4jServer();
		for (File folder : files) {
			delete_all_from_neo4j();
			run_TTL_Automated_Test(folder.getCanonicalPath());
		}
    }

	private void delete_all_from_neo4j() {
		try(Transaction tx = embeddedDatabaseServer.databaseManagementService().database("neo4j").beginTx()) {
            tx.execute("MATCH (n) detach delete n;");
        }
	}

	private void setupNeo4jServer() {
		embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder().withDisabledServer().build();
	}
}
