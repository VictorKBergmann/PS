/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ps;

import java.util.ArrayList;

public class Memory {

 private ArrayList<String> mem;
 private int dataPointer;


 public Memory(){
  mem = new ArrayList<String>();
  
  for(int i=0;i<512;i++)
      mem.add("0");
  
  mem.set(13,"0000100000000010000000000001111"); //add #15 0000.0000.0000.0010./0000.1000.0000.1010
  mem.set(14,"0000000000000111000000000011111"); //store 31
  mem.set(15,"0000000000001011"); // stop
 
 }

 public String getData(int position) {     
  return (mem.get(position));
 }

 public void setData(int position, String data) {
  mem.set(position, data);
 }

 public void setDataPointer(int dataPointer) {
  this.dataPointer = dataPointer;
 }

 public int getDataPointer() {
  return dataPointer;
 }
}
