import java.util.*;

public class Board  implements Ilayout, Cloneable {
    private static int dim;
    private char board[][];
    private char board_char[];
    private char goal[][];
    private String s;
    private int g;
    private HashMap<Integer, Point> map_goal = new HashMap<Integer, Point>();
    private String objective;

    public Board() {
        board = new char[dim][dim];
    }

    /**
     * Construtor de Board, este construtor é usado apenas para o estado inicial onde não existe custo
     * @param str  Estado inicial
     * @param obj   Estado final
     */
    public Board(String str, String obj){
        s = str;
        g = 0;
        dim = dimention(str);
        if (str.length() != dim * dim) throw new
                IllegalStateException("Invalid arg in Board constructor");
        board = new char[dim][dim];
        goal = new char[dim][dim];
        int si = 0;
        objective = obj;
        //setGoal(obj);
        board_char = str.toCharArray();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                board[i][j] = str.charAt(si++);
            }
        }

    }

    /**
     *  Construtor de Board
     * @param str   Estado inicial
     * @param G Custo do estado
     * @param obj   Estado final
     * @throws IllegalStateException
     */
    public Board(String str, int G, String obj) throws IllegalStateException {
        s = str;
        g = G;
        dim = dimention(str);
        if (str.length() != dim * dim) throw new
                IllegalStateException("Invalid arg in Board constructor");
        board = new char[dim][dim];
        int si = 0;
        objective = obj;
        //setGoal(obj);
        board_char = str.toCharArray();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                board[i][j] = str.charAt(si++);
            }
        }
    }

    /**
     * Metodo que define a dimensao da matriz atraves da String
     * @param str   String representante do estado
     * @return  Dimensao da matriz
     */
    public int dimention(String str){
        int n = str.length();
        if(n == 1){
            return 1;
        }
        if(n == 4){
            return 2;
        }
        if(n == 9){
            return 3;
        }
        if(n == 16){
            return 4;
        }
        if(n == 25){
            return 5;
        }
        if(n == 36){
            return 6;
        }
        if(n == 49){
            return 7;
        }
        return 0;
    }

    /**
     * Metodo que retorna o estado em forma de matrix 3x3
     * @return  Matriz 3x3 do estado atual
     */
    public String toString() {
        String str = s;
        return str.substring(0, 3)+"\n"+
                str.substring(3, 6)+"\n"+
                str.substring(6, 9)+"\n";
    }

    /**
     * Metodo que gera todos os nos sucessores deste estado
     * @return  Lista de todos os nos sucessores deste estado
     */
    @Override
    public List<Ilayout> children(){
        List<Ilayout> childs = new ArrayList<>();
        int size = s.length();
        for (int i = 0; i < size-1; i++){
            for (int j = (i+1); j < size; j++){
                    char temp_arr[] = s.toCharArray();
                    char value = temp_arr[i];
                    char swap = temp_arr[j];
                    temp_arr[i] = swap;
                    temp_arr[j] = value;
                    int cost = setValue(value, swap);
                    Board temp = new Board(new String(temp_arr), cost, objective);
                    childs.add(temp);

            }
        }
        return childs;
    }


    /**
     * Metodo que verifica se um determinado estado e a solucao
     * @param l Estado a verificar
     * @return  true se for a solucao, false se nao for
     */
    @Override
    public boolean isGoal(Ilayout l) {
        return this.equals(l);
    }

    /**
     * Metodo que compara dois objetos
     * @param that  Objeto a comparar
     * @return  true se forem iguais, false se não forem iguais
     */
    @Override
    public boolean equals(Object that) {
        if (that instanceof Board) {
            Board b = ((Board) that);
            return s.equals(b.s);
        } else
            return false;
    }

    /**
     * Metodo que calcula o hashcode
     * @return  Hashcode do estado
     */
    @Override
    public int hashCode() {
        return 17 * Arrays.deepHashCode(this.board);
    }

    /**
     * Metodo que verifica se um caracter e um numero par ou letra correspondente a numero par
     * @param c Caracter a verificar
     * @return  true se for par, false se não for par
     */
    public boolean isEven(char c){
        switch (c){
            case '0':
            case '2':
            case '4':
            case '6':
            case '8':
            case 'A':
            case 'C':
            case 'E':
            case 'G':
            case 'I':
            case 'K':
            case 'M':
            case 'O':
            case 'Q':
            case 'S':
            case 'U':
            case 'W':
            case 'Y':
                return true;
        }
        return false;


    }

    /**
     * Metodo para definir o estado objetivo
     * @param str   Estado objetivo
     */
    public void setGoal(String str) {
        int position = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                this.map_goal.put(Character.getNumericValue(str.charAt(position)), new Point(i,j));
                this.goal[i][j] = str.charAt(position++);
            }
        }

    }

    /**
     * Metodo que devolve o custo de uma troca
     * @param c1  Primeiro caracter a ser trocado
     * @param c2  Segundo caracter a ser trocado
     * @return  Custo da troca
     */
    public int setValue(char c1, char c2){
        if(isEven(c1) && isEven(c2)){
            return 20;
        }
        if((isEven(c1) && !isEven(c2)) || (!isEven(c1) && isEven(c2))){
            return 5;
        }
        if(!isEven(c1) && !isEven(c2)){
            return 1;
        }
        return 0;
    }

    /**
     * Metodo que devolve o custo do estado atual
     * @return  Custo do estado atual
     */
    @Override
    public double getG() {
        return g;
    }

    /**
     * Metodo que o HashMap dos pontos feitos com o objetivo
     * @return
     */
    public HashMap<Integer, Point> getMap() {
        return this.map_goal;
    }

    /**
     * Metodo que calcula a distancia de Manhattan
     * @return Valor da distancia de Manhattan para todos os pontos
     */
    public int manhattan() {
        HashMap<Integer, Point> map_goal = this.getMap();
        int result = 0;
        int s = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                s = this.board[i][j];
                if (s != 0) {
                    Point piece = map_goal.get(s);
                    result += (Math.abs(j - piece.getY()) + Math.abs(i - piece.getX()));
                }
            }
        }
        return result;
    }

    /**
     * Metodo que verifica quantos blocos estao fora de ordem quando comparados com o objetivo (Metodo nao utilizado, substituido por distancia de Hamming)
     * @return  Total de blocos fora de ordem
     */
    public int BlockDifferent() {
        int result = 0;
        int s = 0;
        int g = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                s = this.board[i][j];
                g = this.goal[i][j];
                if (s != g) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * Metodo que calcula a ditancia de Hamming
     * @return  Total de blocos fora de ordem
     */
    public int Hamming(){
        int i = 0;
        int result = 0;
        while(i < s.length()){
            if(s.charAt(i) != objective.charAt(i)){
                result++;
            }
            i++;
        }
        return result;
    }

    /**
     * Metodo de devolve a heuristica com o mairo valor
     * @return  Maior valor calculado das heuristicas
     */
    public int getMaxHeu()
    {
        int x = this.manhattan();
        int k = this.BlockDifferent();
        int z = this.Hamming();
        if (x >= z)
            return  x;
        else
            return z;
    }

    /**
     * Metodo que devolve o valor calculado das heuristicas para o estado atual
     * @return  Valor calculado das heuristicas
     */
    @Override
    public int getH() {
        //return this.getMaxHeu();
        return this.Hamming();
    }}


