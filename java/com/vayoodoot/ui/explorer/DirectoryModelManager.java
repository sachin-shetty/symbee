package com.vayoodoot.ui.explorer;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 28, 2007
 * Time: 5:48:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryModelManager {

    private static ArrayList modelList = new ArrayList();

    public static void addModel(DirectoryModel model)  {
        // Check if the model exists
        DirectoryModel model1 = getModel(model.getTargetUserName(), model.getDirectoryName());
        if (model1 != null)
            modelList.remove(model1);
        modelList.add(model);
    }

    public static DirectoryModel getModel(String targetUserName, String directoryName)  {

        for (int i=0; i<modelList.size(); i++) {
            DirectoryModel model = (DirectoryModel)modelList.get(i);
            if (model.getDirectoryName().equals(directoryName)
                && model.getTargetUserName().equals(targetUserName)) {
                return model;
            }
        }

        return null;
    }


}
