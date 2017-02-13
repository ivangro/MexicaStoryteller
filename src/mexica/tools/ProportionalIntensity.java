package mexica.tools;

/**
 * Class to obtains proportional intensities
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class ProportionalIntensity {
    /**
     * First dimension is Lb->B intensity
     * Second dimension is B->A intensity
     * Every number represents Lb->A intensity
     * i.e. matrix[1,6] Lb->B: 1, B->A: 6 => Lb->A: 2
     */
    private static int[][] matrix = 
            {{-1,-1,-1,0,0,0,1},
             {-2,-2,-1,0,0,1,2},
             {-3,-2,-1,0,0,1,1}};
    
    /**
     * Obtains the proportional intensity given two intensitites:
     * getProportionalIntensity(Lb->B, B->A): Lb->A
     * @param intensityLBtoB Intensity from linked character to character B
     * @param intensityBtoA Intensity from character B to character A
     * @return Intensity from linked characer to character A
     */
    public static int getProportionalIntensity(int intensityLBtoB, int intensityBtoA) {
        return matrix[intensityLBtoB-1][intensityBtoA+3];
    }
}
