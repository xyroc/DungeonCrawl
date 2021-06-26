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
import xiroc.dungeoncrawl.theme.Theme;

import java.util.concurrent.CompletableFuture;

public class SecondaryThemeArgument implements ArgumentType<Theme.SecondaryTheme> {

    public static final DynamicCommandExceptionType THEME_NOT_FOUND = new DynamicCommandExceptionType((p_208663_0_) ->
            new TranslationTextComponent("Unknown secondary theme: {0}", p_208663_0_));

    public static SecondaryThemeArgument secondaryTheme() {
        return new SecondaryThemeArgument();
    }

    public static Theme.SecondaryTheme getSecondaryTheme(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, Theme.SecondaryTheme.class);
    }

    @Override
    public Theme.SecondaryTheme parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocation.read(reader);
        if (Theme.KEY_TO_SECONDARY_THEME.containsKey(resourceLocation)) {
            return Theme.KEY_TO_SECONDARY_THEME.get(resourceLocation);
        } else {
            throw THEME_NOT_FOUND.create(resourceLocation);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestIterable(Theme.getSecondaryThemeKeys(), builder);
    }

}
