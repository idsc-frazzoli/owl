// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.sophus.group.St1Geodesic;
import ch.ethz.idsc.sophus.group.St1Group;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum St1GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;
  // ---
  private static final Tensor PENTAGON = CirclePoints.of(5).multiply(RealScalar.of(0.2));
  
//  Fehlerhaft, aber zurzeit Probleme mit Ausnahme bei lambda = 0 
  private static final ScalarUnaryOperator MAX_X = Max.function(RealScalar.of(0.001));


  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return St1Geodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return PENTAGON;
  }
    
  
  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor point = xya.extract(0, 2);
    point.set(MAX_X, 0);
    return point;
  }
  
  @Override
  public Tensor toPoint(Tensor p) {
    return p;
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
     return Se2Utils.toSE2Translation(p);
//    return Se2Utils.toSE2Translation(toPoint(p));
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return St1Group.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "St1";
  }
}
