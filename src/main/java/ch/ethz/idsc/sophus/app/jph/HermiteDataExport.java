// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline2CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.HermiteSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.HermiteSubdivisions;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
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
    this.levels = levels;
    folder = HomeDirectory.Documents(name);
    folder.mkdir();
    Tensor data = GokartPoseDataV2.INSTANCE.getPoseVel(name, 100_000);
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

  public void process(HermiteSubdivision hermiteSubdivision, CurveSubdivision curveSubdivision, String name) throws IOException {
    TensorIteration tensorIteration = //
        hermiteSubdivision.string(delta, control);
    Tensor refined = Do.of(tensorIteration::iterate, levels);
    File dst = new File(folder, name);
    dst.mkdir();
    Export.of(new File(dst, "refined.mathematica"), refined);
    Tensor tensor = Nest.of(curveSubdivision::string, domain, levels);
    Export.of(new File(dst, "refined_domain.mathematica"), tensor);
  }

  public void processAll() throws IOException {
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H1STANDARD.supply(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, Se2BiinvariantMean.LINEAR);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h1standard");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H2A1.supply(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, Se2BiinvariantMean.LINEAR);
      CurveSubdivision curveSubdivision = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h2a1");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H2A2.supply(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, Se2BiinvariantMean.LINEAR);
      CurveSubdivision curveSubdivision = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h2a2");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H3STANDARD.supply(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, Se2BiinvariantMean.FILTER);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h3standard");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H3A1.supply(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, Se2BiinvariantMean.FILTER);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h3a1");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H3A2.supply(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, Se2BiinvariantMean.FILTER);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h3a2");
    }
  }

  public static void main(String[] args) throws IOException {
    Scalar period = Quantity.of(RationalScalar.of(1, 2), "s");
    HermiteDataExport hermiteDataExport = new HermiteDataExport("20190701T163225_01", period, 4);
    hermiteDataExport.processAll();
  }
}
