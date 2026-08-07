package de.ep.astralcores.config;

import blue.endless.jankson.Comment;

public class Config {

    @Comment("Configuration values for all 12 Astral Cores")
    public Cores cores = new Cores();

    public static class Cores {
        public Aero aero = new Aero();
        public Gale gale = new Gale();
        public Chrono chrono = new Chrono();
        public Gravity gravity = new Gravity();
        public Frost frost = new Frost();
        public Phoenix phoenix = new Phoenix();
        public Leviathan leviathan = new Leviathan();
        public Shadow shadow = new Shadow();
        public Berserker berserker = new Berserker();
        public Illusion illusion = new Illusion();
        public Nature nature = new Nature();
        public Magnet magnet = new Magnet();
    }

    public static class Aero {
        public AeroJump aero_jump = new AeroJump();
        public AirCushion air_cushion = new AirCushion();

        public static class AeroJump {
            @Comment("Cooldown in seconds")
            public int cooldown = 15;
            @Comment("Radius of the landing shockwave")
            public double shockwave_radius = 4.0;
            @Comment("Damage dealt by shockwave (2.0 = 1 heart)")
            public double damage = 4.0;
            @Comment("Strength of the knockback effect")
            public double knockback_strength = 1.0;
        }
        public static class AirCushion {
            @Comment("Percentage of fall damage reduction (0.65 = 65% reduction)")
            public double fall_damage_reduction = 0.65;
        }
    }

    public static class Gale {
        public SonicDash sonic_dash = new SonicDash();
        public Lightfeet lightfeet = new Lightfeet();

        public static class SonicDash {
            @Comment("Cooldown in seconds")
            public int cooldown = 12;
            @Comment("Explosion damage (5.0 = 2.5 hearts)")
            public double damage = 5.0;
            @Comment("Strength of the forward dash explosion knockback")
            public double knockback_strength = 0.8;
        }
        public static class Lightfeet {
            @Comment("Speed potion effect amplifier (0 = Speed I, 1 = Speed II)")
            public int speed_amplifier = 0;
        }
    }

    public static class Chrono {
        public TimeReturn time_return = new TimeReturn();
        public SecondTimeline second_timeline = new SecondTimeline();

        public static class TimeReturn {
            @Comment("Cooldown in seconds")
            public int cooldown = 60;
            @Comment("How many seconds of movement history to rewind")
            public int rewind_seconds = 5;
        }
        public static class SecondTimeline {
            @Comment("Chance to cheat death and fully heal (0.50 = 50%)")
            public double trigger_chance = 0.50;
            @Comment("Global cooldown in seconds after a trigger failure/success (600 = 10 min)")
            public int global_cooldown = 600;
        }
    }

    public static class Gravity {
        public GravityPull gravity_pull = new GravityPull();
        public HeavyPresence heavy_presence = new HeavyPresence();

        public static class GravityPull {
            @Comment("Cooldown in seconds")
            public int cooldown = 25;
            @Comment("Radius around the player to pull others in")
            public double pull_radius = 6.0;
        }
        public static class HeavyPresence {
            @Comment("Permanent knockback resistance attribute (1.0 = full immunity)")
            public double knockback_resistance = 0.5;
        }
    }

    public static class Frost {
        public FrostLock frost_lock = new FrostLock();
        public FrostAura frost_aura = new FrostAura();

        public static class FrostLock {
            @Comment("Cooldown in seconds")
            public int cooldown = 30;
            @Comment("Freeze effect duration in seconds")
            public double freeze_duration = 2.0;
        }
        public static class FrostAura {
            @Comment("Radius of the slowness aura field")
            public double radius = 5.0;
            @Comment("Maximum slowness amplifier at closest proximity (1 = Slowness II)")
            public int max_slowness_amplifier = 1;
        }
    }

    public static class Phoenix {
        public PhoenixBurst phoenix_burst = new PhoenixBurst();
        public Flameborn flameborn = new Flameborn();

        public static class PhoenixBurst {
            @Comment("Cooldown in seconds")
            public int cooldown = 40;
            @Comment("Explosion area radius")
            public double explosion_radius = 6.0;
            @Comment("Fire ticks duration for hit targets in seconds")
            public int burn_duration = 5;
        }
        public static class Flameborn {
            @Comment("Regeneration effect amplifier while in the Nether (0 = Regen I, 1 = Regen II)")
            public int static_regen_amplifier_in_nether = 0;
        }
    }

    public static class Leviathan {
        public Whirlpool whirlpool = new Whirlpool();
        public Oceanborn oceanborn = new Oceanborn();

        public static class Whirlpool {
            @Comment("Cooldown in seconds")
            public int cooldown = 35;
            @Comment("Vortex pulling radius on land")
            public double radius_land = 5.0;
            @Comment("Vortex pulling radius when submerged underwater")
            public double radius_water = 10.0;
        }
        public static class Oceanborn {
            @Comment("Water breathing air capacity duration in minutes")
            public int breath_meter_minutes = 10;
            @Comment("Swim speed modifier multiplier")
            public double swim_speed_multiplier = 1.5;
        }
    }

    public static class Shadow {
        public SmokeVeil smoke_veil = new SmokeVeil();
        public LivingShadow living_shadow = new LivingShadow();

        public static class SmokeVeil {
            @Comment("Cooldown in seconds")
            public int cooldown = 45;
            @Comment("Smoke cloud lifespan in seconds")
            public int duration = 8;
            @Comment("Self speed effect amplifier (1 = Speed II)")
            public int speed_amplifier = 1;
            @Comment("Melee damage multiplier inside smoke (1.20 = +20% damage)")
            public double damage_modifier = 1.20;
        }
        public static class LivingShadow {
            @Comment("Seconds required to sneak in darkness before triggering invisibility")
            public int sneak_trigger_seconds = 5;
        }
    }

    public static class Berserker {
        public RageMode rage_mode = new RageMode();
        public Bloodlust bloodlust = new Bloodlust();

        public static class RageMode {
            @Comment("Cooldown in seconds")
            public int cooldown = 60;
            @Comment("Rage active duration in seconds")
            public int duration = 10;
            @Comment("Melee damage multiplier during rage (1.30 = +30% damage)")
            public double damage_modifier = 1.30;
            @Comment("Bonus knockback strength added to attacks")
            public double knockback_bonus = 0.5;
        }
        public static class Bloodlust {
            @Comment("Health restored on every kill (4.0 = 2 hearts)")
            public double heal_amount = 4.0;
        }
    }

    public static class Illusion {
        public MirrorSwap mirror_swap = new MirrorSwap();
        public MirrorImage mirror_image = new MirrorImage();

        public static class MirrorSwap {
            @Comment("Cooldown in seconds")
            public int cooldown = 40;
            @Comment("Decoy lifetime duration in seconds")
            public int decoy_duration = 10;
        }
        public static class MirrorImage {
            @Comment("Chance to trigger a visual misdirection illusion when hit (0.15 = 15%)")
            public double trigger_chance = 0.15;
        }
    }

    public static class Nature {
        public RootTrap root_trap = new RootTrap();
        public NatureBlessing nature_blessing = new NatureBlessing();

        public static class RootTrap {
            @Comment("Cooldown in seconds")
            public int cooldown = 45;
            @Comment("Rooting/vines duration in seconds")
            public int duration = 3;
            @Comment("Radius when used in normal biomes")
            public double radius_default = 4.0;
            @Comment("Expanded radius when used inside natural green biomes")
            public double radius_natural = 8.0;
        }
        public static class NatureBlessing {
            @Comment("Regeneration potion effect amplifier inside natural biomes (0 = Regen I)")
            public int static_regen_amplifier = 0;
            @Comment("Speed potion effect amplifier inside natural biomes (0 = Speed I)")
            public int static_speed_amplifier = 0;
        }
    }

    public static class Magnet {
        public MagneticPull magnetic_pull = new MagneticPull();
        public MagneticGrip magnetic_grip = new MagneticGrip();

        public static class MagneticPull {
            @Comment("Cooldown in seconds")
            public int cooldown = 20;
            @Comment("Item/XP orb suction range radius")
            public double radius = 15.0;
        }
        public static class MagneticGrip {
            @Comment("Chance on landing critical hits to disarm target (0.20 = 20%)")
            public double trigger_chance = 0.20;
            @Comment("Duration in seconds the target's weapon stays in normal inventory")
            public double disarm_duration = 2.0;
        }
    }
}
