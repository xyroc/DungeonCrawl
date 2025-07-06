package xiroc.dungeoncrawl.datapack;

import net.minecraft.resources.ResourceLocation;

public record DatapackDirectory(String path) {
    public ResourceLocation resource(String namespace, String relativePath) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path + '/' + relativePath);
    }

    /**
     * Creates a key for a given resource location. Removes the base directory, the following slash and the file ending.
     *
     * @param resourceLocation the initial resource location.
     * @param fileEnding       the file ending to remove at the end of the path
     * @return the key
     */
    public ResourceLocation key(ResourceLocation resourceLocation, String fileEnding) {
        String path = resourceLocation.getPath();
        return ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), path.substring(this.path.length() + 1, path.length() - fileEnding.length()));
    }

    public DatapackDirectory subdirectory(String directory) {
        return new DatapackDirectory(this.path + '/' + directory);
    }
}
