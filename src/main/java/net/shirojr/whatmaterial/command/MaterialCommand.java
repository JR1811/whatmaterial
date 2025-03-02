package net.shirojr.whatmaterial.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MaterialCommand {
    private static final SimpleCommandExceptionType NO_ITEM_SPECIFIED = new SimpleCommandExceptionType(Text.literal("No Item specified"));
    private static final SimpleCommandExceptionType EMPTY_ITEM = new SimpleCommandExceptionType(Text.literal("Item was empty"));


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("wm").requires(source -> source.hasPermissionLevel(2))
                .executes(MaterialCommand::print)
                .then(argument("itemStack", ItemStackArgumentType.itemStack(commandRegistryAccess))
                        .executes(MaterialCommand::printStack)));
        dispatcher.register(literal("what").requires(source -> source.hasPermissionLevel(2))
                .then(literal("material")
                        .executes(MaterialCommand::print)
                        .then(argument("itemStack", ItemStackArgumentType.itemStack(commandRegistryAccess))
                                .executes(MaterialCommand::printStack))));
        dispatcher.register(literal("info").requires(source -> source.hasPermissionLevel(2))
                .then(literal("material")
                        .executes(MaterialCommand::print)
                        .then(argument("itemStack", ItemStackArgumentType.itemStack(commandRegistryAccess))
                                .executes(MaterialCommand::printStack))));
    }

    private static int printStack(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Item item = ItemStackArgumentType.getItemStackArgument(context, "itemStack").getItem();
        if (item.equals(Items.AIR)) throw EMPTY_ITEM.create();
        sendFeedback(context, item);
        return Command.SINGLE_SUCCESS;
    }

    private static int print(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (!context.getSource().isExecutedByPlayer() || player == null) throw NO_ITEM_SPECIFIED.create();
        Item item = !player.getStackInHand(Hand.MAIN_HAND).isEmpty() ?
                player.getStackInHand(Hand.MAIN_HAND).getItem() :
                player.getStackInHand(Hand.OFF_HAND).getItem();
        if (item.equals(Items.AIR)) throw EMPTY_ITEM.create();
        sendFeedback(context, item);
        return Command.SINGLE_SUCCESS;
    }

    private static void sendFeedback(CommandContext<ServerCommandSource> context, Item item) {
        context.getSource().sendFeedback(() -> Text.literal("Item: " + getIdentifier(item)), true);
        context.getSource().sendFeedback(() -> Text.literal("ItemGroup: " + getItemGroup(item)), true);
        context.getSource().sendFeedback(() -> Text.literal("Material: " + getMaterial(item)), true);
    }

    private static String getIdentifier(Item item) {
        StringBuilder sb = new StringBuilder();
        Identifier identifier = Registries.ITEM.getId(item);
        if (identifier.equals(Registries.ITEM.getDefaultId())) sb.append("-");
        sb.append(identifier);
        return sb.toString();
    }

    private static String getItemGroup(Item item) {
        StringBuilder sb = new StringBuilder();
        List<ItemGroup> groups = Registries.ITEM_GROUP.stream().filter(entry -> {
            if (entry.getType().equals(ItemGroup.Type.SEARCH)) return false;
            return entry.contains(item.getDefaultStack());
        }).toList();
        if (groups.isEmpty()) {
            sb.append("-");
        } else {
            for (int i = 0; i < groups.size(); i++) {
                ItemGroup entry = groups.get(i);
                sb.append(entry.getDisplayName().getString());
                if (i != groups.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    private static String getMaterial(Item item) {
        StringBuilder sb = new StringBuilder();

        if (item instanceof ToolItem toolItem) {
            sb.append(toolItem.getMaterial().toString());
        } else if (item instanceof ArmorItem armorItem) {
            Identifier identifier = Registries.ARMOR_MATERIAL.getId(armorItem.getMaterial().value());
            if (identifier == null) sb.append("-");
            else sb.append(identifier);
        } else {
            sb.append("-");
        }
        return sb.toString();
    }
}
