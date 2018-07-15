package com.dkorobtsov.logging;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import okhttp3.internal.platform.Platform;

/**
 * DefaultLogger double with additional methods for testing purposes.
 * All published events are registered and can be retrieved for validation.
 */
public class TestLogger implements LogWriter {

  private List<String> events = new ArrayList<>(Collections.emptyList());
  private StreamHandler logOutputHandler;
  private OutputStream logOut;
  private Logger testLogger = Logger.getLogger("TestLogger");

  public TestLogger(LogFormatter logFormatter) {
    testLogger.setUseParentHandlers(false);

    // Removing existing handlers for new instance
    Arrays.stream(testLogger.getHandlers()).forEach(testLogger::removeHandler);

    // Configuring output to console
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(logFormatter.formatter);
    testLogger.addHandler(consoleHandler);

    // Configuring output to stream
    logOut = new ByteArrayOutputStream();
    logOutputHandler = new StreamHandler(logOut, logFormatter.formatter);

    testLogger.addHandler(logOutputHandler);
  }

  @Override
  public void log(int type, String msg) {
    switch (type) {
      case Platform.INFO:
        testLogger.log(Level.INFO, msg);
        break;
      default:
        testLogger.log(Level.WARNING, msg);
        break;
    }
    events.add(msg);
  }

  /**
   * @return Returns raw messages
   * (in case we want to check content only and  don't care about format)
   */
  List<String> rawMessages() {
    return events;
  }

  /**
   * @return Returns first formatted event published by current logger
   */
  String firstRawEvent() {
    return rawMessages().get(0).trim();
  }

  /**
   * @return Returns last formatted event published by current logger
   */
  String lastRawEvent() {
    return rawMessages().get(rawMessages().size() - 1).trim();
  }

  /**
   * @return Returns all formatted events published by current logger as String
   */
  String formattedOutput() {
    logOutputHandler.flush();
    return logOut.toString();
  }

  /**
   * @return Returns all formatted events published by current logger as String array
   */
  public String[] outputAsArray() {
    return formattedOutput().split("\r?\n");
  }

  /**
   * @return Returns first formatted event published by current logger
   */
  String firstFormattedEvent() {
    return outputAsArray()[0].trim();
  }

  /**
   * @return Returns last formatted event published by current logger
   */
  String lastFormattedEvent() {
    return outputAsArray()[outputAsArray().length - 1].trim();
  }

}