package demo.dockable.sigtest;

/**
 * 测试运行类
 * 演示如何使用差异表达分析器
 */
public class TestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== 差异表达分析测试 ===");
        System.out.println("演示各种统计方法的使用\n");
        
        // 创建分析器
        DifferentialExpressionAnalyzer analyzer = new DifferentialExpressionAnalyzer();
        
        // 测试参数
        String fileName = "sample_data.tsv";
        String[] group1 = {"Ctrl1", "Ctrl2", "Ctrl3"};
        String[] group2 = {"Treat1", "Treat2", "Treat3"};
        
        System.out.println("输入文件: " + fileName);
        System.out.println("对照组: " + String.join(", ", group1));
        System.out.println("处理组: " + String.join(", ", group2));
        System.out.println();
        
        // 显示可用方法
        System.out.println("可用的统计方法:");
        for (String method : analyzer.getAvailableMethods()) {
            System.out.println("- " + method);
        }
        System.out.println();

        // 运行所有方法
//        System.out.println("开始分析...");
//        for (String method : analyzer.getAvailableMethods()) {
//            String outputFile = "output_" + method.replace("-", "_").replace(" ", "_") + ".txt";
//            System.out.println("运行 " + method + " 检验...");
//            analyzer.performAnalysis(fileName, group1, group2, outputFile, method);
//        }
        
        System.out.println("\n=== 单独测试各个方法 ===");
        
        // 测试数据
        double[] testGroup1 = {10.5, 11.2, 9.8, 10.1, 11.0};
        double[] testGroup2 = {15.2, 14.8, 15.5, 14.9, 15.1};
        
        System.out.println("测试数据:");
        System.out.print("组1: ");
        for (double v : testGroup1) System.out.print(v + " ");
        System.out.println();
        System.out.print("组2: ");
        for (double v : testGroup2) System.out.print(v + " ");
        System.out.println("\n");
        
        // 测试Mann-Whitney U检验
        testMannWhitneyU(testGroup1, testGroup2);
        
        // 测试t检验
        testTTest(testGroup1, testGroup2);
        
        // 测试Wilcoxon秩和检验
        testWilcoxonRankSum(testGroup1, testGroup2);
        
        // 测试Kolmogorov-Smirnov检验
        testKolmogorovSmirnov(testGroup1, testGroup2);
        
        System.out.println("\n=== 测试完成 ===");
    }
    
    private static void testMannWhitneyU(double[] group1, double[] group2) {
        System.out.println("Mann-Whitney U检验:");
        MannWhitneyUTest test = new MannWhitneyUTest();
        double pValue = test.calculatePValue(group1, group2);
        System.out.println("p值: " + pValue);
        System.out.println("结果: " + (pValue < 0.05 ? "显著差异" : "无显著差异"));
        System.out.println();
    }
    
    private static void testTTest(double[] group1, double[] group2) {
        System.out.println("t检验:");
        TTest test = new TTest();
        double pValue = test.calculatePValue(group1, group2);
        System.out.println("p值: " + pValue);
        System.out.println("结果: " + (pValue < 0.05 ? "显著差异" : "无显著差异"));
        System.out.println();
    }
    
    private static void testWilcoxonRankSum(double[] group1, double[] group2) {
        System.out.println("Wilcoxon秩和检验:");
        WilcoxonRankSumTest test = new WilcoxonRankSumTest();
        double pValue = test.calculatePValue(group1, group2);
        System.out.println("p值: " + pValue);
        System.out.println("结果: " + (pValue < 0.05 ? "显著差异" : "无显著差异"));
        System.out.println();
    }
    
    private static void testKolmogorovSmirnov(double[] group1, double[] group2) {
        System.out.println("Kolmogorov-Smirnov检验:");
        KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        double pValue = test.calculatePValue(group1, group2);
        System.out.println("p值: " + pValue);
        System.out.println("结果: " + (pValue < 0.05 ? "显著差异" : "无显著差异"));
        System.out.println();
    }
}