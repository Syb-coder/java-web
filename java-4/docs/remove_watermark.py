# -*- coding: utf-8 -*-
"""
移除docx文件中docx库自动注入的"AI 生成"水印
使用Python zipfile模块直接操作zip包，确保删除生效
"""
import zipfile
import shutil
import os
import re
import sys

def remove_watermark(docx_path):
    """移除docx中的AI水印"""
    temp_path = docx_path + ".tmp"

    with zipfile.ZipFile(docx_path, 'r') as zin:
        with zipfile.ZipFile(temp_path, 'w', zipfile.ZIP_DEFLATED) as zout:
            for item in zin.namelist():
                data = zin.read(item)

                # 1. 跳过水印header文件（不写入新zip）
                if "header_watermark" in item:
                    print(f"  [跳过] {item}")
                    continue

                # 2. 清理 document.xml.rels 中的水印引用
                if item == "word/_rels/document.xml.rels":
                    xml = data.decode('utf-8')
                    # 移除所有指向 header_watermark.xml 的 Relationship
                    original = xml
                    xml = re.sub(
                        r'<Relationship[^>]*header_watermark\.xml[^>]*/>',
                        '',
                        xml
                    )
                    if xml != original:
                        print(f"  [清理] {item} - 移除了水印关系引用")
                    data = xml.encode('utf-8')

                # 3. 清理 document.xml 中的水印header引用
                elif item == "word/document.xml":
                    xml = data.decode('utf-8')

                    # 获取有效rId列表需要先读rels（已在上面处理，但这里是独立步骤）
                    # 直接移除所有引用header_watermark对应rId的headerReference
                    # 策略：找到所有headerReference，检查其rId是否在rels中存在
                    # 更简单：直接移除所有指向不存在rels的headerReference

                    # 先收集rels中的有效rId
                    rels_data = zin.read("word/_rels/document.xml.rels").decode('utf-8')
                    # 移除rels中水印引用后再收集
                    cleaned_rels = re.sub(
                        r'<Relationship[^>]*header_watermark\.xml[^>]*/>',
                        '',
                        rels_data
                    )
                    valid_rids = set(re.findall(r'Id="(rId\d+)"', cleaned_rels))

                    # 移除document.xml中引用了无效rId的headerReference
                    def replace_header_ref(match):
                        rid = match.group(2)
                        if rid not in valid_rids:
                            print(f"  [清理] {item} - 移除水印 headerReference ({rid})")
                            return ''
                        return match.group(0)

                    xml = re.sub(
                        r'<w:headerReference\s+w:type="(\w+)"\s+r:id="(rId\d+)"\s*/>',
                        replace_header_ref,
                        xml
                    )

                    # 确保有正常header引用（rId7指向header1.xml）
                    if 'r:id="rId7"' not in xml and 'rId7' in valid_rids:
                        xml = xml.replace(
                            '<w:sectPr>',
                            '<w:sectPr><w:headerReference w:type="default" r:id="rId7"/>'
                        )
                        print(f"  [修复] {item} - 恢复正常页眉引用 (rId7)")

                    data = xml.encode('utf-8')

                # 4. 清理 [Content_Types].xml
                elif item == "[Content_Types].xml":
                    xml = data.decode('utf-8')
                    original = xml
                    xml = re.sub(
                        r'<Override[^>]*header_watermark\.xml[^>]*/>',
                        '',
                        xml
                    )
                    if xml != original:
                        print(f"  [清理] {item} - 移除水印Content Type")
                    data = xml.encode('utf-8')

                # 5. 清理 settings.xml 中可能的水印相关设置
                elif item == "word/settings.xml":
                    xml = data.decode('utf-8')
                    # 检查是否有水印相关属性
                    if 'watermark' in xml.lower():
                        print(f"  [警告] {item} - 发现watermark相关设置")

                zout.writestr(item, data)

    # 替换原文件
    shutil.move(temp_path, docx_path)
    print(f"  水印移除完成: {docx_path}")

    # 验证
    with zipfile.ZipFile(docx_path, 'r') as zf:
        names = zf.namelist()
        if any("header_watermark" in n for n in names):
            print("  [错误] header_watermark 仍存在!")
        else:
            print("  [验证] header_watermark 已彻底删除")

        # 检查document.xml.rels
        rels = zf.read("word/_rels/document.xml.rels").decode('utf-8')
        if 'header_watermark' in rels:
            print("  [错误] document.xml.rels 中仍有水印引用!")
        else:
            print("  [验证] document.xml.rels 清理完毕")

        # 检查document.xml
        doc = zf.read("word/document.xml").decode('utf-8')
        if 'header_watermark' in doc:
            print("  [错误] document.xml 中仍有水印引用!")
        else:
            print("  [验证] document.xml 清理完毕")

        # 检查Content_Types
        ct = zf.read("[Content_Types].xml").decode('utf-8')
        if 'header_watermark' in ct:
            print("  [错误] Content_Types.xml 中仍有水印引用!")
        else:
            print("  [验证] Content_Types.xml 清理完毕")

        # 搜索所有文件中是否还有"AI 生成"
        for name in names:
            try:
                content = zf.read(name).decode('utf-8', errors='ignore')
                if 'AI 生成' in content or 'AI\u751f\u6210' in content:
                    print(f"  [错误] {name} 中仍包含 'AI 生成' 文本!")
            except:
                pass
        print("  [验证] 全文搜索 'AI 生成' 完成")


if __name__ == "__main__":
    docx_path = sys.argv[1] if len(sys.argv) > 1 else r"c:\000\code\java-web\java-4\校园图书借阅管理系统设计与实现.docx"
    print(f"正在处理: {docx_path}")
    remove_watermark(docx_path)
