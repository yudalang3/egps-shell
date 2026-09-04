package egps2.utils.common.util;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineTemplate provides shared utility logic for eGPS modules and UI.
 */
public abstract class CommandLineTemplate {

	private Options options;
	
	private String helpOptionString = "help";
	
	
	public CommandLineTemplate() {
		List<Option> listOfOptions = new ArrayList<Option>();

		listOfOptions.add(new Option("h", helpOptionString, false, "print help messages."));
		
		/**
		 * Example:
		 * listOfOptions.add(new Option("d", debugOptionString, false, "print debug messages"));
		 * 
		 * listOfOptions.add(Option.builder(existedMainDataFileOptionString).hasArg().argName("file")
				.desc("Set existed main data file.").build());
		 */
		
		completeTheOptions(listOfOptions);
		
		options = new Options();
		for (Option option : listOfOptions) {
			options.addOption(option);
		}
	}


	protected abstract void completeTheOptions(List<Option> listOfOptions) ;
	
	protected void printUsageAndStop() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -cp needed.jar  ", options, true);
		System.exit(0);
	}
	
	protected void parseParameters(String[] args) throws Exception {
		// create the parser
		CommandLineParser parser = new DefaultParser();

		CommandLine line = null;
		try {
			// parse the command line arguments
			line = parser.parse(options, args);
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			return;
		}

		if (line.hasOption(helpOptionString)) {
			printUsageAndStop();
		}
		
		completeTheParsers(line);
	}


	protected abstract void completeTheParsers(CommandLine line);
}
