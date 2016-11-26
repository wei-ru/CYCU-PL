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
class Function {
  private final String mname;
  private final String mreType;
  private ArrayList<Identity> midentity;
  private ArrayList<String> minstruction;
  private ArrayList<Parameter> mparameter;
  
  public Function( ArrayList<Identity> id, ArrayList<String> ins, ArrayList<String> p, String t, String n ) {
    midentity = (ArrayList)id.clone();
    mparameter = (ArrayList)p.clone();
    minstruction = (ArrayList)ins.clone();
    mreType = t;
    mname = n;
  } // Function()
  
  public ArrayList<Parameter> GetPara() {
    return mparameter;
  } // GetPara()
  
  public Parameter GetPara(int i) {
    return mparameter.get(i);
  } // GetPara()
  
  public ArrayList<String> GetIns() {
    return minstruction;
  } // GetValue()
  
  public String GetName() {
    return mname;
  } // GetType()
  
  public String GetReType() {
    return mreType;
  } // GetReType()
  
  public ArrayList<Identity> GetId() {
    return midentity;
  } // GetId()
  
} // class Funcion
