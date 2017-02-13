package mexica.story;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.core.Action;
import mexica.core.CompositeAction;
import mexica.core.Condition;
import mexica.core.ConditionType;
import mexica.core.EmotionType;
import mexica.core.Position;
import mexica.social.SocialAction;
import mexica.social.SocialPosconditionMode;
import mexica.social.SocialStatus;
import mexica.tools.AvatarFactory;
import mexica.tools.ProportionalIntensity;

/**
 * Wrapper for an action
 * All the character variables inside the action are replaced for the 
 * selected characters
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class ActionInstantiated implements Cloneable {
    private Action action;
    private List<CharacterName> charactersList;
    
    private List<ConditionInstantiated> preconditions, posconditions, tensions;
    private List<TextInstantiated> texts;
    
    private InsertionMode insertionMode;
    private SocialData socialData;
    private Position newCharacterPosition;
    /** TRUE when the action has been created by another instance of Mexica Impro */
    private boolean collaborativeAction;
    /** The ID of the Mexica Impro instance that generated this action */
    private int mexicaImproID;
    /** The number of action inside the story */
    private int actionID;
    /** The number of iteration where the action was created */
    private int iterationNumber;
    /**
     * Obtains a fresh copy of the given action
     * @param instance Action to be replicated
     * @return A new action with the same values
     */
    public static ActionInstantiated createActionInstantiated(ActionInstantiated instance) {
        ActionInstantiated act = new ActionInstantiated();
        act.collaborativeAction = instance.collaborativeAction;
        act.mexicaImproID = instance.mexicaImproID;
        act.action = instance.getAction();
        for (CharacterName name : instance.charactersList) {
            act.charactersList.add(name);
        }
        for (Condition cond : act.getAction().getPreconditions()) {
            act.preconditions.add(new ConditionInstantiated(cond, act.charactersList.toArray(new CharacterName[0])));
        }
        for (Condition cond : act.getAction().getPosconditions()) {
            act.posconditions.add(new ConditionInstantiated(cond, act.charactersList.toArray(new CharacterName[0])));
        }
        for (Condition cond : act.getAction().getTensions()) {
            act.tensions.add(new ConditionInstantiated(cond, act.charactersList.toArray(new CharacterName[0])));
        }
        for (String text : act.getAction().getTexts()) {
            act.texts.add(new TextInstantiated(text, act.charactersList.toArray(new CharacterName[0])));
        }
        
        return act;
    }
    
    private ActionInstantiated() {
        charactersList = new ArrayList<>();
        preconditions = new ArrayList<>();
        posconditions = new ArrayList<>();
        tensions = new ArrayList<>();
        texts = new ArrayList<>();
        newCharacterPosition = Position.NotDefined;
        iterationNumber = -1;
    }
    
    public ActionInstantiated(Action action, CharacterName... characters) {
        this.action = action;
        charactersList = new ArrayList<>();
        charactersList.addAll(Arrays.asList(characters));
        
        preconditions = new ArrayList<>();
        posconditions = new ArrayList<>();
        tensions = new ArrayList<>();
        texts = new ArrayList<>();
        newCharacterPosition = Position.NotDefined;
        iterationNumber = -1;
        
        //TODO: Update this to initialize the social data in case the action is a social action
        if (action instanceof CompositeAction)
            initCompositeAction(characters);
        else
            initRegularAction(characters);
    }
    
    private void initRegularAction(CharacterName... characters) {
        for (Condition cond : getAction().getPreconditions()) {
            preconditions.add(new ConditionInstantiated(cond, characters));
        }
        for (Condition cond : getAction().getPosconditions()) {
            posconditions.add(new ConditionInstantiated(cond, characters));
        }
        for (Condition cond : getAction().getTensions()) {
            tensions.add(new ConditionInstantiated(cond, characters));
        }
        for (String text : getAction().getTexts()) {
            texts.add(new TextInstantiated(text, characters));
        }
    }
    
    //TODO: Validate if this version converts % intensity in realised actions
    private void initCompositeAction(CharacterName... characters) {
        CompositeAction act = (CompositeAction)getAction();
        ActionInstantiated contained = act.getContainedAction();
        
        //Adds the posconditions of the composite action plus the posconditions of the contained action
        for (ConditionInstantiated cond : contained.getPosconditions()) {
            if (cond.getCharacterA().equals(CharacterName.Linked_Characters_B) && act.getAvatarFactory() != null) {
                Avatar performer = act.getAvatarFactory().getAvatar(contained.getCharactersList().get(0));
                Avatar receiver = act.getAvatarFactory().getAvatar(contained.getCharactersList().get(1));
                Avatar gossip = act.getAvatarFactory().getAvatar(characters[0]);
                
                //Checks if the receiver of the condition and the owner of the composite action (gossip) are friends
                if (receiver.getContext().getLinkedCharacters(false).contains(gossip)) {
                    Condition c;
                    try {
                        c = (Condition)cond.getCondition().clone();
                        c.setCharacterA("a");
                        c.setCharacterB("b");
                    
                        if (c.getIntensity() == MexicaParameters.PROPORTIONAL_INTENSITY) {
                            c.setIntensity(getProportionalIntensity(performer, receiver, gossip));
                        }
                    
                        ConditionInstantiated cInstantiated = new ConditionInstantiated(c, characters[0], cond.getCharacterB());
                        posconditions.add(cInstantiated);
                    } catch (CloneNotSupportedException cnse) {}
                }
            } 
            else
                posconditions.add(cond);
        }
        
        for (ConditionInstantiated cond : contained.getTensions()) {
            tensions.add(cond);
        }
        
        for (String text : act.getTexts()) {
            texts.add(new TextInstantiated(text, characters));
        }
    }
        
    public List<CharacterName> getCharactersList() {
        return charactersList;
    }
    
    public List<ConditionInstantiated> getPreconditions() {
        return preconditions;
    }
    
    public List<ConditionInstantiated> getPreconditions(ConditionType type) {
        List<ConditionInstantiated> list = new ArrayList<>();
        for (ConditionInstantiated cond : preconditions) {
            if (cond.getType().equals(type))
                list.add(cond);
        }
        return list;
    }
    
    public List<ConditionInstantiated> getPosconditions() {
        return posconditions;
    }
    
    public List<ConditionInstantiated> getTensions() {
        return tensions;
    }
    
    public List<TextInstantiated> getTexts() {
        return texts;
    }
    
    public boolean isSecret() {
        return getAction().isSecret();
    }
    
    public InsertionMode getInsertionMode() {
        return insertionMode;
    }
    
    public void setInsertionMode(InsertionMode insertionMode) {
        this.insertionMode = insertionMode;
    }
    
    /**
     * Defines the new position for a character if it depends on other's character position
     * @param newCharacterPosition 
     */
    public void setNewCharacterPosition(AvatarFactory factory) {
        for (ConditionInstantiated c : getPosconditions()) {
            //If the action will move a character to the position of other character, obtain such position
            if (c.getType() == ConditionType.Position && c.getCondition().getCharactersPosition() != null) {
                newCharacterPosition = factory.getAvatar(c.getCharacterB()).getCurrentPosition();
                break;
            }
        }
    }
    
    /**
     * Position employed to define the new character's position when it depends on other's character position
     * @return 
     */
    public Position getNewCharacterPosition() {
        return newCharacterPosition;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean ans = false;
        if (obj instanceof ActionInstantiated) {
            ActionInstantiated act = ((ActionInstantiated)obj);
            ans = act.getAction().equals(this.getAction()) && act.charactersList.equals(this.charactersList);
        }
        return ans;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.getAction() != null ? this.getAction().hashCode() : 0);
        hash = 23 * hash + (this.charactersList != null ? this.charactersList.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        String text;
        text = (actionID > 0) ? actionID+": " : "";
        text += (socialData != null) ? socialData.toString() : "";
        text += charactersList.get(0) + " " + getAction().toString();
        if (charactersList.size() > 1 && !(getAction() instanceof CompositeAction))
            text += " " + charactersList.get(1);
        return text;
    }
    
    public String toStringWithoutFormat() {
        String text;
        text = charactersList.get(0) + " " + action.toStringWithoutFormat();
        if (charactersList.size() > 1 && !(getAction() instanceof CompositeAction))
            text += " " + charactersList.get(1);
        return text;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @return the breaksSocialNorm
     */
    public boolean breaksSocialNorm() {
        return (socialData != null && socialData.breaksSocialNorm());
    }
    
    public SocialStatus getSocialStatus() {
        return (socialData != null) ? socialData.getSocialStatus() : SocialStatus.regularAction;
    }
    
    public void setSocialAction(ActionInstantiated action) {
        if (socialData == null)
            socialData = new SocialData();
        SocialAction socialAction = (SocialAction)action.getAction();
        socialData.setSocialAction(socialAction);
        //Adds the context of the first character to the broken social norms' context if the action inserts a new social link
        socialData.addContext(action.getCharactersList().get(0), 
                              socialAction.getSocialPosconditionMode() == SocialPosconditionMode.insert);
    }

    public void setSocialStatus(SocialStatus status) {
        if (socialData == null)
            socialData = new SocialData();
        socialData.setSocialStatus(status);
    }
                
    public SocialData getSocialData() {
        return socialData;
    }

    /**
     * Resets the social status value to the initial value inside the social data
     */
    public void resetSocialStatus() {
        if (socialData != null)
            socialData.resetSocialStatus();
    }
    
    public boolean isCollaborativeAction() {
        return collaborativeAction;
    }
    
    public void setCollaborativeAction(boolean collaborativeAction) {
        this.collaborativeAction = collaborativeAction;
    }

    /**
     * @return the mexicaImproID
     */
    public int getMexicaImproID() {
        return mexicaImproID;
    }

    /**
     * @param mexicaImproID the mexicaImproID to set
     */
    public void setMexicaImproID(int mexicaImproID) {
        this.mexicaImproID = mexicaImproID;
    }

    private int getProportionalIntensity(Avatar performer, Avatar receiver, Avatar gossip) throws NullPointerException {
        Condition c = new Condition();
        c.setConditionType(ConditionType.Emotion);
        c.setCharacterA("a");
        c.setCharacterB("b");
        c.setEmotionType(EmotionType.getAnyEmotion());  
        c.setIntensity(MexicaParameters.PROPORTIONAL_INTENSITY);
        int intensity, intensityGossipReceiver, intensityReceiverPerformer;
        ConditionInstantiated cond = new ConditionInstantiated(c, gossip.getName(), receiver.getName());
        ConditionInstantiated gossipReceiver = gossip.getContext().getRelationshipWith(cond, true);
        intensityGossipReceiver = gossipReceiver.getCondition().getIntensity();

        cond = new ConditionInstantiated(c, receiver.getName(), performer.getName());
        ConditionInstantiated receiverPerformer = receiver.getContext().getRelationshipWith(cond, false);
        try {
            intensityReceiverPerformer = receiverPerformer.getCondition().getIntensity();
        } catch (Exception e) {
            intensityReceiverPerformer = -3;
        }

        intensity = ProportionalIntensity.getProportionalIntensity(intensityGossipReceiver, intensityReceiverPerformer);
        return intensity;
    }
    
    public void setActionID(int actionID) {
        this.actionID = actionID;
    }
    
    public int getActionID() {
        return actionID;
    }
    
    public void setIterationNumber(int iteration) {
        this.iterationNumber = iteration;
    }
    
    public int getIterationNumber() {
        return iterationNumber;
    }
}