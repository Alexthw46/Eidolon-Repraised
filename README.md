# Eidolon Repraised

Work in progress fork-port of Eidolon to 1.19.2 and 1.20, trying to give a final wrap to unfinished content made by Elucent in the unfinished 1.18 branch.

Disclaimer

This mod original author is Elucent and all the assets as of 0.3.x versions are made by him. Most of the content made between the old 1.16 mod and this port was left unfinished after he lost interest in Minecraft. Many things might appear part of a much greater scheme because they were, but the goal of this port is not to follow the original plans nor to expand further.

This is also because i can't add significant stuff on my own without breaking the game and art style, so i'll limit myself to wrap up what was half-done to try keep it fun to play like the 1.16 version.

Feel free to reach out if you want to make an addon for this version of Eidolon, but wrapping up the wip stuff take priority on api support and datapack stuff.


Quick resume of added content with 0.3.0 :

- Silver Armor & tools, in addition to previous lead oregen.
- Rituals to recharge your wands.
- Ravens and Slugs. Tame a raven with beetroot seeds, breed slugs with pumpkin seeds. 
- Ethereal Hearts & equips: The soulbone amulet and bonelord armor give additional max health when an enemy is killed. The armor set is currently not craftable, but both are functional.
- Archangel Ring, Raven Cloak & Summoning Staff - extremely cool and powerful items, currently not craftable but fully functional.
 

Main changes related to this port :

- Changed chant visuals. They were planned to become a way more complex spellcasting puzzle with runes, but now they will become a very simple spell system to give prayers a value after the symbol for crafting.
- Research System: Planned as main component of the new progression, now it will be just a way to discover new spells using the known signs. Main WIP feature currently.
- Holy Path: Alternative to the dark lord, functional but will probably suffer from lack of themed items. Currently both paths can be taken at the same time.
- New plants and decoblocks, mainly cosmetic as their planned use was lost. 
 

DO NOT REPORT BUGS OR QUESTIONS TO THE ORIGINAL DISCORD OF EIDOLON, I warned thee, Gabb will have all the rights to feast on your heart.

Only requirements is Curios. No ports to older versions or modloaders will be done.

Every push to this repository is built and published to the [BlameJared](https://maven.blamejared.com) maven, to use
these builds in your project, simply add the following code in your build.gradle

```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    implementation ("com.alexthw.eidolon_repraised:eidolon-[MC_VERSION]:[VERSION]") {transitive=false}
}
```

Current version (1.21.1):
[![Maven](https://img.shields.io/maven-metadata/v?label=&color=C71A36&metadataUrl=https%3A%2F%2Fmaven.blamejared.com%2Fcom%2Falexthw%2Feidolon_repraised%2Feidolon_repraised-1.21.1%2Fmaven-metadata.xml&style=flat-square)](https://maven.blamejared.com/com/alexthw/eidolon_repraised/eidolon_repraised-1.21.1/)

(remove the v)