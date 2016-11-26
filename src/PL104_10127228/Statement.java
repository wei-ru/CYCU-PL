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
class Statement {
  ArrayList<Expression> mexplist;
  boolean misJudge = false, misloop = false ;
  int mjump = -1, mre = -1, melseend = -1;
  
  public int GetExpSize( int i ) {
    return mexplist.get( i ).GetSize();
  } // GetExpSize()
  
  public Statement() {
    mexplist = new ArrayList<Expression>();
  } // Statement()
} // class Statement
