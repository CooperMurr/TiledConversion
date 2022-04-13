import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
// import org.json.simple.*;


public class Main {
    private static final DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] xargs) throws IOException {

        File file1 = new File("C:\\Users\\murrj\\Documents\\Game Design\\GrowCrazyRevise\\tiledConversion\\TiledToSimple\\src\\main\\resources\\leveltest.tmj");
        Scanner sc = new Scanner(file1);

        String levelNumber = "1";

        sc.useDelimiter("\\Z");

        String parent = sc.next();

        Object topJson = JSONValue.parse(parent);
        JSONObject finalOutput = new JSONObject();
        finalOutput.put("defaults", addHighLevelDefaults());

        JSONObject topObject = (JSONObject) topJson;
        finalOutput.put("tileheight", topObject.get("tileheight"));
        finalOutput.put("tilewidth", topObject.get("tilewidth"));
        JSONArray layers = (JSONArray) topObject.get("layers");
        JSONArray gameProperties = (JSONArray) topObject.get("properties");

        JSONArray walls = (JSONArray) ((JSONObject) layers.get(0)).get("objects");

        JSONArray inputObjects = (JSONArray) ((JSONObject) layers.get(2)).get("objects");

        JSONArray objects = new JSONArray();

        finalOutput = addGameProperties(gameProperties, finalOutput);

        //objects = addWalls(walls, objects);

        objects = addInputObjects(objects, inputObjects);
        finalOutput.put("objects", objects);

        ObjectMapper m = new ObjectMapper();

        String s = m.writerWithDefaultPrettyPrinter().writeValueAsString(finalOutput);

        try (FileWriter file = new FileWriter("level" + levelNumber + ".json")) {
            file.write(s);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static JSONObject addGameProperties(JSONArray gameProperties, JSONObject finalOutput) {

        for (int i = 0; i < gameProperties.size(); i++) {
            if (!(((JSONObject) gameProperties.get(i)).get("name").equals("background")) &&
                    !(((JSONObject) gameProperties.get(i)).get("name").equals("foreground"))) {
                finalOutput.put(((JSONObject) gameProperties.get(i)).get("name"),
                        ((JSONObject) gameProperties.get(i)).get("value"));
            }
        }
        JSONObject background = new JSONObject();
        background.put("textureID", "background");
        background.put("constant", ConVal.BACKGROUND_CONSTANT);

        JSONObject foreground = new JSONObject();
        foreground.put("textureID", "foreground");
        foreground.put("constant", ConVal.FOREGROUND_CONSTANT);

        JSONArray tempArr = new JSONArray();
        tempArr.add(background);
        tempArr.add(foreground);
        finalOutput.put("background_layers", tempArr);
        return finalOutput;
    }

    public static JSONArray addInputObjects(JSONArray runningObjects, JSONArray inputObjects) {

        for (int i = 0; i < inputObjects.size(); i++) {
            JSONObject tempObj = new JSONObject();
            String objType = getType((JSONArray) (((JSONObject) inputObjects.get((i))).get("properties")));
            tempObj = addSpecificProp(tempObj, objType, (JSONObject) inputObjects.get(i));
            runningObjects.add(tempObj);
        }
        return runningObjects;
    }

    public static String getType(JSONArray properties) {
        for (int i = 0; i < properties.size(); i++) {
            String name = (String) (((JSONObject) properties.get(i)).get("name"));
            if (name.equals("type")) {
                return (String) (((JSONObject) properties.get(i)).get("value"));
            }
        }
        return "";
    }

    public static JSONObject addSpecificProp(JSONObject tempObj, String type, JSONObject inputObject) {

        switch (type) {
            case "shelf":
                tempObj.put("textureID", "shelf");
                tempObj.put("type", type);
                addMatchID(tempObj, inputObject);
                addXYWidthHeight(tempObj, inputObject, type);
                break;
            case "artifact":
                tempObj.put("type", type);
                addMatchID(tempObj, inputObject);
                addXYWidthHeight(tempObj, inputObject, type);
                JSONArray properties = (JSONArray) inputObject.get("properties");
                for (int i = 0; i < properties.size(); i++) {
                    String name = (String) (((JSONObject) properties.get(i)).get("name"));
                    if (name.equals("textureID")) {
                        tempObj.put("textureID", (String) (((JSONObject) properties.get(i)).get("value")));
                        break;
                    }
                }
                for (int i = 0; i < properties.size(); i++) {
                    String name = (String) (((JSONObject) properties.get(i)).get("name"));
                    if (name.equals("outlineTextureID")) {
                        tempObj.put("outlineTextureID", (String) (((JSONObject) properties.get(i)).get("value")));
                        break;
                    }
                }

                tempObj.put("isBreakable", (((JSONObject) properties.get(0)).get("value")));
                break;
            case "ladder":
                tempObj.put("type", type);
                tempObj.put("textureID", "ladder");
                tempObj.put("matchID", -1);
                addXYWidthHeight(tempObj, inputObject, type);
                break;
            case "player":
                tempObj.put("type", type);
                tempObj.put("textureID", "dude");
                tempObj.put("matchID", -1);
                tempObj.put("jumpTex", "jump");
                tempObj.put("holdTex", "holdstatic");
                addPlayerSpecificProp(tempObj);
                addXYWidthHeight(tempObj, inputObject, type);
                createCutter(tempObj);
                break;
            case "plant":
                tempObj.put("type", type);
                JSONArray tempArr = new JSONArray();
                for (String s : ConVal.PLANT_TEXTURE_IDS) {
                    tempArr.add(s);
                }
                tempObj.put("textureID", tempArr);
                tempObj.put("matchID", -1);
                addXYWidthHeight(tempObj, inputObject, type);


        }
        return tempObj;
    }

    public static JSONObject addPlayerSpecificProp(JSONObject tempObj) {
        JSONArray tempArr = new JSONArray();
        for (float f : ConVal.PLAYER_SHRINK) {
            tempArr.add(f);
        }
        tempObj.put("shrink", tempArr);
        tempObj.put("force", ConVal.FORCE);
        tempObj.put("damping", ConVal.DAMPING);
        tempObj.put("density", ConVal.PLAYER_DENSITY);
        tempObj.put("friction", ConVal.PLAYER_FRICTION);
        tempObj.put("maxspeed", ConVal.MAXSPEED);
        tempObj.put("jump_force", ConVal.JUMP_FORCE);
        tempObj.put("jump_cool", ConVal.JUMP_COOL);
        tempObj.put("dash_force", ConVal.DASH_FORCE);
        tempObj.put("dash_cool", ConVal.DASH_COOL);
        tempObj.put("speed_cool", ConVal.SPEED_COOL);
        tempObj.put("air_control", ConVal.AIR_CONTROL);
        tempObj.put("shot_cool", ConVal.SHOT_COOL);

        return tempObj;
    }

    public static JSONObject addMatchID(JSONObject tempObj, JSONObject inputObject) {
        JSONArray properties = (JSONArray) inputObject.get("properties");

        for (int i = 0; i < properties.size(); i++) {
            String name = (String) (((JSONObject) properties.get(i)).get("name"));
            if (name.equals("matchID")) {
                tempObj.put("matchID", (((JSONObject) properties.get(i)).get("value")));
                break;
            }
        }
        return tempObj;
    }

    public static JSONObject addXYWidthHeight(JSONObject tempObj, JSONObject inputObject, String type) {

        JSONArray tempPosArr = new JSONArray();
        JSONArray tempDimArr = new JSONArray();
        tempPosArr.add(Float.parseFloat(df.format(((Double) inputObject.get("x") / 32d))));
        try {
            tempPosArr.add(Float.parseFloat(df.format((ConVal.GAME_HEIGHT - (Double) inputObject.get("y")) / 32d)));
        } catch (Exception e) {
            tempPosArr.add(Float.parseFloat(df.format(((ConVal.GAME_HEIGHT - (Long) inputObject.get("y")) / 32))));

        }
        if (type.equals("player")) {
            tempDimArr.add(ConVal.PLAYER_WIDTH);
            tempDimArr.add(ConVal.PLAYER_HEIGHT);
        } else {
            tempDimArr.add(Float.parseFloat(df.format((Long) inputObject.get("width") / 32d)));
            tempDimArr.add(Float.parseFloat(df.format((Long) inputObject.get("height") / 32d)));
        }


        tempObj.put("dimension", tempDimArr);
        tempObj.put("position", tempPosArr);
        tempObj.put("orientation", (Long) inputObject.get("rotation"));
        tempObj.put("name", Long.toString((Long) inputObject.get("id")));
        return tempObj;
    }


    public static JSONArray addWalls(JSONArray walls, JSONArray runningObjects) {
        for (int i = 0; i < walls.size(); i++) {
            JSONObject tempObj = new JSONObject();
            JSONArray tempPosArr = new JSONArray();
            JSONArray tempDimArr = new JSONArray();
            tempPosArr.add(Float.parseFloat(df.format((Double) ((JSONObject) walls.get(i)).get("x") / 32d)));
            tempPosArr.add(Float.parseFloat(df.format(ConVal.GAME_HEIGHT -
                    ((Double) ((JSONObject) walls.get(i)).get("y") / 32d))));
            tempDimArr.add(Float.parseFloat(df.format((Long) ((JSONObject) walls.get(i)).get("width") / 32d)));
            tempDimArr.add(Float.parseFloat(df.format((Long) ((JSONObject) walls.get(i)).get("height") / 32d)));

            tempObj.put("dimension", tempDimArr);
            tempObj.put("position", tempPosArr);
            tempObj.put("name", Long.toString((Long) ((JSONObject) walls.get(i)).get("id")));
            tempObj.put("type", "wall");
            tempObj.put("textureID", "texture");
            runningObjects.add(tempObj);
        }

        return runningObjects;
    }

    public static JSONObject createCutter(JSONObject tempObj) {
        JSONObject cutter = new JSONObject();
        cutter.put("range", 1);
        cutter.put("shrink", 1);
        JSONArray cutter_anchor_scale = new JSONArray();
        JSONArray body_anchor_scale = new JSONArray();

        cutter_anchor_scale.add(-0.4f);
        cutter_anchor_scale.add(0);

        body_anchor_scale.add(-0.3f);
        body_anchor_scale.add(-0.1);

        cutter.put("cutter_anchor_scale", cutter_anchor_scale);
        cutter.put("body_anchor_scale", body_anchor_scale);

        tempObj.put("cutter", cutter);
        return tempObj;
    }

    public static JSONObject addHighLevelDefaults() {

        JSONObject defaults = new JSONObject();
        defaults.put("gravity", ConVal.GRAVITY);
        defaults.put("friction", ConVal.FRICTION);
        defaults.put("density", ConVal.DENSITY);
        defaults.put("restitution", ConVal.RESTITUTION);
        defaults.put("volume", ConVal.VOLUME);
        return defaults;
    }

}
