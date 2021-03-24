package test.java.com.KRacR.s2c;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
	private void run_TTL_Automated_Test(String folder) {
		Path rdf_path = Paths.get(folder, "rdf.ttl");
		System.out.println(rdf_path);
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
		for (File folder : files) {
			run_TTL_Automated_Test(folder.getCanonicalPath());
		}
    }
}
