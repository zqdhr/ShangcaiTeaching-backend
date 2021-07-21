package org.tdf.sim.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.tdf.sim.dao.*;
import org.tdf.sim.entity.*;
import org.tdf.sim.service.CoreService;
import org.tdf.sim.type.*;
import org.tdf.sim.util.CommonUtils;
import org.tdf.sim.util.ExcelUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CoreServiceImpl implements CoreService {

    private UserDao userDao;

    private ClassDao classDao;

    private CourseFrequencyInfoDao courseFrequencyInfoDao;

    private CategoryCourseFrequencyDao categoryCourseFrequencyDao;

    private CategoryDao categoryDao;

    public CoreServiceImpl(UserDao userDao,
                           ClassDao classDao,
                           CourseFrequencyInfoDao courseFrequencyInfoDao,
                           CategoryCourseFrequencyDao categoryCourseFrequencyDao,
                           CategoryDao categoryDao) {
        this.userDao = userDao;
        this.classDao = classDao;
        this.courseFrequencyInfoDao = courseFrequencyInfoDao;
        this.categoryCourseFrequencyDao = categoryCourseFrequencyDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public List<OnlineStudent> getOnlineStudents(List<String> ids) {
        return ids.stream().map(userDao::findById).filter(Optional::isPresent)
                .map(x -> {
                    if (x.get().getClassID() != null) {
                        Optional<ClassEntity> optional = classDao.findById(x.get().getClassID());
                        if (optional.isPresent()) {
                            return OnlineStudent.builder()
                                    .className(optional.get().getClassName())
                                    .gradeName(optional.get().getGradeName())
                                    .type(x.get().getRoleID() == UserEntity.ROLE_ID.TEACHER ? 1 : 0)
                                    .userID(x.get().getId())
                                    .name(x.get().getName()).build();
                        }
                    }
                    return OnlineStudent.builder().name(x.get().getName())
                            .type(x.get().getRoleID() == UserEntity.ROLE_ID.TEACHER ? 1 : 0)
                            .userID(x.get().getId())
                            .build();

                }).collect(Collectors.toList());
    }

    @Override
    public Pair<Boolean, String> visitCourse(String userID, String categoryID) {
        int counts = courseFrequencyInfoDao.countByVisitCourse(userID, categoryID);
        if (counts != 0) {
            return new Pair<>(true, "");
        }
        courseFrequencyInfoDao.save(CourseFrequencyInfoEntity.builder().categoryID(categoryID).userID(userID).build());
        Optional<CategoryCourseFrequencyEntity> optional = categoryCourseFrequencyDao.findCurrentVisitCourse(categoryID);
        if (optional.isPresent()) {
            CategoryCourseFrequencyEntity entity = optional.get();
            entity.setFrequency(entity.getFrequency() + 1);
            categoryCourseFrequencyDao.save(entity);
        } else {
            CategoryCourseFrequencyEntity entity = CategoryCourseFrequencyEntity.builder()
                    .categoryID(categoryID).frequency(1).build();
            categoryCourseFrequencyDao.save(entity);
        }
        return new Pair<>(true, "");
    }

    @Override
    public List<CategoryCourseFrequency> getVisitCourseFrequency(String categoryName, String start, String end) {
        List<CategoryCourseFrequencyEntity> entities = categoryCourseFrequencyDao.findAll()
                .stream().sorted(Comparator.comparing(CategoryCourseFrequencyEntity::getCreatedAt).reversed())
                .collect(Collectors.toList());
        List<CategoryCourseFrequency> list = entities.stream().map(x -> {
            Optional<CategoryEntity> optional = categoryDao.findById(x.getCategoryID());
            if (optional.isPresent()) {
                return CategoryCourseFrequency.builder()
                        .createdAt(x.getCreatedAt())
                        .frequency(x.getFrequency())
                        .categoryNameChs(optional.get().getCategoryNameChs())
                        .categoryNameEn(optional.get().getCategoryNameEn())
                        .build();
            } else {
                return CategoryCourseFrequency.builder()
                        .createdAt(x.getCreatedAt())
                        .frequency(x.getFrequency())
                        .build();
            }
        }).collect(Collectors.toList());
        if (!StringUtils.isEmpty(categoryName)) {
            list = list.stream().filter(x -> (x.categoryNameChs != null && x.categoryNameChs.contains(categoryName)) ||
                    (x.categoryNameEn != null && x.categoryNameEn.contains(categoryName))).collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(start)) {
            DateTimeFormatter df = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd[['T'hh][:mm][:ss]]")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                    .toFormatter();
            LocalDateTime startDate = LocalDateTime.parse(start, df);
            list = list.stream().filter(x -> x.createdAt.isAfter(startDate)).collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(end)) {
            DateTimeFormatter df = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd[['T'hh][:mm][:ss]]")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                    .toFormatter();
            LocalDateTime endDate = LocalDateTime.parse(end, df);
            list = list.stream().filter(x -> x.createdAt.isBefore(endDate)).collect(Collectors.toList());
        }
        list = list.stream().sorted(Comparator.comparing(CategoryCourseFrequency::getFrequency).reversed()).collect(Collectors.toList());
        return list;
    }

    @Override
    public Pair<Boolean, String> uploadExcel(String userID, InputStream in, String filename) throws IOException {
        ExcelUtils utils = new ExcelUtils(in, filename);
        int end = utils.getRowCount(0);
        List<Personnel> personnel = utils.parsePersonnel(utils.read(0, 1, end));
        Pair<Boolean, String> pair = validPersonnel(personnel);
        if (!pair.key) {
            return pair;
        }
        Optional<UserEntity> optionalUserEntity = userDao.findById(userID);
        if (!optionalUserEntity.isPresent()) {
            return new Pair<>(false, "user is not exist");
        } else {
            switch (optionalUserEntity.get().getRoleID()) {
                case MANAGER:
                    if (personnel.stream().anyMatch(p -> p.getType() != 1)) {
                        return new Pair<>(false, "excel data error,manager only import teachers");
                    }
                    break;
                case TEACHER:
                    if (personnel.stream().anyMatch(p -> p.getType() != 0 || StringUtils.isEmpty(p.getClasses()) || StringUtils.isEmpty(p.getGrade())|| StringUtils.isEmpty(p.getDepartment())|| StringUtils.isEmpty(p.getMajor()))) {
                        return new Pair<>(false, "excel data error,teacher only import students and students must have classes, grade, major, department");
                    }
                    break;
                case STUDENT:
                    return new Pair<>(false, "excel data error");
            }
        }
        personnel.forEach(p -> {
            Optional<UserEntity> op = userDao.findById(p.getId());
            switch (p.getType()) {
                // 学生
                case 0:
                    Optional<ClassEntity> optional = classDao.findByClassNameAndGradeNameAndDepartmentAndMajor(p.getClasses(), p.getGrade(), StringUtils.isEmpty(p.getDepartment()) ? "" : p.getDepartment(), StringUtils.isEmpty(p.getMajor()) ? "" : p.getMajor());
                    ClassEntity entity;
                    entity = optional.orElseGet(() -> classDao.save(ClassEntity.builder()
                            .className(p.getClasses())
                            .department(p.getDepartment())
                            .gradeName(p.getGrade())
                            .major(p.getMajor())
                            .build()));
                    if (!op.isPresent()) {
                        userDao.save(UserEntity.builder()
                                .classID(entity.getId())
                                .gender(p.getGender() == 0 ? UserEntity.GENDER.MAN : UserEntity.GENDER.WOMAN)
                                .identificationNumber(p.getIdentificationNumber())
                                .password("12345678")
                                .phoneNumber(p.getPhone())
                                .roleID(UserEntity.ROLE_ID.STUDENT)
                                .id(p.getId())
                                .name(p.getName())
                                .build());
                    }
                    break;
                // 教师
                case 1:
                    if (!op.isPresent()) {
                        userDao.save(UserEntity.builder()
                                .gender(p.getGender() == 0 ? UserEntity.GENDER.MAN : UserEntity.GENDER.WOMAN)
                                .identificationNumber(p.getIdentificationNumber())
                                .password("12345678")
                                .phoneNumber(p.getPhone())
                                .roleID(UserEntity.ROLE_ID.TEACHER)
                                .id(p.getId())
                                .name(p.getName())
                                .build());
                    }
                    break;
            }
        });
        return new Pair<>(true, "");
    }

    private Pair<Boolean, String> validPersonnel(List<Personnel> personnel) {
        List<String> iDs = personnel.stream().map(Personnel::getId).collect(Collectors.toList());
        List<String> distinctIds = iDs.stream().distinct().collect(Collectors.toList());
        if (personnel.size() != distinctIds.size()) {
            for (int i = 0; i < personnel.size(); i++) {
                int size = 0;
                for (String iD : iDs) {
                    if (personnel.get(i).id.equals(iD)) {
                        size++;
                    }
                    if (size > 1) {
                        return new Pair<>(false, "id is repeated, line is " + (i + 1));
                    }
                }
            }
        }
        for (int i = 0; i < personnel.size(); i++) {
            if (!CommonUtils.isNumeric(personnel.get(i).getId())) {
                return new Pair<>(false, "id cannot not error, line is " + (i + 1));
            }
            if ((!StringUtils.isEmpty(personnel.get(i).getPhone())) && (!CommonUtils.isMobile(personnel.get(i).getPhone()))) {
                return new Pair<>(false, "phone format error, line is " + (i + 1));
            }
            if (StringUtils.isEmpty(personnel.get(i).getName())) {
                return new Pair<>(false, "name cannot empty, line is " + (i + 1));
            }
            if (StringUtils.isEmpty(personnel.get(i).getId()) || userDao.findById(personnel.get(i).getId()).isPresent()) {
                return new Pair<>(false, "id cannot empty or id is exist, line is " + (i + 1));
            }
        }
        return new Pair<>(true, "");
    }

    @Override
    public Pair<Boolean, String> deleteUser(List<String> userIDs, String ownerID) {
        Optional<UserEntity> op = userDao.findById(ownerID);
        if (!op.isPresent()) {
            return new Pair<>(false, "jwt user_id is exist");
        }
        switch (op.get().getRoleID()) {
            case MANAGER: {
                boolean allExist = userIDs.stream().allMatch(x -> userDao.findById(x).isPresent() && userDao.findById(x).get().getRoleID() == UserEntity.ROLE_ID.TEACHER);
                if (!allExist) {
                    return new Pair<>(false, "user_id is invalid");
                }
            }
            break;
            case TEACHER: {
                boolean allExist = userIDs.stream().allMatch(x -> userDao.findById(x).isPresent() && userDao.findById(x).get().getRoleID() == UserEntity.ROLE_ID.STUDENT);
                if (!allExist) {
                    return new Pair<>(false, "user_id is invalid");
                }
            }
            break;
            default:
                return new Pair<>(false, "jwt user_id cannot have power");
        }
        userIDs.forEach(x -> userDao.deleteById(x));
        return new Pair<>(true, "");
    }

    @Override
    public List<Personnel> searchUser(String ownerID, String search) {
        Optional<UserEntity> op = userDao.findById(ownerID);
        if (!op.isPresent()) {
            return new ArrayList<>();
        }
        UserEntity.ROLE_ID role;
        switch (op.get().getRoleID()) {
            case MANAGER:
                role = UserEntity.ROLE_ID.TEACHER;
                break;
            case TEACHER:
                role = UserEntity.ROLE_ID.STUDENT;
                break;
            case STUDENT:
            case NONE:
            default:
                return new ArrayList<>();
        }
        List<UserEntity> list;
        if (StringUtils.isEmpty(search)) {
            list = userDao.findAllByRoleID(role);
        } else {
            list = userDao.findAllByRoleID(role).stream()
                    .filter(x -> x.getId().contains(search) || x.getName().contains(search))
                    .collect(Collectors.toList());
        }
        return list.stream().map(x -> {
            if (x.getRoleID() == UserEntity.ROLE_ID.TEACHER) {
                return Personnel.builder()
                        .type(1)
                        .phone(x.getPhoneNumber())
                        .gender(x.getGender() == UserEntity.GENDER.MAN ? 0 : 1)
                        .identificationNumber(x.getIdentificationNumber())
                        .name(x.getName())
                        .id(x.getId())
                        .build();
            } else {
                Optional<ClassEntity> entity = classDao.findById(x.getClassID());
                if (entity.isPresent()) {
                    return Personnel.builder()
                            .type(0)
                            .phone(x.getPhoneNumber())
                            .gender(x.getGender() == UserEntity.GENDER.MAN ? 0 : 1)
                            .identificationNumber(x.getIdentificationNumber())
                            .name(x.getName())
                            .id(x.getId())
                            .grade(entity.get().getGradeName())
                            .classes(entity.get().getClassName())
                            .department(entity.get().getDepartment())
                            .major(entity.get().getMajor())
                            .build();
                } else {
                    return Personnel.builder()
                            .type(0)
                            .phone(x.getPhoneNumber())
                            .gender(x.getGender() == UserEntity.GENDER.MAN ? 0 : 1)
                            .identificationNumber(x.getIdentificationNumber())
                            .name(x.getName())
                            .id(x.getId())
                            .build();
                }
            }
        }).sorted(Comparator.comparingInt(x -> Integer.parseInt(x.getId()))).collect(Collectors.toList());
    }

    @Override
    public Pair<Boolean, String> modifyUser(String ownerID, ModifyUserPost modifyUserPost) {
        Optional<UserEntity> op = userDao.findById(ownerID);
        if (!op.isPresent()) {
            return new Pair<>(false, "jwt user_id is exist");
        }
        UserEntity.ROLE_ID role;
        switch (op.get().getRoleID()) {
            case MANAGER:
                role = UserEntity.ROLE_ID.TEACHER;
                break;
            case TEACHER:
                role = UserEntity.ROLE_ID.STUDENT;
                break;
            case STUDENT:
            case NONE:
            default:
                return new Pair<>(false, "jwt user_id cannot have power");
        }
        Optional<UserEntity> optional = userDao.findById(modifyUserPost.useID);
        if (!optional.isPresent()) {
            return new Pair<>(false, "user_id is not exist");
        }
        if (optional.get().getRoleID() != role) {
            return new Pair<>(false, "jwt user_id cannot have power");
        }
        UserEntity entity = optional.get();
        entity.setName(modifyUserPost.name);
        entity.setGender(modifyUserPost.gender == 0 ? UserEntity.GENDER.MAN : UserEntity.GENDER.WOMAN);
        entity.setIdentificationNumber(modifyUserPost.identificationNumber);
        entity.setPhoneNumber(modifyUserPost.phone);
        userDao.save(entity);
        return new Pair<>(true, "");
    }


    @Override
    public Pair<Boolean, String> updateUserPassword(List<String> userIDs, String ownerID, String password) {
        Optional<UserEntity> op = userDao.findById(ownerID);
        if (!op.isPresent()) {
            return new Pair<>(false, "jwt user_id is exist");
        }
        switch (op.get().getRoleID()) {
            case MANAGER: {
                boolean allExist = userIDs.stream().allMatch(x -> userDao.findById(x).isPresent() && userDao.findById(x).get().getRoleID() == UserEntity.ROLE_ID.TEACHER);
                if (!allExist) {
                    return new Pair<>(false, "user_id is invalid");
                }
            }
            break;
            case TEACHER: {
                boolean allExist = userIDs.stream().allMatch(x -> userDao.findById(x).isPresent() && userDao.findById(x).get().getRoleID() == UserEntity.ROLE_ID.STUDENT);
                if (!allExist) {
                    return new Pair<>(false, "user_id is invalid");
                }
            }
            break;
            default:
                return new Pair<>(false, "jwt user_id cannot have power");
        }
        userIDs.forEach(id -> {
            Optional<UserEntity> optional = userDao.findById(id);
            if (optional.isPresent()) {
                UserEntity userEntity = optional.get();
                userEntity.setPassword(password);
                userDao.save(userEntity);
            }
        });
        return new Pair<>(true, "");
    }

}
