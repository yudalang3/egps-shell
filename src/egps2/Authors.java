package egps2;

import org.apache.commons.lang3.tuple.Triple;

/**
 * Registry of authors, contributors, and developers of the eGPS project.
 * This class provides constants for author names, team information, and website references
 * used throughout the application for attribution, about dialogs, and documentation.
 *
 * <p>The EvolGen (Evolutionary Genomics) team at PICB (Partner Institute for Computational Biology)
 * has contributed to the development of this software.</p>
 *
 * @author eGPS Development Team
 * @version 2.1
 */
public abstract class Authors {

	public static final String YUDALANG = "Dalang Yu";

	public static final String DONGLILI = "Lili Dong";

	public static final String YANFANGQI = "Fangqi Yan";

	public static final String MUHAILONG = "Hailong Mu";

	public static final String TANGBIXIA = "Bi-Xia Tang";

	public static final String YANGXIAO = "Xiao Yang";

	public static final String ZHOUQING = "Qing Zhou";

	public static final String GAOFENG = "Feng Gao";

	public static final String WANGZHONGHUANG = "Zhonghuang Wang";

	public static final String HAOZIQIAN = "Ziqian Hao";

	public static final String KANGHONGEN = "Hongen Kang";

	public static final String ZHENGYI = "Yi Zheng";

	public static final String HUANGHONGWEI = "Hongwei Huang";

	public static final String LANLI = "Li Lan";

	public static final String WEIYUZHANG = "Yuzhang Wei";

	public static final String PANWEI = "Wei Pan";

	public static final String XUYAOCHEN = "Yaochen Xu";

	public static final String ZENGTAO = "Tao Zeng";

	public static final String ZHAOSHILEI = "Shilei Zhao";

	public static final String WANGCIRAN = "Ciran Wang";

	public static final String CHENHUA = "Hua Chen";

	public static final String LIMUSHAN = "Mushan Li";

	public static final String ZHANGYIJING = "Yijing Zhang";

	public static final String ZHOUFENG = "Feng Zhou";

	public static final String SHAOZHEN = "Zhen Shao";

	public static final String LIYIXUE = "Yi-Xue Li";

	public static final String WANGPENGYU = "Pengyu Wang";

	public static final String DAILONG = "Long Dai";

	public static final String FUYUNXIN = "Yun-Xin Fu";

	public static final String BAOYIMING = "Yiming Bao";

	public static final String CHENLUONAN = "Luo-Nan Chen";

	public static final String ZHAOFANQING = "Fangqing Zhao";

	public static final String ZHANGGUOQING = "Guo-Qing Zhang";

	public static final String ZHAOWENMING = "Wenming Zhao";

	public static final String LIHAIPENG = "Haipeng Li";

	public static final String LIUXIAOMING = "Xiaoming Liu";

	public static final String YANGJIANING = "Jianing Yang";

	public static final String ZHANGJIANWEI = "Jianwei Zhang";

	public static final String WEB_SITE = EGPSProperties.EVOLGEN_LAB_WEBSITE;

	public static final String LAB = EGPSProperties.EVOLGEN_LAB_NAME;

	private static Triple<String, String, String> yudalangAuthor;

	public static String orgnizeAuthors(String... names) {
		String ret = "";
		for (int i = 0; i < names.length - 1; i++) {
			ret += names[i];
			ret += ",";
		}

		ret += names[names.length - 1];
		return ret;
	}

	public static Triple<String, String, String> getYudalangAuthors() {

		if (yudalangAuthor == null) {
			String team = EGPSProperties.EVOLGEN_LAB_NAME;
			String developers = Authors.YUDALANG + "," + Authors.LIHAIPENG;
			String webSite = EGPSProperties.EVOLGEN_LAB_WEBSITE;

			yudalangAuthor = Triple.of(team, developers, webSite);

		}

		return yudalangAuthor;
	}

}
