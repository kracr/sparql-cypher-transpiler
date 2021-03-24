package com.KRacR.s2c;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.String;

public class SparqlToCypher {
	public static void convert(String sparql_file, String output_file) {
		try {
			write_to_file("match (n)-[e:Edge]->(r {vname:\"http://localhost/persons/Paul_Erdoes\"}) return n.vname, e.type;", output_file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void write_to_file(String string, String output_file) throws IOException {
		BufferedWriter out = null;

		try {
		    FileWriter fstream = new FileWriter(output_file, true); //true tells to append data.
		    out = new BufferedWriter(fstream);
		    out.write(string);
		}

		catch (IOException e) {
		    System.err.println("Error: " + e.getMessage());
		}

		finally {
		    if(out != null) {
		        out.close();
		    }
		}
	}
}
