package slant;

import java.util.ArrayList;
import java.util.List;
import mexica.CharacterName;
import mexica.core.*;
import mexica.reflection.Guideline;
import mexica.reflection.Guidelines;
import mexica.story.ActionInstantiated;
import mexica.story.ConditionInstantiated;

/**
 * Decorator for the Guidelines class to implement slant guidelines
 * @author Ivan Guerrero
 */
public class SlantGuidelines extends Guidelines {
    private Guidelines guidelines;
    private List<SlantGuideline> slantGuidelines;
    
    public SlantGuidelines(Guidelines guidelines) {
        this.guidelines = guidelines;
        slantGuidelines = new ArrayList<>();
    }
    
    public void addSlantGuideline(CharacterName performer, TensionType tension, boolean prohibit) {
        slantGuidelines.add(new SlantGuideline(performer, tension, prohibit));
    }
    
    public void removeSlantGuidelines() {
        slantGuidelines.clear();
    }
    
    /**
     * Determines if the given action satisfies the guidelines suggested by Slant
     * @param action
     * @return 
     */
    public boolean satisfiesSlantGuidelines(ActionInstantiated action) {
        for (SlantGuideline guideline : slantGuidelines) {
            TensionType tension = guideline.getTension();
            for (ConditionInstantiated cond : action.getTensions()) {
                if (cond.getCondition().getTension() == tension &&
                    guideline.getPerformer() == cond.getCharacterA() && 
                    guideline.isProhibit()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /** Decorator */
    
    @Override
    public boolean addGuideline(Guideline guideline) {
        return guidelines.addGuideline(guideline);
    }
    
    @Override
    public boolean removeGuideline(Guideline guideline) {
        return guidelines.removeGuideline(guideline);
    }
    
    @Override
    public boolean containsGuideline(Guideline guideline) {
        return guidelines.containsGuideline(guideline);
    }
    
    @Override
    public boolean satisfiesGuidelines(Action action) {
        return guidelines.satisfiesGuidelines(action);
    }
    
    @Override
    public boolean satisfiesSocialGuidelines(ActionInstantiated action) {
        return guidelines.satisfiesSocialGuidelines(action);
    }
    
    @Override
    public boolean satisfiesSocialGuidelines(ActionInstantiated followingAction, ActionInstantiated action) {
        return guidelines.satisfiesSocialGuidelines(followingAction, action);
    }
    
    @Override
    public boolean satisfiesNoveltyGuidelines(Action action) {
        return guidelines.satisfiesNoveltyGuidelines(action);
    }
    
    @Override
    public boolean satisfiesTensionGuidelines(Action action) {
        return guidelines.satisfiesTensionGuidelines(action);
    }
    
    @Override
    public boolean canTerminate() {
        return guidelines.canTerminate();
    }
    
    @Override
    public void removeTensionGuidelines() {
        guidelines.removeTensionGuidelines();
    }
    
    @Override
    public void removeNoveltyGuidelines() {
        guidelines.removeNoveltyGuidelines();
    }
    
    @Override
    public void removeSocialNormGuidelines() {
        guidelines.removeSocialNormGuidelines();
    }
}