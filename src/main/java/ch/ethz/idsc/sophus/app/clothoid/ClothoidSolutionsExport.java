// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.opt.Pi;

/* package */ enum ClothoidSolutionsExport {
  ;
  public static void main(String[] args) throws IOException {
    int n1 = 30;
    int n2 = 80;
    Tensor S1 = Drop.tail(Subdivide.of(RealScalar.ZERO, Pi.VALUE, n1), 1);
    Tensor S2 = Subdivide.of(RealScalar.ZERO, Pi.TWO.multiply(RealScalar.of(3)), n2);
    TableBuilder tableBuilder = new TableBuilder();
    TableBuilder tableShortes = new TableBuilder();
    for (int index = 0; index < S1.length(); ++index) {
      Scalar s1 = S1.Get(index);
      System.out.println(s1);
      for (Tensor _s2 : S2) {
        Scalar s2 = (Scalar) _s2;
        ClothoidSolutions clothoidSolutions = ClothoidSolutions.of(s1, s2);
        Tensor lambdas = clothoidSolutions.lambdas();
        for (Tensor lambda : lambdas)
          tableBuilder.appendRow(s1, s2, lambda);
        Optional<Scalar> optional = clothoidSolutions.shortest();
        if (optional.isPresent())
          tableShortes.appendRow(s1, s2, optional.get());
      }
    }
    Export.of(HomeDirectory.file("clothoidsol.csv"), tableBuilder.getTable());
    Export.of(HomeDirectory.file("clothoidsht.csv"), tableShortes.getTable());
  }
}
