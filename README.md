# Sparql-to-cypher
This repository contains material related to the SPARQL to Cypher Transpiler.

## Usage
To obtain the repository, execute:
```
git clone https://github.com/kracr/sparql-cypher-transpiler
```

The project is organised around the [Maven](https://maven.apache.org/) build system. To execute the testbench and view the results, execute:
```
mvn test
```

## Description of Components
### Testbench
It must be demonstrated that the query conversion process retains the query semantics from the original language. Following the Test Driven Development
practices, we developed a testbench to test the query conversion and validity. It has support for changing the underlying graph conversion schemes and hence independent of the RDF to PG conversion. This makes it suitable for all the phases in the three-phase development process. The testbench is aimed to provide fast feedback and exhaustively test the transpiler over various datasets and language constructs. It is also made for easy extension of test cases over different databases and queries. Each testcase in the testbench consists of a RDF dataset and SPARQL query. The RDF Graph is converted to PG and SPARQL to Cypher, and the results of executing the queries on their respective datasets is then evaluated for equivalence. The RDF to PG conversion is used to ensure the correctness of transpiler by evaluating equivalence of results of the SPARQL and converted Cypher queries.

![alt text](https://github.com/LakshyAAAgrawal/Sparql-to-cypher/blob/readme_update/readme_images/Testbench.png?raw=true)

### RDF to PG
The custom RDF to PG converter for phase one iterates over each triple in an RDF dataset and creates a node for the subject and the object in the triple. The predicate in the triple becomes the edge label in the PG. A *MERGE* query corresponding to the subject and object of each triple is created using the visitor design pattern. The visitor pattern is used to exhaustively cover all the RDF node types - blank node, IRIs and literals. *MERGE* query in Cypher matches the node pattern in the PG, and upon non-existence, creates it.

![alt text](https://github.com/LakshyAAAgrawal/Sparql-to-cypher/blob/readme_update/readme_images/rdf_to_pg.jpeg?raw=true)

### SPARQL to Cypher:
![alt text](https://github.com/LakshyAAAgrawal/Sparql-to-cypher/blob/readme_update/readme_images/s2c.png?raw=true)
