// code by jph
package ch.ethz.idsc.sophus.app.aurora;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.crv.d2.Curvature2D;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.lie.LieTransport;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringManifold;
import ch.ethz.idsc.sophus.lie.so2.So2Lift;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.sophus.opt.HermiteSubdivisions;
import ch.ethz.idsc.sophus.ref.d1.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.BSpline2CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1h.HermiteSubdivision;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Nest;

/* package */ class HermiteDataExport {
  private final int levels;
  private final File folder;
  private final Tensor control = Tensors.empty();
  private final Scalar delta;
  private final Tensor domain;

  /** @param name "20190701T163225_01"
   * @param period 1/2[s]
   * @param levels 4
   * @throws IOException */
  public HermiteDataExport(String name, Scalar period, int levels) throws IOException {
    this.levels = Integers.requirePositive(levels);
    folder = HomeDirectory.Documents(name);
    folder.mkdir();
    Tensor data = GokartPoseDataV2.INSTANCE.getPoseVel(name, 2_000);
    data.set(new So2Lift(), Tensor.ALL, 0, 2);
    {
      Export.of(new File(folder, "gndtrth.mathematica"), data);
      Tensor domain1 = Range.of(0, data.length()).multiply(RealScalar.of(1 / 50.));
      Export.of(new File(folder, "gndtrth_domain.mathematica"), domain1);
    }
    Scalar rate = GokartPoseDataV2.INSTANCE.getSampleRate();
    delta = QuantityMagnitude.SI().in("s").apply(period);
    int skip = Scalars.intValueExact(period.multiply(rate));
    for (int index = 0; index < data.length(); index += skip)
      control.append(data.get(index));
    Export.of(new File(folder, "control.mathematica"), control);
    domain = Range.of(0, control.length()).multiply(delta);
    Export.of(new File(folder, "control_domain.mathematica"), domain);
  }

  private void process(HermiteSubdivision hermiteSubdivision, CurveSubdivision curveSubdivision, String name) throws IOException {
    TensorIteration tensorIteration = //
        hermiteSubdivision.string(delta, control);
    File dst = new File(folder, name);
    dst.mkdir();
    {
      Tensor refined = Do.of(tensorIteration::iterate, levels);
      Export.of(new File(dst, "refined.mathematica"), refined);
      Tensor curvatu = Curvature2D.string(Tensor.of(refined.stream().map(point -> point.get(0).extract(0, 2))));
      Export.of(new File(dst, "curvatu.mathematica"), curvatu);
    }
    {
      Tensor tensor = Nest.of(curveSubdivision::string, domain, levels);
      Export.of(new File(dst, "refined_domain.mathematica"), tensor);
    }
  }

  private void processAll() throws IOException {
    HsManifold hsManifold = Se2CoveringManifold.INSTANCE;
    HsTransport hsTransport = LieTransport.INSTANCE;
    BiinvariantMean biinvariantMean = Se2CoveringBiinvariantMean.INSTANCE;
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H1STANDARD.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h1standard");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H2STANDARD.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h2standard");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H2MANIFOLD.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h2manifold");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H3STANDARD.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h3standard");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H3A1.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h3a1");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H3A2.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h3a2");
    }
  }

  public static void main(String[] args) throws IOException {
    Scalar period = Quantity.of(RationalScalar.of(1, 1), "s");
    HermiteDataExport hermiteDataExport = new HermiteDataExport("20190701T163225_01", period, 6);
    hermiteDataExport.processAll();
  }
}
