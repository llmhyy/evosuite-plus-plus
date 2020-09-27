package regression.objectconstruction.testgeneration.example.graphcontruction;

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PngEncoder {
  public static final boolean ENCODE_ALPHA = true;
  
  public static final boolean NO_ALPHA = false;
  
  public static final int FILTER_NONE = 0;
  
  public static final int FILTER_SUB = 1;
  
  public static final int FILTER_UP = 2;
  
  public static final int FILTER_LAST = 2;
  
  protected byte[] pngBytes;
  
  protected byte[] priorRow;
  
  protected byte[] leftBytes;
  
  protected Image image;
  
  protected int width;
  
  protected int height;
  
  protected int bytePos;
  
  protected int maxPos;
  
  protected int hdrPos;
  
  protected int dataPos;
  
  protected int endPos;
  
  protected CRC32 crc = new CRC32();
  
  protected long crcValue;
  
  protected boolean encodeAlpha;
  
  protected int filter;
  
  protected int bytesPerPixel;
  
  protected int compressionLevel;
  
  public PngEncoder() {
    this(null, false, 0, 0);
  }
  
  public PngEncoder(Image image) {
    this(image, false, 0, 0);
  }
  
  public PngEncoder(Image image, boolean encodeAlpha) {
    this(image, encodeAlpha, 0, 0);
  }
  
  public PngEncoder(Image image, boolean encodeAlpha, int whichFilter) {
    this(image, encodeAlpha, whichFilter, 0);
  }
  
  public PngEncoder(Image image, boolean encodeAlpha, int whichFilter, int compLevel) {
    this.image = image;
    this.encodeAlpha = encodeAlpha;
    setFilter(whichFilter);
    if (compLevel >= 0 && compLevel <= 9)
      this.compressionLevel = compLevel; 
  }
  
  public void setImage(Image image) {
    this.image = image;
    this.pngBytes = null;
  }
  
  public byte[] pngEncode(boolean encodeAlpha) {
    byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };
    if (this.image == null)
      return null; 
    this.width = this.image.getWidth(null);
    this.height = this.image.getHeight(null);
    this.image = this.image;
    this.pngBytes = new byte[(this.width + 1) * this.height * 3 + 200];
    this.maxPos = 0;
    this.bytePos = writeBytes(pngIdBytes, 0);
    this.hdrPos = this.bytePos;
    writeHeader();
    this.dataPos = this.bytePos;
    if (writeImageData()) {
      writeEnd();
      this.pngBytes = resizeByteArray(this.pngBytes, this.maxPos);
    } else {
      this.pngBytes = null;
    } 
    return this.pngBytes;
  }
  
  public byte[] pngEncode() {
    return pngEncode(this.encodeAlpha);
  }
  
  public void setEncodeAlpha(boolean encodeAlpha) {
    this.encodeAlpha = encodeAlpha;
  }
  
  public boolean getEncodeAlpha() {
    return this.encodeAlpha;
  }
  
  public void setFilter(int whichFilter) {
    this.filter = 0;
    if (whichFilter <= 2)
      this.filter = whichFilter; 
  }
  
  public int getFilter() {
    return this.filter;
  }
  
  public void setCompressionLevel(int level) {
    if (level >= 0 && level <= 9)
      this.compressionLevel = level; 
  }
  
  public int getCompressionLevel() {
    return this.compressionLevel;
  }
  
  protected byte[] resizeByteArray(byte[] array, int newLength) {
    byte[] newArray = new byte[newLength];
    int oldLength = array.length;
    System.arraycopy(array, 0, newArray, 0, Math.min(oldLength, newLength));
    return newArray;
  }
  
  protected int writeBytes(byte[] data, int offset) {
    this.maxPos = Math.max(this.maxPos, offset + data.length);
    if (data.length + offset > this.pngBytes.length)
      this.pngBytes = resizeByteArray(this.pngBytes, this.pngBytes.length + Math.max(1000, data.length)); 
    System.arraycopy(data, 0, this.pngBytes, offset, data.length);
    return offset + data.length;
  }
  
  protected int writeBytes(byte[] data, int nBytes, int offset) {
    this.maxPos = Math.max(this.maxPos, offset + nBytes);
    if (nBytes + offset > this.pngBytes.length)
      this.pngBytes = resizeByteArray(this.pngBytes, this.pngBytes.length + Math.max(1000, nBytes)); 
    System.arraycopy(data, 0, this.pngBytes, offset, nBytes);
    return offset + nBytes;
  }
  
  protected int writeInt2(int n, int offset) {
    byte[] temp = { (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF) };
    return writeBytes(temp, offset);
  }
  
  protected int writeInt4(int n, int offset) {
    byte[] temp = { (byte)(n >> 24 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF) };
    return writeBytes(temp, offset);
  }
  
  protected int writeByte(int b, int offset) {
    byte[] temp = { (byte)b };
    return writeBytes(temp, offset);
  }
  
  protected int writeString(String s, int offset) {
    return writeBytes(s.getBytes(), offset);
  }
  
  protected void writeHeader() {
    int startPos = this.bytePos = writeInt4(13, this.bytePos);
    this.bytePos = writeString("IHDR", this.bytePos);
    this.width = this.image.getWidth(null);
    this.height = this.image.getHeight(null);
    this.bytePos = writeInt4(this.width, this.bytePos);
    this.bytePos = writeInt4(this.height, this.bytePos);
    this.bytePos = writeByte(8, this.bytePos);
    this.bytePos = writeByte(this.encodeAlpha ? 6 : 2, this.bytePos);
    this.bytePos = writeByte(0, this.bytePos);
    this.bytePos = writeByte(0, this.bytePos);
    this.bytePos = writeByte(0, this.bytePos);
    this.crc.reset();
    this.crc.update(this.pngBytes, startPos, this.bytePos - startPos);
    this.crcValue = this.crc.getValue();
    this.bytePos = writeInt4((int)this.crcValue, this.bytePos);
  }
  
  protected void filterSub(byte[] pixels, int startPos, int width) {
    int offset = this.bytesPerPixel;
    int actualStart = startPos + offset;
    int nBytes = width * this.bytesPerPixel;
    int leftInsert = offset;
    int leftExtract = 0;
    for (int i = actualStart; i < startPos + nBytes; i++) {
      this.leftBytes[leftInsert] = pixels[i];
      pixels[i] = (byte)((pixels[i] - this.leftBytes[leftExtract]) % 256);
      leftInsert = (leftInsert + 1) % 15;
      leftExtract = (leftExtract + 1) % 15;
    } 
  }
  
  protected void filterUp(byte[] pixels, int startPos, int width) {
    int nBytes = width * this.bytesPerPixel;
    for (int i = 0; i < nBytes; i++) {
      byte current_byte = pixels[startPos + i];
      pixels[startPos + i] = (byte)((pixels[startPos + i] - this.priorRow[i]) % 256);
      this.priorRow[i] = current_byte;
    } 
  }
  
  protected boolean writeImageData() {
    int rowsLeft = this.height;
    int startRow = 0;
    this.bytesPerPixel = this.encodeAlpha ? 4 : 3;
    Deflater scrunch = new Deflater(this.compressionLevel);
    ByteArrayOutputStream outBytes = new ByteArrayOutputStream(1024);
    DeflaterOutputStream compBytes = new DeflaterOutputStream(outBytes, scrunch);
    try {
      while (rowsLeft > 0) {
        int nRows = Math.min(32767 / this.width * (this.bytesPerPixel + 1), rowsLeft);
        int[] pixels = new int[this.width * nRows];
        PixelGrabber pg = new PixelGrabber(this.image, 0, startRow, this.width, nRows, pixels, 0, this.width);
        try {
          pg.grabPixels();
        } catch (Exception e) {
          System.err.println("interrupted waiting for pixels!");
          return false;
        } 
        if ((pg.getStatus() & 0x80) != 0) {
          System.err.println("image fetch aborted or errored");
          return false;
        } 
        byte[] scanLines = new byte[this.width * nRows * this.bytesPerPixel + nRows];
        if (this.filter == 1)
          this.leftBytes = new byte[16]; 
        if (this.filter == 2)
          this.priorRow = new byte[this.width * this.bytesPerPixel]; 
        int scanPos = 0;
        int startPos = 1;
        for (int i = 0; i < this.width * nRows; i++) {
          if (i % this.width == 0) {
            scanLines[scanPos++] = (byte)this.filter;
            startPos = scanPos;
          } 
          scanLines[scanPos++] = (byte)(pixels[i] >> 16 & 0xFF);
          scanLines[scanPos++] = (byte)(pixels[i] >> 8 & 0xFF);
          scanLines[scanPos++] = (byte)(pixels[i] & 0xFF);
          if (this.encodeAlpha)
            scanLines[scanPos++] = (byte)(pixels[i] >> 24 & 0xFF); 
          if (i % this.width == this.width - 1 && this.filter != 0) {
            if (this.filter == 1)
              filterSub(scanLines, startPos, this.width); 
            if (this.filter == 2)
              filterUp(scanLines, startPos, this.width); 
          } 
        } 
        compBytes.write(scanLines, 0, scanPos);
        startRow += nRows;
        rowsLeft -= nRows;
      } 
      compBytes.close();
      byte[] compressedLines = outBytes.toByteArray();
      int nCompressed = compressedLines.length;
      this.crc.reset();
      this.bytePos = writeInt4(nCompressed, this.bytePos);
      this.bytePos = writeString("IDAT", this.bytePos);
      this.crc.update("IDAT".getBytes());
      this.bytePos = writeBytes(compressedLines, nCompressed, this.bytePos);
      this.crc.update(compressedLines, 0, nCompressed);
      this.crcValue = this.crc.getValue();
      this.bytePos = writeInt4((int)this.crcValue, this.bytePos);
      scrunch.finish();
      return true;
    } catch (IOException e) {
      System.err.println(e.toString());
      return false;
    } 
  }
  
  protected void writeEnd() {
    this.bytePos = writeInt4(0, this.bytePos);
    this.bytePos = writeString("IEND", this.bytePos);
    this.crc.reset();
    this.crc.update("IEND".getBytes());
    this.crcValue = this.crc.getValue();
    this.bytePos = writeInt4((int)this.crcValue, this.bytePos);
  }
}
