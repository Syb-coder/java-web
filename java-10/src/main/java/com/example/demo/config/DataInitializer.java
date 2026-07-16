package com.example.demo.config;

import com.example.demo.entity.Approval;
import com.example.demo.entity.Attendance;
import com.example.demo.entity.ClassInfo;
import com.example.demo.entity.Course;
import com.example.demo.entity.Major;
import com.example.demo.entity.Notice;
import com.example.demo.entity.OperationLog;
import com.example.demo.entity.Score;
import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ClassInfoRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.MajorRepository;
import com.example.demo.repository.NoticeRepository;
import com.example.demo.repository.OperationLogRepository;
import com.example.demo.repository.ScoreRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器：应用启动时自动创建初始数据
 * <p>
 * 为什么实现 CommandLineRunner：Spring Boot 启动完成后自动执行 run 方法，
 * 保证数据库表已创建后再插入初始数据。
 * 为什么每个实体独立检查 count()：各实体初始化互不依赖，避免某实体已有数据时跳过其他实体初始化，
 * 保证每次启动都能补齐缺失的初始数据，同时保证幂等性。
 * </p>
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassInfoRepository classInfoRepository;
    private final CourseRepository courseRepository;
    private final ScoreRepository scoreRepository;
    private final MajorRepository majorRepository;
    private final AttendanceRepository attendanceRepository;
    private final ApprovalRepository approvalRepository;
    private final NoticeRepository noticeRepository;
    private final OperationLogRepository operationLogRepository;

    /**
     * 构造器注入：注入用户仓库及 10 个业务实体仓库
     *
     * @param userRepository 用户仓库
     * @param passwordEncoder 密码加密器
     * @param studentRepository 学生仓库
     * @param teacherRepository 教师仓库
     * @param classInfoRepository 班级仓库
     * @param courseRepository 课程仓库
     * @param scoreRepository 成绩仓库
     * @param majorRepository 专业仓库
     * @param attendanceRepository 考勤仓库
     * @param approvalRepository 审批仓库
     * @param noticeRepository 公告仓库
     * @param operationLogRepository 操作记录仓库
     */
    @Autowired
    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           StudentRepository studentRepository,
                           TeacherRepository teacherRepository,
                           ClassInfoRepository classInfoRepository,
                           CourseRepository courseRepository,
                           ScoreRepository scoreRepository,
                           MajorRepository majorRepository,
                           AttendanceRepository attendanceRepository,
                           ApprovalRepository approvalRepository,
                           NoticeRepository noticeRepository,
                           OperationLogRepository operationLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.classInfoRepository = classInfoRepository;
        this.courseRepository = courseRepository;
        this.scoreRepository = scoreRepository;
        this.majorRepository = majorRepository;
        this.attendanceRepository = attendanceRepository;
        this.approvalRepository = approvalRepository;
        this.noticeRepository = noticeRepository;
        this.operationLogRepository = operationLogRepository;
    }

    @Override
    public void run(String... args) {
        initUsers();
        initStudents();
        initTeachers();
        initClasses();
        initCourses();
        initScores();
        initMajors();
        initAttendances();
        initApprovals();
        initNotices();
        initLogs();
    }

    /**
     * 初始化用户账号：管理员、教师、学生
     * <p>
     * 数据库已有用户时跳过，避免重复插入。
     * </p>
     */
    private void initUsers() {
        if (userRepository.count() > 0) {
            return;
        }
        createUser("admin", "admin123", "ADMIN", "系统管理员", "13800000001");
        createUser("teacher", "teacher123", "TEACHER", "刘老师", "13900139001");
        createUser("student", "student123", "STUDENT", "张三", "13800138001");
    }

    /**
     * 创建单个用户账号
     * <p>
     * 双重检查：先检查用户名是否存在，避免并发场景下重复插入。
     * 密码使用 BCrypt 加密后存储，原始明文不落库。
     * </p>
     *
     * @param username 用户名
     * @param password 明文密码（仅用于加密，不存储）
     * @param role 角色（大写枚举值）
     * @param realName 真实姓名
     * @param phone 联系电话
     */
    private void createUser(String username, String password, String role, String realName, String phone) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setRealName(realName);
        user.setPhone(phone);
        userRepository.save(user);
    }

    /**
     * 初始化学生数据（3 条）
     */
    private void initStudents() {
        if (studentRepository.count() > 0) {
            return;
        }
        Student s1 = new Student();
        s1.setName("张三");
        s1.setStudentNo("2507012401");
        s1.setGender("男");
        s1.setAge("20");
        s1.setClassName("计算机2401");
        s1.setPhone("13800138001");
        studentRepository.save(s1);

        Student s2 = new Student();
        s2.setName("李四");
        s2.setStudentNo("2507012402");
        s2.setGender("女");
        s2.setAge("19");
        s2.setClassName("计算机2401");
        s2.setPhone("13800138002");
        studentRepository.save(s2);

        Student s3 = new Student();
        s3.setName("王五");
        s3.setStudentNo("2507012403");
        s3.setGender("男");
        s3.setAge("21");
        s3.setClassName("计算机2402");
        s3.setPhone("13800138003");
        studentRepository.save(s3);
    }

    /**
     * 初始化教师数据（2 条）
     */
    private void initTeachers() {
        if (teacherRepository.count() > 0) {
            return;
        }
        Teacher t1 = new Teacher();
        t1.setName("刘老师");
        t1.setTeacherNo("T2026001");
        t1.setGender("男");
        t1.setAge("35");
        t1.setPhone("13900139001");
        teacherRepository.save(t1);

        Teacher t2 = new Teacher();
        t2.setName("杨老师");
        t2.setTeacherNo("T2026002");
        t2.setGender("女");
        t2.setAge("40");
        t2.setPhone("13900139002");
        teacherRepository.save(t2);
    }

    /**
     * 初始化班级数据（3 条）
     */
    private void initClasses() {
        if (classInfoRepository.count() > 0) {
            return;
        }
        ClassInfo c1 = new ClassInfo();
        c1.setName("计算机2401");
        c1.setCapacity("40");
        c1.setTeacherType("班主任");
        classInfoRepository.save(c1);

        ClassInfo c2 = new ClassInfo();
        c2.setName("计算机2402");
        c2.setCapacity("40");
        c2.setTeacherType("班主任");
        classInfoRepository.save(c2);

        ClassInfo c3 = new ClassInfo();
        c3.setName("软件2401");
        c3.setCapacity("35");
        c3.setTeacherType("班主任");
        classInfoRepository.save(c3);
    }

    /**
     * 初始化课程数据（4 条）
     */
    private void initCourses() {
        if (courseRepository.count() > 0) {
            return;
        }
        Course c1 = new Course();
        c1.setName("高等数学");
        c1.setRemark("公共基础必修课");
        courseRepository.save(c1);

        Course c2 = new Course();
        c2.setName("数据结构");
        c2.setRemark("专业核心课");
        courseRepository.save(c2);

        Course c3 = new Course();
        c3.setName("操作系统");
        c3.setRemark("专业核心课");
        courseRepository.save(c3);

        Course c4 = new Course();
        c4.setName("Java程序设计");
        c4.setRemark("专业必修课");
        courseRepository.save(c4);
    }

    /**
     * 初始化成绩数据（3 条）
     */
    private void initScores() {
        if (scoreRepository.count() > 0) {
            return;
        }
        Score sc1 = new Score();
        sc1.setCourseId(1L);
        sc1.setStudentNo("2507012401");
        sc1.setScore("85");
        scoreRepository.save(sc1);

        Score sc2 = new Score();
        sc2.setCourseId(1L);
        sc2.setStudentNo("2507012402");
        sc2.setScore("92");
        scoreRepository.save(sc2);

        Score sc3 = new Score();
        sc3.setCourseId(2L);
        sc3.setStudentNo("2507012401");
        sc3.setScore("78");
        scoreRepository.save(sc3);
    }

    /**
     * 初始化专业数据（3 条）
     */
    private void initMajors() {
        if (majorRepository.count() > 0) {
            return;
        }
        Major m1 = new Major();
        m1.setName("计算机科学与技术");
        m1.setCollege("计算机学院");
        m1.setRemark("国家级一流本科专业");
        majorRepository.save(m1);

        Major m2 = new Major();
        m2.setName("软件工程");
        m2.setCollege("计算机学院");
        m2.setRemark("省级特色专业");
        majorRepository.save(m2);

        Major m3 = new Major();
        m3.setName("网络工程");
        m3.setCollege("计算机学院");
        m3.setRemark("应用型人才培养专业");
        majorRepository.save(m3);
    }

    /**
     * 初始化考勤数据（3 条）
     */
    private void initAttendances() {
        if (attendanceRepository.count() > 0) {
            return;
        }
        Attendance a1 = new Attendance();
        a1.setStudentNo("2507012401");
        a1.setCourseId(1L);
        a1.setDate("2026-01-12");
        a1.setStatus("出勤");
        attendanceRepository.save(a1);

        Attendance a2 = new Attendance();
        a2.setStudentNo("2507012402");
        a2.setCourseId(1L);
        a2.setDate("2026-01-12");
        a2.setStatus("迟到");
        attendanceRepository.save(a2);

        Attendance a3 = new Attendance();
        a3.setStudentNo("2507012403");
        a3.setCourseId(2L);
        a3.setDate("2026-01-12");
        a3.setStatus("旷课");
        attendanceRepository.save(a3);
    }

    /**
     * 初始化审批数据（2 条）
     */
    private void initApprovals() {
        if (approvalRepository.count() > 0) {
            return;
        }
        Approval ap1 = new Approval();
        ap1.setStudentNo("2507012401");
        ap1.setType("请假");
        ap1.setReason("家中有事需请假一天");
        ap1.setStatus("待审批");
        ap1.setOpinion("");
        approvalRepository.save(ap1);

        Approval ap2 = new Approval();
        ap2.setStudentNo("2507012403");
        ap2.setType("奖学金申请");
        ap2.setReason("申请校级一等奖学金");
        ap2.setStatus("待审批");
        ap2.setOpinion("");
        approvalRepository.save(ap2);
    }

    /**
     * 初始化公告数据（3 条）
     */
    private void initNotices() {
        if (noticeRepository.count() > 0) {
            return;
        }
        Notice n1 = new Notice();
        n1.setTitle("关于2026年春季学期选课通知");
        n1.setAuthor("教务处");
        n1.setDate("2026-01-15");
        noticeRepository.save(n1);

        Notice n2 = new Notice();
        n2.setTitle("2026年寒假放假安排");
        n2.setAuthor("校办公室");
        n2.setDate("2026-01-10");
        noticeRepository.save(n2);

        Notice n3 = new Notice();
        n3.setTitle("期末考试时间安排通知");
        n3.setAuthor("教务处");
        n3.setDate("2026-01-05");
        noticeRepository.save(n3);
    }

    /**
     * 初始化操作记录数据（5 条）
     * <p>
     * 模拟系统常见操作行为，覆盖登录、学生管理、成绩管理、公告、统计等核心场景。
     * </p>
     */
    private void initLogs() {
        if (operationLogRepository.count() > 0) {
            return;
        }
        createLog("登录系统", "管理员 admin 登录系统");
        createLog("新增学生", "新增学生：张三（学号 2507012401）");
        createLog("修改成绩", "修改学生 2507012401 的数据结构成绩为 78");
        createLog("发布公告", "发布公告：关于2026年春季学期选课通知");
        createLog("查看统计", "查看学生成绩统计报表");
    }

    /**
     * 创建单条操作记录
     *
     * @param action 操作动作
     * @param detail 操作详情
     */
    private void createLog(String action, String detail) {
        OperationLog log = new OperationLog();
        log.setAction(action);
        log.setDetail(detail);
        log.setTime("2026-01-15 09:30:00");
        operationLogRepository.save(log);
    }
}
