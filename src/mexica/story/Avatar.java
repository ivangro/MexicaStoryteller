package mexica.story;

import java.util.*;
import mexica.CharacterName;
import mexica.core.Position;

/**
 * Class that represents a character in the tale
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class Avatar implements Cloneable {
    private int ID;
    private CharacterName name;
    /** When a character is alive this is his current position, when is dead is nowhere */
    private Position currentPosition;
    /** When a character dies, this is the last position where the caracter was alive */
    private Position lastPosition;
    /** When a character changes his position, previousPosition stores his last position */
    private Position previousPosition;
    /** Represents the initial location of the character */
    private Position birthPosition;
    
    private AvatarContext context;
    private boolean active;
    private boolean alive;
    private boolean isVampire;
    private int yearOfBirth, yearOfDeath;
    private Story story;
    private Set<Position> positionsVisited;

    public boolean isVampire() {
        return isVampire;
    }

    public void becomeVampire() {
        isVampire = true;
    }

    public Avatar(int ID, CharacterName name, Story story) {
        this.story = story;
        this.name = name;
        this.ID = ID;
        currentPosition = Position.NotDefined;
        context = new AvatarContext(name, story);
        active = false;
        alive = true;
        isVampire = name.isVampire(name);
        yearOfBirth = -1;
        yearOfDeath = -1;
        positionsVisited = EnumSet.noneOf(Position.class);
    }
    
    /**
     * Creates a new character with the given name
     * Only one character with this name is available in a tale
     * At the beginning the character is alive and inactive
     * @param name 
     */
    public Avatar(CharacterName name, Story story) {
        this(-1, name, story);
    }
    
    public CharacterName getName() {
        return name;
    }
    
    public int getID() {
        return ID;
    }
        
    /**
     * @return the currentPosition
     * TODO: Validate if last position can be returned when the character is not alive
     */
    public Position getCurrentPosition() {
        if (isAlive())
            return currentPosition;
        else
            return lastPosition;
    }
    
    /**
     * Flag to determine if a character has been employed in a story
     * @return TRUE if the character has been employed in at least one action
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Activates a character to be usable inside the tale
     * NOTE: AnyCharacter, LinkedCharacters A/B and NotDefined character
     * are never active or if the character is not alive cannot be active
     */
    public void activate(int yearOfBirth) {
        active = true;
        this.yearOfBirth = yearOfBirth;
    }

    /**
     * @param currentPosition the currentPosition to set
     */
    public void setCurrentPosition(Position currentPosition) {
        birthPosition = (this.currentPosition == Position.NotDefined) ? currentPosition : birthPosition;
        previousPosition = (this.currentPosition != null) ? this.currentPosition : currentPosition;
        this.currentPosition = currentPosition;
        positionsVisited.add(currentPosition);
    }
    
    public AvatarContext getContext() {
        return context;
    }
    
    /**
     * @return the alive
     */
    public boolean isAlive() {
        return alive;
    }


    
    /**
     * Validates if the character was alive at the given year
     */
    public boolean wasAlive(int year) {
        //When yearOfDeath is -1 means that the character is still alive
        return (yearOfDeath == -1 || yearOfDeath > year) && wasBorn(year);
    }
    
    /**
     * Determines if the avatar was already born at the given year
     * @param year
     * @return 
     */
    public boolean wasBorn(int year) {
        return (yearOfBirth <= year);
    }

    /**
     * Kills the character in the tale and moves him to Nowhere position
     * NOTE: The character cannot be alive again
     */
    public void kill(int yearOfDeath) {
        this.alive = false;
        this.active = true;
        this.lastPosition = this.currentPosition;
        this.currentPosition = Position.NoWhere;
        this.yearOfDeath = yearOfDeath - 1; //Substract 1 because current year has moved to the following year 
    }
    
    /**
     * @return the yearOfBirth
     */
    public int getYearOfBirth() {
        return yearOfBirth;
    }

    /**
     * @param yearOfBirth the yearOfBirth to set
     */
    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    /**
     * @return the yearOfDeath
     */
    public int getYearOfDeath() {
        return yearOfDeath;
    }

    /**
     * @param yearOfDeath the yearOfDeath to set
     */
    public void setYearOfDeath(int yearOfDeath) {
        this.yearOfDeath = yearOfDeath;
    }
    
    public Story getStory() {
        return story;
    }
    
    public Set<Position> getPositionsVisited() {
        return positionsVisited;
    }
        
    public Position getPreviousPosition() {
        return previousPosition;
    }
    
    public Position getBirthPosition() {
        return birthPosition;
    }
    
    @Override
    public String toString() {
        return name.toString().replaceAll("_", " ");
    }
    
    public String toStringWithDate() {
        return toString() + " (" + yearOfBirth + "-" + ((yearOfDeath>=0) ? yearOfDeath : " ") + ")";
    }
    
    /**
     * Compares two characters in the story
     * @param obj
     * @return TRUE if both have the same name
     */
    @Override
    public boolean equals(Object obj) {
        boolean result;
        result = (obj != null && obj instanceof Avatar) ? ((Avatar)obj).name.equals(this.name) : false;
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    @Override
    public Object clone() {
        Avatar a = new Avatar(ID, name, story);
        a.active = active;
        a.alive = alive;
        a.context = (AvatarContext)context.clone();
        a.birthPosition = birthPosition;
        a.currentPosition = currentPosition;
        a.lastPosition = lastPosition;
        a.previousPosition = previousPosition;
        a.yearOfBirth = yearOfBirth;
        a.yearOfDeath = yearOfDeath;
        a.positionsVisited.addAll(positionsVisited);
        
        return a;
    }
}