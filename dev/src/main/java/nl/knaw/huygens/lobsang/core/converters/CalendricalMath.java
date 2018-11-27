package nl.knaw.huygens.lobsang.core.converters;

public class CalendricalMath {
  // mod function that will always return a positive value, that is more like the lisp mod function
  // see: https://stackoverflow.com/a/4403642
  // see: http://jtra.cz/stuff/lisp/sclr/mod.html
  public static int mod(int value, int modBase) {
    while (value < 0) {
      value += modBase;
    }
    int mod = value % modBase;
    return mod;
  }
}
