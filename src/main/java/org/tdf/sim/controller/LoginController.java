package org.tdf.sim.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tdf.sim.dao.UserDao;
import org.tdf.sim.entity.UserEntity;
import org.tdf.sim.service.CourseService;
import org.tdf.sim.service.SecurityService;
import org.tdf.sim.type.Response;
import org.tdf.sim.util.ApplicationUtils;
import org.tdf.sim.util.ConstantUtils;
import org.tdf.sim.util.LoginUser;
import org.tdf.sim.util.MD5Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/cas")
public class LoginController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityService securityService;

    private CourseService courseService;

    private MD5Utils md5Utils;

    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @Autowired
    private HttpServletRequest request;

    @Value("${cas.server-login-url}")
    private String serverLoginUrl;

    @Value("${cas.client-host-url}")
    private String clientHostUrl;

    @Value("${ilab.appid}")
    private String appid;

    @Value("${ilab.signature}")
    private String signature;


    /**
     * 用户登录
     *
     * @return
     * @throws NoSuchElementException
     */
    @PostMapping("/login")
    public Response<Map<String, Object>> login(@RequestParam(value = "ticket") String ticket,
                                               @RequestParam(value = "type") int type,
                                               HttpSession session, ModelMap map) throws NoSuchElementException, UnsupportedEncodingException {
        //type 1:cas认证 2：ilab认证
        if(type == 1){
            String name = request.getRemoteUser();
            System.out.println("name = "+name);
            if("".equals(name) || name == null){
                System.out.println("url=" + "redirect:" + serverLoginUrl + "?service=" + clientHostUrl);
                map.put("url","url=" + "redirect:" + serverLoginUrl + "?service=" + clientHostUrl);
                return Response.success(map);
            }
            LoginUser userinfo = ApplicationUtils.getCurrentLoginUser(request);
            System.out.println("userinfo="+userinfo);
            System.out.println(ConstantUtils.USER_SESSION_KEY);
            request.getSession().setAttribute(ConstantUtils.USER_SESSION_KEY,userinfo);
            return Response.success();
        }else if(type == 2){
            String sign = ticket + appid + signature;
            String sign_md5 = md5Utils.getMD5(sign.getBytes());
            String url = "http://www.ilab-x.com/open/api/v2/token?ticket="+ ticket + "&appid=" + appid +"&signature=" + sign_md5;
            String res = md5Utils.sendGet(url,"");
            JSONObject jsonObject = JSON.parseObject(res);
            int code = jsonObject.getInteger("code");
            if (code == 0) {
                return Response.success();
            }else{
                String message = jsonObject.getString("msg");
                return Response.error(message);
            }
        }else{
            return Response.success();
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
