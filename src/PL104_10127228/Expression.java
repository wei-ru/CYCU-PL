/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PL104_10127228;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yujames
 */
class Expression {
  ArrayList<Token> mexp;
  
  public int GetSize() {
    return mexp.size();
  } // GetSize()
  
  public Expression( ArrayList<Token> e ) {
    mexp = Clone( e ) ;
  } // Expression()
  
  ArrayList<Token> Clone( ArrayList<Token> e ) {
    ArrayList<Token> tmp = new ArrayList<Token>();
    for ( int i = 0 ; i < e.size() ; i++ )
      tmp.add( new Token( e.get( i ) ) );
    return tmp;
  } // Clone()
  
} // class Expression
