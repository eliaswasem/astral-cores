package de.ep.astralcores.config;

import blue.endless.jankson.Comment;

// Defines the root configuration structure for the mod settings
public class Config {

    @Comment("General core mechanics and server rules")
    public General general = new General();

    // Nested configurations containing base server rules
    public static class General {
        @Comment("Should the equipped core be removed from the slot when a player dies?")
        public boolean drop_core_on_death = true;
    }
}
