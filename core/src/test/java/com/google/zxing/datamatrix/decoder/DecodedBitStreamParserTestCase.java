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

package com.google.zxing.datamatrix.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.BitSource;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author bbrown@google.com (Brian Brown)
 */
public final class DecodedBitStreamParserTestCase extends Assert {

  private enum Mode {
    PAD_ENCODE, // Not really a mode
    ASCII_ENCODE,
    C40_ENCODE,
    TEXT_ENCODE,
    ANSIX12_ENCODE,
    EDIFACT_ENCODE,
    BASE256_ENCODE
  }

  private Mode testModeTransform(BitSource bits,
                                 StringBuilder result,
                                 StringBuilder resultTrailer) throws FormatException {
    boolean upperShift = false;
    do {
      int oneByte = bits.readBits(8);
      if (oneByte == 0) {
        throw FormatException.getFormatInstance();
      } else if (oneByte <= 128) {  // ASCII data (ASCII value + 1)
        if (upperShift) {
          oneByte += 128;
          //upperShift = false;
        }
        result.append((char) (oneByte - 1));
        return Mode.ASCII_ENCODE;
      } else if (oneByte == 129) {  // Pad
        return Mode.PAD_ENCODE;
      } else if (oneByte <= 229) {  // 2-digit data 00-99 (Numeric Value + 130)
        int value = oneByte - 130;
        if (value < 10) { // pad with '0' for single digit values
          result.append('0');
        }
        result.append(value);
      } else {
        switch (oneByte) {
          case 230: // Latch to C40 encodation
            return Mode.C40_ENCODE;
          case 231: // Latch to Base 256 encodation
            return Mode.BASE256_ENCODE;
          case 232: // FNC1
            result.append((char) 29); // translate as ASCII 29
            break;
          case 233: // Structured Append
          case 234: // Reader Programming
            // Ignore these symbols for now
            //throw ReaderException.getInstance();
            break;
          case 235: // Upper Shift (shift to Extended ASCII)
            upperShift = true;
            break;
          case 236: // 05 Macro
            result.append("[)>\u001E05\u001D");
            resultTrailer.insert(0, "\u001E\u0004");
            break;
          case 237: // 06 Macro
            result.append("[)>\u001E06\u001D");
            resultTrailer.insert(0, "\u001E\u0004");
            break;
          case 238: // Latch to ANSI X12 encodation
            return Mode.ANSIX12_ENCODE;
          case 239: // Latch to Text encodation
            return Mode.TEXT_ENCODE;
          case 240: // Latch to EDIFACT encodation
            return Mode.EDIFACT_ENCODE;
          case 241: // ECI Character
            // TODO(bbrown): I think we need to support ECI
            //throw ReaderException.getInstance();
            // Ignore this symbol for now
            break;
          default:
            // Not to be used in ASCII encodation
            // but work around encoders that end with 254, latch back to ASCII
            if (oneByte != 254 || bits.available() != 0) {
              throw FormatException.getFormatInstance();
            }
            break;
        }
      }
    } while (bits.available() > 0);
    return Mode.ASCII_ENCODE;
  }

  @Test
  public void testAsciiStandardDecode() throws Exception {
    // ASCII characters 0-127 are encoded as the value + 1
    byte[] bytes = {(byte) ('a' + 1), (byte) ('b' + 1), (byte) ('c' + 1),
                    (byte) ('A' + 1), (byte) ('B' + 1), (byte) ('C' + 1)};
    String decodedString = DecodedBitStreamParser.decode(bytes).getText();
    assertEquals("abcABC", decodedString);
  }

  @Test
  public void testAsciiDoubleDigitDecode() throws Exception {
    // ASCII double digit (00 - 99) Numeric Value + 130
    byte[] bytes = {(byte)       130 , (byte) (1 + 130),
                    (byte) (98 + 130), (byte) (99 + 130)};
    String decodedString = DecodedBitStreamParser.decode(bytes).getText();
    assertEquals("00019899", decodedString);
  }

  @Test
  public void testDecodeModeChangeC40() throws Exception{
    byte[] bytes={(byte)230};
    BitSource bits = new BitSource(bytes);
    StringBuilder result = new StringBuilder(100);
    StringBuilder resultTrailer = new StringBuilder(0);
    Mode mo=testModeTransform(bits,result,resultTrailer);
    assertEquals(mo,Mode.C40_ENCODE);
  }

  @Test
  public void testDecodeC40() throws Exception{
    //From HighLevelEncodeTestCase.java, we can get some Strings encoded in different types
    System.out.println("C40 Mode Change Test:");
    byte[] bytes={(byte)230,(byte)91,(byte)11};
    assertEquals(DecodedBitStreamParser.decode(bytes).getText(),"AIM");
  }

  @Test
  public void testDecodeModeChangeTEXT() throws Exception{
    byte[] bytes={(byte)239};
    BitSource bits = new BitSource(bytes);
    StringBuilder result = new StringBuilder(100);
    StringBuilder resultTrailer = new StringBuilder(0);
    Mode mo=testModeTransform(bits,result,resultTrailer);
    assertEquals(mo,Mode.TEXT_ENCODE);
  }

  @Test
  public void testDecodeTEXT() throws Exception{
    //From HighLevelEncodeTestCase.java, we can get some Strings encoded in different types
    System.out.println("TEXT Mode Change Test:");
    byte[] bytes={(byte)239,(byte)91,(byte)11,(byte)254,(byte)67,(byte)129};
    assertEquals(DecodedBitStreamParser.decode(bytes).getText(),"aimB");
  }

  @Test
  public void testDecodeModeChangeANSIX12() throws Exception{
    byte[] bytes={(byte)238};
    BitSource bits = new BitSource(bytes);
    StringBuilder result = new StringBuilder(100);
    StringBuilder resultTrailer = new StringBuilder(0);
    Mode mo=testModeTransform(bits,result,resultTrailer);
    assertEquals(mo,Mode.ANSIX12_ENCODE);
  }

  @Test
  public void testDecodeANSIX12() throws Exception{
    //From HighLevelEncodeTestCase.java, we can get some Strings encoded in different types
    System.out.println("ANSIX12 Mode Change Test:");
    //238 89 233 14 192 100 207 44 31 67 ||  ABC>ABC123>AB
    byte[] bytes={(byte)238,(byte)89,(byte)233,(byte)14,(byte)192,(byte)100,(byte)207,(byte)44,(byte)31,(byte)67};
    assertEquals(DecodedBitStreamParser.decode(bytes).getText(),"ABC>ABC123>AB");
  }

  @Test
  public void testDecodeModeChangeEDIFACT() throws Exception{
    byte[] bytes={(byte)240};
    BitSource bits = new BitSource(bytes);
    StringBuilder result = new StringBuilder(100);
    StringBuilder resultTrailer = new StringBuilder(0);
    Mode mo=testModeTransform(bits,result,resultTrailer);
    assertEquals(mo,Mode.EDIFACT_ENCODE);
  }

  @Test
  public void testDecodeEDIFACT() throws Exception{
    //From HighLevelEncodeTestCase.java, we can get some Strings encoded in different types
    System.out.println("EDIFACT Mode Change Test:");
    //240 184 27 131 198 236 238 98 230 50 47 47  ||  .A.C1.3.X.X2..
    byte[] bytes={(byte)240,(byte)184,(byte)27,(byte)131,(byte)198,(byte)236,(byte)238,(byte)98,(byte)230,(byte)50,(byte)47,(byte)47};
    assertEquals(DecodedBitStreamParser.decode(bytes).getText(),".A.C1.3.X.X2..");
  }

  @Test
  public void testDecodeModeChangeBASE256() throws Exception{
    byte[] bytes={(byte)231};
    BitSource bits = new BitSource(bytes);
    StringBuilder result = new StringBuilder(100);
    StringBuilder resultTrailer = new StringBuilder(0);
    Mode mo=testModeTransform(bits,result,resultTrailer);
    assertEquals(mo,Mode.BASE256_ENCODE);
  }

  @Test
  public void testDecodeBASE256() throws Exception{
    //From HighLevelEncodeTestCase.java, we can get some Strings encoded in different types
    System.out.println("BASE256 Mode Change Test:");
    //231 51 108 59 226 126 1 141 254 129   ||  \u00ABäöüéà\u00BB
    byte[] bytes={(byte)231,(byte)51,(byte)108,(byte)59,(byte)226,(byte)126,(byte)1,(byte)141,(byte)254,(byte)129};
    assertEquals(DecodedBitStreamParser.decode(bytes).getText(),"\u00ABäöüéà\u00BB");
  }

  @Test
  public void testDecodeModeChangePAD() throws Exception{
    byte[] bytes={(byte)129};
    BitSource bits = new BitSource(bytes);
    StringBuilder result = new StringBuilder(100);
    StringBuilder resultTrailer = new StringBuilder(0);
    Mode mo=testModeTransform(bits,result,resultTrailer);
    assertEquals(mo,Mode.PAD_ENCODE);
  }
  @Test
  public void testDecodePAD() throws Exception{
    //From HighLevelEncodeTestCase.java, we can get some Strings encoded in different types
    System.out.println("PAD Mode Change Test:");
    //231 51 108 59 226 126 1 141 254 129   ||  \u00ABäöüéà\u00BB
    byte[] bytes={(byte)130,(byte)131,(byte)129,(byte)135,(byte)137};
    assertEquals(DecodedBitStreamParser.decode(bytes).getText(),"0001");
  }
  // TODO(bbrown): Add test cases for each encoding type
  // TODO(bbrown): Add test cases for switching encoding types
}
