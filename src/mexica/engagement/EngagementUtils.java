package mexica.engagement;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.context.AtomSimilarityResult;
import mexica.core.Action;
import mexica.reflection.Guideline;
import mexica.story.*;
import mexica.story.filter.StoryFilterException;
import mexica.tools.InvalidCharacterException;
import subgraph.Solution;

/**
 *
 * @author Ivan Guerrero
 */
public class EngagementUtils {
    
    /**
     * Obtain the available atoms for each context.<br>
     * First all the atoms with a similarity value >= ACAS are retrieved. 
     * If no atoms are found, they are modified preserving only the following:
     *      - emotional relations of the first character
     *      - tensions of the first character
     *      - Hr and Lr tensions of all the characters
     * If no atoms are found, they are modified again preserving the following:
     *      - tensions of the first character
     *      - Hr and Lr tensions of all the characters
     * If no atoms are found, an empty collection is returned.<br>
     * (For details, see section 4.3.1.1 from the PhD thesis Mexica: a computer model of creativity in writing).
     * @param characters The available characters inside the current story
     * @return A collection of similar atoms
     */
    public static Collection<AtomSimilarityResult> obtainAvailableAtoms(List<Avatar> characters) {
        List<Avatar> selection = selectRepresentativeContexts(characters);
        
        AtomsStore atomStore = MexicaRepository.getInstance().getAtoms();
        Collection<AtomSimilarityResult> availableAtoms = new ArrayList<>();
        Collection<AtomSimilarityResult> list;
            
        for (Avatar avatar : selection) {
            list = analyzeSimilarity(avatar, atomStore);
            availableAtoms.addAll(list);
        }
        
        if (MexicaParameters.ENABLE_ACAS_II && availableAtoms.isEmpty()) {
            for (Avatar avatar : selection) {
                List<ConditionInstantiated> facts = transformFactsII(avatar);
                list = analyzeSimilarity(avatar, facts, atomStore, MexicaParameters.ACAS_II_CONSTANT);
                availableAtoms.addAll(list);
            }
            if (!availableAtoms.isEmpty())
                Logger.getGlobal().log(Level.INFO, "ACAS II utilized");
        }
        
        if (MexicaParameters.ENABLE_ACAS_III && availableAtoms.isEmpty()) {
            for (Avatar avatar : selection) {
                List<ConditionInstantiated> facts = transformFactsIII(avatar);
                list = analyzeSimilarity(avatar, facts, atomStore, MexicaParameters.ACAS_III_CONSTANT);
                availableAtoms.addAll(list);
            }
            
            if (!availableAtoms.isEmpty()) {
                Logger.getGlobal().log(Level.INFO, "ACAS III utilized");
                MexicaParameters.PARTIAL_CHARACTER_INSTANTIATION = false;
            }
        }
        
        return availableAtoms;
    }
    
    /**
     * Determines the similarity pecentage between the context of a character and all the available atoms
     * @param facts Character's context
     * @param atomStore Store with the available atoms
     * @return List of atom's comparison result whith similarity percentage >= to ACAS_CONSTANT
     */
    public static Collection<AtomSimilarityResult> analyzeSimilarity(Avatar owner, AtomsStore atomStore) {
        List<ConditionInstantiated> facts = owner.getContext().getFacts();
        return analyzeSimilarity(owner, facts, atomStore, MexicaParameters.ACAS_CONSTANT);
    }
    
    /**
     * Determines the similarity pecentage between the context of a character and all the available atoms
     * @param owner Avatar owner of the given facts
     * @param facts Character's known facts
     * @param atomStore Store with the available atoms
     * @param minimumSimilarity Minimum similarity value for an atom to be retrieved
     * @return List of atom's comparison result whith similarity percentage
     */
    public static Collection<AtomSimilarityResult> analyzeSimilarity(Avatar owner, List<ConditionInstantiated> facts, AtomsStore atomStore, int minimumSimilarity) {
        Collection<AtomSimilarityResult> result = new ArrayList<>();
                
        //Compares the context against every available atom
        for (Cell cell : atomStore.getCells()) {
            Map<Atom, Solution> atoms = cell.getAtoms(facts);
            for (Atom atom : atoms.keySet()) {
                AtomSimilarityResult atomResult = new AtomSimilarityResult(atom, atoms.get(atom), owner);
                result.add(atomResult);
            }
        }
        
        return result;
    }
    
    public static List<ActionInstantiated> getActionsFromAtoms(Story story, Collection<AtomSimilarityResult> availableAtoms) {
        Random random = new Random();
        boolean actionAdded = false;
        Action action;
        List<ActionInstantiated> result = new ArrayList<>();
        
        List<Action> actionList = MexicaRepository.getInstance().getActions().getActionList();
        
        //Take each atom and try to instantiate an action associated to each one of them taken in descending order
        //in accordance to the similarity with the context
        for (AtomSimilarityResult atomResult : availableAtoms) {
            Logger.getGlobal().log(Level.FINER, "Attempting {0}", atomResult.getAtom());
            List<Action> nextActions = atomResult.getAtom().getNextActions();
            int size = nextActions.size();
            int offset = random.nextInt(size);
            for (int i=0; i<size && !actionAdded; i++) {
                action = nextActions.get((i + offset) % size);
                int index = actionList.indexOf(action);
                if (!story.getGuidelines().satisfiesGuidelines(action))
                    Logger.getGlobal().log(Level.FINER, "Action didn't satisfy guidelines: {0}", action);
                else if (index == -1)
                    Logger.getGlobal().log(Level.FINER, "Action not found: {0}", action);
                else {
                    result.addAll(instantiateActions(story, action, atomResult));
                }
            }
        }
        
        return result;
    }   

    private static List<ActionInstantiated> instantiateActions(Story story, Action action, AtomSimilarityResult atomResult) {
        List<ActionInstantiated> result = new ArrayList<>();
        ActionInstantiated newAction;
        
        List<Action> actionList = MexicaRepository.getInstance().getActions().getActionList();
        int index = actionList.indexOf(action);
        
        if (action.getNoCharacters() == 2) {
            List<CharacterName> characters = EngagementCharacterInstantiator.instantiateCharacters(story, action, atomResult);
            action = actionList.get(index);
            try {
                newAction = story.addAction(action, characters.get(0), characters.get(1));
                if ((newAction.breaksSocialNorm() && story.getGuidelines().containsGuideline(Guideline.breakSocialNorm)) ||
                    (!newAction.breaksSocialNorm() && story.getGuidelines().containsGuideline(Guideline.notBreakSocialNorm)))
                    result.add(newAction);
                story.removeAction(newAction);
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) { }
        }
        else {
            List<CharacterName> characters = EngagementCharacterInstantiator.instantiateCharacters(story, action, atomResult);
            action = actionList.get(index);
            try {
                newAction = story.addAction(action, characters.get(0));
                if ((newAction.breaksSocialNorm() && story.getGuidelines().containsGuideline(Guideline.breakSocialNorm)) ||
                    (!newAction.breaksSocialNorm() && story.getGuidelines().containsGuideline(Guideline.notBreakSocialNorm)))
                    result.add(newAction);
                story.removeAction(newAction);
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {}
        }
        
        return result;
    }

    public static List<Avatar> selectRepresentativeContexts(List<Avatar> characters) {
        List<Avatar> selection = new ArrayList<>();
        int maxSize = 0;
        
        //Obtain the context with the highest number of facts
        for (Avatar a : characters) {
            int size = a.getContext().getFacts().size();
            if (maxSize < size)
                maxSize = size;
        }
        //Select the context with at least half of the facts respect to the highest number of facts
        maxSize /= 2;
        for (Avatar a : characters) {
            int size = a.getContext().getFacts().size();
            if (size >= maxSize)
                selection.add(a);
        }
        
        return selection;
    }

    /**
     * Preserves the following facts from the given avatar
     *      - emotional relations of the avatar
     *      - tensions of the avatar
     *      - Hr and Lr tensions of all the characters
     * @param owner
     * @return 
     */
    private static List<ConditionInstantiated> transformFactsII(Avatar owner) {
        List<ConditionInstantiated> facts = owner.getContext().getFacts();
        List<ConditionInstantiated> transformed = new ArrayList<>();
        
        for (ConditionInstantiated fact : facts) {
            switch (fact.getType()) {
                case Emotion:
                    if (fact.getCharacterA() == owner.getName() || fact.getCharacterB() == owner.getName())
                        transformed.add(fact);
                    break;
                case Tension:
                    if (fact.getCharacterA() == owner.getName() || fact.getCharacterB() == owner.getName())
                        transformed.add(fact);
                    else {
                        switch (fact.getCondition().getTension()) {
                            case HealthAtRisk:
                            case LifeAtRisk:
                                transformed.add(fact);
                        }
                    }
                    break;
            }
        }
        
        return transformed;
    }

    /**
     * Preserves the following facts from the given avatar
     *      - tensions of the avatar
     *      - Hr and Lr tensions of all the characters
     * @param owner
     * @return 
     */
    private static List<ConditionInstantiated> transformFactsIII(Avatar owner) {
        List<ConditionInstantiated> facts = owner.getContext().getFacts();
        List<ConditionInstantiated> transformed = new ArrayList<>();
        
        for (ConditionInstantiated fact : facts) {
            switch (fact.getType()) {
                case Tension:
                    if (fact.getCharacterA() == owner.getName() || fact.getCharacterB() == owner.getName())
                        transformed.add(fact);
                    else {
                        switch (fact.getCondition().getTension()) {
                            case HealthAtRisk:
                            case LifeAtRisk:
                                transformed.add(fact);
                        }
                    }
                    break;
            }
        }
        
        return transformed;
    }
}