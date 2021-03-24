package test.java.com.KRacR.s2c;

//import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.neo4j.graphdb.Label.label;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Values;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

/**
 * Unit test for Sparql to Cypher
 */
public class SparqlToCypherTest{
	private ServerControls embeddedDatabaseServer;
	private static final Config driverConfig = Config.build().withoutEncryption().toConfig();
	
	private void run_TTL_Automated_Test(String folder) {
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
		
		// Create in-memory neo4j database
		
	}

    @Test
    public void test_All_TTL_Automated() throws IOException{
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
			delete_all_from_database();
			run_TTL_Automated_Test(folder.getCanonicalPath());
		}
    }

	private void delete_all_from_database() {
		try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
				Session session = driver.session()) {
			session.run("match (n) detach delete n;");
		}
	}

	private void setupNeo4jServer() {
		this.embeddedDatabaseServer = TestServerBuilders.newInProcessBuilder().withProcedure(apoc.load.LoadJson.class).newServer();
	}
}
