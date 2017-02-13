package mexica.tools;

import java.util.*;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.core.Position;
import mexica.story.*;
import mexica.tools.avatar.AvatarCaretaker;
import mexica.tools.avatar.AvatarMemento;

/**
 * Factory of characters
 * Since there's only one character with the same name inside a story
 * This class ensures that this happens
 * Also is responsible for instantiating the characters, activating them
 *  and establishing their initial location
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class AvatarFactory extends AvatarCaretaker {    
    private Position defaultPosition;
    private List<Avatar> characters;
    private static final List<CharacterName> availableCharacterNames;
    private static final List<CharacterName> nonElegibleCharacterNames;
    private Story story;
    
    static {
        availableCharacterNames = new ArrayList<>(Arrays.asList(CharacterName.values()));
        
        nonElegibleCharacterNames = new ArrayList<>(Arrays.asList(new CharacterName[] {
            CharacterName.Any_Character, CharacterName.Linked_Characters_A, 
            CharacterName.Linked_Characters_B, CharacterName.Not_Defined}));
        
        availableCharacterNames.removeAll(nonElegibleCharacterNames);
    }
    
    public AvatarFactory(Story story) {
        this.story = story;
        characters = new ArrayList<>();
        defaultPosition = Position.NotDefined;
    }
    
    @Override
    public void restart() {
        super.restart();
        characters = new ArrayList<>();
    }
    
    /**
     * Verifies that a character with the given name already exists and retrieves him
     *  if hasn't been created yet, instantiates a new one
     * @param name Character's name
     * @return The avatar for the given character's name (the first time is created is inactive)
     */
    public Avatar getAvatar(CharacterName name, boolean instantiate) {
        Avatar avatar = new Avatar(name, story);
        int index = characters.indexOf(avatar);
        if (index >= 0)
            avatar = characters.get(index);
        else if (instantiate)
            characters.add(avatar);

        return avatar;
    }
    
    public Avatar getAvatar(CharacterName name) {
        return getAvatar(name, false);
    }
    
    /**
     * Verifies that the characters are active. If not, they are set to the default location
     *  unless there's already an active character, in such case, the rest are set at the same location
     * @param names Names of the characters that will be activated
     * @param multiplePositions If true, characters in different positions are available
     * @throws InvalidCharacterLocationsException When two active characters are in different locations
     */
    public void activateCharacters(CharacterName[] names, boolean multiplePositions) throws InvalidCharacterException {
        activateCharacters(names, multiplePositions, false);
    }
    
    /**
     * Verifies that the characters are active. If not, they are set to the default location
     *  unless there's already an active character, in such case, the rest are set at the same location
     * @param names Names of the characters that will be activated
     * @param multiplePositions If true, characters in different positions are available
     * @param realisedAction If true, only the first character is considered
     * @throws InvalidCharacterLocationsException When two active characters are in different locations
     */
    public void activateCharacters(CharacterName[] names, boolean multiplePositions, boolean realisedAction) 
            throws InvalidCharacterException {
        if (realisedAction) {
            names = new CharacterName[]{names[0]};
        }
        
        Position position = Position.NotDefined;
        
        for (CharacterName name : names) {
            Avatar avatar = getAvatar(name, true);
            if (avatar != null && avatar.isActive()) {
                if (position.equals(Position.NotDefined))
                    position = avatar.getCurrentPosition();
                else {
                    if (!position.equals(avatar.getCurrentPosition()) && !multiplePositions)
                        if (MexicaParameters.ENABLE_STORY_EXCEPTIONS)
                            throw new InvalidCharacterException("Characters are in different locations: " + 
                                        Arrays.toString(names) + "\t" + position + " " + avatar.getCurrentPosition());
                }
            }
        }
        
        if (position.equals(Position.NotDefined))
            position = defaultPosition;
        
        for (CharacterName name : names) {
            Avatar avatar = getAvatar(name);
            if (!avatar.isActive() && !getNonElegibleCharacternames().contains(name))
                avatar.activate(story.getCurrentYear());
            if (avatar.isActive() && !multiplePositions)
                avatar.setCurrentPosition(position);
        }
    }
    
    /**
     * Obtains a list with all the characters that have been used inside the story
     * @return 
     */
    public List<Avatar> getActiveCharacters() {
        return getActiveCharacters(true, story.getCurrentYear()-1);
    }
    
    public List<Avatar> getActiveCharacters(boolean includeDeads, int year) {
        List<Avatar> list = new ArrayList<>();
        for (Avatar avatar : characters) {
            if (avatar.isActive() && avatar.wasBorn(year) && (includeDeads || avatar.wasAlive(year))) //avatar.wasBorn(year) && (includeDeads || avatar.wasAlive(year)))
                list.add(avatar);
        }
        return list;
    }
    
    /**
     * Obtains a list with all the elegible characters
     * @return List of character names that ca be instantiated
     */
    public List<CharacterName> getAvailableCharacterNames() {
        List<CharacterName> availables = new ArrayList<>();
        for (CharacterName name : availableCharacterNames) {
            Avatar avatar = new Avatar(name, story);
            if (characters.contains(avatar)) {
                avatar = getAvatar(name);
                if (avatar.isActive() && avatar.wasAlive(story.getCurrentYear()))
                    availables.add(name);
            } else
                availables.add(name);
                
        }
        return availables;
    }
    
    /**
     * Obtains a list with all the non elegible characters
     * @return List of character names that can't be instantiated
     */
    public static List<CharacterName> getNonElegibleCharacternames() {
        return nonElegibleCharacterNames;
    }
    
    /**
     * Obtains all the characters in the given position
     * @param position Position employed for filtering
     * @return List of characters located in the given position
     */
    public List<Avatar> getAvatarsInPosition(Position position) {
        List<Avatar> list = new ArrayList<>();
        for (Avatar avatar : characters) {
            if (avatar.getCurrentPosition().equals(position)) 
                list.add(avatar);
        }
        
        return list;
    }
    
    public void setDefaultPosition(Position position) {
        this.defaultPosition = position;
    }
    
    public Position getDefaultPosition() {
        return defaultPosition;
    }
    
    /**
     * Calls every character context to look for special tensions
     */
    public void validateAdditionalTensions() {
        //TODO: Improve performance to execute only once. 
        //This is because killed character's context is not always the first to be updated
        //FIX: Call first the context of a dead character
        for (Avatar avatar : getActiveCharacters()) {
            avatar.getContext().notifyListeners(IStoryContextListener.ListType.Tensions);
            avatar.getContext().notifyListeners(IStoryContextListener.ListType.Conditions);
            avatar.getContext().notifyListeners(IStoryContextListener.ListType.StoryUpdated);
        }
        
        for (Avatar avatar : getActiveCharacters()) {
            avatar.getContext().notifyListeners(IStoryContextListener.ListType.Tensions);
            avatar.getContext().notifyListeners(IStoryContextListener.ListType.Conditions);
            avatar.getContext().notifyListeners(IStoryContextListener.ListType.StoryUpdated);
        }
    }

    @Override
    public void saveToMemento() {
        super.addMemento(new AvatarMemento(characters));
    }

    @Override
    public void restoreFromMemento() {
        AvatarMemento lastMemento = getLastMemento();
        if (lastMemento != null)
            characters = lastMemento.getState();
    }
}