/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.qrcode.decoder;

import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitSource;
import com.google.zxing.common.BitSourceBuilder;
import com.google.zxing.common.StringUtils;
import com.google.zxing.oned.UPCEANReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.zxing.DecodeHintType;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
/**
 * Tests {@link DecodedBitStreamParser}.
 *
 * @author Sean Owen
 */
@PrepareForTest(StringUtils.class)
@RunWith(PowerMockRunner.class)
public class DecodedBitStreamParserTestCase extends Assert {
//  @Before
//  public void before(){
//    PowerMockito.mockStatic(StringUtils.class);
//  }
  @Test
  public void testSimpleByteMode() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x04, 4); // Byte mode
    builder.write(0x03, 8); // 3 bytes
    builder.write(0xF1, 8);
    builder.write(0xF2, 8);
    builder.write(0xF3, 8);
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
        Version.getVersionForNumber(1), null, null).getText();
    assertEquals("\u00f1\u00f2\u00f3", result);
  }

  @Test
  public void testSimpleSJIS() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x04, 4); // Byte mode
    builder.write(0x04, 8); // 4 bytes
    builder.write(0xA1, 8);
    builder.write(0xA2, 8);
    builder.write(0xA3, 8);
    builder.write(0xD0, 8);
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
        Version.getVersionForNumber(1), null, null).getText();
    assertEquals("\uff61\uff62\uff63\uff90", result);
  }

  @Test
  public void testECI() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x07, 4); // ECI mode
    builder.write(0x02, 8); // ECI 2 = CP437 encoding
    builder.write(0x04, 4); // Byte mode
    builder.write(0x03, 8); // 3 bytes
    builder.write(0xA1, 8);
    builder.write(0xA2, 8);
    builder.write(0xA3, 8);
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
        Version.getVersionForNumber(1), null, null).getText();
    assertEquals("\u00ed\u00f3\u00fa", result);
  }

  @Test
  public void testHanzi() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x0D, 4); // Hanzi mode
    builder.write(0x01, 4); // Subset 1 = GB2312 encoding
    builder.write(0x01, 8); // 1 characters
    builder.write(0x03C1, 13);
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
        Version.getVersionForNumber(1), null, null).getText();
    assertEquals("\u963f", result);
  }

  @Test
  public void testHanziLevel1() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x0D, 4); // Hanzi mode
    builder.write(0x01, 4); // Subset 1 = GB2312 encoding
    builder.write(0x01, 8); // 1 characters
    // A5A2 (U+30A2) => A5A2 - A1A1 = 401, 4*60 + 01 = 0181
    builder.write(0x0181, 13);
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
        Version.getVersionForNumber(1), null, null).getText();
    assertEquals("\u30a2", result);
  }

  @Test
  public void testdecodeNumeric() throws Exception{
    StringBuilder stringBuilder=new StringBuilder();
    BitSourceBuilder bitSourceBuilder=new BitSourceBuilder();
    bitSourceBuilder.write(357,10);
    byte[] a=bitSourceBuilder.toByteArray();
    BitSource bitSource=new BitSource(a);
    DecodedBitStreamParser.decodeNumericSegment1(bitSource,stringBuilder,3);
    assertEquals("357",stringBuilder.toString());
    try {
      DecodedBitStreamParser.decodeNumericSegment1(bitSource,stringBuilder,2);
    }
    catch (FormatException e){
      e.printStackTrace();
    }
  }
  // TODO definitely need more tests here
  @Test
  public void mocktestdecodeByteSegment() throws Exception{

    StringBuilder stringBuilder=new StringBuilder();
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x04, 4); // Byte mode
    builder.write(0x03, 8); // 3 bytes
    builder.write(0xF1, 8);
    builder.write(0xF2, 8);
    builder.write(0xF3, 8);
    BitSource bitSource=new BitSource(builder.toByteArray());
    List<byte[]> byteSegments = new ArrayList<>(1);
    PowerMockito.mockStatic(StringUtils.class);
    //when(StringUtils.guessEncoding(Mockito.any(),Mockito.any())).thenReturn("FOOO2");
    doThrow(new Exception()).when(StringUtils.guessEncoding(any(),any()));
    try{

      DecodedBitStreamParser.decodeByteSegment1(bitSource,stringBuilder,3,null,
        byteSegments,null);
    }
    catch (FormatException e){
      e.printStackTrace();
      
    }
    //when(StringUtils.guessEncoding(builder.toByteArray(),null)).thenReturn("FOOO2");
    //StringUtils stringUtils=mock(StringUtils.class);
    //PowerMockito.doReturn("FOOO2").when(StringUtils.guessEncoding(isA(byte[].class),isA(Map.class)));
    //StringUtils stringUtils=mock(StringUtils.class);
//    when(StringUtils.guessEncoding(isA(byte[].class),isA(Map.class))).thenReturn("FOOO2");
//    when(StringUtils.guessEncoding(builder.toByteArray(),null)).thenReturn("FOOO2");
//    when(StringUtils.guessEncoding(any(byte[].class),any(Map.class))).thenReturn("FOOO2");
//    PowerMockito.verifyStatic(StringUtils.class,Mockito.times(1));
//    StringUtils.guessEncoding(Mockito.any(),Mockito.any());
//and when I remove the static before guessEncoding, the mock works
  }
}
