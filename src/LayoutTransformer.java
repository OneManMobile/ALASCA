import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;


public class LayoutTransformer {

	static  int BASE_WIDTH = 400;
	static  int BASE_HEIGHT = 800;
	
	static  int TARGET_WIDTH = 320;
	static  int TARGET_HEIGHT = 480;
	
	static private final String newline = "\n";
	
	
	public static String transformLayouts(JTextArea log, File[] files, int baseWidth, int baseHeight, int targetWidth, int targetHeight, String path){
		
		BASE_WIDTH = baseWidth;
		BASE_HEIGHT = baseHeight;
		TARGET_WIDTH = targetWidth;
		TARGET_HEIGHT = targetHeight;
	
		
		try{
		String ranName = "" + (System.currentTimeMillis() / 1000 );
		
		boolean success = (new File(path+"/"+"layout-"+ranName)).mkdir();
		
		
		for(File file : files){
			
				String fileString = readFileAsString(file.getAbsolutePath());
				
				String newFileString = SwitchaRoo(fileString);
			
				BufferedWriter writer = null;
		        try
		        {
		        		File newLayoutFile = new File(path+"/"+"layout-"+ranName+"/"+ file.getName());
		                writer = new BufferedWriter( new FileWriter(newLayoutFile));
		                writer.write( newFileString);

		        }
		        catch ( IOException e)
		        {
		        	log.append("1 "+e.toString() + newline);
		        	log.setCaretPosition(log.getDocument().getLength());
		        }
		        finally
		        {
		                try
		                {
		                        if ( writer != null)
		                                writer.close( );
		                }
		                catch ( IOException e)
		                {
		                	log.append("2 "+e.toString() + newline);
				        	log.setCaretPosition(log.getDocument().getLength());
		                }
		                
		     }
				
		}
		
		return path+"/"+"layout-"+ranName;
		}catch(Exception e){
			
			log.append("3 "+e.toString() + newline);
        	log.setCaretPosition(log.getDocument().getLength());
			return "ERROR!!";
		}
		
		
		
	}
	
	private static String SwitchaRoo(String fileString) {
		
		fileString = findAndReplacePadding(fileString);
		fileString = findAndReplaceMargin(fileString);
		
		fileString = findAndReplace(fileString,"android:layout_width=\"[0-9]+dp\"",true);
		fileString = findAndReplace(fileString,"android:paddingRight=\"[0-9]+dp\"",true);
		fileString = findAndReplace(fileString,"android:paddingLeft=\"[0-9]+dp\"",true);
		fileString = findAndReplace(fileString,"android:layout_marginRight=\"[0-9]+dp\"",true);
		fileString = findAndReplace(fileString,"android:layout_marginLeft=\"[0-9]+dp\"",true);
		
		
		fileString = findAndReplace(fileString,"android:layout_height=\"[0-9]+dp\"",false);
		fileString = findAndReplace(fileString,"android:paddingTop=\"[0-9]+dp\"",false);
		fileString = findAndReplace(fileString,"android:paddingBottom=\"[0-9]+dp\"",false);
		fileString = findAndReplace(fileString,"android:layout_marginTop=\"[0-9]+dp\"",false);
		fileString = findAndReplace(fileString,"android:layout_marginBottom=\"[0-9]+dp\"",false);
		
		fileString = findAndReplace(fileString,"android:textSize=\"[0-9]+dp\"",false);
		
		return fileString;
		
	}
	
	public static String findAndReplacePadding(String target) {
		  Pattern patt = Pattern.compile("android:padding=\"[0-9]+dp\"");
		  Matcher m = patt.matcher(target);
		  StringBuffer sb = new StringBuffer(target.length());
		  while (m.find()) {
		    String text = m.group();
		    m.appendReplacement(sb, extendPadding(text));
		  }
		  m.appendTail(sb);
		  return sb.toString();
		}
	
	public static String extendPadding(String text){
		
		text = 
				"android:paddingTop"+text.substring(15)+"\n"+
				"android:paddingBottom"+text.substring(15)+"\n"+
				"android:paddingRight"+text.substring(15)+"\n"+
				"android:paddingLeft"+text.substring(15)+"\n";
		return text;
	}
	
	public static String findAndReplaceMargin(String target) {
		  Pattern patt = Pattern.compile("android:layout_margin=\"[0-9]+dp\"");
		  Matcher m = patt.matcher(target);
		  StringBuffer sb = new StringBuffer(target.length());
		  while (m.find()) {
		    String text = m.group();
		    m.appendReplacement(sb, extendMargin(text));
		  }
		  m.appendTail(sb);
		  return sb.toString();
		}
	
	public static String extendMargin(String text){
		
		text = 
				"android:layout_marginTop"+text.substring(21)+"\n"+
				"android:layout_marginBottom"+text.substring(21)+"\n"+
				"android:layout_marginRight"+text.substring(21)+"\n"+
				"android:layout_marginLeft"+text.substring(21)+"\n";
		return text;
	}
	
	
	
	public static String findAndReplace(String target, String regex, boolean widthReplace) {
		  Pattern patt = Pattern.compile(regex);
		  Matcher m = patt.matcher(target);
		  StringBuffer sb = new StringBuffer(target.length());
		  while (m.find()) {
		    String text = m.group();
		   	if(widthReplace){
		   	 m.appendReplacement(sb, newWidth(text));
		   	}else{
		   	 m.appendReplacement(sb, newHeight(text));
		   	}
		   
		  }
		  m.appendTail(sb);
		  return sb.toString();
		}
	
	

	private static String readFileAsString(String filePath)
		    throws java.io.IOException{
		        StringBuffer fileData = new StringBuffer(1500);
		        BufferedReader reader = new BufferedReader(
		                new FileReader(filePath));
		        char[] buf = new char[1024];
		        int numRead=0;
		        while((numRead=reader.read(buf)) != -1){
		            String readData = String.valueOf(buf, 0, numRead);
		            fileData.append(readData);
		            buf = new char[1024];
		        }
		        reader.close();
		        return fileData.toString();
		    }
	
	public static String newWidth(String group){
		
		 Pattern patt = Pattern.compile("[0-9]+");
		  Matcher m = patt.matcher(group);
		  StringBuffer sb = new StringBuffer(group.length());
		  while (m.find()) {
		    String text = m.group();
		   	 m.appendReplacement(sb, scaleWidth(Integer.parseInt(text)));
		   	
		  }
		  m.appendTail(sb);
		  return sb.toString();
		
	}
	
	public static String scaleWidth(int value){
		int dpUnits = value;
		
		double scale = (double) (1.0 * TARGET_WIDTH / BASE_WIDTH);
		
		dpUnits = (int)(dpUnits * scale);
		
		return ""+dpUnits;
		
	}
	
	public static String newHeight(String group){
		
		Pattern patt = Pattern.compile("[0-9]+");
		  Matcher m = patt.matcher(group);
		  StringBuffer sb = new StringBuffer(group.length());
		  while (m.find()) {
		    String text = m.group();
		   	 m.appendReplacement(sb, scaleHeight(Integer.parseInt(text)));
		   	
		  }
		  m.appendTail(sb);
		  return sb.toString();
	
	}
	
	public static String scaleHeight(int value){
int dpUnits = value;
		
		double scale = (double) (1.0 * TARGET_HEIGHT / BASE_HEIGHT);
		
		dpUnits = (int)(dpUnits * scale);
		
		return ""+dpUnits;
		
	}
	
}
