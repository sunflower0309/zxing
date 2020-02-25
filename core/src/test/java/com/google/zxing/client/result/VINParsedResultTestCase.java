/*
 * Copyright 2014 ZXing authors
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


package com.google.zxing.client.result;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Tests {@link VINParsedResult}.
 */
public final class VINParsedResultTestCase extends Assert {

  @Test
  public void testNotVIN() {
    Result fakeResult = new Result("1M8GDM9A1KP042788", null, null, BarcodeFormat.CODE_39);
    ParsedResult result = ResultParser.parseResult(fakeResult);
    assertEquals(ParsedResultType.TEXT, result.getType());
    fakeResult = new Result("1M8GDM9AXKP042788", null, null, BarcodeFormat.CODE_128);
    result = ResultParser.parseResult(fakeResult);
    assertEquals(ParsedResultType.TEXT, result.getType());
  }

  @Test
  public void testVIN() {
    doTest("1M8GDM9AXKP042788", "1M8", "GDM9AX", "KP042788", "US", "GDM9A", 1989, 'P', "042788");
    doTest("I1M8GDM9AXKP042788", "1M8", "GDM9AX", "KP042788", "US", "GDM9A", 1989, 'P', "042788");
    doTest("LJCPCBLCX11000237", "LJC", "PCBLCX", "11000237", "CN", "PCBLC", 2001, '1', "000237");
    dotest("WBAPCBLCXE1000237", "WBA", "PCBLCX", "E1000237", "DE", "PCBLC", 1984, '1', "000237");
    dotest("9AAPCBLCXP1000237", "9AA", "PCBLCX", "P1000237", "BR", "PCBLC", 1993, '1', "000237");
    dotest("JAAPCBLCXR1000237", "JAA", "PCBLCX", "R1000237", "JP", "PCBLC", 1994, '1', "000237");
    dotest("KLAPCBLCXV1000237", "KLA", "PCBLCX", "V1000237", "KO", "PCBLC", 1997, '1', "000237");
    dotest("3AAPCBLCXA1000237", "3AA", "PCBLCX", "A1000237", "MX", "PCBLC", 2010, '1', "000237");
    dotest("MAAPCBLCXB1000237", "MAA", "PCBLCX", "B1000237", "IN", "PCBLC", 2011, '1', "000237");
    dotest("SAAPCBLCXC1000237", "SAA", "PCBLCX", "C1000237", "UK", "PCBLC", 2012, '1', "000237");
    dotest("SNAPCBLCXD1000237", "SNA", "PCBLCX", "D1000237", "DE", "PCBLC", 2013, '1', "000237");
    dotest("VFAPCBLCX21000237", "VFA", "PCBLCX", "21000237", "FR", "PCBLC", 2002, '1', "000237");
    dotest("VSAPCBLCX31000237", "VSA", "PCBLCX", "31000237", "ES", "PCBLC", 2003, '1', "000237");
    dotest("X0APCBLCX41000237", "X0A", "PCBLCX", "41000237", "RU", "PCBLC", 2004, '1', "000237");
    dotest("ZAAPCBLCX51000237", "ZAA", "PCBLCX", "51000237", "IT", "PCBLC", 2005, '1', "000237");
  }

  private static void doTest(String contents,
                             String wmi,
                             String vds,
                             String vis,
                             String country,
                             String attributes,
                             int year,
                             char plant,
                             String sequential) {
    Result fakeResult = new Result(contents, null, null, BarcodeFormat.CODE_39);
    ParsedResult result = ResultParser.parseResult(fakeResult);
    assertSame(ParsedResultType.VIN, result.getType());
    VINParsedResult vinResult = (VINParsedResult) result;
    assertEquals(wmi, vinResult.getWorldManufacturerID());
    assertEquals(vds, vinResult.getVehicleDescriptorSection());
    assertEquals(vis, vinResult.getVehicleIdentifierSection());
    assertEquals(country, vinResult.getCountryCode());
    assertEquals(attributes, vinResult.getVehicleAttributes());
    assertEquals(year, vinResult.getModelYear());
    assertEquals(plant, vinResult.getPlantCode());
    assertEquals(sequential, vinResult.getSequentialNumber());
  }

  private void dotest(String contents,
                       String wmi,
                       String vds,
                       String vis,
                       String country,
                       String attributes,
                       int year,
                       char plant,
                       String sequential){
    VINResultParser vinResultParser=new VINResultParser();
    Result result=new Result(contents, null, null, BarcodeFormat.CODE_39);
    VINParsedResult vinParsedResult=vinResultParser.parse2(result);
    assertEquals(wmi, vinParsedResult.getWorldManufacturerID());
    assertEquals(vds, vinParsedResult.getVehicleDescriptorSection());
    assertEquals(vis, vinParsedResult.getVehicleIdentifierSection());
    assertEquals(country, vinParsedResult.getCountryCode());
    assertEquals(attributes, vinParsedResult.getVehicleAttributes());
    assertEquals(year, vinParsedResult.getModelYear());
    assertEquals(plant, vinParsedResult.getPlantCode());
    assertEquals(sequential, vinParsedResult.getSequentialNumber());
  }


}
