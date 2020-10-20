// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.DeleteDuplicates;
import ch.ethz.idsc.tensor.alg.NestList;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.img.ImageRotate;

/* package */ enum Ubongo {
  A0("xx"), //
  A1("xx", "x"), //
  A2("xx", "xx"), //
  A3("xx ", " xx"), //
  A4("xx ", " x", " xx"), //
  B0("xxx"), //
  B1("xxx", "x"), //
  B2("xxx", " x"), //
  B3("xxx", "xx"), //
  C0("xxxx"), //
  C1("xxxx", "x"), //
  C2("xxxx", " x"), //
  ;

  private final Tensor mask;
  private final int count;
  private final Set<UbongoStamp> set = new HashSet<>();

  private Ubongo(String... strings) {
    final int n = strings[0].length();
    Tensor prep = Tensors.empty();
    for (String string : strings) {
      Tensor row = Array.zeros(n);
      for (int index = 0; index < string.length(); ++index) {
        if (string.charAt(index) == 'x')
          row.set(RealScalar.ONE, index);
      }
      prep.append(row);
    }
    mask = prep.unmodifiable();
    count = (int) prep.flatten(-1).filter(RealScalar.ONE::equals).count();
    // ---
    Tensor rotated = DeleteDuplicates.of(NestList.of(ImageRotate::of, mask, 4));
    Set<Tensor> stamps = new HashSet<>();
    rotated.stream().forEach(stamps::add);
    rotated.stream().map(Reverse::of).forEach(stamps::add);
    for (Tensor stamp : stamps) {
      set.add(new UbongoStamp(stamp));
    }
  }

  public Tensor mask() {
    return mask;
  }

  public int count() {
    return count;
  }

  public Set<UbongoStamp> stamps() {
    return Collections.unmodifiableSet(set);
  }

  public static void main(String[] args) {
    System.out.println(Ubongo.values().length);
  }
}
