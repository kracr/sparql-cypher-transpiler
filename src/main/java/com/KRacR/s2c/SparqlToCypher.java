package com.KRacR.s2c;

import java.lang.String;

public class SparqlToCypher {
	public static String convert(String sparql_query) {
		return "match (n)-[e:Edge]->(r {uri:\"http://localhost/persons/Paul_Erdoes\"}) return n.uri as subject, e.uri as predicate";
	}
}
