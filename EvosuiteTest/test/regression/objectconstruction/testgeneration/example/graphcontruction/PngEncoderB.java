package regression.objectconstruction.testgeneration.example.graphcontruction;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

public class PngEncoderB extends PngEncoder {
  protected BufferedImage image;
  protected WritableRaster wRaster;
  protected int tType;

  public byte[] pngEncode(boolean encodeAlpha) {
    byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };
    if (this.image == null)
      return null; 
    this.width = this.image.getWidth(null);
    this.height = this.image.getHeight(null);
    this.image = this.image;
    if (!establishStorageInfo())
      return null; 
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
  
  protected boolean establishStorageInfo() {
    this.wRaster = this.image.getRaster();
    int dataBytes = this.wRaster.getNumDataElements();
    this.tType = this.wRaster.getTransferType();
    if ((this.tType == 0 && dataBytes == 4) || (this.tType == 3 && dataBytes == 1)) {
      this.bytesPerPixel = this.encodeAlpha ? 4 : 3;
    } else if (this.tType == 0 && dataBytes == 1) {
      this.bytesPerPixel = 1;
      this.encodeAlpha = false;
    } else {
      return false;
    } 
    return true;
  }
  
  protected void writeHeader() {
    int startPos = this.bytePos = writeInt4(13, this.bytePos);
    this.bytePos = writeString("IHDR", this.bytePos);
    this.width = this.image.getWidth(null);
    this.height = this.image.getHeight(null);
    this.bytePos = writeInt4(this.width, this.bytePos);
    this.bytePos = writeInt4(this.height, this.bytePos);
    this.bytePos = writeByte(8, this.bytePos);
    if (this.bytesPerPixel != 1) {
      this.bytePos = writeByte(this.encodeAlpha ? 6 : 2, this.bytePos);
    } else {
      this.bytePos = writeByte(3, this.bytePos);
    } 
    this.bytePos = writeByte(0, this.bytePos);
    this.bytePos = writeByte(0, this.bytePos);
    this.bytePos = writeByte(0, this.bytePos);
    this.crc.reset();
    this.crc.update(this.pngBytes, startPos, this.bytePos - startPos);
    this.crcValue = this.crc.getValue();
    this.bytePos = writeInt4((int)this.crcValue, this.bytePos);
  }

  protected boolean writeImageData() {
	  return true;
    // Byte code:
    //   0: aload_0
    //   1: getfield height : I
    //   4: istore_1
    //   5: iconst_0
    //   6: istore_2
    //   7: new java/util/zip/Deflater
    //   10: dup
    //   11: aload_0
    //   12: getfield compressionLevel : I
    //   15: invokespecial <init> : (I)V
    //   18: astore #12
    //   20: new java/io/ByteArrayOutputStream
    //   23: dup
    //   24: sipush #1024
    //   27: invokespecial <init> : (I)V
    //   30: astore #13
    //   32: new java/util/zip/DeflaterOutputStream
    //   35: dup
    //   36: aload #13
    //   38: aload #12
    //   40: invokespecial <init> : (Ljava/io/OutputStream;Ljava/util/zip/Deflater;)V
    //   43: astore #14
    //   45: aload_0
    //   46: getfield bytesPerPixel : I
    //   49: iconst_1
    //   50: if_icmpne -> 67
    //   53: aload_0
    //   54: aload_0
    //   55: getfield image : Ljava/awt/image/BufferedImage;
    //   58: invokevirtual getColorModel : ()Ljava/awt/image/ColorModel;
    //   61: checkcast java/awt/image/IndexColorModel
    //   64: invokevirtual writePalette : (Ljava/awt/image/IndexColorModel;)V
    //   67: iload_1
    //   68: ifle -> 554
    //   71: sipush #32767
    //   74: aload_0
    //   75: getfield width : I
    //   78: aload_0
    //   79: getfield bytesPerPixel : I
    //   82: iconst_1
    //   83: iadd
    //   84: imul
    //   85: idiv
    //   86: iload_1
    //   87: invokestatic min : (II)I
    //   90: istore_3
    //   91: aload_0
    //   92: getfield width : I
    //   95: iload_3
    //   96: imul
    //   97: aload_0
    //   98: getfield bytesPerPixel : I
    //   101: imul
    //   102: iload_3
    //   103: iadd
    //   104: newarray byte
    //   106: astore #4
    //   108: aload_0
    //   109: getfield filter : I
    //   112: iconst_1
    //   113: if_icmpne -> 124
    //   116: aload_0
    //   117: bipush #16
    //   119: newarray byte
    //   121: putfield leftBytes : [B
    //   124: aload_0
    //   125: getfield filter : I
    //   128: iconst_2
    //   129: if_icmpne -> 147
    //   132: aload_0
    //   133: aload_0
    //   134: getfield width : I
    //   137: aload_0
    //   138: getfield bytesPerPixel : I
    //   141: imul
    //   142: newarray byte
    //   144: putfield priorRow : [B
    //   147: aload_0
    //   148: getfield tType : I
    //   151: ifne -> 183
    //   154: aload_0
    //   155: getfield wRaster : Ljava/awt/image/WritableRaster;
    //   158: iconst_0
    //   159: iload_2
    //   160: aload_0
    //   161: getfield width : I
    //   164: iload_3
    //   165: aconst_null
    //   166: invokevirtual getDataElements : (IIIILjava/lang/Object;)Ljava/lang/Object;
    //   169: checkcast [B
    //   172: checkcast [B
    //   175: astore #10
    //   177: aconst_null
    //   178: astore #11
    //   180: goto -> 209
    //   183: aload_0
    //   184: getfield wRaster : Ljava/awt/image/WritableRaster;
    //   187: iconst_0
    //   188: iload_2
    //   189: aload_0
    //   190: getfield width : I
    //   193: iload_3
    //   194: aconst_null
    //   195: invokevirtual getDataElements : (IIIILjava/lang/Object;)Ljava/lang/Object;
    //   198: checkcast [I
    //   201: checkcast [I
    //   204: astore #11
    //   206: aconst_null
    //   207: astore #10
    //   209: iconst_0
    //   210: istore #5
    //   212: iconst_0
    //   213: istore #7
    //   215: iconst_1
    //   216: istore #6
    //   218: iconst_0
    //   219: istore #15
    //   221: iload #15
    //   223: aload_0
    //   224: getfield width : I
    //   227: iload_3
    //   228: imul
    //   229: if_icmpge -> 533
    //   232: iload #15
    //   234: aload_0
    //   235: getfield width : I
    //   238: irem
    //   239: ifne -> 259
    //   242: aload #4
    //   244: iload #5
    //   246: iinc #5, 1
    //   249: aload_0
    //   250: getfield filter : I
    //   253: i2b
    //   254: bastore
    //   255: iload #5
    //   257: istore #6
    //   259: aload_0
    //   260: getfield bytesPerPixel : I
    //   263: iconst_1
    //   264: if_icmpne -> 286
    //   267: aload #4
    //   269: iload #5
    //   271: iinc #5, 1
    //   274: aload #10
    //   276: iload #7
    //   278: iinc #7, 1
    //   281: baload
    //   282: bastore
    //   283: goto -> 464
    //   286: aload_0
    //   287: getfield tType : I
    //   290: ifne -> 373
    //   293: aload #4
    //   295: iload #5
    //   297: iinc #5, 1
    //   300: aload #10
    //   302: iload #7
    //   304: iinc #7, 1
    //   307: baload
    //   308: bastore
    //   309: aload #4
    //   311: iload #5
    //   313: iinc #5, 1
    //   316: aload #10
    //   318: iload #7
    //   320: iinc #7, 1
    //   323: baload
    //   324: bastore
    //   325: aload #4
    //   327: iload #5
    //   329: iinc #5, 1
    //   332: aload #10
    //   334: iload #7
    //   336: iinc #7, 1
    //   339: baload
    //   340: bastore
    //   341: aload_0
    //   342: getfield encodeAlpha : Z
    //   345: ifeq -> 367
    //   348: aload #4
    //   350: iload #5
    //   352: iinc #5, 1
    //   355: aload #10
    //   357: iload #7
    //   359: iinc #7, 1
    //   362: baload
    //   363: bastore
    //   364: goto -> 464
    //   367: iinc #7, 1
    //   370: goto -> 464
    //   373: aload #4
    //   375: iload #5
    //   377: iinc #5, 1
    //   380: aload #11
    //   382: iload #7
    //   384: iaload
    //   385: bipush #16
    //   387: ishr
    //   388: sipush #255
    //   391: iand
    //   392: i2b
    //   393: bastore
    //   394: aload #4
    //   396: iload #5
    //   398: iinc #5, 1
    //   401: aload #11
    //   403: iload #7
    //   405: iaload
    //   406: bipush #8
    //   408: ishr
    //   409: sipush #255
    //   412: iand
    //   413: i2b
    //   414: bastore
    //   415: aload #4
    //   417: iload #5
    //   419: iinc #5, 1
    //   422: aload #11
    //   424: iload #7
    //   426: iaload
    //   427: sipush #255
    //   430: iand
    //   431: i2b
    //   432: bastore
    //   433: aload_0
    //   434: getfield encodeAlpha : Z
    //   437: ifeq -> 461
    //   440: aload #4
    //   442: iload #5
    //   444: iinc #5, 1
    //   447: aload #11
    //   449: iload #7
    //   451: iaload
    //   452: bipush #24
    //   454: ishr
    //   455: sipush #255
    //   458: iand
    //   459: i2b
    //   460: bastore
    //   461: iinc #7, 1
    //   464: iload #15
    //   466: aload_0
    //   467: getfield width : I
    //   470: irem
    //   471: aload_0
    //   472: getfield width : I
    //   475: iconst_1
    //   476: isub
    //   477: if_icmpne -> 527
    //   480: aload_0
    //   481: getfield filter : I
    //   484: ifeq -> 527
    //   487: aload_0
    //   488: getfield filter : I
    //   491: iconst_1
    //   492: if_icmpne -> 507
    //   495: aload_0
    //   496: aload #4
    //   498: iload #6
    //   500: aload_0
    //   501: getfield width : I
    //   504: invokevirtual filterSub : ([BII)V
    //   507: aload_0
    //   508: getfield filter : I
    //   511: iconst_2
    //   512: if_icmpne -> 527
    //   515: aload_0
    //   516: aload #4
    //   518: iload #6
    //   520: aload_0
    //   521: getfield width : I
    //   524: invokevirtual filterUp : ([BII)V
    //   527: iinc #15, 1
    //   530: goto -> 221
    //   533: aload #14
    //   535: aload #4
    //   537: iconst_0
    //   538: iload #5
    //   540: invokevirtual write : ([BII)V
    //   543: iload_2
    //   544: iload_3
    //   545: iadd
    //   546: istore_2
    //   547: iload_1
    //   548: iload_3
    //   549: isub
    //   550: istore_1
    //   551: goto -> 67
    //   554: aload #14
    //   556: invokevirtual close : ()V
    //   559: aload #13
    //   561: invokevirtual toByteArray : ()[B
    //   564: astore #8
    //   566: aload #8
    //   568: arraylength
    //   569: istore #9
    //   571: aload_0
    //   572: getfield crc : Ljava/util/zip/CRC32;
    //   575: invokevirtual reset : ()V
    //   578: aload_0
    //   579: aload_0
    //   580: iload #9
    //   582: aload_0
    //   583: getfield bytePos : I
    //   586: invokevirtual writeInt4 : (II)I
    //   589: putfield bytePos : I
    //   592: aload_0
    //   593: aload_0
    //   594: ldc 'IDAT'
    //   596: aload_0
    //   597: getfield bytePos : I
    //   600: invokevirtual writeString : (Ljava/lang/String;I)I
    //   603: putfield bytePos : I
    //   606: aload_0
    //   607: getfield crc : Ljava/util/zip/CRC32;
    //   610: ldc 'IDAT'
    //   612: invokevirtual getBytes : ()[B
    //   615: invokevirtual update : ([B)V
    //   618: aload_0
    //   619: aload_0
    //   620: aload #8
    //   622: iload #9
    //   624: aload_0
    //   625: getfield bytePos : I
    //   628: invokevirtual writeBytes : ([BII)I
    //   631: putfield bytePos : I
    //   634: aload_0
    //   635: getfield crc : Ljava/util/zip/CRC32;
    //   638: aload #8
    //   640: iconst_0
    //   641: iload #9
    //   643: invokevirtual update : ([BII)V
    //   646: aload_0
    //   647: aload_0
    //   648: getfield crc : Ljava/util/zip/CRC32;
    //   651: invokevirtual getValue : ()J
    //   654: putfield crcValue : J
    //   657: aload_0
    //   658: aload_0
    //   659: aload_0
    //   660: getfield crcValue : J
    //   663: l2i
    //   664: aload_0
    //   665: getfield bytePos : I
    //   668: invokevirtual writeInt4 : (II)I
    //   671: putfield bytePos : I
    //   674: aload #12
    //   676: invokevirtual finish : ()V
    //   679: iconst_1
    //   680: ireturn
    //   681: astore #15
    //   683: getstatic java/lang/System.err : Ljava/io/PrintStream;
    //   686: aload #15
    //   688: invokevirtual toString : ()Ljava/lang/String;
    //   691: invokevirtual println : (Ljava/lang/String;)V
    //   694: iconst_0
    //   695: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #294	-> 0
    //   #295	-> 5
    //   #309	-> 7
    //   #310	-> 20
    //   #313	-> 32
    //   #316	-> 45
    //   #318	-> 53
    //   #323	-> 67
    //   #325	-> 71
    //   #332	-> 91
    //   #334	-> 108
    //   #336	-> 116
    //   #338	-> 124
    //   #340	-> 132
    //   #343	-> 147
    //   #345	-> 154
    //   #347	-> 177
    //   #351	-> 183
    //   #353	-> 206
    //   #356	-> 209
    //   #357	-> 212
    //   #358	-> 215
    //   #359	-> 218
    //   #361	-> 232
    //   #363	-> 242
    //   #364	-> 255
    //   #367	-> 259
    //   #369	-> 267
    //   #371	-> 286
    //   #373	-> 293
    //   #374	-> 309
    //   #375	-> 325
    //   #376	-> 341
    //   #378	-> 348
    //   #382	-> 367
    //   #387	-> 373
    //   #388	-> 394
    //   #389	-> 415
    //   #390	-> 433
    //   #392	-> 440
    //   #394	-> 461
    //   #396	-> 464
    //   #398	-> 487
    //   #400	-> 495
    //   #402	-> 507
    //   #404	-> 515
    //   #359	-> 527
    //   #412	-> 533
    //   #414	-> 543
    //   #415	-> 547
    //   #417	-> 554
    //   #422	-> 559
    //   #423	-> 566
    //   #425	-> 571
    //   #426	-> 578
    //   #427	-> 592
    //   #428	-> 606
    //   #429	-> 618
    //   #430	-> 634
    //   #432	-> 646
    //   #433	-> 657
    //   #434	-> 674
    //   #435	-> 679
    //   #437	-> 681
    //   #439	-> 683
    //   #440	-> 694
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   177	6	10	pixels	[B
    //   180	3	11	iPixels	[I
    //   221	312	15	i	I
    //   91	463	3	nRows	I
    //   108	446	4	scanLines	[B
    //   212	342	5	scanPos	I
    //   218	336	6	startPos	I
    //   215	339	7	readPos	I
    //   209	345	10	pixels	[B
    //   206	348	11	iPixels	[I
    //   683	13	15	e	Ljava/io/IOException;
    //   0	696	0	this	Lcorina/map/PngEncoderB;
    //   5	691	1	rowsLeft	I
    //   7	689	2	startRow	I
    //   566	130	8	compressedLines	[B
    //   571	125	9	nCompressed	I
    //   20	676	12	scrunch	Ljava/util/zip/Deflater;
    //   32	664	13	outBytes	Ljava/io/ByteArrayOutputStream;
    //   45	651	14	compBytes	Ljava/util/zip/DeflaterOutputStream;
    // Exception table:
    //   from	to	target	type
    //   67	680	681	java/io/IOException
  }
}
