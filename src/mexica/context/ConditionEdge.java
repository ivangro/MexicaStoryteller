package mexica.context;

import mexica.core.EmotionType;
import mexica.core.TensionType;
import mexica.core.Condition;
import mexica.social.ConditionStatus;
import subgraph.IEdge;
import subgraph.INode;

/**
 *
 * @author Ivan Guerrero
 */
public class ConditionEdge implements IEdge{
    private Condition condition;
    private INode source, target;
    private String label;
    
    public ConditionEdge(Condition condition, INode source, INode target) {
        setCondition(condition);
        this.source = source;
        this.target = target;
    }
    
    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public INode getSource() {
        return source;
    }

    @Override
    public INode getTarget() {
        return target;
    }

    @Override
    public void setLabel(String label) {
        throw new Error("Invalid function, label comes from the linked condition");
    }

    @Override
    public void setSource(INode source) {
        this.source = source;
    }

    @Override
    public void setTarget(INode target) {
        this.target = target;
    }

    /**
     * @return the condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public final void setCondition(Condition condition) {
        this.condition = condition;
        label = ConditionStatus.obtainAbbreviation(condition.getStatus());
        switch (condition.getConditionType()) {
            case Emotion: 
                label += EmotionType.getEmotionValue(condition.getEmotion()) + " (" + condition.getIntensityAsString() + ")";
                break;
            case Tension: 
                label += TensionType.getAbbreviation(condition.getTension());
                break;
            default:
                label = "UNDEF";
        }
    }
    
    @Override
    public String toString() {
        return source + " " + label + " " + target;
    }
}