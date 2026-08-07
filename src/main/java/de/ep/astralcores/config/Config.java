package de.ep.astralcores.config;

import blue.endless.jankson.Comment;

public class Config {

    @Comment("General core mechanics and server rules")
    public General general = new General();

    public static class General {
        @Comment("What happens to equipped cores upon death? Options: ALL, RANDOM, NONE")
        public DeathBehavior core_death_behavior = DeathBehavior.ALL;
    }

    public enum DeathBehavior {
        @Comment("Wipes both equipped cores instantly")
        ALL,
        @Comment("Clears only one randomly selected core slot (50/50 chance)")
        RANDOM,
        @Comment("Players keep all their cores (acts like keepInventory)")
        NONE
    }
}
