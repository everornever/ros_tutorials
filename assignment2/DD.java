import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

public class DD {

	public static void main(String[] args) throws IOException, BadLocationException{
		String fileString =getSourceCodeString(args[0]);
		List<ASTNode> ls=new ArrayList<ASTNode>();
		Document d1=new Document(fileString);
		String result=new String();
		while(true){
			int n=0;
			Document d2=copyDoc(d1);
			CompilationUnit cu=buildAST(ls,d2);
			System.out.println(ls.size());
			for(int i=0;i<ls.size();i++){
				deleteNode(d2,cu,ls.get(i));
				result=checkProgram(d2,args[1]);
				if(result.compareTo("CRASH")==0){
					n=1;
					d1=copyDoc(d2);
					i=0;
				}
				d2=copyDoc(d1);
				cu=buildAST(ls,d2);
			}
			if(n==0)
				break;
		}
		File file=new File("MINI.java");
		FileOutputStream fos=new FileOutputStream(file);
		BufferedWriter bw-new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(d1.get());
		bw.close();
	}
	/**
	 * build a AST tree from the source code, add all nodes into a list
	 * return the compilationunit of the AST 
	 */
	public static CompilationUnit buildAST(List<ASTNode> ls,Document d){
		ls.clear();
		ASTParser parser=ASTParser.newParser(AST.JLS3);
		parser.setSource(d.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu=(CompilationUnit) parser.createAST(null);
		cu.accept(new ASTVisitor()	{ 
				public boolean visit(ImportDeclaration  node){
					ls.add(node);
					return false;
				}
				public boolean visit(TypeDeclaration node){
					node.accept(new ASTVisitor(){
						public boolean visit(FieldDeclaration node){
							ls.add(node);
							return true;
						}
						public boolean visit(MethodDeclaration node){
							SimpleName name=node.getName();
							if(name.getIdentifier().compareTo("main")!=0)
								ls.add(node);
							Block b=node.getBody();
							@SuppressWarnings("unchecked")
							List<Statement> l=b.statements();
							for(int i=0;i<l.size();i++){
								ls.add(l.get(i));
							}
							return false;
						}
					});
					return false;
				}
		});
		return cu;
	}

	/**
	 * @param path of the class file
	 * @return the source code string
	 * @throws IOException
	 * read source code from .class file.
	 */
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
	/**
	 * @throws BadLocationException 
	 * @throws org.eclipse.jface.text.BadLocationException 
	 * remove a node from the given AST and write change to document 
	 */
	public static void deleteNode(Document d,CompilationUnit cu,ASTNode node) throws BadLocationException{
		ASTRewrite rewriter=ASTRewrite.create(cu.getAST());
			rewriter.remove(node, null);
		try {
			TextEdit edits = rewriter.rewriteAST(d,null);
			UndoEdit undo=edits.apply(d);
		} catch (IllegalArgumentException | MalformedTreeException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @throws IOException 
	 * check whether the modified program crashes or not by invoking a new system process and 
	 * execute the m.sh executeble, get the return value.
	 */
	public static String checkProgram(Document d,String filePath) throws IOException{
		PrintWriter out = new PrintWriter("MINI.java");
		String javaString=d.get();
		javaString=javaString.replaceFirst("input", "MINI");
		out.write(javaString);
		out.close();
		Process p=new ProcessBuilder(filePath,"MINI.java").start();
		BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line = null;
		while ( (line = reader.readLine()) != null) {
			builder.append(line);
		}
		String result = builder.toString();
		return result;
	}
	/**
	 * copy a document to another document
	 */
	public static Document copyDoc(Document d1){
		String s=d1.get();
		Document d2=new Document(s);
		return d2;
	}
}
