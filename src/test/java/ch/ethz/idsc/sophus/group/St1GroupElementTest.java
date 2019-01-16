package ch.ethz.idsc.sophus.group;



import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class St1GroupElementTest extends TestCase {
  Tensor p = Tensors.vectorFloat(3,6);
  Tensor id = Tensors.vector(1.0, 0.0);
  
  St1GroupElement P = new St1GroupElement(p);
  
  public void testSimple() {
    assertEquals(P.inverse().combine(p), id);
  }
}
