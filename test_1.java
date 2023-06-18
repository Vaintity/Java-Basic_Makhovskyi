import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class test_1 {
    
    public enum LexemeType {
        NUMBER,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        LPAREN,
        RPAREN,
        VARIABLE,
        EQUALS,
        EOF
    }
    
    public static class Lexeme {
        public LexemeType type;
        public String value;
        
        public Lexeme(LexemeType type, String value) {
            this.type = type;
            this.value = value;
        }
        
        public Lexeme(LexemeType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }
        
        @Override
        public String toString() {
            return "Lexeme [type=" + type + ", value=" + value + "]";
        }
    }
    
    public static List<Lexeme> lexAnalyze(String expText) {
        ArrayList<Lexeme> lexemes = new ArrayList<Lexeme>();
        int pos = 0;
        while (pos < expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LPAREN, "("));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RPAREN, ")"));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.PLUS, "+"));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.MINUS, "-"));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.MULTIPLY, "*"));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.DIVIDE, "/"));
                    pos++;
                    continue;
                case 'x':
                    lexemes.add(new Lexeme(LexemeType.VARIABLE, "x"));
                    pos++;
                    continue;
                case '=':
                    lexemes.add(new Lexeme(LexemeType.EQUALS, "="));
                    pos++;
                    continue;
                default:
                    if (Character.isDigit(c)) {
                        StringBuilder sb = new StringBuilder();
                        while (Character.isDigit(c) || c == '.') {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        }
                        lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                    }
                    else {
                        if (c != ' ') {
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                        pos++;
                    }
                }
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        int i = 0;
        int rpar = 0;
        int lpar = 0;
        while (i < lexemes.size()) {
            if (lexemes.get(i).type == LexemeType.RPAREN) {
                rpar++;
            }
            else if (lexemes.get(i).type == LexemeType.LPAREN) {
                lpar++;
            }
            i++;
        }
        if (rpar != lpar) {
            throw new RuntimeException("Wrong number of parentheses");
        }
        return lexemes;
    }

    public static class LexemeBuffer {
        private int pos;
        public List<Lexeme> lexemes;

        public LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
        }

        public Lexeme next() {
            return lexemes.get(pos++);
        }

        public void back() {
            pos--;
        }

        public int getPos() {
            return pos;
        }
    }

    public static boolean expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            throw new RuntimeException("Unexpected end of expression");
        }
        else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    public static boolean plusminus(LexemeBuffer lexemes) {
        boolean value = multdiv(lexemes);
        while(true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case PLUS:
                    value = multdiv(lexemes);
                    break;
                case MINUS:
                    value = multdiv(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    public static boolean multdiv(LexemeBuffer lexemes) {
        boolean value = factor(lexemes);
        while(true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case MULTIPLY:
                    value = factor(lexemes);
                    break;
                case DIVIDE:
                    value = factor(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    public static boolean factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case MINUS:
                return !factor(lexemes);
            case NUMBER:
                return Double.parseDouble(lexeme.value) > 0;
            case VARIABLE:
                return true;
            case EQUALS:
                return expr(lexemes);
            case LPAREN:
                boolean result = expr(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RPAREN) {
                    throw new RuntimeException("Unexpected token: " + lexeme.value + " at position: " + lexemes.getPos());
                }
                return result;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value + " at position: " + lexemes.getPos());
        }
    }

    public static double exprF(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            throw new RuntimeException("Unexpected end of expression");
        }
        else {
            lexemes.back();
            return plusminusF(lexemes);
        }
    }

    public static double plusminusF(LexemeBuffer lexemes) {
        double value = multdivF(lexemes);
        while(true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case PLUS:
                    value += multdivF(lexemes);
                    break;
                case MINUS:
                    value -= multdivF(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    public static double multdivF(LexemeBuffer lexemes) {
        double value = factorF(lexemes);
        while(true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case MULTIPLY:
                    value *= factorF(lexemes);
                    break;
                case DIVIDE:
                    value /= factorF(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    public static double factorF(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case MINUS:
                return -factorF(lexemes);
            case NUMBER:
                return Double.parseDouble(lexeme.value);
            case LPAREN:
                double result = exprF(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RPAREN) {
                    throw new RuntimeException("Unexpected token: " + lexeme.value + " at position: " + lexemes.getPos());
                }
                return result;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value + " at position: " + lexemes.getPos());
        }
    }

    public static class Action {
        public String value;
        
        public Action(String value) {
            this.value = value;
        }
        
        public Action(Character value) {
            this.value = value.toString();
        }
    }

    public static void main(String[] args) {
        // java -classpath ./mysql-connector-j-8.0.33.jar:./ test_1 
        // url = jdbc:mysql://localhost/func_storage
        // username = root
        // password = H6r-8gF4_dra
        // test expression: (2*(2/2)-4)+x=18
        Scanner urlSc = new Scanner(System.in);
        System.out.println("Enter your url: ");
        String url = urlSc.nextLine();
        Scanner usernameSc = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = usernameSc.nextLine();
        Scanner passwordSc = new Scanner(System.in);
        System.out.println("Enter your password: ");
        String password = passwordSc.nextLine();
        
        try{
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)){
                System.out.println("Connection to DB succesfull!");

                Scanner act = new Scanner(System.in);
                System.out.println("Choose action: ");
                System.out.println("n - enter new function");
                System.out.println("r - enter new roots");
                System.out.println("f - find funtion in database");
                System.out.println("q - exit");
                String actionInput = act.nextLine();
                Action action = new Action(actionInput);
                do {
                    switch (action.value) {
                        case "n":
                            Scanner funcSc = new Scanner(System.in);
                            System.out.println("Enter your function: ");
                            String func = funcSc.nextLine();
                            System.out.println("You entered: " + func);
        
                            List<Lexeme> lexemes = lexAnalyze(func);
                            LexemeBuffer lexemesBuffer = new LexemeBuffer(lexemes);
        
                            if (expr(lexemesBuffer)) {
                                System.out.println("Function is correct");

                                String empty = "SELECT EXISTS(SELECT 1 FROM func)";
                                PreparedStatement stmt = conn.prepareStatement(empty);
                                ResultSet rs = stmt.executeQuery();
                                if (rs.next()) {
                                    int exists = rs.getInt(1);
                                    if (exists == 0) {
                                        System.out.println("Table is empty");

                                        Statement statementFunc = (Statement) conn.createStatement();
                                        int rows = statementFunc.executeUpdate("INSERT func(funccontent) VALUES ('" + func + "')");
                                        System.out.println("Added " + rows + " rows");
                                    }
                                    else {
                                        Statement statementFuncEx = (Statement) conn.createStatement();
                                        ResultSet resultSetFunc = statementFuncEx.executeQuery("SELECT * FROM func");
                                        int flag = 0;
                                        while(resultSetFunc.next()){
                                            String funcContentEx = resultSetFunc.getString(2);
                                            if (funcContentEx.equals(func)) {
                                                System.out.println("Function is already in database");
                                                flag = 1;
                                            }
                                        }
                                        if (flag == 0) {
                                            Statement statementFunc = (Statement) conn.createStatement();
                                            int rows = statementFunc.executeUpdate("INSERT func(funccontent) VALUES ('" + func + "')");
                                            System.out.println("Added " + rows + " rows");
                                        }
                                    }
                                }
                            }   
                            else {
                                System.out.println("Function is incorrect");
                            }
                            break;
                        case "r":
                            System.out.println("How many roots: ");
                            String rootAmountf = act.nextLine();
                            Scanner rootScf = new Scanner(System.in);
                            ArrayList<String> rootsListf = new ArrayList<String>();
                            while (Integer.parseInt(rootAmountf) > 0) {
                                System.out.println("Enter root: ");
                                String rootf1 = rootScf.nextLine();
                                rootsListf.add(rootf1);
                                rootAmountf = Integer.toString(Integer.parseInt(rootAmountf) - 1);
                            }
                            System.out.println("You entered: " + rootsListf);
                            
                            String empty = "SELECT EXISTS(SELECT 1 FROM roots)";
                            PreparedStatement stmt = conn.prepareStatement(empty);
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                int exists = rs.getInt(1);
                                if (exists == 0) {
                                    System.out.println("Table is empty");

                                    Statement statementRootf = (Statement) conn.createStatement();
                                    ResultSet resultSetf = statementRootf.executeQuery("SELECT * FROM func");
                                    //int funcId = 0;
                                    while(resultSetf.next()){
                                        //funcId = resultSetf.getInt(1);
                                        String funcContentf = resultSetf.getString(2);
                                        for (int i = 0; i < rootsListf.size(); i++) {
                                            String funcIf = funcContentf.replace("x", rootsListf.get(i));
                                            for (int j = 0; j < funcIf.length(); j++) {
                                                if (funcIf.charAt(j) == '=') {
                                                    String funcI1f = funcIf.substring(0, j);
                                                    String funcI2f = funcIf.substring(j + 1);
                                                    List<Lexeme> lexemesI1f = lexAnalyze(funcI1f);
                                                    LexemeBuffer lexemesBufferI1f = new LexemeBuffer(lexemesI1f);
                                                    List<Lexeme> lexemesI2f = lexAnalyze(funcI2f);
                                                    LexemeBuffer lexemesBufferI2f = new LexemeBuffer(lexemesI2f);
                                                    if (exprF(lexemesBufferI1f) == exprF(lexemesBufferI2f)) {
                                                        System.out.println("Function " + funcContentf + " with root: " + rootsListf.get(i) + " is correct");
                                                        Statement statementRootin = (Statement) conn.createStatement();
                                                        int rowsR = statementRootin.executeUpdate("INSERT roots(rootscontent) VALUES ('" + Double.parseDouble(rootsListf.get(i)) + "')");
                                                        System.out.println("Added " + rowsR + " rows to roots");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                Statement statementRoot = (Statement) conn.createStatement();
                                ResultSet resultSetr = statementRoot.executeQuery("SELECT * FROM roots");
                                //int rootsId = 0;
                                for (int i = 0; i < rootsListf.size(); i++) {
                                    while(resultSetr.next()){
                                        //rootsId = resultSetr.getInt(1);
                                        String rootsContent = resultSetr.getString(2);
                                        if (rootsContent.equals(rootsListf.get(i))) {
                                            System.out.println("Root " + rootsListf.get(i) + " is already in database");
                                        }
                                        else {
                                            Statement statementRootf = (Statement) conn.createStatement();
                                            ResultSet resultSetf = statementRootf.executeQuery("SELECT * FROM func");
                                            //int funcId = 0;
                                            while(resultSetf.next()){
                                                //funcId = resultSetf.getInt(1);
                                                String funcContentf = resultSetf.getString(2);
                                                String funcIf = funcContentf.replace("x", rootsListf.get(i));
                                                for (int j = 0; j < funcIf.length(); j++) {
                                                    if (funcIf.charAt(j) == '=') {
                                                        String funcI1f = funcIf.substring(0, j);
                                                        String funcI2f = funcIf.substring(j + 1);
                                                        List<Lexeme> lexemesI1f = lexAnalyze(funcI1f);
                                                        LexemeBuffer lexemesBufferI1f = new LexemeBuffer(lexemesI1f);
                                                        List<Lexeme> lexemesI2f = lexAnalyze(funcI2f);
                                                        LexemeBuffer lexemesBufferI2f = new LexemeBuffer(lexemesI2f);
                                                        if (exprF(lexemesBufferI1f) == exprF(lexemesBufferI2f)) {
                                                            System.out.println("Function " + funcContentf + " with root: " + rootsListf.get(i) + " is correct");
                                                            Statement statementRootin = (Statement) conn.createStatement();
                                                            int rowsR = statementRootin.executeUpdate("INSERT roots(rootscontent) VALUES ('" + Double.parseDouble(rootsListf.get(i)) + "')");
                                                            System.out.println("Added " + rowsR + " rows to roots");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            break;
                        case "f":
                            System.out.println("How many roots: ");
                            String rootAmount = act.nextLine();
                            Scanner rootSc = new Scanner(System.in);
                            ArrayList<String> rootsList = new ArrayList<String>();
                            while (Integer.parseInt(rootAmount) > 0) {
                                System.out.println("Enter root: ");
                                String root = rootSc.nextLine();
                                rootsList.add(root);
                                rootAmount = Integer.toString(Integer.parseInt(rootAmount) - 1);
                            }
                            System.out.println("You entered: " + rootsList);

                            Statement statementRootfind = (Statement) conn.createStatement();
                            ResultSet resultSet = statementRootfind.executeQuery("SELECT * FROM func");
                            //int funcId = 0;
                            while(resultSet.next()){
                                //funcId = resultSet.getInt(1);
                                String funcContent = resultSet.getString(2);
                                for (int i = 0; i < rootsList.size(); i++) {
                                    String funcI = funcContent.replace("x", rootsList.get(i));
                                    //System.out.println("Function with root: " + rootsList.get(i) + " is: " + funcI);
                                    for (int j = 0; j < funcI.length(); j++) {
                                        if (funcI.charAt(j) == '=') {
                                            String funcI1 = funcI.substring(0, j);
                                            String funcI2 = funcI.substring(j + 1);
                                            List<Lexeme> lexemesI1 = lexAnalyze(funcI1);
                                            LexemeBuffer lexemesBufferI1 = new LexemeBuffer(lexemesI1);
                                            List<Lexeme> lexemesI2 = lexAnalyze(funcI2);
                                            LexemeBuffer lexemesBufferI2 = new LexemeBuffer(lexemesI2);
                                            if (exprF(lexemesBufferI1) == exprF(lexemesBufferI2)) {
                                                System.out.println("Function " + funcContent + " with root: " + rootsList.get(i) + " is correct");
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case "q":
                        System.exit(0);
                            break;
                        default:
                            System.out.println("Wrong action");
                            break;
                    }
                    System.out.println("Choose action: ");
                    System.out.println("n - enter new function");
                    System.out.println("r - enter new roots");
                    System.out.println("f - find funtion in database");
                    System.out.println("q - exit");
                    String nextAction = act.nextLine();
                    action.value = nextAction;
                }  while (action.value != "q");
                
                act.close();
            }
        }
        catch(Exception ex){
            System.out.println("Connection failed...");
              
            System.out.println(ex);
        }

        
        usernameSc.close();
        urlSc.close();
        passwordSc.close();
    }
}