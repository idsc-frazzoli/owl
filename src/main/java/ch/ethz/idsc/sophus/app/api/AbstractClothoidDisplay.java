// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidInterface;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.ply.Spearhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class AbstractClothoidDisplay implements GeodesicDisplay, Serializable {
  private static final Tensor SPEARHEAD = //
      // Spearhead.of(Tensors.vector(-0.806, -0.250, -0.524), RealScalar.of(0.1)).multiply(RealScalar.of(0.4));
      Spearhead.of(Tensors.vector(-0.842, -0.342, -0.524), RealScalar.of(0.1)).multiply(RealScalar.of(0.35));

  @Override
  public abstract ClothoidInterface geodesicInterface();

  @Override // from GeodesicDisplay
  public final int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return SPEARHEAD;
  }

  @Override // from GeodesicDisplay
  public final Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public final Tensor matrixLift(Tensor p) {
    return Se2Matrix.of(p);
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final Exponential exponential() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final HsExponential hsExponential() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final FlattenLogManifold flattenLogManifold() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return (p, q) -> geodesicInterface().curve(p, q).length();
  }

  @Override // from Object
  public abstract String toString();
}
