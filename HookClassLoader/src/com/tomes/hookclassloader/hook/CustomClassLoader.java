package com.tomes.hookclassloader.hook;

import dalvik.system.DexClassLoader;

/**这里可以直接使用DexClassLoader，这里重新创建一个类是为了更有区分度；以后也可以通过修改这个类实现对于类加载的控制：
 * @author Administrator
 *
 */
public class CustomClassLoader extends DexClassLoader {

	public CustomClassLoader(String dexPath, String optimizedDirectory,
			String libraryPath, ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
	}

}
