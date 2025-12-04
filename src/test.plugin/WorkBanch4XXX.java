package test.plugin;

import java.awt.Font;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import egps2.frame.gui.comp.SimplestWorkBench;
import egps2.EGPSProperties;
import egps2.UnifiedAccessPoint;
import graphic.engine.guirelated.JTextareaRedirector;

@SuppressWarnings("serial")
/**
 * WorkBanch4XXX supports the plugin/template system for extending eGPS.
 */
public class WorkBanch4XXX extends SimplestWorkBench {

	@Override
	protected Font getDefaultFont() {
		return UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
	}
	
	@Override
	protected Pair<String, String> giveExample() {
		StringBuilder sBuilder = EGPSProperties.getSpecificationHeader();

		sBuilder.append("# This is annotation.\n");
		sBuilder.append("# The inputPath parameter could be a directory or file\n");
		sBuilder.append("$inputPath=your/fasta/path\n");
		sBuilder.append("# The fileSuffix parameter has no effect when the $inputPath is a file\n");
		sBuilder.append("# only file with following suffix will be used\n");
		sBuilder.append("# * means all suffix ; file name start with \".\" will not take into consideration\n");
		sBuilder.append("$fileSuffix=*\n");
		sBuilder.append("# The removeGap parameter indicates whether keep the original alignment\n");
		sBuilder.append("# If true all '-' character will be removed\n");
		sBuilder.append("$removeGap=true\n");
		sBuilder.append("# The outputFile parameter means the location to export\n");
		sBuilder.append("$outputFile=your/expected/export/path\n");
		sBuilder.append("# Following are records: you can directly input sequence name or name with start posiition and end position\n");
		sBuilder.append("# Each element seperate with tab key\n");
		sBuilder.append("# Name without position will be considered as full length\n");
		sBuilder.append("seq1\n");
		sBuilder.append("seq2	5\n");
		sBuilder.append("seq3	6	100");

		String left = sBuilder.toString();
		sBuilder.setLength(0);
		sBuilder.append("Here will report some process\n");
		sBuilder.append("The results will be exported to outputFile\n");

		return Pair.of(left, sBuilder.toString());
	}
	
	

	@Override
	protected void handle(List<String> inputStrings) {
		
		bottomTextarea.setText("");
		JTextareaRedirector jTextareaRedirector = new JTextareaRedirector();
		jTextareaRedirector.initialize(bottomTextarea);
		
		try {
//			FastaDumperRunner fastaDumperRunner = new FastaDumperRunner();
//			fastaDumperRunner.run(inputStrings);
		} finally {
			jTextareaRedirector.finished();
		}
		
		
//		egps2.remnant.lowtextedi.IndependentModuleLoader loader = new egps2.remnant.lowtextedi.IndependentModuleLoader();
//		loader.loadTheTextContent2thisModule("Thfefefeis is the text\n");

	}

}
