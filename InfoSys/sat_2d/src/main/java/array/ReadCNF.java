package array;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ReadCNF {
    @SuppressWarnings("unchecked")
    public static List<Integer>[] readCNF(String fileName)
            throws IllegalArgumentException, IOException {
        if (!fileName.substring(fileName.length() - 4).equals(".cnf")) {
            throw new IllegalArgumentException("Invalid file format.");
        }

        String currPath = new File("").getAbsolutePath();
        FileReader readFile = new FileReader(currPath + "/sat_2d/sampleCNF/" + fileName);
        Scanner reader = new Scanner(readFile);
        List<Integer> helper = new ArrayList<>();
        String[] headers;
        String[] params;

        String line = reader.nextLine().trim();
        while (line.startsWith("c") || line.matches("\\s+") || line.isEmpty())
            line = reader.nextLine().trim();

        headers = line.split(" ");
        if (!headers[0].equals("p")) throw new IllegalArgumentException("Missing problem line.");
        if (!headers[1].equals("cnf"))
            throw new IllegalArgumentException("Missing file format declaration.");

        int numOfVars = Integer.parseInt(headers[2]);
        int numOfClauses = Integer.parseInt(headers[3]);
        List<Integer>[] clauses = (List<Integer>[]) new ArrayList[numOfClauses];
        int counter = 0;

        outerloop:
        while (reader.hasNextLine()) {
            line = reader.nextLine().trim();

            while (line.startsWith("c") || line.matches("\\s+") || line.isEmpty()) {
                try {
                    line = reader.nextLine();
                } catch (NoSuchElementException ex) {
                    break outerloop;
                }
            }

            params = line.split(" ");
            for (String param : params) {
                if (!param.equals("0")) {
                    int literal = Integer.parseInt(param);
                    helper.add(literal);
                } else {
                    clauses[counter] = helper;
                    counter++;
                    helper = new ArrayList<>();
                }
            }



        }

        if (counter != numOfClauses)
            throw new IllegalArgumentException("Wrong number of clauses.");

        reader.close();
        readFile.close();
        return clauses;
    }
}

