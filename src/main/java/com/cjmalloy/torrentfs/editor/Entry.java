package com.cjmalloy.torrentfs.editor;

import java.io.PrintStream;

import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.ui.TopLevel;
import com.cjmalloy.torrentfs.editor.ui.fx.FxUiModule;
import com.cjmalloy.torrentfs.editor.ui.swing.SwingUiModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import jargs.gnu.CmdLineParser;

public class Entry {

  private static final String defaultUi = "fx";

  /**
   * Display program usage on the given {@link PrintStream}.
   */
  private static void usage(PrintStream s) {
    usage(s, null);
  }

  /**
   * Display a message and program usage on the given {@link PrintStream}.
   */
  private static void usage(PrintStream s, String msg) {
    if (msg != null) {
      s.println(msg);
      s.println();
    }

    s.println("usage: tfs-gui [options] [file|directory]");
    s.println();
    s.println("Available options:");
    s.println("  -h,--help             Show this help and exit.");
    s.println();
    s.println("  -u,--ui               Set the UI ('fx' or 'swing').");
    s.println();
  }

  public static void main(String[] args) {

    CmdLineParser parser = new CmdLineParser();
    CmdLineParser.Option argHelp = parser.addBooleanOption('h', "help");
    CmdLineParser.Option argUi = parser.addStringOption('u', "ui");

    try {
      parser.parse(args);
    } catch (CmdLineParser.OptionException oe) {
      System.err.println(oe.getMessage());
      usage(System.err, "Error parsing arguments.");
      System.exit(1);
    }

    // Display help and exit if requested
    if (Boolean.TRUE.equals(parser.getOptionValue(argHelp))) {
      usage(System.out);
      System.exit(0);
    }

    String ui = (String) parser.getOptionValue(argUi, defaultUi);
    String[] otherArgs = parser.getRemainingArgs();
    Injector uiInjector;
    if (ui.equals("fx")) {
      uiInjector = Guice.createInjector(new FxUiModule());
    } else {
      uiInjector = Guice.createInjector(new SwingUiModule());
    }

    TopLevel topLevel = uiInjector.getInstance(TopLevel.class);
    MainController.get().updateAll();
    topLevel.exitOnFinish();
  }
}
