package mexica.engagement;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.ForbiddenCharacters;
import mexica.MexicaParameters;
import mexica.context.AtomSimilarityResult;
import mexica.context.Element;
import mexica.core.Action;
import mexica.story.ActionInstantiated;
import mexica.story.Story;
import mexica.tools.AvatarFactory;
import subgraph.INode;
import subgraph.Solution;

/**
 *
 * @author Ivan Guerrero
 */
public class EngagementCharacterInstantiator {
    private static Random random = new Random();
    
    /**
     * Depending on the instantiation mode defined in the parameters file, instantiates the characters for the given action
     * @param story Current story
     * @param action Action to be instantiated
     * @param atomResult Atom to be employed to instantiate the characters
     * @return A list with the selected characters
     */
    public static List<CharacterName> instantiateCharacters(Story story, Action action, AtomSimilarityResult atomResult) {
        List<CharacterName> characters = new ArrayList<>();
        List<CharacterName> candidates;
        switch (MexicaParameters.CHARACTER_INSTANTIATION_MODE) {
            case 1:
                candidates = instantiateCharacterMode1(story, action, atomResult);
                //Select the performer
                characters.add(candidates.get(random.nextInt(candidates.size())));
                //Select the receiver
                CharacterName receiver;
                do {
                     receiver = candidates.get(random.nextInt(candidates.size()));
                } while (characters.contains(receiver));
                characters.add(receiver);
                break;
            case 2:
                characters = instantiateCharacterMode2(story, action, atomResult);
                break;
        }

        Logger.getGlobal().log(Level.FINE, "Characters: {0}", characters);
        return characters;
    }
    
    /**
     * Instantiate a character for the given atom result according to the gidelines set in Mexica 
     * when working in mode E1 or ER1.<br>
     * A set of candidate characters is obtained removing from the available characters,
     * the characters already employed in the context from where the atom was selected.
     * @param atomResult
     * @return 
     */
    private static List<CharacterName> instantiateCharacterMode1(Story story, Action action, AtomSimilarityResult atomResult) {
        List<CharacterName> candidates = new ArrayList<>();
        List<CharacterName> availableCharacters = story.getAvatarFactory().getAvailableCharacterNames();
        List<CharacterName> contextAvatars = obtainContextCharacters(atomResult, availableCharacters);
        
        //Obtain the active characters that do not belong to the mapping (since those in the mapping don't match)
        for (CharacterName name : availableCharacters) {
            if (!contextAvatars.contains(name))
                candidates.add(name);
        }
        
        return candidates;
    }
    
    /**
     * Instantiates characters for the given action depending on the mapping obtained for the most suitable atom found
     * @param story The current story
     * @param action The action to be instantiated
     * @param atomResult The result of comparing one character's context with all the available atoms
     * @return A list with the selected characters
     */
    private static List<CharacterName> instantiateCharacterMode2(Story story, Action action, AtomSimilarityResult atomResult) {
        List<CharacterName> availableCharacters = story.getAvatarFactory().getAvailableCharacterNames();
        List<CharacterName> contextCharacters = obtainContextCharacters(atomResult, availableCharacters);
        CharacterName performer, receiver, owner;
        owner = atomResult.getContextOwner().getName();
        availableCharacters.removeAll(contextCharacters);
        //contextCharacters.remove(owner);
            
        List<CharacterName> candidates = new ArrayList<>();
        //Contains the character mapping for the atom
        Solution solution = atomResult.getSolution();        
        
        List<String> characterNames = action.getCharacters();
        INode performerNode, receiverNode;
        performerNode = solution.getInvertedMapping(new Element(characterNames.get(0).toUpperCase()));
        performer = (performerNode != null) ? CharacterName.valueFromAbbreviation(performerNode.getID()) : 
                                              CharacterName.Not_Defined;
        receiverNode = (characterNames.size() == 2) ? solution.getInvertedMapping(new Element(characterNames.get(1).toUpperCase())) : null;
        receiver = (receiverNode != null) ? CharacterName.valueFromAbbreviation(receiverNode.getID()) :
                                            CharacterName.Not_Defined;
        
        if (action.getNoCharacters() == 1) {
            CharacterName selectedCharacter;
            selectedCharacter = instantiateCharacter(story, action, availableCharacters, contextCharacters, performer, owner);
            candidates.add(selectedCharacter);
        }
        else {
            CharacterName[] selectedCharacters;
            selectedCharacters = instantiateCharacters(story, action, availableCharacters, contextCharacters, performer, receiver, owner);   
            candidates.add(selectedCharacters[0]);
            candidates.add(selectedCharacters[1]);
        }

        return candidates;
    }

    /**
     * Obtains the characters available inside the atom mapping
     * @param atomResult
     * @param availableCharacters
     * @return 
     */
    private static List<CharacterName> obtainContextCharacters(AtomSimilarityResult atomResult, List<CharacterName> availableCharacters) {
        //Since the instantiation of the characters was not successfull, there are characters in the action without mapping
        List<CharacterName> contextAvatars = new ArrayList<>();
        //TODO: Validate if there are always characters in the keyset
        Iterable<INode> contextCharacters = atomResult.getSolution().getMappingKeys();
        //Obtain the list of characters in the context
        for (INode contextChar : contextCharacters) {
            CharacterName character = CharacterName.valueFromAbbreviation(contextChar.getID());
            if (availableCharacters.contains(character))
                contextAvatars.add(character);
        }
        return contextAvatars;
    }

    /**
     * Instantiates the character for an action with one character
     * @param story The current story
     * @param action The selected action
     * @param availableCharacters List with all the available characters (without the context characters)
     * @param performer The action performer (if was able to be mapped from the selected atom)
     * @param owner The context's owner
     * @return The character instantiated
     */
    private static CharacterName instantiateCharacter(Story story, Action action, 
                                                      List<CharacterName> availableCharacters, 
                                                      List<CharacterName> contextCharacters, 
                                                      CharacterName performer, CharacterName owner) {
        ActionInstantiated instance;
        //Try to instantiate the character using the mapping found in the atom
        if (!AvatarFactory.getNonElegibleCharacternames().contains(performer)) {
            instance = new ActionInstantiated(action, performer);
            if (story.getElementsEmployed().isActionAvailable(instance))
                return performer;
        }
        
        if (MexicaParameters.FORBIDDEN_CHARACTERS == ForbiddenCharacters.HalfActive ||
            MexicaParameters.FORBIDDEN_CHARACTERS == ForbiddenCharacters.Inactive) {
            if (MexicaParameters.FORBIDDEN_CHARACTERS == ForbiddenCharacters.HalfActive)
                contextCharacters.remove(owner);

            //Try to instantiate using one previously employed characters
            for (CharacterName name : contextCharacters) {
                instance = new ActionInstantiated(action, name);
                if (story.getElementsEmployed().isActionAvailable(instance))
                    return name;
            }
        }
        
        //Try to instantiate using one new character
        if (MexicaParameters.PARTIAL_CHARACTER_INSTANTIATION) {
            for (CharacterName name : availableCharacters) {
                instance = new ActionInstantiated(action, name);
                if (story.getElementsEmployed().isActionAvailable(instance))
                    return name;
            }
        }
        
        return null;
    }

    /**
     * Instantiates the characters for an action with two characters
     * @param story The current story
     * @param action The action to be instantiated
     * @param availableCharacters Characters available
     * @param contextCharacters Characters employed during the story
     * @param performer The action performer (obtained from the most suitable atom)
     * @param receiver The action receiver (obtained from the most suitable atom)
     * @param owner Context owner
     * @return A list with the two selected characters
     */
    private static CharacterName[] instantiateCharacters(Story story, Action action,
                                                         List<CharacterName> availableCharacters, 
                                                         List<CharacterName> contextCharacters, 
                                                         CharacterName performer, CharacterName receiver, 
                                                         CharacterName owner) {
        CharacterName[] characters = new CharacterName[2];
        
        List<CharacterName> performerCandidates, receiverCandidates;
        performerCandidates = new ArrayList<>();
        receiverCandidates = new ArrayList<>();
        //Adds the possible performers and receivers
        //If the performer or receiver are elegible characters, add it to the candidates
        if (!AvatarFactory.getNonElegibleCharacternames().contains(performer))
            performerCandidates.add(performer);
        if (!AvatarFactory.getNonElegibleCharacternames().contains(receiver))
            receiverCandidates.add(receiver);
        
        //If the context owner was already analyzed in the atom, remove it from the candidates
        switch(MexicaParameters.FORBIDDEN_CHARACTERS) {
            case Active:
                //All the context characters are banned
                if (MexicaParameters.PARTIAL_CHARACTER_INSTANTIATION) {
                    performerCandidates.addAll(shuffle(availableCharacters));
                    receiverCandidates.addAll(shuffle(availableCharacters));
                }
                break;
            case HalfActive:
                //Only the context owner is banned
                if (contextCharacters.contains(owner))
                    contextCharacters.remove(owner);
                
                performerCandidates.addAll(shuffle(contextCharacters));
                receiverCandidates.addAll(shuffle(contextCharacters));
                if (MexicaParameters.PARTIAL_CHARACTER_INSTANTIATION) {
                    performerCandidates.addAll(shuffle(availableCharacters));
                    receiverCandidates.addAll(shuffle(availableCharacters));
                }
                break;
            case Inactive:
                //No characters are banned
                performerCandidates.addAll(shuffle(contextCharacters));
                receiverCandidates.addAll(shuffle(contextCharacters));
                if (MexicaParameters.PARTIAL_CHARACTER_INSTANTIATION) {
                    performerCandidates.addAll(shuffle(availableCharacters));
                    receiverCandidates.addAll(shuffle(availableCharacters));
                }
                break;
        }
        
        for (CharacterName perf : performerCandidates) {
            for (CharacterName rec : receiverCandidates) {
                if (perf != rec) {
                    ActionInstantiated instance = new ActionInstantiated(action, perf, rec);
                    if (story.getElementsEmployed().isActionAvailable(instance)) {
                        characters[0] = perf;
                        characters[1] = rec;
                        return characters;
                    }
                }
            }
        }
        
        return characters;
    }

    private static List<? extends CharacterName> shuffle(List<CharacterName> list) {
        List<CharacterName> newList = new ArrayList<>();
        newList.addAll(list);
        for (int i=0; i<newList.size(); i++) {
            int pos1 = random.nextInt(newList.size());
            int pos2 = random.nextInt(newList.size());
            CharacterName cPos1 = newList.get(pos1);
            CharacterName cPos2 = newList.get(pos2);
            newList.set(pos1, cPos2);
            newList.set(pos2, cPos1);
        }
        return newList;
    }
}