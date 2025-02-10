package xiroc.dungeoncrawl.datapack;

import net.minecraft.resources.ResourceLocation;

public record DatapackDirectory(String path) {
    public DatapackDirectory(String path) {
        this.path = path + '/';
    }

    public ResourceLocation resource(String namespace, String relativePath) {
        return new ResourceLocation(namespace, path + relativePath);
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
        return new ResourceLocation(resourceLocation.getNamespace(), path.substring(this.path.length(), path.length() - fileEnding.length()));
    }

    public DatapackDirectory subdirectory(String directory) {
        return new DatapackDirectory(this.path + directory);
    }
}
