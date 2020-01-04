package com.pixurvival.server.console;

public interface CommandProcessor {

	boolean process(String[] args) throws Exception;
}
