/*
 * Copyright(c) 2020 Risto Lahtela (Rsl1122)
 *
 * The MIT License(MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files(the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions :
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.djrapitops.extension;

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.FormatType;
import com.djrapitops.plan.extension.Group;
import com.djrapitops.plan.extension.annotation.*;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;

import java.util.Optional;
import java.util.UUID;

/**
 * Extension for FactionsUUID (and forks).
 *
 * @author Vankka
 */
@PluginInfo(name = "FactionsUUID", iconName = "map", iconFamily = Family.SOLID, color = Color.GREEN)
public class FactionsUUIDExtension implements DataExtension {

    public FactionsUUIDExtension() {
    }

    private FPlayer getFPlayer(UUID playerUUID) {
        // This shouldn't ever fail
        // due to *questionable* design, it will generate a new entry if the uuid isn't found in memory
        // If this is accessed before FactionsUUID is loaded it will generate
        // a 'grumpy exception' that we cannot handle, but will still run fine
        return FPlayers.getInstance().getById(playerUUID.toString());
    }

    private Optional<Faction> getFaction(UUID playerUUID) {
        FPlayer fPlayer = getFPlayer(playerUUID);

        Faction faction = fPlayer.getFaction();
        if (!faction.isNormal()) return Optional.empty(); // ignore wilderness, safezone & warzone

        return Optional.of(faction);
    }

    private Faction getFaction(Group factionName) {
        return Factions.getInstance().getByTag(factionName.getGroupName());
    }

    @GroupProvider(text = "Faction", iconName = "flag", groupColor = Color.GREEN)
    public String[] faction(UUID playerUUID) {
        return getFaction(playerUUID)
                .map(faction -> new String[]{faction.getTag()})
                .orElse(new String[0]);
    }

    // Player data

    @BooleanProvider(
            text = "Has faction",
            description = "If the player has a faction",
            priority = 101,
            conditionName = "hasFaction",
            iconName = "check-square",
            iconFamily = Family.REGULAR,
            iconColor = Color.GREEN
    )
    public boolean hasFaction(UUID playerUUID) {
        return getFaction(playerUUID).isPresent();
    }

    @DoubleProvider(
            text = "Power",
            description = "How much power the player has",
            priority = 100,
            iconName = "bolt",
            iconColor = Color.GREEN
    )
    public double power(UUID playerUUID) {
        return getFPlayer(playerUUID)
                .getPower();
    }

    @DoubleProvider(
            text = "Max Power",
            description = "How much power the player can have",
            priority = 95,
            iconName = "bolt",
            iconColor = Color.GREEN
    )
    public double powerMax(UUID playerUUID) {
        return getFPlayer(playerUUID)
                .getPowerMax();
    }

    @StringProvider(
            text = "Role",
            description = "The role of the player in the faction",
            priority = 90,
            iconName = "user-tag",
            iconFamily = Family.SOLID,
            iconColor = Color.GREEN
    )
    @Conditional("hasFaction")
    public String role(UUID playerUUID) {
        return getFPlayer(playerUUID)
                .getRole().getTranslation().format();
    }

    // Faction data

    @BooleanProvider(
            text = "Economy enabled",
            description = "If FactionsUUID economy is enabled",
            priority = 101,
            conditionName = "economyEnabled",
            iconName = "wallet",
            iconColor = Color.GREEN,
            hidden = true
    )
    public boolean economyEnabled(Group factionName) {
        return Econ.shouldBeUsed();
    }

    @StringProvider(
            text = "Leader",
            description = "Who leads the faction",
            playerName = true,
            iconName = "user",
            iconColor = Color.GREEN
    )
    public String factionLeader(Group factionName) {
        Faction faction = getFaction(factionName);
        return faction.getFPlayers().stream().
                filter(fPlayer -> fPlayer.getRole() == Role.ADMIN)
                .findAny().map(FPlayer::getName)
                .orElseThrow(() -> new IllegalStateException("Faction doesn't have a leader"));
    }

    @StringProvider(
            text = "Description",
            description = "The description of the faction",
            priority = 100,
            iconName = "sticky-note",
            iconColor = Color.GREEN,
            iconFamily = Family.REGULAR
    )
    public String description(Group factionName) {
        Faction faction = getFaction(factionName);
        return faction.getDescription();
    }

    @DoubleProvider(
            text = "Power",
            description = "How much power the faction has",
            priority = 95,
            iconName = "bolt",
            iconColor = Color.GREEN
    )
    public double power(Group factionName) {
        Faction faction = getFaction(factionName);
        return faction.getPower();
    }

    @DoubleProvider(
            text = "Max Power",
            description = "How much power the faction can have",
            priority = 90,
            iconName = "bolt",
            iconColor = Color.GREEN
    )
    public double maxPower(Group factionName) {
        Faction faction = getFaction(factionName);
        return faction.getPowerMax();
    }

    @NumberProvider(
            text = "Created",
            description = "When the faction was created",
            priority = 85,
            iconName = "calendar",
            iconColor = Color.GREEN,
            iconFamily = Family.REGULAR,
            format = FormatType.DATE_YEAR
    )
    public long created(Group factionName) {
        Faction faction = getFaction(factionName);
        return faction.getFoundedDate();
    }

    @NumberProvider(
            text = "Member count",
            description = "How many members the faction has",
            priority = 80,
            iconName = "users",
            iconFamily = Family.SOLID,
            iconColor = Color.GREEN
    )
    public long memberCount(Group factionName) {
        Faction faction = getFaction(factionName);
        return faction.getSize();
    }

    @NumberProvider(
            text = "Allies",
            description = "How many allies the faction has",
            priority = 75,
            iconName = "user-friends",
            iconFamily = Family.SOLID,
            iconColor = Color.GREEN
    )
    public long allyCount(Group factionName) {
        Faction faction = getFaction(factionName);
        return faction.getRelationCount(Relation.ALLY);
    }

    @NumberProvider(
            text = "Truces",
            description = "How many truced factions the faction has",
            priority = 70,
            iconName = "handshake",
            iconFamily = Family.SOLID,
            iconColor = Color.GREEN
    )
    public long truceCount(Group factionName) {
        Faction faction = getFaction(factionName);
        return faction.getRelationCount(Relation.TRUCE);
    }

    @DoubleProvider(
            text = "Balance",
            description = "How much money the faction has",
            priority = 65,
            iconName = "money-bill-alt",
            iconFamily = Family.REGULAR,
            iconColor = Color.GREEN
    )
    @Conditional("economyEnabled")
    public double balance(Group factionName) {
        Faction faction = getFaction(factionName);
        return Econ.getBalance(faction.getAccountId());
    }
}
