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
class UndefineException extends Exception {
  
  public UndefineException( Token t ) {
    String s = t.GetToken();
    if ( s.equals( "if" ) || s.equals( "else" ) || s.equals( "while" ) || s.equals( "cin" ) || 
         s.equals( "cout" ) || s.equals( "do" ) || s.equals( "return" ) || s.equals( "int" ) || 
         s.equals( "float" ) || s.equals( "char" ) || s.equals( "string" ) || s.equals( "void" ) ||
         s.equals( "bool" ) )
      System.out.println( "Line " + t.GetLine() + " : unexpected token : '" + t.GetToken() + "'" );
    else
      System.out.println( "Line " + t.GetLine() + " : undefined identifier : '" + t.GetToken() + "'" );
  } // UndefineException() 

} // class UndefineException
