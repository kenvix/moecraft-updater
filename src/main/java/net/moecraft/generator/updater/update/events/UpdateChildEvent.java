//--------------------------------------------------
// Interface UpdateChildEvent
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.events;

public abstract class UpdateChildEvent implements Cloneable {
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
