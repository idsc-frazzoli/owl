// code by jph
package ch.ethz.idsc.owl.data.nd;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalars;

enum NdEntryComparators {
  ;
  static final Comparator<NdEntry<?>> INCREASING = new Comparator<NdEntry<?>>() {
    @Override
    public int compare(NdEntry<?> o1, NdEntry<?> o2) {
      return Scalars.compare(o1.distance(), o2.distance());
    }
  };
  static final Comparator<NdEntry<?>> DECREASING = new Comparator<NdEntry<?>>() {
    @Override
    public int compare(NdEntry<?> o1, NdEntry<?> o2) {
      return Scalars.compare(o2.distance(), o1.distance());
    }
  };
}
