package com.example.java8.init;

import com.example.java8.model.AdminUser;
import com.example.java8.model.Fabric;
import com.example.java8.model.Measurement;
import com.example.java8.model.Order;
import com.example.java8.model.OrderStatus;
import com.example.java8.model.Style;
import com.example.java8.model.User;
import com.example.java8.repository.AdminUserRepository;
import com.example.java8.repository.FabricRepository;
import com.example.java8.repository.MeasurementRepository;
import com.example.java8.repository.OrderRepository;
import com.example.java8.repository.StyleRepository;
import com.example.java8.repository.UserRepository;
import com.example.java8.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器
 * <p>
 * 应用启动时若数据库为空，则插入示例管理员、用户、面料、款式、量体与订单数据，
 * 便于首次启动即可看到完整功能演示。设计为幂等：仅当核心表为空时才执行插入。
 * </p>
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminUserRepository adminRepo;
    private final UserRepository userRepo;
    private final FabricRepository fabricRepo;
    private final StyleRepository styleRepo;
    private final MeasurementRepository measureRepo;
    private final OrderRepository orderRepo;
    private final AuthService authService;

    public DataInitializer(AdminUserRepository adminRepo, UserRepository userRepo,
                           FabricRepository fabricRepo, StyleRepository styleRepo,
                           MeasurementRepository measureRepo, OrderRepository orderRepo,
                           AuthService authService) {
        this.adminRepo = adminRepo;
        this.userRepo = userRepo;
        this.fabricRepo = fabricRepo;
        this.styleRepo = styleRepo;
        this.measureRepo = measureRepo;
        this.orderRepo = orderRepo;
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        // 幂等判断：管理员表已有数据则跳过
        if (adminRepo.count() > 0) {
            return;
        }

        // ===== 初始化管理员（密码 BCrypt 加密）=====
        AdminUser admin = new AdminUser("admin", authService.encodePassword("admin123"), "超级管理员");
        admin.setCreateTime(LocalDateTime.now());
        adminRepo.save(admin);

        // ===== 初始化示例用户 =====
        // 密码统一为 123456（BCrypt 加密）
        String userPwd = authService.encodePassword("123456");
        User u1 = createUser("alice", userPwd, "Alice", "13800001111");
        User u2 = createUser("bob", userPwd, "Bob", "13800002222");
        User u3 = createUser("carol", userPwd, "Carol", "13800003333");

        // ===== 初始化示例面料 =====
        Fabric f1 = createFabric("意大利羊毛精纺", "羊毛", "藏青", 280.0, 50.0,
                "意大利进口 100% 羊毛，克重 280g，手感柔滑，适合商务西装");
        Fabric f2 = createFabric("埃及长绒棉", "棉", "白色", 120.0, 80.0,
                "埃及长绒棉，透气性好，适合商务衬衫");
        Fabric f3 = createFabric("日本亚麻", "麻", "米白", 160.0, 40.0,
                "日本亚麻面料，吸湿排汗，适合夏季休闲西装");
        Fabric f4 = createFabric("苏州真丝", "丝", "酒红", 380.0, 30.0,
                "苏州产 100% 真丝，光泽细腻，适合晚宴连衣裙与旗袍");
        Fabric f5 = createFabric("蒙古羊绒", "羊绒", "驼色", 580.0, 25.0,
                "蒙古纯羊绒，保暖轻盈，适合高端冬季大衣");
        Fabric f6 = createFabric("美国牛仔布", "牛仔", "靛蓝", 90.0, 100.0,
                "美国产重磅牛仔布，耐磨挺括，适合休闲外套");
        Fabric f7 = createFabric("英国粗花呢", "羊毛", "炭灰", 320.0, 35.0,
                "英国传统粗花呢，纹理粗犷，适合英伦风衣");
        Fabric f8 = createFabric("法国蕾丝", "丝", "粉色", 420.0, 20.0,
                "法国手工蕾丝，精致优雅，适合婚纱与礼服");

        // ===== 初始化示例款式 =====
        Style s1 = createStyle("单排两粒扣商务西装", "西装", 800.0,
                "经典单排两粒扣，平驳领，修身版型，适合商务场合");
        Style s2 = createStyle("法式袖扣正装衬衫", "衬衫", 300.0,
                "法式叠袖，需配袖扣，修身版型，适合正式商务");
        Style s3 = createStyle("A 字收腰连衣裙", "连衣裙", 600.0,
                "A 字版型，腰部收腰，膝上长度，适合宴会与日常");
        Style s4 = createStyle("英伦双排扣风衣", "风衣", 900.0,
                "双排扣，肩章设计，腰带可调，适合春秋季节");
        Style s5 = createStyle("高腰直筒西裤", "西裤", 350.0,
                "高腰直筒，前片单褶，修身显瘦，适合商务搭配");
        Style s6 = createStyle("改良修身旗袍", "旗袍", 1200.0,
                "立领盘扣，侧开衩，腰身修身，传统与现代融合");

        // ===== 初始化示例量体数据 =====
        Measurement m1 = createMeasurement(u1, "日常版", 175.0, 70.0, 38.0, 45.0,
                96.0, 82.0, 98.0, 76.0, 60.0, 105.0, 56.0, "标准体型");
        Measurement m2 = createMeasurement(u1, "修身版", 175.0, 68.0, 37.5, 44.0,
                94.0, 80.0, 96.0, 75.0, 59.0, 104.0, 55.0, "偏瘦，需收紧");
        Measurement m3 = createMeasurement(u2, "日常版", 168.0, 55.0, 33.0, 38.0,
                86.0, 66.0, 90.0, 64.0, 56.0, 100.0, 50.0, "女性标准体型");
        Measurement m4 = createMeasurement(u3, "日常版", 182.0, 80.0, 40.0, 48.0,
                102.0, 90.0, 104.0, 80.0, 64.0, 108.0, 60.0, "偏壮，肩背较宽");

        // ===== 初始化示例订单（覆盖各状态）=====
        createOrder(u1, s1, f1, m1, OrderStatus.PENDING, null);
        createOrder(u2, s3, f4, m3, OrderStatus.MEASURING, "袖口绣字 A");
        createOrder(u3, s2, f2, m4, OrderStatus.CUTTING, null);
        createOrder(u1, s5, f7, m2, OrderStatus.SEWING, null);
        createOrder(u2, s6, f4, m3, OrderStatus.FITTING, "内衬加刺绣");
        createOrder(u3, s4, f7, m4, OrderStatus.COMPLETED, null);

        System.out.println("[DataInitializer] 示例数据初始化完成：1 管理员、3 用户、8 面料、6 款式、4 量体、6 订单");
    }

    /**
     * 创建并保存用户
     */
    private User createUser(String username, String encodedPwd, String nickname, String phone) {
        User u = new User(username, encodedPwd, nickname, phone);
        u.setCreateTime(LocalDateTime.now());
        return userRepo.save(u);
    }

    /**
     * 创建并保存面料
     */
    private Fabric createFabric(String name, String material, String color,
                                double price, double stock, String desc) {
        return fabricRepo.save(new Fabric(name, material, color, price, stock, desc));
    }

    /**
     * 创建并保存款式
     */
    private Style createStyle(String name, String category, double fee, String desc) {
        return styleRepo.save(new Style(name, category, fee, desc));
    }

    /**
     * 创建并保存量体数据
     */
    private Measurement createMeasurement(User user, String name, double h, double w,
                                          double neck, double shoulder, double chest,
                                          double waist, double hip, double clothesLen,
                                          double sleeveLen, double pantsLen, double thigh,
                                          String remark) {
        Measurement m = new Measurement();
        m.setUser(user);
        m.setName(name);
        m.setHeight(h);
        m.setWeight(w);
        m.setNeckCircumference(neck);
        m.setShoulderWidth(shoulder);
        m.setChestCircumference(chest);
        m.setWaistCircumference(waist);
        m.setHipCircumference(hip);
        m.setClothesLength(clothesLen);
        m.setSleeveLength(sleeveLen);
        m.setPantsLength(pantsLen);
        m.setThighCircumference(thigh);
        m.setRemark(remark);
        m.setCreateTime(LocalDateTime.now());
        return measureRepo.save(m);
    }

    /**
     * 创建并保存订单
     */
    private void createOrder(User user, Style style, Fabric fabric, Measurement m,
                             OrderStatus status, String remark) {
        Order order = new Order();
        order.setUser(user);
        order.setStyle(style);
        order.setFabric(fabric);
        order.setMeasurement(m);
        // 总价 = 工费 + 面料单价 × 3 米
        order.setTotalPrice(style.getCraftFee() + fabric.getUnitPrice() * 3);
        order.setStatus(status);
        order.setRemark(remark);
        order.setCreateTime(LocalDateTime.now().minusHours((long) (Math.random() * 72)));
        orderRepo.save(order);
    }
}
