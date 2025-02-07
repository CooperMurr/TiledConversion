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

        File file1 = new File(
                "C:\\Users\\Lenovo\\Documents\\GameDesign\\TiledConversion\\TiledToSimple\\src\\main\\resources\\birdtutorial.tmj");
        Scanner sc = new Scanner(file1);

        String levelNumber = "birdtutorial";

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

        JSONArray inputObjects = (JSONArray) ((JSONObject) layers.get(1)).get("objects");

        JSONArray objects = new JSONArray();

        finalOutput = addGameProperties(gameProperties, finalOutput);

        //objects = addWalls(walls, objects);

        objects = addInputObjects(objects, inputObjects);
        finalOutput.put("objects", objects);

        ObjectMapper m = new ObjectMapper();

        String s = m.writerWithDefaultPrettyPrinter().writeValueAsString(finalOutput);

        try (FileWriter file = new FileWriter(levelNumber + ".json")) {
            file.write(s);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static JSONObject addGameProperties(JSONArray gameProperties, JSONObject finalOutput) {
        String backgroundValue = "";
        boolean isTutorial = false;
        String tutorialType = "";
        for (int i = 0; i < gameProperties.size(); i++) {
            if (!(((JSONObject) gameProperties.get(i)).get("name").equals("background")) &&
                    !(((JSONObject) gameProperties.get(i)).get("name").equals("foreground"))) {
                if((((JSONObject) gameProperties.get(i)).get("name").equals("isTutorial")))
                {
                    isTutorial = (boolean)  (((JSONObject) gameProperties.get(i)).get("value"));
                }
                else if((((JSONObject) gameProperties.get(i)).get("name").equals("tutorialType")) && isTutorial)
                {
                    finalOutput.put(((JSONObject) gameProperties.get(i)).get("name"),
                            ((JSONObject) gameProperties.get(i)).get("value"));
                    tutorialType =(String) ((JSONObject) gameProperties.get(i)).get("value");
                }
                finalOutput.put(((JSONObject) gameProperties.get(i)).get("name"),
                        ((JSONObject) gameProperties.get(i)).get("value"));
            }
            else if ((((JSONObject) gameProperties.get(i)).get("name").equals("background")))
            {
                backgroundValue =  (String) ((JSONObject) gameProperties.get(i)).get("value");
            }
        }
        JSONObject bgNoSky = new JSONObject();
        bgNoSky.put("textureID", ConVal.BG_NO_GROUND_TEXTURE);
        bgNoSky.put("constant", ConVal.BACKGROUND_CONSTANT);

        JSONObject background = new JSONObject();
        background.put("textureID", backgroundValue);
        background.put("constant", ConVal.BACKGROUND_CONSTANT);

        JSONObject butlerImage = new JSONObject();
        JSONObject butlerText = new JSONObject();
        if(isTutorial)
        {
            switch(tutorialType)
            {
                case("move"):
                    butlerImage.put("textureID", ConVal.TUTORIAL_MOVE_TEXTURE);
                    butlerImage.put("constant", ConVal.BACKGROUND_CONSTANT);
                    butlerText.put("textureID", ConVal.MOVE_BUTLER);
                    butlerText.put("constant", ConVal.BACKGROUND_CONSTANT);
                    break;
                case("plant"):
                    butlerImage.put("textureID", ConVal.TUTORIAL_PLANT_TEXTURE);
                    butlerImage.put("constant", ConVal.BACKGROUND_CONSTANT);
                    butlerText.put("textureID", ConVal.PLANT_BUTLER);
                    butlerText.put("constant", ConVal.BACKGROUND_CONSTANT);
                    break;
                case("pickup"):
                    butlerImage.put("textureID", ConVal.TUTORIAL_PICKUP_TEXTURE);
                    butlerImage.put("constant", ConVal.BACKGROUND_CONSTANT);
                    butlerText.put("textureID", ConVal.PICKUP_BUTLER);
                    butlerText.put("constant", ConVal.BACKGROUND_CONSTANT);
                    break;
                case("owner"):
                    butlerImage.put("textureID", ConVal.TUTORIAL_OWNER_TEXTURE);
                    butlerImage.put("constant", ConVal.BACKGROUND_CONSTANT);
                    butlerText.put("textureID", ConVal.OWNER_BUTLER);
                    butlerText.put("constant", ConVal.BACKGROUND_CONSTANT);
                    break;
                case("breakable"):
                    butlerImage.put("textureID", ConVal.TUTORIAL_BREAKABLE_TEXTURE);
                    butlerImage.put("constant", ConVal.BACKGROUND_CONSTANT);
                    butlerText.put("textureID", ConVal.BREAKABLE_BUTLER);
                    butlerText.put("constant", ConVal.BACKGROUND_CONSTANT);
                    break;
                case("owl"):
                    butlerImage.put("textureID", ConVal.TUTORIAL_OWL);
                    butlerImage.put("constant", ConVal.BACKGROUND_CONSTANT);
                    butlerText.put("textureID", ConVal.OWL_BUTLER);
                    butlerText.put("constant", ConVal.BACKGROUND_CONSTANT);
                    break;
                case("ladder"):
                    butlerImage.put("textureID", ConVal.TUTORIAL_LADDER);
                    butlerImage.put("constant", ConVal.BACKGROUND_CONSTANT);
                    butlerText.put("textureID", ConVal.LADDER_BUTLER);
                    butlerText.put("constant", ConVal.BACKGROUND_CONSTANT);
                    break;
                case("fish"):
                    butlerImage.put("textureID", ConVal.TUTORIAL_FISH);
                    butlerImage.put("constant", ConVal.BACKGROUND_CONSTANT);
                    break;

            }
        }

        JSONArray tempArr = new JSONArray();
        tempArr.add(bgNoSky);
        tempArr.add(background);
        if(isTutorial)
        {
            tempArr.add(butlerImage);
            if(!(tutorialType.equals("fish")))
            {
                tempArr.add(butlerText);
            }
        }
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
            case "fish":
            case "artifact:birdcage":
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
                addPlantJuice(tempObj, inputObject);
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

    public static JSONObject addPlantJuice(JSONObject tempObj, JSONObject inputObject) {
        JSONArray properties = (JSONArray) inputObject.get("properties");

        for (int i = 0; i < properties.size(); i++) {
            String name = (String) (((JSONObject) properties.get(i)).get("name"));
            if (name.equals("juice")) {
                tempObj.put("juice", (((JSONObject) properties.get(i)).get("value")));
            }
            else if(name.equals("pot_type"))
            {
                tempObj.put("pot_type", (((JSONObject) properties.get(i)).get("value")));
            }
        }
        return tempObj;
    }

    public static JSONObject addXYWidthHeight(JSONObject tempObj, JSONObject inputObject, String type) {

        JSONArray tempPosArr = new JSONArray();
        JSONArray tempDimArr = new JSONArray();
        try {
            tempPosArr.add(Float.parseFloat(df.format(((Double) inputObject.get("x") / 32d))));
        }
        catch (Exception e)
        {
            tempPosArr.add(Float.parseFloat(df.format(((Long) inputObject.get("x") / 32))));
        }
        try {
            tempPosArr.add(Float.parseFloat(df.format((ConVal.GAME_HEIGHT - (Double) inputObject.get("y")) / 32d)));
        } catch (Exception e) {
            tempPosArr.add(Float.parseFloat(df.format(((ConVal.GAME_HEIGHT - (Long) inputObject.get("y")) / 32))));

        }
        if (type.equals("player")) {
            tempDimArr.add(ConVal.PLAYER_WIDTH);
            tempDimArr.add(ConVal.PLAYER_HEIGHT);
        } else {
            try{
                tempDimArr.add(Float.parseFloat(df.format((Long) inputObject.get("width") / 32d)));
            }
            catch(Exception e)
            {
                tempDimArr.add(Float.parseFloat(df.format(((Double) inputObject.get("width") / 32))));

            }
            try{
                tempDimArr.add(Float.parseFloat(df.format((Long) inputObject.get("height") / 32d)));

            }
            catch(Exception e)
            {
                tempDimArr.add(Float.parseFloat(df.format((Double) inputObject.get("height") / 32)));

            }
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
