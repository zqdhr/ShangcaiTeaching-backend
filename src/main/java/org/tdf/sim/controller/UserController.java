package org.tdf.sim.controller;

import org.springframework.util.StringUtils;
import org.tdf.sim.type.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.tdf.sim.config.TDSHttpSessionListener;
import org.tdf.sim.service.CoreService;
import org.tdf.sim.service.SecurityService;
import org.tdf.sim.type.*;
import org.tdf.sim.util.PageTool;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static org.tdf.sim.Constants.JWT_HEADER_KEY;

@RestController
public class UserController {

    @GetMapping(value = "/test")
    public Object login(@RequestParam("username") String username, HttpSession session) {
        session.setAttribute("username", username);
        return Response.success(null);
    }

    @PostMapping("/logout")
    public Response<String> logout(HttpSession session) {
        if (StringUtils.isEmpty(session.getAttribute("user_id"))){
            return Response.error("logout fail,cookie does not contain user_id");
        }
        session.invalidate();
        return Response.success(null);
    }

    @RequestMapping("/online")
    public Response<Integer> online() {
        int online = TDSHttpSessionListener.sessions.values().stream().filter(x -> !StringUtils.isEmpty(x.getAttribute("user_id"))).collect(
                collectingAndThen(
                        toCollection(() -> new TreeSet<>(Comparator.comparing(x -> ((String) x.getAttribute("user_id"))))), ArrayList::new)
        ).size();
        return Response.success(online);
    }

    @Autowired
    private CoreService coreService;

    @Autowired
    private SecurityService securityService;

    @RequestMapping("/online_users")
    public Response<Page<OnlineStudent>> onlineStudents(@RequestParam(value = "per_page") Integer perPage,
                                                        @RequestParam(value = "page") Integer page) {
        List<HttpSession> sessions = TDSHttpSessionListener.sessions.values().stream().filter(x -> !StringUtils.isEmpty(x.getAttribute("user_id"))).collect(
                collectingAndThen(
                        toCollection(() -> new TreeSet<>(Comparator.comparing(x -> ((String) x.getAttribute("user_id"))))), ArrayList::new)
        );
        List<String> ids = sessions.stream()
                .map(session -> (String) session.getAttribute("user_id")).collect(Collectors.toList());
        return Response.success(PageTool.getPageList(coreService.getOnlineStudents(ids), page, perPage));
    }

    @PostMapping(value = "/visit_course")
    public Response<String> visitCourse(@RequestBody @Validated VisitCoursePost visitCoursePost) {
        Pair<Boolean, String> pair = coreService.visitCourse(visitCoursePost.getUserID(), visitCoursePost.getCategoryID());
        if (pair.getKey()) {
            return Response.success(null);
        } else {
            return Response.error(pair.getValue());
        }
    }

    @GetMapping("/get_visit_course_frequency")
    public Response<Page<CategoryCourseFrequency>> getVisitCourseFrequency(@RequestParam(value = "per_page") Integer perPage,
                                                                           @RequestParam(value = "page") Integer page,
                                                                           @RequestParam(value = "category_name", required = false) String categoryName,
                                                                           @RequestParam(value = "start", required = false) String start,
                                                                           @RequestParam(value = "end", required = false) String end) {
        return Response.success(PageTool.getPageList(coreService.getVisitCourseFrequency(categoryName, start, end), page, perPage));
    }

    @DeleteMapping(value = "/delete_user")
    public Response<String> deleteUser(@RequestBody DeleteUserPost deleteUserPost,
                                       @RequestHeader(JWT_HEADER_KEY) String jwt) {
        String ownerID = securityService.getUserId(jwt);
        Pair<Boolean, String> pair = coreService.deleteUser(deleteUserPost.userIDs, ownerID);
        if (pair.getKey()) {
            return Response.success(null);
        } else {
            return Response.error(pair.getValue());
        }
    }

    @PostMapping(value = "/update_userPassword")
    public Response<String> updateUserPassword(@RequestBody UpdateUserPasPost updateUserPasPost,
                                               @RequestHeader(JWT_HEADER_KEY) String jwt) {
        String ownerID = securityService.getUserId(jwt);
        Pair<Boolean, String> pair = coreService.updateUserPassword(updateUserPasPost.userIDs, ownerID, updateUserPasPost.password);
        if (pair.getKey()) {
            return Response.success(null);
        } else {
            return Response.error(pair.getValue());
        }
    }

    @GetMapping(value = "/search_user")
    public Response<Page<Personnel>> searchUser(@RequestParam(value = "search", required = false) String search,
                                                @RequestParam(value = "per_page") Integer perPage,
                                                @RequestParam(value = "page") Integer page,
                                                @RequestHeader(JWT_HEADER_KEY) String jwt) {
        String ownerID = securityService.getUserId(jwt);
        List<Personnel> list = coreService.searchUser(ownerID, search);
        return Response.success(PageTool.getPageList(list, page, perPage));
    }

    @PostMapping(value = "/modify_user")
    public Response<String> modifyUser(@RequestBody ModifyUserPost modifyUserPost,
                                       @RequestHeader(JWT_HEADER_KEY) String jwt) {
        String ownerID = securityService.getUserId(jwt);
        Pair<Boolean, String> pair = coreService.modifyUser(ownerID, modifyUserPost);
        if (pair.getKey()) {
            return Response.success(null);
        } else {
            return Response.error(pair.getValue());
        }
    }

}
