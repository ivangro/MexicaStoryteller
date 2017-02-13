package mexica.context;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.*;
import java.util.List;
import mexica.core.Condition;
import mexica.core.EmotionType;
import mexica.core.Tension;
import mexica.engagement.Atom;
import mexica.story.ConditionInstantiated;
import org.apache.commons.collections15.Transformer;
import subgraph.*;

/**
 * Class to generate a graphic context from an atom description
 * @author Ivan Guerrero
 */
public class ContextBuilder {
    public static BasicVisualizationServer<INode, IEdge> getVisualizationElement(List<ConditionInstantiated> context) {
        Layout<INode, IEdge> layout = new CircleLayout<>(generateGraphFromContext(context));
        layout.setSize(new Dimension(300,300));
        return getVisualizationElementFromLayout(layout);
    }
    
    public static BasicVisualizationServer<INode, IEdge> getVisualizationElement(Atom atom) {
        Layout<INode, IEdge> layout = new CircleLayout<>(generateGraphFromAtom(atom));
        layout.setSize(new Dimension(300,300));
        return getVisualizationElementFromLayout(layout);
    }
    
    private static BasicVisualizationServer<INode, IEdge> getVisualizationElementFromLayout(Layout<INode, IEdge> layout) {
        BasicVisualizationServer<INode, IEdge> vv;
        vv = new BasicVisualizationServer<>(layout);
        vv.setPreferredSize(new Dimension(300,300));
        vv.getRenderContext().setVertexLabelTransformer(new Transformer<INode, String>() {
            @Override
            public String transform(INode n) {
                return n.getID();
            }            
        });
        vv.getRenderContext().setEdgeLabelTransformer(new Transformer<IEdge, String>() {
            @Override
            public String transform(IEdge e) {
                return e.getLabel();
            }
        });
        float dash[] = {10.0f};
        final Stroke tensionStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        final Stroke emotionStroke = new BasicStroke();
        
        vv.getRenderContext().setEdgeStrokeTransformer(new Transformer<IEdge, Stroke>() {
            @Override
            public Stroke transform(IEdge e) {
                if (e instanceof ConditionEdge) {
                    ConditionEdge ce = (ConditionEdge)e;
                    switch(ce.getCondition().getConditionType()) {
                        case Tension:
                            return tensionStroke;
                        case Emotion:
                            return emotionStroke;
                    }
                }
                return emotionStroke;
            }
        });
        
        vv.getRenderContext().setEdgeDrawPaintTransformer(new Transformer<IEdge, Paint>() {
            @Override
            public Paint transform(IEdge e) {
                if (e instanceof ConditionEdge) {
                    ConditionEdge ce = (ConditionEdge)e;
                    switch (ce.getCondition().getConditionType()) {
                        case Tension:
                            return Color.BLUE;
                        case Emotion:
                            if (ce.getCondition().getEmotion() == EmotionType.getSocialEmotion())
                                return Color.RED;
                            else
                                return Color.BLACK;
                    }
                }
                return Color.RED;
            }
        });
        
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        
        return vv;
    }
    
    public static Graph<INode, IEdge> generateGraphFromAtom(Atom atom) {
        Graph<INode, IEdge> graph = new DirectedSparseMultigraph<>();
        
        for (Tension t : atom.getTensions()) {
            Element source = new Element(t.getCharacterB());
            Element target = new Element(t.getCharacterA());
            ConditionEdge edge = new ConditionEdge(t, source, target);
            graph.addEdge(edge, source, target);
        }
        
        for (Condition e : atom.getEmotions()) {
            Element source = new Element(e.getCharacterA());
            Element target = new Element(e.getCharacterB());
            ConditionEdge edge = new ConditionEdge(e, source, target);
            graph.addEdge(edge, source, target);
        }
        
        return graph;
    }

    public static Graph<INode, IEdge> generateGraphFromContext(List<ConditionInstantiated> context) {
        Graph<INode, IEdge> graph = new DirectedSparseMultigraph<>();
        Element source, target;
        ConditionEdge edge;
        
        for (ConditionInstantiated cond : context) {
            switch (cond.getCondition().getConditionType()) {
                case Tension:
                    source = new Element(cond.getCharacterB());
                    target = new Element(cond.getCharacterA());
                    edge = new ConditionEdge(cond.getCondition(), source, target);
                    graph.addEdge(edge, source, target);
                    break;
                case Emotion:
                    source = new Element(cond.getCharacterA());
                    target = new Element(cond.getCharacterB());
                    edge = new ConditionEdge(cond.getCondition(), source, target);
                    graph.addEdge(edge, source, target);
                    break;
            }
        }
        return graph;
    }
}
