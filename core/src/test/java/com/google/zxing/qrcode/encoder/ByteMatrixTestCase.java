package com.google.zxing.qrcode.encoder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ByteMatrixTestCase extends Assert{
  private ByteMatrix matrix;
  @Before
  public void setMatrix(){
    matrix=new ByteMatrix(3,4);
    for(int x=0;x<3;x++){
      for(int y=0;y<4;y++){
        matrix.set(x,y,(x+y)%2);
      }
    }
  }
  @Test
  public void MatrixSizeTest(){
    assertEquals(matrix.getHeight(),4);
    assertEquals(matrix.getWidth(),3);
  }

  @Test
  public void MatrixGetPositiveTest(){
    assertEquals(matrix.get(0,0),0);
    assertEquals(matrix.get(0,1),1);
    assertEquals(matrix.get(0,2),0);
    assertEquals(matrix.get(0,3),1);
  }

  @Test
  public void MatrixGetNegativeTest(){
    try {
      matrix.get(-1,0);
    }
    catch (ArrayIndexOutOfBoundsException e){
      e.printStackTrace();
    }
  }

  @Test
  public void MatrixSetPositiveTest(){
    ByteMatrix mat=new ByteMatrix(3,2);
    mat.set(0,0,(byte)0);
    mat.set(0,1,(byte)1);
    mat.set(1,0,0);
    mat.set(1,1,1);
    mat.set(2,0,false);
    mat.set(2,1,true);
    assertEquals(mat.get(0,0),(byte) 0);
    assertEquals(mat.get(0,1),(byte) 1);
    assertEquals(mat.get(1,0),(byte) 0);
    assertEquals(mat.get(1,1),(byte) 1);
    assertEquals(mat.get(2,0),(byte) 0);
    assertEquals(mat.get(2,1),(byte) 1);
  }

  @Test
  public void MatrixSetNegativeTest1(){
    try {
      ByteMatrix mat=new ByteMatrix(1,1);
      mat.set(0,0,(byte)245);
      //System.out.println(mat.get(0,0));
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void MatrixSetNegativeTest2(){
    try {
      ByteMatrix mat=new ByteMatrix(1,1);
      mat.set(0,0,(byte)'a');
      //System.out.println(mat.get(0,0));
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void MatrixSetNegativeTest3(){
    try {
      ByteMatrix mat=new ByteMatrix(1,1);
      mat.set(0,0,(byte)-259);
      //System.out.println(mat.get(0,0));
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void getArrayTest(){
    byte[][] b=new byte[4][3];
    for(int i=0;i<4;i++){
      for(int j=0;j<3;j++){
        b[i][j]=(byte)((i+j)%2);
      }
    }
    assertArrayEquals(matrix.getArray(),b);
  }

  @Test
  public void toStringPositiveTest(){
    assertEquals(matrix.toString()," 0 1 0\n"+" 1 0 1\n"+" 0 1 0\n"+" 1 0 1\n");
  }

  @Test
  public void toStringNegativeTest(){
    ByteMatrix mat=new ByteMatrix(3,1);
    mat.set(0,0,(byte)-259);
    mat.set(1,0,(byte)'a');
    mat.set(2,0,(byte)275);
    assertEquals(mat.toString(),"      \n");
  }
}
