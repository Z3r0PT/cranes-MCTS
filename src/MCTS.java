import java.util.*;

public class MCTS {
    /**
     * Classe State
     */
    static class State {
        private Ilayout layout; //layout do estado atual
        private State father;   //estado pai
        private double g;   //custo do estado atual
        private int wins;   //numero de solucoes encontradas com simulacoes
        private int visits; //numero de visitas do estado
        private ArrayList<State> childs = new ArrayList<>();    //lista de filhos deste estado
        private double sim; // valor sim associado ao estado
        private int nSimulations = 20;  //numero de simulacoes

        /**
         * Construtor da classe State
         *
         * @param l Layout do estado atual
         * @param n State pai do layout atual
         */
        public State(Ilayout l, State n) {
            layout = l;
            father = n;
            if (father != null)
                g = father.g + l.getG();
            else g = 0.0;
            //sim = nSimulations + l.getG();
            sim = nSimulations;
        }

        /**
         * Metodo que retorna o layout do estado atual
         * @return Layout do estado atual
         */
        public Ilayout getState() {
            return this.layout;
        }

        /**
         * Metodo que permite ao estado atual expandir os seus filhos
         */
        public void expansion() {
            List<Ilayout> children = this.layout.children();
            childs = new ArrayList<>();
            for (Ilayout i : children) {
                childs.add(new State(i, this));
            }
        }

        /**
         * Metodo que retorna todos os filhos do estado atual
         * @return  Lista de todos os filhos do estado atual
         */
        public List<State> getChilds() {
            return this.childs;
        }

        /**
         * Metodo que devolve o State atual em forma de matriz
         *
         * @return Matriz do estado atual
         */
        public String toString() {
            return layout.toString();
        }

        /**
         * Metodo que devolve o custo do estado atual
         *
         * @return Custo do estado atual
         */
        public double getG() {
            return g;
        }

        /**
         * Metodo que retorna o pai do State atual
         *
         * @return Pai do State atual
         */
        public State getFather() {
            return this.father;
        }

        /**
         * Metodo que retorna o total de solucoes obtidas neste estado atraves de simulacao
         * @return  Total de solucoes encontradas atraves de simulacao
         */
        public int getWins() {
            return wins;
        }

        /**
         * Metodo que retorna o total de visitas ao estado atual
         * @return  Total de visitas ao estado atual
         */
        public int getVisits() {
            return visits;
        }

        /**
         * Metodo que permite adicionar visitas ao estado atual
         */
        public void addVisits() {
            this.visits++;
        }

        /**
         * metodo que permite adicionar solucoes ao numero de solucoes
         * @param wins  Numero total de solucoes a adicionar ao estado atual
         */
        public void setWins(int wins) {
            this.wins += wins;
        }

        /**
         * Metodo que retorna um filho do estado atual escolhido de forma aleatoria
         * @return  Filho do estado atual escolhido de forma aleatoria
         */
        public State getRandomChild() {
            int n = this.childs.size();
            int selectRandom = (int) (Math.random() * n);
            return this.childs.get(selectRandom);
        }

        /**
         * Metodo que retorna o valor de UCT do estado atual
         * @return
         */
        public double getUCT() {
            return UCT(this.g);
        }

        /**
         * Metodo que permite calcular o UCT, aplicando a formula
         * @param g Custo do no
         * @return
         */
        public double UCT(double g) {
            return g + this.sim;
        }

        /**
         * Metodo que retorna o melhor filho do estado atual,comparados atraves de UCT
         * @return  Melhor filho do estado atual
         */
        public State getBestChild() {
            List<State> stateList = this.childs;
            return Collections.min(stateList, Comparator.comparing(State::getUCT));
        }

        /**
         * Metodo que permite atualizar o valor de sim
         * @param v Valor a introduzir em sim
         */
        public void setSim(double v) {
            sim += v;
        }
    }

    /**
     * Metodo que gera todos os sucessores de um estado
     *
     * @param n Estado
     * @return Lista de sucessores do estado
     */
    final private List<State> sucessores(State n) {
        List<State> sucs = new ArrayList<>();
        List<Ilayout> children = n.layout.children();
        for (Ilayout e : children) {
            if (n.father == null || !e.equals(n.father.layout)) {
                State nn = new State(e, n);
                sucs.add(nn);
            }
        }
        return sucs;
    }

    /**
     * Metodo que executa a fase de simulacao do algoritmo MCTS
     * @param state Estado a executar simulacoes
     * @param goal  Estado objetivo
     * @param iterations    Total de iteracoes a ser executado por cada simulacao
     * @param simulations   Total de simulacoes a executar
     * @return  Total de solucoes encontradas atraves de simulacao
     */
    public int simulation(State state, Ilayout goal, int iterations, int simulations){
        int victories = 0;
        int simulation = 0;
        while (simulation < simulations){
            victories += search(state, goal, iterations);
            simulation++;
        }
        return victories;
    }

    /**
     * Metodo que faz uma busca por solucoes escolhendo nos de forma aleatoria
     * @param state Estado ao qual se ira iniciar a busca
     * @param goal  Estado objetivo
     * @param depth    Profundidade da busca
     * @return  1 se solucao for encontrada, 0 se nao for encontrada solucao
     */
    public int search(State state, Ilayout goal, int depth){
        int iteration = 0;
        State toExplore = state;
        while (iteration < depth){
            if(toExplore.layout.isGoal(goal)){
                return 1;
            }
            toExplore.expansion();
            toExplore = toExplore.getRandomChild();
            iteration++;
        }
        return 0;
    }

    /**
     * Metodo que aplica a fase de backpropagation do algoritmo MCTS
     * @param node  No que da inicia a fase
     */
    public void backpropagation(State node) {
        State temp = node;
        double v = node.sim + temp.layout.getG();
        while (temp.father != null) {
            temp.father.setSim(v);
            v = node.sim + temp.father.layout.getG();
            temp = temp.father;
        }
    }

    /**
     * Metodo que aplica a fase de selection do algritmo MCTS
     * @param rootNode  Raiz da arvore ou estado inicial
     * @return  Melhor no a ser explorado
     */
    private State selection(State rootNode) {
        State st = rootNode;
        while (st.getChilds() != null && st.getChilds().size() > 0) {
            st = findBestNodeWithUCT(st);
        }
        return st;
    }

    /**
     * Metodo que escolhe entre os filhos de um estado o que tem o melhor UCT
     * @param node  No no qual se ira escolher o melhor filho
     * @return  Melhor filho
     */
    static State findBestNodeWithUCT(State node) {
        return Collections.min(
                node.getChilds(),
                Comparator.comparing(State::getUCT));
    }

    /**
     * Metodo que executa o algoritmo Monte Carlo Tree Search
     * @param s Layout inicial
     * @param goal  Layout objetivo
     * @return Iterador do caminho de menor custo calculado pelo algoritmo
    */
    public Iterator<State> solveMCTS(Ilayout s, Ilayout goal) {
        State root = new State(s, null);
        int start = 0;
        int stop = 10000;
        while (start < stop) {
            State st = selection(root);
            st.expansion();
            for (State state : st.getChilds()) {
                if (state.layout.isGoal(goal)) {
                    return new IT_MCTS(state);
                }
                //state.setWins(simulation(state, goal, 10000, nSimulations));
                backpropagation(state);
            }
            start++;
        }
        return new IT_MCTS(selection(root));
    }

    /**
     * Classe iterador de MCTS
     */
    class IT_MCTS implements Iterator<MCTS.State> {
        private MCTS.State last;
        private Stack<MCTS.State> stack;

        /**
         * Construtor de classe IT_MCTS
         *
         * @param actual Estado de MCTS
         */
        public IT_MCTS(MCTS.State actual) {
            last = actual;
            stack = new Stack<MCTS.State>();
            while (last != null) {
                stack.push(last);
                last = last.getFather();
            }
        }

        /**
         * Metodo que verifica se a stack tem proximo valor
         *
         * @return true se tiver proximo valor, false caso contrario
         */
        public boolean hasNext() {
            return !stack.empty();
        }

        /**
         * Metodo que devolve o primeiro elemento da stack
         *
         * @return Primeiro elemento da stack
         */
        public MCTS.State next() {
            return stack.pop();
        }

        /**
         * Metodo que remove elementos da stack(Nao utilizado)
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

