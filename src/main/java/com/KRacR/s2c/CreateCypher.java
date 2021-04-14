package com.KRacR.s2c;

import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFVisitor;
import org.apache.jena.rdf.model.Resource;

// TODO: Change all "IRI" to "URI"
// TODO: Check if there exists a unique id for every node in Neo4j

class CreateCypher implements RDFVisitor {
	String operation;
	String label;
	public CreateCypher() {
		this.label = "";
		this.operation = "";
	}
	
	public CreateCypher(String operation) {
		this.label = "";
		this.operation = operation + " ";
	}

	@Override
	public String visitBlank(Resource r, AnonId id) {
		return String.format(
				"%s(%s {uri:\"\", id:\"%s\", stringrep:\"%s\"})",
				this.operation,
				this.label,
				id.getBlankNodeId().toString(),
				id.getBlankNodeId().toString()
		);
	}

	@Override
	public String visitURI(Resource r, String uri) {
		return String.format(
				"%s(%s {uri:\"%s\", stringrep:\"%s\"})",
				this.operation,
				this.label,
				uri,
				uri
		);
	}

	@Override
	public String visitLiteral(Literal l) {
		return 
				(l.getDatatype() instanceof org.apache.jena.datatypes.xsd.impl.RDFLangString)?
				(
					String.format(
						"%s(%s {uri:\"\", typeiri:\"%s\", lexform:\"%s\", langtag:\"%s\", stringrep:\"%s\"})",
						this.operation,
						this.label,
						l.getDatatypeURI(), 
						l.getLexicalForm(),
						l.getLanguage(),
						l.getLexicalForm() + "@" + l.getLanguage()
					)
				):(
					String.format(
						"%s(%s {uri:\"\", typeiri:\"%s\", lexform:\"%s\", stringrep:\"%s\"})",
						this.operation,
						this.label,
						l.getDatatypeURI(), 
						l.getLexicalForm(),
						l.getLexicalForm()
					)
				);
	}

	public void set_label(String label) {
		this.label = label;
	}

}
