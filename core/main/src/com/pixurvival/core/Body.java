package com.pixurvival.core;

/**
 * Interface qui représente un "corps physique" avec une position et une
 * bounding box. Cette interface a pour intérêt d'être une abstraction pour tout
 * objet devant interragir avec les autres en fonction de sa position, ou de sa
 * bounding box.
 * 
 * @author SharkHendrix
 *
 */
public interface Body extends Positionnable {

	/**
	 * @return Moitié de la largeur de la bounding box
	 */
	double getHalfWidth();

	/**
	 * @return Moitié de la hauteur de la bounding box
	 */
	double getHalfHeight();

	default double getDisplayDeath() {
		return getPosition().getY();
	}
}
