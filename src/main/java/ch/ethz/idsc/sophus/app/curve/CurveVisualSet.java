// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.BasicStroke;
import java.awt.Stroke;

import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.sophus.planar.SignedCurvature2D;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Accumulate;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.FoldList;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Mod;

/* package */ class CurveVisualSet {
  private static final Mod MOD_DISTANCE = Mod.function(Pi.TWO, Pi.VALUE.negate());
  static final Stroke PLOT_STROKE = new BasicStroke(1.5f);
  // ---
  private final VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(192));
  private final Tensor differences;
  private final Tensor differencesNorm;
  private final Tensor curvature;
  private final Tensor arcLength0;
  private final Tensor arcLength1;

  /** @param points {{x1, y1}, {x2, y2}, ..., {xn, yn}} */
  public CurveVisualSet(Tensor points) {
    differences = Differences.of(points);
    differencesNorm = Tensor.of(differences.stream().map(Norm._2::ofVector));
    curvature = SignedCurvature2D.string(points);
    arcLength0 = Accumulate.of(differencesNorm);
    arcLength1 = FoldList.of(Tensor::add, RealScalar.ZERO, differencesNorm);
  }

  public void addCurvature() {
    VisualRow visualRow = visualSet.add(getArcLength1(), curvature);
    visualRow.setLabel("curvature");
    visualRow.setStroke(PLOT_STROKE);
  }

  public void addArcTan(Tensor refined) {
    Tensor arcTan2D = Tensor.of(differences.stream().map(ArcTan2D::of));
    Tensor extract = refined.get(Tensor.ALL, 2).extract(0, arcTan2D.length());
    VisualRow visualRow = visualSet.add(arcLength0, arcTan2D.subtract(extract).map(MOD_DISTANCE));
    visualRow.setLabel("arcTan[dx, dy] - phase");
    visualRow.setStroke(PLOT_STROKE);
  }

  public VisualSet visualSet() {
    return visualSet;
  }

  public Tensor getArcLength1() {
    return arcLength1;
  }
}
