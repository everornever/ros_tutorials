
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jface.text.Document;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
 
public class MSRmini {
	public static void main(String... args) throws IOException{
		File dir=new File(args[0]);
		File[] javaFiles=dir.listFiles(new FilenameFilter(){
			public boolean accept(File dir,String name){
				return name.endsWith(".java");
			}
		});
		int count1=0;
		int count2=0;
		String docString = new String();
		for(int i=0;i<javaFiles.length;i++){
			String fileString=getSourceCodeString(javaFiles[i].getAbsolutePath());
			Document d=new Document(fileString);
			List<TagElement> lsTag=new ArrayList<TagElement>();
			docString=docString.concat(buildAST(lsTag,d));
		}
		System.out.println(docString);
		File f=new File("doc");
		FileOutputStream fo=new FileOutputStream(f);
		BufferedWriter bw1=new BufferedWriter(new OutputStreamWriter(fo));
		bw1.write(docString);
		bw1.close();
		count1=count(docString,"NullPointerException");
		docString=docString.toLowerCase();
		String taggedDocStr=posTagger(docString);
		count2=count(taggedDocStr,"VB");
		File result=new File("RESULT.txt");
		FileOutputStream fos=new FileOutputStream(result);
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(Integer.toString(count1));
		bw.newLine();
		bw.write(Integer.toString(count2));
		bw.close();
	}
	
	public static int count(String docStr,String regex){
		int count=0;
		Pattern p=Pattern.compile(regex);
		Matcher m=p.matcher(docStr);
		while(m.find()){
			count++;
		}
		return count;
	}
	
	public static String getSourceCodeString(String filename) throws IOException {
		StringBuilder sb=new StringBuilder(1024);
		BufferedReader br=new BufferedReader(new FileReader(filename));
		char[] buf=new char[1024];
		int numOfChars=0;
		while((numOfChars=br.read(buf))!=-1){
			String readCodes=String.valueOf(buf,0,numOfChars);
			sb.append(readCodes);
			buf=new char[1024];
		}
		br.close();
		return sb.toString();
	}
	
	public static String buildAST(List<TagElement> ls,Document d){
		ASTParser parser=ASTParser.newParser(AST.JLS3);
		parser.setSource(d.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu=(CompilationUnit) parser.createAST(null);
		cu.accept(new ASTVisitor(){
			@SuppressWarnings("unchecked")
			public boolean visit(Javadoc node){
				ls.addAll(node.tags());
				return true;
			}
		});
		String docString=new String();
		for(int i=0;i<ls.size();i++){
			docString=docString.concat(ls.get(i).toString());
		}
		return docString;
	}
	
	public static String posTagger(String toTagString){
		MaxentTagger tagger=new MaxentTagger("english-left3words-distsim.tagger");
		String taggedString=tagger.tagString(toTagString);
		return taggedString;
	}
}

