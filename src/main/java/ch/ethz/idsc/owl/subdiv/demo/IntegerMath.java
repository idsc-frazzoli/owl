// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

enum IntegerMath {
  ;
  /** mod that behaves like in Matlab. for instance mod(-10, 3) == 2
   * 
   * @param index
   * @param size
   * @return matlab.mod(index, size) */
  public static int mod(int index, int size) {
    int value = index % size;
    // if value is below 0, then -size < value && value < 0.
    // For instance: -3%3==0, and -2%3==-2.
    return value < 0 ? size + value : value;
  }
}
