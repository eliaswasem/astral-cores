# Commands

## For Players

### Actionbar

The **/actionbar** command lets you switch between different actionbar displays:

```text
/actionbar
├── text
└── icon
```

- **text**
  - Displays your equipped core's name and its cooldown.
- **icon**
  - Displays your equipped core as a small icon, along with its cooldown.

### Activate

```text
/activate
```

- Activates the ability of your currently equipped core.

### Trust

```text
/trust <player>
```

- Trusts a player.
- Trusted players aren't affected by your active or passive abilities.

### Untrust

```text
/untrust <player>
```

- Untrusts a player.
- Untrusted players are affected by your active or passive abilities.

### Withdraw

```text
/withdraw
```

- Withdraws your currently equipped core from your actionbar and puts it back into your inventory.

---

## For Server Owners

### Astralcores

```text
/astralcores
├── debug
│   └── resetCooldowns
└── place
    └── altar <pos>
```

- **debug resetCooldowns**
  - Resets all core cooldowns.
- **place altar <pos>**
  - Places an altar at the specified position.
  - An operator can put a core inside the altar if it gets destroyed, e.g. by falling into the void.

### Core

```text
/core
├── give <player>
├── set <player>
├── clear <player>
└── clearInv <player>
```

- **give**
  - Gives a core to a player.
- **set**
  - Sets a player's core in their actionbar.
- **clear**
  - Clears a player's core from their actionbar.
- **clearInv**
  - Clears a player's core from their inventory.
