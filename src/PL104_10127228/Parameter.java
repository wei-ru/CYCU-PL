/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PL104_10127228;

/**
 *
 * @author yujames
 */
class Parameter extends Identity {
  
  boolean mISCBR ; // is call by reference
  
  public Parameter( String n, String t ) {
    super( n, t );
  } // Parameter()

  public Parameter( String n, String t, String s ) {
    super( n, t, s );
  } // Parameter()
  
} // class Parameter
