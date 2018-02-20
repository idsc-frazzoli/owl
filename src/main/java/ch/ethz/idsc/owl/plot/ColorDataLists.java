// code by jph
package ch.ethz.idsc.owl.plot;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

public enum ColorDataLists {
  /** mathematica default */
  _97, //
  ;
  private final List<Color> list;

  private ColorDataLists() {
    String string = "/colorlist/" + name().substring(1) + ".csv";
    list = ResourceData.of(string).stream().map(ColorFormat::toColor).collect(Collectors.toList());
  }

  Color get(int index) {
    return list.get(index);
  }
}
