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
class UnRecException extends Exception {
  
  public UnRecException( int line, char c ) {
    System.out.println( "Line " + line + " : unrecognized token with first char : '" + c + "'" );
  } // UnRecException() 
  
} // class UnRecException
