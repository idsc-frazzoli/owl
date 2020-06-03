// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.rn.RnLineDistance;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.sophus.lie.rn.RnTransport;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Tensor;

public abstract class RnGeodesicDisplay implements GeodesicDisplay, Serializable {
  private final int dimensions;

  public RnGeodesicDisplay(int dimensions) {
    this.dimensions = dimensions;
  }

  @Override // from GeodesicDisplay
  public final GeodesicInterface geodesicInterface() {
    return RnGeodesic.INSTANCE;
  }

  @Override
  public final int dimensions() {
    return dimensions;
  }

  @Override // from GeodesicDisplay
  public final Tensor project(Tensor xya) {
    return xya.extract(0, dimensions);
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return RnGroup.INSTANCE;
  }

  @Override
  public final HsExponential hsExponential() {
    return LieExponential.of(lieGroup(), RnExponential.INSTANCE);
  }

  @Override
  public final HsTransport hsTransport() {
    return RnTransport.INSTANCE;
  }

  @Override
  public final VectorLogManifold vectorLogManifold() {
    return RnManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return RnMetric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return RnBiinvariantMean.INSTANCE;
  }

  @Override
  public final LineDistance lineDistance() {
    return RnLineDistance.INSTANCE;
  }

  @Override // from Object
  public final String toString() {
    return "R" + dimensions;
  }
}
