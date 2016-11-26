package PL104_10127228;

import java.util.ArrayList;


class Parser {
  ArrayList<Token> minput;
  private ArrayList<Identity> midentity;
  private ArrayList<Integer> msemiColonIndex;
  private ArrayList<Identity> mLocal;
  private ArrayList<String> mlv ;
  private Main mMain;
  private Calculator mCal;
  private int mindex = 0, mstart = -1, mlevel = 0;
  private int mcout = 0, mclevel = 0;
  private boolean mstmt = true, mtest = false, mif = false;
  private boolean mwhile = false;

  public Parser( Main m ) {
    minput = new ArrayList<Token>();
    midentity = new ArrayList<Identity>();
    msemiColonIndex = new ArrayList<Integer>();
    mLocal = new ArrayList<Identity>();
    mlv = new ArrayList<String>();
    mMain = m;
    mCal = new Calculator( mLocal, midentity );
  } // Parser() 
  
  boolean Isbigger() {
    return mindex >= minput.size();
  } // Isbigger()
  
  boolean IsEmpty() {
    return minput.isEmpty();
  } // IsEmpty()
  
  boolean HasSameId( String n ) {
    for ( int i = 0 ; i < midentity.size() ; i++ )
      if ( midentity.get( i ).GetName().equals( n ) ) return true;
    return false;
  } // HasSameId()
  
  int SameIdIndex( String n ) {
    for ( int i = 0 ; i < midentity.size() ; i++ )
      if ( midentity.get( i ).GetName().equals( n ) ) return i;
    return -1;
  } // SameIdIndex()
  
  String Cut( String s ) {
    if ( s.matches( "\".*\"" ) )
      return s.substring( 1, s.indexOf( "\"", 1 ) );
    else if ( s.matches( "\'.\'" ) )
      return s.substring( 1, 2 );
    else return s;
  } // Cut()  
  
  void Reset() {
    mindex = mlevel = mcout = 0;
    mclevel = 0;
    mstart = -1;
    mtest = false;
  } // Reset()
  
  boolean Test() throws UnRecException, UndefineException {
    if ( minput.get( 0 ).GetToken().equals( "Done" ) ) return true;
    if ( Done() ) return true;
    
    try {
      mtest = true;
      UserInput();
    } catch ( SyntaxException e ) {
      return false;
    } // catch
    
    return true;    
  } // Test()
  
  boolean CheckIden( String s ) {
    for ( int i = 0 ; i < midentity.size() ; i++ )
      if ( midentity.get( i ).GetName().equals( s ) )
        return true;
    
    for ( int i = 0 ; i < mLocal.size() ; i++ ) {
      if ( mLocal.get( i ).CompareLv( mlv ) ) {
        if ( mLocal.get( i ).GetName().equals( s ) )
          return true;
      } // if
    } // for
    
    if ( s.equals( "cin" ) || s.equals( "cout" ) || s.equals( "true" ) || s.equals( "false" ) ||
         s.equals( "ListAllVariables" ) || s.equals( "ListVariable" ) ) return true;
    
    return false;
  } // CheckIden()
  
  void DeleteSameId() {
    for ( int i = 0 ; i < mLocal.size() ; i++ ) 
      for ( int j = i + 1 ; j < mLocal.size() ; j++ ) 
        if ( mLocal.get( i ).GetName().equals( mLocal.get( j ).GetName() ) ) mLocal.remove( j );
  } // DeleteSameId()
  
  ArrayList<Token> SubL( ArrayList<Token> a, int s, int e ) {
    ArrayList<Token> t = new ArrayList<Token>();
    for ( int i = s ; i < e ; i++ ) {
      t.add( a.get( i ) );
    } // for
    
    return t;
  } // SubL()

  ArrayList<Integer> SubL( int s, int e, ArrayList<Integer> a ) {
    ArrayList<Integer> t = new ArrayList<Integer>();
    for ( int i = s ; i < e ; i++ ) 
      t.add( a.get( i ) );    
    return t;
  } // SubL()
  
  void Calculate() throws UndefineException, ErrorException {
    DeleteSameId();
    mCal.Compute();
  } // Calculate()
  
  ArrayList<Identity> GetLocal() {
    return mLocal;
  } // GetLocal()
  
  ArrayList<Identity> GetIden() {
    return midentity;
  } // GetIden()
  
  void Add( Token a ) {
    minput.add( a );
    if ( a.GetToken().equals( ";" ) ) 
      msemiColonIndex.add( new Integer( minput.size() -1 ) );
  } // Add()
  
  boolean Done() {
    if ( minput.size() < 4 ) return false ;
    String temp = minput.get( mindex ).GetToken() + minput.get( mindex + 1 ).GetToken() + 
                    minput.get( mindex + 2 ).GetToken() + minput.get( mindex + 3 ).GetToken();
    return temp.equals( "Done();" );
  } // Done()
  
  void ListAllVariables() {
    ArrayList<String> tmp = new ArrayList<String>(), tmp2 = new ArrayList<String>();
    int com = 0;
    if ( minput.size() < 4 ) return ;
    String temp = minput.get( mindex ).GetToken() + minput.get( mindex + 1 ).GetToken() + 
                    minput.get( mindex + 2 ).GetToken() + minput.get( mindex + 3 ).GetToken();
    if ( temp.equals( "ListAllVariables();" ) ) {
      for ( int i = 0 ; i < midentity.size() ; i++ ) 
        tmp.add( new String( midentity.get( i ).GetName() ) );
      if ( tmp.size() > 1 )
        while ( !tmp.isEmpty() ) {
          if ( tmp.size() == 1 ) {
            tmp2.add( tmp.get( 0 ) );
            tmp.clear();
          } // if
          else {
            for ( int i = 0 ; i < tmp.size() ; i++ )
              if ( tmp.get( com ).compareTo( tmp.get( i ) ) > 0 ) com = i;
            tmp2.add( tmp.get( com ) );
            tmp.remove( com );
          } // else
          
          com = 0;
        } // while

      for ( int i = 0 ; i < tmp2.size() ; i++ )
        System.out.println( tmp2.get( i ) );
    } // if
    
    mindex = minput.size();
  } // ListAllVariables()
  
  void ListVariable() {
    if ( minput.size() < 5 ) return ;
    midentity.get( SameIdIndex( Cut( minput.get( 2 ).GetToken() ) ) ).Print();
    mindex = minput.size();
  } // ListVariable()
  
  boolean HassemiColon() {
    return !msemiColonIndex.isEmpty();
  } // HassemiColon()
  
  void ClearIfElse() {
    boolean hasBrace = false ;
    int lastRightBrace = 0, lastline = minput.get( mindex ).GetLine();
    for ( int i = 0 ; i < minput.size() ; i++ )
      if ( minput.get( i ).GetToken().equals( "{" ) ) hasBrace = true;
      else if ( minput.get( i ).GetToken().equals( "}" ) ) lastRightBrace = i;
    if ( hasBrace ) {
      minput = SubL( minput, lastRightBrace + 1, minput.size() ) ;
      if ( lastRightBrace < msemiColonIndex.get( msemiColonIndex.size() - 1 ) ) {
        msemiColonIndex = SubL( msemiColonIndex.size() - 1,
                                                    msemiColonIndex.size(),
                                                    msemiColonIndex ) ;
        msemiColonIndex.set( 0, new Integer( minput.size() - 1 ) );
      } // if
      else msemiColonIndex.clear();
    } // if
    else {
      minput = SubL( minput, mindex + 1, minput.size() );
      if ( minput.isEmpty() )
        msemiColonIndex.clear();
      else {
        msemiColonIndex = SubL( msemiColonIndex.size() - 1,
                                                    msemiColonIndex.size(),
                                                    msemiColonIndex ) ;        
      } // else        
    } // else
    
    for ( int i = 0 ; i < minput.size() ; i++ )
      minput.get( i ).SetLine( minput.get( i ).GetLine() - lastline );
    mlv.clear();
    mCal.Clear();    
    mclevel = mcout = mindex = 0;
    mlevel = 0;
    mstart = -1;
    mLocal.clear();
    if ( mstmt )
      System.out.println( "Statement executed ..." );
    mstmt = true;
  } // ClearIfElse()
  
  void ClearWhile() {
    boolean hasBrace = false ;
    int lastRightBrace = 0, lb = 0, rb = 0;
    int lastline = minput.get( mindex ).GetLine();
    for ( int i = 0 ; i < minput.size() ; i++ )
      if ( minput.get( i ).GetToken().equals( "{" ) ) {
        lb++;
        hasBrace = true;
      } // if
      else if ( minput.get( i ).GetToken().equals( "}" ) ) {
        if ( lb - 1 == rb ) lastRightBrace = i;

        rb++;
      } // else if
    
    if ( hasBrace ) {
      if ( lastRightBrace < msemiColonIndex.get( msemiColonIndex.size() - 1 ) )
        msemiColonIndex = SubL( msemiColonIndex.size() - 1,
                                                           msemiColonIndex.size(), 
                                                           msemiColonIndex );
      else msemiColonIndex.clear();
      minput = SubL( minput, lastRightBrace + 1, minput.size() ) ;
    } // if
    else {
      minput = SubL( minput, msemiColonIndex.get( 0 ).intValue() + 1, minput.size() ) ;
      msemiColonIndex.clear();
    } // else

    for ( int i = 0 ; i < minput.size() ; i++ )
      minput.get( i ).SetLine( minput.get( i ).GetLine() - lastline );    
    
    mlv.clear();   
    mCal.Clear();    
    mLocal.clear();
    mclevel = mcout = mindex = 0;
    mlevel = 0;
    mstart = -1;
    if ( mstmt )
      System.out.println( "Statement executed ..." );
    mstmt = true;
  } // ClearWhile()
  
  void ClearDoWhile() {
    boolean hasBrace = false, first = true ;
    int lastRightBrace = 0, semiColon = 0 ;
    
    for ( int i = 0 ; i < minput.size() ; i++ )
      if ( minput.get( i ).GetToken().equals( "{" ) ) hasBrace = true;
      else if ( minput.get( i ).GetToken().equals( "}" ) ) lastRightBrace = i;
    if ( hasBrace ) {
      for ( int i = 0 ; i < minput.size() ; i++ ) 
        if ( first && lastRightBrace < i && minput.get( i ).GetToken().equals( ";" ) ) {
          semiColon = i;
          first = false;
        } // if
         
      minput = SubL( minput, semiColon + 1, minput.size() ) ;
      int indexOf = msemiColonIndex.indexOf( new Integer( semiColon ) );
      msemiColonIndex = SubL( indexOf + 1,
                                                  msemiColonIndex.size(),
                                                  msemiColonIndex ) ;
    } // if
    else {
      minput = SubL( minput, msemiColonIndex.get( 1 ).intValue() + 1, minput.size() ) ;
      msemiColonIndex = SubL( 2, msemiColonIndex.size(), msemiColonIndex ) ;
    } // else
    
    mlv.clear();
    mLocal.clear();
    mCal.Clear();    
    mclevel = mcout = mindex = 0;
    mlevel = 0;
    mstart = -1;
    if ( mstmt )
      System.out.println( "Statement executed ..." );
    mstmt = true;    
  } // ClearDoWhile()
 
  void Clear() {
    int in = 0, lastline = minput.get( mindex ).GetLine();
    mlv.clear();
    minput = SubL( minput, mindex + 1, minput.size() ) ;
    for ( int i = 0 ; i < msemiColonIndex.size() ; i++ ) 
      if ( msemiColonIndex.get( i ).intValue() <= mindex ) in = i;
    
    for ( int i = 0 ; i < minput.size() ; i++ )
      minput.get( i ).SetLine( minput.get( i ).GetLine() - lastline );    
    msemiColonIndex = SubL( in + 1, msemiColonIndex.size(), msemiColonIndex );
    mclevel = mcout = mindex = 0;
    mlevel = 0;
    mstart = -1;
    mCal.Clear();    
    mLocal.clear();
    if ( mstmt )
      System.out.println( "Statement executed ..." );
    mstmt = true;
  } // Clear()
  
  void Interrupt() {
    mCal.Clear();    
    mlv.clear();
    minput.clear();
    msemiColonIndex.clear();
    mLocal.clear();
    mclevel = mcout = mindex = 0;
    mlevel = 0;
    mstart = -1;
  } // Interrupt()  
  
  boolean Parse() throws UnRecException, UndefineException {
    try {
      mtest = false;
      UserInput();
    } catch ( SyntaxException e ) {
      return false;
    } // catch
    
    return true;
  } // Parse()
  
  void UserInput() throws SyntaxException, UnRecException, UndefineException {
    DefinitionOrStmt();
  } // UserInput()
  
  void DefinitionOrStmt() throws SyntaxException, UnRecException, UndefineException {
    String type, name;
    if ( minput.get( mindex ).GetToken().equals( "void" ) ) {
      if ( mindex + 1 < minput.size() && minput.get( mindex + 1 ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) {
        type = "void";
        name = minput.get( mindex + 1 ).GetToken();
        mindex += 2;
        FuncDefWithoutID();
      } // if
    } // if
    else if ( TypeSpec( mindex ) ) {
      if ( mindex + 1 < minput.size() && minput.get( mindex + 1 ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) {
        if ( mindex + 2 < minput.size() && minput.get( mindex + 2 ).GetToken().equals( "(" ) ) {
          type = minput.get( mindex ).GetToken();
          mindex += 2;
          FuncDefWithoutID();
        } // if 
        else {
          mindex += 2;
          ArrayList<Identity> temp = RestOfDec();
          if ( temp != null ) {
            for ( int i = 0 ; i < temp.size() ; i++ ) {
              if ( !mtest ) {
                if ( HasSameId( temp.get( i ).GetName() ) ) {
                  int in = SameIdIndex( temp.get( i ).GetName() );
                  if ( temp.get( i ).IsArray() )
                    midentity.get( in ).Reset( temp.get( i ).GetName(), temp.get( i ).GetType(), 
                                               temp.get( i ).SizeToString() );
                  else 
                    midentity.get( in ).Reset( temp.get( i ).GetName(), temp.get( i ).GetType() );
                  midentity.get( in ).NewMessage();
                } // if
                else {
                  if ( temp.get( i ).IsArray() )
                    midentity.add( new Identity( temp.get( i ).GetName(), temp.get( i ).GetType(), 
                                                 temp.get( i ).SizeToString() ) );
                  else 
                    midentity.add( new Identity( temp.get( i ).GetName(), temp.get( i ).GetType() ) );
                  midentity.get( midentity.size() -1 ).AddMessage();
                } // else
              } // if
            } // for 
          } // if
          
          mstmt = false ;
        } // else
      } // if
    } // else if
    else if ( !mtest && minput.get( 0 ).GetToken().equals( "ListAllVariables" ) ) ListAllVariables();
    else if ( !mtest && minput.get( 0 ).GetToken().equals( "ListVariable" ) ) ListVariable();
    else Stmt( true, false ); 
    
  } // DefinitionOrStmt()
  
  void FuncDefWithoutID() throws SyntaxException, UnRecException, UndefineException {
    int start, end ;
    if ( minput.get( mindex ).GetToken().equals( "(" ) ) {
      if ( !minput.get( ++mindex ).GetToken().equals( ")" ) ) {
        ArrayList<Parameter> temp = FormalParaList();
        if ( minput.get( mindex + 1 ).GetToken().equals( "{" ) ) {
          start = mindex += 2;
          ComState( true, false );
          end = mindex;
        } // if
        else throw new SyntaxException( minput.get( mindex + 1 ) );
      } // if
      else {
        if ( minput.get( mindex + 1 ).GetToken().equals( "{" ) ) {
          start = mindex += 2;
          ComState( true, false );
          end = mindex;
        } // if
        else throw new SyntaxException( minput.get( mindex + 1 ) );
      } // else
    } // if
    else throw new SyntaxException( minput.get( mindex ) );
  } // FuncDefWithoutID()
  
  ArrayList<Identity> RestOfDec() throws SyntaxException {
    boolean first = true;
    String name, type = minput.get( mindex - 2 ).GetToken();
    ArrayList<Identity> temp = new ArrayList<Identity>();
    temp.add( new Identity( minput.get( mindex - 1 ).GetToken(), minput.get( mindex - 2 ).GetToken() ) );
    if ( mindex >= minput.size() ) return null;
    if ( minput.get( mindex ).GetToken().equals( ";" ) ) { 
    } // if 
    else if ( minput.get( mindex ).GetToken().equals( "[" ) || 
              minput.get( mindex ).GetToken().equals( "," ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( first && minput.get( mindex ).GetToken().equals( "[" ) ) { 
          name = minput.get( mindex - 1 ).GetToken();
          mindex = ArrayCheck( mindex );
          if ( mindex == -1 ) return null;
          temp.get( temp.size() - 1 ).SetSize( minput.get( mindex - 1 ).GetToken() );
        } // if
        else if ( minput.get( mindex ).GetToken().equals( "," ) ) {
          if ( mindex + 1 >= minput.size() ) return null;
          if ( minput.get( mindex + 1 ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) {
            name = minput.get( mindex + 1 ).GetToken();
            if ( mindex + 2 >= minput.size() ) return null;
            if ( minput.get( mindex + 2 ).GetToken().equals( "[" ) ) {
              mindex = ArrayCheck( mindex + 2 );
              if ( mindex == -1 ) return null;
              temp.add( new Identity( name, type, minput.get( mindex - 1 ).GetToken() ) );
            } // if
            else { 
              temp.add( new Identity( name, type ) );
              mindex += 1;
            } // else
          } // if
        } // else if
        else if ( minput.get( mindex ).GetToken().equals( ";" ) ) return temp;
        else throw new SyntaxException( minput.get( mindex ) );
        first = false;
      } // for
    } // else if
    else throw new SyntaxException( minput.get( mindex ) ); // 都不是
    
    return temp;
  } // RestOfDec()
  
  int ArrayCheck( int index ) throws SyntaxException {
    if ( minput.size() < index + 3 ) return -1;
    if ( minput.size() > index && minput.get( index ).GetToken().equals( "[" ) ) {
      if ( minput.size() > index + 1 && minput.get( index + 1 ).GetToken().matches( "\\d+" ) ) {
        if ( minput.size() > index + 2 && 
             minput.get( index + 2 ).GetToken().equals( "]" ) ) return index +2;
        else throw new SyntaxException( minput.get( index + 2 ) );
      } // if
      else throw new SyntaxException( minput.get( index + 1 ) );
    } // if
    else throw new SyntaxException( minput.get( index ) );
  } // ArrayCheck()
  
  ArrayList<Parameter> FormalParaList() throws SyntaxException {
    ArrayList<Parameter> temp = new ArrayList<Parameter>();
    String type, name, arraySize;
    boolean first = true;
    if ( TypeSpec( mindex ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( first && TypeSpec( mindex ) ) {
          type = minput.get( mindex ).GetToken();
          if ( minput.get( mindex + 1 ).GetToken().equals( "&" ) ) { // 有 &
            if ( minput.get( mindex + 2 ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) {
              name = minput.get( mindex + 2 ).GetToken();
              if ( minput.get( mindex + 3 ).GetToken().equals( "[" ) ) {
                mindex = ArrayCheck( mindex + 3 );
                if ( mindex == -1 ) return null;
                arraySize = minput.get( mindex - 1 ).GetToken();
                temp.add( new Parameter( name, type, arraySize ) );
              } // if
              else temp.add( new Parameter( name, type ) );
              temp.get( temp.size() - 1 ).mISCBR = true;
            } // if
            else throw new SyntaxException( minput.get( mindex + 2 ) );
          } // if
          else if ( minput.get( mindex + 1 ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) { // 沒有 &
            name = minput.get( mindex + 1 ).GetToken();
            if ( minput.get( mindex + 2 ).GetToken().equals( "[" ) ) {
              mindex = ArrayCheck( mindex + 2 );
              if ( mindex == -1 ) return null;
              arraySize = minput.get( mindex - 1 ).GetToken();
              temp.add( new Parameter( name, type, arraySize ) );
            } // if
            else temp.add( new Parameter( name, type ) );
            temp.get( temp.size() - 1 ).mISCBR = false;
          } // else if
          else throw new SyntaxException( minput.get( mindex + 1 ) );
        } // if
        else if ( !first && minput.get( mindex ).GetToken().equals( "," ) && TypeSpec( mindex + 1 ) ) {
          type = minput.get( mindex + 1 ).GetToken();
          if ( minput.get( mindex + 2 ).GetToken().equals( "&" ) ) { // 有 &
            if ( minput.get( mindex + 3 ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) {
              name = minput.get( mindex + 3 ).GetToken();
              if ( minput.get( mindex + 4 ).GetToken().equals( "[" ) ) {
                mindex = ArrayCheck( mindex + 4 );
                if ( mindex == -1 ) return null;
                arraySize = minput.get( mindex - 1 ).GetToken();
                temp.add( new Parameter( name, type, arraySize ) );
              } // if
              else temp.add( new Parameter( name, type ) );
              temp.get( temp.size() - 1 ).mISCBR = true;
            } // if
            else throw new SyntaxException( minput.get( mindex + 3 ) );
          } // if
          else if ( minput.get( mindex + 2 ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) { // 沒有 &
            name = minput.get( mindex + 2 ).GetToken();
            if ( minput.get( mindex + 3 ).GetToken().equals( "[" ) ) {
              mindex = ArrayCheck( mindex + 3 );
              if ( mindex == -1 ) return null;
              arraySize = minput.get( mindex - 1 ).GetToken();
              temp.add( new Parameter( name, type, arraySize ) );
            } // if
            else temp.add( new Parameter( name, type ) );
            temp.get( temp.size() - 1 ).mISCBR = false;
          } // else if
          else throw new SyntaxException( minput.get( mindex + 2 ) );          
        } // else if
        else if ( minput.get( mindex ).GetToken().equals( ")" ) ) return temp;
        else throw new SyntaxException( minput.get( mindex ) );
      } // for
      
      return temp;
    } // if
    else throw new SyntaxException( minput.get( mindex ) );
  } // FormalParaList()
  
  void Stmt( boolean f, boolean re ) throws SyntaxException, UnRecException, UndefineException {
    boolean ifWithB = true;    
    int in = 0, rp = 0;
    if ( !mtest && !minput.get( mindex ).GetToken().equals( "{" ) )
      mCal.mstate.add( new Statement() );    
    if ( mindex >= minput.size() ) return ;
    if ( minput.get( mindex ).GetToken().equals( ";" ) ) ;
    else if ( minput.get( mindex ).GetToken().equals( "return" ) ) {
      mindex += 1;
      if ( mindex >= minput.size() ) return ;
      if ( minput.get( mindex + 1 ).GetToken().equals( ";" ) ) { }
      else {
        mindex += 1;
        Expre( mcout );
        if ( mindex >= minput.size() ) return ;
        if ( !minput.get( mindex ).GetToken().equals( ";" ) ) 
          throw new SyntaxException( minput.get( mindex ) );
      } // else
    } // else if
    else if ( minput.get( mindex ).GetToken().equals( "{" ) ) {
      mindex += 1;
      ComState( f, re );
    } // else if
    else if ( minput.get( mindex ).GetToken().equals( "if" ) ) {
      mif = true;
      mindex += 1;
      if ( mindex >= minput.size() ) return ;
      if ( !minput.get( mindex ).GetToken().equals( "(" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
      mindex += 1;
      mlv.add( new String( "if" ) );
      Expre( mcout );
      if ( mindex >= minput.size() ) return ;      
      if ( !minput.get( mindex ).GetToken().equals( ")" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
      mindex += 1;
      
      if ( !mtest ) {
        in = mCal.GetStmtSize() - 1;
        mCal.mstate.get( in ).misJudge = true;
      } // if       
      
      if ( !mtest && !re && !minput.get( minput.size() - 1 ).GetToken().equals( "{" ) ) {
        rp = mindex;
        ifWithB = false;       
        mMain.GetNextLine();
        while ( mindex < minput.size() - 1 && !minput.get( mindex ).GetToken().equals( ";" ) ) {
          mindex = rp;
          Stmt( ifWithB, re );
          if ( mindex >= minput.size() && !minput.get( mindex ).GetToken().equals( ";" ) ) 
            mMain.GetNextLine();
        } // while
      } // if
      else {
        ifWithB = true;
        Stmt( ifWithB, re );   
      } // else
      

      
      if ( !mtest ) mCal.mstate.get( in ).mjump = mCal.mstate.size();      
      if ( !mtest && !re ) mMain.GetNextLine();
      if ( !mtest && minput.size() > mindex ) {
        if ( minput.get( mindex + 1 ).GetToken().equals( "else" ) ) {
          if ( !mtest && !re && mindex + 2 < minput.size() && 
               !minput.get( mindex + 2 ).GetToken().equals( "if" ) &&
               !minput.get( minput.size() - 1 ).GetToken().equals( "{" ) ) {
            mMain.GetNextLine();
            ifWithB = false;
          } // if
          else if ( !mtest && !re && minput.size() == mindex + 2 ) {
            mMain.GetNextLine();
            ifWithB = false;          
          } // else if
          else ifWithB = true;
          mindex += 2;
          mlv.remove( mlv.size() - 1 );
          mlv.add( new String( "else" ) );
          Stmt( ifWithB, re );
          if ( !mtest ) mCal.mstate.get( in ).melseend = mCal.mstate.size();          
        } // if
        
        mlv.remove( mlv.size() - 1 );
      } // if
    } // else if
    else if ( minput.get( mindex ).GetToken().equals( "while" ) ) {
      mwhile = true;
      mindex += 1;
      if ( mindex >= minput.size() ) return;
      if ( !minput.get( mindex ).GetToken().equals( "(" ) )
        throw new SyntaxException( minput.get( mindex ) );
      mindex += 1;
      mlv.add( new String( "while" ) );
      Expre( mcout );
      if ( mindex >= minput.size() ) return ;
      if ( !minput.get( mindex ).GetToken().equals( ")" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
      mindex += 1;
      if ( !mtest && !re && !minput.get( minput.size() - 1 ).GetToken().equals( "{" ) )
        mMain.GetNextLine();
      if ( !mtest ) {
        in = mCal.GetStmtSize() - 1;
        mCal.mstate.get( in ).mre = in;
        mCal.mstate.get( in ).misloop = true;
      } // if
      
      if ( mindex >= minput.size() ) return ;
      Stmt( f, re );
      if ( !mtest ) mCal.mstate.get( in ).mjump = mCal.mstate.size();      
      mlv.remove( mlv.size() - 1 );      
    } // else if
    else if ( minput.get( mindex ).GetToken().equals( "do" ) ) {
      mMain.GetNextLine();
      mindex += 1;
      Stmt( f, re );
      mindex += 1;
      if ( !re && minput.size() <= mindex )
        mMain.GetNextLine();
      if ( !minput.get( mindex ).GetToken().equals( "while" ) )
        throw new SyntaxException( minput.get( mindex ) );
      mindex += 1;
      if ( mindex >= minput.size() ) return ;
      if ( !minput.get( mindex ).GetToken().equals( "(" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
      mindex += 1;
      Expre( mcout );
      if ( mindex >= minput.size() ) return ;      
      if ( !minput.get( mindex ).GetToken().equals( ")" ) )
        throw new SyntaxException( minput.get( mindex ) );
      mindex += 1;
      if ( !minput.get( mindex ).GetToken().equals( ";" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
    } // else if
    else  {
      Expre( mcout );
      if ( minput.size() <= mindex ) return ;
      if ( !minput.get( mindex ).GetToken().equals( ";" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
    } // else
  } // Stmt()
 
  ArrayList<Identity> Declaration() throws SyntaxException {
    if ( mindex >= minput.size() ) return null;
    if ( minput.get( mindex ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) {
      mindex += 1;
      ArrayList<Identity> temp = RestOfDec();
      return temp;
    } // if
    else throw new SyntaxException( minput.get( mindex ) );
  } // Declaration()
  
  void ComState( boolean f, boolean re ) throws SyntaxException, UnRecException, UndefineException {
    if ( !mtest && !re && f && ( mindex >= minput.size() || 
                                 !minput.get( mindex ).GetToken().equals( "}" ) ) )
      mMain.GetNextLine(); 
    for ( ; mindex < minput.size() ; mindex++ ) {      
      if ( TypeSpec( mindex ) ) {
        mindex += 1;
        ArrayList<Identity> temp = Declaration();
        if ( temp == null ) ;
        else if ( minput.get( 0 ).GetToken().equals( "{" ) || mif || mwhile ) {
          for ( int i = 0 ; i < temp.size() ; i++ ) {
            if ( temp.get( i ).IsArray() )
              mLocal.add( new Identity( temp.get( i ).GetName(), temp.get( i ).GetType(), 
                                        temp.get( i ).SizeToString() ) );
            else 
              mLocal.add( new Identity( temp.get( i ).GetName(), temp.get( i ).GetType() ) );
            mLocal.get( mLocal.size() - 1 ).SetLv( mlv );
            mLocal.get( mLocal.size() - 1 ).mLocal = true;
          } // for
        } // else if
      } // if
      else if ( minput.get( mindex ).GetToken().equals( "}" ) ) return ;
      else Stmt( f, re );
      
      if ( !mtest && minput.size() - 1 == mindex ) mMain.GetNextLine();
      else if ( !mtest && minput.size() <= mindex ) {
        mindex = 0;
        mMain.GetNextLine();
        Stmt( f, true );        
        mindex -= 1;
      } // if
    } // for
  } // ComState()
  
  void Expre( int cout ) throws SyntaxException, UndefineException {
    int level = ++mlevel;
    if ( mstart == -1 )
      mstart = mindex;
    Basic_exp( cout );
    
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "," ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "," ) ) {
          mindex += 1;
          Basic_exp( cout );
          
          if ( minput.size() <= mindex ) return ;      
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "," ) ) {
            if ( !mtest && level == 1 ) {
              mCal.mstate.get( mCal.mstate.size() - 1 ).
              mexplist.add( new Expression( SubL( minput, mstart, mindex ) ) );
              mlevel = 0;
              mstart = -1;
            } // if
            
            return ;            
          } // if
          else mindex -= 1;
        } // if
      } // for
    } // if 
    
    if ( !mtest && level == 1 ) {
      mCal.mstate.get( mCal.mstate.size() - 1 ).
      mexplist.add( new Expression( SubL( minput, mstart, mindex ) ) );
      mlevel = 0;
      mstart = -1;
    } // if        
  } // Expre()
  
  void Basic_exp( int cout ) throws SyntaxException, UndefineException { 
    if ( mindex >= minput.size() ) return ;
    if ( minput.get( mindex ).GetToken().equals( "cout" ) ) {
      mcout++;
      cout = mcout;    
    } // if 
    
    if ( minput.get( mindex ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) { 
      if ( !CheckIden( minput.get( mindex ).GetToken() ) ) 
        throw new UndefineException( minput.get( mindex ) ) ;
      mindex += 1;
      Rest_of_Identifier_started_basic_exp( cout ); 
    } // if
    else if ( IsPPMM( mindex ) ) {
      mindex += 1;
      if ( minput.get( mindex ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) {
        if ( !CheckIden( minput.get( mindex ).GetToken() ) ) 
          throw new UndefineException( minput.get( mindex ) ) ;
        mindex += 1;
        Rest_of_PPMM_Identifier_started_basic_exp( cout );
      } // if
      else throw new SyntaxException( minput.get( mindex ) );
    } // else if
    else if ( Sign( mindex ) ) {
      for ( ; Sign( mindex ) ; mindex++ ) { 
        if ( minput.size() <= mindex ) return ;
        minput.get( mindex ).misSign = true;
      } // for
      
      Signed_unary_exp( cout );
      Romce_and_romloe( cout );
    } // else if
    else if ( Constant( mindex ) ) {
      mindex += 1;
      Romce_and_romloe( cout );
    } // else if
    else if ( minput.get( mindex ).GetToken().equals( "(" ) ) {
      mindex += 1;
      cout = 0;
      Expre( cout );
      if ( mindex >= minput.size() ) return ;
      if ( !minput.get( mindex ).GetToken().equals( ")" ) ) 
        throw new SyntaxException( minput.get( mindex ) ) ;
      mindex += 1;
      Romce_and_romloe( cout );
    } // else if
    else throw new SyntaxException( minput.get( mindex ) );
  } // Basic_exp()
  
  void Rest_of_Identifier_started_basic_exp( int cout ) throws SyntaxException, UndefineException {
    if ( mindex >= minput.size() ) return ;
    if ( !minput.get( mindex ).GetToken().equals( "(" ) ) {
      if ( minput.get( mindex ).GetToken().equals( "[" ) ) {
        mindex += 1;
        Expre( cout );
        if ( mindex >= minput.size() ) return;
        if ( !minput.get( mindex ).GetToken().equals( "]" ) )
          throw new SyntaxException( minput.get( mindex ) );
        mindex += 1;
      } // if

      if ( Assign_Op( mindex ) ) {
        mindex += 1;
        Basic_exp( cout );
      } // if
      else { 
        if ( IsPPMM( mindex ) ) 
          mindex += 1;
        Romce_and_romloe( cout );
      } // else
    } // if
    else {
      mindex += 1;
      if ( mindex >= minput.size() ) return ;
      if ( minput.get( mindex ).GetToken().equals( ")" ) ) {
        mindex += 1;
        Romce_and_romloe( cout );
      } // if
      else {
        Actual_parameter_list( cout ); 
        if ( !minput.get( mindex ).GetToken().equals( ")" ) )
          throw new SyntaxException( minput.get( mindex ) );
        mindex += 1;
        Romce_and_romloe( cout );
      } // else
    } // else
  } // Rest_of_Identifier_started_basic_exp()
  
  void Actual_parameter_list( int cout ) throws SyntaxException, UndefineException {
    Basic_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "," ) ) {
      mindex += 1;
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "," ) ) {
          mindex += 1;
          Basic_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "," ) )
            return ;
        } // if
      } // for
    } // if
  } // Actual_parameter_list()
  
  void Rest_of_PPMM_Identifier_started_basic_exp( int cout ) throws SyntaxException, UndefineException {
    if ( minput.get( mindex ).GetToken().equals( "[" ) ) {
      mindex += 1;
      cout = 0;
      Expre( cout );
      if ( mindex >= minput.size() ) return ;
      if ( !minput.get( mindex ).GetToken().equals( "]" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
    } // if
    
    Rest_of_maybe_logical_OR_exp( cout );
  } // Rest_of_PPMM_Identifier_started_basic_exp()

  void Romce_and_romloe( int cout ) throws SyntaxException, UndefineException {
    Rest_of_maybe_logical_OR_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "?" ) ) {      
      int mc = ++mclevel;
      minput.get( mindex ).mclevel = mc;
      mindex += 1;
      Basic_exp( cout );
      if ( mindex >= minput.size() ) return ;
      if ( !minput.get( mindex ).GetToken().equals( ":" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
      minput.get( mindex ).mclevel = mc; 
      mindex += 1;
      Basic_exp( cout );
      minput.get( mindex - 1 ).mcolend = true;
    } // if
  } // Romce_and_romloe()
  
  void Rest_of_maybe_logical_OR_exp( int cout ) throws SyntaxException, UndefineException {
    Rest_of_maybe_logical_AND_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "||" ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "||" ) ) {
          mindex += 1;
          Maybe_logical_AND_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "||" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if
  } // Rest_of_maybe_logical_OR_exp()
  
  void Rest_of_maybe_logical_AND_exp( int cout ) throws SyntaxException, UndefineException {
    Rest_of_maybe_bit_OR_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "&&" ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "&&" ) ) {
          mindex += 1;
          Maybe_bit_OR_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "&&" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if
  } // Rest_of_maybe_logical_AND_exp()
  
  void Maybe_logical_AND_exp( int cout ) throws SyntaxException, UndefineException {
    Maybe_bit_OR_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "&&" ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "&&" ) ) {
          mindex += 1;
          Maybe_bit_OR_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "&&" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if    
  } // Maybe_logical_AND_exp()
  
  void Maybe_bit_OR_exp( int cout ) throws SyntaxException, UndefineException {
    Maybe_bit_ex_OR_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "|" ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "|" ) ) {
          mindex += 1;
          Maybe_bit_ex_OR_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "|" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if      
  } // Maybe_bit_OR_exp()
  
  void Rest_of_maybe_bit_OR_exp( int cout ) throws SyntaxException, UndefineException {
    Rest_of_maybe_bit_ex_OR_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "|" ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "|" ) ) {
          mindex += 1;
          Maybe_bit_ex_OR_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "|" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if        
  } // Rest_of_maybe_bit_OR_exp()

  void Rest_of_maybe_bit_AND_exp( int cout ) throws SyntaxException, UndefineException {
    Rest_of_maybe_equality_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "&" ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "&" ) ) {
          mindex += 1;
          Maybe_equality_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "&" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if      
  } // Rest_of_maybe_bit_AND_exp()
  
  void Maybe_bit_AND_exp( int cout ) throws SyntaxException, UndefineException {
    Maybe_equality_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "&" ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "&" ) ) {
          mindex += 1;
          Maybe_equality_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "&" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if       
  } // Maybe_bit_AND_exp()
  
  void Maybe_bit_ex_OR_exp( int cout ) throws SyntaxException, UndefineException {
    Maybe_bit_AND_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "^" ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "^" ) ) {
          mindex += 1;
          Maybe_bit_AND_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "^" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if       
  } // Maybe_bit_ex_OR_exp()
    
  void Rest_of_maybe_bit_ex_OR_exp( int cout ) throws SyntaxException, UndefineException {
    Rest_of_maybe_bit_AND_exp( cout );
    if ( mindex < minput.size() && minput.get( mindex ).GetToken().equals( "^" ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "^" ) ) {
          mindex += 1;
          Maybe_bit_AND_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "^" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if     
  } // Rest_of_maybe_bit_ex_OR_exp()
  
  void Maybe_equality_exp( int cout ) throws SyntaxException, UndefineException {
    Maybe_relational_exp( cout );
    if ( mindex < minput.size() && ( minput.get( mindex ).GetToken().equals( "==" ) ||
                                     minput.get( mindex ).GetToken().equals( "!=" ) ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "==" ) ||
             minput.get( mindex ).GetToken().equals( "!=" ) ) {
          mindex += 1;
          Maybe_relational_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "==" ) &&
               !minput.get( mindex ).GetToken().equals( "!=" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if    
  } // Maybe_equality_exp()
  
  void Rest_of_maybe_equality_exp( int cout ) throws SyntaxException, UndefineException {
    Rest_of_maybe_relational_exp( cout );
    if ( mindex < minput.size() && ( minput.get( mindex ).GetToken().equals( "==" ) ||
                                     minput.get( mindex ).GetToken().equals( "!=" ) ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "==" ) || 
             minput.get( mindex ).GetToken().equals( "!=" ) ) {
          mindex += 1;
          Maybe_relational_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "==" ) &&
               !minput.get( mindex ).GetToken().equals( "!=" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if   
  } // Rest_of_maybe_equality_exp()
  
  void Maybe_relational_exp( int cout ) throws SyntaxException, UndefineException {
    Maybe_shift_exp( cout );
    if ( mindex < minput.size() && ( minput.get( mindex ).GetToken().equals( "<" ) || 
                                     minput.get( mindex ).GetToken().equals( ">" ) ||
                                     minput.get( mindex ).GetToken().equals( "<=" ) || 
                                     minput.get( mindex ).GetToken().equals( ">=" ) ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "<" ) || minput.get( mindex ).GetToken().equals( ">" )
             || minput.get( mindex ).GetToken().equals( "<=" ) || 
             minput.get( mindex ).GetToken().equals( ">=" ) ) {
          mindex += 1;
          Maybe_shift_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "<" ) && 
               !minput.get( mindex ).GetToken().equals( ">" ) &&
               !minput.get( mindex ).GetToken().equals( "<=" ) && 
               !minput.get( mindex ).GetToken().equals( ">=" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if   
  } // Maybe_relational_exp()
  
  void Rest_of_maybe_relational_exp( int cout ) throws SyntaxException, UndefineException {
    Rest_of_maybe_shift_exp( cout );
    if ( mindex < minput.size() && ( minput.get( mindex ).GetToken().equals( "<" ) || 
                                     minput.get( mindex ).GetToken().equals( ">" ) ||
                                     minput.get( mindex ).GetToken().equals( "<=" ) || 
                                     minput.get( mindex ).GetToken().equals( ">=" ) ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "<" ) || minput.get( mindex ).GetToken().equals( ">" )
             || minput.get( mindex ).GetToken().equals( "<=" ) || 
             minput.get( mindex ).GetToken().equals( ">=" ) ) {
          mindex += 1;
          Maybe_shift_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "<" ) && 
               !minput.get( mindex ).GetToken().equals( ">" ) &&
               !minput.get( mindex ).GetToken().equals( "<=" ) && 
               !minput.get( mindex ).GetToken().equals( ">=" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if      
  } // Rest_of_maybe_relational_exp()
  
  void Maybe_shift_exp( int cout ) throws SyntaxException, UndefineException {
    Maybe_additive_exp( cout );
    if ( mindex < minput.size() && ( minput.get( mindex ).GetToken().equals( "<<" ) 
                                     || minput.get( mindex ).GetToken().equals( ">>" ) ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "<<" ) 
             || minput.get( mindex ).GetToken().equals( ">>" ) ) {
          minput.get( mindex ).mcout = cout;
          mindex += 1;
          Maybe_additive_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "==" ) &&
               !minput.get( mindex ).GetToken().equals( "!=" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if         
  } // Maybe_shift_exp()
  
  void Rest_of_maybe_shift_exp( int cout ) throws SyntaxException, UndefineException {
    Rest_of_maybe_additive_exp( cout );
    if ( mindex < minput.size() && ( minput.get( mindex ).GetToken().equals( "<<" ) 
                                     || minput.get( mindex ).GetToken().equals( ">>" ) ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "<<" ) 
             || minput.get( mindex ).GetToken().equals( ">>" ) ) {
          minput.get( mindex ).mcout = cout;
          mindex += 1;
          Maybe_additive_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "<<" ) &&
               !minput.get( mindex ).GetToken().equals( ">>" ) )
            return ;
          else mindex -= 1;
        } // if
      } // for
    } // if        
  } // Rest_of_maybe_shift_exp()
   
  void Maybe_additive_exp( int cout ) throws SyntaxException, UndefineException {
    Maybe_mult_exp( cout );
    if ( mindex < minput.size() && ( minput.get( mindex ).GetToken().equals( "+" ) 
                                     || minput.get( mindex ).GetToken().equals( "-" ) ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "+" ) 
             || minput.get( mindex ).GetToken().equals( "-" ) ) {
          mindex += 1;
          Maybe_mult_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "+" ) &&
               !minput.get( mindex ).GetToken().equals( "-" ) )
            return ;
          else mindex -= 1;
        } // if
      } // for
    } // if            
  } // Maybe_additive_exp()
  
  void Rest_of_maybe_additive_exp( int cout ) throws SyntaxException, UndefineException {
    Rest_of_maybe_mult_exp( cout );
    if ( mindex < minput.size() && ( minput.get( mindex ).GetToken().equals( "+" ) 
                                     || minput.get( mindex ).GetToken().equals( "-" ) ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "+" ) 
             || minput.get( mindex ).GetToken().equals( "-" ) ) {
          mindex += 1;
          Maybe_mult_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "+" ) &&
               !minput.get( mindex ).GetToken().equals( "-" ) )
            return ;
          else mindex -= 1;          
        } // if
      } // for
    } // if     
  } // Rest_of_maybe_additive_exp()
  
  void Maybe_mult_exp( int cout ) throws SyntaxException, UndefineException {
    Unary_exp( cout );
    Rest_of_maybe_mult_exp( cout );
  } // Maybe_mult_exp()

  void Rest_of_maybe_mult_exp( int cout ) throws SyntaxException, UndefineException {
    if ( minput.size() <= mindex ) return;
    if ( minput.get( mindex ).GetToken().equals( "*" ) || minput.get( mindex ).GetToken().equals( "/" ) 
         || minput.get( mindex ).GetToken().equals( "%" ) ) {
      for ( ; mindex < minput.size() ; mindex++ ) {
        if ( minput.get( mindex ).GetToken().equals( "*" ) || minput.get( mindex ).GetToken().equals( "/" ) 
             || minput.get( mindex ).GetToken().equals( "%" ) ) {
          mindex += 1;
          Unary_exp( cout );
          if ( mindex + 1 <= minput.size() && !minput.get( mindex ).GetToken().equals( "*" ) && 
               !minput.get( mindex ).GetToken().equals( "/" ) && 
               !minput.get( mindex ).GetToken().equals( "%" ) ) 
            return ;
          else mindex -= 1;          
        } // if 
      } // for
    } // if
    
  } // Rest_of_maybe_mult_exp()
  
  void Unary_exp( int cout ) throws SyntaxException, UndefineException {
    if ( minput.size() <= mindex ) return ;    
    if ( IsPPMM( mindex ) ) {
      mindex += 1;
      if ( minput.size() <= mindex ) return ;      
      if ( !minput.get( mindex ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
      if ( !CheckIden( minput.get( mindex ).GetToken() ) ) 
        throw new UndefineException( minput.get( mindex ) ) ;      
      mindex += 1;
      if ( minput.size() <= mindex ) return ;      
      if ( minput.get( mindex ).GetToken().equals( "[" ) ) {
        mindex += 1;
        cout = 0;
        Expre( cout );
        if ( mindex >= minput.size() ) return ;
        if ( !minput.get( mindex ).GetToken().equals( "]" ) ) 
          throw new SyntaxException( minput.get( mindex ) );
      } // if      
    } // if    
    else if ( Sign( mindex ) ) {
      for ( ; Sign( mindex ) ; mindex++ ) {
        if ( minput.size() <= mindex ) return ;
        minput.get( mindex ).misSign = true;
      } // for
      
      Signed_unary_exp( cout );
    } // else if
    else if ( Constant( mindex ) ) mindex += 1;
    else if ( minput.get( mindex ).GetToken().matches( "^[a-zA-Z_]\\w*" ) || 
              minput.get( mindex ).GetToken().equals( "(" )  ) {
      Unsigned_unary_exp( cout );
    } // else if
    else throw new SyntaxException( minput.get( mindex ) ) ;
  } // Unary_exp()
  
  void Signed_unary_exp( int cout ) throws SyntaxException, UndefineException {
    if ( minput.size() <= mindex ) return ;    
    if ( minput.get( mindex ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) {
      if ( !CheckIden( minput.get( mindex ).GetToken() ) ) 
        throw new UndefineException( minput.get( mindex ) ) ;      
      mindex += 1;
      if ( minput.size() <= mindex ) return ;      
      if ( minput.get( mindex ).GetToken().equals( "(" ) ) {
        mindex += 1;
        if ( minput.size() <= mindex ) return ;      
        if ( !minput.get( mindex ).GetToken().equals( ")" ) ) {
          mindex += 1;
          Actual_parameter_list( cout );
          if ( !minput.get( mindex ).GetToken().equals( ")" ) ) 
            throw new SyntaxException( minput.get( mindex ) );
          mindex += 1;
        } // if
      } // if
      else if ( minput.get( mindex ).GetToken().equals( "[" ) ) {
        mindex += 1;
        cout = 0;
        Expre( cout );
        if ( mindex >= minput.size() ) return ;
        if ( !minput.get( mindex ).GetToken().equals( "]" ) ) 
          throw new SyntaxException( minput.get( mindex ) );
        mindex += 1;
      } // else if
    } // if
    else if ( Constant( mindex ) ) mindex += 1;     
    else if ( minput.get( mindex ).GetToken().equals( "(" ) ) {
      mindex += 1;
      cout = 0;
      Expre( cout );
      if ( mindex >= minput.size() ) return ;
      if ( !minput.get( mindex ).GetToken().equals( ")" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
      mindex += 1;
    } // else if
    else throw new SyntaxException( minput.get( mindex ) );    
  } // Signed_unary_exp()
  
  void Unsigned_unary_exp( int cout ) throws SyntaxException, UndefineException {
    if ( minput.size() <= mindex ) return ;
    if ( minput.get( mindex ).GetToken().matches( "^[a-zA-Z_]\\w*" ) ) { // id
      if ( !CheckIden( minput.get( mindex ).GetToken() ) ) 
        throw new UndefineException( minput.get( mindex ) ) ;
      mindex += 1;
      if ( minput.size() <= mindex ) return ;      
      if ( minput.get( mindex ).GetToken().equals( "(" ) ) {
        mindex += 1;
        if ( minput.size() <= mindex ) return ;       
        if ( !minput.get( mindex ).GetToken().equals( ")" ) ) {
          Actual_parameter_list( cout );
          if ( !minput.get( mindex ).GetToken().equals( ")" ) ) 
            throw new SyntaxException( minput.get( mindex ) );
          mindex += 1;
        } // if
        else mindex += 1;
      } // if
      else if ( minput.get( mindex ).GetToken().equals( "[" ) ) {
        mindex += 1;
        cout = 0;
        if ( minput.size() <= mindex ) return ;
        Expre( cout );
        if ( mindex >= minput.size() ) return ;
        if ( !minput.get( mindex ).GetToken().equals( "]" ) ) 
          throw new SyntaxException( minput.get( mindex ) );
        mindex += 1 ;
        if ( minput.size() <= mindex ) return ;
        if ( IsPPMM( mindex ) ) mindex += 1;  
      } // else if
      else if ( IsPPMM( mindex ) ) {
        mindex += 1;
      } // else if 
    } // if
    else if ( minput.get( mindex ).GetToken().equals( "(" ) ) { // Exp
      mindex += 1;
      if ( minput.size() <= mindex ) return ;
      cout = 0;
      Expre( cout );
      if ( minput.size() <= mindex ) return ;
      if ( !minput.get( mindex ).GetToken().equals( ")" ) ) 
        throw new SyntaxException( minput.get( mindex ) );
      mindex += 1;
    } // else if
    else throw new SyntaxException( minput.get( mindex ) );
  } // Unsigned_unary_exp()
  
  boolean IsChar() throws SyntaxException {
    return minput.get( mindex ).GetToken().matches( "\'.\'" );
  } // IsChar()
  
  boolean IsString() throws SyntaxException {
    return minput.get( mindex ).GetToken().matches( "\".*\"" ) ;
  } // IsString()
  
  boolean Constant( int index ) throws SyntaxException {
    return minput.get( index ).GetToken().matches( "^\\d+" ) || 
           minput.get( index ).GetToken().matches( "^\\d*[.]\\d+" ) ||
           minput.get( index ).GetToken().equals( "true" ) || 
           minput.get( index ).GetToken().equals( "false" ) || IsChar() || IsString();
  } // Constant()
  
  boolean IsPPMM( int index ) { // 可能會有問題
    String temp = minput.get( index ).GetToken();
    return temp.equals( "++" ) || temp.equals( "--" );
  } // IsPPMM()
  
  boolean Assign_Op( int index ) { // 可能會有問題
    String temp = minput.get( index ).GetToken() ;
    return temp.equals( "=" ) || temp.equals( "+=" ) || temp.equals( "-=" ) ||
           temp.equals( "*=" ) || temp.equals( "/=" ) || temp.equals( "%=" );
  } // Assign_Op()
  
  boolean Sign( int index ) {
    return minput.get( index ).GetToken().equals( "+" ) || 
           minput.get( index ).GetToken().equals( "-" ) ||
           minput.get( index ).GetToken().equals( "!" );
  } // Sign()
  
  boolean TypeSpec( int index ) {
    return minput.get( index ).GetToken().equals( "int" ) || 
           minput.get( index ).GetToken().equals( "char" ) || 
           minput.get( index ).GetToken().equals( "float" ) || 
           minput.get( index ).GetToken().equals( "string" ) ||
           minput.get( index ).GetToken().equals( "bool" );
  } // TypeSpec()
  
} // class Parser
