/* Dyonne Maxime D. Macalino, CMPILER S12
 *  Problem Set - Simple Parser
 * */

/*references/guides:
https://www.cs.bgu.ac.il/~comp171/wiki.files/01-scanning.pdf
https://grrinchas.github.io/posts/dfa-in-java?fbclid=IwAR37iucBV6iKb1Gg_0hj7-nxDDOZ5fLUGbKjW_0RDbjRGoYj6Ho4m_XG7UM
https://www.youtube.com/watch?v=JO_0e9mPofY
https://karmin.ch/ebnf/examples
*/
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Main class which is the entry class file for this project
 */
public class Main {
    /**
     * function to read grammar.txt
    */
    public static HashMap<String, Rule> fileReaderGrammar(HashMap<String, Rule> rules) throws IOException {
        /* read the grammar.txt line by line */
        URL path = Main.class.getResource("grammar.txt");
        File f = new File(path.getFile());
        BufferedReader reader = new BufferedReader(new FileReader(f));
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
                line = reader.readLine();
            }
            rules = Parser.createRuleMap(stringBuilder.toString());
        } finally {
            reader.close();
        }
        return rules;
    }

    /**
     * function to read input.txt
    */
    public static void fileReader(ArrayList<Token> tokenList) throws IOException {
        /* read the input.txt line by line */
        URL path = Main.class.getResource("input.txt");
        File f = new File(path.getFile());
        BufferedReader reader = new BufferedReader(new FileReader(f));
        try {
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
            tokenList.addAll(LexicalAnalyzer.process(sb.toString()));
        }
        finally {
            reader.close();
        }
    }

    /**
     * main function
     */
    public static void main(String[] args)throws Exception {
        HashMap<String, Rule> rules = new HashMap<String, Rule>();
        HashMap<String, Rule> grammarRules = new HashMap<String, Rule>();
        grammarRules = fileReaderGrammar(rules); //read grammar

        ArrayList<Token> tokenList = new ArrayList<>();
        fileReader(tokenList); //read input

        Stack<Token> stack = new Stack<>();
        ArrayList<Token> list = new ArrayList<>();

        System.out.println("Parsing complete!");
        File file = new File("output.txt");
        PrintStream stream = new PrintStream(file);
        System.setOut(stream);

        for (int i = 0; i < tokenList.size(); i++) {
            if (tokenList.get(i).tokenType.toString().equals("NEWLINE")) {
                stack.addAll(list);
                for (int x = list.size() - 1; x >= 0; x--)
                    System.out.print(list.get(x).getLexeme() + " ");
                list.clear();
                if (!stack.isEmpty()) {
                    Parser.parse(stack, grammarRules);
                    stack.clear();
                }
            }
            else
                list.add(0, tokenList.get(i));
        }
    }
}

