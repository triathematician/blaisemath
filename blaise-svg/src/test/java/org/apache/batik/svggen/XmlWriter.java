//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.batik.svggen;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

class XmlWriter implements SVGConstants {
    private static String EOL;
    private static final String TAG_END = "/>";
    private static final String TAG_START = "</";
    private static final char[] SPACES = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
    private static final int SPACES_LEN;

    XmlWriter() {
    }

    private static void writeXml(Attr attr, XmlWriter.IndentWriter out, boolean escaped) throws IOException {
        String name = attr.getName();
        out.write(name);
        out.write("=\"");
        writeChildrenXml(attr, out, escaped);
        out.write(34);
    }

    private static void writeChildrenXml(Attr attr, XmlWriter.IndentWriter out, boolean escaped) throws IOException {
        char[] data = attr.getValue().toCharArray();
        if (data != null) {
            int length = data.length;
            int start = 0;

            int last;
            for(last = 0; last < length; ++last) {
                char c = data[last];
                switch(c) {
                    case '"':
                        out.write(data, start, last - start);
                        start = last + 1;
                        out.write("&quot;");
                        break;
                    case '&':
                        out.write(data, start, last - start);
                        start = last + 1;
                        out.write("&amp;");
                        break;
                    case '<':
                        out.write(data, start, last - start);
                        start = last + 1;
                        out.write("&lt;");
                        break;
                    case '>':
                        out.write(data, start, last - start);
                        start = last + 1;
                        out.write("&gt;");
                        break;
                    default:
                        if (escaped && c > 127) {
                            out.write(data, start, last - start);
                            String hex = "0000" + Integer.toHexString(c);
                            out.write("&#x" + hex.substring(hex.length() - 4) + ";");
                            start = last + 1;
                        }
                }
            }

            out.write(data, start, last - start);
        }
    }

    private static void writeXml(Comment comment, XmlWriter.IndentWriter out, boolean escaped) throws IOException {
        char[] data = comment.getData().toCharArray();
        if (data == null) {
            out.write("<!---->");
        } else {
            out.write("<!--");
            boolean sawDash = false;
            int length = data.length;
            int start = 0;

            int last;
            for(last = 0; last < length; ++last) {
                char c = data[last];
                if (c == '-') {
                    if (sawDash) {
                        out.write(data, start, last - start);
                        start = last;
                        out.write(32);
                    }

                    sawDash = true;
                } else {
                    sawDash = false;
                }
            }

            out.write(data, start, last - start);
            if (sawDash) {
                out.write(32);
            }

            out.write("-->");
        }
    }

    private static void writeXml(Text text, XmlWriter.IndentWriter out, boolean escaped) throws IOException {
        writeXml(text, out, false, escaped);
    }

    private static void writeXml(Text text, XmlWriter.IndentWriter out, boolean trimWS, boolean escaped) throws IOException {
        char[] data = text.getData().toCharArray();
        if (data == null) {
            System.err.println("Null text data??");
        } else {
            int length = data.length;
            int start = 0;
            int last = 0;
            char c;
            if (trimWS) {
                label60:
                while(last < length) {
                    c = data[last];
                    switch(c) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ':
                            ++last;
                            break;
                        default:
                            break label60;
                    }
                }

                start = last;
            }

            int wsStart;
            label51:
            do {
                for(; last < length; ++last) {
                    c = data[last];
                    switch(c) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ':
                            if (trimWS) {
                                wsStart = last++;

                                while(last < length) {
                                    switch(data[last]) {
                                        case '\t':
                                        case '\n':
                                        case '\r':
                                        case ' ':
                                            ++last;
                                            break;
                                        default:
                                            continue label51;
                                    }
                                }
                                continue label51;
                            }
                            break;
                        case '&':
                            out.write(data, start, last - start);
                            start = last + 1;
                            out.write("&amp;");
                            break;
                        case '<':
                            out.write(data, start, last - start);
                            start = last + 1;
                            out.write("&lt;");
                            break;
                        case '>':
                            out.write(data, start, last - start);
                            start = last + 1;
                            out.write("&gt;");
                            break;
                        default:
                            if (escaped && c > 127) {
                                out.write(data, start, last - start);
                                String hex = "0000" + Integer.toHexString(c);
                                out.write("&#x" + hex.substring(hex.length() - 4) + ";");
                                start = last + 1;
                            }
                    }
                }

                out.write(data, start, last - start);
                return;
            } while(last != length);

            out.write(data, start, wsStart - start);
        }
    }

    private static void writeXml(CDATASection cdataSection, XmlWriter.IndentWriter out, boolean escaped) throws IOException {
        char[] data = cdataSection.getData().toCharArray();
        if (data == null) {
            out.write("<![CDATA[]]>");
        } else {
            out.write("<![CDATA[");
            int length = data.length;
            int start = 0;
            int last = 0;

            while(true) {
                while(last < length) {
                    char c = data[last];
                    if (c == ']' && last + 2 < data.length && data[last + 1] == ']' && data[last + 2] == '>') {
                        out.write(data, start, last - start);
                        start = last + 1;
                        out.write("]]]]><![CDATA[>");
                    } else {
                        ++last;
                    }
                }

                out.write(data, start, last - start);
                out.write("]]>");
                return;
            }
        }
    }

    private static void writeXml(Element element, XmlWriter.IndentWriter out, boolean escaped) throws IOException, SVGGraphics2DIOException {
        out.write((String)"</", 0, 1);
        out.write(element.getTagName());
        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null) {
            int nAttr = attributes.getLength();

            for(int i = 0; i < nAttr; ++i) {
                Attr attr = (Attr)attributes.item(i);
                out.write(32);
                writeXml(attr, out, escaped);
            }
        }

        boolean lastElem = element.getParentNode().getLastChild() == element;
        if (!element.hasChildNodes()) {
            if (lastElem) {
                out.setIndentLevel(out.getIndentLevel() - 2);
            }

//            out.printIndent();
            out.write((String)"/>", 0, 2);
        } else {
            Node child = element.getFirstChild();
//            out.printIndent();
            out.write((String)"/>", 1, 1);
            if (child.getNodeType() != 3 || element.getLastChild() != child) {
                out.setIndentLevel(out.getIndentLevel() + 2);
            }

            writeChildrenXml(element, out, escaped);
            out.write((String)"</", 0, 2);
            out.write(element.getTagName());
            if (lastElem) {
                out.setIndentLevel(out.getIndentLevel() - 2);
            }

//            out.printIndent();
            out.write((String)"/>", 1, 1);
        }
    }

    private static void writeChildrenXml(Element element, XmlWriter.IndentWriter out, boolean escaped) throws IOException, SVGGraphics2DIOException {
        for(Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            writeXml((Node)child, (Writer)out, escaped);
        }

    }

    private static void writeDocumentHeader(XmlWriter.IndentWriter out) throws IOException {
        String encoding = null;
        if (out.getProxied() instanceof OutputStreamWriter) {
            OutputStreamWriter osw = (OutputStreamWriter)out.getProxied();
            encoding = java2std(osw.getEncoding());
        }

        out.write("<?xml version=\"1.0\"");
        if (encoding != null) {
            out.write(" encoding=\"");
            out.write(encoding);
            out.write(34);
        }

        out.write("?>");
        out.write(EOL);
        out.write("<!DOCTYPE svg PUBLIC '");
        out.write("-//W3C//DTD SVG 1.0//EN");
        out.write("'");
//        out.write(EOL);
//        out.write("          '");
        out.write(" '");
        out.write("http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
        out.write("'");
        out.write(">");
        out.write(EOL);
    }

    private static void writeXml(Document document, XmlWriter.IndentWriter out, boolean escaped) throws IOException, SVGGraphics2DIOException {
        writeDocumentHeader(out);
        NodeList childList = document.getChildNodes();
        writeXml(childList, out, escaped);
    }

    private static void writeXml(NodeList childList, XmlWriter.IndentWriter out, boolean escaped) throws IOException, SVGGraphics2DIOException {
        int length = childList.getLength();
        if (length != 0) {
            for(int i = 0; i < length; ++i) {
                Node child = childList.item(i);
                writeXml((Node)child, (Writer)out, escaped);
                out.write(EOL);
            }

        }
    }

    static String java2std(String encodingName) {
        if (encodingName == null) {
            return null;
        } else if (encodingName.startsWith("ISO8859_")) {
            return "ISO-8859-" + encodingName.substring(8);
        } else if (encodingName.startsWith("8859_")) {
            return "ISO-8859-" + encodingName.substring(5);
        } else if (!"ASCII7".equalsIgnoreCase(encodingName) && !"ASCII".equalsIgnoreCase(encodingName)) {
            if ("UTF8".equalsIgnoreCase(encodingName)) {
                return "UTF-8";
            } else if (encodingName.startsWith("Unicode")) {
                return "UTF-16";
            } else if ("SJIS".equalsIgnoreCase(encodingName)) {
                return "Shift_JIS";
            } else if ("JIS".equalsIgnoreCase(encodingName)) {
                return "ISO-2022-JP";
            } else {
                return "EUCJIS".equalsIgnoreCase(encodingName) ? "EUC-JP" : "UTF-8";
            }
        } else {
            return "US-ASCII";
        }
    }

    public static void writeXml(Node node, Writer writer, boolean escaped) throws SVGGraphics2DIOException {
        try {
            XmlWriter.IndentWriter out = null;
            if (writer instanceof XmlWriter.IndentWriter) {
                out = (XmlWriter.IndentWriter)writer;
            } else {
                out = new XmlWriter.IndentWriter(writer);
            }

            switch(node.getNodeType()) {
                case 1:
                    writeXml((Element)node, out, escaped);
                    break;
                case 2:
                    writeXml((Attr)node, out, escaped);
                    break;
                case 3:
                    writeXml((Text)node, out, escaped);
                    break;
                case 4:
                    writeXml((CDATASection)node, out, escaped);
                    break;
                case 5:
                case 6:
                case 7:
                case 10:
                default:
                    throw new SVGGraphics2DRuntimeException("Unable to write node of type " + node.getClass().getName());
                case 8:
                    writeXml((Comment)node, out, escaped);
                    break;
                case 9:
                    writeXml((Document)node, out, escaped);
                    break;
                case 11:
                    writeDocumentHeader(out);
                    NodeList childList = node.getChildNodes();
                    writeXml(childList, out, escaped);
            }

        } catch (IOException var5) {
            throw new SVGGraphics2DIOException(var5);
        }
    }

    static {
        SPACES_LEN = SPACES.length;

        String temp;
        try {
            temp = System.getProperty("line.separator", "\n");
        } catch (SecurityException var2) {
            temp = "\n";
        }

        EOL = temp;
    }

    static class IndentWriter extends Writer {
        protected Writer proxied;
        protected int indentLevel;
        protected int column;

        public IndentWriter(Writer proxied) {
            if (proxied == null) {
                throw new SVGGraphics2DRuntimeException("proxy should not be null");
            } else {
                this.proxied = proxied;
            }
        }

        public void setIndentLevel(int indentLevel) {
            this.indentLevel = indentLevel;
        }

        public int getIndentLevel() {
            return this.indentLevel;
        }

        public void printIndent() throws IOException {
            this.proxied.write(XmlWriter.EOL);

            for(int temp = this.indentLevel; temp > 0; temp -= XmlWriter.SPACES_LEN) {
                if (temp <= XmlWriter.SPACES_LEN) {
                    this.proxied.write(XmlWriter.SPACES, 0, temp);
                    break;
                }

                this.proxied.write(XmlWriter.SPACES, 0, XmlWriter.SPACES_LEN);
            }

            this.column = this.indentLevel;
        }

        public Writer getProxied() {
            return this.proxied;
        }

        public int getColumn() {
            return this.column;
        }

        public void write(int c) throws IOException {
            ++this.column;
            this.proxied.write(c);
        }

        public void write(char[] cbuf) throws IOException {
            this.column += cbuf.length;
            this.proxied.write(cbuf);
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            this.column += len;
            this.proxied.write(cbuf, off, len);
        }

        public void write(String str) throws IOException {
            this.column += str.length();
            this.proxied.write(str);
        }

        public void write(String str, int off, int len) throws IOException {
            this.column += len;
            this.proxied.write(str, off, len);
        }

        public void flush() throws IOException {
            this.proxied.flush();
        }

        public void close() throws IOException {
            this.column = -1;
            this.proxied.close();
        }
    }
}
