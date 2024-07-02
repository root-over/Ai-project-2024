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
import fr.uga.pddl4j.problem.operator.Condition;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static fr.uga.pddl4j.planners.Statistics.byteToMByte;
import static fr.uga.pddl4j.planners.Statistics.millisecondToSecond;

public class MyASP extends AbstractPlanner {

    private double weight;

    private int timeout, exploredNodes;

    private long memoryUsed, searchingTime;

    private Problem problem;

    private planning.MyASP.Heuristic heuristicType;

    private planning.MyASP.Search searchType;

    public static long minutesToMilliseconds(int mins) {
        return (long) mins * 60 * 1000;
    }

    private long getCurrentlyUsedMemory() {
        System.gc();
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    public MyASP(double weight, int timeout, String pathDomain, String pathProblem, planning.MyASP.Search searchType, planning.MyASP.Heuristic heuristicType) {
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
        final Problem pb = new DefaultProblem(defaultParsedProblem);
        pb.instantiate();
        return pb;
    }



    @Override
    public Plan solve(Problem problem) throws ProblemNotSupportedException {
        return searchPlan(searchType, problem);
    }

    public enum Search{
        ANYTIME_ASTAR,
        PARALLEL_ANYTIME_ASTAR,
        WEIGHTED_ASTAR,
        ASTAR,
        ENFORCED_HILL_CLIMBING
    }

    public enum Heuristic{
        PLANNING_HEURISTIC,
        AJUSTED_SUM,
        AJUSTED_SUM2,
        AJUSTED_SUM2M,
        COMBO,
        MAX,
        FAST_FORWARD,
        SET_LEVEL,
        SUM,
        SUM_MUTEX;
    }

    private static StateHeuristic createHeuristic(planning.MyASP.Heuristic type, Problem problem){
        return switch (type) {
            case FAST_FORWARD -> new FastForward(problem);
            case SUM -> new Sum(problem);
            case SUM_MUTEX -> new SumMutex(problem);
            case AJUSTED_SUM -> new AdjustedSum(problem);
            case AJUSTED_SUM2 -> new AdjustedSum2(problem);
            case AJUSTED_SUM2M -> new AjustedSum2M(problem);
            case COMBO -> new Combo(problem);
            case MAX -> new Max(problem);
            case SET_LEVEL -> new SetLevel(problem);
            case PLANNING_HEURISTIC -> new PlanningHeuristic(problem);
            default -> null;
        };
    }

    private Plan searchPlan(planning.MyASP.Search type, Problem problem) throws ProblemNotSupportedException {
        switch (type) {
            case ASTAR -> {
                this.weight = 1;
                return this.weighted_astar(problem);
            }
            case WEIGHTED_ASTAR -> {
                return this.weighted_astar(problem);
            }
            case ANYTIME_ASTAR -> {
                return this.anytime_astar(problem);
            }
            case PARALLEL_ANYTIME_ASTAR -> {
                return this.parallel_anytime_astar(problem);
            }
            case ENFORCED_HILL_CLIMBING -> {
                return this.enforced_hillclimbing(problem);
            }
            default -> {
                return null;
            }
        }
    }

    public void solveProblem(){
        System.out.println("Actions: "+problem.getActions().size()+" Fluents: "+problem.getFluents().size());
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
            System.out.printf("Time spent for search: %8.2f seconds %n", millisecondToSecond(searchingTime));
            System.out.println("Explored nodes: "+this.exploredNodes);
            System.out.printf("Memory used for search: %8.2f MBytes %n", byteToMByte(memoryUsed));
        } catch (ProblemNotSupportedException e) {
            throw new RuntimeException("Problem is not supported.");
        }
    }

    @Override
    public boolean isSupported(Problem problem) {
        return (problem.getRequirements().contains(RequireKey.ACTION_COSTS)
                || problem.getRequirements().contains(RequireKey.CONSTRAINTS)
                || problem.getRequirements().contains(RequireKey.CONTINOUS_EFFECTS)
                || problem.getRequirements().contains(RequireKey.DERIVED_PREDICATES)
                || problem.getRequirements().contains(RequireKey.DURATIVE_ACTIONS)
                || problem.getRequirements().contains(RequireKey.DURATION_INEQUALITIES)
                || problem.getRequirements().contains(RequireKey.FLUENTS)
                || problem.getRequirements().contains(RequireKey.GOAL_UTILITIES)
                || problem.getRequirements().contains(RequireKey.METHOD_CONSTRAINTS)
                || problem.getRequirements().contains(RequireKey.NUMERIC_FLUENTS)
                || problem.getRequirements().contains(RequireKey.OBJECT_FLUENTS)
                || problem.getRequirements().contains(RequireKey.PREFERENCES)
                || problem.getRequirements().contains(RequireKey.TIMED_INITIAL_LITERALS)
                || problem.getRequirements().contains(RequireKey.HIERARCHY))
                ? false
                : true;
    }

    public Plan parallel_anytime_astar(Problem codedProblem) throws ProblemNotSupportedException {
        Objects.requireNonNull(codedProblem);
        if (!this.isSupported(codedProblem)) {
            throw new ProblemNotSupportedException("Problem not supported");
        }
        System.out.println("Start parallel anytime a-star search. Weight: "+this.weight+" Timeout: "+this.timeout);
        long begin = System.currentTimeMillis();
        long beforeUsedMem = getCurrentlyUsedMemory();
        Condition goal = codedProblem.getGoal();
        List<Action> actions = codedProblem.getActions();
        StateHeuristic heuristic = createHeuristic(heuristicType, codedProblem);
        ConcurrentHashMap<State, Node> closeSet = new ConcurrentHashMap<>();
        ConcurrentHashMap<State, Node> openSet = new ConcurrentHashMap<>();
        PriorityBlockingQueue<Node> open = new PriorityBlockingQueue<>(100, (n1, n2) -> {
            double f1 = weight * n1.getHeuristic() + n1.getCost();
            double f2 = weight  * n2.getHeuristic() + n2.getCost();
            return Double.compare(f1, f2);
        });
        State init = new State(codedProblem.getInitialState());
        Node root = new Node(init, (Node)null, -1, 0.0, heuristic.estimate(init, goal));
        open.add(root);
        openSet.put(init, root);
        Node solution = null;

        int nThread = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of threads: "+nThread);

        final ExecutorService executor = Executors.newFixedThreadPool(nThread) ;
        ExecutorCompletionService<Node> completionService = new ExecutorCompletionService<>(executor);

        for(int i = 0; i < nThread; i++){
            long timeout = minutesToMilliseconds(this.timeout) - (System.currentTimeMillis() - begin) - (5*1000L);
            completionService.submit(new planning.MyASP.AstarSearchThread(codedProblem, closeSet, openSet, open, timeout, heuristicType, actions, goal));
        }

        for(int i = 0; i < nThread; i++){
            try {
                Future<Node> f = completionService.take();
                Node s = f.get();
                if(s != null){
                    if(solution == null)
                        solution = s;
                    else if (s.getCost() < solution.getCost())
                        solution = s;
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        long end = System.currentTimeMillis();
        long afterUsedMem=getCurrentlyUsedMemory();
        this.memoryUsed = afterUsedMem-beforeUsedMem;
        this.searchingTime = end - begin;
        this.exploredNodes = closeSet.size();

        executor.shutdown();

        if (solution == null) {
            return null;
        } else {
            Node n = solution;

            SequentialPlan plan;
            for(plan = new SequentialPlan(); n.getParent() != null; n = n.getParent()) {
                Action op = (Action)actions.get(n.getAction());
                plan.add(0, op);
            }


            return plan;
        }

    }

    private static class AstarSearchThread implements Callable<Node> {
        private Problem codedProblem;
        private StateHeuristic heuristic;
        private ConcurrentHashMap<State, Node> closeSet;
        private ConcurrentHashMap<State, Node> openSet;
        private PriorityBlockingQueue<Node> open;
        private long timeout;

        private List<Action> actions;

        private Condition goal;

        public AstarSearchThread(Problem codedProblem, ConcurrentHashMap<State, Node> closeSet, ConcurrentHashMap<State, Node> openSet, PriorityBlockingQueue<Node> open, long timeout, planning.MyASP.Heuristic heuristicType, List<Action> actions, Condition goal) {
            this.codedProblem = codedProblem;
            this.closeSet = closeSet;
            this.openSet = openSet;
            this.open = open;
            this.timeout = timeout;
            this.actions = actions;
            this.goal = goal;
            this.heuristic = createHeuristic(heuristicType, codedProblem);
        }

        @Override
        public Node call() throws Exception {
            long begin = System.currentTimeMillis();
            long time;
            double bestCost = Integer.MAX_VALUE;
            Node solution = null;

            for(time = 0L; time < timeout; time = System.currentTimeMillis() - begin) {
                Node current = (Node)open.poll();
                if(current ==  null)
                    continue;
                openSet.remove(current);
                closeSet.put(current, current);
                if (current.satisfy(codedProblem.getGoal()) && current.getCost() < bestCost) {
                    solution = current;
                    bestCost = current.getCost();
                } else {
                    if(current.getCost() < bestCost){
                        int index = 0;
                        for(Iterator<Action> var19 = actions.iterator(); var19.hasNext(); ++index) {
                            Action op = (Action)var19.next();
                            if (op.isApplicable(current)) {
                                Node state = new Node(current);
                                op.getConditionalEffects().stream().filter((ce) -> {
                                    return current.satisfy(ce.getCondition());
                                }).forEach((ce) -> {
                                    state.apply(ce.getEffect());
                                });
                                double g = current.getCost() + op.getCost().getValue();
                                Node result = (Node)openSet.get(state);
                                if (result == null) {
                                    result = (Node)closeSet.get(state);
                                    if (result != null) {
                                        if (g < result.getCost()) {
                                            result.setCost(g);
                                            result.setParent(current);
                                            result.setAction(index);
                                            result.setDepth(current.getDepth() + 1);
                                            open.add(result);
                                            openSet.put(result, result);
                                            closeSet.remove(result);
                                        }
                                    } else {
                                        state.setCost(g);
                                        state.setParent(current);
                                        state.setAction(index);
                                        state.setHeuristic(heuristic.estimate(state, goal));
                                        state.setDepth(current.getDepth() + 1);
                                        open.add(state);
                                        openSet.put(state, state);
                                    }
                                } else if (g < result.getCost()) {
                                    result.setCost(g);
                                    result.setParent(current);
                                    result.setAction(index);
                                    result.setDepth(current.getDepth() + 1);
                                }
                            }
                        }
                    }
                }
            }
            return solution;
        }

    }

    public Plan anytime_astar(Problem codedProblem) throws ProblemNotSupportedException {
        Objects.requireNonNull(codedProblem);
        if (!this.isSupported(codedProblem)) {
            throw new ProblemNotSupportedException("Problem not supported");
        }
        System.out.println("Start anytime a-star search. Weight: "+this.weight+" Timeout: "+this.timeout);
        long begin = System.currentTimeMillis();
        long beforeUsedMem = getCurrentlyUsedMemory();
        StateHeuristic heuristic = createHeuristic(heuristicType, codedProblem);
        State init = new State(codedProblem.getInitialState());
        Map<State, Node> closeSet = new HashMap<>();
        Map<State, Node> openSet = new HashMap<>();
        PriorityQueue<Node> open = new PriorityQueue<>(100, (n1, n2) -> {
            double f1 = weight * n1.getHeuristic() + n1.getCost();
            double f2 = weight  * n2.getHeuristic() + n2.getCost();
            return Double.compare(f1, f2);
        });
        Node root = new Node(init, (Node)null, -1, 0.0, (double)heuristic.estimate(init, codedProblem.getGoal()));
        open.add(root);
        openSet.put(init, root);
        this.searchingTime = 0L;
        this.memoryUsed = 0L;
        this.exploredNodes = 0;
        Node solution = null;
        long timeout = minutesToMilliseconds(this.timeout);
        double bestCost = Integer.MAX_VALUE;

        long time;
        for(time = 0L; !open.isEmpty() && time < timeout; time = System.currentTimeMillis() - begin) {
            Node current = (Node)open.poll();
            openSet.remove(current);
            closeSet.put(current, current);
            if (current.satisfy(codedProblem.getGoal()) && current.getCost() < bestCost) {
                solution = current;
                bestCost = current.getCost();
            } else {
                if(current.getCost() < bestCost){
                    int index = 0;
                    for(Iterator<Action> var19 = codedProblem.getActions().iterator(); var19.hasNext(); ++index) {
                        Action op = (Action)var19.next();
                        if (op.isApplicable(current)) {
                            Node state = new Node(current);
                            op.getConditionalEffects().stream().filter((ce) -> {
                                return current.satisfy(ce.getCondition());
                            }).forEach((ce) -> {
                                state.apply(ce.getEffect());
                            });
                            double g = current.getCost() + op.getCost().getValue();
                            Node result = (Node)openSet.get(state);
                            if (result == null) {
                                result = (Node)closeSet.get(state);
                                if (result != null) {
                                    if (g < result.getCost()) {
                                        result.setCost(g);
                                        result.setParent(current);
                                        result.setAction(index);
                                        result.setDepth(current.getDepth() + 1);
                                        open.add(result);
                                        openSet.put(result, result);
                                        closeSet.remove(result);
                                    }
                                } else {
                                    state.setCost(g);
                                    state.setParent(current);
                                    state.setAction(index);
                                    state.setHeuristic(heuristic.estimate(state, codedProblem.getGoal()));
                                    state.setDepth(current.getDepth() + 1);
                                    open.add(state);
                                    openSet.put(state, state);
                                }
                            } else if (g < result.getCost()) {
                                result.setCost(g);
                                result.setParent(current);
                                result.setAction(index);
                                result.setDepth(current.getDepth() + 1);
                            }
                        }
                    }
                }
            }
        }


        long afterUsedMem=getCurrentlyUsedMemory();
        this.memoryUsed = afterUsedMem-beforeUsedMem;
        this.searchingTime = time;
        this.exploredNodes = closeSet.size();


        if (solution == null) {
            return null;
        } else {
            Node n = solution;

            SequentialPlan plan;
            for(plan = new SequentialPlan(); n.getParent() != null; n = n.getParent()) {
                Action op = (Action)codedProblem.getActions().get(n.getAction());
                plan.add(0, op);
            }

            return plan;

        }

    }

    public Plan weighted_astar(Problem codedProblem) throws ProblemNotSupportedException {
        Objects.requireNonNull(codedProblem);
        if (!this.isSupported(codedProblem)) {
            throw new ProblemNotSupportedException("Problem not supported");
        }
        if (this.weight == 1) {
            System.out.println("Start a-star search. Timeout: "+this.timeout);
        } else {
            System.out.println("Start weighted a-star search. Weight: " + this.weight + " Timeout: "+this.timeout);
        }
        long begin = System.currentTimeMillis();
        long beforeUsedMem = getCurrentlyUsedMemory();
        StateHeuristic heuristic = createHeuristic(heuristicType, codedProblem);
        State init = new State(codedProblem.getInitialState());
        Map<State, Node> closeSet = new HashMap<>();
        Map<State, Node> openSet = new HashMap<>();
        PriorityQueue<Node> open = new PriorityQueue<>(100, (n1, n2) -> {
            double f1 = weight * n1.getHeuristic() + n1.getCost();
            double f2 = weight  * n2.getHeuristic() + n2.getCost();
            return Double.compare(f1, f2);
        });
        Node root = new Node(init, (Node)null, -1, 0.0, (double)heuristic.estimate(init, codedProblem.getGoal()));
        open.add(root);
        openSet.put(init, root);
        Node solution = null;
        long timeout = minutesToMilliseconds(this.timeout);
        this.searchingTime = 0L;
        this.memoryUsed = 0L;
        this.exploredNodes = 0;

        long time;
        for(time = 0L; !open.isEmpty() && solution == null && time < timeout; time = System.currentTimeMillis() - begin) {
            Node current = (Node)open.poll();
            openSet.remove(current);
            closeSet.put(current, current);
            if (current.satisfy(codedProblem.getGoal())) {
                solution = current;
            } else {
                int index = 0;

                for(Iterator var19 = codedProblem.getActions().iterator(); var19.hasNext(); ++index) {
                    Action op = (Action)var19.next();
                    if (op.isApplicable(current)) {
                        Node state = new Node(current);
                        op.getConditionalEffects().stream().filter((ce) -> {
                            return current.satisfy(ce.getCondition());
                        }).forEach((ce) -> {
                            state.apply(ce.getEffect());
                        });
                        double g = current.getCost() + op.getCost().getValue();
                        Node result = (Node)openSet.get(state);
                        if (result == null) {
                            result = (Node)closeSet.get(state);
                            if (result != null) {
                                if (g < result.getCost()) {
                                    result.setCost(g);
                                    result.setParent(current);
                                    result.setAction(index);
                                    result.setDepth(current.getDepth() + 1);
                                    open.add(result);
                                    openSet.put(result, result);
                                    closeSet.remove(result);
                                }
                            } else {
                                state.setCost(g);
                                state.setParent(current);
                                state.setAction(index);
                                state.setHeuristic(heuristic.estimate(state, codedProblem.getGoal()));
                                state.setDepth(current.getDepth() + 1);
                                open.add(state);
                                openSet.put(state, state);
                            }
                        } else if (g < result.getCost()) {
                            result.setCost(g);
                            result.setParent(current);
                            result.setAction(index);
                            result.setDepth(current.getDepth() + 1);
                        }
                    }
                }
            }
        }

        long afterUsedMem=getCurrentlyUsedMemory();
        this.memoryUsed = afterUsedMem-beforeUsedMem;
        this.searchingTime = time;
        this.exploredNodes = closeSet.size();

        if (solution == null) {
            return null;
        } else {
            Node n = solution;

            SequentialPlan plan;
            for(plan = new SequentialPlan(); n.getParent() != null; n = n.getParent()) {
                Action op = (Action)codedProblem.getActions().get(n.getAction());
                plan.add(0, op);
            }

            return plan;

        }
    }

    public Plan enforced_hillclimbing(Problem codedProblem) throws ProblemNotSupportedException {
        Objects.requireNonNull(codedProblem);
        if (!this.isSupported(codedProblem)) {
            throw new ProblemNotSupportedException("Problem not supported");
        }
        long begin = System.currentTimeMillis();
        long beforeUsedMem = getCurrentlyUsedMemory();
        StateHeuristic heuristic = createHeuristic(heuristicType, codedProblem);
        LinkedList<Node> openList = new LinkedList<>();
        State init = new State(codedProblem.getInitialState());
        Node root = new Node(init, (Node)null, 0, 0.0, (double)heuristic.estimate(init, codedProblem.getGoal()));
        openList.add(root);
        double bestHeuristic = root.getHeuristic();
        Node solution = null;
        boolean deadEndFree = true;
        long timeout = minutesToMilliseconds(this.timeout);
        this.searchingTime = 0L;
        this.memoryUsed = 0L;
        this.exploredNodes = 0;

        long searchingTime;
        long end;
        for(searchingTime = 0L; !openList.isEmpty() && solution == null && deadEndFree && searchingTime < timeout; searchingTime = end - begin) {
            Node currentState = (Node)openList.pop();
            this.exploredNodes++;
            LinkedList<Node> successors = this.getSuccessors(currentState, codedProblem, heuristic);

            Node successor;
            for(deadEndFree = !successors.isEmpty(); !successors.isEmpty() && solution == null; openList.addLast(successor)) {
                successor = (Node)successors.pop();

                double heuristicSuccessor = successor.getHeuristic();
                if (heuristicSuccessor == 0.0) {
                    solution = successor;
                }

                if (heuristicSuccessor < bestHeuristic) {
                    successors.clear();
                    openList.clear();
                    bestHeuristic = heuristicSuccessor;
                }
            }

            end = System.currentTimeMillis();
        }

        long afterUsedMem=getCurrentlyUsedMemory();
        this.memoryUsed = afterUsedMem-beforeUsedMem;
        this.searchingTime = searchingTime;

        if (solution == null) {
            return null;
        } else {
            Node n = solution;

            SequentialPlan plan;
            for(plan = new SequentialPlan(); n.getParent() != null; n = n.getParent()) {
                Action op = (Action)codedProblem.getActions().get(n.getAction());
                plan.add(0, op);
            }

            return plan;

        }
    }


    private LinkedList<Node> getSuccessors(Node parent, Problem problem, StateHeuristic heuristic) {
        LinkedList<Node> successors = new LinkedList<>();
        int index = 0;

        for(Iterator<Action> var6 = problem.getActions().iterator(); var6.hasNext(); ++index) {
            Action op = (Action)var6.next();
            if (op.isApplicable(parent)) {
                State nextState = new State(parent);
                op.getConditionalEffects().stream().filter((ce) -> {
                    return parent.satisfy(ce.getCondition());
                }).forEach((ce) -> {
                    nextState.apply(ce.getEffect());
                });
                Node successor = new Node(nextState);
                successor.setCost(parent.getCost() + op.getCost().getValue());
                successor.setHeuristic((double)heuristic.estimate(nextState, problem.getGoal()));
                successor.setParent(parent);
                successor.setAction(index);
                successor.setDepth(parent.getDepth() + 1);
                successors.add(successor);
            }
        }

        return successors;
    }

    public static void main(String[] args) {

        Double weight = Double.parseDouble(args[0]);
        if(weight < 0)
            throw new RuntimeException("Weight must be >= 0.");
        Integer timeout = Integer.parseInt(args[1]);
        if(timeout <= 0)
            throw new RuntimeException("Timeout must be > 0.");
        String domain = args[2];
        String problem = args[3];
        planning.MyASP.Search searchType = planning.MyASP.Search.valueOf(args[4]);
        planning.MyASP.Heuristic heuristicType = planning.MyASP.Heuristic.valueOf(args[5]);

        final planning.MyASP planner = new planning.MyASP(weight, timeout, domain, problem, searchType, heuristicType);

        planner.solveProblem();
    }
}
