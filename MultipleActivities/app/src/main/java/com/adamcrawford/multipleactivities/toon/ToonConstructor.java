package com.adamcrawford.multipleactivities.toon;

/**
 * Author:  Adam Crawford
 * Project: Multiple Activities
 * Package: com.adamcrawford.multipleactivities.toon
 * File:    ToonConstructor
 * Purpose: TODO Minimum 2 sentence description
 */
import org.json.JSONException;
import org.json.JSONObject;

public class ToonConstructor
{
    public String toonName;
    public String toonIcon;
    public String toonLevel;
    public String tnClass;


    public enum CharClass {
        NONE(""),
        WARRIOR("Warrior"),
        PALADIN("Paladin"),
        HUNTER("Hunter"),
        ROGUE("Rogue"),
        PRIEST("Priest"),
        DEATHKNIGHT("Death Knight"),
        SHAMAN("Shaman"),
        MAGE("Mage"),
        WARLOCK("Warlock"),
        MONK("Monk"),
        DRUID("Druid");

        private final String toonClass;

        CharClass(String tClass) {
            toonClass = tClass;
        }
        public String getToonClass() {
            return toonClass;
        }

    }

    public ToonConstructor (JSONObject object) {

        try {
            this.toonName = object.getJSONObject("character").getString("name");
            this.toonLevel = object.getJSONObject("character").getString("level");
            this.toonIcon = object.getJSONObject("character").getString("thumbnail");
            this.tnClass = CharClass.values()[object.getJSONObject("character").getInt("class")].getToonClass();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}