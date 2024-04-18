<img src="banner.png" alt="CombatAttributes Banner">

**Fix combat items attributes : solve a logical combat design problem that Mojang refuses to tackle!**
> Languages available :
> - **English** (default) 🇬🇧 🇺🇸
> - **French** 🇫🇷
> - **Custom** language into **custom file**

## Features

### 🚧 Fix
- **Lore** (damage points, attack speed)  
- **Attack damage** and **attack speed** on **weapons with attributes**  
- **Armor**, **armor toughness** and **knockback resistance** on **armor pieces with attributes**

### 🚫 Remove
- **Knockback resistance** on **netherite armors**

**[ExecutableItems](https://www.spigotmc.org/resources/custom-items-plugin-executable-items.77578/)** support _(soft dependency)_

## How it works

The plugin uses NMS

### When are the items updated ?

> The items are not updated all the time, but they are updated automatically, when :
- a player changes its **game language**
- a player run the **/fix-attributes** command with the item in the hand.
- an **enchantment book** is applied in an **anvil**.
- an **enchantment** is applied in an **enchantment table**.
- (ExecutableItems dependency) the item is given by **a moderator who used /ei give** command or the **EI Editor menu**

### Does this apply to all items ?

> **NO. It only works for CUSTOM ITEMS.**  
> ⚠️ A CUSTOM ITEM is an item which **has attribute modifiers** _(for example +2 speed)_

> If this condition is met, **the item will be updated
> during previously announced events**.   
> You can test the presence of this condition
> with the **/fix-attributes command**.

## Commands

- **/fix-attributes**  
Fix items attributes and update lore (not mandatory).

## Permissions

**Nothing.**

## Dependencies
- **MCTranslationLib** by KeyKatyu