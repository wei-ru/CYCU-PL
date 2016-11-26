package PL104_10127228;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.List;

class Main {
  static Main sMain = new Main();
  private int mTestNum ;
  static int sline = 1;
  static Parser sparser;
  static Scanner ssc = new Scanner( System.in );
  
  public Main() {
    mTestNum = 0;
  } // Main()

  public void SetParser() {
    sparser = new Parser( sMain );
  } // SetParser()
  
  public static Main GetInstance() {
    return sMain;
  } // GetInstance()
    
  public void GetNextLine() throws UnRecException {
    sline += 1;
    Token temp = new Token( ssc.nextLine().trim(), sline );
    while ( temp.GetToken().startsWith( "//" ) || temp.GetToken().equals( "" ) )
      temp = new Token( ssc.nextLine().trim(), ++sline );
    SplitString( temp );
  } // GetNextLine()
  
  public static void main( String[] args ) throws UndefineException, UnRecException {
    Main m = new Main();
    m.SetParser();
    m.main();
  } // main()

  int SplitSign( String s ) {
    if ( s.matches( "^(\\|\\||&&|==|<=|>=|!=|\\+=|-=|\\*=|/=|%=|\\+\\+|--|>>|<<).*" ) ) {
      return 2;
    } // if
    else if ( s.matches( "^(\\)|\\(|<|>|=|;|\\+|-|\\*|/|&|\\||!|\\{|\\}|%|\\^|,|\\?|:|\\[|\\]).*" ) ) {
      return 1;
    } // else if
    else {
      return -999;
    } // else
  } // SplitSign()
  
  void SplitString( Token s ) throws UnRecException {
    boolean findDot = false;  
    String reg = "^(\\)|\\(|<|>|=|;|\\+|-|\\*|/|&|\\||!|\\{|\\}|%|\\^|,|\\?|:|\\[|\\]).*" ;
    int subindex = 0;
    
    if ( s.GetToken().matches( "^//.*" ) ) {
    } // if
    else if ( s.GetToken().matches( "^\\d*[.]\\d+.*" ) )  { // floating point
      if ( s.GetToken().matches( "^\\d*[.]\\d+" ) )
        subindex = s.GetToken().length();
      else {
        for ( int i = 0 ; i < s.GetToken().length() ; i++ ) {
          if ( !findDot && s.GetToken().charAt( i ) == '.' ) {
            findDot = true;
          } // if
          else if ( findDot && s.GetToken().charAt( i ) == '.' ) {
            subindex = i;
            i = s.GetToken().length(); // break loop          
          } // else if
          else {
            if ( !Character.isDigit( s.GetToken().charAt( i ) ) ) {
              subindex = i;
              i = s.GetToken().length(); // break loop
            } // if        
          } // else
        } // for
      } // else
      
      sparser.Add( new Token( s.GetToken().substring( 0, subindex ), s.GetLine() ) );
      if ( subindex != s.GetToken().length() )
        SplitString( new Token( s.GetToken().substring( subindex, s.GetToken().length() ).trim(),
                                 s.GetLine() ) );
    } // else if 
    else if ( s.GetToken().matches( "^\\d+.*" ) ) { // number
      if ( s.GetToken().matches( "^\\d+$" ) ) 
        subindex = s.GetToken().length();
      else {
        for ( int i = 0 ; i < s.GetToken().length() ; i++ ) {
          if ( !Character.isDigit( s.GetToken().charAt( i ) ) ) {
            subindex = i;
            i = s.GetToken().length(); // break loop
          } // if
        } // for
      } // else
      
      sparser.Add( new Token( s.GetToken().substring( 0, subindex ), s.GetLine() ) );
      if ( subindex != s.GetToken().length() )
        SplitString( new Token( s.GetToken().substring( subindex, s.GetToken().length() ).trim(),
                                 s.GetLine() ) );
    } // else if 
    else if ( s.GetToken().matches( reg ) ) { // operator
      subindex = SplitSign( s.GetToken() );
      
      sparser.Add( new Token( s.GetToken().substring( 0, subindex ), s.GetLine() ) );
      if ( subindex != s.GetToken().length() )
        SplitString( new Token( s.GetToken().substring( subindex, s.GetToken().length() ).trim(),
                                 s.GetLine() ) );

    } // else if
    else if ( s.GetToken().matches( "^[a-zA-Z_]\\w*.*" ) ) { // variable
      if ( s.GetToken().matches( "^[a-zA-Z_]\\w*" ) )
        subindex = s.GetToken().length();
      else {
        for ( int i = 0 ; i < s.GetToken().length() ; i++ ) {
          if ( !Character.isLetter( s.GetToken().charAt( i ) ) && 
               !Character.isDigit( s.GetToken().charAt( i ) ) &&
               s.GetToken().charAt( i ) != '_' ) {
            subindex = i;
            i = s.GetToken().length(); // break loop
          } // if
        } // for
      } // else
      
      sparser.Add( new Token( s.GetToken().substring( 0, subindex ), s.GetLine() ) );
      if ( subindex != s.GetToken().length() )
        SplitString( new Token( s.GetToken().substring( subindex, s.GetToken().length() ).trim(),
                                 s.GetLine() ) );
    } // else if 
    else if ( s.GetToken().startsWith( "\"" ) || s.GetToken().startsWith( "\'" ) ) {
      int next ;
      if ( s.GetToken().startsWith( "\"" ) ) next = s.GetToken().indexOf( "\"", 1 );
      else next = s.GetToken().indexOf( "\'", 1 );
      
      sparser.Add( new Token( s.GetToken().substring( 0, next + 1 ), s.GetLine() ) );
      if ( next + 1 != s.GetToken().length() )
        SplitString( new Token( s.GetToken().substring( next + 1, s.GetToken().length() ).trim(),
                                 s.GetLine() ) );
    } // else if
    else throw new UnRecException( s.GetLine(), s.GetToken().charAt( 0 ) );

  } // SplitString()
  
  void main() throws UndefineException, UnRecException {
    Token temp = null;
    boolean exit = false, remain = false, fail = false;

    mTestNum = Integer.parseInt( ssc.nextLine().trim() );
    System.out.println( "Our-C running ..." );
    System.out.print( "> " );  
    
    do {
      temp = new Token( ssc.nextLine().trim(), sline );
      while ( temp.GetToken().startsWith( "//" ) || temp.GetToken().equals( "" ) ) 
        temp = new Token( ssc.nextLine().trim(), ++sline );
            
      try {       
        if ( temp != null && !temp.GetToken().trim().equals( "" ) ) {
          SplitString( temp );
          if ( !sparser.Test() ) {
            sline = 0;
            sparser.Interrupt();
            fail = true;
            System.out.print( "> " );
          } // if
          
          sparser.Reset();
        } // if 
        
        if ( !fail ) {
          if ( sparser.minput.get( 0 ).GetToken().equals( "{" ) || sparser.HassemiColon() ) {
            while ( !exit && !sparser.minput.isEmpty() &&
                    ( sparser.minput.get( 0 ).GetToken().equals( "{" ) || sparser.HassemiColon() ) ) {
              if ( !sparser.Done() ) {
                if ( sparser.Parse() ) { 
                  try {
                    sparser.Calculate();
                    sparser.Clear();
                  } catch ( ErrorException e ) {
                    sparser.Interrupt();
                  } // catch
                } // if
                else 
                  sparser.Interrupt();
                sline = 0;

                System.out.print( "> " );
              } // if
              else exit = true;
            } // while
          } // if
          else if ( sparser.minput.get( 0 ).GetToken().equals( "while" ) ) {
            if ( sparser.Parse() ) {
              if ( sparser.Isbigger() ) sparser.Reset();
              else {
                try {
                  sparser.Calculate();                
                  sparser.ClearWhile();
                  sline = 0; 
                  System.out.print( "> " );   
                  if ( sparser.IsEmpty() ) sline = 0;
                  else remain = true;
                } catch ( ErrorException e ) {
                  sparser.Interrupt();
                } // catch
              } // else
            } // if
            else {
              sparser.Interrupt();
              sline = 0; 
              System.out.print( "> " );
            } // else
          } // else if
          else if ( sparser.minput.get( 0 ).GetToken().equals( "if" ) ) {
            if ( sparser.Parse() ) {
              if ( sparser.Isbigger() ) sparser.Reset();
              else {
                try {
                  sparser.Calculate();                   
                  sparser.ClearIfElse();
                  sline = 0; 
                  System.out.print( "> " );  
                  if ( sparser.IsEmpty() ) sline = 0;
                  else remain = true;  
                } catch ( ErrorException e ) {
                  sparser.Interrupt();
                } // catch                  
              } // else
            } // if
            else {
              sparser.Interrupt();
              sline = 0; 
              System.out.print( "> " );
            } // else
            


          } // else if
          else if ( temp.GetToken().contains( "do" ) ) {
            if ( sparser.Parse() ) {
              sparser.ClearDoWhile();
            } // if
            else 
              sparser.Interrupt();

            sline = 0; 
            System.out.print( "> " );      
          } // else if

          while ( remain ) {
            remain = false;
            if ( !sparser.Test() ) {
              sline = 0;
              sparser.Interrupt();
              fail = true;
              System.out.print( "> " );
            } // if
           
            sparser.Reset();
            if ( !fail ) {           
              if ( sparser.minput.get( 0 ).GetToken().equals( "{" ) || sparser.HassemiColon() ) {
                while ( !exit && !sparser.minput.isEmpty() &&
                        ( sparser.minput.get( 0 ).GetToken().equals( "{" ) || sparser.HassemiColon() ) ) {
                  if ( !sparser.Done() ) {
                    if ( sparser.Parse() ) { 
                      try {
                        sparser.Calculate();                      
                        sparser.Clear();
                        if ( !sparser.IsEmpty() ) remain = true;
                      } catch ( ErrorException e ) {
                        sparser.Interrupt();
                      } // catch
                    } // if
                    else 
                      sparser.Interrupt();
                    sline = 0;

                    System.out.print( "> " );
                  } // if
                  else exit = true;
                } // while
              } // if
              else if ( sparser.minput.get( 0 ).GetToken().equals( "while" ) ) {
                if ( sparser.Parse() ) {
                  if ( sparser.Isbigger() ) sparser.Reset();
                  else {
                    try {
                      sparser.Calculate();                      
                      sparser.ClearWhile();
                      sline = 0; 
                      System.out.print( "> " );     
                      if ( sparser.IsEmpty() ) sline = 0;
                      else remain = true;    
                    } catch ( ErrorException e ) {
                      sparser.Interrupt();
                    } // catch
                  } // else
                } // if
                else {
                  sparser.Interrupt();
                  sline = 0; 
                  System.out.print( "> " );
                } // else
              } // else if
              else if ( sparser.minput.get( 0 ).GetToken().equals( "if" ) ) {
                if ( sparser.Parse() ) {
                  if ( sparser.Isbigger() ) sparser.Reset();
                  else {
                    try {
                      sparser.Calculate();                     
                      sparser.ClearIfElse();
                      sline = 0; 
                      System.out.print( "> " );  

                      if ( sparser.IsEmpty() ) sline = 0;
                      else remain = true;
                    } catch ( ErrorException e ) {
                      sparser.Interrupt();
                    } // catch
                  } // else
                } // if
                else {
                  sparser.Interrupt();
                  sline = 0; 
                  System.out.print( "> " );
                } // else



              } // else if
              else if ( temp.GetToken().contains( "do" ) ) {
                if ( sparser.Parse() ) {
                  sparser.ClearDoWhile();
                } // if
                else 
                  sparser.Interrupt();
                sline = 0; 
                System.out.print( "> " );      
              } // else if
            } // if
          } // while
        } // if  
      } // try
      catch ( UnRecException e ) {
        sparser.Interrupt(); 
        sline = 0;
        System.out.print( "> " );      
      } // catch 
      catch ( UndefineException e ) {
        sparser.Interrupt();
        sline = 0;
        System.out.print( "> " );      
      } // catch
      
      sline += 1;
      fail = false;
      temp = null;
    } while ( !exit );

    System.out.println( "Our-C exited ..." );
  } // main()
} // class Main
/*
2
int x ;
x=10;
{ int x ;
  x = 10 ;
}
cout << x ;
string str ;
str = "This is a fine day.\n" ;
float y ;
{ float yy ;
  yy=y+yy;yy=y
  ; 
}
y = 20 ;
Done(); int x ; cout << y ;
x = 10 ;
*/