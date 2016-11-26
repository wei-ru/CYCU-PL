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
class SyntaxException extends Exception {
  
  public SyntaxException( Token t ) {
    System.out.println( "Line " + t.GetLine() + " : unexpected token : '" + t.GetToken() + "'" );
  } // SyntaxException() 
    
} // class SyntaxException
