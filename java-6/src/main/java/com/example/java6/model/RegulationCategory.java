package com.example.java6.model;

/**
 * 政策法规分类枚举
 *
 * <p>按照我国法律法规层级体系对政策法规进行分类，
 * 便于公众按效力等级查阅相关法律条文。</p>
 */
public enum RegulationCategory {

    /** 法律：全国人大及其常委会制定的法律 */
    LAW,

    /** 行政法规：国务院制定的行政法规 */
    ADMINISTRATIVE_REGULATION,

    /** 部门规章：国务院各部委制定的规章 */
    DEPARTMENTAL_RULE,

    /** 规范性文件：行政机关发布的规范性文件 */
    NORMATIVE_DOCUMENT
}
