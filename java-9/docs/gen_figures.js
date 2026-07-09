/**
 * SVG 转 PNG 转换脚本
 * 使用 @resvg/resvg-js 将 SVG 文件转换为 PNG 格式
 */
const fs = require("fs");
const path = require("path");
const { Resvg } = require("@resvg/resvg-js");

const FIGURES_DIR = path.join(__dirname, "figures");

// 获取所有 SVG 文件
const svgFiles = fs.readdirSync(FIGURES_DIR).filter(f => f.endsWith(".svg"));

for (const svgFile of svgFiles) {
  const svgPath = path.join(FIGURES_DIR, svgFile);
  const pngPath = path.join(FIGURES_DIR, svgFile.replace(".svg", ".png"));

  const svgBuffer = fs.readFileSync(svgPath);
  const resvg = new Resvg(svgBuffer, {
    fitTo: { mode: "width", value: 1200 },
  });
  const pngBuffer = resvg.render().asPng();
  fs.writeFileSync(pngPath, pngBuffer);
  console.log(`[OK] ${svgFile} -> ${path.basename(pngPath)}`);
}

console.log("\nAll SVG files converted to PNG!");
