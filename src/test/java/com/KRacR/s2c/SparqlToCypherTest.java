package test.java.com.KRacR.s2c;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Unit test for Sparql to Cypher
 */
public class SparqlToCypherTest{
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
		for (File folder : files) {
			run_TTL_Automated_Test(folder.getCanonicalPath());
		}
    }
}
