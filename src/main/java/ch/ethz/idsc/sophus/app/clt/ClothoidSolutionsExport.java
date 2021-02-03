// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import ch.ethz.idsc.java.util.DisjointSets;
import ch.ethz.idsc.sophus.clt.ClothoidSolutions;
import ch.ethz.idsc.sophus.clt.ClothoidSolutions.Search;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.ext.DeleteDirectory;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class ClothoidSolutionsExport {
  private static final Scalar LAMBDA_THRES = RealScalar.of(1.0);
  // ---
  final TableBuilder tableBuilder = new TableBuilder();
  final TableBuilder tableShortes = new TableBuilder();
  final DisjointSets disjointSets = new DisjointSets();

  static class Sol {
    final Scalar lambda;
    int index;

    public Sol(Scalar lambda) {
      this.lambda = lambda;
    }

    public boolean isClose(Sol sol) {
      return Scalars.lessThan(Abs.between(sol.lambda, lambda), LAMBDA_THRES);
    }

    public void union(Sol[] sols, DisjointSets disjointSet) {
      for (Sol sol : sols)
        if (isClose(sol))
          disjointSet.union(sol.index, index);
    }
  }

  final Tensor S1;
  final Tensor S2;
  final Sol[][][] sols;
  final Map<Integer, TableBuilder> map;

  public ClothoidSolutionsExport(int _n1, int _n2, Scalar ext2) {
    S1 = Subdivide.of(RealScalar.ZERO, Pi.VALUE, _n1 + 1).extract(1, _n1 + 1);
    S2 = Subdivide.of(RealScalar.ZERO, ext2, _n2 - 1);
    System.out.println(S1.length());
    System.out.println(S2.length());
    sols = new Sol[S1.length()][S2.length()][];
    ClothoidSolutions clothoidSolutions = ClothoidSolutions.of(Clips.absolute(15.0));
    for (int ind1 = 0; ind1 < S1.length(); ++ind1) {
      Scalar s1 = S1.Get(ind1);
      System.out.println(s1);
      for (int ind2 = 0; ind2 < S2.length(); ++ind2) {
        Scalar s2 = S2.Get(ind2);
        Search search = clothoidSolutions.new Search(s1, s2);
        Tensor lambdas = search.lambdas();
        // ---
        sols[ind1][ind2] = new Sol[lambdas.length()];
        for (int ind3 = 0; ind3 < sols[ind1][ind2].length; ++ind3) {
          Sol sol = new Sol(lambdas.Get(ind3));
          sols[ind1][ind2][ind3] = sol;
          sol.index = disjointSets.add();
          if (0 < ind1)
            sol.union(sols[ind1 - 1][ind2], disjointSets);
          if (0 < ind2)
            sol.union(sols[ind1][ind2 - 1], disjointSets);
        }
        // ---
        for (Tensor lambda : lambdas)
          tableBuilder.appendRow(s1, s2, lambda);
        // Optional<Scalar> optional = clothoidSolutions.shortest();
        // if (optional.isPresent())
        // tableShortes.appendRow(s1, s2, optional.get());
      }
    }
    map = disjointSets.createMap(TableBuilder::new);
    for (int ind1 = 0; ind1 < S1.length(); ++ind1) {
      for (int ind2 = 0; ind2 < S2.length(); ++ind2) {
        for (int ind3 = 0; ind3 < sols[ind1][ind2].length; ++ind3) {
          Sol sol = sols[ind1][ind2][ind3];
          int key = disjointSets.key(sol.index);
          map.get(key).appendRow(S1.Get(ind1), S2.Get(ind2), sol.lambda);
        }
      }
    }
  }

  public static void main(String[] args) throws IOException {
    ClothoidSolutionsExport clothoidSolutionsExport = //
        new ClothoidSolutionsExport(40, 40, Pi.VALUE);
    // new ClothoidSolutionsExport(20, 120, Pi.TWO.multiply(RealScalar.of(3)));
    // ---
    Export.of(HomeDirectory.file("clothoidsol.csv"), clothoidSolutionsExport.tableBuilder.getTable());
    Export.of(HomeDirectory.file("clothoidsht.csv"), clothoidSolutionsExport.tableShortes.getTable());
    int index = 0;
    File directory = HomeDirectory.Pictures("clsol");
    if (directory.isDirectory())
      DeleteDirectory.of(directory, 1, 100);
    directory.mkdir();
    for (TableBuilder tableBuilder : clothoidSolutionsExport.map.values())
      if (10 < tableBuilder.getRowCount())
        Export.of( //
            new File(directory, String.format("%03d.csv", index++)), //
            tableBuilder.getTable());
  }
}
