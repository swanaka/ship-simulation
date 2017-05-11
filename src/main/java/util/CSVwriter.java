package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

public class CSVwriter {
	public static void write(String filename, List<String> output, boolean overwrite){
		File file = new File(filename);
		PrintWriter pw = null;

		try {
			// 出力ストリームを生成
			if(overwrite == false){
				pw = new PrintWriter(
						new OutputStreamWriter(
							new FileOutputStream(file, false)));
			}else{
				pw = new PrintWriter(
						new OutputStreamWriter(
							new FileOutputStream(file, true)));
			}
			String sample = null;
			for (String elem: output){
				sample = elem + ", ";
				pw.print(sample);
			}
			pw.println();
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			// クローズ処理
			if (pw != null) {
				pw.close();
			}
		}
	}
}
