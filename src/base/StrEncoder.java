package base;

public class StrEncoder {

  /**
   * Encode a string
   * @param encoder : StrEncoder to use
   * @param sb : where to append result to; if null, constructs buffer
   * @param raw : string to encode
   * @return StringBuilder containing result
   */
  public static StringBuilder encode(StrEncoder encoder, StringBuilder sb,
      CharSequence raw) {
    if (sb == null)
      sb = new StringBuilder();
    encoder.encode(sb, raw);
    return sb;
  }

  /**
   * Decode a string
   * @param encoder : StrEncoder to use
   * @param sb : where to append result to; if null, constructs buffer
   * @param encoded : string to decode
   * @return StringBuilder containing result
   */
  public static StringBuilder decode(StrEncoder encoder, StringBuilder sb,
      CharSequence encoded) {
    if (sb == null)
      sb = new StringBuilder();
    encoder.decode(sb, encoded);
    return sb;
  }

  /**
   * Decode a string from 'safe' form to its appropriate raw form
   * 
   * @param encoded : the 'safe' form
   * @return raw string
   */
  public String decode(CharSequence encoded) {
    return decode(this, null, encoded).toString();
  }

  /**
   * Encode a string from its raw form to its 'safe' form
   * @param raw
   * @return encoded string
   */
  public String encode(CharSequence raw) {
    return encode(this, null, raw).toString();
  }

  /**
   * Encode a string to 'safe' form, one with appropriate escape codes inserted,
   * and optional quoting
   * @param sb StringBuilder to encode to
   * @param raw the raw string to be encoded
   */
  public void encode(StringBuilder sb, CharSequence raw) {
    throw new UnsupportedOperationException();
  }

  /**
   * Decode a string from 'safe' form
   * @param sb StringBuilder to decode to
   * @param encoded the string to be decoded
   */
  public void decode(StringBuilder sb, CharSequence encoded) {
    throw new UnsupportedOperationException();
  }

}
