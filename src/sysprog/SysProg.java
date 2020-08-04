package sysprog;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Scanner;
//import sysprog.converter;
/****@author nadazayed*/
public class SysProg 
{
    
    public static void main(String[] args) throws Exception 
    {
        File file = new File("inSIC.txt"); 
        int i=0,cnt=0;
        String word=null;
        String [] tag=new String [19];
        String [] ins=new String [19];
        String [] add=new String [19];
        String [] loc=new String [19];
        String [] op =new String  [19];
        
        Scanner sc = new Scanner(file); 
        
        while (sc.hasNextLine())
        {
            word=sc.nextLine();
            String[] temp = word.split("	"); //split by tab
        
        
        if (temp[0].matches(".*[A-Z].*")) //lw bad2 b klma
        {
            if(temp[0].matches("END"))
            {
                tag[i]=";";
                ins[i]=temp[0];
                add[i]=temp[1];
            }
            else
            {
                tag[i]=temp[0];
                ins[i]=temp[1];
                add[i]=temp[2];
                cnt++;
            }
            i++;
        }
        
        else 
        {
                tag[i]=";";
                ins[i]=temp[1];
                add[i]=temp[2];
                i++;
        }
        
        }
        
        // filling locations //
        loc[0]=add[0]; //start adr
        loc[1]=add[0];
        
        int dec,y=0,chars;
        String hex=null;
        ///
        
        ///
        for(int j=1;j<loc.length-1;j++)
        {
            if (ins[j].matches("RESW")) //value*3 -> hex + prev add
            {
                dec=Integer.parseInt(add[j])*3;
                hex=Integer.toHexString(dec + Integer.parseInt(loc[j],16));
                loc[j+1]=hex+"";
            }
            
            else if (ins[j].matches("RESB"))
            {
                dec=Integer.parseInt(add[j])*1;
                hex=Integer.toHexString(dec + Integer.parseInt(loc[j],16));
                loc[j+1]=hex+"";
            }
            
            else if (ins[j].matches("BYTE"))
            {
                chars=add[j].length()-3; //length el string mn8er X''
                //System.out.println(chars+"-"+add[j]);
                if (add[j].startsWith("X"))
                {
                    dec=chars/2;
                    hex=Integer.toHexString(dec + Integer.parseInt(loc[j],16));
                    loc[j+1]=hex+"";
                }
                else //C' '
                {
                    hex=Integer.toHexString(chars + Integer.parseInt(loc[j],16));
                    loc[j+1]=hex+"";
                }
            }
            
            else if (ins[j].matches("WORD")) //word + else //prev+3
            {
                if(add[j].contains(","))
                {
                    String [] mon2sha1=add[j].split(",");
                    //System.out.println("mon2sha1: "+mon2sha1.length);
                    dec=Integer.parseInt(loc[j],16)+(3*mon2sha1.length);
                    loc[j+1]=Integer.toHexString(dec);
                }
                else
                {
                    dec=Integer.parseInt(loc[j],16)+3;
                    loc[j+1]=Integer.toHexString(dec);
                }
                
            }
            else
            {
                dec=Integer.parseInt(loc[j],16)+3;
                loc[j+1]=Integer.toHexString(dec);
            }
        }
        
        String [] symTab1=new String [cnt]; //symbol
        String [] symTab2=new String [cnt]; //location
        
        for (int M=0;M<ins.length;M++) //fill symbol table
        {
            if (!tag[M].matches(";")&&!tag[M].matches("END"))
            {
                symTab1[y]=tag[M];
                symTab2[y]=loc[M];
                y++;
            }
        }
//        ---- Filling opcode
        String first=null, last=null, test=null, test2="0";
        int change=0;
        for (int j=0;j<op.length;j++)
        {
            if (ins[j].matches("RESW")||ins[j].matches("RESB")||ins[j].matches("Start")||ins[j].matches("END"))
            {
                op[j]=";";
            }
            
            else if (ins[j].matches("WORD"))
            {
                DecimalFormat df = new DecimalFormat("000000");
                if (add[j].contains(","))
                {
                    op[j]="";
                    String [] mon2sha1=add[j].split(",");
                    System.out.println("1st:"+mon2sha1[0]+"2nd:"+mon2sha1[1]+"3rd:"+mon2sha1[2]+"4th"+mon2sha1[3]);
                    for (int m=0;m<mon2sha1.length;m++)
                    {
                        change=Integer.parseInt(mon2sha1[m]);
                        test=Integer.toHexString(change);
                        //String e=df.format(Integer.parseInt(test));
                        op[j]=op[j]+test+" ";
                    }
                }
                else
                {
                    change=Integer.parseInt(add[j]);
                    test=Integer.toHexString(change);
                    
                    String c = df.format(Integer.parseInt(test));   // 0009
                    op[j]=c;
                }
                
            }
            
            else if (ins[j].matches("BYTE"))
            {
                if (add[j].contains("X")) //X' '
                {
                    test=add[j].substring(2, add[j].length()-1);
                    op[j]=test;
                }
                else // C' ' > ASCII CODE
                {
                    test="";
                    char [] c=add[j].substring(2,add[j].length()-1).toCharArray();
                    for (int t=0;t<c.length;t++)
                    {
                        test=test+(int)c[t];
                        
                    }
                    op[j]=test;
                }
            }
            
            else
            {
                first=converter.get(ins[j]); //ins op code
                String [] tmp=add[j].split(",");
                test=add[j];
                if (tmp.length==2)
                {
                    test=tmp[0];
                }
                
            for (int k=0;k<symTab1.length;k++)
            {
                if (test.matches(symTab1[k]))
                {
                    last=symTab2[k]; //loc of symbol tab
                }
                
                if (tmp.length==2)
                {
                    
                    change=Integer.valueOf(last)+1400; //1=5
                    last=change+"";
                }
            }
            op[j]=first.concat(last);
            }
            
        }
        
        System.out.println("-Location-"+"\t \t"+"-Tag-"+"\t \t \t"+"-Instruction-"+"\t \t"+"-Address-"+"\t \t"+"-OpCode-");
        for (int M=0;M<ins.length;M++) //print
        {
            
            System.out.println(" "+loc[M]+"\t \t \t"+tag[M]+"\t \t \t"+ins[M]+"\t \t \t"+add[M]+"\t \t \t"+op[M]);
        }
        
//        print symbol table
        System.out.println("\n"+"-Symbol-"+"\t \t"+"-Location-");
        for (int M=0;M<symTab1.length;M++)
        {
            System.out.println(" "+symTab1[M]+"\t \t \t"+symTab2[M]);
        }
        
        int length=Integer.parseInt(loc[18],16)-Integer.parseInt(loc[0],16);
        String lengthSTR=Integer.toHexString(length);
        
        System.out.println("\n"+"-HTE records-");
        
        System.out.println("H ^ "+tag[0]+" ^ "+loc[0]+" ^ "+lengthSTR); //H- rec
        
        length=Integer.parseInt(loc[12],16)-Integer.parseInt(loc[0],16);
        lengthSTR=Integer.toHexString(length);
        
        System.out.print("T ^ "+lengthSTR+" ^ ");
        
        int t=1;
        while (!ins[t].matches("RESW")) //T-rec 1  // first 11 ins
        {
            System.out.print(op[t]+" ");
            t++;
        }
        
        length=Integer.parseInt(loc[loc.length-1],16)-Integer.parseInt(loc[t+2],16);
        lengthSTR=Integer.toHexString(length);
        System.out.print("\n"+"T ^ "+lengthSTR+" ^ ");
        
        for(t=t+2;t<loc.length;t++)
        {
            System.out.print(op[t]+" ");
        }
        
        System.out.println("\n"+"E ^ "+loc[0]); //E-rec
        
        
        
        
        String tes="#30".substring(1, "#30".length());
        
        System.out.println(tes);
    }
    
}
