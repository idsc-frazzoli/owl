// code by jph
package ch.ethz.idsc.sophus.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.crv.d2.CurvatureComb;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;

public enum Curvature2DRender {
  ;
  private static final Color COLOR_CURVATURE_COMB = new Color(0, 0, 0, 128);
  private static final Scalar COMB_SCALE = DoubleScalar.of(-1);

  /** @param curve {{x0, y0}, {x1, y1}, ...}
   * @param isCyclic
   * @param geometricLayer
   * @param graphics */
  public static void of(Tensor curve, boolean isCyclic, GeometricLayer geometricLayer, Graphics2D graphics) {
    of(curve, isCyclic, true, geometricLayer, graphics);
  }

  /** @param curve {{x0, y0}, {x1, y1}, ...}
   * @param isCyclic
   * @param comb
   * @param geometricLayer
   * @param graphics */
  public static void of(Tensor curve, boolean isCyclic, boolean comb, GeometricLayer geometricLayer, Graphics2D graphics) {
    of(curve, isCyclic, comb, COMB_SCALE, geometricLayer, graphics);
  }

  /** Hint: when control points have coordinates with unit "m",
   * scale should have unit "m^2"
   *
   * @param curve {{x0, y0}, {x1, y1}, ...}
   * @param isCyclic
   * @param scale
   * @param geometricLayer
   * @param graphics */
  public static void of(Tensor curve, boolean isCyclic, Scalar scale, GeometricLayer geometricLayer, Graphics2D graphics) {
    of(curve, isCyclic, true, scale, geometricLayer, graphics);
  }

  /** Hint: when control points have coordinates with unit "m",
   * scale should have unit "m^2"
   * 
   * @param curve {{x0, y0}, {x1, y1}, ...}
   * @param isCyclic
   * @param comb
   * @param scale
   * @param geometricLayer
   * @param graphics */
  public static void of(Tensor curve, boolean isCyclic, boolean comb, Scalar scale, GeometricLayer geometricLayer, Graphics2D graphics) {
    if (0 < curve.length())
      if (Unprotect.dimension1(curve) != 2)
        throw TensorRuntimeException.of(curve);
    new PathRender(Color.BLUE, 1.25f).setCurve(curve, isCyclic).render(geometricLayer, graphics);
    if (comb)
      new PathRender(COLOR_CURVATURE_COMB) //
          .setCurve(CurvatureComb.of(curve, scale, isCyclic), isCyclic) //
          .render(geometricLayer, graphics);
  }
}
