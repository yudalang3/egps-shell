# 基因富集分析工具 (Gene Enrichment Analysis Tool)

这是一个用Java实现的基因富集分析工具，支持ORA (Over-Representation Analysis) 和GSEA (Gene Set Enrichment Analysis) 两种经典的富集分析方法。

## 功能特性

- **ORA分析**: 使用Fisher精确检验进行过表达分析
- **GSEA分析**: 基于基因排序的富集分析
- **支持标准GMT格式**: 兼容MSigDB等数据库的基因集文件
- **多种统计校正**: 支持FDR (Benjamini-Hochberg) 多重检验校正
- **灵活的输入格式**: 支持TSV格式的基因表达数据

## 依赖要求

- Java 8 或更高版本
- 无需任何第三方库依赖

## 编译和运行

1. 确保已安装Java
2. 编译Java文件:

```bash
javac *.java
```

3. 运行程序:

```bash
java EnrichmentAnalysis
```

或运行测试程序:

```bash
java TestEnrichment
```

### 自定义参数

在`EnrichmentAnalysis.java`的main方法中修改以下参数:

```java
String outputFileName = "output.tsv";                    // 输出文件名
String inputGMTFileName = "h.all.v2025.1.Hs.entrez.gmt"; // GMT基因集文件
String inputSignificantResultFileName = "output_Mann_Whitney_U.txt"; // 输入数据文件
String geneSymbolColName = "GeneName";                   // 基因名列名
String significantColName = "p.value";                   // p值列名
```

## 输入文件格式

### GMT文件格式
GMT (Gene Matrix Transposed) 格式，每行包含:
- 第1列: 基因集名称
- 第2列: 基因集描述
- 第3列及以后: 基因列表

示例:
```
HALLMARK_ADIPOGENESIS	https://www.gsea-msigdb.org/gsea/msigdb/human/geneset/HALLMARK_ADIPOGENESIS	19	11194	10449	33
```

### 基因表达数据文件
TSV格式，包含表头行，必须包含:
- 基因名称列 (由`geneSymbolColName`指定)
- p值列 (由`significantColName`指定)

示例:
```
GeneName	Ctrl1	Ctrl2	Ctrl3	Treat1	Treat2	Treat3	p.value
Gene1	10.5	11.2	9.8	15.3	16.1	14.7	4.953448e-02
```

## 输出文件

**重要**: 输出文件包含GMT文件中的所有基因集，每个基因集一行结果，无论是否显著富集。

### ORA结果 (*_ORA.tsv)
包含以下列:
- `Pathway`: 通路名称
- `Description`: 通路描述
- `Genes_in_pathway`: 通路中的基因数
- `Significant_genes_in_pathway`: 通路中显著基因数
- `Total_significant_genes`: 总显著基因数
- `Total_genes`: 总基因数
- `P_value`: Fisher精确检验p值（无富集时为1.0）
- `FDR`: 错误发现率
- `Genes_in_set`: 通路中的显著基因列表

### GSEA结果 (*_GSEA.tsv)
包含以下列:
- `Pathway`: 通路名称
- `Description`: 通路描述
- `Enrichment_Score`: 富集分数
- `Normalized_ES`: 标准化富集分数
- `P_value`: 置换检验p值
- `FDR`: 错误发现率
- `Size`: 基因集大小
- `Leading_Edge`: 前导边缘基因

## 算法说明

### ORA (Over-Representation Analysis)
- 使用超几何分布进行Fisher精确检验
- 计算基因集在显著基因中的过表达程度
- 适用于已有显著基因列表的情况

### GSEA (Gene Set Enrichment Analysis)
- 基于基因的排序列表计算富集分数
- 使用置换检验计算统计显著性
- 不需要预先定义显著性阈值
- 考虑所有基因的贡献

## 参数调整

- **p值阈值**: 在`performEnrichmentAnalysis`方法中修改最后一个参数
- **GSEA置换次数**: 在`performGSEA`方法调用中修改`permutations`参数
- **基因集大小限制**: 在GSEA分析中默认使用15-500个基因的基因集

## 注意事项

1. 确保GMT文件中的基因标识符与输入数据中的基因名称一致
2. 对于大型数据集，GSEA分析可能需要较长时间
3. 建议在分析前检查基因名称的匹配情况
4. 输出文件将覆盖同名的现有文件

## 故障排除

- **找不到指定的列名**: 检查输入文件的表头是否正确
- **内存不足**: 增加JVM堆内存大小 `-Xmx4g`
- **编译错误**: 确保Java版本兼容性

## 许可证

本项目采用MIT许可证。