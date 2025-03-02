package net.shirojr.whatmaterial.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.shirojr.whatmaterial.command.MaterialCommand;

public class WhatMaterialCommonEvents {
    static {
        CommandRegistrationCallback.EVENT.register(MaterialCommand::register);
    }

    public static void initialize() {
        // static initialisation
    }
}
