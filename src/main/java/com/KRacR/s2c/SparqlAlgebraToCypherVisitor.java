package com.KRacR.s2c;

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

public class SparqlAlgebraToCypherVisitor implements OpVisitor {
	String cypher;
	
	@Override
	public void visit(OpBGP opBGP) {
		// TODO Auto-generated method stub
		System.out.println("In opBGP\n" + opBGP.toString());
	}

	@Override
	public void visit(OpQuadPattern quadPattern) {
		// TODO Auto-generated method stub
		System.out.println("In quadPattern\n" + quadPattern.toString());
	}

	@Override
	public void visit(OpQuadBlock quadBlock) {
		// TODO Auto-generated method stub
		System.out.println("In quadBlock\n" + quadBlock.toString());
	}

	@Override
	public void visit(OpTriple opTriple) {
		// TODO Auto-generated method stub
		System.out.println("In opTriple\n" + opTriple.toString());
	}

	@Override
	public void visit(OpQuad opQuad) {
		// TODO Auto-generated method stub
		System.out.println("In opQuad\n" + opQuad.toString());
	}

	@Override
	public void visit(OpPath opPath) {
		// TODO Auto-generated method stub
		System.out.println("In opPath\n" + opPath.toString());
	}

	@Override
	public void visit(OpFind opFind) {
		// TODO Auto-generated method stub
		System.out.println("In opFind\n" + opFind.toString());
	}

	@Override
	public void visit(OpTable opTable) {
		// TODO Auto-generated method stub
		System.out.println("In opTable\n" + opTable.toString());
	}

	@Override
	public void visit(OpNull opNull) {
		// TODO Auto-generated method stub
		System.out.println("In opNull\n" + opNull.toString());
	}

	@Override
	public void visit(OpProcedure opProc) {
		// TODO Auto-generated method stub
		System.out.println("In opProc\n" + opProc.toString());
	}

	@Override
	public void visit(OpPropFunc opPropFunc) {
		// TODO Auto-generated method stub
		System.out.println("In opPropFunc\n" + opPropFunc.toString());
	}

	@Override
	public void visit(OpFilter opFilter) {
		// TODO Auto-generated method stub
		System.out.println("In opFilter\n" + opFilter.toString());
	}

	@Override
	public void visit(OpGraph opGraph) {
		// TODO Auto-generated method stub
		System.out.println("In opGraph\n" + opGraph.toString());
	}

	@Override
	public void visit(OpService opService) {
		// TODO Auto-generated method stub
		System.out.println("In opService\n" + opService.toString());
	}

	@Override
	public void visit(OpDatasetNames dsNames) {
		// TODO Auto-generated method stub
		System.out.println("In dsNames\n" + dsNames.toString());
	}

	@Override
	public void visit(OpLabel opLabel) {
		// TODO Auto-generated method stub
		System.out.println("In opLabel\n" + opLabel.toString());
	}

	@Override
	public void visit(OpAssign opAssign) {
		// TODO Auto-generated method stub
		System.out.println("In opAssign\n" + opAssign.toString());
	}

	@Override
	public void visit(OpExtend opExtend) {
		// TODO Auto-generated method stub
		System.out.println("In opExtend\n" + opExtend.toString());
	}

	@Override
	public void visit(OpJoin opJoin) {
		// TODO Auto-generated method stub
		System.out.println("In opJoin\n" + opJoin.toString());
	}

	@Override
	public void visit(OpLeftJoin opLeftJoin) {
		// TODO Auto-generated method stub
		System.out.println("In opLeftJoin\n" + opLeftJoin.toString());
	}

	@Override
	public void visit(OpUnion opUnion) {
		// TODO Auto-generated method stub
		System.out.println("In opUnion\n" + opUnion.toString());
	}

	@Override
	public void visit(OpDiff opDiff) {
		// TODO Auto-generated method stub
		System.out.println("In opDiff\n" + opDiff.toString());
	}

	@Override
	public void visit(OpMinus opMinus) {
		// TODO Auto-generated method stub
		System.out.println("In opMinus\n" + opMinus.toString());
	}

	@Override
	public void visit(OpConditional opCondition) {
		// TODO Auto-generated method stub
		System.out.println("In opCondition\n" + opCondition.toString());
	}

	@Override
	public void visit(OpSequence opSequence) {
		// TODO Auto-generated method stub
		System.out.println("In opSequence\n" + opSequence.toString());
	}

	@Override
	public void visit(OpDisjunction opDisjunction) {
		// TODO Auto-generated method stub
		System.out.println("In opDisjunction\n" + opDisjunction.toString());
	}

	@Override
	public void visit(OpList opList) {
		// TODO Auto-generated method stub
		System.out.println("In opList\n" + opList.toString());
	}

	@Override
	public void visit(OpOrder opOrder) {
		// TODO Auto-generated method stub
		System.out.println("In opOrder\n" + opOrder.toString());
	}

	@Override
	public void visit(OpProject opProject) {
		// TODO Auto-generated method stub
		System.out.println("In opProject\n" + opProject.toString());
	}

	@Override
	public void visit(OpReduced opReduced) {
		// TODO Auto-generated method stub
		System.out.println("In opReduced\n" + opReduced.toString());
	}

	@Override
	public void visit(OpDistinct opDistinct) {
		// TODO Auto-generated method stub
		System.out.println("In opDistinct\n" + opDistinct.toString());
	}

	@Override
	public void visit(OpSlice opSlice) {
		// TODO Auto-generated method stub
		System.out.println("In opSlice\n" + opSlice.toString());
	}

	@Override
	public void visit(OpGroup opGroup) {
		// TODO Auto-generated method stub
		System.out.println("In opGroup\n" + opGroup.toString());
	}

	@Override
	public void visit(OpTopN opTop) {
		// TODO Auto-generated method stub
		System.out.println("In opTop\n" + opTop.toString());
	}

	public String getCypher() {
		// TODO Auto-generated method stub
		return "match (n)-[e:Edge]->(r {uri:\"http://localhost/persons/Paul_Erdoes\"}) return n.uri as subject, e.uri as predicate";
	}

}
