package testbed;

import java.io.*;
import base.*;

class IPEDocument {

  /**
   * Add content to document's "page" tree
   * @param str text to add; it is encoded to be XML-safe
   */
  public void addContent(String str) {
    addContent(null, str);
  }

  /**
   * Add content to tree
   * @param tree tree to add to; if null, adds to previous tree
   * @param str text to add; it is encoded to be XML-safe
   */
  public void addContent(XMLTree tree, String str) {
    if (tree == null)
      tree = prevTree;
    if (prevTree == null)
      throw new IllegalArgumentException("no prev tree");

    tree.addChild(new XMLTree(XMLTree.ENCODER.encode(str)));
  }

  /**
   * Add a new XML element to the page
   * @param tagName tag name
   * @return tree
   */
  public XMLTree addTag(String tagName) {
    XMLTree t = pageTree.addChild(XMLTree.newOpenTag(tagName));
    prevTree = t;
    return t;

  }

  /**
   * Construct IPE document
   */
  public IPEDocument(FRect bounds) {
    xml = XMLTree.newOpenTag("ipe");

    if (bounds.x != 0 || bounds.y != 0)
      throw new IllegalArgumentException("IPE document must start at 0,0");

    XMLTree info = xml.addChild(XMLTree.newOpenTag("info"));

    info.addAttribute("media", IPEGraphics.str(bounds.x)
        + IPEGraphics.str(bounds.y) + IPEGraphics.str(bounds.width)
        + IPEGraphics.str(bounds.height));

    info.addAttribute("pagemode", "fullscreen");

    pageTree = xml.addChild(XMLTree.newOpenTag("page"));
    prevTree = pageTree;
  }

  public synchronized void write(Writer writer) throws IOException {
    // close any open tags
    xml.compile();
    // strip whitespace; makes it more readable; it gets indented when printing out anyways
    xml.stripComments();

    writer.write(xml.toString(true));
    writer.flush();
  }

  public synchronized void flush() throws IOException {
  }

  public synchronized void close() throws IOException {
  }

  private XMLTree xml;
  private XMLTree pageTree;
  private XMLTree prevTree;
}
