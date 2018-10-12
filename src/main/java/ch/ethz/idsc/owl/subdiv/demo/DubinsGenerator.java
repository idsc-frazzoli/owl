// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import ch.ethz.idsc.owl.math.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Last;

enum DubinsGenerator {
  ;
  public static Tensor of(Tensor p, Tensor deltas) {
    Tensor points = Tensors.of(p);
    for (Tensor delta : deltas)
      points.append(Se2CoveringIntegrator.INSTANCE.spin(Last.of(points), delta));
    return points;
  }

  public static void main(String[] args) {
    Tensor tensor = of(Array.zeros(3), Tensors.fromString("{{1,0,0},{1,0,.3}}"));
    System.out.println(tensor);
  }
}
