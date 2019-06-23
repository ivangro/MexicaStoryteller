package mexica.core;

/**
 * Available tension types in Mexica
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public enum TensionType {
  ActorVampire,
  ActorHuman,
  ActorDead,
  LifeAtRisk,
  HealthAtRisk,
  Prisoner,
  LifeNormal,
  HealthNormal,
  PrisonerFree,
  ClashingEmotions,
  PotencialDanger,
  LoveCompetition, 
  SocialDisobedience,
  SocialBurden,
  SocialThreat, 
  SocialDisobedienceNormal,
  SocialBurdenNormal,
  SocialThreatNormal;

    public static String getAbbreviation(TensionType tension) {
        switch (tension) {
            case ActorVampire: return "Av";
            case ActorHuman: return "Ah";
            case ActorDead: return "Ad";
            case ClashingEmotions: return "Ce";
            case HealthAtRisk: return "Hr";
            case HealthNormal: return "Hn";
            case LifeAtRisk: return "Lr";
            case LifeNormal: return "Ln";
            case LoveCompetition: return "Lc";
            case PotencialDanger: return "Pd";
            case Prisoner: return "Pr";
            case PrisonerFree: return "Pf";
            case SocialDisobedience: return "Sd";
            case SocialBurden: return "Sb";
            case SocialThreat: return "St";
            case SocialDisobedienceNormal: return "Sdn";
            case SocialBurdenNormal: return "Sbn";
            case SocialThreatNormal: return "Stn";
            default: return "";
        }
    }
    
    public static TensionType fromAbbreviation(String abbreviation) {
        switch (abbreviation) {
            case "Dead":
            case "Ad": 
                return ActorDead;
            case "Av": return ActorVampire;
            case "Ah": return ActorHuman;
            case "Ce": return ClashingEmotions;
            case "Hr": return HealthAtRisk;
            case "Hn": return HealthNormal;
            case "Lr": return LifeAtRisk;
            case "Ln": return LifeNormal;
            case "Lc": return LoveCompetition;
            case "Pd": return PotencialDanger;
            case "Pr": return Prisoner;
            case "Pf": return PrisonerFree;
            case "Sb": return SocialBurden;
            case "Sd": return SocialDisobedience;
            case "St": return SocialThreat;
            case "Sbn": return SocialBurdenNormal;
            case "Sdn": return SocialDisobedienceNormal;
            case "Stn": return SocialThreatNormal;
        }
        return null;
    }
    
    /**
     * Determines if the given tension can deactivate a previously added tension
     * @param tension
     * @return 
     */
    public static boolean isTensionDeactivator(TensionType tension) {
        switch (tension) {
            case ActorHuman:
            case HealthNormal:
            case LifeNormal:
            case PrisonerFree:
            case SocialBurdenNormal:
            case SocialDisobedienceNormal:
            case SocialThreatNormal:
                return true;
            default: 
                return false;
        }
    }
    
    /**
     * Determines if the given tension can activate a tension in a story
     * @param tension
     * @return 
     */
    public static boolean isTensionTrigger(TensionType tension) {
        return !isTensionDeactivator(tension);
    }
    
    public static boolean isModerateTension(TensionType tension) {
        switch (tension) {
            case HealthAtRisk:
            case LifeAtRisk:
            case Prisoner:
            case ActorVampire:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isIntenseTension(TensionType tension) {
        switch(tension) {
            case ActorDead:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isSocialTension(TensionType tension) {
        switch (tension) {
            case SocialBurden:
            case SocialDisobedience:
            case SocialThreat:
            case SocialBurdenNormal:
            case SocialDisobedienceNormal:
            case SocialThreatNormal:
                return true;
            default:
                return false;
        }
    }
};