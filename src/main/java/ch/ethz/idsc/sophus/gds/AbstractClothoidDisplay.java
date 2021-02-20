// code by jph
package ch.ethz.idsc.sophus.gds;

import java.io.Serializable;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.sophus.ply.PolygonNormalize;
import ch.ethz.idsc.sophus.ply.Spearhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

// TODO probably obsolete: instead use Se2 and Se2Covering with different clothoid builders
public abstract class AbstractClothoidDisplay implements ManifoldDisplay, Serializable {
  private static final Tensor SPEARHEAD = PolygonNormalize.of( //
      Spearhead.of(Tensors.vector(-0.217, -0.183, 4.189), RealScalar.of(0.1)), RealScalar.of(0.08));
  private static final Tensor ARROWHEAD = Arrowhead.of(0.2).unmodifiable();

  @Override
  public abstract ClothoidBuilder geodesicInterface();

  @Override // from GeodesicDisplay
  public final int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return SPEARHEAD;
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor p) {
    return v -> v.extract(0, 2);
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
  public final LieExponential lieExponential() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final HsManifold hsManifold() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return (p, q) -> geodesicInterface().curve(p, q).length();
  }

  @Override // from GeodesicDisplay
  public final Biinvariant metricBiinvariant() {
    return null;
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override // from Object
  public abstract String toString();
}
