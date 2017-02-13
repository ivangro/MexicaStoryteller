package mexica.engagement.parser;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.core.*;
import mexica.engagement.*;
import mexica.social.ConditionStatus;
import mexica.social.SocialAtom;

/**
 * Class to write into a file the atoms available in the system
 * @author Ivan Guerrero
 */
public class AtomWriter {
    public static final String ATOM_CELL_HEADER = "*Tension:%d EmoLin:%d\n";
    /**
     * Atom "["StoryID (, StoryID)* "]" SocialAction?
     */
    public static final String ATOM_DEFINITION = "\t**Atom %s %s\n";
    /**
     * FeaTen=> AD->number  Lr->number  Hr->number  Pd->number  Pr->number  Ce->number  Lc->number
     */
    public static final String ATOM_TENSION_COUNT = "\t\tFeaTen=> AD->%d Lr->%d Hr->%d Pd->%d Pr->%d Ce->%d Lc->%d\n";
    /**
     * FeaEmoLin=>   Type1->number    Type2->number    Type3->number    Type4->number  
     */
    public static final String ATOM_EMOTION_COUNT = "\t\tFeaEmoLin=> Type1->%d Type2->%d Type3->%d Type4->%d\n";
    public static final String ATOM_TENSION_HEADER = "\t\t\tTension=>\n";
    public static final String ATOM_EMOTION_HEADER = "\t\t\tEmoLin=>\n";
    public static final String SOCIAL_ATOM_RELATION_HEADER = "\t\t\tRelation=>\n";
    /**
     * [+-]character([+-]?intensity,emotionType)(:character)?
     */
    public static final String ATOM_EMOTION = "\t\t\t\t%s%s(%d,%d):%s\n";
    /**
     * [+-]tensionType(character):character?
     */
    public static final String ATOM_TENSION = "\t\t\t\t%s%s(%s):%s\n";
    /**
     * LasAct=> character actionName character?
     */
    public static final String ATOM_LAST_ACTION = "\t\t\tLasAct=> %s %s %s\n";
    /**
     * NexAct=> character actionName character?
     */
    public static final String ATOM_NEXT_ACTION = "\t\t\tNexAct=> %s %s %s\n";
    /**
     * Rel=> relationType intensity
     */
    public static final String SOCIAL_ATOM_RELATION = "\t\t\t\tRel=>%s %d\n";
    
    public void saveAtomsInFile(AtomsStore store, String path) {
        FileWriter writer = null;
        try {
            Logger.getGlobal().log(Level.FINE, "Saving atoms in {0}", getClass().getClassLoader().getResource(path));
            writer = new FileWriter(path);
            for (Cell cell : store.getCells()) {
                if (!cell.getAtoms().isEmpty()) {
                    writer.write(String.format(ATOM_CELL_HEADER, cell.getTensionCount(), cell.getEmotionCount()));
                    for (Atom atom : cell.getAtoms()) {
                        saveAtom(atom, writer);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AtomWriter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(AtomWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveAtom(Atom atom, FileWriter writer) throws IOException {
        int[] tensionTypes = new int[]{0,0,0,0,0,0,0};
        int[] emotionTypes = new int[]{0,0,0,0,0};
        String performer, receiver, tension, action, conditionStatus;
        int emotion, intensity;
        
        for (Condition c : atom.getEmotions()) {
            emotionTypes[EmotionType.getEmotionValue(c.getEmotion())-1]++;
        }
        
        for (Tension t : atom.getTensions()) {
            switch(t.getTension()) {
                case ActorDead: tensionTypes[0]++;break;
                case LifeAtRisk: tensionTypes[1]++;break;
                case HealthAtRisk: tensionTypes[2]++;break;
                case PotencialDanger: tensionTypes[3]++;break;
                case Prisoner: tensionTypes[4]++;break;
                case ClashingEmotions: tensionTypes[5]++;break;
                case LoveCompetition: tensionTypes[6]++;break;
            }
        }
        
        if (atom instanceof SocialAtom) {
            SocialAtom socialAtom = (SocialAtom)atom;
            writer.write(String.format(ATOM_DEFINITION, atom.getStoryIDs().toString(), socialAtom.getSocialAction()));
        }
        else {
            writer.write(String.format(ATOM_DEFINITION, atom.getStoryIDs(), ""));
            //Add this line to include the story IDs from which the atoms were created
            //writer.write(" " + atom.getStoryIDs() + "\n");
        }
        writer.write(String.format(ATOM_TENSION_COUNT, tensionTypes[0], tensionTypes[1], tensionTypes[2], 
                                         tensionTypes[3], tensionTypes[4], tensionTypes[5], tensionTypes[6]));
        writer.write(String.format(ATOM_EMOTION_COUNT, emotionTypes[0], emotionTypes[1], emotionTypes[2], emotionTypes[3]));
        writer.write(ATOM_EMOTION_HEADER);
        for (Condition c : atom.getEmotions()) {
            performer = c.getCharacterA();
            receiver = c.getCharacterB();
            emotion = EmotionType.getEmotionValue(c.getEmotion());
            intensity = c.getIntensity();
            conditionStatus = ConditionStatus.obtainAbbreviation(c.getStatus());
            writer.write(String.format(ATOM_EMOTION, conditionStatus, performer, intensity, emotion, receiver));
        }
        writer.write(ATOM_TENSION_HEADER);
        for (Tension t : atom.getTensions()) {
            tension = TensionType.getAbbreviation(t.getTension());
            performer = t.getCharacterA();
            receiver = t.getCharacterB();
            conditionStatus = ConditionStatus.obtainAbbreviation(t.getStatus());
            writer.write(String.format(ATOM_TENSION, conditionStatus, tension, performer, receiver));
        }
        //writer.write(String.format(ATOM_LAST_ACTION, performer, action, receiver));
        for (Action act : atom.getNextActions()) {
            action = act.getActionName();
            performer = act.getCharacters().get(0);
            if (act.getCharacters().size() > 1)
                receiver = act.getCharacters().get(1);
            else
                receiver = "";
            writer.write(String.format(ATOM_NEXT_ACTION, performer, action, receiver));
        }
        
        if (atom instanceof SocialAtom) {
            SocialAtom social = (SocialAtom)atom;
            if (social.isGenderRelation() || social.isSocialRelation()) {
                writer.write(String.format(SOCIAL_ATOM_RELATION_HEADER));
                if (social.isSocialRelation()) 
                    writer.write(String.format(SOCIAL_ATOM_RELATION, "social", social.getSocialRelation()));
                else if (social.isGenderRelation())
                    writer.write(String.format(SOCIAL_ATOM_RELATION, "gender", social.getGenderRelation()));
            }
        }
    }
    
    public void saveStoryAtomRelations(AtomsStore store, String path) {
        FileWriter writer;
        try {
            Logger.getGlobal().log(Level.FINE, "Saving story-atom relations in {0}", getClass().getClassLoader().getResource(path));
            writer = new FileWriter(path);
            for (Cell cell : store.getCells()) {
                if (!cell.getAtoms().isEmpty()) {
                    for (Atom atom : cell.getAtoms()) {
                        writer.write(atom.getAtomLabel() + "\t");
                        writer.write(atom.getStoryIDs() + "\n");
                    }
                }
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
    }
}