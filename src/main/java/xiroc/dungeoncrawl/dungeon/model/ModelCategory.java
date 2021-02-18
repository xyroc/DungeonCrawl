package xiroc.dungeoncrawl.dungeon.model;

import com.google.common.collect.Lists;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum ModelCategory {

    STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5,
    NODE_DEAD_END, NODE_STRAIGHT, NODE_TURN, NODE_FORK, NODE_FULL,
    NORMAL_NODE, LARGE_NODE,
    CORRIDOR, CORRIDOR_LINKER, NODE_CONNECTOR,
    ENTRANCE,
    SIDE_ROOM, ROOM;

    public static final ModelCategory[] STAGES = {STAGE_1, STAGE_2, STAGE_3, STAGE_4,
            STAGE_5};

    public static final ModelCategory[] NODE_TYPES = {NODE_FULL, NODE_FORK, NODE_STRAIGHT,
            NODE_TURN, NODE_DEAD_END};

    public static final ModelCategory[] EMPTY = new ModelCategory[0];

    public final List<DungeonModel> members;

    ModelCategory() {
        members = Lists.newArrayList();
    }

    public void verifyModelPresence(int... stages) {
        for (int stage : stages) {
            int hashCode = 1;
            hashCode = 31 * hashCode + hashCode();
            hashCode = 31 * hashCode + getCategoryForStage(stage).hashCode();

            if (!DungeonModels.WEIGHTED_MODELS.containsKey(hashCode) || DungeonModels.WEIGHTED_MODELS.get(hashCode).size() == 0) {
                throw new RuntimeException("There is no present " + this + " model for stage " + (stage + 1));
            }
        }
    }

    public void verifyModelPresence(ModelCategory secondaryCategory, int... stages) {
        for (int stage : stages) {
            int hashCode = 1;
            hashCode = 31 * hashCode + hashCode();
            hashCode = 31 * hashCode + getCategoryForStage(stage).hashCode();
            hashCode = 31 * hashCode + secondaryCategory.hashCode();

            if (!DungeonModels.WEIGHTED_MODELS.containsKey(hashCode) || DungeonModels.WEIGHTED_MODELS.get(hashCode).size() == 0) {
                throw new RuntimeException("There is no present " + this + "(" + secondaryCategory + ")" + " model for stage " + (stage + 1));
            }
        }
    }

    public static void clear() {
        for (ModelCategory category : values()) {
            category.members.clear();
        }
    }

    public static WeightedRandom<DungeonModel> get(ModelCategory... categories) {
        int hash = Arrays.hashCode(categories);
        return DungeonModels.WEIGHTED_MODELS.get(hash);
    }

    public static DungeonModel[] getIntersection(HashMap<Integer, DungeonModel[]> map,
                                                 ModelCategory... categories) {
        int hash = Arrays.hashCode(categories);

        if (map.containsKey(hash)) {
            return map.get(hash);
        }

        List<DungeonModel> intersection = Lists.newArrayList();

        mainLoop:
        for (DungeonModel model : categories[0].members) {
            for (int i = 1; i < categories.length; i++) {
                if (!categories[i].members.contains(model)) {
                    continue mainLoop;
                }
            }
            intersection.add(model);
        }

        DungeonModel[] array = intersection.toArray(new DungeonModel[0]);
        map.put(hash, array);
        return array;
    }

    public static ModelCategory[] getSecondaryNodeCategories(ModelCategory primeCategory) {
        switch (primeCategory) {
            case NODE_DEAD_END:
                return new ModelCategory[]{NODE_TURN, NODE_STRAIGHT, NODE_FORK, NODE_FULL};
            case NODE_FORK:
                return new ModelCategory[]{NODE_FULL};
            case NODE_STRAIGHT:
            case NODE_TURN:
                return new ModelCategory[]{NODE_FULL, NODE_FORK};
            default:
                return EMPTY;
        }
    }

    public static ModelCategory getCategoryForStage(int stage) {
        if (stage < 0)
            return STAGE_1;
        if (stage > 4)
            return STAGE_5;
        return STAGES[stage];
    }

}
