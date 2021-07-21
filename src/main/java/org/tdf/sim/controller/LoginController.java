package org.tdf.sim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tdf.sim.dao.UserDao;
import org.tdf.sim.entity.UserEntity;
import org.tdf.sim.service.CourseService;
import org.tdf.sim.service.SecurityService;
import org.tdf.sim.type.Response;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
public class LoginController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityService securityService;

    private CourseService courseService;

    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }


    /**
     * 用户登录
     *
     * @param id
     * @param password
     * @return
     * @throws NoSuchElementException
     */
    @PostMapping("/login")
    public Response<Map<String, Object>> login(@RequestParam(value = "id") String id,
                                               @RequestParam(value = "password") String password,
                                               @RequestParam(value = "loginType") String loginType,
                                               HttpSession session) throws NoSuchElementException {
        Map<String, Object> map = new HashMap<>();
        Optional<UserEntity> optional1 = null;
        if(loginType.equals("1")){
            optional1 = userDao.findByIdAndRoleID(id,UserEntity.ROLE_ID.STUDENT);
        }else if(loginType.equals("2")){
            optional1 = userDao.findByIdAndRoleID(id,UserEntity.ROLE_ID.TEACHER);
            if(!optional1.isPresent()){
                optional1 = userDao.findByIdAndRoleID(id,UserEntity.ROLE_ID.MANAGER);
            }
        }else optional1 = userDao.findById(id);
        if (optional1.isPresent()) {
            Optional<UserEntity> optional = userDao.findById(id);
            userDao.findByIdAndRoleID(id, optional.get().getRoleID());
            if (optional.isPresent() && optional.get().getPassword().equals(password) && !StringUtils.isEmpty(password)) {
                session.setAttribute("user_id", optional.get().getId());
                map.put("id", optional.get().getId());
                map.put("role_id", optional.get().getRoleID().getId());
                if (loginType.equals("1")) {
                    int size = courseService.getCourseHaveClasses(optional.get().getId(), 1).size();
                    map.put("custom_course_size", size);
                }
                return Response.success(map);
            } else {
                return Response.error("密码错误");
            }
        } else {
            return Response.error("用户名不存在");
        }
    }

    @PostMapping("/createToken")
    public Response<Map<String, Object>> createToken(@RequestParam(value = "id") String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", securityService.createJWT(id));
        return Response.success(map);
    }

    /**
     * 用户修改个人密码
     *
     * @param id
     * @param password
     * @param newPassword
     * @return
     */
    @PostMapping("/updateUser")
    public Response<String> updateUser(@RequestParam(value = "id", required = true) String id,
                                       @RequestParam(value = "password", required = true) String password,
                                       @RequestParam(value = "newPassword", required = true) String newPassword) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        if (newPassword.length() > 20 || 6 > newPassword.length()
                || p.matcher(password).find() || p.matcher(newPassword).find()) {
            return Response.error("输入的密码有误");
        } else {
            if (userDao.findById(id).isPresent()) {
                Optional<UserEntity> optional = userDao.findById(id);
                if (password.equals(newPassword) && password.equals(optional.get().getPassword())) {
                    return Response.error("新旧密码一致");
                }
                if (optional.isPresent() && optional.get().getPassword().equals(password) && !StringUtils.isEmpty(password)) {
                    UserEntity entity = optional.get();
                    entity.setPassword(newPassword);
                    entity.setUpdatedAt(LocalDateTime.now());
                    userDao.save(entity);
                    return Response.success("修改成功");
                } else {
                    return Response.error("修改失败");
                }
            } else {
                return Response.error("用户不存在");
            }
        }
    }
}
