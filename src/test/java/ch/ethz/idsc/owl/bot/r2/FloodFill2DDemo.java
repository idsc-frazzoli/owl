// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.IOException;
import java.util.List;

import ch.ethz.idsc.owl.math.ImageGradient;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.ext.Timing;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ enum FloodFill2DDemo {
  ;
  public static final ScalarUnaryOperator GRAYSCALE = new ScalarUnaryOperator() {
    final Scalar scale = RealScalar.of(63.0);
    final Scalar middl = RealScalar.of(128.0);
    final Clip clip = Clips.interval(0, 255);

    @Override // from Function
    public Scalar apply(Scalar scalar) {
      Scalar r = scalar.multiply(scale).add(middl);
      return clip.isInside(r) ? r : middl;
    }
  };

  public static void main(String[] args) throws IOException {
    final Tensor tensor = R2ImageRegions.inside_0f5c_2182_charImage();
    int ttl = 30;
    // ---
    System.out.println("export image " + Dimensions.of(tensor));
    Export.of(HomeDirectory.Pictures("image.png"), tensor);
    Timing timing = Timing.started();
    Tensor cost_raw = FloodFill2D.of(tensor, ttl);
    System.out.println("floodfill    " + timing.seconds());
    System.out.println("export cost  " + Dimensions.of(cost_raw));
    Export.of(HomeDirectory.Pictures("image_cost_raw.png"), cost_raw);
    // ---
    Tensor cost = cost_raw;
    // MeanFilter.of(cost_raw, 2);
    // ---
    Tensor field_copy = ImageGradient.rotated(cost).multiply(RealScalar.of(1.0));
    System.out.println("field: " + Dimensions.of(field_copy));
    Tensor dx = field_copy.get(Tensor.ALL, Tensor.ALL, 0);
    Tensor dy = field_copy.get(Tensor.ALL, Tensor.ALL, 1);
    dx = dx.map(GRAYSCALE);
    dy = dy.map(GRAYSCALE);
    List<Integer> dims = Dimensions.of(dx);
    Tensor visual = Array.of(l -> RealScalar.of(255), dims.get(0), dims.get(1), 4);
    visual.set(dx, Tensor.ALL, Tensor.ALL, 0);
    visual.set(dy, Tensor.ALL, Tensor.ALL, 1);
    // Export.of(UserHome.Pictures("cost_dx.png"), dx);
    // Export.of(UserHome.Pictures("cost_dy.png"), dy);
    Export.of(HomeDirectory.Pictures("visual.png"), visual);
  }
}
