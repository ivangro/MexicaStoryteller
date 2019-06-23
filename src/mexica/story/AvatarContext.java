package mexica.story;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.core.*;
import mexica.story.analyzer.AvatarTensions;
import mexica.tools.*;
import mexica.tools.context.SpecialTensionAnalyzer;

/**
 * Class for representing the known facts for the character owner of the context
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class AvatarContext extends StoryContextListener implements Cloneable {
    /** 
     * facts is the list of known facts. 
     * previousFacts is the list of facts in the previous iteration.
     * storicalFacts is the list of all the facts known or forgotten(removed) by the avatar
     */
    private List<ConditionInstantiated> facts, previousFacts, storicalFacts;
    /** Characters with a positive emotion against the context owner */
    private List<CharacterName> linkedCharacters;
    private List<CharacterName> knownCharacters;
    private CharacterName owner;
    private AvatarTensions tensions;
    
    private SpecialTensionAnalyzer analyzer;
    private Story story;
    
    AvatarContext(CharacterName characterName, Story story) {
        this.story = story;
        facts = new ArrayList<>();
        previousFacts = new ArrayList<>();
        storicalFacts = new ArrayList<>();
        linkedCharacters = new ArrayList<>();
        knownCharacters = new ArrayList<>();
        tensions = new AvatarTensions(characterName);
        owner = characterName;

        //Adds analyzer that will update the information when triggered
        analyzer = new SpecialTensionAnalyzer(owner, story);
        addStoryContextListener(analyzer, IStoryContextListener.ListType.Tensions);
        addStoryContextListener(analyzer, IStoryContextListener.ListType.Conditions);
        addStoryContextListener(analyzer, IStoryContextListener.ListType.StoryUpdated);
    }
    
    /**
     * Adds the condition to the known facts
     * Validates if there are no equivalent conditions first
     * @param condition Condition to be validated and added
     */
    public void addCondition(ConditionInstantiated condition) {
        Avatar avatarA, avatarB;
        avatarA = story.getAvatarFactory().getAvatar(condition.getCharacterA());
        avatarB = story.getAvatarFactory().getAvatar(condition.getCharacterB());
        
        switch (condition.getType()) {
            case Emotion:
                if (!avatarA.equals(avatarB))
                    addEmotionToList(condition);
                break;
            case Position:
                if (condition.getCharacterA().equals(getOwner())) {
                    if (condition.getPosition().equals(Position.OtherCharactersPosition)) {
                        avatarA.setCurrentPosition(avatarB.getCurrentPosition());
                    }
                    else {
                        avatarA.setCurrentPosition(condition.getPosition());
                    }
                }
                break;
            case Tension:
                if (!avatarA.equals(avatarB))
                    addTensionToList(condition, false);
                break;
        }
    }
    
    /**
     * Adds a new emotion between the performer of the action and the owner of the context
     * @param cond Condition to be added
     * @param friend The friend of the context's owner
     * @param performer The character who performed the action against a friend of the context's owner
     */
    public void createCondition(ConditionInstantiated cond, CharacterName friend, CharacterName performer) {
        int intensity, intensityOwnerAndFriend = 0, intensityFriendAndPerformer = 0;
        try {
            if (!friend.equals(performer)) {
                Condition condition = cond.getCondition();
                //The new condition will be created between the owner and the friend of the performer
                Condition newCondition = new Condition();
                newCondition.setConditionType(ConditionType.Emotion);
                newCondition.setCharacterA("a");
                newCondition.setCharacterB("b");
                //For searching purposes is any emotion, but in the end is the same emotion as in condition
                newCondition.setEmotionType(EmotionType.getAnyEmotion());  
                newCondition.setIntensity(condition.getIntensity());
                ConditionInstantiated instance = new ConditionInstantiated(newCondition, getOwner(), performer);

                if (condition.getIntensity() == MexicaParameters.PROPORTIONAL_INTENSITY) {
                    //If the intensity is proportional , obtain the intensity of the emotion of the given type
                    //between the owner and the friend, then between the friend and the performer
                    //and mix both to obtain the intensity between the owner and the performer
                    ConditionInstantiated ownerAndFriend = 
                            getRelationshipWith(new ConditionInstantiated(newCondition, getOwner(), friend), true);
                    ConditionInstantiated friendAndPerformer = 
                            getRelationshipWith(new ConditionInstantiated(newCondition, friend, performer), false);

                    intensityOwnerAndFriend = ownerAndFriend.getCondition().getIntensity();
                    intensityFriendAndPerformer = friendAndPerformer.getCondition().getIntensity();
                    intensity = ProportionalIntensity.getProportionalIntensity(intensityOwnerAndFriend, intensityFriendAndPerformer);

                    newCondition.setIntensity(intensity);
                    //Restablishes the original emotion type
                    newCondition.setEmotionType(condition.getEmotion());
                    instance = new ConditionInstantiated(newCondition, getOwner(), performer);
                }

                if (!instance.getCharacterA().equals(instance.getCharacterB()))
                    addEmotionToList(instance);
            }
        } catch (NullPointerException npe) {
            //Due to the lack of related characters, so move on
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, "Error creating condition {1} from action {0} ({2}, {3}) {4}", new Object[]{ cond.getAction(), cond, friend, performer, e});
            Logger.getGlobal().log(Level.WARNING, "Values: {0} {1}", new Object[]{intensityOwnerAndFriend, intensityFriendAndPerformer});
        }
    }
    
    /**
     * Known facts stored in the context of the character owner
     * @return A list of known conditions
     */
    public List<ConditionInstantiated> getFacts() {
        return facts;
    }
    
    /**
     * Obtains a list with all the facts known or forgotten (deleted) by the avatar
     * @return 
     */
    public List<ConditionInstantiated> getHistoricalFacts() {
        return storicalFacts;
    }
        
    /**
     * Obtains a list with all the characters who have a positive relationship
     * against the owner of the context and share the same position
     * @return A list of linked character's names
     */
    public List<Avatar> getLinkedCharacters() {
        return getLinkedCharacters(true);
    }
    
    /**
     * Obtains a list with all the characters who have a positive relationship
     * with the owner of the context
     * @param validatePosition TRUE to include only characters who share the same position
     * @return A list of linked character's names
     */
    public List<Avatar> getLinkedCharacters(boolean validatePosition) {
        List<Avatar> list = new ArrayList<>();
        
        Avatar avatarOwner = story.getAvatarFactory().getAvatar(getOwner());
        Position ownerPosition = avatarOwner.getCurrentPosition();
        
        for (CharacterName name : linkedCharacters) {
            Avatar linkedAvatar = story.getAvatarFactory().getAvatar(name);
            if (!validatePosition || linkedAvatar.getCurrentPosition().equals(ownerPosition)) {
                list.add(linkedAvatar);
            }
        }
        
        return list;
    }
    
    /**
     * Obtains a list of the known characters by the context owner
     * @return A list of known character names
     */
    public List<CharacterName> getKnownCharacters() {
        return knownCharacters;
    }
    
    /**
     * Adds the given condition to the known facts
     * If an equivalent condition is found in the list, is not added twice
     * @param condition 
     */
    public void addEmotionToList(ConditionInstantiated condition) {
        boolean equivalent = false;
        
        for (int i=0; i< facts.size(); i++) {
            ConditionInstantiated cond = facts.get(i);
            ComparisonResult res = ConditionComparer.compare(condition, cond);
            //If conditions are equivalent and the intensity of the new condition is greater
            //Then remove the previous condition
            if (res.isEqualOrEquivalent(true, true)) {
                facts.remove(i);
            } 
            //If conditions are equivalent and the intensity of the new condition is of the same sign
            //Then don't add the new one
            else if (res.isEqualOrEquivalent(true, false) && res.areSignsEqual()) {
                equivalent = true;
            }
            
        }
        
        if (!equivalent) {
            facts.add(condition);
            storicalFacts.add(condition);
            if (condition.getCondition().getIntensity() > 0) {
                //Establishes a link between B->A
                story.getAvatarFactory().getAvatar(condition.getCharacterB()).getContext().addLinkedCharacter(condition.getCharacterA());
            }
            addKnownCharacter(condition.getCharacterA());
            addKnownCharacter(condition.getCharacterB());
        }
    }
    
    /**
     * If an equivalent condition is found, remove it from the list
     * @param condition The condition to be searched
     */
    public void removeEmotionFromList(ConditionInstantiated condition) {
        for (int i=0; i< facts.size(); i++) {
            ConditionInstantiated cond = facts.get(i);
            ComparisonResult res = ConditionComparer.compare(condition, cond);
            if (res.isEqualOrEquivalent(true, true)) {
                facts.remove(i);
                break;
            }
        }
    }
    
    /**
     * Adds the given condition to the known facts
     * @param condition The condition to be added, if founds a defeatable condition, this condition
     * will be removed from the list and the new tension won't be added
     * @param removeEquivalent When TRUE if an equivalent condition is found in the list, this is removed
     */
    public void addTensionToList(ConditionInstantiated condition, boolean removeEquivalent) {
        boolean equivalent = false;
        
        for (int i=0; i< facts.size(); i++) {
            ConditionInstantiated cond = facts.get(i);
            ComparisonResult res = ConditionComparer.compare(condition, cond);
            //If the tension removes one of the previous tensions, perform it and don't add the new condition
            if (res.isDefeatable(true)) {
                MexicaRepository.getInstance().getStoryAnalyzer().addTensionSolved(cond, condition);
                facts.remove(i);
                equivalent = true;
            }
            else if (res.isEqualOrEquivalent(true, false)) {
                if (removeEquivalent)
                    facts.remove(i);
                equivalent = true;
            }
        }
        
        if (!equivalent) {
            TensionType type = condition.getCondition().getTension();
            boolean addTension = false;
            switch (type) {
                case ClashingEmotions:
                case HealthAtRisk:
                case LifeAtRisk:
                case ActorVampire:
                case LoveCompetition:
                    addTension = true;
                    MexicaRepository.getInstance().getStoryAnalyzer().addSpecialTension(condition);
                    break;
                case PotencialDanger:
                case ActorDead:
                case Prisoner:
                    addTension = true;
                    break;
                case ActorHuman:
                case HealthNormal:
                case LifeNormal:
                case PrisonerFree:
                    addTension = false;
                    break;
                case SocialBurden:
                case SocialDisobedience:
                case SocialThreat:
                    addTension = true;
                    break;
            }
            if (addTension) {
                facts.add(condition);
                storicalFacts.add(condition);
                addKnownCharacter(condition.getCharacterA());
                addKnownCharacter(condition.getCharacterB());
            }
        }
    }
    
    /**
     * Adds a new character positively linked to the owner of the context
     * NOTE: The positive link must be verified before calling this method
     * @param name Name of the character to link
     */
    private void addLinkedCharacter(CharacterName name) {
        if (!name.equals(owner) && 
            !AvatarFactory.getNonElegibleCharacternames().contains(name) &&
            !linkedCharacters.contains(name)) {
                linkedCharacters.add(name);
        }
    }

    private void addKnownCharacter(CharacterName name) {
        if (!name.equals(owner) && 
            !AvatarFactory.getNonElegibleCharacternames().contains(name) &&
            !knownCharacters.contains(name)) {
                knownCharacters.add(name);
        }
    }
    
    /**
     * Obtains a condition with the same characters and the same type
     * @param instance Condition to look for
     * @param onlyPositive Looks only for positive intensities in the relation
     * @return Condition equivalent on the current context
     */
    public ConditionInstantiated getRelationshipWith(ConditionInstantiated instance, boolean onlyPositive) {
        ConditionInstantiated condition = null;
        for (ConditionInstantiated cond : facts) {
            ComparisonResult res = ConditionComparer.compare(instance, cond);
            if (res.isEqualOrEquivalent(true, false) && (!onlyPositive || cond.getCondition().getIntensity() > 0)) {
                condition = cond;
                break;
            }
        }
        return condition;
    }
    
    /**
     * @return the character owner of the context
     */
    public CharacterName getOwner() {
        return owner;
    }

    public AvatarTensions getAvatarTensions() {
        return tensions;
    }
    
    /**
     * Determines if the given condition is already included in the context
     * @param condition
     * @return 
     */
    public boolean containsCondition(ConditionInstantiated condition) {
        for (ConditionInstantiated cond : facts) {
            ComparisonResult result = ConditionComparer.compare(cond, condition); //TODO: Test conditions inverted
            if (result.isEqualOrEquivalent(true, true))
                return true;
        }
        return false;
    }
    
    /**
     * Determines if the given condition removes a tension from the context
     * @param condition
     * @return 
     */
    public boolean removesTension(ConditionInstantiated condition) {
        for (ConditionInstantiated cond : facts) {
            if (cond.getType() == ConditionType.Tension) {
                ComparisonResult result = ConditionComparer.compare(condition, cond);
                if (result.isDefeatable(true))
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Stores a backup of the current facts in the context
     */
    public void storePreviousFacts() {
        previousFacts = new ArrayList<>();
        for (ConditionInstantiated c : facts) {
            previousFacts.add(c);
        }
    }
    
    /**
     * Obtains a list with las backed up facts
     * @return 
     */
    public List<ConditionInstantiated> getPreviousFacts() {
        return previousFacts;
    }
    
    @Override
    public Object clone() {
        AvatarContext ctx = new AvatarContext(owner, story);
        for (CharacterName name : linkedCharacters) {
            ctx.linkedCharacters.add(name);
        }
        for (CharacterName name : knownCharacters) {
            ctx.knownCharacters.add(name);
        }
        for (ConditionInstantiated cond : facts) {
            ctx.facts.add((ConditionInstantiated)cond.clone());
        }
        for (ConditionInstantiated cond : previousFacts) {
            ctx.previousFacts.add((ConditionInstantiated)cond.clone());
        }
        for (ConditionInstantiated cond : storicalFacts) {
            ctx.storicalFacts.add((ConditionInstantiated)cond.clone());
        }
        ctx.tensions = (AvatarTensions)tensions.clone();
        
        return ctx;
    }
}