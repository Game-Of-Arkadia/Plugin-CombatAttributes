# How it works ?

## When are the items updated ?

> The items are not updated all the time. They are updated when :
- a player run the **/fix-attributes** command with the item in the hand.
- an **enchantment book** is applied in an **anvil**.
- an **enchantment** is applied in an **enchantment table**.

### Does this apply to all items ?

> **NO. It only works for custom items, but (see below)**  

⚠️ **YOU MUST :**
- **hide** the default attributes **by setting flag "Hide Attributes"**
- add **"customItem"** to the item NBT Tags.

> If these 2 conditions are met, the item will be updated 
> during previously announced events.   
> You can test the presence of these conditions 
> with the **/fix-attributes command**.