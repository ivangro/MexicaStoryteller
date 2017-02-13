package mexica.social;

import java.util.*;
import mexica.CharacterName;
import mexica.MexicaRepository;
import mexica.core.Condition;
import mexica.core.Tension;
import mexica.engagement.Atom;
import mexica.engagement.parser.AtomGenerator;
import mexica.story.ActionInstantiated;
import mexica.story.ConditionInstantiated;
import mexica.tools.*;

/**
 * Class to generate social atoms to represent exceptional social norms
 * @author Ivan Guerrero
 */
public class SocialAtomGenerator {
    /**
     * Creates a social atom mixing the two sets of facts.<br>
     * Obtains the newly inserted facts and marks them @see(mexica.social.ConditionStatus).<br>
     * Obtains the deleted facts and marks them.<br>
     * Obtains the facts that remained unchanged and marks them.
     */
    public SocialAtom createSocialAtom(List<ConditionInstantiated> facts, List<ConditionInstantiated> prevFacts, ActionInstantiated action, CharacterName contextOwner) {
        Map<CharacterName, String> mapping = buildMapping(action);
        detectJustifiedActionByContext(action, facts, contextOwner);
        List<ConditionInstantiated> simpleFacts = parseFacts(facts, action);
        List<ConditionInstantiated> simplePrevFacts = parseFacts(prevFacts, action);
        Atom atom = AtomGenerator.createAtom(simpleFacts, mapping);
        Atom prevAtom = AtomGenerator.createAtom(simplePrevFacts, mapping);
        mergeAtoms(prevAtom, atom);
        SocialAtom socialAtom = new SocialAtom(prevAtom, mapping);
        
        SocialAction socialAction = action.getSocialData().getSocialAction();
        socialAtom.setSocialAction(socialAction.getActionName());
        
        switch (socialAction.getSocialRelation()) {
            case Social:
                //Obtain the social relationship between the characters in the action
                int socialRelation = 
                        MexicaRepository.getInstance().getHierarchyStore().getSocialRelationship(action.getCharactersList());
                socialAtom.setSocialRelation(socialRelation);
                break;
            case Gender:
                //Obtain the gender relationship between the characters in the action
                int genderRelation = 
                        MexicaRepository.getInstance().getHierarchyStore().getGenderRelationship(action.getCharactersList());
                socialAtom.setGenderRelation(genderRelation);
                break;
        }
        
        return socialAtom;
    }
    
    SocialAtom createSocialAtom(List<ConditionInstantiated> facts, List<ConditionInstantiated> prevFacts) {
        Map<CharacterName, String> mapping = new HashMap<>();
        Atom atom = AtomGenerator.createAtom(facts, mapping);
        Atom prevAtom = AtomGenerator.createAtom(prevFacts, mapping);
        mergeAtoms(prevAtom, atom);
        
        return new SocialAtom(prevAtom, mapping);
    }
    
    /**
     * Creates a social atom mixing the two given atoms.<br>
     * Obtains the newly inserted facts and marks them @see(mexica.social.ConditionStatus).<br>
     * Obtains the deleted facts and marks them.<br>
     * Obtains the facts that remained unchanged and marks them.
     * @param socialAtom The atom with the facts on the previous stage
     * @param atom The atom with the facts after the latest action insertion
     */
    private void mergeAtoms(Atom socialAtom, Atom atom) {
        List<Integer> matchings = new ArrayList<>();
        
        //Find the facts in socialAtom and not in atom. Mark them as deleted facts.
        for (Condition c : socialAtom.getEmotions()) {
            c.setStatus(ConditionStatus.Remained);
            boolean equivalentFound = false;
            for (int i=0; i<atom.getEmotions().size(); i++) {
                Condition c2 = atom.getEmotions().get(i);
                if (ConditionComparer.compareEmotions(c, c2) == ConditionRelationship.Equal &&
                    c.getCharacterA().equalsIgnoreCase(c2.getCharacterA()) && 
                    c.getCharacterB().equalsIgnoreCase(c2.getCharacterB())) {
                    equivalentFound = true;
                    matchings.add(i);
                    break;
                }
            }
            if (!equivalentFound)
                c.setStatus(ConditionStatus.Removed);
        }
        
        //Find the facts in atom and not in socialAtom. mark them as inserted facts.
        for (int i=0; i<atom.getEmotions().size(); i++) {
            if (!matchings.contains(i)) {
                Condition c = atom.getEmotions().get(i);
                c.setStatus(ConditionStatus.Inserted);
                socialAtom.getEmotions().add(c);
            }
        }
        //The rest are unchanged facts.
        
        matchings = new ArrayList<>();
        
        //Find the facts in socialAtom and not in atom. Mark them as deleted facts.
        for (Condition c : socialAtom.getTensions()) {
            c.setStatus(ConditionStatus.Remained);
            boolean equivalentFound = false;
            for (int i=0; i<atom.getTensions().size(); i++) {
                Condition c2 = atom.getTensions().get(i);
                if (ConditionComparer.compareTensions(c, c2) == ConditionRelationship.Equal &&
                    c.getCharacterA().equalsIgnoreCase(c2.getCharacterA()) && 
                    c.getCharacterB().equalsIgnoreCase(c2.getCharacterB())) {
                    equivalentFound = true;
                    matchings.add(i);
                    break;
                }
            }
            if (!equivalentFound)
                c.setStatus(ConditionStatus.Removed);
        }
        
        //Find the facts in atom and not in socialAtom. mark them as inserted facts.
        for (int i=0; i<atom.getTensions().size(); i++) {
            if (!matchings.contains(i)) {
                Tension c = atom.getTensions().get(i);
                c.setStatus(ConditionStatus.Inserted);
                socialAtom.getTensions().add(c);
            }
        }
        
        //The rest are unchanged facts.        
    }
    
    /**
     * Builds a special mapping to create the social atom.
     * The mapping only contains elements for the characters involved in the given action and the rest of the characters is mapped to an unknown node.
     * @param action The given action
     * @return A mapping for all the characters.
     */
    public Map<CharacterName, String> buildMapping(ActionInstantiated action) {
        Map<CharacterName, String> mapping = new HashMap<>();
        //Obtain the characters of the action
        List<CharacterName> charactersList = action.getCharactersList();
        //Map the social characters of the action to themselves
        
        SocialAction socialAction = action.getSocialData().getSocialAction();
        switch (socialAction.getSocialCharacter()) {
            case BothCharacters:
                mapping.put(charactersList.get(0), "c" + CharacterName.getAbbreviation(charactersList.get(0)));
                mapping.put(charactersList.get(1), "c" + CharacterName.getAbbreviation(charactersList.get(1)));
                break;
            case Performer:
                mapping.put(charactersList.get(0), "c" + CharacterName.getAbbreviation(charactersList.get(0)));
                if (charactersList.size() == 2)
                    mapping.put(charactersList.get(1), "A");
                break;
            case Receiver:
                mapping.put(charactersList.get(1), "c" + CharacterName.getAbbreviation(charactersList.get(1)));
                if (charactersList.size() == 2)
                    mapping.put(charactersList.get(0), "A");
                break;
            default:
                mapping.put(charactersList.get(0), "A");
                if (charactersList.size() == 2)
                    mapping.put(charactersList.get(1), "B");
                break;
        }
        
        //Add Unknown mapping for the rest of the characters
        for (CharacterName name : CharacterName.values()) {
            if (!mapping.containsKey(name) && !AvatarFactory.getNonElegibleCharacternames().contains(name))
                mapping.put(name, CharacterName.getAbbreviation(CharacterName.Not_Defined));
        }
        return mapping;
    }

    /**
     * Removes the facts that do not employ characters mapped
     * @param previousFacts
     * @return 
     */
    public List<ConditionInstantiated> parseFacts(List<ConditionInstantiated> previousFacts, ActionInstantiated action) {
        List<ConditionInstantiated> facts = new ArrayList<>();
        List<CharacterName> charactersList = action.getCharactersList();
        //If the fact involves a different character from those available in the action, remove the fact
        for (ConditionInstantiated c : previousFacts) {
            switch (c.getType()) {
                case Emotion:
                    if (charactersList.contains(c.getCharacterA()) && (action.getAction().getNoCharacters() == 2 && charactersList.contains(c.getCharacterB())))
                        facts.add(c);
                    break;
                case Tension:
                    //If one of the characters in the action receives a tension, keep it
                    if (charactersList.contains(c.getCharacterA()))
                        facts.add(c);
                    break;
            }
        }
            
        return facts;
    }

    private void detectJustifiedActionByContext(ActionInstantiated action, List<ConditionInstantiated> facts, CharacterName contextOwner) {
        if (action.getSocialStatus() == SocialStatus.exceptionToGeneralPrinciple) {
            ConditionInstantiated socialCondition = SocialNormsUtils.createSocialLink(contextOwner, action.getCharactersList().get(0));
            int forRemoval = -1;
            for (int i=0; i<facts.size(); i++) {
                ComparisonResult res = ConditionComparer.compare(facts.get(i), socialCondition);
                if (res.isEqualOrEquivalent(true, true)) {
                        forRemoval = i;
                        break;
                }
            }
            if (forRemoval >= 0)
                facts.remove(forRemoval);
        }
    }
}