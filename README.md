FactionsUUID
====================
<rant>
I'd appreciate it if you could pay for the [resource](http://www.spigotmc.org/resources/factionsuuid.1035/) if your server makes money. If you only run your server for your kids or some friends and don't make any money, then I don't want to stop you from doing so by forcing you to buy a $10 plugin. If that's the case, then just send me a message and we can work something out.

The repo is open because of that and because I still hope to see people contributing upstream :)
</rant>

This plugin will allow the players on the server to create factions/guilds. The factions can claim territory that will be protected from non-members. Factions can forge alliances and declare themselves enemies with others. Land may be taken from other factions through war.

The goals of this plugin:

 * The players should be able to take care of anti-griefing themselves.
 * Inspire politics and intrigues on your server.
 * Guilding and team spirit! :)
 * Auto convert old 1.6.9.x versions to save with UUIDs.

Usage
---------
<b>Read the full userguide here: [http://massivecraft.com/dev/factions](http://massivecraft.com/dev/factions)</b>

The chat console command is:

 * `/f`

This command has subcommands like:

* `/f create MyFactionName`
* `/f invite MyFriendsName`
* `/f claim`
* `/f map`
* ... etc

You may also read the documentation ingame as the plugin ships with an ingame help manual. Read the help pages like this:

* `/f help 1`
* `/f help 2`
* `/f help 3`

Note that you may optionally skip the slash and just write

* `f`

Installing
----------
1. Download the latest release: [http://ci.drtshock.net/job/FactionsUUID/](http://ci.drtshock.net/job/FactionsUUID/)<br>
1. Put Factions.jar in the plugins folder.

A default config file will be created on the first run.

License
----------
This project has a LGPL license just like the Bukkit project.<br>
This project uses [GSON](http://code.google.com/p/google-gson/) which has a [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0 ).

