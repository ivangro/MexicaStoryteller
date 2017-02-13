package subgraph;

import edu.uci.ics.jung.graph.*;
import java.text.DecimalFormat;

/**
 * Class to represent the result of comparing two graphs
 * @author Ivan Guerrero
 */
public class Solution implements Cloneable {
    private Graph<INode, IEdge> remainingGraph;
    private Mapping<INode> vertexMapping;
    private int noEdges, noRemovedEdges, noUnmatchedEdges, totalEdges;
    /** Employed when the order of the compared graphs is changed */
    private boolean isInverted;
    private double socialSimilarity;
    private DecimalFormat format = new DecimalFormat("0.00");
    
    public Solution() {
        vertexMapping = new Mapping<>();
        noEdges = 0;
        noRemovedEdges = 0;
    }
    
    public void setRemainingGraph(Graph<INode, IEdge> graph) {
        remainingGraph = new SparseMultigraph<>();
        for (INode n : graph.getVertices()) {
            remainingGraph.addVertex(n);
        }
        
        for (IEdge e : graph.getEdges()) {
            remainingGraph.addEdge(e, e.getSource(), e.getTarget());
        }
    }
    
    public Graph<INode, IEdge> getRemainingGraph() {
        return remainingGraph;
    }
    
    public INode getMapping(INode vertex) {
        return vertexMapping.getMapping(vertex);
    }
    
    public INode getInvertedMapping(INode vertex) {
        return vertexMapping.getInvertedMapping(vertex);
    }
    
    public Iterable<INode> getMappingKeys() {
        return vertexMapping.getKeys();
    }
    
    public Iterable<INode> getMappingValues() {
        return vertexMapping.getValues();
    }
    
    public boolean addMapping(INode source, INode target) {
        if (vertexMapping.getMapping(source) == null)
            vertexMapping.addMapping(source, target);
        else 
            return false;
        
        return true;
    }
    
    @Override
    public Object clone() {
        Solution s = new Solution();
        s.setRemainingGraph(remainingGraph);
        s.noEdges = noEdges;
        s.noRemovedEdges = getNoRemovedEdges();
        s.noUnmatchedEdges = noUnmatchedEdges;
        s.isInverted = isInverted;
        s.totalEdges = totalEdges;
        for (INode n : vertexMapping.getKeys()) {
            s.addMapping(n, vertexMapping.getMapping(n));
        }
        
        return s;
    }

    public void removeEdge(IEdge ej) {
        remainingGraph.removeEdge(ej);
        noRemovedEdges++;
        noUnmatchedEdges--;
    }
    
    /**
     * Obtains the Jaccard similarity of the two sets<br>
     * Similarity = A&B / A|B
     * @return Jaccard similarity
     */
    public double getSimilarity() {
        if (totalEdges - noRemovedEdges > 0)
            return noRemovedEdges * 100.0 / (totalEdges - noRemovedEdges);
        else
            return 100;
        //return (double)getNoRemovedEdges() / (noEdges + noUnmatchedEdges) * 100.0;
    }
    
    public String getSimilarityAsString() {
        return format.format(getSimilarity());
    }
    
    public void setNoEdges(int noEdges) {
        this.noEdges = noEdges;
    }
    
    @Override
    public String toString() {
        return "Graph: " + remainingGraph.toString() + 
                "\nMap: " + vertexMapping.toString() + 
                "\nSimilarity: " + format.format(getSimilarity());
    }

    public void invertMapping() {
        Mapping<INode> newMapping = new Mapping<>();
        for (INode node : vertexMapping.getKeys()) {
            newMapping.addMapping(vertexMapping.getMapping(node), node);
        }
        
        vertexMapping = newMapping;
        isInverted = true;
    }

    /**
     * @return the noUnmatchedEdges
     */
    public int getNoUnmatchedEdges() {
        return noUnmatchedEdges;
    }

    /**
     * @param noUnmatchedEdges the noUnmatchedEdges to set
     */
    public void setNoUnmatchedEdges(int noUnmatchedEdges) {
        this.noUnmatchedEdges = noUnmatchedEdges;
    }

    /**
     * @return the noRemovedEdges
     */
    public int getNoRemovedEdges() {
        return noRemovedEdges;
    }

    public void setSocialSimilarity(double similarity) {
        socialSimilarity = similarity;
    }
    
    public double getSocialSimilarity() {
        return socialSimilarity;
    }
    
    public int getTotalEdges() {
        return totalEdges;
    }
    
    public void setTotalEdges(int totalEdges) {
        this.totalEdges = totalEdges;
    }
}