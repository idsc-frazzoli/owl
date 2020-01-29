// code by ob
package ch.ethz.idsc.sophus.app.sym;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;
import junit.framework.TestCase;

public class SymWeightsToSplitsTest extends TestCase {
  private static final TensorUnaryOperator NORMALIZE_AFFINE = Normalize.with(Total::ofVector);

  public void testTerminal() {
    Tensor tree = Tensors.vector(0, 1);
    Tensor weights = Tensors.vector(.5, .5);
    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits(tree, weights);
    Tensor expected = Tensors.vector(0, 1, 0.5, 1);
    Tensor actual = symWeightsToSplits.splits();
    assertEquals(expected, actual);
  }

  public void testSimple() {
    Tensor tree = Tensors.of(Tensors.vector(0, 1), Tensors.of(Tensors.vector(2, 3), RealScalar.of(4)));
    Tensor weights = NORMALIZE_AFFINE.apply(Tensors.vector(2, 1, 2, 3, 4));
    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits(tree, weights);
    Tensor expected = Tensors.fromString("{{0, 1, 0.3333333333333333, 0.25}, {{2, 3, 0.6, 0.4166666666666667}, 4, 4/9, 3/4}, 3/4, 1}");
    Tensor actual = symWeightsToSplits.splits();
    assertEquals(expected, actual);
  }

  public void testMultiplicitySimple() {
    Tensor tree = Tensors.of(Tensors.vector(0, 1), RealScalar.ONE);
    Tensor weights = NORMALIZE_AFFINE.apply(Tensors.vector(0.5, 0.5));
    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits(tree, weights);
    Tensor actual = symWeightsToSplits.weights;
    Tensor expected = Tensors.vector(0.5, 0.25);
    assertEquals(expected, actual);
  }

  public void testMultiplicity() {
    Tensor tree = Tensors.of(Tensors.vector(0, 1), Tensors.of(Tensors.vector(2, 3), RealScalar.of(3)));
    Tensor weights = NORMALIZE_AFFINE.apply(Tensors.vector(1, 1, 1, 1));
    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits(tree, weights);
    Tensor expected = Tensors.fromString("{{0, 1, 1/2, 1/2}, {{2, 3, 1/3, 3/8}, 3, 1/4, 1/2}, 1/2, 1}");
    Tensor actual = symWeightsToSplits.splits();
    assertEquals(expected, actual);
  }
}
