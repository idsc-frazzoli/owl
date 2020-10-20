// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subsets;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.red.Total;

/* package */ class UbongoBoard {
  private static final Scalar FREE = RealScalar.ONE.negate();
  // ---
  private final Tensor mask;
  private final int count;

  public UbongoBoard(String... strings) {
    final int n = strings[0].length();
    Tensor prep = Tensors.empty();
    for (String string : strings) {
      Tensor row = Array.zeros(n);
      for (int index = 0; index < string.length(); ++index) {
        if (string.charAt(index) == 'o')
          row.set(FREE, index);
      }
      prep.append(row);
    }
    mask = prep.unmodifiable();
    count = (int) mask.flatten(-1).filter(FREE::equals).count();
  }

  public List<List<UbongoEntry>> filter0(int use) {
    List<List<UbongoEntry>> solutions = new LinkedList<>();
    for (Tensor index : Subsets.of(Range.of(0, 12), use)) {
      int sum = index.stream() //
          .map(Scalar.class::cast) //
          .map(Scalar::number) //
          .mapToInt(Number::intValue) //
          .map(i -> Ubongo.values()[i].count()).sum();
      if (sum == count) {
        // System.out.println(index);
        List<Ubongo> list = index.stream() //
            .map(Scalar.class::cast) //
            .map(Scalar::number) //
            .mapToInt(Number::intValue) //
            .mapToObj(i -> Ubongo.values()[i]) //
            .collect(Collectors.toList());
        Solve solve = new Solve(list);
        if (solve.solutions.size() == 1) {
          List<UbongoEntry> list2 = solve.solutions.get(0);
          solutions.add(list2);
          System.out.println(list2);
        }
      }
    }
    return solutions;
  }

  class Solve {
    private final List<List<UbongoEntry>> solutions = new LinkedList<>();

    public Solve(List<Ubongo> list) {
      solve(mask.copy(), list, Collections.emptyList());
    }

    private void solve(Tensor board, List<Ubongo> list, List<UbongoEntry> entries) {
      List<Integer> board_size = Dimensions.of(board);
      if (list.isEmpty()) {
        solutions.add(entries);
      } else {
        final Ubongo ubongo = list.get(0);
        Tensor cols = Tensor.of(board.stream().map(Total::of));
        Tensor rows = Total.of(board);
        for (UbongoStamp ubongoStamp : ubongo.stamps()) {
          Tensor stamp = ubongoStamp.stamp;
          List<Integer> size = Dimensions.of(stamp);
          List<Integer> bsi = new LinkedList<>();
          List<Integer> bsj = new LinkedList<>();
          for (int bi = 0; bi < board_size.get(0) - size.get(0) + 1; ++bi) {
            boolean status = true;
            for (int si = 0; si < size.get(0); ++si)
              if (Scalars.lessThan(cols.Get(bi + si).negate(), ubongoStamp.cols.Get(si))) {
                status = false;
                break;
              }
            if (status)
              bsi.add(bi);
          }
          for (int bj = 0; bj < board_size.get(1) - size.get(1) + 1; ++bj) {
            boolean status = true;
            for (int sj = 0; sj < size.get(1); ++sj)
              if (Scalars.lessThan(rows.Get(bj + sj).negate(), ubongoStamp.rows.Get(sj))) {
                status = false;
                break;
              }
            if (status)
              bsj.add(bj);
          }
          for (int bi : bsi)
            for (int bj : bsj) {
              Tensor nubrd = board.copy();
              boolean status = true;
              for (int si = 0; si < size.get(0); ++si)
                for (int sj = 0; sj < size.get(1); ++sj) {
                  boolean occupied = stamp.get(si, sj).equals(RealScalar.ONE);
                  if (occupied)
                    if (nubrd.get(bi + si, bj + sj).equals(FREE)) {
                      nubrd.set(RealScalar.ZERO, bi + si, bj + sj);
                    } else {
                      status = false;
                      break;
                    }
                }
              // ---
              if (status) {
                UbongoEntry ubongoEntry = new UbongoEntry();
                ubongoEntry.i = bi;
                ubongoEntry.j = bj;
                ubongoEntry.ubongo = ubongo;
                ubongoEntry.stamp = stamp;
                ArrayList<UbongoEntry> arrayList = new ArrayList<>(entries);
                arrayList.add(ubongoEntry);
                solve(nubrd, list.subList(1, list.size()), arrayList);
              }
            }
        }
      }
    }
  }

  public static void export(File folder, Tensor mask, List<List<UbongoEntry>> solutions) throws IOException {
    folder.mkdir();
    int index = 0;
    for (List<UbongoEntry> sol : solutions) {
      Tensor tensor = UbongoRender.of(mask, sol);
      Export.of(new File(folder, String.format("ub%03d.png", index)), ImageResize.nearest(tensor, 10));
      ++index;
    }
  }

  public static void main(String[] args) throws IOException {
    UbongoBoard ubongoBoard = new UbongoBoard("o oo ", "ooooo", " oooo", " oo o", "ooooo");
    export(HomeDirectory.Pictures("hole2_5"), ubongoBoard.mask, ubongoBoard.filter0(5));
    // System.out.println(Pretty.of(ubongoBoard.mask));
    // Solve solve = ubongoBoard.new Solve(3);
    // System.out.println(solve.solutions.size());
    // UbongoBoard ubongoBoard = new UbongoBoard("ooooo", "ooooo", "oooo ", "oooo ", " o o ");
    // ubongoBoard.filter0(5);
    // System.out.println(Pretty.of(ubongoBoard.mask));
    // Solve solve = ubongoBoard.new Solve(5);
    // System.out.println(solve.solutions.size());
  }
}
