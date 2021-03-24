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
