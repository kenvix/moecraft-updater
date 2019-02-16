//--------------------------------------------------
// Class UpdateEvent
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.updater.update.events.UpdateChildEvent;

public class UpdateEvent implements Cloneable {

    private UpdateStage updateStage;
    private String description;
    private UpdateChildEvent childEvent;
    private Object result;
    private Exception exception;
    private SimpleResultType simpleResultType = SimpleResultType.Unknown;

    public UpdateEvent(UpdateStage updateStage) {
        this.updateStage = updateStage;
    }

    public UpdateEvent(UpdateStage updateStage, String description) {
        this.updateStage = updateStage;
        this.description = description;
    }

    public UpdateEvent(UpdateStage updateStage, String description, UpdateChildEvent childEvent) {
        this.updateStage = updateStage;
        this.description = description;
        this.childEvent = childEvent;
        simpleResultType = SimpleResultType.Customed;
    }

    public UpdateEvent(UpdateStage updateStage, String description, SimpleResultType simpleResultType) {
        this.updateStage = updateStage;
        this.description = description;
        this.simpleResultType = simpleResultType;
    }

    public UpdateEvent(UpdateStage updateStage, String description, UpdateChildEvent childEvent, Exception exception) {
        this.updateStage = updateStage;
        this.description = description;
        this.childEvent = childEvent;
        this.exception = exception;
    }

    public UpdateEvent(UpdateStage updateStage, Object result, SimpleResultType simpleResultType) {
        this.updateStage = updateStage;
        this.result = result;
        this.simpleResultType = simpleResultType;
    }

    public UpdateEvent(UpdateStage updateStage, Object result) {
        this.updateStage = updateStage;
        this.result = result;
        this.simpleResultType = SimpleResultType.Success;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public UpdateStage getUpdateStage() {
        return updateStage;
    }

    UpdateEvent setUpdateStage(UpdateStage updateStage) {
        this.updateStage = updateStage;
        return this;
    }

    public String getDescription() {
        return description;
    }

    UpdateEvent setDescription(String description) {
        this.description = description;
        return this;
    }

    public UpdateChildEvent getChildEvent() {
        return childEvent;
    }

    UpdateEvent setChildEvent(UpdateChildEvent childEvent) {
        this.childEvent = childEvent;
        return this;
    }

    public Object getOriginalResult() {
        return result;
    }

    public String getStringResult() {
        return (String) result;
    }

    public MetaResult getMetaResult() {
        return (MetaResult) result;
    }

    UpdateEvent setResult(Object result) {
        this.result = result;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public SimpleResultType getSimpleResultType() {
        return simpleResultType;
    }

    UpdateEvent setSimpleResultType(SimpleResultType simpleResultType) {
        this.simpleResultType = simpleResultType;
        return this;
    }

    public Exception getException() {
        return exception;
    }

    UpdateEvent setException(Exception exception) {
        this.exception = exception;
        return this;
    }
}
