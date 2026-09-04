package egps.lnf.utils;

import java.util.HashMap;

/**
 * 本地磁盘资源文件缓存中心超类，子类可继承本类以实现磁盘资源的集中缓存.
 *
 * @param <T> the generic type
 * @version 1.0
 */
public abstract class RawCache<T> {

	/** 本地磁盘资源缓存中心（key=path,value=image对象）. */
	private HashMap<String, T> rawCache = new HashMap<String, T>();

	/**
	 * 本地磁盘资源（如果缓存中已存在，则从中取之，否则从磁盘读取并缓存之）。.
	 * 
	 * yudalang注释：错 它relativePath这个参数写错了。应该是 imgs/pic/some.png才对 这里的/要去掉
	 *
	 * @param relativePath 本地磁盘资源相对于baseClass类的相对路径，比如它如果在/res/imgs/pic/下，baseClass在
	 *                     /res下，则本地磁盘资源此处传过来的相对路径应该是/imgs/pic/some.png
	 * @param baseClass    基准类，指定此类则获取本地磁盘资源时会以此类为基准取本地磁盘资源的相对物理目录
	 * @return T
	 */
	public T getRaw(String relativePath, Class<?> baseClass) {

		String key = relativePath + baseClass.getCanonicalName();
		T ic = rawCache.get(key);

		if (ic == null) {
			try {
				ic = getResource(relativePath, baseClass);
				rawCache.put(key, ic);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ic;
	}

	/**
	 * 本地资源获取方法实现.
	 *
	 * @param relativePath 相对路径
	 * @param baseClass    基准类
	 * @return the resource
	 */
	protected abstract T getResource(String relativePath, Class<?> baseClass);
}
