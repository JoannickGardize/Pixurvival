package com.pixurvival.core.entity;

/**
 * Used to find the source of an effect, in a "chain" style, and then apply
 * stats specifics operation.
 * 
 * @author SharkHendrix
 *
 */
public interface SourceProvider {

	Object getSource();
}
