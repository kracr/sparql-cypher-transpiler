//		DatabaseManagementService managementService = null;
//		try {
//			managementService = new DatabaseManagementServiceBuilder(Paths.get(""))
//					.setConfig(GraphDatabaseSettings.plugin_dir, Paths.get("lib/neo4j-test-plugins"))
//					//.setConfig(GraphDatabaseSettings.procedure_allowlist, Arrays.asList("apoc.load.json"))
//					//.setConfig(GraphDatabaseSettings., null)
//					.build();
//	        GraphDatabaseService db = managementService.database( DEFAULT_DATABASE_NAME );
//	        
//	        try ( Transaction tx = db.beginTx() ){
//	        	tx.execute(
//	        			"with \"file:///Output.json\" as url "
//	        			+ "CALL apoc.load.json(url) yield value "
//	        			+ "unwind value.vertices as vertex "
//	        			+ "unwind value.edges as edge "
//	        			+ "merge (v {id:vertex.id, vname:vertex.vname}) on create set v=vertex "
//	        			+ "merge ({vname:edge.out})-[e:Edge {id:edge.id, type:edge.type}]->({vname:edge.in}) on create set e=edge;"
//	        	);
//	        	tx.commit();
//	        }
//		}finally {
//			if(managementService != null) managementService.shutdown();
//		}
		
		Path pluginDirContainingApocJar = null;
		try {
			pluginDirContainingApocJar = new File(
			        ApocConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI())
			        .getParentFile().toPath();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Neo4j neo4j = Neo4jBuilders
                .newInProcessBuilder()
                .withDisabledServer()
                .withFixture("CREATE (p1:Person)-[:knows]->(p2:Person)-[:knows]->(p3:Person)")
                .withConfig(GraphDatabaseSettings.plugin_dir, pluginDirContainingApocJar)
                .withConfig(GraphDatabaseSettings.procedure_unrestricted, Arrays.asList("apoc.*"))
                .build();
        Driver driver = GraphDatabase.driver(neo4j.boltURI(), AuthTokens.none());
