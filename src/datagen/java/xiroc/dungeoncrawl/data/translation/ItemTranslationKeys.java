package xiroc.dungeoncrawl.data.translation;

public interface ItemTranslationKeys {
    String CASE_HARDENED_PICK = speciality("case_hardened_pick");
    String CASE_HARDENED_PICK_OF_PRECISION = speciality("case_hardened_pick_of_precision");
    String CASE_HARDENED_PICK_OF_PROSPECTING = speciality("case_hardened_pick_of_prospecting");

    String CRYSTAL_HEAD_AXE = speciality("crystal_head_axe");

    String CRYSTAL_PICK = speciality("crystal_pick");
    String CRYSTAL_PICK_OF_PRECISION = speciality("crystal_pick_of_precison");
    String CRYSTAL_PICK_OF_PROSPECTING = speciality("crystal_pick_of_prospecting");

    String ELDRITCH_BLADE = speciality("eldritch_blade");
    String ELDRITCH_BLADE_LORE = lore(ELDRITCH_BLADE);
    String ELDRITCH_BLADE_OF_PLUNDERING = speciality("eldritch_blade_of_plundering");
    String ELDRITCH_BLADE_OF_PLUNDERING_LORE = lore(ELDRITCH_BLADE_OF_PLUNDERING);
    String ELDRITCH_BLADE_OF_THE_INFERNO = speciality("eldritch_blade_of_the_inferno");
    String ELDRITCH_BLADE_OF_THE_INFERNO_LORE = lore(ELDRITCH_BLADE_OF_THE_INFERNO);

    String GRAVE_SPADE = speciality("grave_spade");

    String SOUL_SPADE = speciality("soul_spade");

    String TEMPERED_BLADE = speciality("tempered_blade");
    String TEMPERED_BLADE_LORE = lore(TEMPERED_BLADE);

    String WOODLAND_HATCHET = speciality("woodland_hatchet");


    private static String key(String path) {
        return "item.dungeoncrawl." + path;
    }

    private static String lore(String key) {
        return key + ".lore";
    }

    private static String speciality(String name) {
        return key("speciality." + name);
    }
}
