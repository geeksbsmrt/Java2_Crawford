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
    public String tnColor;


    public enum CharClass {
        NONE("", ""),
        WARRIOR("Warrior", "#C79C6E"),
        PALADIN("Paladin", "#F58CBA"),
        HUNTER("Hunter", "#ABD473"),
        ROGUE("Rogue", "#FFF569"),
        PRIEST("Priest", "#FFFFFF"),
        DEATHKNIGHT("Death Knight", "#C41F3B"),
        SHAMAN("Shaman", "#0070DE"),
        MAGE("Mage", "#69CCF0"),
        WARLOCK("Warlock", "#9482C9"),
        MONK("Monk", "#00FF96"),
        DRUID("Druid", "#FF7D0A");

        private final String toonClass;
        private final String toonColor;

        CharClass(String tClass, String tColor) {
            toonClass = tClass;
            toonColor = tColor;
        }

        public String getToonClass() {
            return toonClass;
        }

        public String getToonColor () {
            return toonColor;
        }

    }

    public ToonConstructor (JSONObject object) {

        try {
            this.toonName = object.getJSONObject("character").getString("name");
            this.toonLevel = object.getJSONObject("character").getString("level");
            this.toonIcon = object.getJSONObject("character").getString("thumbnail");
            this.tnClass = CharClass.values()[object.getJSONObject("character").getInt("class")].getToonClass();
            this.tnColor = CharClass.values()[object.getJSONObject("character").getInt("class")].getToonColor();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}