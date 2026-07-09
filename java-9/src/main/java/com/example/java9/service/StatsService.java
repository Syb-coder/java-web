package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.dto.StatsResponse;  // 导入统计响应 DTO
import com.example.java9.model.InvestmentOrder;  // 导入投资订单实体
import com.example.java9.model.PaymentOrder;  // 导入支付订单实体
import com.example.java9.model.PaymentStatus;  // 导入支付状态枚举
import com.example.java9.model.RiskStatus;  // 导入风控状态枚举
import com.example.java9.model.TicketStatus;  // 导入工单状态枚举
import com.example.java9.repository.FinancialProductRepository;  // 导入产品 Repository
import com.example.java9.repository.InvestmentOrderRepository;  // 导入投资订单 Repository
import com.example.java9.repository.MerchantRepository;  // 导入商户 Repository
import com.example.java9.repository.PaymentOrderRepository;  // 导入支付订单 Repository
import com.example.java9.repository.RiskRecordRepository;  // 导入风控记录 Repository
import com.example.java9.repository.SupportTicketRepository;  // 导入工单 Repository
import com.example.java9.repository.UserRepository;  // 导入用户 Repository
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解

import java.math.BigDecimal;  // 导入高精度十进制类，金额求和必须用 BigDecimal 避免 double 精度丢失
import java.util.List;  // 导入集合 List

/**
 * 平台统计服务
 * <p>
 * 汇总各业务模块的核心指标，供运营/管理后台首页概览展示。
 * 计数使用 Repository 的 count() 方法，金额使用 stream + reduce 求和。
 * </p>
 */
@Service  // 标记为 Spring Service Bean
public class StatsService {

    private final UserRepository userRepository;  // 用户 Repository，final 保证不可变
    private final MerchantRepository merchantRepository;  // 商户 Repository，final 保证不可变
    private final FinancialProductRepository financialProductRepository;  // 产品 Repository，final 保证不可变
    private final InvestmentOrderRepository investmentOrderRepository;  // 投资订单 Repository，final 保证不可变
    private final PaymentOrderRepository paymentOrderRepository;  // 支付订单 Repository，final 保证不可变
    private final RiskRecordRepository riskRecordRepository;  // 风控记录 Repository，final 保证不可变
    private final SupportTicketRepository supportTicketRepository;  // 工单 Repository，final 保证不可变

    // 构造器注入：保证依赖不可变、显式暴露依赖、便于单元测试，依赖较多时构造器注入更显式清晰
    public StatsService(UserRepository userRepository, MerchantRepository merchantRepository,
                        FinancialProductRepository financialProductRepository,
                        InvestmentOrderRepository investmentOrderRepository,
                        PaymentOrderRepository paymentOrderRepository,
                        RiskRecordRepository riskRecordRepository,
                        SupportTicketRepository supportTicketRepository) {
        this.userRepository = userRepository;  // 注入用户 Repository
        this.merchantRepository = merchantRepository;  // 注入商户 Repository
        this.financialProductRepository = financialProductRepository;  // 注入产品 Repository
        this.investmentOrderRepository = investmentOrderRepository;  // 注入投资订单 Repository
        this.paymentOrderRepository = paymentOrderRepository;  // 注入支付订单 Repository
        this.riskRecordRepository = riskRecordRepository;  // 注入风控记录 Repository
        this.supportTicketRepository = supportTicketRepository;  // 注入工单 Repository
    }

    /**
     * 获取平台总览统计
     * <p>
     * 汇总用户/商户/产品/订单数量，以及待处理风控与工单数，
     * 并计算投资总金额与已支付订单总金额。
     * </p>
     *
     * @return 平台统计数据响应体
     */
    public StatsResponse getStats() {
        // 各实体总数（count 返回 long，自动装箱为 Long）
        long userCount = userRepository.count();  // 统计用户总数
        long merchantCount = merchantRepository.count();  // 统计商户总数
        long productCount = financialProductRepository.count();  // 统计产品总数
        long investmentCount = investmentOrderRepository.count();  // 统计投资订单总数
        long paymentCount = paymentOrderRepository.count();  // 统计支付订单总数

        // 待处理风控记录数（无 countByStatus 方法，按状态查询后取 size）
        long riskPendingCount = riskRecordRepository.findByStatus(RiskStatus.PENDING).size();  // 查询 PENDING 状态风控记录并取数量
        // 待处理工单数
        long ticketOpenCount = supportTicketRepository.findByStatus(TicketStatus.OPEN).size();  // 查询 OPEN 状态工单并取数量

        // 投资总金额：所有投资订单金额求和
        // 使用 BigDecimal ZERO 作为初始值，BigDecimal::add 作为累加器，避免 double 求和精度丢失
        BigDecimal totalInvestmentAmount = investmentOrderRepository.findAll().stream()
                .map(InvestmentOrder::getAmount)  // 提取每笔投资金额
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // 归约求和

        // 支付总金额：仅统计已支付（PAID）订单
        List<PaymentOrder> paidOrders = paymentOrderRepository.findByStatus(PaymentStatus.PAID);  // 查询已支付订单
        BigDecimal totalPaymentAmount = paidOrders.stream()
                .map(PaymentOrder::getAmount)  // 提取每笔支付金额
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // 归约求和

        return new StatsResponse(userCount, merchantCount, productCount, investmentCount,
                paymentCount, riskPendingCount, ticketOpenCount,
                totalInvestmentAmount, totalPaymentAmount);  // 封装统计响应
    }
}
