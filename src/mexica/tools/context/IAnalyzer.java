package mexica.tools.context;

import mexica.story.AvatarContext;

/**
 * Interface implemented by every class that analyzes an avatar context
 * looking for special tensions
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public interface IAnalyzer {
    public void analyze(AvatarContext context);
}
