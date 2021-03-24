package com.KRacR.s2c;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.String;

public class SparqlToCypher {
	public static String convert(String sparql_query) {
		return "match (n)-[e:Edge]->(r {vname:\"http://localhost/persons/Paul_Erdoes\"}) return n.vname, e.type";
	}
}
