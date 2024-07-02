package planning;

import fr.uga.pddl4j.heuristics.state.RelaxedGraphHeuristic;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.util.BitVector;

public class PlanningHeuristic extends RelaxedGraphHeuristic {

    private BitVector ist;

    public PlanningHeuristic(Problem problem) {
        super(problem);
        super.setAdmissible(false);
        this.ist = problem.getGoal().getPositiveFluents();
    }

    public int estimate(State state, Condition goal) {
        if(state.satisfy(goal))
            return 0;
        super.setGoal(goal);
        super.expandRelaxedPlanningGraph(state);
        return super.isGoalReachable() ? super.getRelaxedPlanValue() + ist.cardinality() - state.getIntersection(ist).cardinality() : Integer.MAX_VALUE;

    }


    public double estimate(Node node, Condition goal) {
        return (double)this.estimate((State)node, goal);
    }

}

