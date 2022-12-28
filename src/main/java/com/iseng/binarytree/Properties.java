package com.iseng.binarytree;


public class Properties {
    private String Data[];
    private int Left_Pointer[];
    private int Right_Pointer[];
    private int Position = 0;

    /**
     * Use to set data in Data Array. less than 0 will not be tolerate.
     * @param HowManyArray How many index in Data Array
     * @return <p><strong>TRUE</strong> if successfully set, <strong>FALSE</strong> if you set HowManyData less than 0
     */
    public boolean setData(int HowManyArray){
        if(HowManyArray < 0) return false;
        Data = new String[HowManyArray];
        Left_Pointer = new int[HowManyArray]; 
        Right_Pointer = new int[HowManyArray];
        return true;
    }

    Properties(int HowManyArray){
        setData(HowManyArray);
    }

    /**
     * Use to store value to Data Array with string array type. Null or empty will not be tolerate.
     * @param Value is a string type, value will stored to Data Array
     * @return <p><strong>TRUE</strong> if successfully added, <strong>FALSE</strong> if value was null or you didnt set the Data</p> 
     * @see #getData
     * @see #setData(int)
     */
    public boolean setValue(String Value){
        if(Data.length < 1 || Value.isBlank()) return false;
        int pointer = 0;
        Data[Position] = Value;    
        if(Position != 0){
            while(true){
                if((int)Character.toUpperCase(Value.charAt(0)) > (int)Character.toUpperCase(Data[pointer].charAt(0))){
                    if(Right_Pointer[pointer] == 0){
                        Right_Pointer[pointer] = Position;
                        pointer = 0;
                        break;
                    }else{
                        pointer = Right_Pointer[pointer];
                    }
                }else if((int)Character.toUpperCase(Value.charAt(0)) < (int)Character.toUpperCase(Data[pointer].charAt(0))){
                    if(Left_Pointer[pointer] == 0){
                        Left_Pointer[pointer] = Position;
                        pointer = 0;
                        break;
                    }else{
                        pointer = Left_Pointer[pointer];
                    }
                }
            }
        }else if(Position == 0){
            Left_Pointer[Position] = 0;
            Right_Pointer[Position] = 0;
        }
        Position = Position + 1;
        return true;
    }

    /**
     * Use to store value to Data Array with string array type. Null or empty will not be tolerate.
     * @param Value is a string type, that stored to Data Array
     * @return <p>Return string if successf, or return <strong>null</strong> if the data wasn't there</p> 
     * @see #getData
     * @see #setData(int)
     */
    public String findValue(String Value){
        if(Data.length < 1 || Value.isBlank()) return null;
        int pointer = 0;
        if(Data[0] != Value){
            while(true){
                if(Value.equals(Data[pointer])){return Data[pointer]+": "+"{"+"L:"+Left_Pointer[pointer]+" | "+"R:"+Right_Pointer[pointer]+"}";}
                if((int)Character.toUpperCase(Value.charAt(0)) > (int)Character.toUpperCase(Data[pointer].charAt(0))){
                    if(Right_Pointer[pointer] == 0){
                        return null;
                    }else{
                        pointer = Right_Pointer[pointer];
                    }
                }else if((int)Character.toUpperCase(Value.charAt(0)) < (int)Character.toUpperCase(Data[pointer].charAt(0))){
                    if(Left_Pointer[pointer] == 0){
                        return null;
                    }else{
                        pointer = Left_Pointer[pointer];
                    }
                }
            }
        }else{
            return Data[0]+": "+"{"+"L:"+
            Left_Pointer[0]+" | "+"R:"+Right_Pointer[0]+"}";
        }
        
    }

    /**
     * Will get any data in Data Array if any.
     * @return All data in Data Array with String Array type
     * @see #setData(Object, int)
     */
    public String[] getData(){
        String[] TempDATA = new String[Data.length];
        for(int i = 0; i < Data.length; i++){
            if(Data[i] != null){
                TempDATA[i] = Data[i]+": "+"{"+"L:"+
                Left_Pointer[i]+" | "+"R:"+Right_Pointer[i]+"}";
            }
        }
        return TempDATA;
    }
}
