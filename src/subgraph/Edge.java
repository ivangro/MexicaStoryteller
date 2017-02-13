package subgraph;

/**
 *
 * @author Ivan Guerrero
 */
public class Edge implements IEdge {
    private INode source, target;
    private String label;
    
    public Edge() {}
    
    public Edge(INode source, INode target, String label) {
        this.source = source;
        this.target = target;
        this.label = label;
    }
    
    /**
     * @return the source
     */
    @Override
    public INode getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    @Override
    public void setSource(INode source) {
        this.source = source;
    }

    /**
     * @return the target
     */
    @Override
    public INode getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    @Override
    public void setTarget(INode target) {
        this.target = target;
    }

    /**
     * @return the label
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    @Override
    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return source.toString() + "-" + label + "-" + target.toString();
    }
}
