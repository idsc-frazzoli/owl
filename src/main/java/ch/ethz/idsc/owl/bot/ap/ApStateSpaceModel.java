package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class ApStateSpaceModel implements StateSpaceModel {
  @Override
  public Tensor f(Tensor x, Tensor u) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Scalar getLipschitz() {
    // TODO Auto-generated method stub
    return null;
  }

  public static void main(String[] args) {
    System.out.println("Test");
    ApStateSpaceModel apStateSpaceModel = new ApStateSpaceModel();
    apStateSpaceModel.f(Tensors.vector(1, 2, 3), Tensors.vector(1, 2));
  }
}
