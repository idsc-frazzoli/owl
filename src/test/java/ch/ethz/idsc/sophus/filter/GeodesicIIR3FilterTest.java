package ch.ethz.idsc.sophus.filter;

import java.util.Random;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;


//TODO: OB/JH überprüfen ob werte im asserEquals sinnvoll sind.
public class GeodesicIIR3FilterTest extends TestCase {
  public void testTranslation() {
    Tensor p = Tensors.vector(0,0,0);
    Tensor q = Tensors.vector(1,1,0);
    Tensor r = Tensors.vector(2,2,0);
    Scalar alpha = RealScalar.of(0.5);
    Scalar beta = RealScalar.of(Math.random());

    Tensor control = Tensors.of(p,q,r);

    TensorUnaryOperator geodesicCenterFilter = new GeodesicIIR3Filter(Se2Geodesic.INSTANCE, alpha, beta);
    Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    assertEquals(refined.get(2), Tensors.vector(1.4166666666666665, 1.4166666666666665, 0.0));
  }
  
  public void testRotation() {
    Tensor p = Tensors.vector(0,0,0);
    Tensor q = Tensors.vector(0,0,1);
    Tensor r = Tensors.vector(0,0,2);
    Scalar alpha = RealScalar.of(0.5);
    Scalar beta = RealScalar.of(Math.random());
    
    Tensor control = Tensors.of(p,q,r);

    TensorUnaryOperator geodesicCenterFilter = new GeodesicIIR3Filter(Se2Geodesic.INSTANCE, alpha, beta);
    Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    assertEquals(refined.get(2), Tensors.vector(0,0, 1.4166666666666665));
  }
  
  
  public void testCombined() {
    Tensor p = Tensors.vector(0,0,0);
    Tensor q = Tensors.vector(1,1,1);
    Tensor r = Tensors.vector(2,2,2);
    Scalar alpha = RealScalar.of(0.5);
    Scalar beta = RealScalar.of(Math.random());

    Tensor control = Tensors.of(p,q,r);

    TensorUnaryOperator geodesicCenterFilter = new GeodesicIIR3Filter(Se2Geodesic.INSTANCE, alpha, beta);
    Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    assertEquals(refined.get(2), Tensors.vector(1.640245988919491, 1.222086008157701, 1.4166666666666665));
  }
}
