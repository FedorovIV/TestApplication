package ru.FedorILyaCO.MLTests.application.preferences;

import java.io.File;
import java.util.List;
import java.util.Objects;
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

    public ByBitAPIPageData getByBitAPIPageData(){
        Preferences upByBitAPIPage = userPrefs.node("pages").node("ByBitAPIPage");
        return new ByBitAPIPageData(upByBitAPIPage.get("firstTicker", ""),
                upByBitAPIPage.get("secondTicker", ""),
                upByBitAPIPage.get("baseInterval", ""),
                upByBitAPIPage.get("dataBegin", ""),
                upByBitAPIPage.get("dataEnd", ""));
    }

    public void setByBitAPIPageData(ByBitAPIPageData byBitAPIPageData){
        Preferences upByBitAPIPage = userPrefs.node("pages").node("ByBitAPIPage");
        upByBitAPIPage.put("firstTicker", byBitAPIPageData.getFirstTicker());
        upByBitAPIPage.put("secondTicker", byBitAPIPageData.getSecondTicker());
        upByBitAPIPage.put("baseInterval", byBitAPIPageData.getBaseInterval());
        upByBitAPIPage.put("dataBegin", byBitAPIPageData.getDataBegin());
        upByBitAPIPage.put("dataEnd", byBitAPIPageData.getDataEnd());
    }

    public static class ByBitAPIPageData{
        String firstTicker;
        String secondTicker;
        String baseInterval;
        String dataBegin;
        String dataEnd;

        public ByBitAPIPageData(String firstTicker, String secondTicker,
                                String baseInterval, String dataBegin, String dataEnd){
            this.firstTicker = firstTicker;
            this.secondTicker = secondTicker;
            this.baseInterval = baseInterval;
            this.dataBegin = dataBegin;
            this.dataEnd = dataEnd;
        }

        public String getFirstTicker(){
            return firstTicker;
        }
        public String getSecondTicker(){
            return secondTicker;
        }
        public String getBaseInterval(){
            return baseInterval;
        }
        public String getDataBegin(){
            return dataBegin;
        }
        public String getDataEnd(){
            return dataEnd;
        }
    }
    public void setChosenPyFiles(List<String> chosenPyFiles) {
        this.chosenPyFiles = chosenPyFiles;
    }
    public List<String> getChosenPyFiles(){return chosenPyFiles;}

    public static class CreateDataSetData{
        String pathToDataFrame;
        String dtp;
        String dls;
        String Rule;
        String typeConsOrPar;
        String y_Time;

        public CreateDataSetData(String pathToDataFrame, String dtp,
                                     String dls, String Rule, String typeConsOrPar, String y_Time){
            this.pathToDataFrame = pathToDataFrame;
            this.dtp = dtp;
            this.dls = dls;
            this.Rule = Rule;
            this.typeConsOrPar = typeConsOrPar;
            this.y_Time = y_Time;
        }

        public String getPathToDataFrame(){
            return pathToDataFrame;
        }
        public String getDtp(){
            return dtp;
        }
        public String getDls(){
            return dls;
        }
        public String getRule(){
            return Rule;
        }
        public String getTypeConsOrPar(){
            return typeConsOrPar;
        }
        public String getY_Time(){
            return y_Time;
        }

    }

    public CreateDataSetData getCreateDataSetData(){
        Preferences upByBitAPIPage = userPrefs.node("pages").node("CreateDataSetPage");

        return new CreateDataSetData(
                upByBitAPIPage.get("pathToDataFrame", ""),
                upByBitAPIPage.get("DTP", ""),
                upByBitAPIPage.get("DLS", ""),
                upByBitAPIPage.get("rule", ""),
                upByBitAPIPage.get("typeConsOrPar", ""),
                upByBitAPIPage.get("Y_Time", "")
              );
    }
    public void setCreateDataSetData(CreateDataSetData createDataSetData){
        Preferences upByBitAPIPage = userPrefs.node("pages").node("CreateDataSetPage");
        upByBitAPIPage.put("DLS", createDataSetData.getDls());
        upByBitAPIPage.put("DTP", createDataSetData.getDtp());
        upByBitAPIPage.put("pathToDataFrame", createDataSetData.getPathToDataFrame());
        upByBitAPIPage.put("rule", createDataSetData.getRule());
        upByBitAPIPage.put("Y_Time", createDataSetData.getY_Time());
        upByBitAPIPage.put("typeConsOrPar", createDataSetData.getTypeConsOrPar());
    }
}
