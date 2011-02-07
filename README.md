Factions - Guilding and user-controlled antigrief plugin for Minecraft
====================
This plugin will allow the players on the server to create factions/guilds. The factions can claim territory that will be protected from non-members. Factions can forge alliances and declare themselves enemies with others. Land may be taken from other factions through war.

The goals of this plugin:

 * The players should be able to take care of anti-griefing themselves.
 * Inspire politics and intrigues on your server.
 * Guilding and team spirit! :)

Usage
---------
Read the full userguide here: [http://mcteam.org/factions](http://mcteam.org/factions)

The chat console command is:

 * `/f`

This command has subcommands like:

* `/f create My faction name`
* `/f invite my friends name`
* `/f claim`
* `/f map`
* ... etc

You may also read the documentation ingame as the plugin ships with an ingame help manual. Read the help pages like this:

* `/f help 1`
* `/f help 2`
* `/f help 1`

Note that you may optionally skip the slash and just write

* `f`

Installing
---------
1. Download the latest release: [https://github.com/oloflarsson/Factions/tree/master/releases](https://github.com/oloflarsson/Factions/tree/master/releases)
1. Put the included gson.jar here `your_minecraft_server/gson.jar`
1. Put the included Factions.jar here `your_minecraft_server/plugins/Factions.jar`

Compiling
---------
<b>You will need the Bukkit library to compile (Bukkit.jar)</b><br/>
You can download it here: [http://bamboo.lukegb.com/browse/BUKKIT-BUKKITMAIN/](http://bamboo.lukegb.com/browse/BUKKIT-BUKKITMAIN/)

<b>You will also need Google GSON in your build path.</b><br/>
You can download it here: [http://code.google.com/p/google-gson/](http://code.google.com/p/google-gson/)<br/>
<b>OR</b> you can use the file in this repo `/packaging/your_minecraft_server/gson.jar`<br/>
However you won't get javadocs and source that way.

You will need to include the MANIFEST.MF and plugin.yml in your jar. All bukkit plugins need a plugin.yml and the MANIFEST.MF is needed for the plugin to find the gson.jar (wich should be in the same folder as the main server jar when running the server).