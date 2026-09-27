package xiroc.dungeoncrawl.dungeon.blueprint.assembly.type;

import xiroc.dungeoncrawl.dungeon.blueprint.EntranceContainer;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.config.AssemblyConfig;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.context.AssemblyContext;

/**
 * Handles custom logic related to blueprint entrance parts.
 * <p></p>
 * The Blueprint assembly ecosystem consists of the following parts:
 * <ul>
 *     <li>{@link AssemblyConfig}: Static configuration which supplies an {@link AssemblyTypeBuilder} to construct an Assembly Type for a specific blueprint.</li>
 *     <li>{@link AssemblyType}: Constant of a blueprint. Supplies an Assembly Context whenever the blueprint is placed in the world to handle the instance-specific context.</li>
 *     <li>{@link AssemblyContext}: Handles the context of a blueprint instance in the world and uses it to perform the final assembly step.</li>
 * </ul>
 */
public interface AssemblyType {
    /**
     * Instantiates a new assembly context to be used for a specific blueprint placement in a dungeon.
     *
     * @return the context.
     */
    AssemblyContext createContext();

    /**
     * Provides all entrances of the blueprint that this assembly type belongs to.
     *
     * @return The immutable entrance container with all entrances.
     */
    EntranceContainer entrances();
}
