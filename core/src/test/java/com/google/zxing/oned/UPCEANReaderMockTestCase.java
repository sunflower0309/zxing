package com.google.zxing.oned;

import com.google.zxing.*;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;

import com.google.zxing.common.BitMatrixTestCase;
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

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UPCEANReaderMockTestCase extends Assert{
  @Test
  public void decodeRowMockTest1() throws Exception{
    BitArray bitArray=mock(BitArray.class);
    when(bitArray.isRange(isA(int.class),isA(int.class),isA(boolean.class))).thenReturn(false);
    UPCEANReader upceanReader=new UPCEANReader() {
      @Override
      BarcodeFormat getBarcodeFormat() {
        return null;
      }

      @Override
      protected int decodeMiddle(BitArray row, int[] startRange, StringBuilder resultString) throws NotFoundException {
        return 0;
      }
    };
    upceanReader.decodeRow(15,bitArray,null);
  }

  @Test
  public void decodeRowMockTest2() throws Exception{
    UPCEANReader upceanReader=mock(UPCEANReader.class);
    when(upceanReader.checkChecksum(isA(String.class))).thenReturn(false);

  }

  @Test
  public void decodeRowMockTest3() throws Exception{
    String str=mock(String.class);
    when(str.length()).thenReturn(7);
  }
}
