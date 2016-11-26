package PL104_10127228;

import java.util.ArrayList;
import java.util.Stack;

class Calculator {
  private ArrayList<Identity> mLocal;
  private ArrayList<Identity> midentity;
  private ArrayList<Identity> mvar;
  ArrayList<Statement> mstate;

  ArrayList<Token> SubList( ArrayList<Token> a, int s, int e ) {
    ArrayList<Token> t = new ArrayList<Token>();
    for ( int i = s ; i < e ; i++ ) {
      t.add( a.get( i ) );
    } // for
    
    return t;
  } // SubList()
  
  ArrayList<Token> Clone( ArrayList<Token> e ) {
    ArrayList<Token> tmp = new ArrayList<Token>();
    for ( int i = 0 ; i < e.size() ; i++ )
      tmp.add( new Token( e.get( i ) ) );
    return tmp;
  } // Clone()
  
  ArrayList<Identity> AddAll( ArrayList<Identity> s, ArrayList<Identity> a ) {
    ArrayList<Identity> temp = new ArrayList<Identity>();
    boolean same = false;
    for ( int i = 0 ; i < s.size() ; i++ ) 
      temp.add( s.get( i ) );
    for ( int i = 0 ; i < a.size() ; i++ ) {
      for ( int j = 0 ; j < temp.size() ; j++ )
        if ( a.get( i ).GetName().equals( temp.get( j ).GetName() ) ) same = true;
      if ( !same ) temp.add( a.get( i ) );
      same = false;
    } // for
    
    return temp;
  } // AddAll()
  
  public int GetStmtSize() {
    return mstate.size();
  } // GetStmtSize()
  
  
  public void Clear() {
    mstate.clear();
  } // Clear()
  
  boolean Assign_Op( Token t ) { // 可能會有問題
    String temp = t.GetToken() ;
    return temp.equals( "=" ) || temp.equals( "+=" ) || temp.equals( "-=" ) || 
           temp.equals( "*=" ) || temp.equals( "/=" ) || temp.equals( "%=" ) ;
  } // Assign_Op()

  Token Cout( ArrayList<Token> t ) throws ErrorException {
    int clevel = -1, nextindex = -1, start = 2;
    Token token = new Token( "", "" );
    if ( t.get( 0 ).GetToken().equals( "(" ) ) {
      t.remove( 0 );
      t.remove( t.size() -1 );
    } // if

    clevel = t.get( 1 ).mcout;
    nextindex = FindNext( t, clevel, 2 );    
    if ( t.size() > 4 && t.get( 3 ).GetToken().equals( "cout" ) ) {
      if ( nextindex == -1 ) {
        start = t.size();
        token.SetToken( Cut( Cout( SubList( t, 2, t.size() ) ).GetToken() ) );
      } // if
      else {
        start = nextindex + 1;
        token.SetToken( Cut( Cout( SubList( t, 2, nextindex ) ).GetToken() ) );
        nextindex = FindNext( t, clevel, start );
      } // else
    } // if
    
    for ( int i = start ; i < t.size() ; i++ ) {
      if ( nextindex == -1 ) {
        start = t.size();
        token.SetToken( token.GetToken() + Cut( Calculate( ToPostOrder( SubList( t, i, t.size() ) ) ).
                                                GetToken() ) );
      } // if
      else {
        start = nextindex;
        token.SetToken( token.GetToken() + Cut( Calculate( ToPostOrder( SubList( t, i, nextindex ) ) )
                                                .GetToken() ) );
      } // else
      
      i = start;
      nextindex = FindNext( t, clevel, i + 1 );          
    } // for

    token.SetToken( token.GetToken().replace( "\\n", "\n" ) );
    System.out.print( token.GetToken() );
    return token;
  } // Cout()
  
  int FindNext( ArrayList<Token> t, int curlevel, int curpos ) {
    for ( ; curpos < t.size() ; curpos++ ) 
      if ( t.get( curpos ).mcout == curlevel ) return curpos;
    return -1;
  } // FindNext()
  
  public void Compute() throws ErrorException {
    Stack<Integer>repoint = new Stack<Integer>(), reto = new Stack<Integer>();
    Stack<Integer>elstart = new Stack<Integer>(), elendpoint = new Stack<Integer>();
    Token token = null;
    mvar = AddAll( mvar, mLocal );
    mvar = AddAll( mvar, midentity );    
    ArrayList<Token> sb = null;
    for ( int i = 0 ; i < mstate.size() ; i++ ) {
      for ( int j = 0 ; j < mstate.get( i ).mexplist.size() ; j++ ) {
        if ( !mstate.get( i ).mexplist.get( j ).mexp.get( 0 ).GetToken().equals( "cout" ) )
          sb = ToPostOrder( mstate.get( i ).mexplist.get( j ).mexp ) ;
        else sb = mstate.get( i ).mexplist.get( j ).mexp;
        
        if ( !elstart.empty() && elstart.peek().intValue() == i ) {
          int tmp = elendpoint.pop();
          if ( tmp != -1 )
            i = tmp - 1;
          else i -= 1 ;
          elstart.pop();
        } // if 
        else {
          if ( mstate.get( i ).misJudge ) {
            token = Calculate( sb );
            if ( token.GetToken().equals( "false" ) ) 
              i = mstate.get( i ).mjump - 1;
            else {
              elstart.push( new Integer( mstate.get( i ).mjump ) ) ;
              elendpoint.push( new Integer( mstate.get( i ).melseend ) );
            } // else
          } // if
          else if ( mstate.get( i ).misloop ) {
            token = Calculate( sb );
            if ( token.GetToken().equals( "true" ) ) {
              reto.push( new Integer( mstate.get( i ).mre ) );
              repoint.push( new Integer( mstate.get( i ).mjump - 1 ) );
            } // if
            else {
              i = mstate.get( i ).mjump - 1;
            } // else
          } // else if
          else if ( mstate.get( i ).mexplist.get( j ).mexp.get( 0 ).GetToken().equals( "cout" ) ) {
            token = Cout( sb );
          } // else if
          else Calculate( sb );        
        } // else
      } // for
      
      if ( !repoint.empty() && i == repoint.peek().intValue() ) {
        i = reto.pop().intValue() - 1;
        repoint.pop();
      } // if
    } // for
    
    for ( int i = 0 ; i < mvar.size() ; i++ )
      for ( int j = 0 ; j < midentity.size() ; j++ ) 
        if ( !mvar.get( i ).mLocal ) {
          if ( mvar.get( i ).GetName().equals( midentity.get( j ).GetName() ) ) {
            midentity.get( j ).SetValue( Cut( mvar.get( i ).GetValue() ) );
            if ( mvar.get( i ).IsArray() )
              midentity.get( j ).SetArray( mvar.get( i ).GetArray() );
          } // if
        } // if
    
    for ( int i = 0 ; i < mvar.size() ; i++ ) 
      for ( int j = 0 ; j < mLocal.size() ; j++ )
        if ( mvar.get( i ).GetName().equals( mLocal.get( j ).GetName() ) ) {
          mLocal.get( j ).SetValue( Cut( mvar.get( i ).GetValue() ) );
          if ( mvar.get( i ).IsArray() )
            mLocal.get( j ).SetArray( mvar.get( i ).GetArray() );
        } // if
    
    mvar.clear();
  } // Compute()
  
  String Compare( String s1, String s2, String op ) throws ErrorException {
    if ( ( s1.equals( "true" ) || s1.equals( "false" ) ) && 
         ( s2.equals( "true" ) || s2.equals( "false" ) ) ) {
      boolean b1 = Boolean.parseBoolean( s1 ), b2 = Boolean.parseBoolean( s2 );
      if ( op.equals( "==" ) )
        if ( b1 == b2 ) return "true";
        else return "false";
      else if ( op.equals( "!=" ) )
        if ( b1 != b2 ) return "true";
        else return "false";
      else if ( op.equals( "&&" ) )
        if ( b1 && b2 ) return "true";
        else return "false";
      else if ( op.equals( "||" ) ) {
        if ( b1 || b2 ) return "true";
        else return "false";
      } // else if
    } // if
    else {
      double d1 = Double.parseDouble( s1 ), d2 = Double.parseDouble( s2 );
      if ( op.equals( ">" ) ) {
        if ( d1 > d2 + 0.001 ) return "true";
        else return "false";
      } // if
      else if ( op.equals( ">=" ) ) {
        if ( d1 >= d2 - 0.001 ) return "true";
        else return "false";        
      } // else if
      else if ( op.equals( "<" ) ) {
        if ( d1 < d2 - 0.001 ) return "true";
        else return "false";       
      } // else if
      else if ( op.equals( "<=" ) ) {
        if ( d1 <= d2 + 0.001 ) return "true";
        else return "false";        
      } // else if
      else if ( op.equals( "==" ) ) {
        if ( d1 <= d2 + 0.001 && d1 >= d2 - 0.001 ) return "true";
        else return "false";
      } // else if
      else if ( op.equals( "!=" ) ) {
        if ( d1 > d2 + 0.001 || d1 < d2 - 0.001 ) return "true";
        else return "false";        
      } // else if
    } // else
    
    throw new ErrorException();
  } // Compare()
  
  String Cut( String s ) {
    if ( s.matches( "\".*\"" ) )
      return s.substring( 1, s.indexOf( "\"", 1 ) );
    else if ( s.matches( "\'.\'" ) )
      return s.substring( 1, 2 );
    else return s;
  } // Cut()
  
  String ToString( String s, String type ) {
    if ( !s.isEmpty() ) {
      if ( type.equals( "int" ) ) {
        Double d = Double.parseDouble( s );
        Integer i = d.intValue();
        return i.toString();
      } // if
      else if ( type.equals( "float" ) )
        return s;
      else if ( type.equals( "bool" ) )
        return s;
      else
        return "\"" + s + "\"";
    } // if
    else
      return s ;
  } // ToString()
  
  public Calculator( ArrayList<Identity> l, ArrayList<Identity> i ) {
    mLocal = l;
    midentity = i;
    mvar = new ArrayList<Identity>();
    mstate = new ArrayList<Statement>();
  } // Calculator()
    
  int FindIden( String s ) {
    for ( int i = 0 ; i < mvar.size() ; i++ ) 
      if ( mvar.get( i ).GetName().equals( s ) )
        return i;
    return -1;  
  } // FindIden()
  
  ArrayList<Token> ToPostOrder( ArrayList<Token> e ) throws ErrorException {
    ArrayList<Token> sb = new ArrayList<Token>(), oneOp = new ArrayList<Token>(); 
    Stack<Token> stack = new Stack<Token>(); 
    ArrayList<Token> exp = Clone( e ), ex = Clone( e );
    int array, arrayend = 0;
    Token re = null;
    
    for ( int i = 0 ; i < exp.size() ; ++i ) {
      String ch = exp.get( i ).GetToken();
      if ( ch.matches( "\\d+" ) || ch.matches( "\'.\'" ) || ch.matches( "\".*\"" ) || ch.equals( "true" ) ||
           ch.equals( "false" ) ) {
        if ( ch.matches( "\\d+" ) ) exp.get( i ).mtype = "int";
        else if ( ch.matches( "\'.\'" ) ) exp.get( i ).mtype = "char";
        else if ( ch.matches( "\".*\"" ) ) exp.get( i ).mtype = "string";
        else if ( ch.equals( "true" ) || ch.equals( "false" ) ) exp.get( i ).mtype = "bool";
        sb.add( exp.get( i ) );
      } // if
      else if ( ch.matches( "\\d*[.]\\d+" ) ) {
        if ( ch.indexOf( "." ) + 4 <= ch.length() )
          exp.get( i ).SetToken( ch.substring( 0, ch.indexOf( "." ) + 4 ) );
        exp.get( i ).mtype = "float";
        sb.add( exp.get( i ) );
      } // else if
      else if ( ch.matches( "^[a-zA-Z_]\\w*" ) ) {
        int index = FindIden( ch );
        sb.add( exp.get( i ) );
      } // else if
      else if ( exp.get( i ).GetToken().equals( "[" ) ) {
        array = i ;
        ArrayList<Token> t = null;
        for ( int j = i + 1 ; j < exp.size() ; j++ ) {
          if ( exp.get( j ).GetToken().equals( "]" ) ) {
            t = SubList( exp, array + 1, j ) ;
            re = Calculate( ToPostOrder( t ) );
            re.misArrayIndex = true;
            arrayend = j;
            j = exp.size();
          } // if
        } // for  

        i = arrayend;
        
        sb.add( re );
        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ )
            sb.add( oneOp.get( j ) );
          oneOp.clear();
        } // if        
      } // else if
      else if ( ch.equals( "(" ) ) {
        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ )
            stack.push( oneOp.get( j ) );
          oneOp.clear();
        } // if
          
        stack.push( exp.get( i ) );
      } // else if
      else if ( ch.equals( ")" ) ) {
        Token cur;

        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ ) 
            sb.add( oneOp.get( j ) );
          oneOp.clear();
        } // if
        
        do {
          cur = stack.pop();         
          if ( !cur.GetToken().equals( "(" ) ) sb.add( cur );          
        } while ( !cur.GetToken().equals( "(" ) );
      } // else if
      else if ( ch.equals( "," ) ) {
        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ )
            sb.add( oneOp.get( j ) );
          oneOp.clear();
        } // if
        
        while ( !stack.empty() && 
                ( stack.peek().GetToken().equals( "+" ) || stack.peek().GetToken().equals( "-" ) || 
                  stack.peek().GetToken().equals( "!" ) || stack.peek().GetToken().equals( "*" ) || 
                  stack.peek().GetToken().equals( "/" ) || stack.peek().GetToken().equals( "%" ) || 
                  stack.peek().GetToken().equals( "--" ) || stack.peek().GetToken().equals( "++" ) || 
                  stack.peek().GetToken().equals( ">" ) || stack.peek().GetToken().equals( "<" ) || 
                  stack.peek().GetToken().equals( "<=" ) || stack.peek().GetToken().equals( ">=" ) || 
                  stack.peek().GetToken().equals( "==" ) || stack.peek().GetToken().equals( "!=" ) || 
                  stack.peek().GetToken().equals( "&&" ) || stack.peek().GetToken().equals( "||" ) || 
                  stack.peek().GetToken().equals( "<<" ) || stack.peek().GetToken().equals( ">>" ) ||
                  stack.peek().GetToken().equals( "?" ) || stack.peek().GetToken().equals( ":" ) ||
                  stack.peek().GetToken().equals( "=" ) || stack.peek().GetToken().equals( "+=" ) ||
                  stack.peek().GetToken().equals( "-=" ) || stack.peek().GetToken().equals( "*=" ) || 
                  stack.peek().GetToken().equals( "/=" ) || stack.peek().GetToken().equals( "%=" ) 
                ) )
          sb.add( stack.pop() );

        
        sb.add( exp.get( i ) );
      } // else if
      else if ( ch.equals( "=" ) || ch.equals( "+=" ) || ch.equals( "-=" ) || ch.equals( "*=" ) || 
                ch.equals( "/=" ) || ch.equals( "%=" ) ) {
        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ )
            sb.add( oneOp.get( j ) );
          oneOp.clear();
        } // if
        
        while ( !stack.empty() && 
                ( stack.peek().GetToken().equals( "+" ) || stack.peek().GetToken().equals( "-" ) || 
                  stack.peek().GetToken().equals( "!" ) || stack.peek().GetToken().equals( "*" ) || 
                  stack.peek().GetToken().equals( "/" ) || stack.peek().GetToken().equals( "%" ) || 
                  stack.peek().GetToken().equals( "--" ) || stack.peek().GetToken().equals( "++" ) || 
                  stack.peek().GetToken().equals( ">" ) || stack.peek().GetToken().equals( "<" ) || 
                  stack.peek().GetToken().equals( "<=" ) || stack.peek().GetToken().equals( ">=" ) || 
                  stack.peek().GetToken().equals( "==" ) || stack.peek().GetToken().equals( "!=" ) || 
                  stack.peek().GetToken().equals( "&&" ) || stack.peek().GetToken().equals( "||" ) || 
                  stack.peek().GetToken().equals( "<<" ) || stack.peek().GetToken().equals( ">>" ) 
                ) )
          sb.add( stack.pop() );

        stack.push( exp.get( i ) );        
      } // else if
      else if ( ch.equals( ":" ) || ch.equals( "?" ) ) {
        ArrayList<Token> t = null;
        int in = -1;
        if ( ch.equals( ":" ) ) {
          if ( !oneOp.isEmpty() ) {
            for ( int j = 0 ; j < oneOp.size() ; j++ )
              sb.add( oneOp.get( j ) );
            oneOp.clear();
          } // if 

          while ( !stack.empty() && !stack.peek().GetToken().equals( "?" ) &&
                  ( stack.peek().GetToken().equals( "+" ) || stack.peek().GetToken().equals( "-" ) ||
                    stack.peek().GetToken().equals( "!" ) || stack.peek().GetToken().equals( "*" ) ||
                    stack.peek().GetToken().equals( "/" ) || stack.peek().GetToken().equals( "%" ) || 
                    stack.peek().GetToken().equals( "--" ) || stack.peek().GetToken().equals( "++" ) ||
                    stack.peek().GetToken().equals( ">" ) || stack.peek().GetToken().equals( "<" ) || 
                    stack.peek().GetToken().equals( "<=" ) || stack.peek().GetToken().equals( ">=" ) ||
                    stack.peek().GetToken().equals( "==" ) || stack.peek().GetToken().equals( "!=" ) ||
                    stack.peek().GetToken().equals( "&&" ) || stack.peek().GetToken().equals( "<<" ) || 
                    stack.peek().GetToken().equals( ">>" ) || stack.peek().GetToken().equals( "||" ) ||
                    stack.peek().GetToken().equals( "=" ) || stack.peek().GetToken().equals( "+=" ) ||
                    stack.peek().GetToken().equals( "-=" ) || stack.peek().GetToken().equals( "*=" ) ||
                    stack.peek().GetToken().equals( "/=" ) || stack.peek().GetToken().equals( "%=" ) 
                  ) ) 
            sb.add( stack.pop() );
          stack.pop();
          
          for ( int j = i + 1 ; j < exp.size() ; j++ ) {
            if ( exp.get( j ).mcolend ) {
              in = j;
              j = exp.size();
            } // if
          } // for
          
          t = ToPostOrder( SubList( exp, i + 1, in + 1 ) );
          exp.get( i ).mcend = t.size() ;
    
                    
          sb.add( exp.get( i ) );
        } // if
        else {
          if ( !oneOp.isEmpty() ) {
            for ( int j = 0 ; j < oneOp.size() ; j++ )
              sb.add( oneOp.get( j ) );
            oneOp.clear();
          } // if

          while ( !stack.empty() && !Assign_Op( stack.peek() ) && !stack.peek().GetToken().equals( "(" ) &&
                  ( stack.peek().GetToken().equals( "+" ) || stack.peek().GetToken().equals( "-" ) ||
                    stack.peek().GetToken().equals( "!" ) || stack.peek().GetToken().equals( "*" ) ||
                    stack.peek().GetToken().equals( "/" ) || stack.peek().GetToken().equals( "%" ) || 
                    stack.peek().GetToken().equals( "--" ) || stack.peek().GetToken().equals( "++" ) ||
                    stack.peek().GetToken().equals( ">" ) || stack.peek().GetToken().equals( "<" ) || 
                    stack.peek().GetToken().equals( "<=" ) || stack.peek().GetToken().equals( ">=" ) ||
                    stack.peek().GetToken().equals( "==" ) || stack.peek().GetToken().equals( "!=" ) ||
                    stack.peek().GetToken().equals( "&&" ) || stack.peek().GetToken().equals( "<<" ) || 
                    stack.peek().GetToken().equals( ">>" ) || stack.peek().GetToken().equals( "||" ) ||
                    stack.peek().GetToken().equals( "=" ) || stack.peek().GetToken().equals( "+=" ) ||
                    stack.peek().GetToken().equals( "-=" ) || stack.peek().GetToken().equals( "*=" ) ||
                    stack.peek().GetToken().equals( "/=" ) || stack.peek().GetToken().equals( "%=" ) 
                  ) ) 
            sb.add( stack.pop() );
          
          stack.push( exp.get( i ) );
          sb.add( exp.get( i ) );
        } // else
      } // else if
      else if ( ch.equals( "||" ) ) {
        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ )
            sb.add( oneOp.get( j ) );
          oneOp.clear();
        } // if
        
        while ( !stack.empty() && 
                ( stack.peek().GetToken().equals( "+" ) || stack.peek().GetToken().equals( "-" ) ||
                  stack.peek().GetToken().equals( "!" ) || stack.peek().GetToken().equals( "*" ) ||
                  stack.peek().GetToken().equals( "/" ) || stack.peek().GetToken().equals( "%" ) || 
                  stack.peek().GetToken().equals( "--" ) || stack.peek().GetToken().equals( "++" ) ||
                  stack.peek().GetToken().equals( ">" ) || stack.peek().GetToken().equals( "<" ) || 
                  stack.peek().GetToken().equals( "<=" ) || stack.peek().GetToken().equals( ">=" ) ||
                  stack.peek().GetToken().equals( "==" ) || stack.peek().GetToken().equals( "!=" ) ||
                  stack.peek().GetToken().equals( "&&" ) || stack.peek().GetToken().equals( "<<" ) || 
                  stack.peek().GetToken().equals( ">>" ) || stack.peek().GetToken().equals( "||" ) ) ) 
          sb.add( stack.pop() );       

        stack.push( exp.get( i ) );        
      } // else if
      else if ( ch.equals( "&&" ) ) {
        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ )
            sb.add( oneOp.get( j ) );
          oneOp.clear();
        } // if
        
        while ( !stack.empty() && 
                ( stack.peek().GetToken().equals( "+" ) || stack.peek().GetToken().equals( "-" ) ||
                  stack.peek().GetToken().equals( "!" ) || stack.peek().GetToken().equals( "*" ) || 
                  stack.peek().GetToken().equals( "/" ) || stack.peek().GetToken().equals( "%" ) || 
                  stack.peek().GetToken().equals( "--" ) || stack.peek().GetToken().equals( "++" ) ||
                  stack.peek().GetToken().equals( ">" ) || stack.peek().GetToken().equals( "<" ) ||
                  stack.peek().GetToken().equals( "<=" ) || stack.peek().GetToken().equals( ">=" ) ||
                  stack.peek().GetToken().equals( "==" ) || stack.peek().GetToken().equals( "!=" ) ||
                  stack.peek().GetToken().equals( "<<" ) || stack.peek().GetToken().equals( ">>" ) ||
                  stack.peek().GetToken().equals( "&&" ) ) )
          sb.add( stack.pop() );


        stack.push( exp.get( i ) );        
      } // else if
      else if ( ch.equals( "==" ) || ch.equals( "!=" ) ) {
        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ )
            sb.add( oneOp.get( j ) );
          oneOp.clear();
        } // if
        
        while ( !stack.empty() && 
                ( stack.peek().GetToken().equals( "+" ) || stack.peek().GetToken().equals( "-" ) ||
                  stack.peek().GetToken().equals( "!" ) || stack.peek().GetToken().equals( "*" ) ||
                  stack.peek().GetToken().equals( "/" ) || stack.peek().GetToken().equals( "%" ) || 
                  stack.peek().GetToken().equals( "--" ) || stack.peek().GetToken().equals( "++" ) ||
                  stack.peek().GetToken().equals( ">" ) || stack.peek().GetToken().equals( "<" ) ||
                  stack.peek().GetToken().equals( "<=" ) || stack.peek().GetToken().equals( ">=" ) ||
                  stack.peek().GetToken().equals( "<<" ) || stack.peek().GetToken().equals( ">>" ) || 
                  stack.peek().GetToken().equals( "!=" ) || stack.peek().GetToken().equals( "==" ) ) )
          sb.add( stack.pop() );


        stack.push( exp.get( i ) );        
      } // else if      
      else if ( ch.equals( ">" ) || ch.equals( "<" ) || ch.equals( "<=" ) || ch.equals( ">=" ) ) {
        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ )
            sb.add( oneOp.get( j ) );
          oneOp.clear();
        } // if
        
        while ( !stack.empty() && 
                ( stack.peek().GetToken().equals( "+" ) || stack.peek().GetToken().equals( "-" ) ||
                  stack.peek().GetToken().equals( "!" ) || stack.peek().GetToken().equals( "*" ) || 
                  stack.peek().GetToken().equals( "/" ) || stack.peek().GetToken().equals( "%" ) || 
                  stack.peek().GetToken().equals( "--" ) || stack.peek().GetToken().equals( "++" ) ||
                  stack.peek().GetToken().equals( "<<" ) || stack.peek().GetToken().equals( ">>" ) || 
                  stack.peek().GetToken().equals( ">" ) || stack.peek().GetToken().equals( "<" ) ||
                  stack.peek().GetToken().equals( ">=" ) || stack.peek().GetToken().equals( "<=" ) ) )
          sb.add( stack.pop() );


        stack.push( exp.get( i ) );        
      } // else if
      else if ( ch.equals( "<<" ) || ch.equals( ">>" ) ) {
        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ )
            sb.add( oneOp.get( j ) );
          oneOp.clear();
        } // if
        
        while ( !stack.empty() && 
                ( stack.peek().GetToken().equals( "+" ) || stack.peek().GetToken().equals( "-" ) ||
                  stack.peek().GetToken().equals( "!" ) || stack.peek().GetToken().equals( "*" ) || 
                  stack.peek().GetToken().equals( "/" ) || stack.peek().GetToken().equals( "%" ) || 
                  stack.peek().GetToken().equals( "--" ) || stack.peek().GetToken().equals( "++" ) ||
                  stack.peek().GetToken().equals( "<<" ) || stack.peek().GetToken().equals( ">>" ) ) )
          sb.add( stack.pop() );


        stack.push( exp.get( i ) );
      } // else if
      else if ( ch.equals( "+" ) || ch.equals( "-" ) || ch.equals( "!" ) ) {
        if ( exp.get( i ).misSign || ch.equals( "!" ) )
          oneOp.add( exp.get( i ) );
        else {
          if ( !oneOp.isEmpty() ) {
            for ( int j = 0 ; j < oneOp.size() ; j++ )
              sb.add( oneOp.get( j ) );
            oneOp.clear();
          } // if          
          
          while ( !stack.empty() && 
                  ( stack.peek().GetToken().equals( "*" ) || stack.peek().GetToken().equals( "/" ) ||
                    stack.peek().GetToken().equals( "%" ) || stack.peek().GetToken().equals( "--" ) || 
                    stack.peek().GetToken().equals( "++" ) || stack.peek().GetToken().equals( "<<" ) ||
                    stack.peek().GetToken().equals( "+" ) || stack.peek().GetToken().equals( "-" ) || 
                    stack.peek().GetToken().equals( "!" ) ) )
            sb.add( stack.pop() );
          stack.push( exp.get( i ) );
        } // else
      } // else if 
      else if ( ch.equals( "*" ) || ch.equals( "/" ) || ch.equals( "%" ) ) {
        if ( !oneOp.isEmpty() ) {
          for ( int j = 0 ; j < oneOp.size() ; j++ )
            sb.add( oneOp.get( j ) );
          oneOp.clear();
        } // if

        while ( !stack.empty() &&
                ( stack.peek().GetToken().equals( "*" ) || stack.peek().GetToken().equals( "/" ) ||
                  stack.peek().GetToken().equals( "%" ) || stack.peek().GetToken().equals( "--" ) || 
                  stack.peek().GetToken().equals( "++" ) || stack.peek().GetToken().equals( "*" ) ||
                  stack.peek().GetToken().equals( "/" ) || stack.peek().GetToken().equals( "%" ) ) )
          sb.add( stack.pop() );        
        stack.push( exp.get( i ) );
      } // else if
      else if ( ch.equals( "++" ) || ch.equals( "--" ) ) {
        if ( i + 1 < exp.size() && exp.get( i + 1 ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) {
          int in = FindIden( exp.get( i + 1 ).GetToken() );
          String str = exp.get( i + 1 ).mtype;
          if ( mvar.get( in ).IsArray() ) {
            array = i + 2 ;
            for ( int j = i + 1 ; j < exp.size() ; j++ ) {
              if ( exp.get( j ).GetToken().equals( "]" ) ) {
                re = Calculate( SubList( exp, array + 1, j ) );
                re.misArrayIndex = true;
                arrayend = j;
                j = exp.size();
              } // if
            } // for  

            for ( int j = array ; j < arrayend ; j++ ) 
              exp.remove( array );
            
            if ( ch.equals( "++" ) ) {
              mvar.get( in ).
              SetArrayValue( Double.toString( Double.parseDouble( mvar.get( in ).
                                                                  GetArrayValue( re.GetToken() ) ) + 1 ),
                                                                  re.GetToken() );
            } // if
            else {
              mvar.get( in ).
              SetArrayValue( Double.toString( Double.parseDouble( mvar.get( in ).
                                                                  GetArrayValue( re.GetToken() ) ) - 1 ),
                                                                  re.GetToken() );
            } // else
            
            sb.add( new Token( mvar.get( in ).GetArrayValue( re.GetToken() ),
                               mvar.get( in ).GetType() ) );
          } // if
          else {
            if ( ch.equals( "++" ) ) {
              mvar.get( in ).
              SetValue( Double.toString( Double.parseDouble( mvar.get( in ).GetValue() ) + 1 ) );
            } // if
            else {
              mvar.get( in ).
              SetValue( Double.toString( Double.parseDouble( mvar.get( in ).GetValue() ) - 1 ) );
            } // else 
            
            sb.add( new Token( mvar.get( in ).GetValue(), mvar.get( in ).GetType() ) );
          } // else
          
          i = i + 1;
        } // if
        else
          stack.push( exp.get( i ) );
      } // else if
    } // for
    
    if ( !oneOp.isEmpty() ) {
      for ( int j = 0 ; j < oneOp.size() ; j++ )
        sb.add( oneOp.get( j ) );
      oneOp.clear();
    } // if
    
    while ( !stack.empty() ) 
      sb.add( stack.pop() );
    return sb;
  } // ToPostOrder()
  
  public Token Calculate( ArrayList<Token> exp ) throws ErrorException {
    ArrayList<Token> sb = Clone( exp );
    int arindex1 = -1;
    String s1 = null;
    
    String tmp = "";
    for ( int i = 0 ; i < sb.size() ; i++ )
      tmp += sb.get( i ).GetToken() + " ";
    Stack<Token> resStack = new Stack<Token>();
    for ( int i = 0 ; i < sb.size() ; ++i ) {
      String ch = sb.get( i ).GetToken();
      if ( ch.matches( "-?\\d+" ) || ch.matches( "\'.\'" ) || ch.equals( "true" ) || ch.equals( "false" ) ||
           ch.matches( "\".*\"" ) || ch.matches( "-?\\d*[.]\\d+" ) || ch.equals( "cout" ) ) {
        resStack.push( sb.get( i ) );
      } // if
      else if ( ch.matches( "^[a-zA-Z_]\\w*" ) ) {
        if ( i + 1 < sb.size() && sb.get( i + 1 ).misArrayIndex ) {
          sb.get( i ).marindex = s1 = sb.get( i + 1 ).GetToken();          
          sb.get( i ).mvindex = arindex1 = FindIden( ch ) ;
          sb.get( i ).mtype = mvar.get( arindex1 ).GetType();                        
          sb.get( i ).SetToken( mvar.get( arindex1 ).GetArrayValue( s1 ) );
          sb.remove( i + 1 );
        } // if  
        else { 
          sb.get( i ).mvindex = arindex1 = FindIden( ch );
          sb.get( i ).mtype = mvar.get( arindex1 ).GetType();          
          sb.get( i ).SetToken( mvar.get( arindex1 ).GetValue() );  
        } // else
        
        resStack.push( sb.get( i ) );
      } // else if
      else {
        Token first = null;
        if ( !ch.equals( ":" ) ) {
          first = resStack.pop();
          if ( first.mvindex != -1 ) {
            if ( mvar.get( first.mvindex ).IsArray() )
              first.SetToken( mvar.get( first.mvindex ).GetArrayValue( first.marindex ) );
            else
              first.SetToken( mvar.get( first.mvindex ).GetValue() );
          } // if
        } // if
        
        if ( ch.equals( "++" ) || ch.equals( "--" ) ) {
          if ( !first.marindex.isEmpty() ) {
            resStack.push( new Token( mvar.get( first.mvindex ).GetArrayValue( first.marindex ),
                                      mvar.get( first.mvindex ).GetType() ) ); 

            if ( ch.equals( "++" ) ) {
              mvar.get( first.mvindex ).
              SetArrayValue( Double.toString( Double.parseDouble( first.GetToken() ) + 1 ), 
                                              first.marindex );
            } // if
            else {
              mvar.get( first.mvindex ).
              SetArrayValue( Double.toString( Double.parseDouble( first.GetToken() ) - 1 ), 
                                              first.marindex );
            } // else

          } // if
          else {
            resStack.push( new Token( first.GetToken(), first.mtype ) );            
            if ( ch.equals( "++" ) )
              mvar.get( first.mvindex ).
              SetValue( Double.toString( Double.parseDouble( first.GetToken() ) + 1 ) );
            else 
              mvar.get( first.mvindex ).
              SetValue( Double.toString( Double.parseDouble( first.GetToken() ) - 1 ) );
          } // else          
        } // if
        else if ( ch.equals( "!" ) ) {
          if ( first.GetToken().equals( "true" ) ) first.SetToken( "false" );
          else if ( first.GetToken().equals( "false" ) ) first.SetToken( "true" );
        
          resStack.push( new Token( first.GetToken(), "bool" ) );
        } // else if
        else if ( ch.equals( "," ) ) {
          while ( !resStack.empty() )
            resStack.pop();
        } // else if
        else if ( ch.equals( "?" ) ) {
          int jmp = -1;
          for ( int j = i ; j < sb.size() ; j++ ) 
            if ( sb.get( j ).mclevel == sb.get( i ).mclevel )
              jmp = j;
          
          if ( !first.GetToken().equals( "true" ) ) i = jmp;
        } // else if
        else if ( ch.equals( ":" ) ) i = i + sb.get( i ).mcend;
        else {
          Token second = null;

          
          if ( !sb.get( i ).misSign ) second = resStack.pop();
          if ( second != null ) {
            if ( second.mvindex != -1 ) {
              if ( mvar.get( second.mvindex ).IsArray() )
                second.SetToken( mvar.get( second.mvindex ).GetArrayValue( second.marindex ) );
              else
                second.SetToken( mvar.get( second.mvindex ).GetValue() );
            } // if
          } // if
          
          if ( first.mtype.equals( "string" ) ) second.mtype = "string";
                    
          if ( ch.equals( "+" ) ) {
            if ( sb.get( i ).misSign ) {
              Token t = new Token( first.GetToken(), first.mtype );
              resStack.push( t );
            } // if
            else { 
              if ( second.mtype.equals( "string" ) ) { 
                String str = Cut( second.GetToken() ) + Cut( first.GetToken() );
                second.SetToken( ToString( str, second.mtype ) );
              } // if
              else {
                second.
                SetToken( ToString( Double.toString( Double.parseDouble( second.GetToken() ) + 
                                                     Double.parseDouble( first.GetToken() )
                                                   ), second.mtype ) );
              } // else
              
              resStack.push( new Token( second.GetToken(), second.mtype ) );
            } // else
          } // if
          else if ( ch.equals( "-" ) ) {
            if ( sb.get( i ).misSign ) {
              first.SetToken( ToString( Double.toString( Double.parseDouble( first.GetToken() ) * -1 ),
                                        first.mtype ) );
              Token t = new Token( first.GetToken(), first.mtype );
              resStack.push( t );
            } // if
            else {
              second.SetToken( ToString( Double.toString( Double.parseDouble( second.GetToken() ) - 
                                                          Double.parseDouble( first.GetToken() ) 
                                                        ), second.mtype ) );
              resStack.push( new Token( second.GetToken(), second.mtype ) );
            } // else
          } // else if
          else if ( ch.equals( "*" ) ) {
            second.SetToken( ToString( Double.toString( Double.parseDouble( second.GetToken() ) * 
                                                        Double.parseDouble( first.GetToken() ) 
                                                      ), second.mtype ) );            
            resStack.push( new Token( second.GetToken(), second.mtype ) );
          } // else if
          else if ( ch.equals( "/" ) ) {
            if ( first.GetToken().matches( "0.?0*" ) ) {
              throw new ErrorException();
            } // if
            else {
              second.SetToken( ToString( Double.toString( Double.parseDouble( second.GetToken() ) /
                                                          Double.parseDouble( first.GetToken() ) 
                                                        ), second.mtype ) );         
              resStack.push( new Token( second.GetToken(), second.mtype ) );
            } // else
          } // else if
          else if ( ch.equals( "%" ) ) {
            if ( first.GetToken().matches( "0.?0*" ) ) {
              throw new ErrorException();
            } // if
            else {
              second.SetToken( ToString( Double.toString( Double.parseDouble( second.GetToken() ) % 
                                                          Double.parseDouble( first.GetToken() ) 
                                                        ), second.mtype ) );            
              resStack.push( new Token( second.GetToken(), second.mtype ) );
            } // else
          } // else if
          else if ( ch.equals( "<<" ) ) {
            second.SetToken( ToString( Integer.toString( Integer.parseInt( second.GetToken() ) <<
                                                         Integer.parseInt( first.GetToken() ) 
                                                       ), second.mtype ) );            
            resStack.push( new Token( second.GetToken(), second.mtype ) );            
          } // else if
          else if ( ch.equals( ">>" ) ) {
            second.SetToken( ToString( Integer.toString( Integer.parseInt( second.GetToken() ) >>
                                                         Integer.parseInt( first.GetToken() ) 
                                                       ), second.mtype ) );            
            resStack.push( new Token( second.GetToken(), second.mtype ) );                        
          } // else if
          else if ( ch.equals( ">" ) || ch.equals( "<" ) || ch.equals( ">=" ) || ch.equals( "<=" ) || 
                    ch.equals( "==" ) || ch.equals( "!=" ) || ch.equals( "&&" ) || ch.equals( "||" ) ) {
            second.SetToken( Compare( second.GetToken(), first.GetToken(), ch ) );
            resStack.push( new Token( second.GetToken(), second.mtype ) );
          } // else if
          else if ( ch.equals( "=" ) ) {
            if ( !second.marindex.isEmpty() ) {
              mvar.get( second.mvindex ).SetArrayValue( Cut( first.GetToken() ), second.marindex ) ;
              resStack.push( new Token( mvar.get( second.mvindex ).GetArrayValue( second.marindex ),
                                        second.mtype ) );         
            } // if
            else {
              mvar.get( second.mvindex ).SetValue( Cut( first.GetToken() ) );
              resStack.push( new Token( mvar.get( second.mvindex ).GetValue(), second.mtype ) );
            } // else
          } // else if
          else if ( ch.equals( "+=" ) ) {
            if ( !second.marindex.isEmpty() ) {
              if ( second.mtype.equals( "string" ) ) {
                String str = Cut( second.GetToken() ) + Cut( first.GetToken() );
                mvar.get( second.mvindex ).SetArrayValue( str, second.marindex );
              } // if
              else {
                mvar.get( second.mvindex ).
                SetArrayValue( ToString( Double.toString( Double.parseDouble( second.GetToken() ) 
                                                          + Double.parseDouble( first.GetToken() ) 
                                                        ), second.mtype ), second.marindex );
              } // else
              
              resStack.push( new Token( mvar.get( second.mvindex ).
                                        GetArrayValue( second.marindex ), second.mtype ) );
            } // if
            else {
              if ( second.mtype.equals( "string" ) )
                mvar.get( second.mvindex ).SetValue( Cut( second.GetToken() ) +
                                                     Cut( first.GetToken() ) );
              else {
                mvar.get( second.mvindex ).
                SetValue( Double.toString( Double.parseDouble( second.GetToken() ) + 
                                           Double.parseDouble( first.GetToken() ) ) );
              } // else
              
              resStack.push( new Token( mvar.get( second.mvindex ).GetValue(), second.mtype ) );            
            } // else
          } // else if
          else if ( ch.equals( "-=" ) ) {
            if ( !second.marindex.isEmpty() ) {
              mvar.get( second.mvindex ).
              SetArrayValue( Double.toString( Double.parseDouble( second.GetToken() ) - 
                                              Double.parseDouble( first.GetToken() ) 
                                            ), second.marindex );
              resStack.push( new Token( mvar.get( second.mvindex ).
                                        GetArrayValue( second.marindex ), second.mtype 
                                      ) );              
            } // if
            else {
              mvar.get( second.mvindex ).
              SetValue( Double.toString( Double.parseDouble( second.GetToken() ) - 
                                         Double.parseDouble( first.GetToken() ) ) );
              resStack.push( new Token( mvar.get( second.mvindex ).GetValue(), second.mtype ) );            
            } // else
          } // else if
          else if ( ch.equals( "*=" ) ) {
            if ( !second.marindex.isEmpty() ) {
              mvar.get( second.mvindex ).
              SetArrayValue( Double.toString( Double.parseDouble( second.GetToken() ) * 
                                              Double.parseDouble( first.GetToken() ) 
                                            ), second.marindex );
              resStack.push( new Token( mvar.get( second.mvindex ).GetArrayValue( second.marindex ),
                                        second.mtype ) );                            
            } // if
            else {
              mvar.get( second.mvindex ).
              SetValue( Double.toString( Double.parseDouble( second.GetToken() ) * 
                                         Double.parseDouble( first.GetToken() ) ) );
              resStack.push( new Token( mvar.get( second.mvindex ).GetValue(), second.mtype ) );
            } // else      
          } // else if
          else if ( ch.equals( "/=" ) ) {
            if ( !second.marindex.isEmpty() ) {
              mvar.get( second.mvindex ).
              SetArrayValue( Double.toString( Double.parseDouble( second.GetToken() ) / 
                                              Double.parseDouble( first.GetToken() ) 
                                            ), second.marindex );
              resStack.push( new Token( mvar.get( second.mvindex ).GetArrayValue( second.marindex ),
                                         second.mtype ) );
            } // if
            else {
              mvar.get( second.mvindex ).
              SetValue( Double.toString( Double.parseDouble( second.GetToken() ) / 
                                         Double.parseDouble( first.GetToken() ) ) );
              resStack.push( new Token( mvar.get( second.mvindex ).GetValue(), second.mtype ) );            
            } // else      
          } // else if
          else if ( ch.equals( "%=" ) ) {
            if ( !second.marindex.isEmpty() ) {
              mvar.get( second.mvindex ).
              SetArrayValue( Double.toString( Double.parseDouble( second.GetToken() ) % 
                                              Double.parseDouble( first.GetToken() ) 
                                            ), second.marindex );
              resStack.push( new Token( mvar.get( second.mvindex ).GetArrayValue( second.marindex ),
                                         second.mcout ) );
            } // if
            else {
              mvar.get( second.mvindex ).
              SetValue( Double.toString( Double.parseDouble( second.GetToken() ) % 
                                         Double.parseDouble( first.GetToken() ) ) );
              resStack.push( new Token( mvar.get( second.mvindex ).GetValue(), second.mtype ) );            
            } // else      
          } // else if
        } // else
      } // else
      
      s1 = null;
      arindex1 = -1;
    } // for

    if ( resStack.peek().misArrayIndex == true ) {
      Token temp1 = resStack.pop();
      Token temp2 = resStack.pop();
      temp2.SetToken( mvar.get( FindIden( temp2.GetToken() ) ).GetArrayValue( temp1.GetToken() ) );
      resStack.push( temp2 );
    } // if
    else if ( resStack.peek().GetToken().matches( "[a-zA-Z_]\\w*" ) ) {
      Token temp1 = resStack.pop();
      if ( FindIden( temp1.GetToken() ) != -1 )
        temp1.SetToken( mvar.get( FindIden( temp1.GetToken() ) ).GetValue() );
      resStack.push( temp1 );
    } // else if
    
    
    if ( resStack.peek().GetToken().matches( "\\d*[.]\\d+" ) ) {
      Token t = resStack.pop();
      if ( t.GetToken().indexOf( "." ) + 4 <= t.GetToken().length() )
        t.SetToken( t.GetToken().substring( 0, t.GetToken().indexOf( "." ) + 4 ) );
      resStack.push( t );
    } // if
    
    return resStack.pop();
  } // Calculate()
  
} // class Calculator
