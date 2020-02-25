package com.google.zxing.datamatrix.encoder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
public class C40EncoderTestCase extends Assert{
  @Test
  public void C40EncodeTest(){
    StringBuilder stringBuilder=new StringBuilder();
    C40Encoder c40Encoder=new C40Encoder();
    int x1=c40Encoder.encodeChar((char)30,stringBuilder);
    assertEquals(2, x1);
    int x2=c40Encoder.encodeChar(' ',stringBuilder);//32
    assertEquals(1, x2);
    int x3=c40Encoder.encodeChar('/',stringBuilder);//47
    assertEquals(2, x3);
    int x4=c40Encoder.encodeChar('!',stringBuilder);//33
    assertEquals(2, x4);
    int x5=c40Encoder.encodeChar('@',stringBuilder);//64
    assertEquals(2, x5);
    int x6=c40Encoder.encodeChar('<',stringBuilder);//60
    assertEquals(2, x6);
    int x7=c40Encoder.encodeChar('_',stringBuilder);//95
    assertEquals(2, x7);
    int x8=c40Encoder.encodeChar('|',stringBuilder);//124
    assertEquals(2, x8);
    int x9=c40Encoder.encodeChar((char)130,stringBuilder);
    assertEquals(4, x9);
    assertEquals(stringBuilder.toString(),"\0"+(char)30+"\3\1"+(char)14+"\1"+(char)0+
      "\1"+(char)21+"\1"+(char)17+"\1"+(char)26+"\2"+(char)28+"\1\u001e\0"+(char)2);
  }
}
