package subgraph;

/**
 *
 * @author Ivan Guerrero
 */
public interface IEdge {

    /**
     * @return the label
     */
    String getLabel();

    /**
     * @return the source
     */
    INode getSource();

    /**
     * @return the target
     */
    INode getTarget();

    /**
     * @param label the label to set
     */
    void setLabel(String label);

    /**
     * @param source the source to set
     */
    void setSource(INode source);

    /**
     * @param target the target to set
     */
    void setTarget(INode target);
    
}
