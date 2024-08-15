package com.demo.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jdk8.PackageVersion;

public class OptionFModule extends Module {
    protected boolean _cfgHandleAbsentAsNull = false;

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new OptionFSerializers());
        context.addDeserializers(new OptionFDeserializerBase());
        context.addTypeModifier(new OptionFTypeModifier());
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Deprecated
    public OptionFModule configureAbsentsAsNulls(boolean state) {
        _cfgHandleAbsentAsNull = state;
        return this;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public String getModuleName() {
        return "IOptionFModule";
    }
}
