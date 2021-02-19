// code by jph
package ch.ethz.idsc.sophus.gui.ren;

import java.awt.BasicStroke;
import java.awt.Stroke;

import ch.ethz.idsc.sophus.crv.d2.Curvature2D;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.d2.ArcTan2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Accumulate;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.FoldList;
import ch.ethz.idsc.tensor.fig.VisualRow;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;

public class CurveVisualSet {
  private static final Stroke PLOT_STROKE = new BasicStroke(1.5f);
  // ---
  private final Tensor differences;
  private final Tensor differencesNorm;
  private final Tensor curvature;
  private final Tensor arcLength0;
  private final Tensor arcLength1;

  /** @param points {{x1, y1}, {x2, y2}, ..., {xn, yn}} */
  public CurveVisualSet(Tensor points) {
    differences = Differences.of(points);
    differencesNorm = Tensor.of(differences.stream().map(Vector2Norm::of));
    curvature = Curvature2D.string(points);
    arcLength0 = Accumulate.of(differencesNorm);
    arcLength1 = FoldList.of(Tensor::add, RealScalar.ZERO, differencesNorm);
  }

  public VisualRow addCurvature(VisualSet visualSet) {
    VisualRow visualRow = visualSet.add(getArcLength1(), curvature);
    visualRow.setStroke(PLOT_STROKE);
    return visualRow;
  }

  public void addArcTan(VisualSet visualSet, Tensor refined) {
    Tensor arcTan2D = Tensor.of(differences.stream().map(ArcTan2D::of));
    Tensor extract = refined.get(Tensor.ALL, 2).extract(0, arcTan2D.length());
    VisualRow visualRow = visualSet.add(arcLength0, arcTan2D.subtract(extract).map(So2.MOD));
    visualRow.setLabel("arcTan[dx, dy] - phase");
    visualRow.setStroke(PLOT_STROKE);
  }

  public Tensor getArcLength1() {
    return arcLength1;
  }
}
