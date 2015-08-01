package com.phuotstorydaogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class PhuotStoryDAOGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.m2team.phuotstory.model");
        Entity story = schema.addEntity("Story");
        story.addIdProperty();
        story.addStringProperty("title");
        story.addStringProperty("description");
        story.addStringProperty("shortTitleLocation");
        story.addStringProperty("fullLocationList");
        story.addStringProperty("photoUri");
        story.addStringProperty("friends");
        story.addStringProperty("feeling");
        story.addLongProperty("travelTime");
        story.addLongProperty("createdTime");
        story.addDoubleProperty("distance");//km

        Entity level = schema.addEntity("Level");
        level.addIdProperty();
        level.addStringProperty("name");
        level.addLongProperty("levelRoad");
        level.addLongProperty("levelStory");

        new DaoGenerator().generateAll(schema, "app/src/main/java/");
    }
}
