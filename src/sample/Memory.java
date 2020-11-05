package sample;

import java.util.ArrayList;

public class Memory {

 private static ArrayList<Short> men;
 private static int dataPointer;


 public Memory(){
  men = new ArrayList<Short>(512);
 }

 public Short getData(int position) {
  return (men.get(position));
 }

 public void setData(int position, Short data) {
  men.set(position, data);
 }

 public void setDataPointer(int dataPointer) {
  this.dataPointer = dataPointer;
 }

 public int getDataPointer() {
  return dataPointer;
 }

 public void updateDataPointer() {
  //TODO
  //LE TUDO ATÉ ACHAR O STOP
 }

}
