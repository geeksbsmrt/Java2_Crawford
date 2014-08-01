package com.adamcrawford.actionbar.toon;

/**
 * Author:  Adam Crawford
 * Project: Multiple Activities
 * Package: com.adamcrawford.actionbar.toon
 * File:    ToonConstructor
 * Purpose: Builds toon objects based on data passed in.
 */

import com.adamcrawford.actionbar.MainActivity;
import com.adamcrawford.actionbar.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ToonConstructor implements Serializable {
    public String toonName;
    public String toonIcon;
    public String toonLevel;
    public String tnClass;
    public String tnColor;
    public String tnRace;
    public String tnGender;
    public String toonRole;
    public String toonSpec;
    public String toonRating;


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

        public String getToonColor() {
            return toonColor;
        }

    }

    public enum CharRace {
        NONE(""),
        HUMAN("Human"),
        ORC("Orc"),
        DWARF("Dwarf"),
        NIGHTELF("Night Elf"),
        UNDEAD("Undead"),
        TAUREN("Tauren"),
        GNOME("Gnome"),
        TROLL("Troll"),
        GOBLIN("Goblin"),
        BLOODELF("Blood Elf"),
        DRAENEI("Draenei"),
        TWELVE(""),
        THIRTEEN(""),
        FOURTEEN(""),
        FIFTEEN(""),
        SIXTEEN(""),
        SEVENTEEN(""),
        EIGHTEEN(""),
        NINETEEN(""),
        TWENTY(""),
        TWENTYONE(""),
        WORGEN("Worgen"),
        TWENTYTHREE(""),
        PANDARENN("Pandaren (Neutral)"),
        PANDARENA("Pandaren (Alliance)"),
        PANDARENH("Pandaren (Horde)");

        private final String toonRace;

        CharRace(String tRace) {
            toonRace = tRace;
        }

        public String getToonRace() {
            return toonRace;
        }
    }

    public enum CharGender {
        MALE("Male"),
        FEMALE("Female");

        private final String toonGender;

        CharGender(String cGender) {
            toonGender = cGender;
        }

        public String getToonGender() {
            return toonGender;
        }
    }

    public ToonConstructor(JSONObject object) {

        try {
            this.toonName = object.getJSONObject("character").getString("name");
            this.toonLevel = object.getJSONObject("character").getString("level");
            this.toonIcon = object.getJSONObject("character").getString("thumbnail");
            this.tnClass = CharClass.values()[object.getJSONObject("character").getInt("class")].getToonClass();
            this.tnColor = CharClass.values()[object.getJSONObject("character").getInt("class")].getToonColor();
            this.tnRace = CharRace.values()[object.getJSONObject("character").getInt("race")].getToonRace();
            this.tnGender = CharGender.values()[object.getJSONObject("character").getInt("gender")].getToonGender();
            if (object.getJSONObject("character").has("spec")) {
                this.toonRole = object.getJSONObject("character").getJSONObject("spec").getString("role");
                this.toonSpec = object.getJSONObject("character").getJSONObject("spec").getString("name");
            } else {
                this.toonRole = MainActivity.getContext().getString(R.string.none);
                this.toonSpec = MainActivity.getContext().getString(R.string.none);
            }
            if (object.getJSONObject("character").has("rating")) {
                this.toonRating = object.getJSONObject("character").getString("rating");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}