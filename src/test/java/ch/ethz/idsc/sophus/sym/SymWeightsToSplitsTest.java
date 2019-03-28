package ch.ethz.idsc.sophus.sym;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.red.VectorTotal;
import junit.framework.Assert;
import junit.framework.TestCase;

public class SymWeightsToSplitsTest extends TestCase {
  public void testTerminal() {
    Tensor tree = Tensors.vector(1,1);
    Tensor weights = Tensors.vector(.5, .5);
    
    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits(weights);
    Tensor expected = Tensors.vector(1, 1, 0.5, 1);
    Tensor actual = symWeightsToSplits.recursion(tree);
    Assert.assertEquals(expected, actual);
  }
  
  public void testSimple() {
    Tensor tree = Tensors.of(Tensors.vector(0, 1), Tensors.of(Tensors.vector(2, 3), RealScalar.of(4)));
    Tensor weights = Normalize.with(VectorTotal.FUNCTION).apply(Tensors.vector(2, 1, 2, 3, 4));
    
    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits(weights);
    Tensor expected = Tensors.of(Tensors.vector(0, 1, 1.0/3.0, 1.0/4.0), //
        Tensors.of(Tensors.vector(2, 3, 3.0 / 5.0, 5.0/12.0), //
            RealScalar.of(4), RationalScalar.of(4, 9), RationalScalar.of(3, 4)), //
        RationalScalar.of(3, 4), RealScalar.ONE);
    Tensor actual = symWeightsToSplits.recursion(tree);
    Assert.assertEquals(expected, actual);
  }


//  // Throw exception if weights are not affine
//  public void testAffinity() {
//    Tensor tree = Tensors.vector(1,1);
//    Tensor weights = Tensors.vector(3,2);
//    
//    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits(weights);
//    Tensor expected = Tensors.vector(1, 1, 0.5, 1);
//    Tensor actual = symWeightsToSplits.recursion(tree);
//    Assert.assertEquals(expected, actual);
//  }
//  
  
}




