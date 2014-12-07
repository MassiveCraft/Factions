package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

import java.util.ArrayList;


public class CmdHelp extends FCommand {

    public CmdHelp() {
        super();
        this.aliases.add("help");
        this.aliases.add("h");
        this.aliases.add("?");

        //this.requiredArgs.add("");
        this.optionalArgs.put("page", "1");

        this.permission = Permission.HELP.node;
        this.disableOnLock = false;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (helpPages == null) {
            updateHelp();
        }

        int page = this.argAsInt(0, 1);

        sendMessage(p.txt.titleize("Factions Help (" + page + "/" + helpPages.size() + ")"));

        page -= 1;

        if (page < 0 || page >= helpPages.size()) {
            msg(TL.COMMAND_HELP_404.toString());
            return;
        }
        sendMessage(helpPages.get(page));
    }

    //----------------------------------------------//
    // Build the help pages
    //----------------------------------------------//

    public ArrayList<ArrayList<String>> helpPages;

    public void updateHelp() {
        helpPages = new ArrayList<ArrayList<String>>();
        ArrayList<String> pageLines;

        pageLines = new ArrayList<String>();
        pageLines.add(p.cmdBase.cmdHelp.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdList.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdShow.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdPower.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdJoin.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdLeave.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdChat.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdHome.getUseageTemplate(true));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_NEXTCREATE.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<String>();
        pageLines.add(p.cmdBase.cmdCreate.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdDescription.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdTag.getUseageTemplate(true));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_INVITATIONS.toString()));
        pageLines.add(p.cmdBase.cmdOpen.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdInvite.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdDeinvite.getUseageTemplate(true));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_HOME.toString()));
        pageLines.add(p.cmdBase.cmdSethome.getUseageTemplate(true));
        helpPages.add(pageLines);

        if (Econ.isSetup() && Conf.econEnabled && Conf.bankEnabled) {
            pageLines = new ArrayList<String>();
            pageLines.add("");
            pageLines.add(p.txt.parse(TL.COMMAND_HELP_BANK_1.toString()));
            pageLines.add(p.txt.parse(TL.COMMAND_HELP_BANK_2.toString()));
            pageLines.add(p.txt.parse(TL.COMMAND_HELP_BANK_3.toString()));
            pageLines.add("");
            pageLines.add(p.cmdBase.cmdMoney.getUseageTemplate(true));
            pageLines.add("");
            pageLines.add("");
            pageLines.add("");
            helpPages.add(pageLines);
        }

        pageLines = new ArrayList<String>();
        pageLines.add(p.cmdBase.cmdClaim.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdAutoClaim.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdUnclaim.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdUnclaimall.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdKick.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdMod.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdAdmin.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdTitle.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdSB.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdSeeChunk.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdStatus.getUseageTemplate(true));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_PLAYERTITLES.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<String>();
        pageLines.add(p.cmdBase.cmdMap.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdBoom.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdOwner.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdOwnerList.getUseageTemplate(true));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_OWNERSHIP_1.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_OWNERSHIP_2.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_OWNERSHIP_3.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<String>();
        pageLines.add(p.cmdBase.cmdDisband.getUseageTemplate(true));
        pageLines.add("");
        pageLines.add(p.cmdBase.cmdRelationAlly.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdRelationNeutral.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdRelationEnemy.getUseageTemplate(true));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_1.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_2.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_3.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_4.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<String>();
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_5.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_6.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_7.toString()));
        			pageLines.add(TL.COMMAND_HELP_RELATIONS_8.toString());
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_9.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_10.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_11.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_12.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_RELATIONS_13.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<String>();
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_PERMISSIONS_1.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_PERMISSIONS_2.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_PERMISSIONS_3.toString()));
        			pageLines.add(TL.COMMAND_HELP_PERMISSIONS_4.toString());
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_PERMISSIONS_5.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_PERMISSIONS_6.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_PERMISSIONS_7.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_PERMISSIONS_8.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_PERMISSIONS_9.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<String>();
        pageLines.add(TL.COMMAND_HELP_MOAR_1.toString());
        pageLines.add(p.cmdBase.cmdBypass.getUseageTemplate(true));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_ADMIN_1.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_ADMIN_2.toString()));
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_ADMIN_3.toString()));
        pageLines.add(p.cmdBase.cmdSafeunclaimall.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdWarunclaimall.getUseageTemplate(true));
        //TODO:TL
        pageLines.add(p.txt.parse("<i>Note: " + p.cmdBase.cmdUnclaim.getUseageTemplate(false) + P.p.txt.parse("<i>") + " works on safe/war zones as well."));
        pageLines.add(p.cmdBase.cmdPeaceful.getUseageTemplate(true));
        helpPages.add(pageLines);

        pageLines = new ArrayList<String>();
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_MOAR_2.toString()));
        pageLines.add(p.cmdBase.cmdChatSpy.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdPermanent.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdPermanentPower.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdPowerBoost.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdConfig.getUseageTemplate(true));
        helpPages.add(pageLines);

        pageLines = new ArrayList<String>();
        pageLines.add(p.txt.parse(TL.COMMAND_HELP_MOAR_3.toString()));
        pageLines.add(p.cmdBase.cmdLock.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdReload.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdSaveAll.getUseageTemplate(true));
        pageLines.add(p.cmdBase.cmdVersion.getUseageTemplate(true));
        helpPages.add(pageLines);
    }
}

