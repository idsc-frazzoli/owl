// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.io.IOException;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.nrm.NormalizeTotal;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class HnMeansTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = NormalDistribution.standard();
    for (HnMeans hnMeans : HnMeans.values()) {
      BiinvariantMean biinvariantMean = Serialization.copy(hnMeans).get();
      for (int d = 1; d < 5; ++d) {
        final int fd = d;
        Tensor sequence = Array.of(l -> HnWeierstrassCoordinate.toPoint(RandomVariate.of(distribution, fd)), 10);
        Tensor weights = NormalizeTotal.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit(), 10));
        biinvariantMean.mean(sequence, weights);
      }
    }
  }
}
