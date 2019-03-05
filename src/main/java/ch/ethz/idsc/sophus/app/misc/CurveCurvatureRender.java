// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.planar.CurvatureComb;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;

public enum CurveCurvatureRender {
  ;
  private static final Color COLOR_CURVATURE_COMB = new Color(0, 0, 0, 128);
  private static final Scalar COMB_SCALE = DoubleScalar.of(1); // .5 (1 for presentation)
  private static final PathRender PATH_RENDER_CURVE = new PathRender(Color.BLUE, 1.25f);
  private static final PathRender PATH_RENDER_CURVATURE = new PathRender(COLOR_CURVATURE_COMB);

  /** @param refined
   * @param isCyclic
   * @param geometricLayer
   * @param graphics */
  public static void of(Tensor refined, boolean isCyclic, GeometricLayer geometricLayer, Graphics2D graphics) {
    if (0 < refined.length())
      if (Unprotect.dimension1(refined) != 2)
        throw TensorRuntimeException.of(refined);
    PATH_RENDER_CURVE.setCurve(refined, isCyclic).render(geometricLayer, graphics);
    PATH_RENDER_CURVATURE.setCurve(CurvatureComb.of(refined, COMB_SCALE, isCyclic), isCyclic).render(geometricLayer, graphics);
  }
}
