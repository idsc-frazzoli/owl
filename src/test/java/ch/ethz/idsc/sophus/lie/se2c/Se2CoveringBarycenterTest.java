// code by jph
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.sophus.math.AffineQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CoveringBarycenterTest extends TestCase {
  public void testZeros() {
    Tensor sequence = Tensors.fromString("{{2,3,4},{1,2,3},{-2,1,1},{2,-1,-7}}");
    TensorUnaryOperator se2CoveringBarycenter = new Se2CoveringBarycenter(sequence);
    Tensor mean = Array.zeros(3);
    Tensor weights = se2CoveringBarycenter.apply(mean);
    AffineQ.require(weights);
    Tensor result = Se2CoveringBiinvariantMean.INSTANCE.mean(sequence, weights);
    Chop._12.requireClose(result, mean);
  }

  public void testXY() {
    Tensor sequence = Tensors.fromString("{{2,3,4},{1,2,3},{-2,1,1},{2,-1,-7}}");
    TensorUnaryOperator se2CoveringBarycenter = new Se2CoveringBarycenter(sequence);
    Tensor mean = Tensors.vector(0.3, 0.6, 0);
    Tensor weights = se2CoveringBarycenter.apply(mean);
    AffineQ.require(weights);
    Tensor result = Se2CoveringBiinvariantMean.INSTANCE.mean(sequence, weights);
    Chop._12.requireClose(result, mean);
  }

  public void testXYA() {
    Tensor sequence = Tensors.fromString("{{2,3,4},{1,2,3},{-2,1,1},{2,-1,-7}}");
    TensorUnaryOperator se2CoveringBarycenter = new Se2CoveringBarycenter(sequence);
    Tensor mean = Tensors.vector(0.3, 0.6, 0.9);
    Tensor weights = se2CoveringBarycenter.apply(mean);
    AffineQ.require(weights);
    Tensor result = Se2CoveringBiinvariantMean.INSTANCE.mean(sequence, weights);
    Chop._12.requireClose(result, mean);
  }

  public void testRank() {
    Tensor sequence = Tensors.fromString("{{0,0,0},{1,0,0},{0,1,0},{0,0,1}}");
    Se2CoveringBarycenter se2CoveringBarycenter = new Se2CoveringBarycenter(sequence);
    Tensor mean = Tensors.vector(0.75, 1, 0);
    // System.out.println("DET=" + Det.of(se2CoveringBarycenter.matrix(mean)));
    Tensor weights = se2CoveringBarycenter.apply(mean);
    AffineQ.require(weights);
    // System.out.println(weights);
    Tensor result = Se2CoveringBiinvariantMean.INSTANCE.mean(sequence, weights);
    Chop._12.requireClose(result, mean);
    // TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);
    Scalar w = weights.Get(1);
    // Tensor approx =
    Se2CoveringBiinvariantMean.INSTANCE.mean(sequence.extract(0, 2), Tensors.of(RealScalar.ONE.subtract(w), w));
    // System.out.println(approx.map(Round._4));
  }

  public void testLengthFail() {
    try {
      new Se2CoveringBarycenter(HilbertMatrix.of(5, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
