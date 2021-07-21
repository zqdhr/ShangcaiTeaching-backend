package org.tdf.sim.controller;

import org.tdf.sim.type.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tdf.sim.service.CoreService;
import org.tdf.sim.service.SecurityService;
import org.tdf.sim.type.Response;
import org.tdf.sim.util.ExcelUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.tdf.sim.Constants.JWT_HEADER_KEY;

@RestController
public class ExcelController {

    @GetMapping("/download_excel")
    public void exportExcel(HttpServletResponse response) throws Exception {
        List<Map<String, Object>> dataList = new ArrayList<>();
        ExcelUtils.createExcel(response, "template.xlsx", new String[]{"学号/工号（必填）", "姓名（必填）", "证件证号", "性别（男/女）（必填）", "电话", "院系（学生必填）", "专业（学生必填）", "年级（学生必填）"
                , "班级（学生必填）", "职称（0学生,1教师）（必填）"}, dataList);
    }

    @Autowired
    private CoreService coreService;


    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/upload_excel", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> uploadExcel(@RequestPart("excel_file") MultipartFile uploadFile, @RequestHeader(JWT_HEADER_KEY) String jwt) throws IOException {
        String userID = securityService.getUserId(jwt);
        org.tdf.sim.type.Pair<Boolean, String> pair = coreService.uploadExcel(userID, uploadFile.getInputStream(), uploadFile.getName());
        if (pair.getKey()) {
            return Response.success(null);
        } else {
            return Response.error(pair.getValue());
        }
    }

    @RequestMapping(value = "/test_upload_excel", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> testUploadExcel() throws IOException {
        File file = new File("src/main/resources/1.xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        Pair<Boolean, String> pair = coreService.uploadExcel("213213213", inputStream, "1.xlsx");
        if (pair.getKey()) {
            return Response.success(null);
        } else {
            return Response.error(pair.getValue());
        }
    }
}
