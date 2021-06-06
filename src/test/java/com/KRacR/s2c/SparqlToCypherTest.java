package com.KRacR.s2c;
//package test.java.com.KRacR.s2c;

import static org.junit.Assert.assertEquals;
//import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

// https://stackoverflow.com/questions/8255738/is-there-a-stopwatch-in-java
class Stopwatch{
	  private long startTime;
	  private long stopTime;

	  /**
	   starting the stop watch.
	  */
	  public void resetstart(){
		  this.startTime = 0;
		  this.stopTime = 0;
	      startTime = System.nanoTime();
	  }

	  /**
	   stopping the stop watch.
	  */
	  public void stop()
	  {     stopTime = System.nanoTime(); }

	  /**
	  elapsed time in nanoseconds.
	  */
	  public long time(){
	        return (stopTime - startTime);
	  }

	  public String toString(){
	      return "elapsed time: " + time() + " nanoseconds.";
	  }
}

/**
 * Unit test for Sparql to Cypher
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SparqlToCypherTest{
	private Neo4j embeddedDatabaseServer;
	private Stopwatch sw = new Stopwatch();
	
	private static List<Arguments> SparqlToCypherQueriesProvider() throws IOException {
		File f = new File("src/test/resources/ttl_automated_tests"); // current directory

		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		File[] files = f.listFiles(directoryFilter);
		List<Arguments> test_args = new LinkedList<Arguments>();
		for (File folder_ : files) {
			String folder = folder_.getCanonicalPath();
			
			Path rdf_path = Paths.get(folder, "rdf.ttl");
			
			// Convert the RDF to PG, it is saved under $folder/Output.json
			// Use PG_Bench RDF_to_PG_PG_Bench(rdf_path, folder);
			
			// Create RDF model from ttl
			Model rdf_model = RDFDataMgr.loadModel(rdf_path.toString());
			List<String> RDF_converted_to_cypher = RDFtoCypher.RDFtoCypherDirect(rdf_model);
			
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
			for(File query_file: query_files) {
				test_args.add(Arguments.of(rdf_model, RDF_converted_to_cypher, query_file));
			}
		}
		return test_args;
	}
	
	@ParameterizedTest
	@MethodSource("SparqlToCypherQueriesProvider")
	public void test_run_TTL_Automated_Test(Model rdf_model, List<String> RDF_converted_to_cypher, File query_file) throws IOException{
		// Load the RDF Converted Cypher to Neo4j
		try(Transaction tx = embeddedDatabaseServer.databaseManagementService().database("neo4j").beginTx()) {
            for(String q: RDF_converted_to_cypher) {
            	tx.execute(q);
            }
            tx.commit();
        }catch(Exception e) {
        	e.printStackTrace();
        	fail("Error executing RDF converted cypher queries in neo4j");
        	return;
        }
		
		String sparql_query = null;
		try {
			sparql_query = new String(Files.readAllBytes(query_file.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Run the Sparql to Cypher converter
		sw.resetstart();
		String cypher_query;
		try {
			cypher_query = SparqlToCypher.convert(sparql_query);
		} catch (QueryNotSupportedException e1) {
			Assumptions.assumeTrue(false, e1.getMessage());
			System.out.println(e1.getMessage());
			return;
		}
		sw.stop();
		System.out.println("Query Conversion time: " + ((double)sw.time())/1000 + " ms.");
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
        	fail(
        			"Running the returned Cypher query FAILED with exception:\n" 
        			+ e.getMessage()
        			+ "\nSparql query:\n" + sparql_query
        			+ "\n\nConverted Cypher:\n" + cypher_query
        	);
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
		
		System.out.println(String.format("Sparql Result\n%s\n", sparql_result.toString()));
		System.out.println(String.format("Cypher Result\n%s\n", cypher_result.toString()));
		
		// TODO: Account for ORDER BY queries
		// TODO: Match all column names in both the result sets
		assertEquals(sparql_result, cypher_result, String.format(
				"Equality test failed for %s\nSparql result:\n%s\n\nCypher Result\n%s", 
				query_file.getCanonicalPath(),
				sparql_result.toString(),
				cypher_result.toString()
		));
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

    @BeforeEach
	public void delete_all_from_neo4j() {
		try(Transaction tx = embeddedDatabaseServer.databaseManagementService().database("neo4j").beginTx()) {
            tx.execute("MATCH (n) detach delete n;");
            tx.commit();
        }
	}

	@BeforeAll
	public void setupNeo4jServer() {
		embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder().withDisabledServer().build();
	}
}
