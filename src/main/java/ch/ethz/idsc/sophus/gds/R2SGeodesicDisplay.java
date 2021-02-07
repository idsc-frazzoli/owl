// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.r2s.R2SBiinvariantMean;
import ch.ethz.idsc.sophus.lie.r2s.R2SExponential;
import ch.ethz.idsc.sophus.lie.r2s.R2SGeodesic;
import ch.ethz.idsc.sophus.lie.r2s.R2SGroup;
import ch.ethz.idsc.sophus.lie.r2s.R2SManifold;
import ch.ethz.idsc.sophus.lie.rn.RnTransport;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

public enum R2SGeodesicDisplay implements GeodesicDisplay {
  INSTANCE;

  private static final Tensor ARROWHEAD = Arrowhead.of(0.2).unmodifiable();

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return R2SGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return ARROWHEAD;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xym = xya.copy();
    xym.set(So2.MOD, 2);
    return xym;
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor xyz) {
    return null;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Matrix.of(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return R2SGroup.INSTANCE;
  }

  @Override
  public LieExponential lieExponential() {
    return R2SManifold.HS_EXP;
  }

  @Override // from GeodesicDisplay
  public HsExponential hsExponential() {
    return LieExponential.of(lieGroup(), R2SExponential.INSTANCE);
  }

  @Override // from GeodesicDisplay
  public HsTransport hsTransport() {
    return RnTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public boolean isMetricBiinvariant() {
    return false;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return R2SBiinvariantMean.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public VectorLogManifold vectorLogManifold() {
    return R2SManifold.INSTANCE;
  }

  @Override
  public LineDistance lineDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public String toString() {
    return "R2S";
  }
}
