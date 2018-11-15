// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Arg;

/** inspired by Mathematica's documentation of Gamma */
public enum FresnelDemo {
  ;
  // ---
  private static final int RES = 128 + 64;
  private static final int DEPTH = 2;
  private static final Tensor RE = Subdivide.of(-1, -1, RES - 1);
  private static final Tensor IM = Subdivide.of(-1, +1, RES - 1);

  private static Scalar function(int y, int x) {
    Scalar z = ComplexScalar.of(RE.Get(x), IM.Get(y));
    try {
      // return Arg.of(FresnelS.FUNCTION.apply(z));
      return Arg.of(Nest.of(FresnelS.FUNCTION, z, DEPTH));
    } catch (Exception exception) {
      System.out.println("fail=" + z);
    }
    return DoubleScalar.INDETERMINATE;
  }

  public static void main(String[] args) throws Exception {
    {
      Tensor plot = Tensors.empty();
      for (Tensor s : Subdivide.of(-6, 6, 1000)) {
        Scalar apply = FresnelC.FUNCTION.apply(s.Get());
        plot.append(Tensors.of(s, apply));
      }
      Put.of(UserHome.file("fresC"), plot);
    }
    {
      Tensor plot = Tensors.empty();
      for (Tensor s : Subdivide.of(-6, 6, 1000)) {
        Scalar apply = FresnelS.FUNCTION.apply(s.Get());
        plot.append(Tensors.of(s, apply));
      }
      Put.of(UserHome.file("fresS"), plot);
    }
    Tensor matrix = Parallelize.matrix(FresnelDemo::function, RES, RES);
    Export.of(UserHome.Pictures("blub.png"), //
        ArrayPlot.of(matrix, ColorDataGradients.HUE));
  }
}
