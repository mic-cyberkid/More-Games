# STORY.md — Echoes of Aetheria

## World Premise & Lore
Aetheria was once a paradise of floating continents, sustained by the Aether Core—a source of infinite energy that blurred the line between magic and technology. Five centuries ago, the Great Cataclysm shattered the Core. The continents plummeted, crashing into the seas or merging into a jagged, desolate landscape now known as the "Shattered Lands."

The leaked Aether didn't dissipate; it crystallized into "Echoes"—spectral distortions of people, places, and events from the Golden Age. These Echoes are both a source of power and a deadly contagion, slowly draining the life from the living to sustain their ghostly existence.

## Protagonist: Name, Background, Internal Conflict
**Name:** Kaelen
**Background:** A scavenger born in the Lower Wastes, Kaelen has spent his life picking through the "Rain of Iron"—debris that still occasionally falls from the remaining high-altitude fragments of Aetheria. He possesses an uncanny knack for repairing "Dead-Tech" (ancient Aether machinery).
**Internal Conflict:** Kaelen is haunted by the Echo of his younger sister, Lyra, who vanished during an Aether-pulse when he was a child. He blames his fascination with tech for his failure to protect her. He seeks the Core shards not to save the world, but in the desperate hope that a restored Core can pull his sister back from the spectral void.

## Chapter Synopses (7 chapters)
- **Chapter 1: The Rain of Iron** | Location: The Lower Wastes | Key NPCs: Old Man Silas | Core Mechanic: Basic Movement/Combat | Emotional Beat: Finding Lyra's pendant in a fallen ruin. | Player Choice: Give your only Aether-battery to Silas to save his leg, or keep it to power your Gauntlet.
- **Chapter 2: The Whispering Canopy** | Location: Whispering Woods | Key NPCs: Elara (Rebel Scout) | Core Mechanic: Stealth | Emotional Beat: Seeing Lyra's Echo for the first time, leading him to safety. | Player Choice: Spare an Empire patrol to avoid detection, or eliminate them to secure rebel supplies.
- **Chapter 3: The Iron Grip** | Location: Iron Fortress | Key NPCs: Commander Vax | Core Mechanic: Interaction/Puzzles | Emotional Beat: Discovery that the Empire is using Echoes as fuel for their war machines. | Player Choice: Overload the Echo-bins (freeing the ghosts but destroying the town's power) or steal the blueprints quietly.
- **Chapter 4: The Sea of Glass** | Location: The Spectral Sea | Key NPCs: Captain Maro | Core Mechanic: Navigation / World Transitions | Emotional Beat: Navigating through a "Memory Storm" that forces Kaelen to relive the day Lyra was lost. | Player Choice: Save Maro's crew from a spectral kraken, or prioritize the Aether-compass needed to find the Core.
- **Chapter 5: The Oracle's Silence** | Location: The Ancient Capitol | Key NPCs: The Oracle | Core Mechanic: Advanced ECS Combat | Emotional Beat: The Oracle reveals that Lyra's soul is the current "Anchor" preventing the world's total collapse. | Player Choice: Promise to find another way to save the world, or accept that Lyra must remain an Anchor.
- **Chapter 6: The Void's Edge** | Location: The Shattered Core | Key NPCs: None (Solo) | Core Mechanic: Boss Combat | Emotional Beat: Kaelen confronts the "Grief-Shadow"—a manifestation of his own guilt taking the form of a twisted Kaelen. | Player Choice: Sacrifice his Aether Gauntlet (his only weapon) to bridge the gap to the Core.
- **Chapter 7: Echoes of Tomorrow** | Location: The Reborn Core | Key NPCs: All remaining NPCs | Core Mechanic: Finale / Narrative Resolution | Emotional Beat: The final choice to mend the world or save a single soul. | Player Choice: Use the restored Core to heal the land (Good), stabilize the status quo (Neutral), or tear the veil to join Lyra (Secret).

## Major NPCs
1. **Old Man Silas:** A former engineer turned junk-merchant. *Motivation:* Wants to see one last sunrise over a healed world. *Arc:* From a cynical hoarder to a selfless mentor who sacrifices his shop to help Kaelen escape.
2. **Elara:** Leader of the Aether Reclaimers. *Motivation:* Total eradication of Empire influence and restoration of nature. *Arc:* Learns that the "old ways" weren't perfect and that technology isn't inherently evil.
3. **Commander Vax:** High Commander of the Empire of Iron. *Motivation:* Order and security through absolute control of Aether. *Arc:* Driven to madness by his own attempts to "tame" an Echo, becoming a boss encounter.
4. **Captain Maro:** A sky-sailor with a ship that can "swim" through Aether. *Motivation:* Gold and the freedom of the clouds. *Arc:* Becomes the emotional heart of the team, proving that loyalty is worth more than any treasure.
5. **The Oracle:** An ancient AI residing in a crystalline shell. *Motivation:* Preservation of Aetheria's history. *Arc:* Evolves from a cold, logical observer to an empathetic guide who regrets the errors of the past.
6. **Lyra (The Echo):** Kaelen's sister. *Motivation:* Guiding Kaelen to the truth. *Arc:* Finds her own agency within the spectral realm, eventually choosing her own fate.

## Faction Map
1. **The Empire of Iron:** A militaristic meritocracy. They view Aether as a resource to be harvested and the Echoes as industrial waste.
2. **The Aether Reclaimers:** A loose coalition of druids and rebels. They believe the Aether is the world's blood and should be returned to the earth.
3. **The Echo-Walkers:** A mysterious cult that inhabits the spectral zones. They worship the Echoes and seek to "ascend" by shedding their physical forms.

## Ending Variations
- **Good Ending:** (Conditions: Silas saved, Rebels supported, Lyra released) Kaelen restores the Core. The floating islands begin to rise, the Echoes find peace, and the land begins to bloom.
- **Neutral Ending:** (Conditions: Mixed choices) The Core is stabilized, preventing further collapse, but the Empire remains in power and the Echoes continue to haunt the fringes.
- **Bad Ending:** (Conditions: Selfish choices, high casualties) The Core is consumed by the Void. The world becomes a permanent spectral wasteland. Kaelen becomes the new Grief-Shadow.
- **Secret Ending:** (Conditions: Find all 12 Lyra Mementos, Lyra's Pendant fully charged) Kaelen uses the Core's power to transcend the physical realm, reuniting with Lyra and creating a new world within the Aether.

## Key Items & Their Story Significance
- **The Aether Gauntlet:** Kaelen's primary tool. It allows him to interact with Echoes and manipulate Dead-Tech. It's a symbol of his burden and his bridge to the past.
- **Lyra's Pendant:** A simple copper locket. It glows in the presence of Core shards and records the memories Kaelen finds throughout his journey.
- **The Alpha Shard:** The first piece of the Core. Its possession marks Kaelen as a target for both the Empire and the Echo-Walkers.

## Flag Reference
- `CH1_SILAS_SAVED` (Bool): Affects Silas's presence in later chapters.
- `CH3_EMPIRE_DATA` (Bool): Determines if the player has the blueprints for the final dungeon.
- `LYRA_MEMENTOS_COUNT` (Int): Tracks progress toward the Secret Ending.
- `WORLD_CORRUPTION_LEVEL` (Int): Increases with selfish choices; affects ending and enemy difficulty.
- `FACTION_REPUTATION_RECLAIMERS` (Int): Unlocks specific dialogue and side-quests.
