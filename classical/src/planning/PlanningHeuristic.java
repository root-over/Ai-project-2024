package planning;

import fr.uga.pddl4j.heuristics.state.RelaxedGraphHeuristic;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.util.BitVector;

/**
 * HeuristicEvaluation estende la RelaxedGraphHeuristic per fornire
 * una valutazione euristica specializzata per problemi di pianificazione.
 */
public class HeuristicEvaluation extends RelaxedGraphHeuristic {

    private BitVector goalFluents;

    /**
     * Il costruttore inizializza l'euristica con i goal fluents del problema.
     * @param problem Il problema di pianificazione.
     */
    public HeuristicEvaluation(Problem problem) {
        super(problem);
        this.setAdmissible(false);
        this.goalFluents = problem.getGoal().getPositiveFluents();
    }

    /**
     * Stima il costo per raggiungere l'obiettivo dallo stato attuale.
     * @param state Lo stato attuale.
     * @param goal La condizione di obiettivo.
     * @return Il costo stimato per raggiungere l'obiettivo.
     */
    public int estimate(State state, Condition goal) {
        if (state.satisfy(goal)) return 0;
        this.setGoal(goal);
        this.expandRelaxedPlanningGraph(state);
        int goalDistance = this.isGoalReachable() ? this.getRelaxedPlanValue() : Integer.MAX_VALUE;
        int heuristicValue = goalDistance + goalFluents.cardinality() - state.getIntersection(goalFluents).cardinality();
        return heuristicValue;
    }

    /**
     * Sovraccarica il metodo estimate per funzionare direttamente con un Nodo.
     * @param node Il nodo che rappresenta uno stato nello spazio di ricerca.
     * @param goal La condizione di obiettivo.
     * @return Il costo stimato per raggiungere l'obiettivo dallo stato del nodo.
     */
    public double estimate(Node node, Condition goal) {
        return (double) this.estimate((State) node, goal);
    }
}
