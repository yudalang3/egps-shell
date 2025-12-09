# 差异表达分析Java工具

这是一个用于计算基因差异表达显著性的Java工具包，支持多种统计检验方法。

## 项目结构

```
testJava/
├── StatisticalTest.java              # 统计检验接口
├── MannWhitneyUTest.java             # Mann-Whitney U检验实现
├── TTest.java                        # t检验实现
├── WilcoxonRankSumTest.java          # Wilcoxon秩和检验实现
├── KolmogorovSmirnovTest.java        # Kolmogorov-Smirnov检验实现
├── DifferentialExpressionAnalyzer.java # 主分析类
├── TestRunner.java                   # 测试运行类
├── sample_data.tsv                   # 示例数据文件
└── README.md                         # 说明文档
```

## 支持的统计方法

### 基础统计方法
1. **Mann-Whitney U检验** - 非参数检验，适用于不满足正态分布的数据
2. **t检验** - 参数检验，适用于正态分布数据
3. **Wilcoxon秩和检验** - 非参数检验，与Mann-Whitney U等价
4. **Kolmogorov-Smirnov检验** - 比较两个分布是否相同

### 高级生物信息学方法
5. **Limma风格分析** - 线性模型+经验贝叶斯，适合微阵列和归一化表达量
6. **DESeq2风格分析** - 负二项分布建模，适合原始count矩阵
7. **edgeR风格分析** - 负二项分布+TMM归一化，适合原始count矩阵

## 高级方法使用说明

### 1. 编译项目
```bash
javac *.java
```

### 2. 运行高级分析测试
```bash
java AdvancedTestRunner
```

### 3. 直接调用高级分析器
```java
AdvancedDifferentialExpressionAnalyzer analyzer = new AdvancedDifferentialExpressionAnalyzer();
String fileName = "sample_data.tsv";
String[] group1Labels = {"Ctrl1", "Ctrl2", "Ctrl3"};
String[] group2Labels = {"Treat1", "Treat2", "Treat3"};
String outputFileName = "advanced_output.txt";
String method = "Limma"; // 可选：Limma, DESeq2, edgeR
analyzer.performAdvancedAnalysis(fileName, group1Labels, group2Labels, outputFileName, method);
```

#### 支持的高级方法名称
- Limma
- DESeq2
- edgeR

#### 输入数据要求
- **Limma**：支持归一化表达量或原始count，内部可自动log转换和归一化
- **DESeq2/edgeR**：建议输入原始count矩阵（整数型），内部自动归一化
- **基础方法**：建议输入归一化后的表达量（如TPM、FPKM、log2(count+1)等）

## 使用方法

### 1. 编译项目

```bash
javac *.java
```

### 2. 运行测试

```bash
java TestRunner
```

### 3. 使用主分析类

```java
DifferentialExpressionAnalyzer analyzer = new DifferentialExpressionAnalyzer();

String fileName = "sample_data.tsv";
String[] group1Labels = {"Ctrl1", "Ctrl2", "Ctrl3"};
String[] group2Labels = {"Treat1", "Treat2", "Treat3"};
String newColumnName = "p.value";
String outputFileName = "output.txt";
String method = "Mann-Whitney U";

analyzer.performAnalysis(fileName, group1Labels, group2Labels, 
                        newColumnName, outputFileName, method);
```

## 输入文件格式

输入文件应为TSV格式，包含：
- 第一列：基因名称
- 后续列：各样本的表达值
- 第一行：列标题

示例：
```
GeneName	Ctrl1	Ctrl2	Ctrl3	Treat1	Treat2	Treat3
Gene1	10.5	11.2	9.8	15.3	16.1	14.7
Gene2	5.2	4.9	5.5	5.1	4.8	5.3
```

## 输出文件格式

输出文件将包含原始数据的所有列，并在最后添加一列p值：
```
GeneName	Ctrl1	Ctrl2	Ctrl3	Treat1	Treat2	Treat3	p.value
Gene1	10.5	11.2	9.8	15.3	16.1	14.7	1.234567e-02
Gene2	5.2	4.9	5.5	5.1	4.8	5.3	8.765432e-01
```

## 统计方法说明

### Mann-Whitney U检验
- **适用场景**：两组独立样本，不要求正态分布
- **原理**：基于秩的非参数检验
- **优点**：对分布假设要求低，稳健性好

### t检验
- **适用场景**：两组独立样本，假设正态分布
- **原理**：比较两组均值差异
- **优点**：功效高，理论基础完善

### Wilcoxon秩和检验
- **适用场景**：与Mann-Whitney U等价
- **原理**：基于秩和的非参数检验
- **优点**：计算简单，结果稳定

### Kolmogorov-Smirnov检验
- **适用场景**：比较两个分布的整体差异
- **原理**：基于累积分布函数的最大差异
- **优点**：能检测分布形状的差异

## 注意事项

1. **数据质量**：确保输入数据中的数值格式正确
2. **样本大小**：样本量太小可能影响检验效力
3. **多重比较**：进行多个基因的检验时，考虑多重比较校正
4. **方法选择**：根据数据分布特征选择合适的统计方法

## 扩展功能

如需添加新的统计方法，只需：
1. 实现`StatisticalTest`接口
2. 在`DifferentialExpressionAnalyzer`的`initializeTests()`方法中注册新方法

## 示例代码

完整的使用示例请参考`TestRunner.java`文件。