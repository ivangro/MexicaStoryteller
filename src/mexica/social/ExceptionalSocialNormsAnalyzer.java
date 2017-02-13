package mexica.social;

import java.util.*;
import mexica.*;
import mexica.CharacterName;
import mexica.engagement.*;
import mexica.story.*;
import mexica.tools.AvatarFactory;
import mexica.tools.context.IAnalyzer;
import subgraph.*;

/**
 * Class to determine if an action breaks a special social norm
 * @author Ivan Guerrero
 */
public class ExceptionalSocialNormsAnalyzer implements IAnalyzer{
    private Story story;
    private AtomsStore socialAtoms;
    /** Determines if the social norm found in the current action justifies or breaks additional norms */
    private SocialPosconditionMode socialMode;
    
    public ExceptionalSocialNormsAnalyzer(Story story) {
        this.story = story;
        socialAtoms = MexicaRepository.getInstance().getSocialAtoms();
    }
    
    @Override
    public void analyze(AvatarContext context) {
        List<ActionInstantiated> actions = story.getActions();
        ActionInstantiated lastAction = actions.get(story.getCurrentYear()-2);
        
        AvatarFactory avatarFactory = story.getAvatarFactory();
        Avatar owner = avatarFactory.getAvatar(context.getOwner());
        
        //Create a new atom with the previous facts of the context and the current ones
        List<ConditionInstantiated> facts = context.getFacts();
        List<ConditionInstantiated> previousFacts = context.getPreviousFacts();
        
        SocialAtomGenerator socialGenerator = new SocialAtomGenerator();
        SocialAtom socialAtom = socialGenerator.createSocialAtom(facts, previousFacts);
        Atom atom = socialAtom.getAtom();
        
        //Look for similar social atoms
        for (Cell c : socialAtoms.getCells()) {
            for (Atom a : c.getAtoms()) {
                Solution res = AtomComparer.compareInclusion(a, atom);
                if (res != null) {
                    double similarity = res.getNoRemovedEdges() * 100.0 / (a.getEmotions().size() + a.getTensions().size());
                    //If a social atom exists, then the action breaks a social norm
                    if (similarity >= MexicaParameters.SOCIAL_ACAS_CONSTANT) {
                        SocialAtom socialA = (SocialAtom)a;
                        if (!MexicaParameters.ENABLE_SOCIAL_CHARACTER_ANALYSIS || validateCharacters(res, socialAtom, socialA, lastAction)) {
                            SocialAction socialAction = (SocialAction)MexicaRepository.getInstance().getActions().getAction(socialA.getSocialAction());
                            socialMode = socialAction.getSocialPosconditionMode();
                            //If the action breaks a social norm, add the social condition
                            switch (socialMode) {
                                case insert:
                                    lastAction.setSocialStatus(SocialStatus.specialSocialNorm);
                                    ConditionInstantiated socialCondition = SocialNormsUtils.createSocialLink(context.getOwner(), lastAction, socialAction);
                                    context.addCondition(socialCondition);
                                    break;
                                case justify:
                                    lastAction.setSocialStatus(SocialStatus.specialActionJustified);
                                    break;
                            }
//                            for (Avatar avatar : avatarFactory.getAvatarsInPosition(owner.getCurrentPosition())) {
//                                avatar.getContext().addCondition(socialCondition);
//                            }
                            lastAction.getSocialData().addContext(context.getOwner(), true);
                            lastAction.getSocialData().setSocialAction(socialAction);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @param solution The result of comparing a social atom with a character context
     * @param socialAtom The social atom employed for comparison against a character context
     * @param action The action being analyzed
     * @return True if the fixed characters in the social atom match with the characters in the context.<br>
     * If the social atom has a hierarchical or gender context, this relation is also validated.
     */
    private boolean validateCharacters(Solution solution, SocialAtom socialAtom, SocialAtom atom, ActionInstantiated action) {
        boolean res = true;
        int atomRelation = 0, contextRelation = 0;
        
        for (INode atomNode : solution.getMappingKeys()) {
            //If the node in the atom is a character, check if it matches with the context node
            if (atomNode.getID().startsWith("c")) {
                CharacterName atomCharacter = CharacterName.valueFromAbbreviation(atomNode.getID().substring(1));
                INode contextNode = solution.getMapping(atomNode);
                String contextMapping = socialAtom.getMapping().get(atomCharacter);
                res &= (contextNode.getID().equalsIgnoreCase(contextMapping));
            }
        }
        
        if ((atom.isGenderRelation() || atom.isSocialRelation()) && action.getCharactersList().size() == 2) {
            //Validates social relation
            if (atom.isSocialRelation()){
                atomRelation = atom.getSocialRelation();
                contextRelation = MexicaRepository.getInstance().getHierarchyStore().getSocialRelationship(action.getCharactersList());
            }

            if (atom.isGenderRelation()){
                atomRelation = atom.getGenderRelation();
                contextRelation = MexicaRepository.getInstance().getHierarchyStore().getSocialRelationship(action.getCharactersList());
            }
            
            res &= Math.abs(atomRelation) <= Math.abs(contextRelation);
//            if ((atomRelation > 0) ^ (contextRelation > 0)) {
//                res &= Math.abs(atomRelation) <= Math.abs(contextRelation);
//            }
        }
        
        return res;
    }
    
    public SocialPosconditionMode getSocialPosconditionMode() {
        return socialMode;
    }
}