package de.ep.astralcores.config;

import blue.endless.jankson.Comment;

// Defines the root configuration structure for the mod settings
public class Config {

    @Comment("General core mechanics and server rules")
    public General general = new General();
    public Structure structure = new Structure();

    // Nested configurations containing base server rules
    public static class General {
        @Comment("Should the equipped core be removed from the slot when a player dies?")
        public boolean drop_core_on_death = true;
    }

    public static class Structure {
        @Comment("How many structures of each type should exist")
        public int structures_per_core = 1;

        @Comment("Maximum spawn radius")
        public int structure_spawn_radius = 5000;

        @Comment("Should structures spawn at randomized positions per spawn")
        public boolean randomized_structure_spawn = true;
    }
}
