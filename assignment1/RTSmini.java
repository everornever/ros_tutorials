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
			HashMap<String,Long> hm1=new HashMap<String,Long>();
			HashMap<String,Long> hm2=new HashMap<String,Long>();
			List<MethodNode> methods=getMethods(args[u]+".class");
			for(int i=0;i<methods.size();i++){
				MethodNode m=methods.get(i);
				long hash=getHash(m);
				if(m.name.compareTo("<init>")==0)
					continue;
				hm1.put(args[u]+"."+m.name,hash);
				hm2.put(args[u]+"."+m.name,hash);
				readMethodInstructions(hm1,m,args[0]);
				readMethodInstructions(hm2,m,args[1]);
			}
			if(!findChanges(hm1,hm2)){
				FileOutputStream fos=new FileOutputStream("RUN.txt");
				BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
				bw.write(args[u]);
				bw.close();
			}
			String file=args[u]+".txt";
			writeToFile(hm1,file);
		}
	}
	
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

	public static  void readMethodInstructions(HashMap<String,Long> hm, MethodNode m,String filePath) throws Exception{
		int t;
		for(int j=0;j<m.instructions.size();j++){
			AbstractInsnNode ai=m.instructions.get(j);
			t=ai.getType();
			if(t==AbstractInsnNode.METHOD_INSN){
				MethodInsnNode min= (MethodInsnNode) ai ;
				String owner=min.owner;
				String name=min.name;
				List<MethodNode> ls=getMethods(filePath+owner+".class");
				if(ls==null)
					continue;
				for(int i=0;i<ls.size();i++)
					if(ls.get(i).name.compareTo(name)==0){
						long hash = getHash(ls.get(i));
						owner=owner.replace("/",".");
						hm.put(owner+"."+name,hash);
						readMethodInstructions(hm,ls.get(i),filePath);
						break;
					}
			}
		}
		return;
	}

	public static long getHash(MethodNode m){
		int k;
		byte[] opcode=new byte[m.instructions.size()];
		for(int j=0;j<m.instructions.size();j++){
			AbstractInsnNode ai=m.instructions.get(j);
			k=ai.getOpcode();
			opcode[j]=(byte)k;
		}
		Checksum c=new Adler32();
		c.update(opcode,0,opcode.length);
		long hash=c.getValue();
		return hash;
	}
	
	public static boolean findChanges(HashMap<String,Long> hm1,HashMap<String,Long> hm2){
		for(String s1: hm1.keySet())
			if(hm1.get(s1).compareTo(hm2.get(s1))!=0)
				return false;
		return true;
	}
	
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

