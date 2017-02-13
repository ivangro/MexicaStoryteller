package mexica.tools.context;

import java.util.*;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.social.SocialNormsAnalyzer;
import mexica.story.*;

/**
 * Class that receives a notification when a new condition or tension has been
 * added to the owner's context
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class SpecialTensionAnalyzer implements IStoryContextListener {
    private CharacterName owner;
    private List<IAnalyzer> conditionAnalyzers, tensionAnalizers, storyAnalizers;
    private Story story;
    
    public SpecialTensionAnalyzer(CharacterName owner, Story story) {
        this.story = story;
        this.owner = owner;
        setConditionAnalyzers();
        setTensionAnalyzers();
        setStoryAnalizers();
    }

    private void setConditionAnalyzers() {
        conditionAnalyzers = new ArrayList<>();
        IAnalyzer analyzerPD = new PotentialDangerAnalyzer(story);
        IAnalyzer analyzerCE = new ClashingEmotionsAnalyzer(story);
        IAnalyzer analyzerLC = new LoveCompetitionAnalyzer(story);
        conditionAnalyzers.add(analyzerPD);
        conditionAnalyzers.add(analyzerCE);
        conditionAnalyzers.add(analyzerLC);
    }
    
    private void setTensionAnalyzers() {
        tensionAnalizers = new ArrayList<>();
        IAnalyzer analyzerCD = new CharacterDeadAnalyzer(story);
        IAnalyzer analyzerPCT = new PresenceConditionedTensionAnalyzer(story);
        IAnalyzer analyzerTension = new TensionAnalyzer(story);
        tensionAnalizers.add(analyzerCD);
        tensionAnalizers.add(analyzerPCT);
        tensionAnalizers.add(analyzerTension);
    }
    
    private void setStoryAnalizers() {
        storyAnalizers = new ArrayList<>();
        if (MexicaParameters.ENABLE_SOCIAL_ANALYSIS) {    
            IAnalyzer analyzerSocial = new SocialNormsAnalyzer(story);
            storyAnalizers.add(analyzerSocial);
        }
    }
    
    /**
     * Analyzes the owner's context in search of special tensions
     * @param type Type of notification (Conditions or tensions)
     */
    @Override
    public void notifyListener(ListType type) {
        AvatarContext context = story.getAvatarFactory().getAvatar(owner).getContext();

        switch (type) {
            case Conditions:
                for (IAnalyzer analyzer : conditionAnalyzers) {
                    analyzer.analyze(context);
                }
                break;
            case Tensions:
                for (IAnalyzer analyzer : tensionAnalizers) {
                    analyzer.analyze(context);
                }
                break;
            case StoryUpdated:
                for (IAnalyzer analyzer : storyAnalizers) {
                    analyzer.analyze(context);
                }
                break;
            default:
                break;
        }
    }
}