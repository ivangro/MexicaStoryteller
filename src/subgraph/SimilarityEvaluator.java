package subgraph;

import edu.uci.ics.jung.graph.Graph;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mexica.core.EmotionType;

/**
 * Class to determine the similary between two graphs
 * @author Ivan Guerrero
 */
public class SimilarityEvaluator {
    private static Pattern emotion = Pattern.compile("\\s*(\\d)\\s*\\((-?\\d)\\)\\s*");
    /** Allows the correct label comparison */
    private boolean isInverted;
    
    /**
     * Compares the two graphs obtaining its similarity. 
     * The method analyzes if the graph with the less number of edges is included in the other graph.
     * @param graph1
     * @param graph2
     * @return 
     */
    public Solution evaluate(Graph<INode, IEdge> graph1, Graph<INode, IEdge> graph2) {
        if (graph1.getEdgeCount() > graph2.getEdgeCount()) {
            isInverted = false;
            return evaluateGraphs(graph2, graph1);
        }
        else {
            isInverted = true;
            Solution solution = evaluateGraphs(graph1, graph2);
            if (solution != null)
                solution.invertMapping();
            return solution;
        }
    }
    
    /**
     * Compares the percentage of elements in the graph1 included in the graph2
     * @param graph1 The graph with the largest number of edges
     * @param graph2 The graph with the smallest number of edges
     * @return 
     */
    public Solution evaluateGraphs(Graph<INode, IEdge> graph1, Graph<INode, IEdge> graph2) {
        Map<Integer,List<Solution>> solutionBundle = new HashMap<>();
        List<Solution> solutions = new ArrayList<>();
        Solution solution = new Solution();
        solution.setRemainingGraph(graph2);
        solution.setNoUnmatchedEdges(graph1.getEdgeCount());
        solution.setNoEdges(graph2.getEdgeCount());
        solution.setTotalEdges(graph1.getEdgeCount() + graph2.getEdgeCount());
        solutions.add(solution);
        int level = 0;
        solutionBundle.put(level, solutions);
        
        for (IEdge ei : graph1.getEdges()) {
            solutions = solutionBundle.get(level);
            List<Solution> newSolutions = new ArrayList<>();
            
            for (Solution s : solutions) {
                Collection<IEdge> sei = getEdgesWithLabel(ei, s.getRemainingGraph());
                for (IEdge ej : sei) {
                    INode mapSource = s.getMapping(ei.getSource());
                    INode mapTarget = s.getMapping(ei.getTarget());
                    boolean unmappedSource = isUnmapped(s, ej.getSource());
                    boolean unmappedTarget = isUnmapped(s, ej.getTarget());
                    if (((mapSource == null && unmappedSource) || (mapSource != null && mapSource.equals(ej.getSource()))) &&
                        ((mapTarget == null && unmappedTarget) || (mapTarget != null && mapTarget.equals(ej.getTarget())))) {
                        Solution newSolution = (Solution)s.clone();
                        newSolution.setNoEdges(graph2.getEdgeCount());
                        if (newSolution.getMapping(ei.getSource()) == null)
                            newSolution.addMapping(ei.getSource(), ej.getSource());
                        if (newSolution.getMapping(ei.getTarget()) == null)
                            newSolution.addMapping(ei.getTarget(), ej.getTarget());
                        newSolution.removeEdge(ej);
                        newSolutions.add(newSolution);
                    }
                }
            }
            //TODO: Prevents from adding an empty level when no mappings were found, and preserves the last working set of solutions
            if (!newSolutions.isEmpty()) {
                level++;
                solutionBundle.put(level, newSolutions);
            }
        }
        
        Solution maxSimilarity = calculateMaxSimilarity(solutionBundle);
        return maxSimilarity;
    }
    
    private Collection<IEdge> getEdgesWithLabel(IEdge e, Graph<INode, IEdge> graph) {
        Collection<IEdge> edges = new ArrayList<>();
        for (IEdge edge : graph.getEdges()) {
            if (compareLabels(e.getLabel(), edge.getLabel()))
                edges.add(edge);
        }
        return edges;
    }

    private Solution calculateMaxSimilarity(Map<Integer, List<Solution>> solutionBundle) {
        double maxSimilarity = 0;
        Solution maxSolution = null;
        
        for (Integer key : solutionBundle.keySet()) {
            for (Solution sol : solutionBundle.get(key)) {
                if (sol.getSimilarity() > maxSimilarity) {
                    maxSimilarity = sol.getSimilarity();
                    maxSolution = sol;
                }
            }
        }
        
        return maxSolution;
    }

    /**
     * Compare the emotions represented on every label
     * @param label1
     * @param label2
     * @return True if the emotion on the first label can replace the emotion on the second label
     */
    private boolean compareLabels(String label1, String label2) {
        Matcher m = emotion.matcher(label1);
        Matcher m2 = emotion.matcher(label2);
        //If both are emotions, compare its intensities
        if (m.matches() && m2.matches()) {
            try {
                int type1 = Integer.parseInt(m.group(1));
                int type2 = Integer.parseInt(m2.group(1));
                if ((type1 == type2) || 
                    (type1 == EmotionType.getEmotionValue(EmotionType.getAnyEmotion())) || 
                    (type2 == EmotionType.getEmotionValue(EmotionType.getAnyEmotion()))) {
                    int intensity1 = Integer.parseInt(m.group(2));
                    int intensity2 = Integer.parseInt(m2.group(2));
                    //If both have the same sign, the second label can be equal or higher
                    if (intensity1 > 0 && intensity2 > 0) {
                        if (isInverted)
                            return (Math.abs(intensity1) <= Math.abs(intensity2));
                        else
                            return (Math.abs(intensity1) >= Math.abs(intensity2));
                    }
                    else if (intensity1 < 0 && intensity2 < 0) {
                        if (isInverted)
                            return (Math.abs(intensity1) >= Math.abs(intensity2));
                        else
                            return (Math.abs(intensity1) <= Math.abs(intensity2));
                    }
                    else
                        return false;
                }
                else
                    return false;
            } catch (Exception e) {
                return false;
            }
        }
        else
            return label1.equalsIgnoreCase(label2);
    }

    /**
     * Verifies that the given node is unmapped inside the solution, so it can be mapped
     * @param solution
     * @param source
     * @return True if the node is unmapped inside the solution
     */
    private boolean isUnmapped(Solution solution, INode node) {
        for  (INode mapped : solution.getMappingValues()) {
            if (mapped.equals(node))
                return false;
        }
        return true;
    }
}