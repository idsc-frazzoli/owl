// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Import;

enum ImageEdgesDemo {
  ;
  public static void main(String[] args) throws IOException {
    final Tensor tensor = ImageRegions.grayscale( //
        Import.of(UserHome.Pictures("20180122_duebendorf_hangar.png"))).unmodifiable();
    // ---
    for (int ttl = 0; ttl <= 5; ++ttl) {
      Stopwatch stopwatch = Stopwatch.started();
      Tensor visual = ImageEdges.extrusion(tensor, ttl);
      System.out.println(stopwatch.display_seconds());
      Export.of(UserHome.Pictures(String.format("hangar%02d.png", ttl)), visual);
    }
  }
}
