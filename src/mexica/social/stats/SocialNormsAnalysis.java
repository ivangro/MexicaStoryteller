package mexica.social.stats;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Class to encapsulate the  social norms analysis results to a set of stories
 * @author Ivan Guerrero
 */
public class SocialNormsAnalysis {
    private double socialNormsAvg;
    private double justifiedActionsAvg;
    private double performerCharactersAvg;
    private double receiverCharactersAvg;
    private DecimalFormat formatter;
    
    public SocialNormsAnalysis() {
        formatter = new DecimalFormat("0.00");
    }
    
    public void analyzeResults(List<SocialNormsResult> results) {
        int noStories = results.size();
        for (SocialNormsResult res : results) {
            socialNormsAvg += res.getSocialNormActions();
            justifiedActionsAvg += res.getJustifiedActions();
            performerCharactersAvg += res.getPerformerSocialCharacters();
            receiverCharactersAvg += res.getReceiverSocialCharacters();
        }
        socialNormsAvg = socialNormsAvg / noStories;
        justifiedActionsAvg = justifiedActionsAvg / noStories;
        performerCharactersAvg = performerCharactersAvg / noStories;
        receiverCharactersAvg = receiverCharactersAvg / noStories;
    }

    /**
     * @return the socialNormsAvg
     */
    public double getSocialNormsAvg() {
        return socialNormsAvg;
    }

    /**
     * @return the justifiedActionsAvg
     */
    public double getJustifiedActionsAvg() {
        return justifiedActionsAvg;
    }

    /**
     * @return the performerCharactersAvg
     */
    public double getPerformerCharactersAvg() {
        return performerCharactersAvg;
    }

    /**
     * @return the receiverCharactersAvg
     */
    public double getReceiverCharactersAvg() {
        return receiverCharactersAvg;
    }
    
    @Override
    public String toString() {
        return "Social norms avg: " + formatter.format(getSocialNormsAvg()) + 
               "\nJustified actions avg: " + formatter.format(getJustifiedActionsAvg()) + 
               "\nPerformer characters avg: " + formatter.format(getPerformerCharactersAvg()) + 
               "\nReceiver characters avg: " + formatter.format(getReceiverCharactersAvg());
    }
}