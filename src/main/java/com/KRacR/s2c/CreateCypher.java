package com.KRacR.s2c;

import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFVisitor;
import org.apache.jena.rdf.model.Resource;

// TODO: Change all "IRI" to "URI"
// TODO: Check if there exists a unique id for every node in Neo4j

class CreateCypher implements RDFVisitor {
	String label;
	public CreateCypher() {
		this.label = "";
	}

	@Override
	public String visitBlank(Resource r, AnonId id) {
		return String.format(
				"MERGE (%s {uri:\"\", id:\"%s\"})",
				this.label,
				id.getBlankNodeId().toString()
		);
	}

	@Override
	public String visitURI(Resource r, String uri) {
		return String.format(
				"MERGE (%s {uri:\"%s\"})",
				this.label,
				uri
		);
	}

	@Override
	public String visitLiteral(Literal l) {
		return 
				(l.getDatatype() instanceof org.apache.jena.datatypes.xsd.impl.RDFLangString)?
				(
					String.format(
						"MERGE (%s {uri:\"\", typeiri:\"%s\", lexform:\"%s\", langtag:\"%s\"})", 
						this.label,
						l.getDatatypeURI(), 
						l.getLexicalForm(),
						l.getLanguage()
					)
				):(
					String.format(
						"MERGE (%s {uri:\"\", typeiri:\"%s\", lexform:\"%s\"})", 
						this.label,
						l.getDatatypeURI(), 
						l.getLexicalForm()
					)
				);
	}

	public void set_label(String label) {
		this.label = label;
	}

}
