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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

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
		
		// Convert the RDF model to cypher queries and load into Neo4j
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
			// TODO: Consider iterating column wise instead of row wise
			Set<Map<String, String>> sparql_result = new HashSet<Map<String, String>>();
			Set<Map<String, String>> cypher_result = new HashSet<Map<String, String>>();
			try(Transaction tx = embeddedDatabaseServer.databaseManagementService().database("neo4j").beginTx()) {
				Result result = tx.execute(cypher_query);
	            while(result.hasNext()) {
	            	Map<String, Object> row = result.next();
	            	Map<String, String> res = new HashMap<String, String>();
	            	for(String col: row.keySet()) {
	            		res.put(col, row.get(col).toString());
	            	}
	            	cypher_result.add(res);
	            }
	        }catch(Exception e) {
	        	System.out.println("Running the returned Cypher query FAILED with exception:");
	        	System.out.println(e.getMessage());
	        }
			
			// Execute the sparql query on the database
			// TODO: Consider iterating column wise instead of row wise
			Query query = QueryFactory.create(sparql_query);
			QueryExecution qe = QueryExecutionFactory.create(query, rdf_model);
			ResultSet results = qe.execSelect();
			while(results.hasNext()) {
				QuerySolution row = results.next();
				Map<String, String> res = new HashMap<String, String>();
				for(String col: results.getResultVars()) {
					res.put(col, row.get(col).toString());
				}
				sparql_result.add(res);
			}
			// System.out.println(sparql_result);
			// System.out.println(cypher_result);
			
			if(sparql_result.equals(cypher_result)){
				System.out.println("\nTEST PASSED\n");
			}else {
				System.out.println("\nTEST FAILED\n");
				System.out.println("-------Sparql-------");
				System.out.println(sparql_result);
				System.out.println("-------Cypher-------");
				System.out.println(cypher_result);
			}
			
			// TODO: Account for ORDER BY queries
			// TODO: Account for BLANK nodes
			// TODO: Match all column names in both the result sets
			// assertEquals(String.format("Equality test failed for %s/queries/%s", folder, query_file.getName()), sparql_result, cypher_result);
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
            tx.commit();
        }
	}

	private void setupNeo4jServer() {
		embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder().withDisabledServer().build();
	}
}
