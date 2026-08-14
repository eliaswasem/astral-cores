# Astral Cores Architecture Documentation

## Overview
Astral Cores splits each core into a configuration class and a corresponding static logic class.

```
core/
└── cores/
    ├── AeroCore.java
    ├── GaleCore.java
    ├── GravityCore.java
    └── logic/
        ├── AeroCoreLogic.java
        ├── GaleCoreLogic.java
        └── GravityCoreLogic.java
```

### Main Rule
* **Core Subclasses** define the item's identity, metadata, and core parameters.
* **CoreLogic Classes** handle the actual feature implementation, events, tasks, and state tracking.

This keeps all features for a specific core inside a single logic class, rather than breaking them apart based on how they are triggered (Active vs. Passive vs. Event).

---

## Global Codebase Layout
The complete architecture maps out into the following package schema:

```text
src/main/java/de/ep/astralcores/
├── command/
│   ├── actionbar/
│   │   ├── ActionBarCommand.java
│   │   └── ActionBarCommandLogic.java
│   ├── activate/
│   │   ├── ActivateCommand.java
│   │   └── ActivateCommandLogic.java
│   ├── core/
│   │   ├── CoreCommand.java
│   │   └── CoreCommandLogic.java
│   ├── trust/
│   │   ├── TrustCommand.java
│   │   └── TrustCommandLogic.java
│   ├── untrust/
│   │   ├── UntrustCommand.java
│   │   └── UntrustCommandLogic.java
│   ├── withdraw/
│   │   ├── WithdrawCommand.java
│   │   └── WithdrawCommandLogic.java
│   └── CommandRegistry.java
│
├── config/
│   ├── Config.java
│   └── ConfigManager.java
│
├── core/
│   ├── cores/
│   │   ├── AeroCore.java
│   │   ├── BerserkerCore.java
│   │   ├── ChronoCore.java
│   │   ├── FrostCore.java
│   │   ├── GaleCore.java
│   │   ├── GravityCore.java
│   │   ├── IllusionCore.java
│   │   ├── LeviathanCore.java
│   │   ├── MagnetCore.java
│   │   ├── NatureCore.java
│   │   ├── PhoenixCore.java
│   │   ├── ShadowCore.java
│   │   └── logic/
│   │       ├── AeroCoreLogic.java
│   │       ├── BerserkerCoreLogic.java
│   │       ├── ChronoCoreLogic.java
│   │       ├── FrostCoreLogic.java
│   │       ├── GaleCoreLogic.java
│   │       ├── GravityCoreLogic.java
│   │       ├── IllusionCoreLogic.java
│   │       ├── LeviathanCoreLogic.java
│   │       ├── MagnetCoreLogic.java
│   │       ├── NatureCoreLogic.java
│   │       ├── PhoenixCoreLogic.java
│   │       └── ShadowCoreLogic.java
│   ├── Core.java
│   ├── CoreFactory.java
│   ├── CoreRegistry.java
│   └── CoreType.java
│
├── event/
│   ├── logic/
│   │   ├── CoreDeathLogic.java
│   │   └── CoreInteractLogic.java
│   ├── PlayerEvents.java
│   └── ServerLifecycleEventsListener.java
│
├── manager/
│   ├── ActionBarManager.java
│   ├── CooldownManager.java
│   ├── CoreActivateManager.java
│   └── CoreTickManager.java
│
├── mixin/
│   ├── BundleItemMixin.java
│   ├── ClientboundSetEquipmentPacketMixin.java
│   ├── HopperBlockEntityMixin.java
│   ├── ItemEntityMixin.java
│   ├── MobMixin.java
│   ├── PlayerEntityMixin.java
│   ├── ServerExplosionMixin.java
│   └── SlotAndShulkerBoxSlotMixin.java
│
├── playerdata/
│   ├── PlayerData.java
│   └── PlayerDataManager.java
│
├── util/
│   ├── BiomeUtils.java
│   ├── CropUtils.java
│   ├── Effects.java
│   ├── FoodUtils.java
│   └── TickTimer.java
│
├── AstralCores.java
└── MainLoop.java
```

---

## core/Core.java
`Core` is the base abstract class for all cores. It holds global item configurations and API metadata:

* `CoreType` identifier
* Custom display names and lore lists
* Base `Item` references and custom model data IDs
* Cooldown durations and capability identification strings
* Unicode symbols (`getCustomChar()`) for `ActionBarManager` displays

It provides the standard life cycle hooks:
* `applyPassive(ServerPlayer player)`
* `activate(ServerPlayer player)`
* `tick(ServerPlayer player)`
* `onRemoved(ServerPlayer player)`

---

## core/cores/
This package contains the light implementation classes (`AeroCore`, `GaleCore`, `ShadowCore`). Their only jobs are:
1. Passing initialization properties into `super()`.
2. Routing life cycle calls straight into their static `CoreLogic` class.

### Code Example
```java
@Override
public void activate(ServerPlayer player) {
    AeroCoreLogic.activate(player);
}

@Override
public void tick(ServerPlayer player) {
    AeroCoreLogic.tick(player);
}
```
**Rule:** No logic, math, particle spawning, or calculations belong in these files.

---

## core/cores/logic/
This package contains the real mechanics for every core. Active, passive, and event-based code for a single core lives together here.

```
logic/
    ├── AeroCoreLogic.java
    ├── GaleCoreLogic.java
    ├── GravityCoreLogic.java
    └── ShadowCoreLogic.java
```
### Structure Blueprint
```java
public final class AeroCoreLogic {
    // Weak structure to prevent memory retention
    public static final Set<ServerPlayer> activePlayers = Collections.newSetFromMap(new WeakHashMap<>());

    public static void applyPassive(ServerPlayer player) { ... }
    public static void activate(ServerPlayer player) { ... }
    public static void tick(ServerPlayer player) { ... }
    public static void onRemoved(ServerPlayer player) { ... }

    // Event listener delegate
    public static boolean handleFallShockwave(ServerPlayer player, DamageSource source) { ... }
}
```

---

## Command Routing & Execution Architecture (`command/`)
The `command/` package mirrors the core layout strategy by dividing commands into a dedicated **registration layer** and a decoupled **execution logic layer**.

```text
command/
└── trust/
    ├── TrustCommand.java       -> Registers the literal nodes, permissions, and arguments
    └── TrustCommandLogic.java  -> Handles data alterations, verification checks, and database saves
```

### 1. Separation Guidelines
* **`Command` Classes:** Solely responsible for building the Brigadier command tree, defining arguments (e.g., target players), checking command source permissions, and passing the context downwards into the logic companion.
* **`CommandLogic` Classes:** Handle the mechanical operations of the command. They cross-reference underlying profile variables, query the registry, interact safely with `PlayerData`, and dispatch confirmation messages.

### 2. Interfacing with PlayerData Safely
Commands modifying player state or trust profiles (like `/trust` or `/untrust`) must retrieve data cleanly via `AstralCores.PLAYER_DATA.get(player)` and make targeted edits through explicit thread-safe actions rather than duplicating access loops.

---

## Persistent Storage & Player Tracking (`playerdata/`)
The `playerdata/` package maintains both session-based runtime profiles and persistent SQLite backend operations.

### 1. PlayerData.java
Represents an in-memory session wrapper containing active parameters assigned to an individual player. This container stores structural states such as the chosen `ActionBarMode`, trusted ally lists, and active equipped core types.

### 2. PlayerDataManager.java
Directly handles SQL database queries, local transaction tasks, and active mapping arrays.
* **Database Management:** Hosts SQL statement structures to securely execute asynchronous load, insert, update, and deletion queries.
* **Session Lifecycle Operations:** Listens to global join/quit triggers to fetch records out of the SQLite table structure upon connection, loading them into `AstralCores.PLAYER_DATA`. When a connection terminates, it unloads active maps and saves dirty records back to disk.

---

## Memory & State Management

### 1. No `UUID` Tracking Maps
**Do not use `java.util.UUID` as keys for temporary trackers or task timers.**
* `UUID` objects are persistent identifiers detached from the server level lifecycle.
* If a player disconnects unexpectedly during a core task, a normal `HashMap<UUID, Data>` keeps the reference, causing a memory leak.

### 2. Using `WeakHashMap` with `ServerPlayer`
Always store temporary fields, players, and active timers using direct **`ServerPlayer`** or **`LivingEntity`** instances inside weak collections:

```java
// Safe background tracking for passives
public static final Set<ServerPlayer> armedPlayers =
        Collections.newSetFromMap(new WeakHashMap<>());

// Thread-safe weak map for active targets or entities
private static final Map<LivingEntity, FrostLock> activeLocks =
        Collections.synchronizedMap(new WeakHashMap<>());
```
#### How it works:
When a player leaves the server, Minecraft discards their current `ServerPlayer` instance. Java's Garbage Collector then cleans up the weak reference—**wiping the entry from the map without needing any manual cleanup code.**

### 3. Optimization via 1-Second Passive Loops
To save server performance, expensive background checks (like checking light values for `ShadowCoreLogic`) **must run on a 1-second interval (every 20 ticks) instead of firing on every server tick.**

* **The Problem:** Calling light calculation routines like `getBrightness()` 20 times a second per player drops server performance (TPS).
* **The Solution:** Evaluate passives once per second and adjust tracking math to use seconds instead of ticks (`+1` equals 1 full second passed).

```java
// Second-based tracking in ShadowCoreLogic
int secondsPassed = sneakTimers.getOrDefault(player, 0) + 1;
sneakTimers.put(player, secondsPassed);
```

---

## Action Bar & Text Priority

Minecraft has **only one action bar slot** per player. To prevent text from flickering due to multiple features trying to write to the action bar simultaneously, follow these layout rules:

1. **`ActionBarManager`** owns the high-frequency action bar display (cooldowns, icons, status symbols).
2. **`CoreLogic` classes** must not send raw action bar text packets while a main overlay loop is running. Instead, push status text to the standard chat feed (`isOverlay = false`) or force an immediate refresh via `ActionBarManager.forceUpdate(player);`.

```java
// Safe message delivery to regular text chat
player.sendSystemMessage(
        Component.literal("[Living Shadow] Dissolving in " + remainingSeconds + "s...")
                .withStyle(ChatFormatting.GRAY),
        false // Bypasses action bar, sends to normal scrolling chat feed
);
```

---

## Event Routing
The event system catches vanilla Minecraft / Fabric events and routes them immediately to the correct logic class.
* Event listeners must remain **thin and stateless**.
* Their only job is finding the relevant player or target entity and handing off execution.

```
Minecraft/Fabric Engine Context
            ↓
Specific Core Event Handler (Listener)
            ↓
Extract relevant ServerPlayer entity
            ↓
Delegate execution to targeted CoreLogic handler method
```

Do not write calculations, velocity modifications, particle calls, or capability formulas inside event listeners.

---

## Shared Utilities (`util/`)
Generic features used by multiple cores (like applying safe potion durations or formatting time counters) go into the `util/` package (e.g., `Effects.java`, `TickTimer.java`).

### File Placement Rules:
* If a helper method is only relevant to **one specific core type**, leave it as a private helper method inside that core's `CoreLogic` class.
* If a helper method can be **reused by multiple distinct cores**, move it out into the global `util/` package.

---

## Responsibility Matrix

| Package Component | Core Architectural Responsibility |
| :--- | :--- |
| `core/Core` | Holds base configurations, field parameters, and lifecycle methods. |
| `core/cores/` | Houses small declarations specifying core parameters and layout items. |
| `core/cores/logic/` | Complete ownership of gameplay variables, capabilities, visual tasks, and tracking states. |
| `core/CoreRegistry` | Handles central core lookup indexing and registry maps. |
| `command/` | Processes literal Brigadier node paths, verifies argument parsing inputs, and forwards parameters. |
| `event/` | Intercepts game events, extracts target references, and delegates tasks. |
| `playerdata/` | Manages runtime session mapping context definitions (`PlayerData`) and handles direct SQLite disk saves (`PlayerDataManager`). |
| `util/` | Hosts standalone mathematical utility tools, tick objects, and shared static helpers. |
| `manager/` | Manages structural layout layouts, UI formatting, and time tracking clocks. |

---

## Architecture Flow Execution Loop

```text
       ┌─────────────────────────────────┐
       │    Minecraft / Fabric Engine    │
       │      (Server Tick / Events)     │
       └────────────────┬────────────────┘
                        │
       ┌────────────────┴────────────────┐
       │       Core System Main Loop     │ (Handled via MainLoop scheduling task)
       └────────────────┬────────────────┘
                        │
         ┌──────────────┴──────────────┐
         ▼                             ▼
   [ EVERY TICK ]              [ ONLY EVERY 20 TICKS (1s) ]
         │                             │
         ▼                             ▼
 ┌───────────────┐             ┌───────────────┐
 │ Core.tick()   │             │ Core.passive()│ (Background evaluation loops)
 └───────┬───────┘             └───────┬───────┘
         │                             │
         ▼                             ▼
 ┌───────────────┐             ┌───────────────┐
 │  CoreLogic    │             │  CoreLogic    │
 │ (e.g. Tornado │             │ (e.g. Shadow  │
 │  Particles)   │             │  Light-Check) │
 └───────┬───────┘             └───────┬───────┘
         │                             │
         └──────────────┬──────────────┘
                        │
                        ▼
       ┌─────────────────────────────────┐
       │         PlayerData Lookup       │ (Fetches user preferences, 
       │  AstralCores.PLAYER_DATA.get()  │  cooldown indexes, and configurations)
       └────────────────┬────────────────┘
                        │
                        ▼
       ┌─────────────────────────────────┐
       │        ActionBarManager         │ (Consolidates active cooldowns and 
       │        (Unified Display)        │  resolves overlay text collisions)
       └────────────────┬────────────────┘
                        │
                        ▼
       ┌─────────────────────────────────┐
       │      Minecraft Client GUI       │ (Outputs stable screen elements 
       │    (Hotbar / Actionbar / Chat)  │  free of rendering conflicts)
       └─────────────────────────────────┘
```