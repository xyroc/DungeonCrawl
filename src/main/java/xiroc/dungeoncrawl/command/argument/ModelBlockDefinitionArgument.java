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
import xiroc.dungeoncrawl.dungeon.model.ModelBlockDefinition;

import java.util.concurrent.CompletableFuture;

public class ModelBlockDefinitionArgument implements ArgumentType<ModelBlockDefinition> {

    public static final DynamicCommandExceptionType DEFINITION_NOT_FOUND = new DynamicCommandExceptionType((p_208663_0_) ->
            new TranslationTextComponent("Unknown block definition: {0}", p_208663_0_));

    public static ModelBlockDefinitionArgument modelBlockDefinitionArgument() {
        return new ModelBlockDefinitionArgument();
    }

    public static ModelBlockDefinition getDefinition(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, ModelBlockDefinition.class);
    }

    @Override
    public ModelBlockDefinition parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocation.read(reader);
        if (ModelBlockDefinition.DEFINITIONS.containsKey(resourceLocation)) {
            return ModelBlockDefinition.DEFINITIONS.get(resourceLocation);
        } else {
            throw DEFINITION_NOT_FOUND.create(resourceLocation);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestIterable(ModelBlockDefinition.getKeys(), builder);
    }

}
