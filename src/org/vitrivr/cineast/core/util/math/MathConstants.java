package org.vitrivr.cineast.core.util.math;

/**
 * @author rgasser
 * @version 1.0
 * @created 15.03.17
 */
public final class MathConstants {
    /** Definition of the golden ratio PHI. */
    public static final double PHI = ((1.0+Math.sqrt(5.0))/2.0);

    /** Defines the vertices of a regular Dodecahedron. */
    public static final double[][] VERTICES_3D_DODECAHEDRON = {
            {1,1,1},{-1,-1,-1},{1,-1,-1},{-1,-1,1},
            {-1,1,-1},{-1,1,1},{1,-1,1},{1,1,-1},
            {0,1/PHI,PHI},{0,-1/PHI,PHI},{0,1/PHI,-PHI},{0,-1/PHI,-PHI},
            {1/PHI,PHI,0},{-1/PHI,PHI,0},{1/PHI,-PHI,0},{-1/PHI,-PHI,0},
            { PHI,0,1/PHI},{- PHI,0,1/PHI},{ PHI,0,-1/PHI},{-PHI,0,-1/PHI}
    };

    /**
     * Private constructor; no instantiation possible!
     */
    private MathConstants() {

    }
}
