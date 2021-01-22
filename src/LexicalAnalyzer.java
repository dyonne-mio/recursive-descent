/* Dyonne Maxime D. Macalino, CMPILER S12
 *  Problem Set - Simple Parser
 * */
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Token class which holds the type of the parsed token
 */
class Token {
    /**
     * Types for this lexical analyzer to analyze
     */
    enum TokenType {
        DIGIT,
        ADD,
        MULT,
        OPENP,
        CLOSEP,
        OPENB,
        CLOSEB,
        NEWLINE,
        ERROR
    }

    public TokenType tokenType;
    public String lexeme;

    /**
     * Token constructor
     * @param line String
     */
    public Token (String line){
        this.tokenType = identifyToken(line);
        this.lexeme = line;
    }

    /**
     * Returns the token's type
     * @return tokenType
     */
    public TokenType getTokenType() {
        return tokenType;
    }

    /**
     * Returns the token's lexeme
     * @return lexeme
     */
    public String getLexeme() {
        return lexeme;
    }

    /**
     * Returns the corresponding token type
     * @param line String
     */
    public static TokenType identifyToken(String line) {

        DFA dfa = new DFA();
        char[] c = new char[line.length()];

        //copy characters to array
        for (int i = 0; i < line.length(); i++)
            c[i] = line.charAt(i);

        //check final state
        String final_state = dfa.checkFinalState(line);
        if (final_state.equals("S10"))
            return TokenType.ADD;
        else if (final_state.equals("S11"))
            return TokenType.MULT;
        else if (final_state.equals("S12"))
            return TokenType.OPENP;
        else if (final_state.equals("S13"))
            return TokenType.CLOSEP;
        else if (final_state.equals("S14"))
            return TokenType.OPENB;
        else if (final_state.equals("S15"))
            return TokenType.CLOSEB;
        else if (final_state.equals("S16"))
            return TokenType.NEWLINE;
        else if (!final_state.equals("S0") && !final_state.equals("Sdead") && !final_state.equals("Sbottom"))
            return TokenType.DIGIT;
        else
            return TokenType.ERROR;
    }

    public static String matchToken(String token){
        if (token.equals("OPENP"))
            return "(";
        else if (token.equals("CLOSEP"))
            return ")";
        else if (token.equals("OPENB"))
            return "[";
        else if (token.equals("CLOSEB"))
            return "]";
        else if (token.equals("ADD"))
            return "+";
        else if (token.equals("MULT"))
            return "*";
        else
            return "unknown";
    }
}

/**
 * DFA class which shows DFA implementation
 */
class DFA {
    /**
     * Different states this DFA can have
     */
    enum States{
        S0(false), S1(true), S2(true), S10(true), S11(true),
        S12(true), S13(true), S14(true), S15(true), S16(true),
        Sdead(false), Sbottom(false);

        final boolean accept;

        States(boolean accept) {
            this.accept = accept;
        }

        States line, lparen, rparen, add, mult, lbracket, rbracket, zero, one, two, three, four, five, six, seven, eight, nine;

        static {
            S0.zero = S1; S0.one = S2; S0.two = S2; S0.three = S2; S0.four = S2; S0.five = S2; S0.six = S2;
            S0.seven = S2; S0.eight = S2; S0.nine = S2; S0.line = S16;
            S0.mult = S11; S0.add = S10; S0.lparen = S12; S0.lbracket = S14; S0.rparen = S13; S0.rbracket = S15;
            S2.zero = S2; S2.one = S2; S2.two = S2; S2.three = S2; S2.four = S2; S2.five = S2;
            S2.six = S2; S2.seven = S2; S2.eight = S2; S2.nine = S2;
        }

        /**
         * Different transitions this DFA can have
         */
        States transition(char symbol) {
            switch (symbol) {
                case '\n':
                    return this.line;
                case '(':
                    return this.lparen;
                case ')':
                    return this.rparen;
                case '[':
                    return this.lbracket;
                case ']':
                    return this.rbracket;
                case '+':
                    return this.add;
                case '*':
                    return this.mult;
                case '0':
                    return this.zero;
                case '1':
                    return this.one;
                case '2':
                    return this.two;
                case '3':
                    return this.three;
                case '4':
                    return this.four;
                case '5':
                    return this.five;
                case '6':
                    return this.six;
                case '7':
                    return this.seven;
                case '8':
                    return this.eight;
                case '9':
                    return this.nine;
                default:
                    return Sdead; //dead state if input is not part of the alphabet
            }
        }
    }

    /**
     * Maximal munch implementation that returns the final state
     * @param line String
     */
    public String checkFinalState(String line) {
        int i = 1;
        Stack<Pair<States, Integer>> stack = new Stack<>();
        States state;
        state = States.S0;
        Pair<States, Integer> pair = new Pair<>(States.Sbottom, i);
        stack.push(pair);
        while (i <= line.length() && state.transition(line.charAt(i-1)) != null) {
            if (state.accept)
                stack.empty();
            pair = new Pair<>(state, i);
            stack.push(pair);
            state = state.transition(line.charAt(i-1));
            i++;
        }
        while (!state.accept) {
            if (stack.isEmpty())
                return States.Sdead.toString();
            state = stack.peek().getKey();
            i = stack.peek().getValue();
            stack.pop();
            //dead state
            if (state == States.Sbottom)
                return States.Sdead.toString();
        }
        if (i > line.length())
            return state.toString();
        else
            return States.Sdead.toString(); //dead state
    }
}

/**
 * Lexical analyzer class
 */
public class LexicalAnalyzer {
    /**
     * Lexically analyzes input, prints out tokens in output, and process to return tokenList
     */
    static ArrayList<Token> process(String sourceCode){
        //delimiter: whitespace
        String[] words = sourceCode.split(" ");
        ArrayList<Token> tokenList = new ArrayList<>();
        String token = "";
        for (String w: words){
            if(!w.isEmpty()) {
                for (int i=0; i<w.length(); i++){
                    if((w.charAt(i) < 48 || w.charAt(i) > 57) && w.charAt(i)!='\n'){
                        if(!token.isEmpty()) {
                            Token t = new Token(token);
                            tokenList.add(t);
                            token = "";
                        }
                        Token t = new Token(w.charAt(i)+"");
                        tokenList.add(t);
                    }
                    else if (w.charAt(i) == '\n'){
                        if(!token.isEmpty()) {
                            Token t = new Token(token);
                            tokenList.add(t);
                            token = "";
                        }
                        Token t = new Token(w.charAt(i)+"");
                        tokenList.add(t);
                    }
                    else{
                        token = token.concat(w.charAt(i)+"");
                    }
                }
            }
        }
        return tokenList;
    }
}

