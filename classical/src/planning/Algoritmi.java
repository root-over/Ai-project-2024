package planning;

import fr.uga.pddl4j.heuristics.state.*;
import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.RequireKey;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.plan.SequentialPlan;
import fr.uga.pddl4j.planners.AbstractPlanner;
import fr.uga.pddl4j.planners.ProblemNotSupportedException;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;

import java.io.IOException;
import java.util.*;

import static fr.uga.pddl4j.planners.Statistics.byteToMByte;
import static fr.uga.pddl4j.planners.Statistics.millisecondToSecond;

public class Algoritmi extends AbstractPlanner {

    private double weight;
    private int timeout, exploredNodes;
    private long memoryUsed, searchingTime;
    private Problem problem;
    private Heuristic heuristicType;
    private Search searchType;

    public Algoritmi(double weight, int timeout, String pathDomain, String pathProblem, Search searchType, Heuristic heuristicType) {
        this.weight = weight;
        this.timeout = timeout;
        this.searchType = searchType;
        this.heuristicType = heuristicType;
        DefaultParsedProblem dpp = null;
        try {
            dpp = this.parse(pathDomain, pathProblem);
            this.problem = this.instantiate(dpp);
        } catch (IOException e) {
            throw new RuntimeException("Parsing problem.");
        }
    }

    @Override
    public Problem instantiate(DefaultParsedProblem defaultParsedProblem) {
        final Problem p = new DefaultProblem(defaultParsedProblem);
        p.instantiate();
        return p;
    }

    @Override
    public boolean isSupported(Problem p) {
        return (!p.getRequirements().contains(RequireKey.ACTION_COSTS)
                && !p.getRequirements().contains(RequireKey.CONSTRAINTS)
                && !p.getRequirements().contains(RequireKey.CONTINOUS_EFFECTS)
                && !p.getRequirements().contains(RequireKey.DERIVED_PREDICATES)
                && !p.getRequirements().contains(RequireKey.DURATIVE_ACTIONS)
                && !p.getRequirements().contains(RequireKey.DURATION_INEQUALITIES)
                && !p.getRequirements().contains(RequireKey.FLUENTS)
                && !p.getRequirements().contains(RequireKey.GOAL_UTILITIES)
                && !p.getRequirements().contains(RequireKey.HIERARCHY))
                && !p.getRequirements().contains(RequireKey.METHOD_CONSTRAINTS)
                && !p.getRequirements().contains(RequireKey.NUMERIC_FLUENTS)
                && !p.getRequirements().contains(RequireKey.OBJECT_FLUENTS)
                && !p.getRequirements().contains(RequireKey.PREFERENCES)
                && !p.getRequirements().contains(RequireKey.TIMED_INITIAL_LITERALS);
    }

    @Override
    public Plan solve(Problem p) throws ProblemNotSupportedException {
        return searchPlan(searchType, p);
    }

    public enum Search {
        WEIGHTED_ASTAR,
        ASTAR,
        ENFORCED_HILL_CLIMBING;
    }

    public enum Heuristic {
        HEURISTIC_EVALUATION,
        AJUSTED_SUM,
        FAST_FORWARD;
    }

    private static StateHeuristic createHeuristic(Heuristic type, Problem p) {
        return switch (type) {
            case FAST_FORWARD -> new FastForward(p);
            case AJUSTED_SUM -> new AdjustedSum(p);
            case HEURISTIC_EVALUATION -> new HeuristicEvaluation(p);
            default -> null;
        };
    }

    private Plan searchPlan(Search type, Problem p) throws ProblemNotSupportedException {
        switch (type) {
            case ASTAR -> {
                this.weight = 1;
                return this.weighted_astar(p);
            }
            case WEIGHTED_ASTAR -> {
                return this.weighted_astar(p);
            }
            case ENFORCED_HILL_CLIMBING -> {
                return this.enforced_hillclimbing(p);
            }
            default -> {
                return null;
            }
        }
    }

    public void solveProblem() {
        System.out.println("Actions: " + problem.getActions().size() + " Fluents: " + problem.getFluents().size());
        try {
            Plan plan = searchPlan(searchType, problem);
            StringBuilder strb = new StringBuilder();
            if (plan != null) {
                strb.append(String.format("%nFound plan as follows:%n%n"));
                strb.append(problem.toString(plan));
            } else {
                strb.append(String.format("%nNo plan found%n%n"));
            }
            System.out.println(strb);
            System.out.printf("Time spent for searching: %8.2f seconds %n", millisecondToSecond(searchingTime));
            System.out.println("Nodes Explored: " + this.exploredNodes);
            System.out.printf("Memory used for search: %8.2f MBytes %n", byteToMByte(memoryUsed));
        } catch (ProblemNotSupportedException e) {
            throw new RuntimeException("Problem not supported.");
        }
    }

    ////////////// ////////////// ////////////// UTILITY ////////////// ////////////// //////////////

    public static long minutesToMilliseconds(int mins) {
        return (long) mins * 60 * 1000;
    }

    private long getCurrentMemoryUsed() {
        System.gc();
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    private Node applyEffects(Node current, Action action) {
        Node state = new Node(current);
        action.getConditionalEffects()
                .stream()
                .filter(conditionalEffect -> current.satisfy(conditionalEffect.getCondition()))
                .forEach(conditionalEffect -> state.apply(conditionalEffect.getEffect()));
        return state;
    }

    private void logSearchStartAStar() {
        if (this.weight == 1) {
            System.out.println("Start a-star search. Timeout: " + this.timeout);
        } else {
            System.out.println("Start weighted a-star search. Weight: " + this.weight + " Timeout: " + this.timeout);
        }
    }

    public static PriorityQueue<Node> createPriorityQueue(double weight) {
        return new PriorityQueue<>(100, new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                double f1 = weight * n1.getHeuristic() + n1.getCost();
                double f2 = weight * n2.getHeuristic() + n2.getCost();
                return Double.compare(f1, f2);
            }
        });
    }

    private void validateProblem(Problem codedProblem) throws ProblemNotSupportedException {
        Objects.requireNonNull(codedProblem);
        if (!this.isSupported(codedProblem)) {
            throw new ProblemNotSupportedException("Problem not supported");
        }
    }

    ////////////// ////////////// ////////////// WEIGHTED ASTAR ////////////// ////////////// //////////////

    public Plan weighted_astar(Problem codedProblem) throws ProblemNotSupportedException {
        validateProblem(codedProblem);
        logSearchStartAStar();

        long startTime = System.currentTimeMillis();
        long initialMemory = getCurrentMemoryUsed();

        StateHeuristic heuristic = createHeuristic(heuristicType, codedProblem);
        State initState = new State(codedProblem.getInitialState());

        Map<State, Node> openSet = new HashMap<>();
        Map<State, Node> closedSet = new HashMap<>();

        PriorityQueue<Node> openQueue = createPriorityQueue(this.weight);
        Node rootNode = new Node(initState, (Node) null, -1, 0.0,(double) heuristic.estimate(initState, codedProblem.getGoal()));

        openQueue.add(rootNode);
        openSet.put(initState, rootNode);

        this.searchingTime = 0L;
        this.memoryUsed = 0L;
        this.exploredNodes = 0;
        Node goal = null;
        long timeout = minutesToMilliseconds(this.timeout);
        double bestCost = Integer.MAX_VALUE;
        long searchingTime;

        for (searchingTime = 0L; !openQueue.isEmpty() && searchingTime < timeout; searchingTime= System.currentTimeMillis() - startTime) {
            Node current = (Node) openQueue.poll();
            openSet.remove(current);
            closedSet.put(current, current);

            if (current.satisfy(codedProblem.getGoal())) {
                goal = current;
                break;
            }
            int index = 0;

            for (Iterator i = codedProblem.getActions().iterator(); i.hasNext(); index++) {
                Action action = (Action) i.next();
                if (action.isApplicable(current)) {
                    Node state = applyEffects(current, action);
                    double cost = current.getCost() + action.getCost().getValue();
                    Node result = (Node) openSet.get(state);
                    if (result == null) {
                        result = (Node) closedSet.get(state);
                        if (result != null) {
                            if (cost < result.getCost()) {
                                result.setCost(cost);
                                result.setParent(current);
                                result.setAction(index);
                                result.setDepth(current.getDepth() + 1);
                                openQueue.add(result);
                                openSet.put(result, result);
                                closedSet.remove(result);
                            }
                        } else {
                            state.setCost(cost);
                            state.setParent(current);
                            state.setAction(index);
                            state.setHeuristic(heuristic.estimate(state, codedProblem.getGoal()));
                            state.setDepth(current.getDepth() + 1);
                            openQueue.add(state);
                            openSet.put(state, state);
                        }
                    } else if (cost < result.getCost()) {
                        result.setCost(cost);
                        result.setParent(current);
                        result.setAction(index);
                        result.setDepth(current.getDepth() + 1);
                    }
                }
            }
        }
        long currentMemory = getCurrentMemoryUsed();
        this.memoryUsed = currentMemory - initialMemory;
        this.searchingTime = searchingTime;
        this.exploredNodes = closedSet.size();

        if (goal == null) { return null; }
        else {
            Node n = goal;
            SequentialPlan plan;
            for (plan = new SequentialPlan(); n.getParent() != null; n = n.getParent()) {
                Action action = (Action) codedProblem.getActions().get(n.getAction());
                plan.add(0, action);
            }
            return plan;
        }
    }

    ////////////// ////////////// ////////////// ENFORCED HILLCLIMBING ////////////// ////////////// //////////////

    public Plan enforced_hillclimbing(Problem codedProblem) throws ProblemNotSupportedException {
        validateProblem(codedProblem);

        long startTime = System.currentTimeMillis();
        long initialMemory = getCurrentMemoryUsed();

        StateHeuristic heuristic = createHeuristic(heuristicType, codedProblem);
        LinkedList<Node> list = new LinkedList<>();
        State initState = new State(codedProblem.getInitialState());
        Node rootNode = new Node(initState, (Node) null, 0, 0.0, (double) heuristic.estimate(initState,
                codedProblem.getGoal()));

        list.add(rootNode);
        double bestHeuristic = rootNode.getHeuristic();

        this.searchingTime = 0L;
        this.memoryUsed = 0L;
        this.exploredNodes = 0;
        Node goal = null;
        long timeout = minutesToMilliseconds(this.timeout);
        boolean deadEndFree = true;

        long searchingTime;
        long end;
        for (searchingTime = 0L; !list.isEmpty() && goal == null && deadEndFree
                && searchingTime < timeout; searchingTime = end - startTime) {

            Node current = (Node) list.pop();
            this.exploredNodes++;

            LinkedList<Node> successors = this.getSuccessors(current, codedProblem, heuristic);

            Node successor;
            for (deadEndFree = !successors.isEmpty(); !successors.isEmpty() && goal == null; list
                    .addLast(successor)) {
                successor = (Node) successors.pop();

                double heuristicSuccessor = successor.getHeuristic();
                if (heuristicSuccessor == 0.0) {
                    goal = successor;
                }

                if (heuristicSuccessor < bestHeuristic) {
                    successors.clear();
                    list.clear();
                    bestHeuristic = heuristicSuccessor;
                }
            }
            end = System.currentTimeMillis();
        }

        long currentMemory = getCurrentMemoryUsed();
        this.memoryUsed = currentMemory - initialMemory;
        this.searchingTime = searchingTime;
        if (goal == null) {
            return null;
        } else {
            Node n = goal;
            SequentialPlan plan;
            for (plan = new SequentialPlan(); n.getParent() != null; n = n.getParent()) {
                Action action = (Action) codedProblem.getActions().get(n.getAction());
                plan.add(0, action);
            }
            return plan;
        }
    }

    private LinkedList<Node> getSuccessors(Node parent, Problem problem, StateHeuristic heuristic) {
        LinkedList<Node> successors = new LinkedList<>();
        int index = 0;

        for (Iterator<Action> i = problem.getActions().iterator(); i.hasNext(); ++index) {
            Action action = (Action) i.next();
            if (action.isApplicable(parent)) {

                State nextState = new State(parent);
                action.getConditionalEffects()
                        .stream()
                        .filter((conditionalEffect) -> { return parent.satisfy(conditionalEffect.getCondition()); })
                        .forEach((conditionalEffect) -> { nextState.apply(conditionalEffect.getEffect()); });

                Node successor = new Node(nextState);
                successor.setCost(parent.getCost() + action.getCost().getValue());
                successor.setHeuristic((double) heuristic.estimate(nextState, problem.getGoal()));
                successor.setParent(parent);
                successor.setAction(index);
                successor.setDepth(parent.getDepth() + 1);
                successors.add(successor);
            }
        }
        return successors;
    }

    ////////////// ////////////// ////////////// MAIN ////////////// ////////////// //////////////

    public static void main(String[] args) {

        double weight = Double.parseDouble(args[0]);
        if (weight < 0)
            throw new RuntimeException("Weight must be >= 0.");
        int timeout = Integer.parseInt(args[1]);
        if (timeout <= 0)
            throw new RuntimeException("Timeout must be > 0.");

        String domain = args[2];
        String problem = args[3];

        Search searchType = Search.valueOf(args[4]);


        Heuristic heuristicType = Heuristic.valueOf(args[5]);

        final Algoritmi planner = new Algoritmi(weight, timeout, domain, problem, searchType, heuristicType);

        planner.solveProblem();
    }
}
