/*
  Copyright 2018 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/
package ec.co.ant;

import ec.EvolutionState;
import ec.co.ConstructiveIndividual;
import ec.co.ConstructiveProblemForm;
import ec.util.Misc;
import ec.util.Parameter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A construction rule that ignores pheromones and selects the best local move 
 * at each step.
 * 
 * @author Eric O. Scott
 */
public class GreedyConstructionRule implements ConstructionRule
{
    public final static String P_MINIMIZE = "minimize";
    private boolean minimize;
    
    public boolean isMinimize()
    {
        return minimize;
    }
    
    @Override
    public void setup(final EvolutionState state, final Parameter base)
    {
        assert(state != null);
        assert(base != null);
        minimize = state.parameters.getBoolean(base.push(P_MINIMIZE), null, true);
        assert(repOK());
    }

    /** Constructs a solution by greedily adding the lowest-cost component at 
     * each step until a complete solution is formed.  The pheromone matrix
     * argument is ignored, and may be null.
     */
    @Override
    public ConstructiveIndividual constructSolution(final EvolutionState state, final ConstructiveIndividual ind, final List<Double> pheromones)
    {
        assert(state != null);
        assert(state.evaluator.p_problem instanceof ConstructiveProblemForm);
        
        final ConstructiveProblemForm problem = (ConstructiveProblemForm) state.evaluator.p_problem;
        
        assert(problem != null);
        
        // Prepare data structures
        final Set<Integer> solution = new HashSet<Integer>();
        final Collection<Integer> unusedComponents = problem.componentSet();
        
        // Constructively build a new individual
        while (!unusedComponents.isEmpty())
            {
            final int component = bestMove(problem, unusedComponents);
            solution.add(component);
            unusedComponents.remove(component);
            }
        
        ind.setComponents(solution);
        assert(repOK());
        return ind;
    }
    
    /** Greedily select the next move. */
    private int bestMove(final ConstructiveProblemForm problem, final Collection<Integer> allowedMoves)
    {
        assert(problem != null);
        assert(allowedMoves != null);
        assert(!allowedMoves.isEmpty());
        assert(!Misc.containsNulls(allowedMoves));
        
        double bestCost = minimize ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        int best = -1;
        for (final int move : allowedMoves)
            {
            final double cost = problem.cost(move);
            if (minimize ? cost <= bestCost : cost >= bestCost)
                {
                bestCost = cost;
                best = move;
                }
            }
        assert(best >= 0);
        return best;
    }
    
    /** Representation invariant, used for verification.
     * 
     * @return true if the class is found to be in an erroneous state.
     */
    public final boolean repOK()
    {
        return P_MINIMIZE != null
                && !P_MINIMIZE.isEmpty();
    }
}