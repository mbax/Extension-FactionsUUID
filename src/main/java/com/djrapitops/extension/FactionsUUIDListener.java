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

import com.djrapitops.plan.extension.Caller;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.event.*;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * Listener for FactionsUUID events.
 *
 * @author Vankka
 */
public class FactionsUUIDListener implements Listener {

    private final Caller caller;

    public FactionsUUIDListener(Caller caller) {
        this.caller = caller;
    }

    public void register(Caller caller) {
        Plugin plan = Bukkit.getPluginManager().getPlugin("Plan");
        Bukkit.getPluginManager().registerEvents(this, plan);
    }

    private void updateFPlayer(FPlayer fPlayer) {
        caller.updatePlayerData(UUID.fromString(fPlayer.getId()), fPlayer.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionCreate(FactionCreateEvent event) {
        updateFPlayer(event.getFPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionDisband(FactionDisbandEvent event) {
        event.getFaction().getFPlayers().forEach(this::updateFPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionAutoDisband(FactionAutoDisbandEvent event) {
        event.getFaction().getFPlayers().forEach(this::updateFPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFPlayerJoin(FPlayerJoinEvent event) {
        updateFPlayer(event.getfPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFPlayerLeave(FPlayerLeaveEvent event) {
        updateFPlayer(event.getfPlayer());
    }

}
