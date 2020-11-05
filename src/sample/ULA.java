package sample;

public class ULA {
    Integer temp;
    public ULA(){
        temp = 0;
    }

    public Short operation(short opp, Short opr1, Short opr2){
        switch (opp){
            case 2:
                temp = opr1.intValue() + opr2.intValue();
                return temp.shortValue();
            case 6:
                temp = opr1.intValue() - opr2.intValue();
                return temp.shortValue();
            case 10:
                temp = opr1.intValue() / opr2.intValue();
                return temp.shortValue();
            case 14:
                temp = opr1.intValue() * opr2.intValue();
                return temp.shortValue();
            default:
                return -1;
        }
    }
}