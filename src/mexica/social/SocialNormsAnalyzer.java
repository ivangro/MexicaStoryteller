package mexica.social;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.core.Condition;
import mexica.core.ConditionType;
import mexica.core.EmotionType;
import mexica.core.Position;
import mexica.core.TensionType;
import mexica.groups.HierarchyStore;
import mexica.story.*;
import mexica.tools.AvatarFactory;
import mexica.tools.context.IAnalyzer;

/**
 * Class to determine if a given action breaks a social norm inside a story
 * @author Ivan Guerrero
 */
public class SocialNormsAnalyzer implements IAnalyzer {
    private Story story;
    private ExceptionalSocialNormsAnalyzer exceptionalAnalyzer;
    private static final int SOCIAL_THREAT_INTENSITY = -3;
    
    public SocialNormsAnalyzer(Story story) {
        this.story = story;
        exceptionalAnalyzer = new ExceptionalSocialNormsAnalyzer(story);
    }
    
    /**
     * Determine if the last action added to the story breaks a social norm.
     * An action breaks a social norm when unjustifiably triggers a tension with social impact (moderate or intense),
     * or when unjustifiably triggers a groupal negative emotion against the action performer.
     * @param context 
     */
    @Override
    public void analyze(AvatarContext context) {
        //Social conditions to be added to the contexts
        List<ConditionInstantiated> socialConditions = new ArrayList<>();

        List<ActionInstantiated> actions = story.getActions();
        ActionInstantiated lastAction = actions.get(story.getCurrentYear()-2);
        
        AvatarFactory avatarFactory = story.getAvatarFactory();
        Avatar owner = avatarFactory.getAvatar(context.getOwner());
        boolean characterDead = false;
        
        //If the context owner was at the place where the action took place, analyse social norms
        Avatar performer = avatarFactory.getAvatar(lastAction.getCharactersList().get(0));
        Position actionPosition = performer.getPreviousPosition();
        if (actionPosition != owner.getCurrentPosition() && !lastAction.getCharactersList().contains(owner.getName()))
            return;
        
        //When a tension is deactivated, the groupal response is socially accepted because is positive
        boolean deactivatesTension = false;
        boolean isJustified = true;
        
        //Obtain the tensions generated by the last action
        for (ConditionInstantiated cond : lastAction.getTensions()) {
            deactivatesTension |= TensionType.isTensionDeactivator(cond.getCondition().getTension());
            //Evaluate if the tension is justified or not
            isJustified = evaluateTensionJustification(cond, context, lastAction);
            //If a Ad tension is found, don't evaluate negative emotion
            if (cond.getCondition().getTension() == TensionType.ActorDead)
                characterDead = true;
            //If the tension is unjustified, the action breaks a social norm and a social tension is added to the context
            if (!isJustified) {
                socialConditions.addAll(getSocialRelations(owner, cond, context));
                //lastAction.setSocialStatus(SocialStatus.breakSocialNorm);
                //lastAction.getSocialData().addContext(context.getOwner(), true);
                Logger.getGlobal().log(Level.FINE, "Social norms broken with action {0} in context {1} due to Tension", 
                                                    new Object[] {lastAction, context.getOwner()});
                break;
            }
        }
        
        //Analyze the emotions to detect groupal negative response against the action performer
        if (!characterDead && isJustified) {
            for (ConditionInstantiated cond : lastAction.getPosconditions()) {
                if (cond.getType() == ConditionType.Emotion && !deactivatesTension) {
                    isJustified = evaluateEmotionJustification(cond, context, lastAction);

                    //If the action is not justified, look for all the characters in the location and add the social link
                    if (!isJustified) {
                        socialConditions.addAll(getSocialRelations(owner, cond, context));
                        //lastAction.setSocialStatus(SocialStatus.breakSocialNorm);
                        //lastAction.getSocialData().addContext(context.getOwner(), true);
                        Logger.getGlobal().log(Level.FINE, "Social norms broken with action {0} in context {1} due to Groupal Response", 
                                                            new Object[] {lastAction, context.getOwner()});
                        break;
                    }
                }
            }
        }
        
        if (MexicaParameters.ENABLE_SPECIAL_SOCIAL_NORMS) {
            exceptionalAnalyzer.analyze(context);
            SocialPosconditionMode socialMode = exceptionalAnalyzer.getSocialPosconditionMode();
            if (socialMode != null) {
                switch (socialMode) {
                    case justify:
                        isJustified = true; break;
                    case insert:
                        isJustified = false; break;
                }
            }
        }
        else 
            isJustified = false;
        
        if (!isJustified) {
            addSocialConditions(owner, socialConditions, avatarFactory, lastAction);
        }
    }
    
    /**
     * Determines if the given condition is justified or not depending on the context
     * @param cond Condition to be evaluated
     * @param context Context where the condition will be evaluated
     * @return TRUE if the action is justified
     */
    private boolean evaluateTensionJustification(ConditionInstantiated cond, AvatarContext context, ActionInstantiated lastAction) {
        //If the tension deactivates another tension, the action is justified
        if (TensionType.isTensionDeactivator(cond.getCondition().getTension()))
            return true;
        
        CharacterName receiver, performer, actionReceiver;
        receiver = cond.getCharacterA();
        performer = cond.getCharacterB();
        if (lastAction.getCharactersList().size() == 2)
            actionReceiver = lastAction.getCharactersList().get(1);
        else
            return true;
        
        //If the tension was generated by the same character who receives it, the action is justified
        if (AvatarFactory.getNonElegibleCharacternames().contains(receiver) ||
            AvatarFactory.getNonElegibleCharacternames().contains(performer))
            return true;
        
        //Verify if exists in the context a similar tension against the performer
        List<CharacterName> linkedCharacters = new ArrayList<>();
        Avatar performerAvatar = story.getAvatarFactory().getAvatar(performer);
        for (Avatar a : story.getAvatarFactory().getActiveCharacters()) {
            if (a.getContext().getLinkedCharacters().contains(performerAvatar))
                linkedCharacters.add(a.getName());
        }
        linkedCharacters.add(performer);
        
        //Look for a similar tension in the context or a social link against the performer if no the action didn't kill a character
        for (ConditionInstantiated c : context.getPreviousFacts()) {
            if (c.getType() == ConditionType.Tension && TensionType.isModerateTension(c.getCondition().getTension())) {
                //Performer and receiver characters for the context tension
                CharacterName perf, rec;
                rec = c.getCharacterA();
                perf = c.getCharacterB();
                if (linkedCharacters.contains(rec) && receiver == perf) {
                    lastAction.setSocialStatus(SocialStatus.actionJustified);
                    lastAction.getSocialData().addContext(context.getOwner(), false);
                    //System.out.printf("Action justified with %s in context %s\n", lastAction, context.getOwner());
                    return true;
                }
            }
            else if ((cond.getCondition().getTension() != TensionType.ActorDead) &&
                    (c.getType() == ConditionType.Emotion && c.getCondition().getEmotion() == EmotionType.getSocialEmotion())) {
                CharacterName perf, rec;
                perf = c.getCharacterA();
                rec = c.getCharacterB();
                if (linkedCharacters.contains(perf) && rec == actionReceiver) {
                    lastAction.setSocialStatus(SocialStatus.actionJustified);
                    lastAction.getSocialData().addContext(context.getOwner(), false);
                    //System.out.printf("Action justified with %s in context %s\n", lastAction, context.getOwner());
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * When a groupal negative emotion is triggered, looks for a previous action that justifies it.<br>
     * The action is justified when a previous tension against the action performer was previously in the story.
     * @param cond The condition to be analyzed
     * @param actionReceiver The character who performed the action
     * @param context The context being analyzed
     * @return TRUE if the condition is justified inside the context
     */
    private boolean evaluateEmotionJustification(ConditionInstantiated cond, AvatarContext context, ActionInstantiated lastAction) {
        CharacterName actionReceiver, performer, receiver;
        performer = cond.getCharacterA();
        receiver = cond.getCharacterB();
        if (lastAction.getCharactersList().size() == 2)
            actionReceiver = lastAction.getCharactersList().get(1);
        else
            return true;
        
        if (performer != CharacterName.Linked_Characters_B &&
            performer != CharacterName.Linked_Characters_A)
            return true;
        
        //Verify if exists in the context a previous tension against the receiver or one of his linked characters
        List<CharacterName> linkedCharacters = new ArrayList<>();
        Avatar receiverAvatar = story.getAvatarFactory().getAvatar(receiver);
        for (Avatar a : story.getAvatarFactory().getActiveCharacters()) {
            if (a.getContext().getLinkedCharacters().contains(receiverAvatar))
                linkedCharacters.add(a.getName());
        }
        linkedCharacters.add(receiver);
        
        //Look for a previous tension in the context or a social emotion against the action performer
        for (ConditionInstantiated c : context.getPreviousFacts()) {
            if (c.getType() == ConditionType.Tension && TensionType.isModerateTension(c.getCondition().getTension())) {
                //Performer and receiver characters for the context tension
                CharacterName perf, rec;
                rec = c.getCharacterA();
                perf = c.getCharacterB();
                if (linkedCharacters.contains(rec) && perf == actionReceiver) {
                    lastAction.setSocialStatus(SocialStatus.actionJustified);
                    //System.out.printf("Action justified %s with condition %s in context %s\n", lastAction, c, context.getOwner());
                    return true;
                }
            }
            else if (c.getType() == ConditionType.Emotion && c.getCondition().getEmotion() == EmotionType.getSocialEmotion()) {
                CharacterName perf, rec;
                perf = c.getCharacterA();
                rec = c.getCharacterB();
                if (linkedCharacters.contains(perf) && rec == actionReceiver) {
                    lastAction.setSocialStatus(SocialStatus.actionJustified);
                    //System.out.printf("Action justified %s with condition %s in context %s\n", lastAction, c, context.getOwner());
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Creates a social link between the given characters. A social link is created when the first given character 
     * detects that the second character has broken a social norm
     * @param owner The context's owner where additional social links may exist
     * @param condition The condition that triggered the social link (employed to determine the intensity of the link)
     * @param names Character names (Observer, Social norm broker)
     * @return The condition with the social link
     */
    public static ConditionInstantiated createSocialLink(Avatar owner, ConditionInstantiated condition, CharacterName... names) {
        //Look up a previous social link inside the context
        int previousIntensity = 0;
        int intensity = 0;
        for (ConditionInstantiated c : owner.getContext().getFacts()) {
            switch (c.getType()) {
                case Emotion:
                    if (c.getCondition().getEmotion() == EmotionType.getSocialEmotion()) {
                        //TODO: Validate positive social emotions
                        if (c.getCharacterB() == names[1] && c.getCharacterA() == names[0] && c.getCondition().getIntensity() < 0) {
                            if (!c.getAction().equals(condition.getAction()))
                                previousIntensity = c.getCondition().getIntensity();
                        }
                    }
                    break;
            }
        }
        
        Condition cond = new Condition();
        cond.setConditionType(ConditionType.Emotion);
        cond.setEmotionType(EmotionType.getSocialEmotion());
        switch (condition.getType()) {
            case Tension:
                if (TensionType.isModerateTension(condition.getCondition().getTension()))
                    intensity = (previousIntensity < 0) ? previousIntensity-1 : -1;
                else if (TensionType.isIntenseTension(condition.getCondition().getTension()))
                    intensity = (previousIntensity < 0) ? previousIntensity-2 : -2;
                break;
            case Emotion:
                intensity = (previousIntensity < 0) ? previousIntensity-1 : -1;
                break;
        }
        cond.setIntensity(Math.max(intensity, -3));
        cond.setCharacterA("a");
        cond.setCharacterB("b");
        ConditionInstantiated instance = new ConditionInstantiated(cond, names);
        instance.setAction(condition.getAction());
        return instance;
    }

    /**
     * Adds social relations to the context of all the characters in the same position of the owner to represent the
     * rupture of a social norm
     * @param owner The context owner
     * @param condition The condition that triggered the social link
     * @param context The context being analyzed
     */
    private List<ConditionInstantiated> getSocialRelations(Avatar owner, ConditionInstantiated condition, AvatarContext context) {
        List<ConditionInstantiated> conditions = new ArrayList<>();
        //Obtains the social emotion to represent that a social norm has been broken
        ConditionInstantiated socialEmotion = createSocialLink(owner, condition, context.getOwner(), condition.getCharacterB());
        conditions.add(socialEmotion);
        //Analyze if additional tensions must be added to the context of the characters in the same position
        HierarchyStore hierarchyStore = MexicaRepository.getInstance().getHierarchyStore();
        List<CharacterName> characters = new ArrayList<>();
        characters.add(context.getOwner());
        characters.add(condition.getCharacterB());
        int socialRelationship = hierarchyStore.getSocialRelationship(characters);
        
        if (socialRelationship > 0)
            conditions.add(createCondition(TensionType.SocialDisobedience, condition.getCharacterB(), context.getOwner()));
        else if (socialRelationship < 0)
            conditions.add(createCondition(TensionType.SocialBurden, condition.getCharacterB(), context.getOwner()));
        
        if (socialEmotion.getCondition().getIntensity() <= SOCIAL_THREAT_INTENSITY)
            conditions.add(createCondition(TensionType.SocialThreat, condition.getCharacterB(), context.getOwner()));
        
        return conditions;
    }
    
    /**
     * Adds the given conditions to those characters in the same locations as the owner
     * @param owner The owner of the context being analized
     * @param conditions The conditions to add
     * @param avatarFactory The factory of the characters in the story
     */
    private void addSocialConditions(Avatar owner, List<ConditionInstantiated> conditions, AvatarFactory avatarFactory, ActionInstantiated action) {
        if (!conditions.isEmpty()) {
            action.setSocialStatus(SocialStatus.breakSocialNorm);
            action.getSocialData().addContext(owner.getName(), true);
            for (Avatar avatar : avatarFactory.getAvatarsInPosition(owner.getCurrentPosition())) {
                for (ConditionInstantiated cond : conditions) {
                    cond.setAction(action);
                    avatar.getContext().addCondition(cond);
                }
            }
        }
    }
    
    public static ConditionInstantiated createCondition(TensionType tension, CharacterName... characterNames) {
        Condition cond = new Condition();
        cond.setConditionType(ConditionType.Tension);
        cond.setCharacterA("a");
        cond.setCharacterB("b");
        cond.setTensionType(tension);
        ConditionInstantiated instance = new ConditionInstantiated(cond, characterNames);
        
        return instance;
    }
}