package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Sqrt;
import junit.framework.TestCase;

public class Se2PseudoDistanceInterfaceTest extends TestCase {
  
  public void testTranslation() {
    Tensor p = Tensors.vector(1,1,0);
    Tensor q = Tensors.vector(2,2,0);
    //TODO: Namen aendern
    Tensor result = Se2PseudoDistanceInterface.se2(p, q).Se2PseudoDistance();
    
    assertEquals(Tensors.of(Sqrt.FUNCTION.apply(RealScalar.of(2)), RealScalar.ZERO), result);
  }
  
  public void testRotation() {
    Tensor p = Tensors.vector(0,0,0);
    Tensor q = Tensors.vector(0,0,2);
    Tensor result = Se2PseudoDistanceInterface.se2(p, q).Se2PseudoDistance();
    
    assertEquals(Tensors.vector(0,2), result);
  }
  
  public void testCombined() {
    Tensor p = Tensors.vector(1,3,5);
    Tensor q = Tensors.vector(-5,9,3);
    Tensor result = Se2PseudoDistanceInterface.se2(p, q).Se2PseudoDistance();
    
    //TODO: Werte ueberpruefen, oder besseres Testbeispiel finden.
    assertEquals(Tensors.vector(10.083866856295366, 2.0), result);
  }
  
}
