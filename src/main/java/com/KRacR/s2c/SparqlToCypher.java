package com.KRacR.s2c;

import java.lang.String;

import javax.ws.rs.NotSupportedException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryVisitor;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.op.OpAssign;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpConditional;
import org.apache.jena.sparql.algebra.op.OpDatasetNames;
import org.apache.jena.sparql.algebra.op.OpDiff;
import org.apache.jena.sparql.algebra.op.OpDisjunction;
import org.apache.jena.sparql.algebra.op.OpDistinct;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpFind;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpLabel;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpList;
import org.apache.jena.sparql.algebra.op.OpMinus;
import org.apache.jena.sparql.algebra.op.OpNull;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProcedure;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.algebra.op.OpQuad;
import org.apache.jena.sparql.algebra.op.OpQuadBlock;
import org.apache.jena.sparql.algebra.op.OpQuadPattern;
import org.apache.jena.sparql.algebra.op.OpReduced;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpSlice;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpTopN;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.Prologue;

public class SparqlToCypher {
	public static String convert(String sparql_query) throws QueryNotSupportedException {
		System.out.println("------------------------------------------\nInside Sparql to Cypher Converter");
		Query sq = QueryFactory.create(sparql_query);
		if(!(sq.isSelectType())) throw new QueryNotSupportedException("Conversion of the following query failed:\n" + sparql_query + "\nDue to the following reason:\n" + "Describe, Construct and Ask type queries not supported");
		Op op = Algebra.compile(sq);
		System.out.println(sparql_query);
		//System.out.println(op);
		SparqlAlgebraToCypherVisitor visitor = new SparqlAlgebraToCypherVisitor();
		op.visit(visitor);
		String cypher_query = null;
		try {
			cypher_query = visitor.getCypher();
		}catch(QueryNotSupportedException qe) {
			throw new QueryNotSupportedException("Conversion of the following query failed:\n" + sparql_query + "\nDue to the following reason:\n" + qe.getMessage());
		}
		System.out.println(cypher_query);
		System.out.println("Exit Sparql to Cypher Converter\n------------------------------------------");
		return visitor.getCypher();
	}
}
