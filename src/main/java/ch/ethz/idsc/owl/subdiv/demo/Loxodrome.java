// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.math.planar.S2Geodesic;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenter;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenterFilter;
import ch.ethz.idsc.owl.symlink.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

public class Loxodrome implements ScalarTensorFunction {
  private final Scalar angle;

  public Loxodrome(Scalar angle) {
    this.angle = angle;
  }

  @Override
  public Tensor apply(Scalar t) {
    Scalar f = ArcTan.FUNCTION.apply(t.multiply(angle));
    Scalar cf = Cos.FUNCTION.apply(f);
    Scalar x = Cos.FUNCTION.apply(t).multiply(cf);
    Scalar y = Sin.FUNCTION.apply(t).multiply(cf);
    Scalar z = Sin.FUNCTION.apply(f);
    return Tensors.of(x, y, z);
  }

  public static void main(String[] args) throws IOException {
    Loxodrome loxodrome = new Loxodrome(RealScalar.of(.15));
    Tensor tensor = Subdivide.of(0, 4.5, 250).map(AbsSquared.FUNCTION).map(loxodrome);
    Export.of(UserHome.file("loxodrome_exact.csv"), tensor);
    Tensor noise = RandomVariate.of(NormalDistribution.of(0, .05), Dimensions.of(tensor));
    tensor = tensor.add(noise);
    tensor = Tensor.of(tensor.stream().map(Normalize::of));
    Export.of(UserHome.file("loxodrome_noise.csv"), tensor);
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      TensorUnaryOperator tensorUnaryOperator = GeodesicCenterFilter.of( //
          GeodesicCenter.of(S2Geodesic.INSTANCE, smoothingKernel), 7);
      Tensor smooth = tensorUnaryOperator.apply(tensor);
      Export.of(UserHome.file("loxodrome_" + smoothingKernel.name().toLowerCase() + ".csv"), smooth);
    }
  }
}
