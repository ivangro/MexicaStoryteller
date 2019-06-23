package mexica.story.analyzer;

import java.util.*;
import mexica.CharacterName;
import mexica.story.*;
import mexica.core.*;
import mexica.tools.ComparisonResult;
import mexica.tools.ConditionComparer;

/**
 * Looks for resolved tensions and adds explanatory text to the final story
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class FinalStoryAnalyzer {
    /**
     * List of tensions solved of the type healthAtRisk, lifeAtRisk, prisioner
     */
    private List<TensionHistory> tensionsSolved;
    /**
     * List of special tensions triggered inside the story of the type
     * health at risk, life at risk, clasing emotions, love competition
     */
    private List<ConditionInstantiated> specialTensions;
    private List<SpecialAction> specialActions;
    private int healthCounter, lifeCounter, clashingCounter, loveCounter;
    
    public FinalStoryAnalyzer() {
        restart();
    }
    
    public final void restart() {
        tensionsSolved = new ArrayList<TensionHistory>();
        specialTensions = new ArrayList<ConditionInstantiated>();
        healthCounter = 0;
        lifeCounter = 0;
        clashingCounter = 0;
        loveCounter = 0;
    }
    
    public void setSpecialActions(List<SpecialAction> list) {
        this.specialActions = list;
    }
    
    public void addTensionSolved(ConditionInstantiated generator, ConditionInstantiated solution) {
        TensionHistory ten = new TensionHistory(generator, solution);
        if (!tensionsSolved.contains(ten)) {
            tensionsSolved.add(ten);
        }
    }
    
    public void addSpecialTension(ConditionInstantiated condition) {
        if (!specialTensions.contains(condition)) {
            specialTensions.add(condition);
        }
    }
    
    public List<TextInstantiated> generateFinalStory(Story story) {
        List<TextInstantiated> text = story.getStory();
        List<ActionInstantiated> actions = story.getActions();
        
        List<TextInstantiated> finalStory = new ArrayList<TextInstantiated>();
        int counter = 0;
        
        for (ActionInstantiated action : actions) {
            finalStory.add(text.get(counter));
            /* TODO: Implement code
            for (TensionHistory tension : tensionsSolved) {
                if (tension.getGenerator().getAction().equals(action)) {
                    ActionInstantiated ins = instantiateSpecialAction(
                            tension.getGenerator().getCondition().getTension(), 
                            tension.getHero(), tension.getVictim());
                    finalStory.add(ins.getTexts().get(0));
                    System.out.println("Tension solved: " + tension);
                }
            }
            for (ConditionInstantiated cond : specialTensions) {
                if (cond.getAction().equals(action) && !containsAsGenerator(cond)) {
                    ActionInstantiated ins = (cond.getCharacterB().equals(CharacterName.Not_Defined)) ?
                        instantiateSpecialAction(cond.getCondition().getTension(), cond.getCharacterA()) : 
                        instantiateSpecialAction(cond.getCondition().getTension(), cond.getCharacterA(), cond.getCharacterB());
                    finalStory.add(ins.getTexts().get(0));
                    System.out.println("Special tension: " + cond);
                }
            }
            */
            counter++;
        }
        
        return finalStory;
    }

    private ActionInstantiated instantiateSpecialAction(TensionType tensionType, CharacterName... names) {
        Action action = new Action();
        String actionName = "";
        switch (tensionType) {
            case HealthAtRisk:
                healthCounter++;
                actionName += "Health_Risk_" + ((names.length == 2) ? 1: 2);
                break;
            case LifeAtRisk:
                lifeCounter++;
                actionName += "Life_Risk_" + ((names.length == 2) ? 1: 2);
                break;
            case ClashingEmotions:
                clashingCounter++;
                actionName += "Clash_Emotion_" + ((names.length == 2) ? 1: 2);
                break;
            case LoveCompetition:
                loveCounter++;
                actionName += "Love_Competition_" + loveCounter;
                break;
            case Prisoner:
                actionName += "Pr_Free_" + ((names.length == 2) ? 1: 2);
                break;
            case ActorVampire:
                actionName += "Actor_Vampire_" + ((names.length == 2) ? 1: 2);
                break;
        }
        action.setActionName(actionName);
        
        action.setNoCharacters(names.length);
        int index = specialActions.indexOf(action);
        action = specialActions.get(index);
        ActionInstantiated instance = new ActionInstantiated(action, names);
        
        return instance;
    }

    private boolean containsAsGenerator(ConditionInstantiated cond) {
        boolean res = false;
        for (TensionHistory tension : tensionsSolved) {
            ComparisonResult comp = ConditionComparer.compare(cond, tension.getGenerator());
            res = comp.isEqualOrEquivalent(true, true);
            if (res)
                break;
        }
        return res;
    }
}

class TensionHistory {
    private ConditionInstantiated generator, solution;
    private CharacterName victim, hero;
    
    public TensionHistory(ConditionInstantiated generator, ConditionInstantiated solution) {
        this.generator = generator;
        this.solution = solution;
        victim = generator.getCharacterA();
        hero = solution.getAction().getCharactersList().get(0);
    }

    /**
     * @return the generator
     */
    public ConditionInstantiated getGenerator() {
        return generator;
    }

    /**
     * @return the solution
     */
    public ConditionInstantiated getSolution() {
        return solution;
    }

    /**
     * @return the victim
     */
    public CharacterName getVictim() {
        return victim;
    }

    /**
     * @return the hero
     */
    public CharacterName getHero() {
        return hero;
    }
    
    @Override
    public String toString() {
        return hero + ": " + generator + "/" + solution;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean res = obj instanceof TensionHistory;
        if (res) {
            TensionHistory ten = (TensionHistory)obj;
            res = ConditionComparer.compare(solution, ten.getSolution()).isEqualOrEquivalent(true, true);
            res &= ConditionComparer.compare(generator, ten.getGenerator()).isEqualOrEquivalent(true, true);
        }
        
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.generator != null ? this.generator.hashCode() : 0);
        hash = 29 * hash + (this.solution != null ? this.solution.hashCode() : 0);
        return hash;
    }
}
