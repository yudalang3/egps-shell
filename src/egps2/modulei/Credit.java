package egps2.modulei;
/**
 * 模块开发团队信息接口，定义了获取作者和贡献者信息的契约。
 * Module development team information interface that defines the contract for obtaining author and contributor information.
 *
 * <p>此接口是顶层模块接口之一，用于在应用程序的"关于"对话框或模块信息面板中
 * 显示开发团队、开发者列表和网站链接等信息。
 * This is one of the top-level module interfaces, used to display development team,
 * developer list, and website links in the application's "About" dialog or module information panel.
 *
 * <p><strong>返回信息包括：</strong>
 * Returned information includes:
 * <ul>
 *   <li>团队名称（Team name）- 例如 "EvolGene"</li>
 *   <li>开发者列表（Developers list）- 例如 "A, B, C, D, E"</li>
 *   <li>网站链接（Website link）- 例如 "www.example.com"</li>
 * </ul>
 *
 * <p><strong>实现示例：</strong>
 * Implementation example:
 * <pre>{@code
 * @Override
 * public CreditBean getDevTeam() {
 *     String team = "EvolGene";
 *     String developers = "A, B,<br>C, D, E";  // 支持HTML标签
 *     String webSite = "www.a.c.com";
 *
 *     CreditBean creditBean = new CreditBean();
 *     creditBean.setTeam(team);
 *     creditBean.setDevelopers(developers);
 *     creditBean.setWebSite(webSite);
 *     return creditBean;
 * }
 * }</pre>
 *
 * <p><strong>HTML标签支持：</strong>
 * HTML tag support:
 * <br>开发者字符串支持简单的HTML标签（如 {@code <br>} 换行），以便格式化显示。
 * The developers string supports simple HTML tags (such as {@code <br>} for line breaks) for formatted display.
 *
 * <p>线程模型：在EDT线程中调用，应快速返回而不阻塞。
 * Thread model: Called in EDT thread, should return quickly without blocking.
 *
 * @see CreditBean
 * @see egps2.Authors
 * @author eGPS Dev Team
 * @since 2.1
 */
public interface Credit{

	/**
	 * 
	 * The Credit.
	 * 
	 * String[0]: the team name of developers 
	 * String[1]: the list of developers
	 * String[2]: the web link
	 * 
	 * Implement example:
	 * 
	 * <pre>
	 * String team = "EvolGene";
	 * String developers = "A , B ,< br > C , D , E";
	 * String webSite = "www.a.c.com";
	 * 
	 * return Triple.of(team,developers,webSite);
	 * 
	 * </pre>
	 * 
	 * The developers string support the html tags, 
	 * 
	 * for example, please use br to initialize new line.
	 * 
	 * @return Three elements string arrays!
	 */
	CreditBean getDevTeam();
	
	
}
