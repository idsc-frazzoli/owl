// code by jph
package ch.ethz.idsc.sophus.surf;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.Sin;

class Plot3DDesign {
  private static final Scalar DT = RealScalar.of(.01);
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);
  // ---
  private final TensorScalarFunction tensorScalarFunction;

  public Plot3DDesign(TensorScalarFunction tensorScalarFunction) {
    this.tensorScalarFunction = tensorScalarFunction;
  }

  public Tensor at(Number x, Number y) {
    return at(RealScalar.of(x), RealScalar.of(y));
  }

  public Tensor at(Scalar x, Scalar y) {
    Scalar fv = tensorScalarFunction.apply(Tensors.of(x, y));
    Tensor p = Tensors.of(x, y, fv).unmodifiable();
    Scalar fx = tensorScalarFunction.apply(Tensors.of(x.add(DT), y));
    Scalar fy = tensorScalarFunction.apply(Tensors.of(x, y.add(DT)));
    Tensor dx = Tensors.of(DT, RealScalar.ZERO, fx.subtract(fv));
    Tensor dy = Tensors.of(RealScalar.ZERO, DT, fy.subtract(fv));
    Tensor normal = NORMALIZE.apply(Cross.of(dx, dy));
    return Tensors.of(p, normal);
  }

  private static Scalar sin_xy2(Tensor xy) {
    return Sin.FUNCTION.apply(xy.Get(0).add(AbsSquared.FUNCTION.apply(xy.Get(1))));
  }

  public static void main(String[] args) throws IOException {
    Plot3DDesign plot3dDesign = new Plot3DDesign(Plot3DDesign::sin_xy2);
    Tensor matrix = Tensors.empty();
    for (Tensor x : Subdivide.of(0, 4, 12)) {
      Tensor row = Tensors.empty();
      for (Tensor y : Subdivide.of(0, 2, 8)) {
        row.append(plot3dDesign.at(x.Get(), y.Get()));
      }
      matrix.append(row);
    }
    Put.of(HomeDirectory.file("sinxy2in.mathematica"), matrix);
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(R3S2Geodesic.INSTANCE);
    Tensor tensor = Nest.of(catmullClarkSubdivision::refine, matrix, 3);
    Put.of(HomeDirectory.file("sinxy2.mathematica"), tensor);
  }
}
