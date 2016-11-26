package PL104_10127228;

import java.util.ArrayList;

class Identity {
  private String mname;
  private String mtype;
  private ArrayList<String> marray;
  private String mvalue;
  private String marraysize;
  boolean mLocal = false;
  ArrayList<String> mlv;
  
  public Identity( String n, String t ) {
    mname = n;
    mtype = t;
    marraysize = "";
    mvalue = "";
    mlv = new ArrayList<String>();
  } // Identity()
  
  public Identity( String n, String t, String s ) {
    mname = n;
    mtype = t;
    marraysize = s;
    mvalue = "";
    marray = new ArrayList<String>();
    mlv = new ArrayList<String>();    
    for ( int i = 0 ; i < Integer.parseInt( s ) ; i++ )
      marray.add( "" );
  } // Identity()  
  
  public void SetLv( ArrayList<String> s ) {
    for ( int i = 0 ; i < s.size() ; i++ )
      mlv.add( new String( s.get( i ) ) );
  } // SetLv();
  
  boolean CompareLv( ArrayList<String> s ) {
    if ( s.size() < mlv.size() ) return false;
    else if ( s.size() > mlv.size() ) {
      for ( int i = 0 ; i < mlv.size() ; i++ ) 
        if ( !s.get( i ).equals( mlv.get( i ) ) ) return false;    
      return true;
    } // else if
    else {
      for ( int i = 0 ; i < s.size() ; i++ ) 
        if ( !s.get( i ).equals( mlv.get( i ) ) ) return false;    
      return true;
    } // else
  } // CompareLv()
  
  public void Reset( String n, String t ) {
    mname = n;
    mtype = t;
    marraysize = "";
    mvalue = "";
    mlv = new ArrayList<String>();
  } // Reset()
  
  public void Reset( String n, String t, String s ) {
    mname = n;
    mtype = t;
    marraysize = s;
    mvalue = "";
    marray = new ArrayList<String>();
    mlv = new ArrayList<String>();    
    for ( int i = 0 ; i < Integer.parseInt( s ) ; i++ )
      marray.add( "" );
  } // Reset()
   
  public void Print() {
    if ( IsArray() ) System.out.println( mtype + " " + mname + "[ " + marray.size() + " ] ;" );
    else System.out.println( mtype + " " + mname + " ;" );
  } // Print()
  
  public String GetValue() {
    String tmp = mvalue;
    if ( mtype.equals( "string" ) || mtype.equals( "char" ) ) tmp = "\"" + tmp + "\"";
    else if ( mtype.equals( "float" ) ) 
      if ( tmp.indexOf( "." ) + 4 <= tmp.length() )
        tmp = tmp.substring( 0, tmp.indexOf( "." ) + 4 ) ;
      
    return tmp;
  } // GetValue()
  
  public String GetType() {
    return mtype;
  } // GetType()
  
  public String GetName() {
    return mname;
  } // GetName()
  
  public int GetSize() {
    return Integer.parseInt( marraysize );
  } // GetSize()
  
  public boolean IsArray() {
    return !marraysize.isEmpty();
  } // IsArray()
  
  public String SizeToString() {
    return marraysize;
  } // SizeToString()

  public ArrayList<String> GetArray() {
    return marray;
  } // GetArray()

  public void SetArray( ArrayList<String> s ) {
    marray = s ;
  } // SetArray()

  public String GetArrayValue( String s ) {
    String tmp = marray.get( Integer.parseInt( s ) );
    if ( mtype.equals( "string" ) || mtype.equals( "char" ) ) tmp = "\"" + tmp + "\"";
    else if ( mtype.equals( "float" ) ) 
      if ( tmp.indexOf( "." ) + 4 <= tmp.length() )
        tmp = tmp.substring( 0, tmp.indexOf( "." ) + 4 ) ;
    
    return tmp;
  } // GetArrayValue()
   
  
  public void SetArrayValue( String s, String index ) throws ErrorException {
    if ( !s.isEmpty() ) {
      int j ;
      if ( mtype.equals( "int" ) ) {
        Double d = Double.parseDouble( s );
        Integer i = d.intValue();
        s = i.toString();
      } // if

      if ( index.matches( "\\d+" ) ) {
        j = Integer.parseInt( index );
      } // if
      else if ( index.matches( "\\d*[.]\\d+" ) ) {
        j = Integer.parseInt( index.substring( 0, index.indexOf( "." ) ) );
      } // else if
      else throw new ErrorException();
      marray.set( j, s );
    } // if
  } // SetArrayValue()
  
  public void SetValue( String s ) {
    if ( !s.isEmpty() ) {
      if ( mtype.equals( "int" ) ) {
        Double d = Double.parseDouble( s );
        Integer i = d.intValue();
        s = i.toString();
      } // if

      mvalue = s;
    } // if
  } // SetValue()
  
  public void AddMessage() {
    System.out.println( "Definition of " + mname + " entered ..." );
  } // AddMessage()
  
  public void NewMessage() {
    System.out.println( "New definition of " + mname + " entered ..." );    
  } // NewMessage()
  
  public void SetSize( String s ) {
    marraysize += s;
  } // SetSize()
  
} // class Identity
