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

package org.essencemc.essence.modules.vanish;

public class VanishData {

    private boolean chat;
    private boolean attack;
    private boolean damage;
    private boolean interact;
    private boolean pickup;
    private boolean target;

    public VanishData(boolean chat, boolean attack, boolean damage, boolean interact, boolean pickup, boolean target) {
        this.chat = chat;
        this.attack = attack;
        this.damage = damage;
        this.interact = interact;
        this.pickup = pickup;
        this.target = target;
    }


    public boolean canChat() {
        return chat;
    }

    public void setCanChat(boolean chat) {
        this.chat = chat;
    }


    public boolean canAttack() {
        return attack;
    }

    public void setCanAttack(boolean attack) {
        this.attack = attack;
    }


    public boolean canDamage() {
        return damage;
    }

    public void setCanDamage(boolean damage) {
        this.damage = damage;
    }


    public boolean canInteract() {
        return interact;
    }

    public void setCanInteract(boolean interact) {
        this.interact = interact;
    }


    public boolean canPickup() {
        return pickup;
    }

    public void setCanPickup(boolean pickup) {
        this.pickup = pickup;
    }

    public boolean canBeTargeted() {
        return target;
    }

    public void setCanBeTargeted(boolean target) {
        this.target = target;
    }
}
