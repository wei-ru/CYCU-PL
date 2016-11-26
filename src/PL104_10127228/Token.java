/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PL104_10127228;

import java.util.ArrayList;

/**
 *
 * @author yujames
 */
class Token {
  private String minput;
  private int mline;
  String mtype, marindex = "";
  int mvindex = -1, mcout, mclevel;
  int mcend = -1;
  boolean misSign = false, misArrayIndex = false, mcolend = false;

  public Token( Token t ) {
    mcend = t.mcend;
    mcolend = t.mcolend;
    minput = t.GetToken();
    mline = t.GetLine();
    mtype = t.mtype;
    marindex = t.marindex;
    mvindex = t.mvindex;
    mcout = t.mcout;
    mclevel = t.mclevel;
    misSign = t.misSign;
    misArrayIndex = t.misArrayIndex;
  } // Token()

  public Token( String i, String t ) {
    minput = i;
    mtype = t;
    mline = -1;
  } // Token()

  public Token( String i, int l ) {
    minput = i;
    mline = l;
  } // Token()

  public String GetToken() {
    return minput;
  } // GetToken()

  public int GetLine() {
    return mline;
  } // GetLine()

  public void SetToken( String t ) {
    minput = t;
  } // SetToken()

  public void SetLine( int l ) {
    mline = l;
  } // SetLine()  
  
} // class Token
