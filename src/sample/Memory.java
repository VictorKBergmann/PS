package sample;

import java.util.ArrayList;

public class Memory {

 ArrayList<Short> men;
 int stackPointer;
 int dataPointer;

 public Memory(){
  men = new ArrayList<Short>(512);
  stackPointer = 511;

 }
 public Short getData(int position) {
  return (men.get(position));
 }

 public void setData(int position, Short data) {
  men.set(position, data);
 }

 public int getStackPointer() {
  return stackPointer;
 }

 public void setDataPointer(int dataPointer) {
  this.dataPointer = dataPointer;
 }

 public void updateDataPointer() {
  //TODO
  //LE TUDO ATÉ ACHAR O STOP
 }



}
