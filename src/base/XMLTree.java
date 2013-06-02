package base;

import java.util.*;
import base.*;

/**
 * Manipulates XML files
 * 
 * If a node is open, its text is not yet defined, and the attributes can
 * be manipulated.  A tree must be compiled to close its nodes, at which point
 * the attributes are combined with the tag name to create the node's text, and
 * if necessary, a closing tag.
 */
public class XMLTree {

  public static final String MISSING_END_TAG = "Missing end tag";

  private static final int T_UNKNOWN = 0, T_START = 1, T_END = 2, T_EMPTY = 3;

  /**
   * Get number of children of the root node
   * @return number of children
   */
  public int nChildren() {
    int ret = 0;
    if (children != null)
      ret = children.size();
    return ret;
  }

  /**
   * Get array containing children
   * @return array of child trees
   */
  public DArray getChildren() {
    if (children == null)
      children = new DArray();
    return children;
  }

  /**
   * Get child tree
   * @param n child number
   * @return child tree
   */
  public XMLTree child(int n) {
    return (XMLTree) children.get(n);
  }

  /**
   * Construct a tree that has an empty string for
   * the root, and has this tree's children 
   * @return tree composed of this tree's children
   */
  public XMLTree treeOfChildren() {
    XMLTree t2 = new XMLTree("");
    for (int i = 0; i < nChildren(); i++)
      t2.addChild(child(i));
    return t2;
  }

  /**
   * Parse XML tree from string
   * @param st string to parse
   * @param context where string originated from, for error reporting
   * @param problems where to store problems, or null; a problem is stored
   *   as a Token (holds root node of problem tree) followed by a String
   *   (description of the problem)
   * @return tree
   */
  public static XMLTree parse(String st, Object context, DArray problems) {
    if (problems != null)
      problems.clear();

    XMLTokenizer t = new XMLTokenizer(st, context);

    XMLTree root = new XMLTree("");

    while (t.hasNext()) {
      XMLTree t2 = parseAux(root, t, root, problems);
      if (t2 == null)
        break;
      root.addChild(t2);
    }
    return root;
  }

  /**
   * Add a child node
   * @param t child to add
   * @return t
   */
  public XMLTree addChild(XMLTree t) {
    getChildren().add(t);
    return t;
  }

  /**
   * Recursively parse child tree 
   * @param root tree to parse children of
   * @param t tokenizer
   * @param base tree to add problems to
   * @return parsed child tree, or null
   */

  private static XMLTree parseAux(XMLTree root, XMLTokenizer t, XMLTree base,
      DArray problems) {
    XMLTree tree = null;
    if (t.hasNext()) {
      Token tk = t.peekNext();
      String s = tk.text();
      int type = tagType(s);

      switch (type) {
      case T_END:
        break;
      default:
        t.next();
        tree = new XMLTree(s);
        break;
      case T_START:
        t.next();
        {
          tree = new XMLTree(s);

          while (true) {
            XMLTree child = parseAux(tree, t, base, problems);
            if (child == null)
              break;
            tree.addChild(child);
          }

          boolean problem = true;
          if (t.hasNext()) {
            Token t2 = t.peekNext();
            String s2 = t2.text();
            int type2 = tagType(s2);
            String name2 = null;
            if (type2 == T_END)
              name2 = extractName(s2);
            if (type2 == T_END && tree.tagName.equals(name2)) {
              problem = false;
              tree.endTag = new XMLTree(s2);
              t.next();
            }
          }
          if (problem && problems != null) {
            problems.add(tk);
            problems.add(MISSING_END_TAG);
          }
        }
        break;
      }
    }
    return tree;
  }

  /**
   * Extract name of tag 
   * @param token
   * @return name, or null if not a named token
   */
  private static String extractName(String token) {
    String ret = null;
    int type = tagType(token);
    if (type != T_UNKNOWN) {
      int i = 1;
      if (type == T_END)
        i++;
      int j = i;
      while (j < token.length()) {
        char c = token.charAt(j);
        if (c <= ' ' || c == '/' || c == '>')
          break;
        j++;
      }
      ret = token.substring(i, j);
    }
    return ret;
  }

  private static boolean isNameStartChar(char c) {
    return (c == ':' || c == '_' || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'));

  }

  /**
   * Determine type of tag
   * @param s tag text 
   * @return T_xxx
   */
  private static int tagType(String s) {
    int ret = T_UNKNOWN;
    int len = s.length();
    if (len > 0) {
      if (s.charAt(0) == '<' && s.charAt(len - 1) == '>') {
        if (s.charAt(1) == '/') {
          ret = T_END;
        } else {
          if (isNameStartChar(s.charAt(1))) {
            ret = T_START;
            if (s.charAt(len - 2) == '/') {
              ret = T_EMPTY;
            }
          }
        }
      }
    }
    return ret;
  }

  /**
   * Constructor
   * @param text text of root node
   */
  public XMLTree(String text) {
    this.text = text;
    tagName = extractName(text);
  }

  private static void indent(StringBuilder sb, int indent, String content) {
    if (indent >= 0) {
      Tools.addCr(sb);
      Tools.tab(sb, sb.length() + indent);
      String t = content.trim();
      for (int i = 0; i < t.length(); i++) {
        char c = t.charAt(i);
        sb.append(c);
        if (c == '\n')
          Tools.tab(sb, sb.length() + indent);
      }
      Tools.addCr(sb);
    } else {
      sb.append(content);
    }
  }

  private static int skipPastWS(String str, int cursor) {
    while (cursor < str.length() && str.charAt(cursor) <= ' ')
      cursor++;
    return cursor;
  }

  /**
   * Get attributes for root node
   * @return attribute list, or null if not a start or empty tag 
   * @throws IllegalArgumentException if problem parsing attributes
   */
  public AttributeList getAttributes() {
    final boolean db = true;

    if (attr == null) {
      if (db)
        Streams.out.println("getAttr, tagName=" + tagName + ", called from "
            + Tools.st());

      // if tag is open, create attribute list
      if (tagName != null) {
        if (db)
          Streams.out.println("getAttributes, tagName=[" + tagName + "] text=["
              + text + "]");

        attr = new AttributeList();

        String s = text;

        // trim off <, />, >  
        {
          int k = s.length() - 1;
          if (s.charAt(k - 1) == '/')
            k--;
          s = text.substring(1, k);
        }
        int c = 0;

        if (db)
          Streams.out.println("getAttributes, s=[" + s + "]");

        // skip name
        {
          while (c < s.length() && s.charAt(c) > ' ')
            c++;
          c = skipPastWS(s, c);
        }

        if (db)
          Streams.out.println("getAttributes, s=" + s);

        String problem = "attributes syntax error";
        while (true) {
          c = skipPastWS(s, c);

          if (c == s.length()) {
            problem = null;
            break;
          }
          if (db)
            Streams.out.println("parsing: " + s.substring(c));
          int eq = s.indexOf('=', c);
          if (eq < 0)
            break;
          String name = s.substring(c, eq).trim();

          c = eq + 1;
          if (c == s.length())
            break;

          String value = null;

          char delim = s.charAt(c);
          if (delim == '"' || delim == '\'') {
            c++;
            int k = s.indexOf(delim, c);
            if (k < 0)
              break;
            value = s.substring(c, k);
            c = k + 1;
          } else {
            int k = c;
            while (k < s.length() && s.charAt(k) > ' ')
              k++;
            value = s.substring(c, k);
            c = k;
          }

          String prevValue = attr.set(name, value);
          if (prevValue != null) {
            problem = "duplicate attribute" + name;
            break;
          }

        }
        if (problem != null)
          throw new IllegalArgumentException(problem + ": " + text);
      }
    }
    return attr;
  }
  private static void toString(StringBuilder sb, XMLTree t, int indent) {
    if (t != null) {
      if (t.isOpen())
        throw new IllegalArgumentException("uncompiled tag: " + t.tagName);

      {
        String text = t.text;
        if (indent >= 0) {
          text = text.trim();
          if (text.length() == 0)
            text = null;
        }
        if (text != null)
          indent(sb, indent, text);
      }
      for (int i = 0; i < t.nChildren(); i++)
        toString(sb, t.child(i), (indent < 0) ? indent : indent + 2);
      String et = t.getEndText();
      if (et != null)
        indent(sb, indent, et);
    }
  }

  /**
   * Close any open tags
   */
  public void compile() {
    final boolean db = false;

    if (isOpen()) {
      StringBuilder sb = new StringBuilder();
      sb.append('<');
      sb.append(tagName);

      //      
      //      if (nChildren() == 0 && (attr == null || attr.size() == 0)) {
      //        sb.append("/>");
      //      } else {
      if (attr.size() > 0) {
        for (int i = 0; i < attr.size(); i++) {
          String s = attr.getName(i);
          sb.append(' ');
          sb.append(s);
          sb.append("=\"");
          ENCODER.encode(sb, attr.getValue(i));
          sb.append('"');
        }
      }
      if (nChildren() == 0)
        sb.append("/");
      sb.append(">");
      text = sb.toString();
      if (nChildren() != 0)
        endTag = new XMLTree("</" + tagName + ">");

      if (db)
        Streams.out.println("compiled tagName=" + tagName + " nCh="
            + nChildren() + " text=" + text + " endTag=" + endTag);
    }
    for (int i = 0; i < nChildren(); i++)
      child(i).compile();
  }

  public String toString() {
    return toString(false);
  }

  public String toString(boolean autoIndenting) {
    StringBuilder sb = new StringBuilder();
    toString(sb, this, autoIndenting ? 0 : -1);
    return sb.toString();
  }

  public String getText() {
    return text;
  }

  /**
   * Find subtree with a tag with a particular name.  Returns first subtree
   * found in depth first search.
   * @param name name to look for
   * @return first subtree found with this name, or null
   */
  public XMLTree findTag(String name) {
    XMLTree ret = null;

    if (name.equals(tagName))
      ret = this;
    for (int i = 0; ret == null && i < nChildren(); i++) {
      ret = child(i).findTag(name);
    }
    return ret;
  }

  /**
   * Find subtree with a particular id value.  Returns first subtree
   * found in depth first search.
   * @param id value of id attribute to look for
   * @return first subtree found with this id, or null
   */
  public XMLTree findId(String id) {
    XMLTree ret = null;

    if (tagName != null) {
      AttributeList a = getAttributes();
      String idVal = a.getValueOf("id");
      if (id.equals(idVal)) {
        ret = this;
      }
    }
    for (int i = 0; ret == null && i < nChildren(); i++) {
      ret = child(i).findId(id);
    }
    return ret;
  }

  /**
   * Get text of end tag
   * @return text, or null if root node is not a start tag  
   */
  public String getEndText() {
    return endTag != null ? endTag.text : null;
  }

  /**
   * Get name of tag at root node
   * @return name of tag, or null if not a named tag
   */
  public String getName() {
    return tagName;
  }

  private static class XMLTokenizer implements Iterator {
    public XMLTokenizer(String s, Object context) {
      this.source = s;
      if (context != null)
        this.context = context.toString();
    }

    private void prepareNext() {

      final int TAG_NONE = 0, // user text
      TAG_NORMAL = 1, // <....>
      TAG_REF = 2, // &xxxx;
      TAG_CDATA = 3 // <![CDATA[...]]>
      ;

      int wStart = cursor;

      int tagType = TAG_NONE;

      outer: while (cursor < source.length()) {
        int c = source.charAt(cursor++);

        switch (tagType) {
        case TAG_NONE:
          if (c == '<') {
            if (cursor - wStart == 1)
              tagType = TAG_NORMAL;
            else {
              cursor--;
              break outer;
            }
          } else if (c == '&') {
            if (cursor - wStart == 1)
              tagType = TAG_REF;
            else {
              cursor--;
              break outer;
            }
          }
          break;

        case TAG_NORMAL:
          {
            final String cDataPrefix = "<![CDATA[";
            if (cursor - wStart == cDataPrefix.length()
                && source.substring(wStart, cursor).equals(cDataPrefix)) {
              tagType = TAG_CDATA;
            } else if (c == '>')
              break outer;
          }
          break;
        case TAG_REF:
          if (c == ';') {
            break outer;
          }
          break;
        case TAG_CDATA:
          {
            if (c == '>' && source.charAt(cursor - 2) == ']'
                && source.charAt(cursor - 3) == ']') {
              break outer;
            }
          }
          break;
        }

      }

      if (cursor > wStart) {
        String currLine = source.substring(wStart - column, cursor);
        String str = source.substring(wStart, cursor);
        nextToken = new Token(context, currLine, line, column, str,

        Token.T_USER, null);
        //      Streams.out.println("line=" + line + " col=" + column + " str="
        //          + Tools.d(str));
        for (int i = 0; i < str.length(); i++) {
          char c = str.charAt(i);
          column++;
          if (c == '\n') {
            column = 0;
            line++;
          }
        }
      }
    }
    public boolean hasNext() {
      if (nextToken == null && cursor < source.length())
        prepareNext();
      return nextToken != null;
    }

    public Token peekNext() {
      if (!hasNext())
        throw new IllegalStateException();
      return nextToken;
    }

    public Object next() {
      if (!hasNext())
        throw new IllegalStateException();
      Token ret = nextToken;
      nextToken = null;
      return ret;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    private Token nextToken;
    private int cursor;
    private String source;
    private int line, column;
    private String context;
  }

  /**
   * Add an attribute to the root node
   * @param label
   * @param value
   * @throws IllegalStateException if node is not open
   */
  public void addAttribute(String label, String value) {
    if (!isOpen())
      throw new IllegalStateException();
    getAttributes();
    attr.set(label, value);
  }
  public static StrEncoder ENCODER = new XMLStrEncoder();

  /**
   * Encodes/decodes strings to XML-safe mode.
   * 
   */
  private static class XMLStrEncoder extends StrEncoder {

    private static final String hexDigits = "0123456789abcdef";

    /**
     * Encode a string to 'safe' form, one with appropriate escape codes inserted,
     * and optional quoting
     * @param raw : the raw string to be encoded
     * @param StringBuilder : the StringBuilder to append to
     */
    public void encode(StringBuilder sb, CharSequence raw) {
      for (int i = 0; i < raw.length(); i++) {
        char c = raw.charAt(i);
        switch (c) {
        case '"':
          sb.append("&quot;");
          break;
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        case '\n':
          sb.append(c);
          break;
        default:
          if (c < 0x20 || c > 0x7f) {
            sb.append("&#x");
            boolean zero = true;
            for (int d = 12; d >= 0; d -= 4) {
              int dig = (c >>> d) & 0xf;
              if (d == 0 || dig != 0)
                zero = false;
              if (!zero)
                sb.append(hexDigits.charAt(dig));
            }
            sb.append(';');
            break;
          }
          sb.append(c);
          break;
        }
      }
    }

    /**
     * Decode a string from 'safe' form
     * @param encoded : the string to be decoded
     * @param StringBuilder : the StringBuilder to append to
     */
    public void decode(StringBuilder sb, CharSequence encoded) {
      throw new UnsupportedOperationException();
    }

    private XMLStrEncoder() {
    }

  }

  private XMLTree() {
  }

  /**
   * Create a tree with an open tag.  It must be compiled before it is displayed
   * @param tagName name of tag; it will be used to generate start, end, empty tags as required
   * @return
   */
  public static XMLTree newOpenTag(String tagName) {
    XMLTree t = new XMLTree();
    t.tagName = tagName;
    t.attr = new AttributeList();
    return t;
  }

  public boolean isOpen() {
    return text == null;
  }

  public void stripComments() {

    StringBuilder sb = null;
    boolean prevWS = false;

    DArray newChildren = new DArray();

    for (int i = 0; i < nChildren(); i++) {

      XMLTree c = child(i);

      // strip comments from subtree
      c.stripComments();

      // if this child is a comment, skip it
      if (c.getText().startsWith("<!--"))
        continue;

      // if not a named tag, filter out whitespace
      if (c.getName() == null) {
        String str = c.getText();

        if (sb == null) {
          sb = new StringBuilder();
          prevWS = false;
        }

        for (int j = 0; j < str.length(); j++) {
          char ch = str.charAt(j);
          if (ch <= ' ') {
            if (!prevWS)
              sb.append(' ');
            prevWS = true;
          } else {
            prevWS = false;
            sb.append(ch);
          }
        }
      } else {
        if (sb != null) {
          newChildren.add(new XMLTree(sb.toString()));
          sb = null;
        }
        newChildren.add(c);
      }
    }
    if (sb != null) {
      newChildren.add(new XMLTree(sb.toString()));
    }
    children = newChildren;
  }

  // text of root node
  private String text;

  // tag name, or null: e.g. xxx in  <xxx>, <xxx/>, </xxx>
  private String tagName;

  // end tag corresponding to this tag
  private XMLTree endTag;

  // array of child trees; if null, there are none
  private DArray children;

  // lazy-initialized attribute list
  private AttributeList attr;

}
