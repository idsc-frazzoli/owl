// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.IOException;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

enum FloodFill2DDemo {
  ;
  public static final ScalarUnaryOperator GRAYSCALE = new ScalarUnaryOperator() {
    final Scalar scale = RealScalar.of(63.0);
    final Scalar middl = RealScalar.of(128.0);
    final Clip clip = Clip.function(0, 255);

    @Override // from Function
    public Scalar apply(Scalar scalar) {
      Scalar r = scalar.multiply(scale).add(middl);
      return clip.isInside(r) ? r : middl;
    }
  };

  public static void main(String[] args) throws IOException {
    final Tensor tensor = R2ImageRegions._0F5C_2182.imageRegion().image();
    Scalar ttl = RealScalar.of(30);
    // ---
    System.out.println("export image " + Dimensions.of(tensor));
    Export.of(UserHome.Pictures("image.png"), tensor);
    Stopwatch stopwatch = Stopwatch.started();
    Tensor cost_raw = FloodFill2D.of(ttl, tensor);
    System.out.println("floodfill    " + stopwatch.display_seconds());
    System.out.println("export cost  " + Dimensions.of(cost_raw));
    Export.of(UserHome.Pictures("image_cost_raw.png"), cost_raw);
    // ---
    // stopwatch = Stopwatch.started();
    Tensor cost = cost_raw;
    // MeanFilter.of(cost_raw, 2);
    // System.out.println("mean filter " + stopwatch.display_seconds());
    // ---
    Tensor range = Tensors.vector(Dimensions.of(cost));
    ImageGradient imageGradient = //
        ImageGradient.linear(cost, range, DoubleScalar.of(1.0));
    System.out.println("field: " + Dimensions.of(imageGradient.field_copy));
    Tensor dx = imageGradient.field_copy.get(Tensor.ALL, Tensor.ALL, 0);
    Tensor dy = imageGradient.field_copy.get(Tensor.ALL, Tensor.ALL, 1);
    dx = dx.map(GRAYSCALE);
    dy = dy.map(GRAYSCALE);
    List<Integer> dims = Dimensions.of(dx);
    Tensor visual = Array.of(l -> RealScalar.of(255), dims.get(0), dims.get(1), 4);
    visual.set(dx, Tensor.ALL, Tensor.ALL, 0);
    visual.set(dy, Tensor.ALL, Tensor.ALL, 1);
    // System.out.println(min + " " + max);
    // Export.of(UserHome.Pictures("cost_dx.png"), dx);
    // Export.of(UserHome.Pictures("cost_dy.png"), dy);
    Export.of(UserHome.Pictures("visual.png"), visual);
  }
}
