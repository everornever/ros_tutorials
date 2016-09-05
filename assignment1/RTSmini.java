import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
public class RTSmini{	
	public static void main(String args[]) throws Exception{
		for(int u=2;u<args.length;u++){
			HashMap<String,Long> hm1=new HashMap<String,Long>();//using hashmap to store dependent methods and  hash
			HashMap<String,Long> hm2=new HashMap<String,Long>();
			List<MethodNode> methods=getMethods(args[u]+".class");//get the methods of a class 
			//analyse all the methods of a class
			for(int i=0;i<methods.size();i++){
				MethodNode m=methods.get(i);
				long hash=getHash(m);
				if(m.name.compareTo("<init>")==0)
					continue;
				hm1.put(args[u]+"."+m.name,hash);
				hm2.put(args[u]+"."+m.name,hash);
				readMethodInstructions(hm1,m,args[0]);//traverse all instructions of a method
				readMethodInstructions(hm2,m,args[1]);
			}
			//check whether there is any change in classes by comparing hashcodes 
			if(!findChanges(hm1,hm2)){
				FileOutputStream fos=new FileOutputStream("RUN.txt");
				BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
				bw.write(args[u]);
				bw.close();
			}
			String file=args[u]+".txt";
			writeToFile(hm1,file);//output dependencies to files 
		}
	}
	/**
	 *this function takes the path of class as input and 
	 *and analyse this specific class, return all of its 
	 *methods stored in a MethodNode List.
	*/	
	public static List<MethodNode> getMethods(String classPath) throws Exception{
		if(!new File(classPath).exists()){
			return null;
		}
		FileInputStream io=new FileInputStream(classPath);
		ClassReader cr=new ClassReader(io);
		ClassNode cn=new ClassNode();
		cr.accept(cn,ClassReader.SKIP_DEBUG);   
		@SuppressWarnings("unchecked")
		List<MethodNode> ls=cn.methods;
		return ls;
	}
	/**
	 *This function takes a HashMap, MethodNode and the path
	 *of the class as inputs, read every instruction of the 
	 *method and if it is another method then find its owner 
	 *and analyseits owner class recursively.
	 */  
	public static  void readMethodInstructions(HashMap<String,Long> hm, MethodNode m,String filePath) throws Exception{
		int t;
		for(int j=0;j<m.instructions.size();j++){
			AbstractInsnNode ai=m.instructions.get(j);
			t=ai.getType();//get the type of a instruction
			//if a instruction is a call of method, then go to the body of that method and analyse it recursively 
			if(t==AbstractInsnNode.METHOD_INSN){
				MethodInsnNode min= (MethodInsnNode) ai ;
				String owner=min.owner;//get the owner class of a method 
				String name=min.name;//get the name of the method 
				String desc=min.desc;//get the descriptor of a method 
				List<MethodNode> ls=getMethods(filePath+owner+".class");
				if(ls==null)
					continue;
				//traverse the methods of the class and find out the specific method
				for(int i=0;i<ls.size();i++)
					if(ls.get(i).name.compareTo(name)==0 && ls.get(i).desc.compareTo(desc)==0){
						long hash = getHash(ls.get(i));
						owner=owner.replace("/",".");
						hm.put(owner+"."+name,hash);//put the method and its hash into hashmap
						readMethodInstructions(hm,ls.get(i),filePath);//analyse the method recursively 
						break;
					}
			}
		}
		return;
	}

	/**
	 *This function will return the hashcode of a method 
	 *using Adler32 algorithm
	 */
	public static long getHash(MethodNode m){
		int k;
		byte[] opcode=new byte[m.instructions.size()];
		//get the opcode of every instruction and store it in a byte array
		for(int j=0;j<m.instructions.size();j++){
			AbstractInsnNode ai=m.instructions.get(j);
			k=ai.getOpcode();//get Opcode
			opcode[j]=(byte)k;
		}
		//generate hashcode
		Checksum c=new Adler32();
		c.update(opcode,0,opcode.length);
		long hash=c.getValue();
		return hash;
	}

	/*This function is designed to compare two HashMaps so 
	 *as to determine whether there is any change in the new 
	 *programs
	 */	
	public static boolean findChanges(HashMap<String,Long> hm1,HashMap<String,Long> hm2){
		//traverse two hashmaps and check whether there exists any difference in hashcode 
		for(String s1: hm1.keySet())
			if(hm1.get(s1).compareTo(hm2.get(s1))!=0)
				return false;
		return true;
	}
	/**
	 *This function writes the dependencies stored in hashmap
	 * to a file
	 */ 	
	public static void writeToFile(HashMap<String,Long> hm,String filename) throws IOException{
	File f=new File(filename);	
	FileOutputStream fos=new FileOutputStream(f);
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
		for(String s:hm.keySet()){
			bw.write(s+":"+hm.get(s));
			bw.newLine();
		}
		bw.close();
	}
}

