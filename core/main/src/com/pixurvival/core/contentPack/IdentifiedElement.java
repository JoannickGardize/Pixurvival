package com.pixurvival.core.contentPack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * An identified element is an element intended to be stored in a List of
 * elements, its id correspond to the index it is stored in this List.
 *
 * @author SharkHendrix
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentifiedElement implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id = 0;
}
