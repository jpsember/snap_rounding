package base;

public class MutableInteger {
  public MutableInteger(int n) {
    this.n = n;
  }
  public MutableInteger() {
  }
  public String toString() {
    return Integer.toString(n);
  }
  public int n;
  public void setBit(int bitNumber) {
    n |= (1 << bitNumber);
  }
  public boolean testBit(int bitNumber) {
    return (n & (1 << bitNumber)) != 0;
  }
  public void clearBit(int bitNumber) {
    n &= ~(1 << bitNumber);
  }

  /**
   * Adjust value of integer, synchronized
   * @param amt amount to add
   * @return new value of counter
   */
  public synchronized int syncAdd(int amt) {
    int ret = n;
    n += amt;
    return ret;
  }
}
