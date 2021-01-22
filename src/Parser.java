/* Dyonne Maxime D. Macalino, CMPILER S12
 *  Problem Set - Simple Parser
 * */
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Rule class which represents the different rules, LHS and RHS
 */
class Rule {
    public String LHS;
    public List<String> RHS = new ArrayList<>();
}

/**
 * Backstack class holds the different productions
 */
class Backstack{
    public String production;
    public int totalProductions, pastProductions;
    public Backstack(String production, int totalProductions, int pastProductions) {
        this.production = production;
        this.totalProductions = totalProductions;
        this.pastProductions = pastProductions;
    }
    public int getTotalProductions() {
        return totalProductions;
    }
    public int getPastProductions() {
        return pastProductions;
    }
    public String getProduction() {
        return production;
    }
    public void setPastProductions(int pastProductions) {
        this.pastProductions = pastProductions;
    }
}

/**
 * Simple parser that implements recursive descent
 */
public class Parser {
    /**
     * function to create rules and return hashmap of rules
     * @param text String
     */
    public static HashMap<String, Rule> createRuleMap(String text){
        //store rules
        HashMap<String, Rule> rules = new HashMap<>();
        String[] productions = text.split(";\n");

        for (String p: productions) {
            String[] line = p.split(": ");
            Rule R = new Rule();
            R.LHS = line[0];
            if (line[1].contains("|")) {
                String[] prod = line[1].split(" \\| ");
                R.RHS.addAll(Arrays.asList(prod));
            }
            else
                R.RHS.add(line[1]);
            rules.put(line[0], R);
        }
        return rules;
    }

    /**
     * function for recursive descent parsing
     * @param originalInput Stack<Token>
     * @param rulesMap HashMap<String, Rule>
     */
    public static void parse(Stack<Token> originalInput, HashMap<String, Rule> rulesMap){
        //stacks and lists
        Stack<String> currStack = new Stack<>();
        Stack<Token> backInput = new Stack<>();
        Stack<Token> input = originalInput;
        List<String> production = new ArrayList<>();
        List<Backstack> backstack = new ArrayList<>();
        currStack.push("S");
        boolean ruleStatus = false;
        production.add("0");
        while (!currStack.isEmpty() && !input.isEmpty()){
            String top = currStack.peek();
            if (rulesMap.get(top) != null) {
                production = rulesMap.get(top).RHS;
                if (backstack.isEmpty())
                    expand(currStack, production.get(0), backstack, rulesMap);
                else
                    expand(currStack, production.get(backstack.get(backstack.size()-1).getPastProductions()), backstack, rulesMap);
            }
            else if (top.equals("ε"))
                currStack.pop();
            else if (input.peek().getTokenType().toString().equals("ERROR")){
                ruleStatus = true;
                currStack.clear();
                break;
            }
            else{
                for (int x=0; x < Token.TokenType.values().length; x++) {
                    if (top.equals(input.peek().getTokenType().toString())){
                        currStack.pop();
                        input.pop();
                        ruleStatus = false;
                        break;
                    }
                    else
                        ruleStatus = true;
                }
                if (ruleStatus){
                    input.clear();
                    input.addAll(backInput);
                    backtrackingProcess(currStack, backstack, rulesMap);
                }
            }
            backInput.clear();
            backInput.addAll(input);
        }
        boolean missingStatus = false;
        if (!currStack.isEmpty()){
            while(!currStack.isEmpty()) {
                if (rulesMap.get(currStack.peek())!= null)
                    currStack.pop();
                else {
                    System.out.println(" - REJECT. Missing token '" + Token.matchToken(currStack.pop()) + "'"); //missing token
                    missingStatus = true;
                }
            }
        }
        if (ruleStatus && !missingStatus)
            System.out.println(" - REJECT. Offending token '" + input.peek().getLexeme() + "'"); //offending token
        else if (!missingStatus)
            System.out.println(" - ACCEPT");
    }

    /**
     * function for expanding production rule
     * @param stack Stack
     * @param production String
     * @param backstack List<Backstack>
     * @param rulesMap HashMap<String, Rule>
     */
    public static void expand(Stack stack, String production, List<Backstack>backstack, HashMap<String, Rule> rulesMap){
        if (stack.isEmpty())
            return;
        stack.pop();
        String[] prod = production.split(" ");
        for(int i = prod.length-1; i >= 0; i--){
            stack.push(prod[i]);
            if (rulesMap.get(prod[i]) != null)
                backstack.add(new Backstack(prod[i], rulesMap.get(prod[i]).RHS.size(), 0));
            else
                backstack.add(new Backstack(prod[i], 0, 0));
        }
    }

    /**
     * function for backtracking
     * @param currStack Stack
     * @param previousStack List<Backstack>
     * @param rulesMap HashMap<String, Rule>
     */
    public static void backtrackingProcess(Stack currStack, List<Backstack> previousStack, HashMap<String, Rule> rulesMap){
        while(true){
            if (previousStack.isEmpty()){
                return;
            }
            else if (currStack.isEmpty()){
                while (rulesMap.get(previousStack.get(previousStack.size()-1)) != null){
                    previousStack.remove(previousStack.size() - 1);
                }
                currStack.push(previousStack.get(previousStack.size() - 1).getProduction());
                previousStack.get(previousStack.size()-1).setPastProductions(previousStack.get(previousStack.size()-1).getPastProductions()+1);
                break;
            }
            if(!currStack.peek().equals(previousStack.get(previousStack.size()-1).getProduction())){
                if (previousStack.get(previousStack.size() - 1).getPastProductions() >= previousStack.get(previousStack.size() - 1).getTotalProductions())
                    while ((previousStack.get(previousStack.size() - 1).getPastProductions() >= previousStack.get(previousStack.size() - 1).getTotalProductions()))
                        previousStack.remove(previousStack.size() - 1);
                currStack.push(previousStack.get(previousStack.size() - 1).getProduction());
                previousStack.get(previousStack.size()-1).setPastProductions(previousStack.get(previousStack.size()-1).getPastProductions()+1);
                break;
            }
            else{
                previousStack.remove(previousStack.size() - 1);
                currStack.pop();
            }
        }
        //last production
        if (previousStack.get(previousStack.size()-1).getPastProductions() >= previousStack.get(previousStack.size()-1).getTotalProductions()){
            backtrackingProcess(currStack, previousStack, rulesMap);
        }
    }
}
