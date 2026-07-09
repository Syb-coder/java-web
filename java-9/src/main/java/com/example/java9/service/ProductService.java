package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.model.FinancialProduct;  // 导入理财产品实体
import com.example.java9.model.ProductStatus;  // 导入产品状态枚举（ON_SALE/OFF_SHELF/SOLD_OUT）
import com.example.java9.model.ProductType;  // 导入产品类型枚举
import com.example.java9.repository.FinancialProductRepository;  // 导入产品 Repository
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解
import org.springframework.transaction.annotation.Transactional;  // 导入事务注解

import java.math.BigDecimal;  // 导入高精度十进制类，金额计算必须用 BigDecimal 避免 double 精度丢失
import java.util.List;  // 导入集合 List

/**
 * 理财产品服务
 * <p>
 * 负责理财产品的 CRUD 及额度管理。
 * 投资时通过 {@link #addInvestedAmount} 累加已投金额，满额自动置为售罄。
 * </p>
 */
@Service  // 标记为 Spring Service Bean
public class ProductService {

    private final FinancialProductRepository financialProductRepository;  // 产品 Repository，final 保证不可变

    // 构造器注入：保证依赖不可变、显式暴露依赖、便于单元测试，Spring 启动时即可发现循环依赖
    public ProductService(FinancialProductRepository financialProductRepository) {
        this.financialProductRepository = financialProductRepository;  // 注入产品 Repository
    }

    /**
     * 创建产品
     * <p>
     * 新建产品默认状态为 ON_SALE、已投金额为 0。
     * </p>
     *
     * @param name        产品名称
     * @param type        产品类型
     * @param annualRate  年化收益率
     * @param minAmount   起投金额
     * @param duration    投资期限（天）
     * @param riskLevel   风险等级（1-5）
     * @param totalAmount 募集总额
     * @param description 产品描述
     * @return 已保存的产品实体
     */
    @Transactional  // 声明事务边界：产品创建原子化
    public FinancialProduct create(String name, ProductType type, BigDecimal annualRate, BigDecimal minAmount,
                                   Integer duration, Integer riskLevel, BigDecimal totalAmount, String description) {
        FinancialProduct product = new FinancialProduct(name, type, annualRate, minAmount,
                duration, riskLevel, totalAmount, description);  // 构造产品实体，构造器内默认状态 ON_SALE、已投金额 0
        return financialProductRepository.save(product);  // 持久化并返回带 ID 的实体
    }

    /**
     * 更新产品
     * <p>
     * 覆盖更新可编辑字段，状态与已投金额不变（避免运营篡改募集进度）。
     * </p>
     *
     * @param id          产品 ID
     * @param name        产品名称
     * @param type        产品类型
     * @param annualRate  年化收益率
     * @param minAmount   起投金额
     * @param duration    投资期限（天）
     * @param riskLevel   风险等级（1-5）
     * @param totalAmount 募集总额
     * @param description 产品描述
     * @return 更新后的产品实体
     * @throws IllegalArgumentException 产品不存在
     */
    @Transactional  // 声明事务边界：产品字段更新原子化
    public FinancialProduct update(Long id, String name, ProductType type, BigDecimal annualRate, BigDecimal minAmount,
                                   Integer duration, Integer riskLevel, BigDecimal totalAmount, String description) {
        FinancialProduct product = financialProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("理财产品不存在"));  // 查询产品，不存在则抛异常
        product.setName(name);  // 更新产品名称
        product.setType(type);  // 更新产品类型
        product.setAnnualRate(annualRate);  // 更新年化收益率
        product.setMinAmount(minAmount);  // 更新起投金额
        product.setDuration(duration);  // 更新投资期限
        product.setRiskLevel(riskLevel);  // 更新风险等级
        product.setTotalAmount(totalAmount);  // 更新募集总额
        product.setDescription(description);  // 更新产品描述
        // 注意：不更新 status 与 investedAmount，避免运营篡改募集进度
        return financialProductRepository.save(product);  // 持久化并返回
    }

    /**
     * 下架产品
     *
     * @param id 产品 ID
     * @return 更新后的产品实体（状态为 OFF_SHELF）
     * @throws IllegalArgumentException 产品不存在
     */
    @Transactional  // 声明事务边界：产品状态更新原子化
    public FinancialProduct offShelf(Long id) {
        FinancialProduct product = financialProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("理财产品不存在"));  // 查询产品
        product.setStatus(ProductStatus.OFF_SHELF);  // 置为下架状态，下架后用户无法新申购
        return financialProductRepository.save(product);  // 持久化并返回
    }

    /**
     * 查询在售产品
     *
     * @return 状态为 ON_SALE 的产品列表
     */
    public List<FinancialProduct> findOnSale() {
        return financialProductRepository.findByStatus(ProductStatus.ON_SALE);  // 查询在售产品供 C 端展示
    }

    /**
     * 查询所有产品
     *
     * @return 全部产品列表
     */
    public List<FinancialProduct> findAll() {
        return financialProductRepository.findAll();  // 查询全部产品
    }

    /**
     * 根据 ID 查询产品
     *
     * @param id 产品 ID
     * @return 产品实体
     * @throws IllegalArgumentException 产品不存在
     */
    public FinancialProduct findById(Long id) {
        return financialProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("理财产品不存在"));  // 查询产品，不存在则抛异常
    }

    /**
     * 增加已投金额（投资时调用）
     * <p>
     * 累加已投金额，若达到募集总额则自动置为售罄（SOLD_OUT）。
     * </p>
     *
     * @param productId 产品 ID
     * @param amount    本次投资金额
     * @return 更新后的产品实体
     * @throws IllegalArgumentException 产品不存在
     */
    @Transactional  // 声明事务边界：已投金额累加与售罄状态判断原子化，避免超卖
    public FinancialProduct addInvestedAmount(Long productId, BigDecimal amount) {
        FinancialProduct product = financialProductRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("理财产品不存在"));  // 查询产品
        product.setInvestedAmount(product.getInvestedAmount().add(amount));  // 已投金额累加，使用 BigDecimal.add 避免浮点精度丢失
        // 已投金额 >= 募集总额，标记售罄
        // 使用 compareTo 比较金额大小，避免 BigDecimal equals 因 scale 不同导致判断错误
        if (product.getInvestedAmount().compareTo(product.getTotalAmount()) >= 0) {
            product.setStatus(ProductStatus.SOLD_OUT);  // 满额自动售罄，防止超额募集
        }
        return financialProductRepository.save(product);  // 持久化并返回
    }
}
