package ru.FedorILyaCO.MLTests.application.preferences;

import java.util.List;
import java.util.prefs.Preferences;


public class UserPreferences
{
    private Preferences userPrefs = Preferences.userRoot().node("testApplication");
    private List<String> chosenPyFiles = null;

    public UserPreferences()
    {

    }
    public void setPathToPyFiles(String path){
        userPrefs.node("pages").node("ChoosingPathPage")
                .put("pathToPyFiles", path);
    }

    public String getPathToPyFiles(){
        return  userPrefs.node("pages").node("ChoosingPathPage")
                .get("pathToPyFiles", "");
    }

    public void setPathToTempData(String path){
        userPrefs.node("pages").node("ChoosingPathPage")
                .put("pathToTempData", path);
    }

    public String getPathToTempData(){
        return  userPrefs.node("pages").node("ChoosingPathPage")
                .get("pathToTempData", "");
    }

    public void setChosenPyFiles(List<String> chosenPyFiles) {
        this.chosenPyFiles = chosenPyFiles;
    }
    public List<String> getChosenPyFiles(){return chosenPyFiles;}
}
