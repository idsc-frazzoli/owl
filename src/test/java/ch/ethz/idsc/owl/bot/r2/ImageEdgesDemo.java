// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Timing;

enum ImageEdgesDemo {
  ;
  public static void main(String[] args) throws IOException {
    final Tensor tensor = ImageRegions.grayscale( //
        Import.of(HomeDirectory.Pictures("20180122_duebendorf_hangar.png"))).unmodifiable();
    // ---
    for (int ttl = 0; ttl <= 5; ++ttl) {
      Timing timing = Timing.started();
      Tensor visual = ImageEdges.extrusion(tensor, ttl);
      System.out.println(timing.seconds());
      Export.of(HomeDirectory.Pictures(String.format("hangar%02d.png", ttl)), visual);
    }
  }
}
