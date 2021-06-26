/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;

import java.util.concurrent.CompletableFuture;

public class DungeonModelArgument implements ArgumentType<DungeonModel> {

    public static final DynamicCommandExceptionType MODEL_NOT_FOUND = new DynamicCommandExceptionType((p_208663_0_) ->
            new TranslationTextComponent("Unknown model: {0}", p_208663_0_));

    public static DungeonModelArgument modelArgument() {
        return new DungeonModelArgument();
    }

    public static DungeonModel getModel(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, DungeonModel.class);
    }

    @Override
    public DungeonModel parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocation.read(reader);
        if (DungeonModels.KEY_TO_MODEL.containsKey(resourceLocation)) {
            return DungeonModels.KEY_TO_MODEL.get(resourceLocation);
        } else {
            throw MODEL_NOT_FOUND.create(resourceLocation);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestIterable(DungeonModels.getKeys(), builder);
    }

}
