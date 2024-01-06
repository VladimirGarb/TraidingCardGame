package org.example.application.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;

//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public enum CardType {

    MONSTER,
    SPELL
}
