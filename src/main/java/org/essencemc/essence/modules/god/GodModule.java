/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Essence <http://essencemc.org>
 * Copyright (c) 2015 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.essencemc.essence.modules.god;


import org.essencemc.essence.Essence;
import org.essencemc.essencecore.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class GodModule extends Module {

    private List<String> godPlayers = new ArrayList<>();

    public GodModule(String name){
        super(Essence.inst(), name);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    protected void onReload() {

    }

    /**
     * Get the god list of this session.
     * @return Returns god list.
     */
    public List<String> getGodList(){
        return godPlayers;
    }

    public boolean isGod(String playerName){
        return godPlayers.contains(playerName);
    }

    public void toggleGod(String playerName){
        if(godPlayers.contains(playerName)){
            godPlayers.remove(playerName);
            return;
        }
        godPlayers.add(playerName);
    }

    public void setGod(String playerName){
        if(godPlayers.contains(playerName)){
            return;
        }
        godPlayers.add(playerName);
    }
}
