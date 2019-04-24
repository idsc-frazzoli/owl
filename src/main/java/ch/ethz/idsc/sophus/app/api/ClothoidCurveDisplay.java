// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.curve.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.group.Se2ParametricDistance;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.planar.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;

public enum ClothoidCurveDisplay implements GeodesicDisplay {
  INSTANCE;
  // ---
  private static final Tensor ARROWHEAD = Arrowhead.of(0.4);
  private static final CurveSubdivision CURVE_SUBDIVISION = //
      new LaneRiesenfeldCurveSubdivision(ClothoidCurve.INSTANCE, 1);
  private static final int DEPTH = 3;

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return ClothoidCurve.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return ARROWHEAD;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return xya;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Utils.toSE2Matrix(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return Se2Group.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return Se2CoveringExponential.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Scalar parametricDistance(Tensor p, Tensor q) {
    Tensor tensor = Nest.of(CURVE_SUBDIVISION::string, Tensors.of(p, q), DEPTH);
    Scalar sum = RealScalar.ZERO;
    Tensor a = tensor.get(0);
    for (int index = 1; index < tensor.length(); ++index)
      sum = sum.add(Se2ParametricDistance.INSTANCE.distance(a, a = tensor.get(index)));
    return sum;
  }

  @Override // from Object
  public String toString() {
    return "Cloth";
  }
}
