package mexica.engagement.parser;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.MexicaRepository;
import mexica.core.EmotionType;
import mexica.core.Position;
import mexica.engagement.*;
import mexica.social.SocialAtomGenerator;
import mexica.social.SocialStatus;
import mexica.story.*;
import mexica.story.filter.StoryFilterException;
import mexica.tools.InvalidCharacterException;

/**
 * Class to generate atoms from a list of previous stories
 * @author Ivan Guerrero
 */
public class AtomGenerator {
    private AtomsStore store, socialStore;
    private SocialAtomGenerator socialGenerator;
    
    public AtomGenerator() {
        store = new AtomsStore();
        socialStore = new AtomsStore();
        socialGenerator = new SocialAtomGenerator();
    }
    
    public void generateAtoms(List<PreviousStory> stories) {
        Logger.getGlobal().log(Level.FINE, "Stories analyzed for atom generation: {0}", stories.size());
        int i=0;
        for (PreviousStory story : stories) {
            generateAtomsFromStory(i++, story.getActions());
        }
    }
    
    public AtomsStore getAtomsStore() {
        return store;
    }
    
    public AtomsStore getSocialAtomsStore() {
        return socialStore;
    }
    
    public void generateAtomsFromStory(Story story) {
        generateAtomsFromStory(story.getStoryID(), story.getActions(), story.getDefaultPosition());
    }
    
    public void generateAtomsFromStory(int storyID, List<ActionInstantiated> storyActions) {
        generateAtomsFromStory(storyID, storyActions, Position.Village);
    }

    public void generateAtomsFromStory(int storyID, List<ActionInstantiated> storyActions, Position defaultPosition) {
        Story story = new Story();
        story.setStoryID(storyID);
        story.setDefaultPosition(defaultPosition);
        CharacterName contextOwner;
        Avatar ownerData;
        
        for (int i=0; i<storyActions.size()-1; i++) {
            try {
                List<ConditionInstantiated> facts;
                
                //Obtain the next action in the story
                ActionInstantiated action = storyActions.get(i);
                //Create the social atom
                //IMPORTANT: Inside this method the latest action is added to the given story
                createSocialAtom(action, story);
                //printLog(story);
                //Create the regular atom
                contextOwner = action.getCharactersList().get(0);
                ownerData = story.getAvatarFactory().getAvatar(contextOwner);
                facts = ownerData.getContext().getFacts();
                ActionInstantiated nextAction = storyActions.get(i+1);
                
                Atom atom;
                Map<CharacterName, String> characterMapping = new HashMap<>();
                atom = createAtom(facts, characterMapping);
                //Add the next action to the atom
                addNextAction(nextAction, atom, characterMapping);
                atom.setOrder(i);
                atom.addStoryID(storyID);
                store.addAtom(atom);
                
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
                Logger.getGlobal().log(Level.WARNING, "Error generating atoms {0}", ex.getMessage());
            }
        }
    }
 
    /**
     * Converts the given character name into a variable
     * @param name
     * @param mapping
     * @return 
     */
    private static String simplify(CharacterName name, Map<CharacterName, String> mapping) {
        String label;
        if (mapping.containsKey(name)) {
            label = mapping.get(name);
        }
        else {
            label = String.valueOf((char)('A' + mapping.size()));
            mapping.put(name, label);
        }
        return label;
    }

    /**
     * Creates the atom with the given facts according to the given character mapping
     * @param facts Facts to include in the atom
     * @param atom The atom to be amployed
     * @param characterMapping The character mapping employed for character simplification (creation of variables)
     */
    public static Atom createAtom(List<ConditionInstantiated> facts, Map<CharacterName, String> characterMapping) {
        Atom atom = new Atom();
        for (ConditionInstantiated cond : facts) {
            switch (cond.getType()) {
                case Emotion:
                    atom.addEmotion(simplify(cond.getCharacterA(), characterMapping),
                                    cond.getCondition().getIntensity() + "", 
                                    EmotionType.getEmotionValue(cond.getCondition().getEmotion()) + "",
                                    simplify(cond.getCharacterB(), characterMapping));
                    break;
                case Tension:
                    atom.addTension(cond.getCondition().getTension(), 
                                    simplify(cond.getCharacterA(), characterMapping),
                                    simplify(cond.getCharacterB(), characterMapping));
                    break;
            }
        }
        
        return atom;
    }

    /**
     * Adds the given next action to the atom. The next action is mapped according to the given character mapping.
     * @param nextAction The next action to add.
     * @param atom The atom where the next action will be added.
     * @param characterMapping The character mapping employed for the next action.
     */
    public void addNextAction(ActionInstantiated nextAction, Atom atom, Map<CharacterName, String> characterMapping) {
        if (nextAction.getCharactersList().size() == 2)
            atom.addNextAction(simplify(nextAction.getCharactersList().get(0), characterMapping), 
                               nextAction.getAction().getActionName(), 
                               simplify(nextAction.getCharactersList().get(1), characterMapping));
        else
            atom.addNextAction(simplify(nextAction.getCharactersList().get(0), characterMapping), 
                               nextAction.getAction().getActionName(), 
                               "");
    }

    private void createSocialAtom(ActionInstantiated action, Story story) throws InvalidCharacterException, DeadAvatarException, StoryFilterException {
        Atom socialAtom;
        Avatar ownerData = null;
        List<ConditionInstantiated> prevFacts;
        List<ConditionInstantiated> facts;
        //Stores the data for the social atom if the action matches with an exceptional social norm
        if (action.getSocialStatus() == SocialStatus.specialSocialNorm ||
            action.getSocialStatus() == SocialStatus.exceptionToGeneralPrinciple) {
            //The facts are retrieved from the context defined inside the action
            ownerData = story.getAvatarFactory().getAvatar(action.getSocialData().getContexts().iterator().next());
            prevFacts = new ArrayList<>();
            for (ConditionInstantiated c : ownerData.getContext().getFacts()) {
                prevFacts.add(c);
            }
        }
        else if (action.getSocialStatus() == SocialStatus.specialActionJustified) {
            ownerData = story.getAvatarFactory().getAvatar(action.getSocialData().getJustifiedContexts().iterator().next());
            prevFacts = new ArrayList<>();
            for (ConditionInstantiated c : ownerData.getContext().getFacts()) {
                prevFacts.add(c);
            }
        }
        else
            prevFacts = null;
        
        //Apply action to story
        ActionInstantiated addedAction = story.addAction(action.getAction(), action.getCharactersList().toArray(new CharacterName[0]));
        //If an exceptional social norm is detected, create the social atom
        if (prevFacts != null) {
            //ownerData = story.getAvatarFactory().getAvatar(action.getSocialData().getContexts().iterator().next());
            facts = ownerData.getContext().getFacts();
            socialAtom = socialGenerator.createSocialAtom(facts, prevFacts, action, ownerData.getName());
            socialAtom.addStoryID(story.getStoryID());
            socialStore.addAtom(socialAtom);
            
            //If the action is a exceptional social norm, 
            //  create the social atom first and consider it during the addition of the action
            story.removeAction(addedAction);
            MexicaRepository.getInstance().getSocialAtoms().addAtom(socialAtom);
            story.addAction(action.getAction(), action.getCharactersList().toArray(new CharacterName[0]));
        }
    }

    private void printLog(Story story) {
        System.out.println(story.getActions());
        for (Avatar a : story.getAvatarFactory().getActiveCharacters()) {
            System.out.println(a.getName() + "-" + a.getCurrentPosition());
        }
        System.out.println("------------------------------------------------");
    }
}