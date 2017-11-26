// code by jph
package ch.ethz.idsc.owl.img;

import java.io.IOException;
import java.util.Set;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;

enum FloodFill2DDemo {
  ;
  public static void main(String[] args) throws IOException {
    final Tensor tensor = R2ImageRegions.inside_gtob_charImage();
    Export.of(UserHome.Pictures("image.png"), tensor);
    Set<Tensor> seeds = FloodFill2D.seeds(tensor);
    Scalar ttl = RealScalar.of(30);
    Stopwatch stopwatch = Stopwatch.started();
    Tensor cost = FloodFill2D.of(seeds, ttl, tensor);
    System.out.println("floodfill " + stopwatch.display_seconds());
    Export.of(UserHome.Pictures("image_cost.png"), cost);
  }
}
