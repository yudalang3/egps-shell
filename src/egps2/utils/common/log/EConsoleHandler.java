package egps2.utils.common.log;

import java.util.logging.ConsoleHandler;

/**
 * 这个类用来替代JAVA默认日志的输出类。
 * 我现在的需求是希望输出的日志不要在System.err，而是在System.out
 * @author yudalang
 *
 */
public class EConsoleHandler extends ConsoleHandler {
	
	public EConsoleHandler() {
		super();
        setOutputStream(System.out);
	}

}
