package org.tdf.sim.service;

import org.tdf.sim.type.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface CoreService {

    List<OnlineStudent> getOnlineStudents(List<String> ids);

    Pair<Boolean, String> visitExperiment(String userID, String experimentID);

    List<CategoryExperimentFrequency> getVisitExperimentFrequency(String experimentName, String start, String end);

    Pair<Boolean, String> uploadExcel(String userID, InputStream in, String filename) throws IOException;

    Pair<Boolean, String> deleteUser(List<String> userIDs,String ownerID);

    List<Personnel> searchUser(String ownerID, String search);

    Pair<Boolean, String> modifyUser(String ownerID, ModifyUserPost modifyUserPost);

    Pair<Boolean, String> updateUserPassword(List<String> userIDs,String ownerID,String password);
}
