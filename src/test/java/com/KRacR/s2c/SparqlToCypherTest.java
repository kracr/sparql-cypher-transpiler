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
import java.util.Arrays;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

import com.KRacR.s2c.SparqlToCypher;


/**
 * Unit test for Sparql to Cypher
 */
public class SparqlToCypherTest{
	private ServerControls embeddedDatabaseServer;
	private static final Config driverConfig = Config.build().withoutEncryption().toConfig();
	
	private void run_TTL_Automated_Test(String folder){
		Path rdf_path = Paths.get(folder, "rdf.ttl");
		Path path_to_pg_bench = Paths.get("lib/RDFtoPGConverter.jar");
		
		// Convert the RDF to PG, it is saved under $folder/Output.json
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
		
		// Create RDF model from ttl
		Model rdf_model = RDFDataMgr.loadModel(rdf_path.toString()) ;
		
		// Create in-memory neo4j database and load converted Output.json
		try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
				Session session = driver.session()) {
			session.run(
					"with \"file:///"
					+ Paths.get(folder, "Output.json").toAbsolutePath().toString()
					+ "\" as url "
					+ "CALL apoc.load.json(url) yield value "
					+ "unwind value.vertices as vertex "
					+ "unwind value.edges as edge "
					+ "merge (v {id:vertex.id, vname:vertex.vname}) on create set v=vertex "
					+ "merge ({vname:edge.out})-[e:Edge {id:edge.id, type:edge.type}]->({vname:edge.in}) on create set e=edge"
			);
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
			
			// Execute the sparql query on the database
			Query query = QueryFactory.create(sparql_query);
			QueryExecution qe = QueryExecutionFactory.create(query, rdf_model);
			ResultSet results = qe.execSelect();
			ResultSetFormatter.out(System.out, results, query);
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
		try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
				Session session = driver.session()) {
			session.run("match (n) detach delete n;");
		}
	}

	private void setupNeo4jServer() {
		this.embeddedDatabaseServer = TestServerBuilders.newInProcessBuilder()
				.withProcedure(apoc.load.LoadJson.class)
				.withConfig("apoc.import.file.enabled", "true")
				.newServer();
	}
}
